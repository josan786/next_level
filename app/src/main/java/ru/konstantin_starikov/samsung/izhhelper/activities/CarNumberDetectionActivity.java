package ru.konstantin_starikov.samsung.izhhelper.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

import com.google.gson.Gson;

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

        //получаем переданный violationReport (с типом нарушения)
        violationReport = (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_number_detection);

        cameraView = findViewById(R.id.cameraView);
        foreground = findViewById(R.id.foreground);
        cancelButton = findViewById(R.id.cancelButton);
        timerSecondsCountText = findViewById(R.id.timerSecondsCountText);
        informationLayout = findViewById(R.id.informationLayout);
        carNumberText = findViewById(R.id.carNumberText);

        cameraView.setFocusable(true);

        //Проверяем, есть ли доступ к камере и памяти устройства
        //Если разрешения отсутсвуют, спрашиваем их у пользователя
        if(checkIfAlreadyHaveStoragePermission())
        {
            if(checkIfAlreadyHaveCameraPermission())
            {
                carNumberRecognizeTimer = new Timer();
                carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, 3500);
            }
            else requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
        }
        else requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        //ActionBar - настройка
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Определение номера");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //OpenALPR
        ANDROID_DATA_DIR = this.getApplicationInfo().dataDir;
        openAlprConfFile = ANDROID_DATA_DIR + File.separatorChar + "runtime_data" + File.separatorChar + "openalpr.conf";
        alpr = OpenALPR.Factory.create(CarNumberDetectionActivity.this, ANDROID_DATA_DIR);
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
                    }
                    else requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
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
                    cameraView.refreshCamera();
                    cameraView.ini();
                    if(checkIfAlreadyHaveStoragePermission()) {
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
            }
            catch (Exception e){
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

    public void cancelResults(View v)
    {
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
        Bitmap rotatedPicture = rotateImageIfRequired(getBitmapFromPath(picturePath));
        //удаляем исходное изображение
        deleteImageByPath(picturePath);
        //дальше получаем путь корректного изображения
        String rotatedPicturePath = "";
        FileOutputStream fileOutputStream = null;
        try {
            rotatedPicturePath = String.format(getApplicationInfo().dataDir + File.separator + "%d.jpg", System.currentTimeMillis());
            fileOutputStream = new FileOutputStream(rotatedPicturePath);
            rotatedPicture.compress(Bitmap.CompressFormat.PNG, 90, fileOutputStream);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        finally {
            try {
                fileOutputStream.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        //передаём изображение на распознавание
        recognizeNumber(rotatedPicturePath);
    }

    private Bitmap rotateImageIfRequired(Bitmap img) {

        if(img.getWidth() > img.getHeight()) return rotateImage(img, 90);
        else return img;
    }

    private Bitmap rotateImage(Bitmap image, int degree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(image, 0, 0, image.getWidth(), image.getHeight(), matrix, true);
        image.recycle();
        return rotatedImg;
    }

    private Bitmap getBitmapFromPath(String imagePath) {

        File imgFile = new  File(imagePath);
        Bitmap result = null;

        if(imgFile.exists()) {
            result = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        }

        return result;
    }

    @SuppressLint("LongLogTag")
    private void deleteImageByPath(String imagePath)
    {
        Log.i("Image deleted, path was - ", imagePath);
        new File(imagePath).delete();
    }

    private void recognizeNumber(String picturePath) {
        AsyncTask.execute(new Runnable() {
            @SuppressLint("LongLogTag")
            @Override
            public void run() {
                Log.i("Recognizing picture path", picturePath);
                String result = alpr.recognizeWithCountryRegionNConfig("eu", "", picturePath, openAlprConfFile, 10);
                Log.i("OpenALPR result", result);
                //Результаты представлены в JSON Формате, поэтому парсим их с помощью GSON
                final Results results = new Gson().fromJson(result, Results.class);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (results != null && results.getResults() != null && results.getResults().size() != 0) {
                            List<Result> resultsList = results.getResults();
                            boolean isRecognizedCorrectly = false;
                            for (int i = 0; i < resultsList.size(); ++i) {
                                {
                                    Result result = resultsList.get(i);
                                    String plate = result.getPlate();
                                    //Длина распознанного номера должны быть не меньше 6, но может распознать ещё и регион, тогда ещё плюс 2 или 3 (8 или 9 соответственно)
                                    if (result.getConfidence() > 82 && plate.length() >= 6) {
                                        String carNumberSeries = "";
                                        //Серия номера машины - это первый, пятый и шестой символы (это  буквы)
                                        carNumberSeries += plate.charAt(0);
                                        carNumberSeries += plate.charAt(4);
                                        carNumberSeries += plate.charAt(5);
                                        //Регистрационный номер машины - второй, третий и четвёртый символы (это цифры)
                                        String carNumberRegistrationNumber = "";
                                        carNumberRegistrationNumber += plate.charAt(1);
                                        carNumberRegistrationNumber += plate.charAt(2);
                                        carNumberRegistrationNumber += plate.charAt(3);
                                        //Проверяем, действительно ли регистрационный номер представляет из себя число
                                        try {
                                            Integer.parseInt(carNumberRegistrationNumber);
                                        }
                                        catch (NumberFormatException exception)
                                        {
                                            Log.e("RegistrationNumber parsing exception", exception.getMessage());
                                            continue;
                                        }
                                        violationReport.carNumber = new CarNumber(carNumberSeries, carNumberRegistrationNumber);

                                        //проверяем, не распознал ли номер уже другой процесс
                                        if(carNumberText.getVisibility() == View.VISIBLE)
                                        {
                                            deleteImageByPath(picturePath);
                                            return;
                                        }
                                        //Если все условия пройдены, работаем с UI и устанавливаем 10-секундный таймер
                                        CarNumberDetectionActivity.this.runOnUiThread(new Runnable() {
                                            public void run() {
                                                carNumberText.setVisibility(View.VISIBLE);
                                                carNumberText.setText(plate);
                                                informationLayout.setVisibility(View.INVISIBLE);
                                                foreground.setVisibility(View.VISIBLE);
                                                foreground.setAlpha(0.2f);
                                                cancelButton.setVisibility(View.VISIBLE);
                                                timerSecondsCountText.setVisibility(View.VISIBLE);
                                            }
                                        });
                                        carNumberRecognizeTimer.cancel();
                                        cancelTimer = new CountDownTimer(10 * 1000, 1000) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                                CarNumberDetectionActivity.this.runOnUiThread(new Runnable() {
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
                                        return;
                                    }
                                }
                            }
                            deleteImageByPath(picturePath);
                        }
                    }
                });
            }
        });
    }
}