package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;

public class AccountCreationActivity extends AppCompatActivity {

    public final static String USER_ACCOUNT = "user_account";
    private Account userAccount;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText userEmailEditText;
    private EditText userFlatEditText;
    private EditText userHomeEditText;
    private EditText userStreetEditText;
    private EditText userTownEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        userAccount = (Account) getIntent().getSerializableExtra(EnterSMSCodeActivity.USER_ACCOUNT);

        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        userEmailEditText = findViewById(R.id.userEmail);
        userFlatEditText = findViewById(R.id.userFlat);
        userHomeEditText = findViewById(R.id.userHome);
        userStreetEditText = findViewById(R.id.userStreet);
        userTownEditText = findViewById(R.id.userTown);
    }

    public void createAccount(View view)
    {
        userAccount.firstName = firstNameEditText.getText().toString();
        userAccount.lastName = lastNameEditText.getText().toString();
        userAccount.email = userEmailEditText.getText().toString();
        userAccount.address.flat = Integer.parseInt(userFlatEditText.getText().toString());
        userAccount.address.street = userStreetEditText.getText().toString();
        userAccount.address.home = userHomeEditText.getText().toString();
        userAccount.address.town = userTownEditText.getText().toString();
        userAccount.updateUserDataOnFirebase();
        userAccount.saveAccount(this);
        Intent intent = new Intent(this, MainMenuActivity.class);
        startActivity(intent);
    }
}