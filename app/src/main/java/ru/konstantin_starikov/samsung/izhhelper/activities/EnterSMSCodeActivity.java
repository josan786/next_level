package ru.konstantin_starikov.samsung.izhhelper.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gongw.VerifyCodeView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import ru.konstantin_starikov.samsung.izhhelper.R;
import ru.konstantin_starikov.samsung.izhhelper.models.Account;
import ru.konstantin_starikov.samsung.izhhelper.models.interfaces.Action;

public class EnterSMSCodeActivity extends AppCompatActivity {

    private static final String TAG = "EnterSMSCodeActivity";
    public final static String USER_ACCOUNT = "user_account";

    private VerifyCodeView verifyCodeView;
    private TextView incorrectSMSCodeTextView;

    private String phoneNumber;
    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_sms_code);

        findAndSetViews();

        phoneNumber = getIntent().getStringExtra(LoginWithPhoneNumberActivity.PHONE_NUMBER);
        verificationId = getIntent().getStringExtra(LoginWithPhoneNumberActivity.VERIFICATION_ID);
        resendToken = getIntent().getParcelableExtra(LoginWithPhoneNumberActivity.RESEND_TOKEN);

        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
    }

    private void findAndSetViews()
    {
        verifyCodeView = findViewById(R.id.verifyCodeView);
        incorrectSMSCodeTextView = findViewById(R.id.incorrectSMSCodeTextView);
    }

    public void verifySentCode(View view)
    {
        String usersCode = verifyCodeView.getVcText();
        if (usersCode != null) {
            PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(verificationId, usersCode);
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            Account userAccount = new Account(user);
                            userAccount.retrieveUserDataFromFirebase(new Action() {
                                @Override
                                public void run() {
                                    if (userAccount.isUserHasDataInDatabase(EnterSMSCodeActivity.this)) {
                                        userAccount.updateUserData(EnterSMSCodeActivity.this);
                                    } else userAccount.saveAccount(EnterSMSCodeActivity.this);
                                    goToMainMenu();
                                }
                            }, new Action() {
                                @Override
                                public void run() {
                                    userAccount.setUserPhone(phoneNumber);
                                    goToAccountCreationActivity();
                                }
                            });
                        } else {
                            incorrectSMSCodeTextView.setVisibility(View.VISIBLE);
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

    private void goToAccountCreationActivity()
    {
        Intent intent = new Intent(getApplicationContext(), AccountCreationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}