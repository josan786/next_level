package ru.konstantin_starikov.samsung.izhhelper.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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

        //получаем переданный violationReport (с всеми данными правонарушения)
        violationReport = (ViolationReport) getIntent().getSerializableExtra(PhotofixationActivity.VIOLATION_REPORT);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_violation);

        violationPlaceText = findViewById(R.id.violationPlaceText);
        violationTypeText = findViewById(R.id.violationTypeText);
        violationCarNumberText = findViewById(R.id.violationCarNumberText);

        //установка полученных значений
        //установка места нарушения
        violationPlaceText.setText("Место: " + violationReport.place);
        //установка типа нарушения
        violationTypeText.setText("Тип нарушения: " + violationReport.violationType.toString());

        //установка номера машины
        String carNumber = "";
        carNumber += violationReport.carNumber.getSeries().charAt(0);
        carNumber += violationReport.carNumber.getRegistrationNumber();
        carNumber += violationReport.carNumber.getSeries().substring(1,3);
        Log.i("CarNumber series", violationReport.carNumber.getSeries());
        Log.i("CarNumber registration number", violationReport.carNumber.getRegistrationNumber());
        violationCarNumberText.setText("Номер машины: " + carNumber);

        //ActionBar - настройка
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Отправить нарушение");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void sendViolation(View v)
    {
        Intent openSuccessfullySentIntent = new Intent(SendViolationActivity.this, SuccessfullySentActivity.class);
        startActivity(openSuccessfullySentIntent);
    }
}