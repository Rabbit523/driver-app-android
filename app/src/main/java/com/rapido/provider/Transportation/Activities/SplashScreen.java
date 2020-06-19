package com.rapido.provider.Transportation.Activities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.rapido.provider.Login;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.chat.UserChatActivity;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = SplashScreen.class.getSimpleName();
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    public Activity activity = SplashScreen.this;
    public Context context = SplashScreen.this;
    ConnectionHelper helper;
    Boolean isInternet;
    Handler handleCheckStatus;
    int retryCount = 0;
    AlertDialog alert;
    String device_token;

    public static String printKeyHash(Activity context) {
        PackageInfo packageInfo;
        String key = null;
        try {
//getting application package name, as defined in manifest
            String packageName = context.getApplicationContext().getPackageName();

//Retriving package info
            packageInfo = context.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_SIGNATURES);

            Log.e("Package Name=", context.getApplicationContext().getPackageName());

            for (Signature signature : packageInfo.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                key = new String(Base64.encode(md.digest(), 0));

// String key = new String(Base64.encodeBytes(md.digest()));
                Log.e("Key Hash=", key);
            }
        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("Name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("No such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("Exception", e.toString());
        }

        return key;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(getString(R.string.True))) {
            try {
                if (getIntent().getExtras() != null) {

                    String msgType = getIntent().getExtras().get("msg_type").toString();
                    String msg = getIntent().getExtras().get("msg").toString();
                    String requestId = getIntent().getExtras().get("request_id").toString();
                    String userName = getIntent().getExtras().get("user_name").toString();
                    Log.v(TAG, "msgType: " + msgType);
                    Log.v(TAG, "msg: " + msg);
                    if (msgType.equalsIgnoreCase("chat")) {
                        Intent intent = new Intent(SplashScreen.this, UserChatActivity.class);
                        intent.putExtra("message", msg);
                        intent.putExtra("request_id", requestId);
                        intent.putExtra("userName", userName);
                        startActivity(intent);
                    } else if (msgType.equalsIgnoreCase("admin")) {
                        startActivity(new Intent(SplashScreen.this, NotificationTab.class));
                        finish();
                    }
                    else if (msgType.equalsIgnoreCase("offline")) {

                        Intent intent =new Intent(SplashScreen.this,MainActivity.class);
                        intent.putExtra("status","offline");
                        startActivity(intent);
                        finish();
                    }
                    else {
                        startActivity(new Intent(SplashScreen.this, MainActivity.class));
                        finish();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        final NetworkInfo mobileInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();
        handleCheckStatus = new Handler();


        if (Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (checkAndRequestPermissions()) {
            handleCheckStatus.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (helper.isConnectingToInternet()) {
                        if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(getString(R.string.True))) {
                            getProfile();
                        } else {
                            GoToBeginActivity();
                        }
                        if (alert != null && alert.isShowing()) {
                            alert.dismiss();
                        }
                    } else {
                        showDialog();
                        handleCheckStatus.postDelayed(this, 3000);
                    }
                }
            }, 5000);
        }

        Log.e("printKeyHash", printKeyHash(SplashScreen.this) + "");

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener(SplashScreen.this,
                        new OnSuccessListener<InstanceIdResult>() {
                            @Override
                            public void onSuccess(InstanceIdResult instanceIdResult) {
                                String newToken = instanceIdResult.getToken();
                                Log.e("newToken", newToken);
                                SharedHelper.putKey(getApplicationContext(), "device_token", "" + newToken);
                                device_token = newToken;

                            }
                        });

    }

    public void getProfile() {
        retryCount++;
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.GET,
                        URLHelper.USER_PROFILE_API,
                        object,
                        response -> {
                            Log.e("responseBody", response.toString());
                            SharedHelper.putKey(context, "id", response.optString("id"));
                            SharedHelper.putKey(context, "first_name", response.optString("first_name"));
//                            SharedHelper.putKey(context, "last_name", response.optString("last_name"));
                            SharedHelper.putKey(context, "email", response.optString("email"));
                            SharedHelper.putKey(context, "sos", response.optString("sos"));
                            if (response.optString("avatar").startsWith("http"))
                                SharedHelper.putKey(context, "picture", response.optString("avatar"));
                            else
                                SharedHelper.putKey(context, "picture", URLHelper.base + "storage/app/public/" + response.optString("avatar"));
                            SharedHelper.putKey(context, "gender", response.optString("gender"));
                            SharedHelper.putKey(context, "mobile", response.optString("mobile"));
                            SharedHelper.putKey(context, "approval_status", response.optString("status"));
                            //                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
                            //                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.True));

                            if (response.optJSONObject("service") != null) {
                                try {
                                    JSONObject service = response.optJSONObject("service");
                                    if (service.optJSONObject("service_type") != null) {
                                        JSONObject serviceType = service.optJSONObject("service_type");
                                        SharedHelper.putKey(context, "service",
                                                serviceType.optString("name"));
                                        SharedHelper.putKey(getApplicationContext(), "service_image",
                                                serviceType.optString("image"));
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if (response.optString("status").equalsIgnoreCase("new")) {
                                Intent intent = new Intent(activity, WaitingForApproval.class);
                                activity.startActivity(intent);
                                finish();
                            } else {
                                GoToMainActivity();
                            }

                        },
                        error -> {

                            if (retryCount < 1) {
                                getProfile();
                            } else {
                                GoToBeginActivity();
                            }
                            String json = null;
                            String Message;
                            NetworkResponse response = error.networkResponse;
                            if (response != null && response.data != null) {
                                Log.v("response.statusCode",response.statusCode+" ");
                                try {
                                    JSONObject errorObj = new JSONObject(new String(response.data));

                                    if (response.statusCode == 400 || response.statusCode == 405 ||
                                            response.statusCode == 500) {
                                        try {
                                            displayMessage(errorObj.optString("message"));
                                        } catch (Exception e) {
                                            displayMessage(getString(R.string.something_went_wrong));
                                        }
                                    } else if (response.statusCode == 401) {
                                        SharedHelper.putKey(context, "loggedIn",
                                                getString(R.string.False));
                                        if (retryCount > 5)
                                            GoToBeginActivity();
                                    } else if (response.statusCode == 422) {

                                        json = Ilyft.trimMessage(new String(response.data));
                                        if (json != "" && json != null) {
                                            displayMessage(json);
                                        } else {
                                            displayMessage(getString(R.string.please_try_again));
                                        }

                                    } else if (response.statusCode == 503) {
                                        displayMessage(getString(R.string.server_down));
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
                        headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                        Log.e("accesstoken", SharedHelper.getKey(context, "access_token") + "");
                        return headers;
                    }
                };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GoToMainActivity() {
        Intent mainIntent = new Intent(activity, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void displayMessage(String toastString) {
        Toast.makeText(activity, toastString, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }

    private void showDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.connect_to_network))
                .setCancelable(false)
                .setPositiveButton(getString(R.string.connect_to_wifi), (dialog, id) -> {
                    alert.dismiss();
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                })
                .setNegativeButton(getString(R.string.quit),
                        (dialog, id) -> {
                            alert.dismiss();
                            finish();
                        });
        if (alert == null) {
            alert = builder.create();
            alert.show();
        }
    }

    private boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int locationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int cameraPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CAMERA);
        int writeExternalStorage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int callphonePermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.CALL_PHONE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (locationPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (writeExternalStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (callphonePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(Manifest.permission.SEND_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "sms & location services permission granted");
                        handleCheckStatus = new Handler();

                        handleCheckStatus.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Log.w("Handler", "Called");
                                if (SharedHelper.getKey(context, "loggedIn")
                                        .equalsIgnoreCase(getString(R.string.True))) {
                                    getProfile();
                                } else {
                                    GoToBeginActivity();
                                }
                            }
                        }, 3000);
                        // process the normal flow
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.SEND_SMS) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                            showDialogOK("SMS and Location Services Permission required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            handleCheckStatus = new Handler();

                            handleCheckStatus.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Log.w("Handler", "Called");
                                    if (SharedHelper.getKey(context, "loggedIn").equalsIgnoreCase(getString(R.string.True))) {
                                        getProfile();
                                    } else {
                                        GoToBeginActivity();
                                    }
                                }
                            }, 3000);
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
}
