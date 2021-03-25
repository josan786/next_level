package ru.konstantin_starikov.samsung.izhhelper.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.fragments.CarViewpointFragment;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.views.CameraView;

public class PhotofixationActivity extends AppCompatActivity {

    public final static String VIOLATION_REPORT = "violation_report";

    private ViolationReport violationReport;

    private CameraView cameraView;

    private Timer takingPhotosTimer;

    CarViewpointFragment carViewpointFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //получаем переданный violationReport (с номером автомобиля правонарушителя)
        violationReport = (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photofixation);

        cameraView = findViewById(R.id.cameraViewPhotofixation);
        carViewpointFragment = (CarViewpointFragment) getSupportFragmentManager().findFragmentById(R.id.carViewpointFragment);

        //ActionBar - настройка
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Фотофиксация");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        takingPhotosTimer = new Timer();
        takingPhotosTimer.schedule(new TakingPhotosTimerTask(), 5000);
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

    class TakingPhotosTimerTask extends TimerTask implements CameraView.SaveImageListener{
        @Override
        public void run() {
            Intent openSendViolationIntent = new Intent(PhotofixationActivity.this, SendViolationActivity.class);
            openSendViolationIntent.putExtra(VIOLATION_REPORT, violationReport);
            startActivity(openSendViolationIntent);
/*            cameraView.takePicture(this);
            cameraView.refreshCamera();*/
        }

        @Override
        public void saveFile(String path) {
            carViewpointFragment.addPhotoToProcessing(null);
            carViewpointFragment.updateUserPosition();
        }

        @Override
        public void error(Exception e) {
            Toast.makeText( PhotofixationActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}