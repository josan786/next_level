package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;

import org.openalpr.OpenALPR;
import org.openalpr.model.Result;
import org.openalpr.model.Results;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ApiService;
import ru.konstantin_starikov.samsung.izhhelper.models.CarNumber;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.views.CameraView;

public class CarNumberDetectionActivity extends AppCompatActivity {

    public final static String VIOLATION_REPORT = "violation_report";

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_CAMERA = 2;

    private ViolationReport violationReport;

    private CameraView cameraView;
    private Timer carNumberRecognizeTimer;
    private CountDownTimer cancelTimer;
    private Button foreground;
    private Button cancelButton;
    private TextView timerSecondsCountText;
    private TextView carNumberText;
    private FrameLayout informationLayout;

    //Распознавание номера через OpenALPR
    int recognitionPeriod = 4000;
    private String ANDROID_DATA_DIR;
    private String openAlprConfFile;
    private OpenALPR alpr;

    boolean isRecognized;

    ExecutorService recognizingTasksService;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        violationReport = getTransmittedViolationReport();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_number_detection);

        isRecognized = false;

        Camera.PreviewCallback previewCallback = new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if(isRecognized) return;
                Camera.Parameters parameters = camera.getParameters();
                Camera.Size size = parameters.getPreviewSize();
                YuvImage image = new YuvImage(data, parameters.getPreviewFormat(),
                        size.width, size.height, null);
                File file = new File( String.format(getApplicationInfo().dataDir + File.separator + "%d.jpg", System.currentTimeMillis()));
                FileOutputStream filecon = null;
                try {
                    filecon = new FileOutputStream(file);
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
                image.compressToJpeg(
                        new Rect(0, 0, image.getWidth(), image.getHeight()), 90,
                        filecon);
                processPicture(file.getPath());
            }
        };

        findAndSetViews();

        tuneCamera();

        tuneActionBar();

        //Проверяем, есть ли доступ к камере и памяти устройства
        //Если разрешения отсутсвуют, спрашиваем их у пользователя
        if (checkIfAlreadyHaveStoragePermission()) {
            if (checkIfAlreadyHaveCameraPermission()) {
                organizeRecognizingThreadsPool();
            } else requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
        } else requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        ANDROID_DATA_DIR = this.getApplicationInfo().dataDir;
        setupALPR();

        cameraView.previewCallback  = previewCallback;
    }

    private void organizeRecognizingThreadsPool()
    {

    }

    private ViolationReport getTransmittedViolationReport() {
        return (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);
    }

    private void findAndSetViews() {
        cameraView = findViewById(R.id.cameraView);
        foreground = findViewById(R.id.foreground);
        cancelButton = findViewById(R.id.cancelButton);
        timerSecondsCountText = findViewById(R.id.timerSecondsCountText);
        informationLayout = findViewById(R.id.informationLayout);
        carNumberText = findViewById(R.id.carNumberText);
    }

    private void tuneCamera() {
        cameraView.setFocusable(true);
    }

    private void tuneActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Определение номера");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setupALPR() {
        openAlprConfFile = ANDROID_DATA_DIR + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";
        alpr = OpenALPR.Factory.create(CarNumberDetectionActivity.this, ANDROID_DATA_DIR);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean checkIfAlreadyHaveStoragePermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkIfAlreadyHaveCameraPermission() {
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return result == PackageManager.PERMISSION_GRANTED;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_READ_EXTERNAL_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Разрешение на чтение файлов получено", Toast.LENGTH_LONG).show();
                    if (checkIfAlreadyHaveCameraPermission()) {
                        organizeRecognizingThreadsPool();
                    } else requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
                } else {
                    Toast.makeText(this, "Нет разрешения на чтение файлов", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case PERMISSION_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Доступ к камере получен", Toast.LENGTH_LONG).show();
                    cameraView.surfaceCreated(cameraView.getHolder());
                    if (checkIfAlreadyHaveStoragePermission()) {
                        organizeRecognizingThreadsPool();
                    }
                } else {
                    Toast.makeText(this, "Нет доступа к камере", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    private void goToNextActivity() {
        Intent openPhotofixationIntent = new Intent(CarNumberDetectionActivity.this, PhotofixationActivity.class);
        openPhotofixationIntent.putExtra(VIOLATION_REPORT, violationReport);
        startActivity(openPhotofixationIntent);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void cancelResults(View v) {
        carNumberText.setVisibility(View.INVISIBLE);
        informationLayout.setVisibility(View.VISIBLE);
        foreground.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.INVISIBLE);
        timerSecondsCountText.setVisibility(View.INVISIBLE);
        cameraView.camera.startPreview();
        isRecognized = false;
        //Todo: удаление фото при омтене распознавания
        cancelTimer.cancel();
    }

    @SuppressLint("Range")
    public void processPicture(String picturePath) {
        //вращаем исходное изображение, если его ширина больше высоты
        Bitmap rotatedPicture = Helper.rotateImageIfRequired(Helper.getBitmapFromPath(picturePath));
        //удаляем исходное изображение
        Helper.deleteImageByPath(picturePath);
        //дальше получаем путь корректного изображения
        String rotatedPicturePath = "";
        FileOutputStream fileOutputStream = null;
        try {
            rotatedPicturePath = String.format(getApplicationInfo().dataDir + File.separator + "%d.jpg", System.currentTimeMillis());
            fileOutputStream = new FileOutputStream(rotatedPicturePath);
            rotatedPicture.compress(Bitmap.CompressFormat.PNG, 80, fileOutputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        //передаём изображение на распознавание
        recognizeNumber(rotatedPicturePath);
    }

    private void setRecognizedPanelVisible(String plate) {
        carNumberText.setVisibility(View.VISIBLE);
        carNumberText.setText(plate);
        informationLayout.setVisibility(View.INVISIBLE);
        foreground.setVisibility(View.VISIBLE);
        foreground.setAlpha(0.2f);
        cancelButton.setVisibility(View.VISIBLE);
        timerSecondsCountText.setVisibility(View.VISIBLE);
    }

    private void createAndStartCancelTimer(String picturePath) {
        cancelTimer = new CountDownTimer(10 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timerSecondsCountText.setText("0:" + Long.toString(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                cancelTimer.cancel();
                violationReport.carNumberPhotoName = picturePath.substring(picturePath.lastIndexOf('/') + 1);
                Log.i("CarNumberPhotoName", violationReport.carNumberPhotoName);
                goToNextActivity();
            }
        };
        cancelTimer.start();
    }

    private void recognizeNumber(String picturePath) {
        AsyncTask.execute(() ->
        {
            if (Helper.isOnline(CarNumberDetectionActivity.this) && isPlateRecognizerWork())
                recognizeNumberByPlateRecognizer(picturePath);
            else {
                recognizeNumberByOpenALPR(picturePath);
            }
        });
    }

    private boolean isPlateRecognizerWork()
    {
        boolean isPlateRecognizerWork = false;
        try {
            HttpResponse<String> response = Unirest.get("https://api.platerecognizer.com/v1/statistics/")
                    .header("Authorization", "Token " + Helper.getConfigValue(CarNumberDetectionActivity.this, "PLATE_RECOGNIZER_TOKEN"))
                    .asString();
            Log.i("Usage", response.getBody());
            JsonParser parser = new JsonParser();
            JsonElement jsonTree = parser.parse(response.getBody());
            if (jsonTree.isJsonObject()) {
                JsonObject jsonObject = null;
                int totalCalls = 2500;
                int callsUsage = 2500;
                try {
                    jsonObject = jsonTree.getAsJsonObject();
                    totalCalls = jsonObject.getAsJsonPrimitive("total_calls").getAsInt();
                    callsUsage = jsonObject.getAsJsonObject("usage").getAsJsonPrimitive("calls").getAsInt();
                } catch (Exception e) {
                    Log.e("JSON exception", e.getMessage());
                    isPlateRecognizerWork = false;
                }
                if (totalCalls > callsUsage) isPlateRecognizerWork = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isPlateRecognizerWork;
    }

    private void recognizeNumberByPlateRecognizer(String picturePath)
    {
        final File file = new File(picturePath);
        OkHttpClient client = new OkHttpClient.Builder().build();
        ApiService apiService = new Retrofit.Builder().baseUrl("https://api.platerecognizer.com").client(client).build().create(ApiService.class);
        RequestBody reqFile = RequestBody.create(okhttp3.MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("upload",
                file.getName(), reqFile);
        RequestBody name = RequestBody.create(MediaType.parse("text/plain"), "upload");
        String token = Helper.getConfigValue(CarNumberDetectionActivity.this, "PLATE_RECOGNIZER_TOKEN");
        Call<ResponseBody> req = apiService.postImage(body, name, "Token " + token);
        req.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("LongLogTag")
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Toast.makeText(getApplication(), response.code() + " ", Toast.LENGTH_SHORT).show();
                try {
                    String resp = response.body().string();
                    JsonParser parser = new JsonParser();
                    JsonElement jsonTree = parser.parse(resp);
                    if (jsonTree.isJsonObject()) {
                        JsonObject jsonObject = null;
                        String plate = "";
                        JsonElement score = null;
                        JsonElement countryCode = null;
                        try {
                            jsonObject = jsonTree.getAsJsonObject();
                            plate = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonPrimitive("plate").getAsString();
                            score = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonPrimitive("score");
                            countryCode = jsonObject.getAsJsonArray("results").get(0).getAsJsonObject().getAsJsonObject("region").getAsJsonPrimitive("code");
                        } catch (Exception e) {
                            Helper.deleteImageByPath(picturePath);
                            Log.e("JSON exception", e.getMessage());
                            return;
                        }
                        if (countryCode.getAsString().equals("ru") && score.getAsDouble() > 0.8) {
                            violationReport.carNumber = new CarNumber(plate);
                            try {
                                Integer.parseInt(violationReport.carNumber.getRegistrationNumber());
                            } catch (NumberFormatException exception) {
                                Helper.deleteImageByPath(picturePath);
                                Log.e("RegistrationNumber parsing exception", exception.getMessage());
                                return;
                            }
                            if (carNumberText.getVisibility() == View.VISIBLE) {
                                Helper.deleteImageByPath(picturePath);
                                return;
                            }
                            CarNumberDetectionActivity.this.runOnUiThread(new Runnable() {
                                public void run() {
                                    isRecognized = true;
                                    cameraView.camera.stopPreview();
                                    setRecognizedPanelVisible(violationReport.carNumber.toString());
                                    createAndStartCancelTimer(picturePath);
                                }
                            });
                        } else Helper.deleteImageByPath(picturePath);
                    } else Helper.deleteImageByPath(picturePath);
                } catch (Exception e) {
                    Helper.deleteImageByPath(picturePath);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Helper.deleteImageByPath(picturePath);
                Toast.makeText(getApplication(), "Request failed", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    @SuppressLint("LongLogTag")
    private void recognizeNumberByOpenALPR(String picturePath)
    {
        Log.i("ALPR: Recognizing picture path", picturePath);
        //хотел использовать 'ru', но не работает(
        String recognizingResult = alpr.recognizeWithCountryRegionNConfig("eu", "", picturePath, openAlprConfFile, 10);
        Log.i("OpenALPR result", recognizingResult);
        //Результаты представлены в JSON Формате, поэтому парсим их с помощью GSON
        final Results results = new Gson().fromJson(recognizingResult, Results.class);
        if (results != null && results.getResults() != null && results.getResults().size() != 0) {
            List<Result> resultsList = results.getResults();
            for (int i = 0; i < resultsList.size(); ++i) {
                {
                    Result result = resultsList.get(i);
                    String plate = result.getPlate();
                    //Длина распознанного номера должны быть не меньше 6, но может распознать ещё и регион, тогда ещё плюс 2 или 3 (8 или 9 соответственно)
                    if (result.getConfidence() > 82 && plate.length() >= 6) {
                        violationReport.carNumber = new CarNumber(plate);
                        try {
                            Integer.parseInt(violationReport.carNumber.getRegistrationNumber());
                        } catch (NumberFormatException exception) {
                            Log.e("RegistrationNumber parsing exception", exception.getMessage());
                            continue;
                        }
                        //проверяем, не распознал ли номер уже другой процесс
                        if (carNumberText.getVisibility() == View.VISIBLE) {
                            Helper.deleteImageByPath(picturePath);
                            return;
                        }
                        //Если все условия пройдены, работаем с UI и устанавливаем 10-секундный таймер
                        CarNumberDetectionActivity.this.runOnUiThread(new Runnable() {
                            public void run() {
                                isRecognized = true;
                                cameraView.camera.stopPreview();
                                setRecognizedPanelVisible(violationReport.carNumber.toString());
                                createAndStartCancelTimer(picturePath);
                            }
                        });
                        return;
                    }
                }
            }
            Helper.deleteImageByPath(picturePath);
        } else Helper.deleteImageByPath(picturePath);
    }
}
