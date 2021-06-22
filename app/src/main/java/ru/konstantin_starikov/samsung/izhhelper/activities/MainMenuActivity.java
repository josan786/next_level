package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationMenu;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.fragments.AccountFragment;
import ru.konstantin_starikov.samsung.izhhelper.fragments.AchievementsFragment;
import ru.konstantin_starikov.samsung.izhhelper.fragments.LeaderboardFragment;
import ru.konstantin_starikov.samsung.izhhelper.fragments.MapFragment;
import ru.konstantin_starikov.samsung.izhhelper.fragments.SettingsFragment;
import ru.konstantin_starikov.samsung.izhhelper.fragments.UsersViolationsFragment;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Cleaner;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;
import ru.konstantin_starikov.samsung.izhhelper.models.Settings;
import ru.konstantin_starikov.samsung.izhhelper.models.databases.SettingsDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.databases.UsersDatabase;
import ru.konstantin_starikov.samsung.izhhelper.models.ViolationReport;
import ru.konstantin_starikov.samsung.izhhelper.models.adapters.ViolationReportsListAdapter;
import ru.konstantin_starikov.samsung.izhhelper.models.databases.ViolationsDatabase;

public class MainMenuActivity extends AppCompatActivity {

    public final static String TAG = "MainMenuActivity";

    public final static String USER_ACCOUNT = "user_account";
    public final static String VIOLATION_REPORT = "violation_report";

    private Account userAccount = null;

    private BottomNavigationView bottomNavigationView;
    private ImageView avatarImageView;

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Cleaner.deleteUnusedFiles(this);

        userAccount = new Account();
        loadUserData();

        userAccount.loadViolations(this);

        findAndSetViews();

        fragmentManager = getSupportFragmentManager();

        userAccount.loadViolationsFromFirebase(this);

        addAccountFragment();

        tuneActionBar();

        if(Helper.isOnline(this)){
            userAccount.sendNotSentViolations(this);
        }
        else{
            ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            connectivityManager.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
                @Override
                public void onNetworkActive() {
                    userAccount.sendNotSentViolations(getApplicationContext());
                }
            });
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.page_account:
                {
                    openAccountFragment();
                    return true;
                }
                case R.id.page_violations_reports:
                {
                    openViolationsFragment(null);
                    return true;
                }
                case R.id.page_map:
                {
                    openMapFragment(null);
                    return true;
                }
                default:
                {
                    return false;
                }
            }
        });
    }

    private void findAndSetViews()
    {
        avatarImageView = findViewById(R.id.avatar);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.Account));
        actionBar.setHomeButtonEnabled(false);
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if(!fragments.isEmpty()) return fragments.get(0).onOptionsItemSelected(item);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        List<Fragment> fragments = fragmentManager.getFragments();
        if (!fragments.isEmpty()) {
            fragments.get(0).onCreateOptionsMenu(menu, null);
            return true;
        }
        return false;
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

    private void loadUserData()
    {
        UsersDatabase usersDatabase = new UsersDatabase(this);
        Log.i(TAG, "userAccount.ID: " + userAccount.ID);
        Account userData = usersDatabase.select(userAccount.ID);
        if (userData != null)
            userAccount = userData;
    }

    private void addAccountFragment()
    {
        AccountFragment accountFragment = AccountFragment.newInstance(userAccount);
        fragmentManager
                .beginTransaction()
                .add(R.id.usersFragmentsLayout, accountFragment)
                .commit();
    }

    public void openAccountFragment()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.Account));
        AccountFragment accountFragment = AccountFragment.newInstance(userAccount);
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, accountFragment)
                .commit();
    }

    public void openViolationsFragment(View view)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.ViolationsReports));
        UsersViolationsFragment usersViolationsFragment = UsersViolationsFragment.newInstance(userAccount);
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, usersViolationsFragment)
                .commit();
    }

    public void openAchievementsFragment(View view)
    {
        AchievementsFragment achievementsFragment = AchievementsFragment.newInstance();
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, achievementsFragment)
                .commit();
    }

    public void openSettingsFragment(View view)
    {
        SettingsFragment settingsFragment = new SettingsFragment();
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, settingsFragment)
                .commit();
    }

    public void openLeaderboardFragment(View view)
    {
        LeaderboardFragment leaderboardFragment = new LeaderboardFragment();
        fragmentManager
                .beginTransaction()
                .replace(R.id.usersFragmentsLayout, leaderboardFragment)
                .commit();
    }


    public void openMapFragment(View view)
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.Map));
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
        Intent editProfileIntent = new Intent(this, EditProfileActivity.class);
        editProfileIntent.putExtra(USER_ACCOUNT, userAccount);
        startActivity(editProfileIntent);
    }

}