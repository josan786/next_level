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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.views.CameraView;

public class CarNumberDetectionActivity extends AppCompatActivity {

    public final static String VIOLATION_REPORT = "violation_report";

    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PERMISSION_REQUEST_CAMERA = 2;

    private ViolationReport violationReport;

    private CameraView cameraView;
    private Timer carNumberRecognizeTimer;
    private Timer cancelTimer;
    private Button foreground;
    private Button cancelButton;
    private TextView timerSecondsCountText;
    private TextView carNumberText;
    private FrameLayout informationLayout;

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
                carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, 1500);
            }
            else requestPermissions(new String[]{Manifest.permission.CAMERA}, 2);
        }
        else requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        //ActionBar - настройка
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Определение номера");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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
                        carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, 1500);
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
                        carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 3500, 1500);
                    }
                } else {
                    Toast.makeText(this, "Нет доступа к камере", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }

    class CarNumberRecognizeUpdateTimerTask extends TimerTask implements CameraView.SaveImageListener {
        public void run() {
            try {
                cameraView.takePicture(this);
            }
            catch (Exception e){
                carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimerTask(), 1500);
                Log.i("TAKING_PICTURE", "не удаётся сделать снимок : " + e.getMessage());
            }
        }

        @Override
        public void saveFile(String path) {
            try {
                String picturePath = path;
                processPicture(picturePath);
                cameraView.refreshCamera();
                cameraView.ini();
                Toast.makeText(CarNumberDetectionActivity.this, "Path" + path, Toast.LENGTH_SHORT).show();
            }
            catch (Exception exception)
            {
                Toast.makeText(CarNumberDetectionActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void error(Exception e) {
            Toast.makeText( CarNumberDetectionActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    class CancelUpdateTimerTask extends TimerTask {
        public void run() {
            cancelTimer.cancel();
            Intent openPhotofixationIntent = new Intent(CarNumberDetectionActivity.this, PhotofixationActivity.class);
            openPhotofixationIntent.putExtra(VIOLATION_REPORT, violationReport);
            startActivity(openPhotofixationIntent);
        }
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

    @SuppressLint("Range")
    public void processPicture(String picturePath) {
        if(recognizeNumber(picturePath)) {
            carNumberRecognizeTimer.cancel();
            CarNumberDetectionActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    informationLayout.setVisibility(View.INVISIBLE);
                    foreground.setVisibility(View.VISIBLE);
                    foreground.setAlpha(0.1f);
                    cancelButton.setVisibility(View.VISIBLE);
                    timerSecondsCountText.setVisibility(View.VISIBLE);
                }
            });
            cancelTimer = new Timer();
            cancelTimer.schedule(new CancelUpdateTimerTask(), 10 * 1000);
        }
    }

    private boolean recognizeNumber(String picturePath) {
        return true;
    }
}