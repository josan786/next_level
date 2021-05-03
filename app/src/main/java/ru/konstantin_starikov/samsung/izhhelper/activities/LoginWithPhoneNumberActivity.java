package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.willena.phoneinputview.CountryConfigurator;
import com.github.willena.phoneinputview.PhoneInputView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.interfaces.Action;

public class LoginWithPhoneNumberActivity extends AppCompatActivity {

    public static final String PHONE_NUMBER = "phone_number";
    public static final String VERIFICATION_ID = "verification_ID";
    public static final String RESEND_TOKEN = "resend_token";

    private static final String TAG = "PhoneAuthActivity";

    private PhoneInputView phoneInputView;

    private String verificationId;
    private FirebaseAuth firebaseAuth;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_phone_number);

        findAndSetViews();
        configPhoneInputView();

        firebaseAuth = FirebaseAuth.getInstance();
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d(TAG, "onVerificationCompleted:" + credential);
                String code = credential.getSmsCode();
                if (code != null) {
                    PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, code);
                    signInWithPhoneAuthCredential(phoneAuthCredential);
                }
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w(TAG, "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

                // Show a message and update the UI
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d(TAG, "onCodeSent:" + verificationId);

                super.onCodeSent(verificationId, token);

                LoginWithPhoneNumberActivity.this.verificationId = verificationId;

                // Save verification ID and resending token so we can use them later
                Intent intent = new Intent(getApplicationContext(), EnterSMSCodeActivity.class);
                intent.putExtra(PHONE_NUMBER, phoneInputView.getFormatedNumber());
                intent.putExtra(VERIFICATION_ID, verificationId);
                intent.putExtra(RESEND_TOKEN, token);
                startActivity(intent);
            }
        };
    }

    private void findAndSetViews()
    {
        phoneInputView = findViewById(R.id.phoneInputView);
    }

    private void configPhoneInputView()
    {
        CountryConfigurator config = new CountryConfigurator();
        config.setDisplayFlag(false);
        config.setDisplayCountryCode(false);
        config.setDisplayDialingCode(true);
        config.setDefaultCountry("RU"); //Set the default country that will be selected when loading
        phoneInputView.setConfig(config);
    }

    public void verifyNumber(View view)
    {
        String phoneNumber = phoneInputView.getFormatedNumber();
        Log.i("Trim", trimPhoneNumber(phoneNumber));
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                trimPhoneNumber(phoneNumber),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this, // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
    }

    private String trimPhoneNumber(String phone)
    {
        String result = "";
        for(int i = 0 ; i < phone.length(); i++)
        {
            if(phone.charAt(i) > 47 && phone.charAt(i) < 58 || phone.charAt(i) == 43)
            {
                result += phone.charAt(i);
            }
        }
        return result;
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Account userAccount = new Account(user);
                            userAccount.retrieveUserDataFromFirebase(new Action() {
                                @Override
                                public void run() {
                                    if (userAccount.isUserHasDataInDatabase(LoginWithPhoneNumberActivity.this)) {
                                        userAccount.updateUserData(LoginWithPhoneNumberActivity.this);
                                    }
                                    else userAccount.saveAccount(LoginWithPhoneNumberActivity.this);
                                    goToMainMenu();
                                }
                            }, new Action() {
                                @Override
                                public void run() {
                                    goToAccountCreation();
                                }
                            });
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void goToMainMenu()
    {
        Intent intent = new Intent(getApplicationContext(), MainMenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void goToAccountCreation()
    {
        Intent intent = new Intent(getApplicationContext(), AccountCreationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}