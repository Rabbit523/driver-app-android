package com.rapido.provider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rapido.provider.Transportation.Activities.MainActivity;
import com.rapido.provider.Transportation.Activities.OtpVerification;
import com.rapido.provider.Transportation.Activities.SplashScreen;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.google.android.material.snackbar.Snackbar;
import com.hbb20.CountryCodePicker;
import com.jaredrummler.materialspinner.MaterialSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;

public class SignUp extends AppCompatActivity implements View.OnClickListener {
    String TAG = "SignUp";
    TextView txtSignIn;
    EditText etName,etEmail,etPassword;
    Button btnSignUp;
    private MaterialSpinner spRegister;

    CustomDialog customDialog;
    ConnectionHelper helper;
    Boolean isInternet;
    Utilities utils = new Utilities();
    String device_token, device_UDID;
    public static int APP_REQUEST_CODE = 99;
    AccountKitConfiguration.AccountKitConfigurationBuilder configurationBuilder;
    UIManager uiManager;
//    Button btnFb,btnGoogle;
    Dialog dialog;
    Login login=new Login();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        txtSignIn= findViewById(R.id.txtSignIn);
        etName= findViewById(R.id.etName);
        etEmail= findViewById(R.id.etEmail);
        etPassword= findViewById(R.id.etPassword);
        btnSignUp= findViewById(R.id.btnSignUp);
//        btnFb=(Button)findViewById(R.id.btnFb);
//        btnGoogle=(Button)findViewById(R.id.btnGoogle);
        txtSignIn.setOnClickListener(this);
        btnSignUp.setOnClickListener(this);
//        btnGoogle.setOnClickListener(this);
//        btnFb.setOnClickListener(this);

        getToken();
    }

    @Override
    public void onClick(View v) {
//        if (v.getId()==R.id.btnFb)
//        {
//            logInType("fb");
//        }
//        if (v.getId()==R.id.btnGoogle)
//        {
//            logInType("google");
//        }
        if (v.getId()==R.id.txtSignIn)
        {
            startActivity(new Intent(SignUp.this,Login.class));
            finish();
        }
        if (v.getId()==R.id.btnSignUp)
        {

                Pattern ps = Pattern.compile(".*[0-9].*");
                Matcher firstName = ps.matcher(etName.getText().toString());


                if (etName.getText().toString().equals("") ||
                        etName.getText().toString().equalsIgnoreCase(getString(R.string.first_name))) {
                    displayMessage(getString(R.string.first_name_empty));
                } else if (firstName.matches()) {
                    displayMessage(getString(R.string.first_name_no_number));
                } else if (etEmail.getText().toString().equals("") ||
                        etEmail.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                    displayMessage(getString(R.string.email_validation));
                } else if (!Utilities.isValidEmail(etEmail.getText().toString())) {
                    displayMessage(getString(R.string.not_valid_email));
                } else if (etPassword.getText().toString().equals("") ||
                        etPassword.getText().toString().equalsIgnoreCase(getString(R.string.password_txt))) {
                    displayMessage(getString(R.string.password_validation));
                } else if (etPassword.length() < 6) {
                    displayMessage(getString(R.string.password_size));
                }
                else {
                    if (isInternet) {


                        checkEmail();

                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net));
                    }
                }
        }
    }

    private void checkEmail() {
        customDialog = new CustomDialog(SignUp.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("email", etEmail.getText().toString());
            utils.print("InputToEmailCheck", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.email_check,
                        object,
                        response -> {
                            customDialog.dismiss();
                    if (response.optString("status").equalsIgnoreCase("0"))
                    {
                        displayMessage(response.optString("msg"));
                    }
                    else {
                        openphonelogin();
                    }
                        },
                        error -> {
//                            if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;

                            if (response != null && response.data != null) {
                                utils.print("MyTest", "" + error);
                                utils.print("MyTestError", "" + error.networkResponse);
                                utils.print("MyTestError1", "" + response.statusCode);
                                try {
                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 ||
                                            response.statusCode == 500) {
                                        try {
                                            displayMessage(errorObj.optString("error"));
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }
                                    } else if (response.statusCode == 401) {
                                        if ((customDialog != null) && (customDialog.isShowing()))
                                            customDialog.dismiss();
                                        try {
                                            if (errorObj.optString("message")
                                                    .equalsIgnoreCase("invalid_token")) {
                                                //   Refresh token
                                            } else {
                                                displayMessage(errorObj.optString("message"));
                                            }
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }

                                    } else if (response.statusCode == 422) {
                                        Snackbar.make(getCurrentFocus(),
                                                errorObj.optString("password"), Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                        json = trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
                                            if (json.startsWith("The email")) {
                                                Snackbar.make(getCurrentFocus(),
                                                        json, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                            //displayMessage(json);
                                        } else {
                                            displayMessage(getString(R.string.please_try_again));
                                        }

                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }

                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }


                            } else {
                                if (error instanceof NoConnectionError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof NetworkError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof TimeoutError) {
                                    registerAPI();
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

         dialog = new Dialog(SignUp.this,R.style.AppTheme_NoActionBar);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.mobileverification);
        dialog.setCancelable(true);
        dialog.show();
        CountryCodePicker ccp= dialog.findViewById(R.id.ccp);
        ImageButton nextIcon= dialog.findViewById(R.id.nextIcon);
        ImageView imgBack = dialog.findViewById(R.id.imgBack);

        EditText mobile_no= dialog.findViewById(R.id.mobile_no);
        final String countryCode=ccp.getDefaultCountryCode();
        final String countryIso=ccp.getSelectedCountryNameCode();

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        nextIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                String phone=ccp.getSelectedCountryCodeWithPlus()+mobile_no.getText().toString();
//
//                PhoneNumber phoneNumber = new PhoneNumber(ccp.getSelectedCountryCode(),mobile_no.getText().toString(),ccp.getSelectedCountryNameCode());
//
//                checkMobile(phone,phoneNumber);
//                dialog.dismiss();

                String phone = ccp.getSelectedCountryCodeWithPlus() + mobile_no.getText().toString();
                SharedHelper.putKey(getApplicationContext(), "mobile", phone);
//                SharedHelper.putKey(getApplicationContext(), "mobile", phoneNumberString);
                Log.v("Phonecode",phone+" ");
                Intent intent =new Intent(SignUp.this, OtpVerification.class);
                intent.putExtra("phonenumber",phone);
                startActivityForResult(intent,APP_REQUEST_CODE);
                dialog.dismiss();
            }
        });
    }
    private void checkMobile(String phone,  PhoneNumber phoneNumber) {
        customDialog = new CustomDialog(SignUp.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("mobile", phone);
            utils.print("InputToEmailCheck", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.mobile_check,
                        object,
                        response -> {
                            customDialog.dismiss();
                            if (response.optString("status").equalsIgnoreCase("0"))
                            {
                                displayMessage(response.optString("msg"));
                            }
                            else {
                                phoneLogin(phoneNumber);
                            }
                        },
                        error -> {
//                            if ((customDialog != null) && (customDialog.isShowing()))
                            customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;

                            if (response != null && response.data != null) {
                                utils.print("MyTest", "" + error);
                                utils.print("MyTestError", "" + error.networkResponse);
                                utils.print("MyTestError1", "" + response.statusCode);
                                try {
                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 ||
                                            response.statusCode == 500) {
                                        try {
                                            displayMessage(errorObj.optString("error"));
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }
                                    } else if (response.statusCode == 401) {
                                        if ((customDialog != null) && (customDialog.isShowing()))
                                            customDialog.dismiss();
                                        try {
                                            if (errorObj.optString("message")
                                                    .equalsIgnoreCase("invalid_token")) {
                                                //   Refresh token
                                            } else {
                                                displayMessage(errorObj.optString("message"));
                                            }
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }

                                    } else if (response.statusCode == 422) {
                                        Snackbar.make(getCurrentFocus(),
                                                errorObj.optString("password"), Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                        json = trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
                                            if (json.startsWith("The email")) {
                                                Snackbar.make(getCurrentFocus(),
                                                        json, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                            //displayMessage(json);
                                        } else {
                                            displayMessage(getString(R.string.please_try_again));
                                        }

                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }

                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }


                            } else {
                                if (error instanceof NoConnectionError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof NetworkError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof TimeoutError) {
                                    registerAPI();
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
    public void phoneLogin( PhoneNumber phoneNumber) {
        Log.e(TAG, "PhoneLogin");
        final Intent intent = new Intent(this, AccountKitActivity.class);
        uiManager = new SkinManager(SkinManager.Skin.TRANSLUCENT,
                ContextCompat.getColor(getApplicationContext(),
                        R.color.colorPrimary),
                R.color.colorPrimary,
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
    public void onActivityResult(
            final int requestCode,
            final int resultCode,
            final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
        if (data != null) {
            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request

            dialog.dismiss();
//                SharedHelper.putKey(getApplicationContext(), "mobile", countryCodePicker.getSelectedCountryCodeWithPlus()+etMobile.getText().toString());
            registerAPI();
        }
//            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request
//                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//
//                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                    @Override
//                    public void onSuccess(Account account) {
//                        dialog.dismiss();
//                        Log.e(TAG, "onSuccess: Account Kit" + account.getId());
//                        Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
//                        if (AccountKit.getCurrentAccessToken().getToken() != null) {
//                            SharedHelper.putKey(getApplicationContext(), "account_kit_token",
//                                    AccountKit.getCurrentAccessToken().getToken());
//                            //SharedHelper.putKey(RegisterActivity.this, "loggedIn", getString(R.string.True));
//                            // Get phone number
//                            PhoneNumber phoneNumber = account.getPhoneNumber();
//                            String phoneNumberString = phoneNumber.toString();
//                            SharedHelper.putKey(getApplicationContext(), "mobile", phoneNumberString);
//                            registerAPI();
//                        } else {
//                            SharedHelper.putKey(getApplicationContext(), "account_kit_token", "");
//                            SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.False));
//                            SharedHelper.putKey(getApplicationContext(), "email", "");
//                            SharedHelper.putKey(getApplicationContext(), "login_by", "");
//                            SharedHelper.putKey(getApplicationContext(), "account_kit_token", "");
//                            Intent goToLogin = new Intent(getApplicationContext(), Login.class);
//                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(goToLogin);
//                           finish();
//                        }
//                    }
//
//                    @Override
//                    public void onError(AccountKitError accountKitError) {
//                        Log.e(TAG, "onError: Account Kit" + accountKitError);
//                    }
//                });
//                if (loginResult != null) {
//                    SharedHelper.putKey(getApplicationContext(), "account_kit", getString(R.string.True));
//                } else {
//                    SharedHelper.putKey(getApplicationContext(), "account_kit", getString(R.string.False));
//                }
//                String toastMessage;
//                if (loginResult.getError() != null) {
//                    toastMessage = loginResult.getError().getErrorType().getMessage();
//                    // showErrorActivity(loginResult.getError());
//                } else if (loginResult.wasCancelled()) {
//                    toastMessage = "Login Cancelled";
//                } else {
//                    if (loginResult.getAccessToken() != null) {
//                        Log.e(TAG, "onActivityResult: Account Kit" + loginResult.getAccessToken().toString());
//                        SharedHelper.putKey(getApplicationContext(), "account_kit", loginResult.getAccessToken().toString());
//                        toastMessage = "Welcome to Tranxit...";
//                    } else {
//                        SharedHelper.putKey(getApplicationContext(), "account_kit", "");
//                        toastMessage = String.format(
//                                "Welcome to Tranxit...",
//                                loginResult.getAuthorizationCode().substring(0, 10));
//                    }
//                }
//            }
        }
    }

    private void registerAPI() {
        customDialog = new CustomDialog(SignUp.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        JSONObject object = new JSONObject();
        try {

            object.put("device_type", "android");
            object.put("device_id", device_UDID);
            object.put("device_token", "" + device_token);
            object.put("login_by", "manual");
            object.put("first_name", etName.getText().toString());
            object.put("last_name", "");
            object.put("email", etEmail.getText().toString());
            object.put("password", etPassword.getText().toString());
            object.put("password_confirmation", etPassword.getText().toString());
            object.put("mobile", SharedHelper.getKey(getApplicationContext(), "mobile"));

            utils.print("InputToRegisterAPI", "" + object);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.register,
                        object,
                        response -> {
                            Log.e("registerresponse", response + "");

                                utils.print("SignInResponse", response.toString());
                            if (response.optString("msg").equalsIgnoreCase("The mobile has already been taken."))
                            {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    customDialog.dismiss();
                            }
                            else {
                                SharedHelper.putKey(getApplicationContext(), "email", etEmail.getText().toString());
                                SharedHelper.putKey(getApplicationContext(), "password", etPassword.getText().toString());
                                signIn();
                            }
                        },
                        error -> {
//                            if ((customDialog != null) && (customDialog.isShowing()))
                                customDialog.dismiss();
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;

                            if (response != null && response.data != null) {
                                utils.print("MyTest", "" + error);
                                utils.print("MyTestError", "" + error.networkResponse);
                                utils.print("MyTestError1", "" + response.statusCode);
                                try {
                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 ||
                                            response.statusCode == 500) {
                                        try {
                                            displayMessage(errorObj.optString("error"));
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }
                                    } else if (response.statusCode == 401) {
                                        if ((customDialog != null) && (customDialog.isShowing()))
                                            customDialog.dismiss();
                                        try {
                                            if (errorObj.optString("message")
                                                    .equalsIgnoreCase("invalid_token")) {
                                                //   Refresh token
                                            } else {
                                                displayMessage(errorObj.optString("message"));
                                            }
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }

                                    } else if (response.statusCode == 422) {
                                        Snackbar.make(getCurrentFocus(),
                                                errorObj.optString("password"), Snackbar.LENGTH_SHORT)
                                                .setAction("Action", null).show();
                                        json = trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
                                            if (json.startsWith("The email")) {
                                                Snackbar.make(getCurrentFocus(),
                                                        json, Snackbar.LENGTH_LONG)
                                                        .setAction("Action", null).show();
                                            }
                                            //displayMessage(json);
                                        } else {
                                            displayMessage(getString(R.string.please_try_again));
                                        }

                                    } else {
                                        displayMessage(getString(R.string.please_try_again));
                                    }

                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }


                            } else {
                                if (error instanceof NoConnectionError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof NetworkError) {
                                    displayMessage(getString(R.string.oops_connect_your_internet));
                                } else if (error instanceof TimeoutError) {
                                    registerAPI();
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



    private void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(SignUp.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            try {

                object.put("grant_type", "password");
                object.put("client_id", URLHelper.client_id);
                object.put("client_secret", URLHelper.client_secret);
                object.put("email", SharedHelper.getKey(getApplicationContext(), "email"));
                object.put("password", SharedHelper.getKey(getApplicationContext(), "password"));
                object.put("scope", "");
                object.put("device_type", "android");
                object.put("device_id", device_UDID);
                object.put("device_token", device_token);
                object.put("logged_in", "1");
                utils.print("InputToLoginAPI", "" + object);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.POST,
                            URLHelper.login,
                            object,
                            response -> {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                utils.print("SignUpResponse", response.toString());
                                SharedHelper.putKey(getApplicationContext(),
                                        "access_token", response.optString("access_token"));
                                SharedHelper.putKey(getApplicationContext(),
                                        "refresh_token", response.optString("refresh_token"));
                                SharedHelper.putKey(getApplicationContext(),
                                        "token_type", response.optString("token_type"));

                                if (!response.optString("currency").equalsIgnoreCase("") &&
                                        response.optString("currency") != null)
                                    SharedHelper.putKey(getApplicationContext(), "currency",
                                            response.optString("currency"));
//                                    SharedHelper.putKey(getApplicationContext(), "currency",
//                                            response.optString("currency"));
                                getProfile();
                            },
                            error -> {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                String json = null;
                                String Message;
                                NetworkResponse response = error.networkResponse;
                                utils.print("MyTest", "" + error);
                                utils.print("MyTestError", "" + error.networkResponse);

                                if (response != null && response.data != null) {
                                    try {
                                        JSONObject errorObj = new JSONObject(new String(response.data));

                                        if (response.statusCode == 400 || response.statusCode == 405 ||
                                                response.statusCode == 500 || response.statusCode == 401) {
                                            try {
                                                displayMessage(errorObj.optString("error"));
                                            } catch (Exception e) {
                                                displayMessage(getString(R.string.something_went_wrong));
                                            }
                                        } else if (response.statusCode == 422) {
                                            json = trimMessage(new String(response.data));
                                            if (json != "" && json != null) {
                                                displayMessage(json);
                                            } else {
                                                displayMessage(getString(R.string.please_try_again));
                                            }

                                        } else {
                                            displayMessage(getString(R.string.please_try_again));
                                        }

                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }


                                } else {
                                    if (error instanceof NoConnectionError) {
                                        displayMessage(getString(R.string.oops_connect_your_internet));
                                    } else if (error instanceof NetworkError) {
                                        displayMessage(getString(R.string.oops_connect_your_internet));
                                    } else if (error instanceof TimeoutError) {
                                        signIn();
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

        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }

    }



    public void getProfile() {
        if (isInternet) {
            customDialog = new CustomDialog(SignUp.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            URLHelper.USER_PROFILE_API,
                            object,
                            response -> {
                                if ((customDialog != null) && (customDialog.isShowing()))
                                    //customDialog.dismiss();
                                    utils.print("GetProfile", response.toString());
                                SharedHelper.putKey(getApplicationContext(), "id",
                                        response.optString("id"));
                                SharedHelper.putKey(getApplicationContext(), "first_name",
                                        response.optString("first_name"));
//                                SharedHelper.putKey(getApplicationContext(), "last_name",
//                                        response.optString("last_name"));
                                SharedHelper.putKey(getApplicationContext(), "email",
                                        response.optString("email"));
                                SharedHelper.putKey(getApplicationContext(), "picture",
                                        URLHelper.base + "storage/app/public/" +
                                                response.optString("picture"));
                                SharedHelper.putKey(getApplicationContext(), "gender",
                                        response.optString("gender"));
                                SharedHelper.putKey(getApplicationContext(), "mobile",
                                        response.optString("mobile"));
                                SharedHelper.putKey(getApplicationContext(), "wallet_balance",
                                        response.optString("wallet_balance"));
                                SharedHelper.putKey(getApplicationContext(), "payment_mode",
                                        response.optString("payment_mode"));
                                if (!response.optString("currency")
                                        .equalsIgnoreCase("") &&
                                        response.optString("currency") != null)
                                    SharedHelper.putKey(getApplicationContext(), "currency",
                                            response.optString("currency"));
//                                    SharedHelper.putKey(getApplicationContext(), "currency",
//                                            response.optString("currency"));
                                else
                                    SharedHelper.putKey(getApplicationContext(), "currency", "$");
                                SharedHelper.putKey(getApplicationContext(), "sos",
                                        response.optString("sos"));
                                SharedHelper.putKey(getApplicationContext(), "loggedIn",
                                        getString(R.string.True));
                                GoToMainActivity();
                            },
                            error -> {

                                    customDialog.dismiss();
                                String json = null;
                                String Message;
                                NetworkResponse response = error.networkResponse;
                                if (response != null && response.data != null) {
                                    try {
                                        JSONObject errorObj = new JSONObject(new String(response.data));

                                        if (response.statusCode == 400 || response.statusCode == 405 ||
                                                response.statusCode == 500) {
                                            try {
                                                displayMessage(errorObj.optString("error"));
                                            } catch (Exception e) {
                                                displayMessage(getString(R.string.something_went_wrong));
                                            }
                                        } else if (response.statusCode == 401) {
                                            try {
                                                if (errorObj.optString("error")
                                                        .equalsIgnoreCase("invalid_token")) {
                                                    refreshAccessToken();
                                                } else {
                                                    displayMessage(errorObj.optString("error"));
                                                }
                                            } catch (Exception e) {
                                                displayMessage(getString(R.string.something_went_wrong));
                                            }

                                        } else if (response.statusCode == 422) {

                                            json = trimMessage(new String(response.data));
                                            if (json != "" && json != null) {
                                                displayMessage(json);
                                            } else {
                                                displayMessage(getString(R.string.please_try_again));
                                            }

                                        } else {
                                            displayMessage(getString(R.string.please_try_again));
                                        }

                                    } catch (Exception e) {
                                        displayMessage(getString(R.string.something_went_wrong));
                                    }


                                } else {
                                    Log.e("error",error.toString()+"");
                                    if (error instanceof NoConnectionError) {
                                        displayMessage(getString(R.string.oops_connect_your_internet));
                                    } else if (error instanceof NetworkError) {
                                        displayMessage(getString(R.string.oops_connect_your_internet));
                                    } else if (error instanceof TimeoutError) {
                                        getProfile();
                                    }
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "" + "Bearer"
                                    + " " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                            Log.e("headers",headers+"");
                            return headers;
                        }
                    };

            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }
    public void getToken() {
        try {
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnSuccessListener(SignUp.this,
                            new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    String newToken = instanceIdResult.getToken();
                                    Log.e("newToken", newToken);
                                    SharedHelper.putKey(getApplicationContext(), "device_token", "" + newToken);
                                    device_token = newToken;

                                }
                            });
        } catch (Exception e) {
            device_token = "COULD NOT GET FCM TOKEN";
            utils.print(TAG, "Failed to complete token refresh");
        }

        try {
            device_UDID = android.provider.Settings.Secure.getString(getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (Exception e) {
            device_UDID = "COULD NOT GET UDID";
            e.printStackTrace();
            utils.print(TAG, "Failed to complete device UDID");
        }
    }
    private void refreshAccessToken() {
        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(getApplicationContext(), "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.login,
                        object,
                        response -> {
                            Log.v("SignUpResponse", response.toString());
                            SharedHelper.putKey(getApplicationContext(), "access_token",
                                    response.optString("access_token"));
                            SharedHelper.putKey(getApplicationContext(), "refresh_token",
                                    response.optString("refresh_token"));
                            SharedHelper.putKey(getApplicationContext(), "token_type",
                                    response.optString("token_type"));
                            getProfile();


                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        String json = null;
                        String Message;
                        NetworkResponse response = error.networkResponse;

                        if (response != null && response.data != null) {
                            SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.False));
                            GoToBeginActivity();
                        } else {
                            if (error instanceof NoConnectionError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof NetworkError) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (error instanceof TimeoutError) {
                                refreshAccessToken();
                            }
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

    private void GoToBeginActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(getApplicationContext(), SplashScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
      finish();
    }
    public void GoToMainActivity() {
        if (customDialog != null && customDialog.isShowing())
            customDialog.dismiss();
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
       finish();
    }
    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.txtSignIn), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }


    public  void logInType(String loginType) {
        Intent intent = new Intent(SignUp.this, Login.class);
        intent.putExtra("loginTypeSignUP", loginType);
        startActivity(intent);

    }
}
