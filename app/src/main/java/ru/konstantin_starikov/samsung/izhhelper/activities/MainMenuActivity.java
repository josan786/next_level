package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.content.res.Configuration;
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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.Locale;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.fragments.AchievementsFragment;
import ru.konstantin_starikov.samsung.izhhelper.fragments.MapFragment;
import ru.konstantin_starikov.samsung.izhhelper.fragments.UsersViolationsFragment;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.databases.UsersDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.adapters.ViolationReportsListAdapter;

public class MainMenuActivity extends AppCompatActivity {

    public final static String TAG = "MainMenuActivity";

    public final static String USER_ACCOUNT = "user_account";
    public final static String VIOLATION_REPORT = "violation_report";

    public final static String UDMURT_LANGUAGE = "udm";
    public final static String RUSSIAN_LANGUAGE = "ru";

    private Account userAccount = null;

    private ImageView avatarImageView;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        userAccount = new Account();
        loadUserData();
        userAccount.loadViolations(this);

        findAndSetViews();

        fragmentManager = getSupportFragmentManager();

        addViolationsFragment();

        if(userAccount.getAvatarPath() != null)
            setUserAvatar();

        getUserNameTextView().setText(userAccount.firstName + " " + userAccount.lastName);
        getUserIDTextView().setText(userAccount.ID);
    }

    private void findAndSetViews()
    {
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
            case R.id.signOut: {
                signOut();
                return true;
            }
            case R.id.selectUdmurt: {
                selectLanguage(UDMURT_LANGUAGE);
                reloadApplication();
                return true;
            }
            case R.id.selectRussian: {
                selectLanguage(RUSSIAN_LANGUAGE);
                reloadApplication();
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void signOut()
    {
        FirebaseAuth firebaseAuth;
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null)
                    reloadApplication();
            }
        };
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.addAuthStateListener(authStateListener);
        firebaseAuth.signOut();
    }

    private void reloadApplication()
    {
        Intent intent = new Intent(this, SplashScreenActivity.class);
        startActivity(intent);
        finish();
    }

    private void selectLanguage(String language)
    {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        getBaseContext().getResources().updateConfiguration(configuration, null);
    }

    private void loadUserData()
    {
        UsersDatabase usersDatabase = new UsersDatabase(this);
        Log.i(TAG, "userAccount.ID: " + userAccount.ID);
        Account userData = usersDatabase.select(userAccount.ID);
        if (userData != null)
            userAccount = userData;
    }

    private void addViolationsFragment()
    {
        UsersViolationsFragment usersViolationsFragment = UsersViolationsFragment.newInstance(userAccount.getViolationReports());
        fragmentManager
                .beginTransaction()
                .add(R.id.usersFragmentsLayout, usersViolationsFragment)
                .commit();
    }

    private void setUserAvatar()
    {
        String avatarPath = Helper.getFullPathFromDataDirectory(userAccount.getAvatarPath(), this);
        Log.i(TAG, "Avatar path: " + avatarPath);
        avatarImageView.setImageDrawable(Drawable.createFromPath(avatarPath));
    }

    public void openViolationsFragment(View view)
    {
        UsersViolationsFragment usersViolationsFragment = UsersViolationsFragment.newInstance(userAccount.getViolationReports());
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, usersViolationsFragment)
                .commit();
    }

    public void openAchievementsFragment(View view)
    {
        AchievementsFragment achievementsFragment = new AchievementsFragment();
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, achievementsFragment)
                .commit();
    }

    public void openMapFragment(View view)
    {
        MapFragment mapFragment = new MapFragment();
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, mapFragment)
                .commit();
    }

    public void createViolationReport(View v)
    {
        ViolationReport violationReport = new ViolationReport();
        violationReport.senderAccount = userAccount;
        Log.i(TAG, "Report ID: " + violationReport.getID());

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