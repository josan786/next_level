package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CountDownLatch;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;

public class SendViolationActivity extends AppCompatActivity {

    private ViolationReport violationReport;

    private TextView violationPlaceText;
    private TextView violationTypeText;
    private TextView violationCarNumberText;

    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        violationReport = getTransmittedViolationReport();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_violation);

        violationPlaceText = findViewById(R.id.violationPlaceText);
        violationTypeText = findViewById(R.id.violationTypeText);
        violationCarNumberText = findViewById(R.id.violationCarNumberText);

        //установка полученных значений
        //установка места нарушения
        violationPlaceText.setText("Место: " + violationReport.location.getPlace());
        //установка типа нарушения
        violationTypeText.setText("Тип нарушения: " + violationReport.violationType.toString());

        //установка номера машины
        Log.i("CarNumber series", violationReport.carNumber.getSeries());
        Log.i("CarNumber registration number", violationReport.carNumber.getRegistrationNumber());
        violationCarNumberText.setText("Номер машины: " + violationReport.carNumber.toString());

        tuneActionBar();
    }

    private ViolationReport getTransmittedViolationReport()
    {
        return (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Отправка нарушения");
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

    public void sendViolation(View v)
    {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        violationReport.setCountDownLatch(countDownLatch);
        try {
            Thread submitViolationThread = new Thread(){
                @Override
                public void run()
                {
                    violationReport.submitViolationToAuthorizedBody(SendViolationActivity.this);
                }
            };
            submitViolationThread.start();
            countDownLatch.await();
            Intent openSuccessfullySentIntent = new Intent(SendViolationActivity.this, SuccessfullySentActivity.class);
            startActivity(openSuccessfullySentIntent);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}