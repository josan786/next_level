package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.UsersDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReportsListAdapter;

public class MainMenuActivity extends AppCompatActivity {

    private Account userAccount = null;

    public final static String USER_ACCOUNT = "user_account";
    public final static String VIOLATION_REPORT = "violation_report";

    private ImageView avatarImageView;

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

        if(userAccount.getAvatarPath() != null)
            setUserAvatar();

        fillViolationReports();
        getUserNameTextView().setText(userAccount.firstName + " " + userAccount.lastName);
        getUserIDTextView().setText(userAccount.ID);
    }

    private void findAndSetViews()
    {
        violationReportsList = findViewById(R.id.violationReports);
        avatarImageView = findViewById(R.id.avatar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id){
            case R.id.signOut : {
                FirebaseAuth firebaseAuth;
                FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        if (firebaseAuth.getCurrentUser() == null)
                            signOutComplete();
                        else {
                        }
                    }
                };
                firebaseAuth = FirebaseAuth.getInstance();
                firebaseAuth.addAuthStateListener(authStateListener);
                firebaseAuth.signOut();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOutComplete()
    {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void fillViolationReports()
    {
        ViolationReportsListAdapter violationReportsListAdapter = new ViolationReportsListAdapter(this, R.layout.violation_report_item, userAccount.getViolationReports());
        violationReportsList.setAdapter(violationReportsListAdapter);
    }

    private void loadUserData()
    {
        UsersDatabase usersDatabase = new UsersDatabase(this);
        Log.i("userAccount.ID", userAccount.ID);
        Account userData = usersDatabase.select(userAccount.ID);
        if (userData != null)
            userAccount = userData;
    }

    private void setUserAvatar()
    {
        Log.i("AVATAR_PATH", Helper.getFullPathFromDataDirectory(userAccount.getAvatarPath(), this));
        avatarImageView.setImageDrawable(Drawable.createFromPath((Helper.getFullPathFromDataDirectory(userAccount.getAvatarPath(), this))));
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
        editProfileIntent.putExtra(USER_ACCOUNT, userAccount);
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