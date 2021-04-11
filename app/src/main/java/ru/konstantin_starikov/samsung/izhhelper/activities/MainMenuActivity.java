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
import ru.konstantin_starikov.samsung.izhhelper.models.UsersDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReportsListAdapter;

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

        userAccount = new Account();
        loadUserData();
        userAccount.loadViolations(this);

        findAndSetViews();
        fillViolationReports();
        getUserNameTextView().setText(userAccount.firstName + " " + userAccount.lastName);
        getUserIDTextView().setText(userAccount.ID);
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

    private void loadUserData()
    {
        UsersDatabase usersDatabase = new UsersDatabase(this);
        Account userData = usersDatabase.select(userAccount.ID);
        if(userData != null) userAccount = userData;
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

    public void editProfile(View view)
    {
        Intent editProfileIntent = new Intent(MainMenuActivity.this, EditProfileActivity.class);
        editProfileIntent.putExtra(AccountCreationActivity.USER_ACCOUNT, userAccount);
        startActivity(editProfileIntent);
    }

    private TextView getUserNameTextView()
    {
        return (TextView) findViewById(R.id.userName);
    }

    private TextView getUserIDTextView()
    {
        return (TextView) findViewById(R.id.userID);
    }
}