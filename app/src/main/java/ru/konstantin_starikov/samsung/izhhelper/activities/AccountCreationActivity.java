package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.carbs.android.avatarimageview.library.AvatarImageView;
import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.Helper;

public class AccountCreationActivity extends AppCompatActivity {

    private final int REQUEST_FILE = 42;

    private Account userAccount;

    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private EditText userEmailEditText;
    private EditText userFlatEditText;
    private EditText userHomeEditText;
    private EditText userStreetEditText;
    private EditText userTownEditText;

    private TextView warningText;

    private AvatarImageView userAvatarImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_creation);

        userAccount = new Account(FirebaseAuth.getInstance().getCurrentUser());

        findAndSetViews();
    }

    private void findAndSetViews() {
        firstNameEditText = findViewById(R.id.firstName);
        lastNameEditText = findViewById(R.id.lastName);
        userEmailEditText = findViewById(R.id.enterPasswordEditText);
        userFlatEditText = findViewById(R.id.userFlat);
        userHomeEditText = findViewById(R.id.userHome);
        userStreetEditText = findViewById(R.id.userStreet);
        userTownEditText = findViewById(R.id.userTown);
        warningText = findViewById(R.id.warningAccountCreationTextView);
        userAvatarImageView = findViewById(R.id.user_avatar);
    }

    public void createAccount(View view)
    {
        if(isAllFieldsFilled()) {
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
        else showWarningText();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE && resultCode == Activity.RESULT_OK) {
            try {
                Bitmap avatar = MediaStore.Images.Media.getBitmap(getContentResolver(), data.getData());
                userAvatarImageView.setBitmap(avatar);
                String avatarPath = Helper.saveAvatarFromBitmap(avatar, userAccount.ID, this);
                userAccount.setAvatarPath(avatarPath.substring(avatarPath.lastIndexOf('/') + 1));
            }
            catch (IOException exception)
            {
                exception.printStackTrace();
            }
        }
    }

    public void changeAvatar(View v) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_FILE);
    }

    private void showWarningText()
    {
        warningText.setVisibility(View.VISIBLE);
    }

    private boolean isAllFieldsFilled()
    {
        boolean result = true;

        String firstName = firstNameEditText.getText().toString();
        String lastName = lastNameEditText.getText().toString();
        String email = userEmailEditText.getText().toString();
        String flat = userFlatEditText.getText().toString();
        String home = userHomeEditText.getText().toString();
        String street = userStreetEditText.getText().toString();
        String town = userTownEditText.getText().toString();

        if(firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || flat.isEmpty() ||
                home.isEmpty() || street.isEmpty() || town.isEmpty()) result = false;

        return result;
    }
}