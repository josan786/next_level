package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Address;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;

public class MainMenuActivity extends AppCompatActivity {

    private Account userAccount = null;

    public final static String VIOLATION_REPORT = "violation_report";

    private ListView violationReportsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //строчка оставлена закоментированной потому что часто приходится удалять старую базу данных
        //this.deleteDatabase( "violations.db");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        findAndSetViews();
        long accountID = getIntent().getLongExtra(LoginActivity.ACCOUNT_ID, 0);
        userAccount = getUserAccountByID(accountID);
        userAccount.loadViolations(this);
        fillViolationReports();
        getUserNameTextView().setText(userAccount.firstName + " " + userAccount.lastName);
        getUserIDTextView().setText("\u0040" + Long.toString(userAccount.ID));
    }

    private void findAndSetViews()
    {
        violationReportsList = findViewById(R.id.violationReports);
    }

    private void fillViolationReports()
    {
        ViolationReportsListAdapter violationReportsListAdapter = new ViolationReportsListAdapter(this, R.layout.violation_report_item, userAccount.getViolationReports());
        violationReportsList.setAdapter(violationReportsListAdapter);
    }

    public void createViolationReport(View v)
    {
        ViolationReport violationReport = new ViolationReport();
        violationReport.senderAccount = userAccount;
        Log.i("Report ID: ", violationReport.getID());

        Intent choosePlaceIntent = new Intent(MainMenuActivity.this, PlaceChoiceActivity.class);
        choosePlaceIntent.putExtra(VIOLATION_REPORT, violationReport);
        startActivity(choosePlaceIntent);
    }

    private TextView getUserNameTextView()
    {
        return (TextView) findViewById(R.id.userName);
    }

    private TextView getUserIDTextView()
    {
        return (TextView) findViewById(R.id.userID);
    }

    private Account getUserAccountByID(long accountID)
    {
        Account result = new Account();
        result.firstName = "Иван";
        result.lastName = "Иванов";
        result.phoneNumber = "89513406702";
        result.address = new Address("106", "Ленина", 53, "Ижевск");
        return  result;
    }
}