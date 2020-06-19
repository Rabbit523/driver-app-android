package com.rapido.provider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class OtpVerifyForgetPassword extends AppCompatActivity implements View.OnClickListener {

    private ImageButton imgBack;
    private ImageButton nextIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify_forget_password);

        initViews();
    }

    private void initViews() {
        imgBack= findViewById(R.id.imgBack);
        nextIcon = findViewById(R.id.nextIcon);
        nextIcon.setOnClickListener(this);
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.nextIcon:

               // Intent intent = new Intent(this, OtpVerifyForgetPassword.class);
               // startActivity(intent);

                break;

            case R.id.imgBack:

                onBackPressed();
                finish();
                break;
        }
    }
}
