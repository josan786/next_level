package ru.konstantin_starikov.samsung.izhhelper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Address;

public class MainMenuActivity extends AppCompatActivity {

    private Account userAccount = null;

    public final static String VIOLATION_REPORT = "violation_report";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        long accountID = getIntent().getLongExtra(LoginActivity.ACCOUNT_ID, 0);
        userAccount = GetUserAccountByID(accountID);
        GetUserNameTextView().setText(userAccount.firstName + " " + userAccount.lastName);
        GetUserIDTextView().setText("\u0040" + Long.toString(userAccount.ID));
    }

    public void CreateViolationReport(View v)
    {
        ViolationReport violationReport = new ViolationReport();
        violationReport.senderAccount = userAccount;

        Intent choosePlaceIntent = new Intent(MainMenuActivity.this, PlaceChoiceActivity.class);
        choosePlaceIntent.putExtra(VIOLATION_REPORT, violationReport);
        startActivity(choosePlaceIntent);
    }

    private TextView GetUserNameTextView()
    {
        return (TextView) findViewById(R.id.userName);
    }

    private TextView GetUserIDTextView()
    {
        return (TextView) findViewById(R.id.userID);
    }

    private Account GetUserAccountByID(long accountID)
    {
        Account result = new Account();
        result.firstName = "Иван";
        result.lastName = "Иванов";
        result.phoneNumber = "89513406702";
        result.address = new Address("106", "Ленина", 53, "Ижевск");
        return  result;
    }
}