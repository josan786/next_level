package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Timer;
import java.util.TimerTask;

import ru.konstantin_starikov.samsung.izhhelper.R;

public class SuccessfullySentActivity extends AppCompatActivity {

    private Timer transitionInMainMenuTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successfully_sent);

        transitionInMainMenuTimer = new Timer();
    }

    @Override
    protected void onStart() {
        super.onStart();
        transitionInMainMenuTimer.schedule(new TransitionInMainMenuTimerTask(), 5000);
    }

    class TransitionInMainMenuTimerTask extends TimerTask {

        @Override
        public void run() {
            Intent openMainMenuIntent = new Intent(SuccessfullySentActivity.this, MainMenuActivity.class);
            startActivity(openMainMenuIntent);
        }
    }
}