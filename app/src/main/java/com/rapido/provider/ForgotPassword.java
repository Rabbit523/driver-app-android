package com.rapido.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hbb20.CountryCodePicker;
import com.rapido.provider.Transportation.Activities.OtpVerification;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;



public class ForgotPassword extends AppCompatActivity {
    Dialog dialog;
    String TAG = "ForgotPassword";
    public Context context = ForgotPassword.this;
    ImageView nextICON, backArrow;
    TextView titleText;
    TextInputLayout newPasswordLayout, confirmPasswordLayout, OtpLay;
    LinearLayout ll_resend;
    EditText newPassowrd, confirmPassword, OTP;
    EditText email;
    EditText mobile_no;
    CustomDialog customDialog;
    String validation = "",
            str_newPassword,
            str_confirmPassword,
            id, str_email = "",
            str_otp,
            server_opt,
            getemail,
            getmobile;
    ConnectionHelper helper;
    Boolean isInternet;
    TextView note_txt;
    boolean fromActivity = false;
    Button resend;
    String phoneNumberString;
    public static int APP_REQUEST_CODE = 99;
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
    UIManager uiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        try {
            Intent intent = getIntent();
            if (intent != null) {
                if (getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = true;
                } else if (!getIntent().getExtras().getBoolean("isFromMailActivity")) {
                    fromActivity = false;
                } else {
                    fromActivity = false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fromActivity = false;
        }
        findViewById();

        if (Build.VERSION.SDK_INT > 15) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }


        nextICON.setOnClickListener(view -> {
            str_email = email.getText().toString();
            if (validation.equalsIgnoreCase("")) {
                if (email.getText().toString().equals("")) {
                    displayMessage(getString(R.string.email_validation));
                } else if (!Utilities.isValidEmail(email.getText().toString())) {
                    displayMessage(getString(R.string.not_valid_email));
                } else {
                    if (isInternet) {
                        ForgotPassword();
                        //phoneLogin();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
            } else {
                str_newPassword = newPassowrd.getText().toString();
                str_confirmPassword = confirmPassword.getText().toString();
                str_otp = OTP.getText().toString();
                if (str_newPassword.equals("") ||
                        str_newPassword.equalsIgnoreCase(getString(R.string.new_password))) {
                    displayMessage(getString(R.string.password_validation));
                } else if (newPassowrd.getText().toString().length() < 6) {
                    displayMessage(getString(R.string.new_validation));
                } else if (confirmPassword.getText().toString().length() < 6) {
                    displayMessage(getString(R.string.confirm_validation));
                } else if (str_confirmPassword.equals("") ||
                        str_confirmPassword.equalsIgnoreCase(getString(R.string.confirm_password))
                        || !str_newPassword.equalsIgnoreCase(str_confirmPassword)) {
                    displayMessage(getString(R.string.confirm_password_validation));
                } else {
                    if (isInternet) {
                        resetpassword();
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
            }
        });

        backArrow.setOnClickListener(view -> {
            Intent mainIntent = new Intent(ForgotPassword.this,
                    Login.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            ForgotPassword.this.finish();
        });

    }


    private void resetpassword() {
        customDialog = new CustomDialog(ForgotPassword.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("password", str_newPassword);
            object.put("password_confirmation", str_confirmPassword);
            Log.e("ResetPassword", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.RESET_PASSWORD,
                        object,
                        response -> {
                            customDialog.dismiss();
                            Log.e("ResetPasswordResponse", response.toString());
                            try {
                                JSONObject object1 = new JSONObject(response.toString());
                                Toast.makeText(context, object1.optString("message"),
                                        Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassword.this,
                                        Login.class)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    Log.e("MyTest", "" + error);
                    Log.e("MyTestError", "" + error.networkResponse);
                    Log.e("MyTestError1", "" + response.statusCode);
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));
                            if (response.statusCode == 400 || response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message")
                                            .equalsIgnoreCase("invalid_token")) {
                                        //Call Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }
                            } else if (response.statusCode == 422) {
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage("Please try again.");
                                }
                            } else {
                                displayMessage("Please try again.");
                            }
                        } catch (Exception e) {
                            displayMessage("Something went wrong.");
                        }
                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            resetpassword();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        return headers;
                    }
                };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

    }
    private void openphonelogin() {

        dialog = new Dialog(ForgotPassword.this,R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(true);
        dialog.show();
        CountryCodePicker ccp= dialog.findViewById(R.id.ccp);
        ImageButton nextIcon= dialog.findViewById(R.id.nextIcon);
        EditText mobile_no= dialog.findViewById(R.id.mobile_no);
        final String countryCode=ccp.getDefaultCountryCode();
        final String countryIso=ccp.getSelectedCountryNameCode();
        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                phoneNumberString = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();
                SharedHelper.putKey(getApplicationContext(), "mobile", phoneNumberString);
                Log.v("Phonecode",phoneNumberString+" ");
                Intent intent =new Intent(ForgotPassword.this, OtpVerification.class);
                intent.putExtra("phonenumber",phoneNumberString);
                startActivityForResult(intent,APP_REQUEST_CODE);
                dialog.dismiss();

//                String phone=ccp.getDefaultCountryCode()+mobile_no.getText().toString();
//                PhoneNumber phoneNumber = new PhoneNumber(ccp.getSelectedCountryCode(),mobile_no.getText().toString(),ccp.getSelectedCountryNameCode());
//                phoneLogin(phoneNumber);
            }
        });

    }
    public void phoneLogin( PhoneNumber phoneNumber) {
        Log.e(TAG, "onActivityResult: phone Login Account Kit" + AccountKit.getCurrentAccessToken() + "");
        final Intent intent = new Intent(this, AccountKitActivity.class);
        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
                ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimary),
                R.color.white,
                SkinManager.Tint.WHITE,
                0.001);
        AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder =
                new AccountKitConfiguration.AccountKitConfigurationBuilder(
                        LoginType.PHONE,
                        AccountKitActivity.ResponseType.TOKEN); // or .ResponseType.TOKEN
        configurationBuilder.setUIManager(uiManager);
        intent.putExtra(
                AccountKitActivity.ACCOUNT_KIT_ACTIVITY_CONFIGURATION,
                configurationBuilder
                        .setInitialPhoneNumber(phoneNumber)
                        .build());

        startActivityForResult(intent, APP_REQUEST_CODE);

    }


    @Override
    protected void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
            if (data != null) {

                if (getmobile != null) {
                    Log.e("getmobile",getmobile+"");
                    Log.e("phoneNumberString",phoneNumberString+"");
                    if (getmobile.equals(phoneNumberString)) {
                        email.setFocusable(false);
                        email.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
                        email.setClickable(false);

                        validation = "reset";
                        titleText.setText(R.string.reset_password);
                        newPasswordLayout.setVisibility(View.VISIBLE);
                        confirmPasswordLayout.setVisibility(View.VISIBLE);
                        OtpLay.setVisibility(View.GONE);
                        note_txt.setVisibility(View.GONE);
                        //OTP.performClick();
                        ll_resend.setVisibility(View.GONE);
                    } else {
                        displayMessage("Mobile no is not match with register emailid");
                    }
                }
                SharedHelper.putKey(ForgotPassword.this, "mobile", phoneNumberString);
            }
        }
    }

    private void ForgotPassword() {
        customDialog = new CustomDialog(ForgotPassword.this);
        customDialog.setCancelable(false);
        customDialog.show();

        StringRequest jsonObjectRequest = new
                StringRequest(Request.Method.POST,
                        URLHelper.FORGET_PASSWORD,
                        response -> {
                            customDialog.dismiss();
                            Log.e("ForgotPasswordResponse", response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                JSONObject userObject = obj.getJSONObject("provider");
                                if (userObject.getString("mobile") != null) {
                                    id = userObject.getString("id");
                                    getemail = userObject.getString("email");
                                    getmobile = userObject.getString("mobile");
                                    openphonelogin();
                                } else {
                                    displayMessage("Mobile no is not exist with this email_id");
                                }
                            } catch (JSONException e) {
                                displayMessage("Mobile no is not exist with this email_id");
                                e.printStackTrace();
                            }
                        }, error -> {
                    customDialog.dismiss();
                    Log.e("error",error+"");
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        Log.e("MyTest", "" + error);
                        Log.e("MyTestError", "" + error.networkResponse);
                        Log.e("MyTestError1", "" + response.statusCode);
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 ||
                                    response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message")
                                            .equalsIgnoreCase("invalid_token")) {
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage("Something went wrong.");
                                }
                            } else if (response.statusCode == 422) {
                                Snackbar.make(getCurrentFocus(), "Your mail ID is invalid.",
                                        Snackbar.LENGTH_SHORT)
                                        .setAction("Action", null).show();
                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    //displayMessage(json);
                                } else {
                                    displayMessage("Please try again.");
                                }
                            } else {
                                displayMessage("Please try again.");
                            }
                        } catch (Exception e) {
                            displayMessage("Something went wrong.");
                        }
                    } else {
                        if (error instanceof NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet));
                        } else if (error instanceof TimeoutError) {
                            //ForgotPassword();
                        }
                    }
                }) {
                    @Override
                    public Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        Log.e(TAG, "headers: " + headers.toString());
                        return headers;

                    }

                    @Override
                    public Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<>();
                        params.put("email", str_email);
                        Log.e(TAG, "params: " + params.toString());
                        return params;
                    }
                };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void findViewById() {
        mobile_no = findViewById(R.id.mobile_no);
        email = findViewById(R.id.email);
        nextICON = findViewById(R.id.nextIcon);
        backArrow = findViewById(R.id.backArrow);
        titleText = findViewById(R.id.title_txt);
        note_txt = findViewById(R.id.note);
        newPassowrd = findViewById(R.id.new_password);
        OTP = findViewById(R.id.otp);
        confirmPassword = findViewById(R.id.confirm_password);
        confirmPasswordLayout = findViewById(R.id.confirm_password_lay);
        OtpLay = findViewById(R.id.otp_lay);
        newPasswordLayout = findViewById(R.id.new_password_lay);
        resend = findViewById(R.id.resend);
        ll_resend = findViewById(R.id.ll_resend);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        str_email = SharedHelper.getKey(ForgotPassword.this, "email");
        email.setText(str_email);

    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.title_txt), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if (fromActivity) {
            Intent mainIntent = new Intent(ForgotPassword.this, Login.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            ForgotPassword.this.finish();
        } else {
            Intent mainIntent = new Intent(ForgotPassword.this, Login.class);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mainIntent);
            ForgotPassword.this.finish();
        }
    }
}
