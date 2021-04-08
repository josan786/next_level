package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.github.willena.phoneinputview.CountryConfigurator;
import com.github.willena.phoneinputview.PhoneInputView;

import ru.konstantin_starikov.samsung.izhhelper.R;

public class LoginWithPhoneNumberActivity extends AppCompatActivity {

    public static final String PHONE_NUMBER = "phone_number";

    private static final String TAG = "PhoneAuthActivity";

    private PhoneInputView phoneInputView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone_number);

        phoneInputView = findViewById(R.id.phoneInputView);
        CountryConfigurator config = new CountryConfigurator();
        config.setDisplayFlag(true);
        config.setDisplayCountryCode(true);
        config.setDisplayDialingCode(true);
        config.setDefaultCountry("RU"); //Set the default country that will be selected when loading
        phoneInputView.setConfig(config);
    }

    public void verifyNumber(View view)
    {
        String phoneNumber = phoneInputView.getFormatedNumber();
        Intent intent = new Intent(getApplicationContext(), EnterSMSCodeActivity.class);
        intent.putExtra(PHONE_NUMBER, phoneNumber);
        startActivity(intent);
    }
}