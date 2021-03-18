package ru.konstantin_starikov.samsung.izhhelper;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class CarNumberDetectionActivity extends AppCompatActivity implements CameraView.SaveImageListener {

    public final static String VIOLATION_REPORT = "violation_report";

    private ViolationReport violationReport;

    private SurfaceView preview;
    private CameraView cameraView;
    private Timer carNumberRecognizeTimer;
    private Timer cancelTimer;
    private Button foreground;
    private Button cancelButton;
    private TextView timerSecondsCountText;
    private FrameLayout informationLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //получаем переданный violationReport (с ипом нарушения)
        violationReport = (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_car_number_detection);

        cameraView =  findViewById(R.id.cameraView);
        foreground = findViewById(R.id.foreground);
        cancelButton = findViewById(R.id.cancelButton);
        timerSecondsCountText = findViewById(R.id.timerSecondsCount);
        //informationLayout = findViewById(R.id.i)

        carNumberRecognizeTimer = new Timer();
        carNumberRecognizeTimer.schedule(new CarNumberRecognizeUpdateTimeTask (), 0, 1500);

        //ActionBar - настройка
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Определение номера");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    class CarNumberRecognizeUpdateTimeTask extends TimerTask {
        public void run() {
            takePicture();
        }
    }

    class CancelUpdateTimeTask extends TimerTask {
        public void run() {
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

    public void takePicture() {
        cameraView.takePicture(this);
    }

    @SuppressLint("Range")
    public void processPicture(String picturePath) {
        if(recognizeNumber(null)) {
            carNumberRecognizeTimer.cancel();
            foreground.setVisibility(View.VISIBLE);
            foreground.setAlpha(10.0f);
            cancelButton.setAlpha(View.VISIBLE);
            timerSecondsCountText.setAlpha(View.VISIBLE);

            cancelTimer = new Timer();
            cancelTimer.schedule(new CancelUpdateTimeTask(), 0, 10 * 1000);
        }
    }

    private boolean recognizeNumber(Picture picture) {
        return true;
    }

    @Override
    public void saveFile(String path) {
        String picturePath = path;
        processPicture(picturePath);
    }

    @Override
    public void error(Exception e) {

    }
}