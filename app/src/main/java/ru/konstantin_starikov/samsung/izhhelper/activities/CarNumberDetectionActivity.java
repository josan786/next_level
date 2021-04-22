package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ru.konstantin_starikov.samsung.izhhelper.R;
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        violationReport = getTransmittedViolationReport();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_number_detection);

        findAndSetViews();

        tuneCamera();

        tuneActionBar();

        //Проверяем, есть ли доступ к камере и памяти устройства
        //Если разрешения отсутсвуют, спрашиваем их у пользователя
        if (checkIfAlreadyHaveStoragePermission()) {
            if (checkIfAlreadyHaveCameraPermission()) {
                carNumberRecognizeTimer = new Timer();
                carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, recognitionPeriod);
            } else requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
        } else requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        ANDROID_DATA_DIR = this.getApplicationInfo().dataDir;
        setupALPR();
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
                        carNumberRecognizeTimer = new Timer();
                        carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, recognitionPeriod);
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
                        carNumberRecognizeTimer = new Timer();
                        carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, recognitionPeriod);
                    }
                } else {
                    Toast.makeText(this, "Нет доступа к камере", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    class CarNumberRecognizeUpdateTimerTask extends TimerTask implements CameraView.SaveImageListener {
        @SuppressLint("LongLogTag")
        public void run() {
            try {
                cameraView.takePicture(this);
            } catch (Exception e) {
                Log.e("Taking picture exception", "не удаётся сделать снимок : " + e.getMessage());
            }
        }

        @Override
        public void saveFile(String path) {
            cameraView.refreshCamera();
            cameraView.ini();
            String picturePath = path;
            processPicture(picturePath);
        }

        @Override
        public void error(Exception e) {
            Log.e("Save file error", "не удаётся сохранить изображение для распознавания: " + e.getMessage());
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
        carNumberRecognizeTimer = new Timer();
        carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, recognitionPeriod);
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
            rotatedPicture.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timerSecondsCountText.setText("0:" + Long.toString(millisUntilFinished / 1000));
                    }
                });
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

    //Todo: отрефакторить этот ужас
    private void recognizeNumber(String picturePath) {
        AsyncTask.execute(new Runnable() {
            @SuppressLint("LongLogTag")
            @Override
            public void run() {
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
                        int totalCalls;
                        int callsUsage;
                        try {
                            jsonObject = jsonTree.getAsJsonObject();
                            totalCalls = jsonObject.getAsJsonPrimitive("total_calls").getAsInt();
                            callsUsage = jsonObject.getAsJsonObject("usage").getAsJsonPrimitive("calls").getAsInt();
                        } catch (Exception e) {
                            Log.e("JSON exception", e.getMessage());
                            return;
                        }
                        if (totalCalls > callsUsage) isPlateRecognizerWork = true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (Helper.isOnline(CarNumberDetectionActivity.this) && isPlateRecognizerWork) {
                    AsyncTask.execute(new Runnable() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void run() {
                            try {
                                HttpResponse<String> response = Unirest.post("https://api.platerecognizer.com/v1/plate-reader/")
                                        .header("Authorization", "Token " + Helper.getConfigValue(CarNumberDetectionActivity.this, "PLATE_RECOGNIZER_TOKEN"))
                                        .field("upload", new File(picturePath))
                                        .asString();
                                Log.i("Recognize: ", response.getBody());
                                JsonParser parser = new JsonParser();
                                JsonElement jsonTree = parser.parse(response.getBody());
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
                                            Log.e("RegistrationNumber parsing exception", exception.getMessage());
                                            return;
                                        }
                                        if (carNumberText.getVisibility() == View.VISIBLE) {
                                            Helper.deleteImageByPath(picturePath);
                                            return;
                                        }
                                        CarNumberDetectionActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                setRecognizedPanelVisible(violationReport.carNumber.toString());
                                                carNumberRecognizeTimer.cancel();
                                                createAndStartCancelTimer(picturePath);
                                            }
                                        });
                                    } else Helper.deleteImageByPath(picturePath);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                } else {
                    AsyncTask.execute(new Runnable() {
                        @SuppressLint("LongLogTag")
                        @Override
                        public void run() {
                            Log.i("Recognizing picture path", picturePath);
                            //хотел использовать 'ru', но не работает(
                            String recognizingResult = alpr.recognizeWithCountryRegionNConfig("eu", "", picturePath, openAlprConfFile, 10);
                            Log.i("OpenALPR result", recognizingResult);
                            //Результаты представлены в JSON Формате, поэтому парсим их с помощью GSON
                            final Results results = new Gson().fromJson(recognizingResult, Results.class);
                            if (results != null && results.getResults() != null && results.getResults().size() != 0) {
                                List<Result> resultsList = results.getResults();
                                boolean isRecognizedCorrectly = false;
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
                                                    setRecognizedPanelVisible(violationReport.carNumber.toString());
                                                    carNumberRecognizeTimer.cancel();
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
                    });
                }
            }
        });
    }
}
