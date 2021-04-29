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

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        violationReport = getTransmittedViolationReport();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_violation);

        violationPlaceText = findViewById(R.id.violationPlaceText);
        violationTypeText = findViewById(R.id.violationTypeText);
        violationCarNumberText = findViewById(R.id.violationCarNumberText);

        violationPlaceText.setText(getString(R.string.Place) + ": " + violationReport.location.getPlace());
        violationTypeText.setText(getString(R.string.ViolationType) + ": " + violationReport.violationType.toString());
        violationCarNumberText.setText(getString(R.string.CarNumber) + ": " + violationReport.carNumber.toString());

        tuneActionBar();
    }

    private ViolationReport getTransmittedViolationReport()
    {
        return (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.SendViolationActivityTitle));
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