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

import ru.konstantin_starikov.samsung.izhhelper.views.CameraView;

public class PhotofixationSequence implements CameraView.SaveImageListener{
    private CameraView cameraView;
    private ArrayList<String> photosDescriptions;
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

    public PhotofixationSequence(CameraView cameraView, PhotofixationPictureTakingListener pictureTakingListener, int photofixationInterval, ProgressBar progressBar, TextView timerText, TextView photoDescriptionText, ArrayList<String> photosDescriptions) {
        this.pictureTakingListener = pictureTakingListener;
        this.cameraView = cameraView;
        this.photosDescriptions = photosDescriptions;
        this.photoDescriptionText = photoDescriptionText;
        this.photofixationInterval = photofixationInterval;
        this.progressBar = progressBar;
        this.timerText = timerText;
    }

    public void start()
    {
        if(photosDescriptions != null) photoDescriptionText.setText(photosDescriptions.get(0));
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
                    photoDescriptionText.setText(photosDescriptions.get(nowTimerIndex));
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
