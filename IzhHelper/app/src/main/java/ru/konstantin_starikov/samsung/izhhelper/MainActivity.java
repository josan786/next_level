package ru.konstantin_starikov.samsung.izhhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    public final static String ACCOUNT_ID = "accountID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void LogIntoAccount(View view)
    {
        String inputLogin = ((TextView) findViewById(R.id.loginText)).toString();
        String inputPassword = ((TextView) findViewById(R.id.passwordText)).toString();

        if(isInputDataRight(inputLogin, inputPassword)) {
            Intent loginIntent = new Intent(this, mainMenu.class);
            loginIntent.putExtra(ACCOUNT_ID, GetAccountID(inputLogin, inputPassword));
            startActivity(loginIntent);
        }
    }

    private boolean isInputDataRight(String login, String password)
    {
        return true;
    }

    private long GetAccountID(String login, String password)
    {
        return 1234567;
    }
}