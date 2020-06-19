package com.rapido.provider.Transportation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.rapido.provider.R;
import com.mukesh.OnOtpCompletionListener;
import com.mukesh.OtpView;

import java.util.concurrent.TimeUnit;

public class OtpVerification extends AppCompatActivity implements OnOtpCompletionListener {
    private OtpView otpView;
    Button btnverify;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks
            verificationCallbacks;
    String phoneNumber;
    private FirebaseAuth fbAuth;
    ImageView backArrow;
    String code="";
    TextView tvResend;
    private String phoneVerificationId;
    private PhoneAuthProvider.ForceResendingToken resendToken;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verification);
        fbAuth = FirebaseAuth.getInstance();
        phoneNumber =getIntent().getStringExtra("phonenumber");
        btnverify = findViewById(R.id.btnverify);
        backArrow = findViewById(R.id.backArrow);
        otpView = findViewById(R.id.otp_view);
        tvResend=findViewById(R.id.tvResend);
        otpView.setOtpCompletionListener(this);

        setUpVerificatonCallbacks();
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tvResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setUpVerificatonCallbacks();

                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        OtpVerification.this,
                        verificationCallbacks,
                        resendToken);
            }
        });

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber+"",        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                OtpVerification.this,               // Activity (for callback binding)
                verificationCallbacks);
        btnverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   if (!otpView.getText().toString().equals(""))
                   {
                       try {
                           PhoneAuthCredential credential =
                                   PhoneAuthProvider.getCredential(phoneVerificationId,otpView.getText().toString() );
                           signInWithPhoneAuthCredential(credential);
                       }catch (Exception e){
                           e.printStackTrace();
                           Toast toast = Toast.makeText(OtpVerification.this, "Verification Code is wrong", Toast.LENGTH_SHORT);
                           toast.setGravity(Gravity.CENTER,0,0);
                           toast.show();
                       }

                   }else {
                       Toast.makeText(OtpVerification.this, "Enter the Correct varification Code", Toast.LENGTH_SHORT).show();
                   }

            }
        });
    }


    private void setUpVerificatonCallbacks() {
        verificationCallbacks =
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential credential) {
//                        code= credential.getSmsCode();
//                        Toast.makeText(OtpVerification.this, credential.getSmsCode()+" ", Toast.LENGTH_SHORT).show();

                        signInWithPhoneAuthCredential(credential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {


                        Log.d("responce",e.toString());
                        Toast.makeText(OtpVerification.this, e.getMessage()+" ", Toast.LENGTH_SHORT).show();
                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            e.printStackTrace();
                            // Invalid request
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            e.printStackTrace();
                            // SMS quota exceeded
                        }
                    }

                    @Override
                    public void onCodeSent(String verificationId, PhoneAuthProvider.ForceResendingToken token) {
                        phoneVerificationId = verificationId;
                        resendToken = token;
                        Toast.makeText(OtpVerification.this, "Send to ( "+phoneNumber+" )", Toast.LENGTH_SHORT).show();
                        startTimer(1);

                    }
                };
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        fbAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(OtpVerification.this,"Successfully verified",Toast.LENGTH_LONG).show();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
                            // get the user info to know that user is already login or not

                        } else {
                            if (task.getException() instanceof
                                    FirebaseAuthInvalidCredentialsException) {
                                Toast.makeText(OtpVerification.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                            }
                        }
                    }
                });
    }


    @Override
    public void onOtpCompleted(String otp) {
        btnverify.setEnabled(true);


//        Toast.makeText(this, "OnOtpCompletionListener called", Toast.LENGTH_SHORT).show();
    }

    private void startTimer(int noOfMinutes) {

        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResend.setEnabled(false);
                tvResend.setText("Resend In "+ millisUntilFinished / 1000);
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                tvResend.setEnabled(true);
                tvResend.setText("Resend");
            }

        }.start();

    }
}
