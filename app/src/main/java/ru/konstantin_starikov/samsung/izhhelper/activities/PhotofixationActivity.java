package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.fragments.CarViewpointFragment;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.PhotoDescription;
import ru.konstantin_starikov.samsung.izhhelper.models.interfaces.PhotofixationPictureTakingListener;
import ru.konstantin_starikov.samsung.izhhelper.models.PhotofixationSequence;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.enumerators.ViolationTypeEnum;
import ru.konstantin_starikov.samsung.izhhelper.views.CameraView;

public class PhotofixationActivity extends AppCompatActivity implements PhotofixationPictureTakingListener {

    public final static String VIOLATION_REPORT = "violation_report";

    private ViolationReport violationReport;

    private CameraView cameraView;

    private CarViewpointFragment carViewpointFragment;

    private ProgressBar progressBar;
    private TextView timerText;
    private TextView photoDescriptionTextView;

    private PhotofixationSequence photofixationSequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        violationReport = getTransmittedViolationReport();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photofixation);

        findAndSetViews();
        carViewpointFragment = (CarViewpointFragment) getSupportFragmentManager().findFragmentById(R.id.carViewpointFragment);

        tuneActionBar();
        ArrayList<PhotoDescription> photosDescriptions = fillAndGetPhotosDescriptions();
        photofixationSequence = new PhotofixationSequence(cameraView, this,15,
                progressBar, timerText, photoDescriptionTextView, carViewpointFragment, photosDescriptions);
    }

    private ArrayList<PhotoDescription> fillAndGetPhotosDescriptions()
    {
        ArrayList<PhotoDescription> photosDescriptions = new ArrayList<PhotoDescription>();
        Resources resources = getResources();
        if(violationReport.violationType.getViolationType() == ViolationTypeEnum.Lawn)
            photosDescriptions.add(new PhotoDescription(getString(R.string.LawnPhotoDescription), resources.getDrawable(R.drawable.lawn_contact)));
        if(violationReport.violationType.getViolationType() == ViolationTypeEnum.ParkingProhibited)
            photosDescriptions.add(new PhotoDescription(getString(R.string.ParkingProhibitedPhotoDescription), resources.getDrawable(R.drawable.parking_prohibited_view)));
        if(violationReport.violationType.getViolationType() == ViolationTypeEnum.StoppingProhibited)
            photosDescriptions.add(new PhotoDescription(getString(R.string.StoppingProhibitedPhotoDescription), resources.getDrawable(R.drawable.stop_prohibited_view)));
        if(violationReport.violationType.getViolationType() == ViolationTypeEnum.Pavement)
            photosDescriptions.add(new PhotoDescription(getString(R.string.PavementPhotoDescription), resources.getDrawable(R.drawable.pavement_contact)));
        if(violationReport.violationType.getViolationType() == ViolationTypeEnum.PedestrianCrossing)
            photosDescriptions.add(new PhotoDescription(getString(R.string.PedestrianCrossingPhotoDescription), resources.getDrawable(R.drawable.pedestrian_crossing_contact)));
        photosDescriptions.add(new PhotoDescription(getString(R.string.OverallPlanPhotoDescription), resources.getDrawable(R.drawable.overall_plan)));
        return photosDescriptions;
    }


    @Override
    protected void onStart() {
        super.onStart();
        photofixationSequence.start();
    }

    private void findAndSetViews()
    {
        cameraView = findViewById(R.id.cameraViewPhotofixation);
        progressBar = findViewById(R.id.timerProgressBar);
        timerText = findViewById(R.id.photofixationTimerText);
        photoDescriptionTextView = findViewById(R.id.photoDescriptionText);
    }

    private ViolationReport getTransmittedViolationReport()
    {
        return (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.PhotofixationActivityTitle));
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
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

    @SuppressLint("LongLogTag")
    @Override
    public void saveTakedPicture(String picturePath) {
        cameraView.refreshCamera();
        cameraView.ini();
        Bitmap rotatedPicture = Helper.rotateImageIfRequired(Helper.getBitmapFromPath(picturePath));
        Helper.deleteImageByPath(picturePath);
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
        violationReport.addPhoto(rotatedPicturePath.substring(rotatedPicturePath.lastIndexOf('/') + 1));
    }

    @Override
    public void onPhotofixationFinished(String picturePath) {
        this.runOnUiThread(new Runnable() {
            public void run() {
                carViewpointFragment.setReadiness(100);
            }
        });
        saveTakedPicture(picturePath);
        Intent openSendViolationIntent = new Intent(PhotofixationActivity.this, SendViolationActivity.class);
        openSendViolationIntent.putExtra(VIOLATION_REPORT, violationReport);
        startActivity(openSendViolationIntent);
    }
}