package ru.konstantin_starikov.samsung.izhhepler;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class mainMenu extends AppCompatActivity {

    private Account userAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        long accountID = getIntent().getLongExtra(MainActivity.ACCOUNT_ID, 0);
        userAccount = GetUserAccountByID(accountID);
        GetUserNameTextView().setText(userAccount.firstName + " " + userAccount.lastName);
        GetUserIDTextView().setText("\u0040" + Long.toString(userAccount.ID));
    }

    private TextView GetUserNameTextView()
    {
        return (TextView) findViewById(R.id.userName);
    }

    private TextView GetUserIDTextView()
    {
        return (TextView) findViewById(R.id.userID);
    }

    private Account GetUserAccountByID(long accountID)
    {
        Account result = new Account();
        result.firstName = "Иван";
        result.lastName = "Иванов";
        result.phoneNumber = "89513406702";
        result.address = new Address("106", "Ленина", 53, "Ижевск");
        return  result;
    }
}