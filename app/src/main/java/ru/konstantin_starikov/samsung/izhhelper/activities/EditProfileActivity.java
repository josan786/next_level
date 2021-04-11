package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;

public class EditProfileActivity extends AppCompatActivity {

    private Account userAccount;

    private TextView displayText;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText phoneEditText;
    private EditText emailEditText;
    private EditText flatEditText;
    private EditText homeEditText;
    private EditText streetEditText;
    private EditText townEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        tuneActionBar();

        userAccount = (Account) getIntent().getSerializableExtra(AccountCreationActivity.USER_ACCOUNT);

        findAndSetViews();

        displayText.setText(userAccount.firstName + " " + userAccount.lastName);
        firstNameEditText.setText(userAccount.firstName);
        lastNameEditText.setText(userAccount.lastName);
        phoneEditText.setText(userAccount.phoneNumber);
        emailEditText.setText(userAccount.email);
        flatEditText.setText(Integer.toString(userAccount.address.flat));
        homeEditText.setText(userAccount.address.home);
        streetEditText.setText(userAccount.address.street);
        townEditText.setText(userAccount.address.town);
    }

    private void findAndSetViews()
    {
        displayText = findViewById(R.id.displayName);
        firstNameEditText = findViewById(R.id.editFirstName);
        lastNameEditText = findViewById(R.id.editLastName);
        phoneEditText = findViewById(R.id.editPhoneNumber);
        emailEditText = findViewById(R.id.editEmail);
        flatEditText = findViewById(R.id.editFlat);
        homeEditText = findViewById(R.id.editHome);
        streetEditText = findViewById(R.id.editStreet);
        townEditText = findViewById(R.id.editTown);
    }

    private void tuneActionBar()
    {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Редактировать профиль");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}