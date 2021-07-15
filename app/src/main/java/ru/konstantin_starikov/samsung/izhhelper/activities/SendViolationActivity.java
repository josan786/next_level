package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.CountDownLatch;

import cn.pedant.SweetAlert.SweetAlertDialog;
import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.interfaces.Action;

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

        findAndSetViews();

        setStartsValues();

        tuneActionBar();
    }

    private ViolationReport getTransmittedViolationReport() {
        return (ViolationReport) getIntent().getSerializableExtra(MainMenuActivity.VIOLATION_REPORT);
    }

    private void findAndSetViews() {
        violationPlaceText = findViewById(R.id.violationPlaceText);
        violationTypeText = findViewById(R.id.violationTypeText);
        violationCarNumberText = findViewById(R.id.violationCarNumberText);
    }

    private void setStartsValues() {
        violationPlaceText.setText(getString(R.string.Place) + ": " + violationReport.location.getPlace());
        violationTypeText.setText(getString(R.string.ViolationType) + ": " + violationReport.violationType.toString(this));
        violationCarNumberText.setText(getString(R.string.CarNumber) + ": " + violationReport.carNumber.toString());
    }

    private void tuneActionBar() {
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
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(R.color.blueAppBarColor);
        pDialog.setTitleText(getString(R.string.Loading));
        pDialog.setCancelable(false);
        pDialog.show();
/*        CountDownLatch countDownLatch = new CountDownLatch(3);
        violationReport.setCountDownLatch(countDownLatch);
        try {
            Thread submitViolationThread = new Thread(){
                @Override
                public void run()
                {*/
                    violationReport.submitViolationToAuthorizedBodyAndDoAction(SendViolationActivity.this, new Action() {
                        @Override
                        public void run() {
                            Intent openSuccessfullySentIntent = new Intent(SendViolationActivity.this, SuccessfullySentActivity.class);
                            startActivity(openSuccessfullySentIntent);
                        }
                    });
/*                }
            };
            submitViolationThread.start();
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        finally {
            Intent openSuccessfullySentIntent = new Intent(SendViolationActivity.this, SuccessfullySentActivity.class);
            startActivity(openSuccessfullySentIntent);
        }*/
    }
}