package ru.konstantin_starikov.samsung.izhhelper.models;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import ru.konstantin_starikov.samsung.izhhelper.fragments.CarViewpointFragment;
import ru.konstantin_starikov.samsung.izhhelper.views.CameraView;

public class PhotofixationSequence implements CameraView.SaveImageListener{
    private CameraView cameraView;
    private CarViewpointFragment viewpointFragment;
    private ArrayList<PhotoDescription> photosDescriptions;
    private int photofixationInterval;
    private ProgressBar progressBar;
    private TextView photoDescriptionText;
    private TextView timerText;
    private PhotofixationPictureTakingListener pictureTakingListener;

    private CountDownTimer timer;
    private int nowTimerIndex = 0;

    private PhotofixationSequence getThis()
    {
        return this;
    }

    public PhotofixationSequence(CameraView cameraView, PhotofixationPictureTakingListener pictureTakingListener, int photofixationInterval, ProgressBar progressBar, TextView timerText, TextView photoDescriptionText, CarViewpointFragment viewpointFragment, ArrayList<PhotoDescription> photosDescriptions) {
        this.pictureTakingListener = pictureTakingListener;
        this.cameraView = cameraView;
        this.viewpointFragment = viewpointFragment;
        this.photosDescriptions = photosDescriptions;
        this.photoDescriptionText = photoDescriptionText;
        this.photofixationInterval = photofixationInterval;
        this.progressBar = progressBar;
        this.timerText = timerText;
    }

    public void start()
    {
        if(photosDescriptions != null) {
            photoDescriptionText.setText(photosDescriptions.get(0).getDescription());
            viewpointFragment.setImage(photosDescriptions.get(0).getViewpointDrawable());
        }
        timer = new CountDownTimer(photofixationInterval * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        timerText.setText(Long.toString(millisUntilFinished / 1000));
                        progressBar.setProgress((int) millisUntilFinished / 1000);
                    }
                });
            }

            @SuppressLint("LongLogTag")
            @Override
            public void onFinish() {
                try {
                    cameraView.takePicture(getThis());
                }
                catch (Exception e){
                    Log.e("Taking picture exception", "не удаётся сделать снимок : " + e.getMessage());
                }
                nowTimerIndex++;
                if(nowTimerIndex < photosDescriptions.size()) {
                    photoDescriptionText.setText(photosDescriptions.get(nowTimerIndex).getDescription());
                    viewpointFragment.setReadiness((int) Math.ceil(((double) nowTimerIndex/photosDescriptions.size()) * 100));
                    viewpointFragment.setImage(photosDescriptions.get(nowTimerIndex).getViewpointDrawable());
                    timer.start();
                }
            }
        };
        timer.start();
    }

    @Override
    public void saveFile(String path) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                if (nowTimerIndex >= photosDescriptions.size())
                    pictureTakingListener.onPhotofixationFinished(path);
                else pictureTakingListener.saveTakedPicture(path);
            }
        });
    }

    @Override
    public void error(Exception e) {
        Log.e("Save file error", "не удаётся сохранить изображение для распознавания: " + e.getMessage());
    }
}
