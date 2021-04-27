package ru.konstantin_starikov.samsung.izhhelper.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;

public class SetUserPasswordActivity extends AppCompatActivity {

    EditText editPasswordEditText;
    EditText repeatPasswordEditText;

    private Account userAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_password);

        userAccount = new Account(FirebaseAuth.getInstance().getCurrentUser());

        editPasswordEditText = findViewById(R.id.enterPasswordEditText);
        repeatPasswordEditText = findViewById(R.id.repeatPassword);
    }

    public void createPassword(View v)
    {
        if(editPasswordEditText.getText().toString().equals(repeatPasswordEditText.getText().toString()))
        {
            userAccount.setUserPassword(editPasswordEditText.getText().toString());
        }
    }
}