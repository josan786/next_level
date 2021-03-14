package ru.konstantin_starikov.samsung.izhhelper;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class RegistrationActivity extends AppCompatActivity {

    public static final String REGISTERED_LOGIN_KEY = "registered_login_key";
    public static final String REGISTERED_PASSWORD_KEY = "registered_password_key";

    private EditText loginText;
    private EditText passwordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        loginText = (EditText) findViewById(R.id.registrationLoginText);
        passwordText = (EditText) findViewById(R.id.registrationPasswordText);
    }

    public void Register(View view) {
        AddNewAccount(loginText.getText().toString(), passwordText.getText().toString());
    }

    private void AddNewAccount(String login, String password)
    {
        Intent i = new Intent();
        i.putExtra(REGISTERED_LOGIN_KEY, loginText.getText().toString());
        i.putExtra(REGISTERED_PASSWORD_KEY, loginText.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }
}