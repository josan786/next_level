package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.interfaces.Action;

public class SplashScreenActivity extends AppCompatActivity {

    private CountDownTimer timer;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if(currentUser != null) {
            Account userAccount = new Account(currentUser);
            userAccount.retrieveUserDataFromFirebase(new Action() {
                @Override
                public void run() {
                    if (userAccount.isUserHasDataInDatabase(SplashScreenActivity.this))
                        userAccount.updateUserData(SplashScreenActivity.this);
                    else userAccount.saveAccount(SplashScreenActivity.this);
                    Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, new Action() {
                @Override
                public void run() {
                    Intent intent = new Intent(getApplicationContext(), AccountCreationActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        } else {
            Intent intent = new Intent(getApplicationContext(), LoginWithPhoneNumberActivity.class);
            startActivity(intent);
            finish();
        }
    }
}