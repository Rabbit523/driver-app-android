package com.rapido.provider;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.accounts.NetworkErrorException;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
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
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rapido.provider.Transportation.Activities.MainActivity;
import com.rapido.provider.Transportation.Activities.OtpVerification;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.accountkit.AccountKit;
import com.facebook.accountkit.PhoneNumber;
import com.facebook.accountkit.ui.AccountKitActivity;
import com.facebook.accountkit.ui.AccountKitConfiguration;
import com.facebook.accountkit.ui.LoginType;
import com.facebook.accountkit.ui.SkinManager;
import com.facebook.accountkit.ui.UIManager;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.hbb20.CountryCodePicker;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;
import java.util.regex.Pattern;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;

public class Login extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    TextView txtSignUp,txtForget;
    Button btnLogin;
     EditText etEmail, etPassword;
    TextView btnFb,btnGoogle;
    CustomDialog customDialog;
    Boolean isInternet;
    ConnectionHelper helper;
    String device_token, device_UDID;
    String TAG = "FragmentLogin";
    Utilities utils = new Utilities();
    private static final int REQ_SIGN_IN_REQUIRED = 100;
    /*----------Facebook Login---------*/
    CallbackManager callbackManager;
    ImageView backArrow;
    AccessTokenTracker accessTokenTracker;
    String UserName, UserEmail, result, FBUserID, FBImageURLString;
    JSONObject json;

    UIManager uiManager;



    /*----------Google Login---------------*/
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 100;
    public static int APP_REQUEST_CODE = 99;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(getApplicationContext());
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        txtSignUp = findViewById(R.id.txtSignUp);
        txtForget = findViewById(R.id.txtForget);
        btnLogin = findViewById(R.id.btnLogin);
        etEmail = findViewById(R.id.etEmail);
        btnFb =  findViewById(R.id.btnFb);
        etPassword = findViewById(R.id.etPassword);
        btnGoogle = findViewById(R.id.btnGoogle);
        txtSignUp.setOnClickListener(this);
        txtForget.setOnClickListener(this);
        btnLogin.setOnClickListener(this);
        btnFb.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);
        callbackManager = CallbackManager.Factory.create();
        getToken();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        try {

            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                String texto = bundle.getString("loginTypeSignUP");
                if (texto != null) {
                   if (texto.contains("fb"))
                   {
                       facebookLogin();
                   }
                    if (texto.contains("google"))
                    {
                        googleLogIn();
                    }

                }

            }
        }catch (Exception e) {
                e.printStackTrace();
        }


    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.txtSignUp)
        {
            startActivity(new Intent(Login.this,SignUp.class));
        }
        if (v.getId()==R.id.txtForget)
        {
            startActivity(new Intent(Login.this,ForgotPassword.class));
        }
        if (v.getId()==R.id.btnFb)
        {
            facebookLogin();
        }
        if (v.getId()==R.id.btnLogin)
        {
            Pattern ps = Pattern.compile(".*[0-9].*");
            if (etEmail.getText().toString().equals("") ||
                    etEmail.getText().toString().equalsIgnoreCase(getString(R.string.sample_mail_id))) {
                displayMessage(getString(R.string.email_validation));
            } else if (!Utilities.isValidEmail(etEmail.getText().toString())) {
                displayMessage(getString(R.string.not_valid_email));
            } else if (etPassword.getText().toString().equals("") ||
                    etPassword.getText().toString()
                            .equalsIgnoreCase(getString(R.string.password_txt))) {
                displayMessage(getString(R.string.password_validation));
            } else if (etPassword.length() < 6) {
                displayMessage(getString(R.string.password_size));
            } else {
                SharedHelper.putKey(getApplicationContext(), "email", etEmail.getText().toString());
                SharedHelper.putKey(getApplicationContext(), "password", etPassword.getText().toString());
                signIn();
            }
        }
        if(v.getId()==R.id.btnGoogle)
        {
            googleLogIn();
        }
    }

    public void googleLogIn() {
        Log.e(TAG,"Google signin");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        Log.e(TAG,"RC_SIGN_IN: "+RC_SIGN_IN);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    private void signIn() {
        if (isInternet) {
            customDialog = new CustomDialog(Login.this);
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
                                if (!response.optString("currency").equalsIgnoreCase("") && response.optString("currency") != null)
                                    SharedHelper.putKey(getApplicationContext(), "currency", response.optString("currency"));
                                  else
                                    SharedHelper.putKey(getApplicationContext(), "currency", "$");
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
            customDialog = new CustomDialog(Login.this);
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new
                    JsonObjectRequest(Request.Method.GET,
                            URLHelper.USER_PROFILE_API,
                            object,
                            response -> {
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                SharedHelper.putKey(getApplicationContext(), "id",
                                        response.optString("id"));
                                SharedHelper.putKey(getApplicationContext(), "first_name",
                                        response.optString("first_name"));
//                                SharedHelper.putKey(getApplicationContext(), "last_name",
//                                        response.optString("last_name"));
                                SharedHelper.putKey(getApplicationContext(), "email",
                                        response.optString("email"));
                                SharedHelper.putKey(getApplicationContext(), "gender", "" +
                                        response.optString("gender"));
                                SharedHelper.putKey(getApplicationContext(), "mobile",
                                        response.optString("mobile"));
                                SharedHelper.putKey(getApplicationContext(), "approval_status",
                                        response.optString("status"));
                                SharedHelper.putKey(getApplicationContext(), "loggedIn",
                                        getString(R.string.True));
                                if (response.optString("avatar").startsWith("http"))
                                    SharedHelper.putKey(getApplicationContext(), "picture",
                                            response.optString("avatar"));
                                else
                                    SharedHelper.putKey(getApplicationContext(), "picture",
                                            URLHelper.base + "storage/app/public/" +
                                                    response.optString("avatar"));

                                SharedHelper.getKey(getApplicationContext(), "picture");

                                if (response.optJSONObject("service") != null) {
                                    try {
                                        JSONObject service = response.optJSONObject("service");
                                        if (service.optJSONObject("service_type") != null) {
                                            JSONObject serviceType = service.optJSONObject("service_type");
                                            SharedHelper.putKey(getApplicationContext(), "service",
                                                    serviceType.optString("name"));
                                            SharedHelper.putKey(getApplicationContext(), "service_image",
                                                    serviceType.optString("image"));
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                SharedHelper.putKey(getApplicationContext(), "sos",
                                        response.optString("sos"));
                                SharedHelper.putKey(getApplicationContext(), "sos", response.optString("sos"));
                                SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.True));
                                GoToMainActivity();

                            },
                            error -> {
                                if ((customDialog != null) && customDialog.isShowing())
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
                                            refreshAccessToken();
                                        } else if (response.statusCode == 422) {

                                            json =trimMessage(new String(response.data));
                                            if (json != "" && json != null) {
                                                displayMessage(json);
                                            } else {
                                                displayMessage(getString(R.string.please_try_again));
                                            }

                                        } else if (response.statusCode == 503) {
                                            displayMessage(getString(R.string.server_down));
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
                                        getProfile();
                                    }
                                }
                            }) {
                        @Override
                        public Map<String, String> getHeaders() {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "Bearer " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                            utils.print("authoization", "" + SharedHelper.getKey(getApplicationContext(), "token_type") + " "
                                    + SharedHelper.getKey(getApplicationContext(), "access_token"));
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
                    .addOnSuccessListener(Login.this,
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
        if (isInternet) {
            customDialog = new CustomDialog(getApplicationContext());
            customDialog.setCancelable(false);
            if (customDialog != null)
                customDialog.show();
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
                                if ((customDialog != null) && customDialog.isShowing())
                                    customDialog.dismiss();
                                utils.print("SignUpResponse", response.toString());
                                SharedHelper.putKey(getApplicationContext(), "access_token",
                                        response.optString("access_token"));
                                SharedHelper.putKey(getApplicationContext(), "refresh_token",
                                        response.optString("refresh_token"));
                                SharedHelper.putKey(getApplicationContext(), "token_type",
                                        response.optString("token_type"));
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
                                utils.print("MyTestError1", "" + response.statusCode);

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
    public void facebookLogin() {
        if (isInternet) {
            LoginManager.getInstance().logInWithReadPermissions(Login.this,
                    Arrays.asList("public_profile","email"));


            LoginManager.getInstance().registerCallback(callbackManager,
                    new FacebookCallback<LoginResult>() {


                        public void onSuccess(final LoginResult loginResult) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(),
                                        new GraphRequest.GraphJSONObjectCallback() {

                                            @Override
                                            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                                                try {
                                                    Log.e(TAG,"id"+user.optString("id"));
                                                    Log.e(TAG,"name"+user.optString("first_name"));

                                                    String profileUrl="https://graph.facebook.com/v2.8/"+user.optString("id")+"/picture?width=1920";


                                                    final JsonObject json = new JsonObject();
                                                    json.addProperty("device_type", "android");
                                                    json.addProperty("device_token", device_token);
                                                    json.addProperty("accessToken", loginResult.getAccessToken().getToken());
                                                    json.addProperty("device_id", device_UDID);
                                                    json.addProperty("login_by", "facebook");
                                                    json.addProperty("first_name", user.optString("first_name"));
                                                    json.addProperty("last_name", user.optString("last_name"));
                                                    json.addProperty("id", user.optString("id"));
                                                    json.addProperty("email", user.optString("email"));
                                                    json.addProperty("avatar",profileUrl);
                                                    json.addProperty("logged_in", "1");
                                                    login(json, URLHelper.FACEBOOK_LOGIN, "facebook");

                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    Log.d("facebookExp", e.getMessage());
                                                }
                                            }
                                        });
                                Bundle parameters = new Bundle();
                                parameters.putString("fields", "id,first_name,last_name,email");
                                request.setParameters(parameters);
                                request.executeAsync();
                                Log.e("getAccessToken", "" + loginResult.getAccessToken().getToken());
                                SharedHelper.putKey(Login.this, "accessToken", loginResult.getAccessToken().getToken());
//                                        login(loginResult.getAccessToken().getToken(), URLHelper.FACEBOOK_LOGIN, "facebook");
                            }

                        }

                        @Override
                        public void onCancel() {
                            // App code
                        }

                        @Override
                        public void onError(FacebookException exception) {
                            Log.e("exceptionfacebook",exception.toString());
                            // App code
                        }
                    });
        } else {
            //mProgressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
            builder.setMessage("Check your Internet").setCancelable(false);
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Intent NetworkAction = new Intent(Settings.ACTION_SETTINGS);
                    startActivity(NetworkAction);

                }
            });
            builder.show();
        }
    }

    public void login(final JsonObject json, final String URL, final String Loginby) {
        customDialog = new CustomDialog(Login.this);
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
        Log.e("url",URL+"");
        Log.e(TAG, "login: Facebook" + json);
        Ion.with(Login.this)
                .load(URL)
                .addHeader("X-Requested-With", "XMLHttpRequest")
//                .addHeader("Authorization",""+SharedHelper.getKey(context, "token_type")+" "+SharedHelper.getKey(context, "access_token"))
                .setJsonObjectBody(json)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.e("result_data",result+"");
                        // do stuff with the result or error
                        if ((customDialog != null) && customDialog.isShowing())
                            customDialog.dismiss();
                        if (e != null) {
                            if (e instanceof NetworkErrorException) {
                                displayMessage(getString(R.string.oops_connect_your_internet));
                            } else if (e instanceof TimeoutException) {
                                login(json, URL, Loginby);
                            }
                            return;
                        }
                        if (result != null) {
                            Log.e(Loginby + "_Response", result.toString());
                            try {
                                JSONObject jsonObject = new JSONObject(result.toString());
                                String status = jsonObject.optString("status");
//                                if (status.equalsIgnoreCase("true")) {
                                SharedHelper.putKey(Login.this, "token_type", jsonObject.optString("token_type"));
                                SharedHelper.putKey(Login.this, "access_token", jsonObject.optString("access_token"));
                                if (Loginby.equalsIgnoreCase("facebook"))
                                    SharedHelper.putKey(Login.this, "login_by", "facebook");
                                if (Loginby.equalsIgnoreCase("google"))
                                    SharedHelper.putKey(Login.this, "login_by", "google");

                                openphonelogin();


                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }

                        }
                        // onBackPressed();
                    }
                });
    }
    String phoneNumberString="";
    private void openphonelogin() {

        Dialog dialog = new Dialog(Login.this,R.style.AppTheme_NoActionBar);

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
                Intent intent =new Intent(Login.this, OtpVerification.class);
                intent.putExtra("phonenumber",phoneNumberString);
                startActivityForResult(intent,APP_REQUEST_CODE);
                dialog.dismiss();
//                String phone=ccp.getDefaultCountryCode()+mobile_no.getText().toString();
//                PhoneNumber phoneNumber = new PhoneNumber(ccp.getSelectedCountryCode(),
//                        mobile_no.getText().toString(),ccp.getSelectedCountryNameCode());
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
                R.drawable.bg,
                SkinManager.Tint.BLACK,
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
    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
       overridePendingTransition(R.anim.activity_back_in, R.anim.activity_back_out);
        finish();
    }
    public void GoToMainActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
       finish();
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.txtSignUp), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG,"Result: "+requestCode);
        if (data != null) {
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            if (requestCode == RC_SIGN_IN) {

//                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//                Log.e(TAG,"If: "+result);
//                handleSignInResult(result);
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
                Log.e(TAG,"If: "+result.toString());

            }

            if (requestCode == APP_REQUEST_CODE) { // confirm that this response matches your request


//                SharedHelper.putKey(Login.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());
//                PhoneNumber phoneNumber = account.getPhoneNumber();
//                String phoneNumberString = phoneNumber.toString();
                SharedHelper.putKey(Login.this, "mobile", phoneNumberString);
                getProfile();
//                AccountKitLoginResult loginResult = data.getParcelableExtra(AccountKitLoginResult.RESULT_KEY);
//
//                AccountKit.getCurrentAccount(new AccountKitCallback<Account>() {
//                    @Override
//                    public void onSuccess(Account account) {
//                        Log.e(TAG, "onSuccess: Account Kit" + account.getId());
//                        Log.e(TAG, "onSuccess: Account Kit" + AccountKit.getCurrentAccessToken().getToken());
//                        if (AccountKit.getCurrentAccessToken().getToken() != null) {
//                            SharedHelper.putKey(Login.this, "account_kit_token", AccountKit.getCurrentAccessToken().getToken());
//                            PhoneNumber phoneNumber = account.getPhoneNumber();
//                            String phoneNumberString = phoneNumber.toString();
//                            SharedHelper.putKey(Login.this, "mobile", phoneNumberString);
//                            getProfile();
////                            GoToMainActivity();
//                        } else {
//                            SharedHelper.putKey(Login.this, "account_kit_token", "");
//                            SharedHelper.putKey(Login.this, "loggedIn", getString(R.string.False));
//                            SharedHelper.putKey(getApplicationContext(), "email", "");
//                            SharedHelper.putKey(getApplicationContext(), "login_by", "");
//                            SharedHelper.putKey(Login.this, "account_kit_token", "");
//                            //Intent goToLogin = new Intent(ActivitySocialLogin.this, BeginScreen.class);
//                            Intent goToLogin = new Intent(Login.this, MainActivity.class);
//                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            startActivity(goToLogin);
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onError(AccountKitError accountKitError) {
//                        Log.e(TAG, "onError: Account Kit" + accountKitError);
//                    }
//                });
//                String toastMessage;
//                if (loginResult.getError() != null) {
//                    toastMessage = loginResult.getError().getErrorType().getMessage();
//                    // showErrorActivity(loginResult.getError());
//                } else if (loginResult.wasCancelled()) {
//                    toastMessage = "Login Cancelled";
//                } else {
//                    if (loginResult.getAccessToken() != null) {
//                        Log.e(TAG, "onActivityResult: Account Kit" + loginResult.getAccessToken() + "");
//                        //SharedHelper.putKey(this,"account_kit",loginResult.getAccessToken().toString());
//                        toastMessage = "Welcome to Tranxit...";
//                    } else {
//                        toastMessage = String.format(
//                                "Welcome to Tranxit...",
//                                loginResult.getAuthorizationCode().substring(0, 10));
//                    }
//                }
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("Beginscreen", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d("Google", "display_name:" + acct.getDisplayName());
            Log.d("Google", "mail:" + acct.getEmail());
            Log.d("Google", "photo:" + acct.getPhotoUrl());

            new RetrieveTokenTask().execute(acct.getEmail());
        }
    }
    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String accessToken) {
            super.onPostExecute(accessToken);
            Log.e("Token", accessToken);
            final JsonObject json = new JsonObject();
            json.addProperty("device_type", "android");
            json.addProperty("device_token", device_token);
            json.addProperty("accessToken", accessToken);
            json.addProperty("device_id", device_UDID);
            json.addProperty("login_by", "google");

            login(json, URLHelper.GOOGLE_LOGIN, "google");

        }
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
