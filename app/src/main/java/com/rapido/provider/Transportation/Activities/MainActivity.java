package com.rapido.provider.Transportation.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.crashlytics.android.Crashlytics;
import com.rapido.provider.Login;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Bean.Connect;
import com.rapido.provider.Transportation.Fragment.Help;
import com.rapido.provider.Transportation.Fragment.DriverMapFragment;
import com.rapido.provider.Transportation.Fragment.SummaryFragment;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Service.StatusCheckServie;
import com.rapido.provider.Transportation.Utilities.CustomTypefaceSpan;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.facebook.FacebookSdk;
import com.facebook.accountkit.AccountKit;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import io.fabric.sdk.android.Fabric;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;


public class MainActivity extends AppCompatActivity {
    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_SUMMARY = "summary";
    private static final String TAG_HELP = "help";
    public static FragmentManager fragmentManager;
    // index to identify current nav menu item
    public int navItemIndex = 0;
    public String CURRENT_TAG = TAG_HOME;
    Fragment fragment;
    Activity activity;
    Context context;
    Toolbar toolbar;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View navHeader;
    private ImageView imgProfile;
    private TextView txtName, approvaltxt;
    private ImageView status;
    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = true;
    private static final int REQUEST_LOCATION = 1450;
    Utilities utils = new Utilities();
    boolean push = false;
     public static  String statustg="";
    GoogleApiClient mGoogleApiClient;

    private NotificationManager notificationManager;

    DriverMapFragment lFrag;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        context = getApplicationContext();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (SharedHelper.getKey(context, "login_by").equals("facebook"))
            FacebookSdk.sdkInitialize(getApplicationContext());
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);

        findViewById();
        Bundle extras = getIntent().getExtras();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();

        if (extras != null) {
            push = extras.getBoolean("push");
        }
        map();

        Connect.addMyBooleanListener(() -> Toast.makeText(getApplication(),
                "Changed", Toast.LENGTH_SHORT).show());
        loadNavHeader();
        setUpNavigationView();

        navHeader.setOnClickListener(view -> {
            drawer.closeDrawers();
            new Handler().postDelayed(() -> startActivity(new Intent(activity,
                    Profile.class)), 250);
        });
        if (getIntent().getStringExtra("status")!=null)
        {
            statustg = getIntent().getStringExtra("status");
        }

    }

    private void findViewById() {
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navHeader = navigationView.getHeaderView(0);
        txtName = navHeader.findViewById(R.id.usernameTxt);
        approvaltxt = navHeader.findViewById(R.id.status_txt);
        imgProfile = navHeader.findViewById(R.id.img_profile);
        status = navHeader.findViewById(R.id.status);
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.nav_home:
                    if (CURRENT_TAG != TAG_HOME) {
                        fragment = new DriverMapFragment();
                        GoToFragment();
                    } else {
                        drawer.closeDrawers();
                    }
                    break;
                case R.id.nav_profile:
                    drawer.closeDrawers();
                    new Handler().postDelayed(() -> startActivity(new Intent(activity, Profile.class)),
                            250);
                    break;
                case R.id.nav_document:

                    startActivity(new Intent(activity, DocumentStatus.class));
                    break;

                //
                case R.id.nav_yourtrips:
                    drawer.closeDrawers();
                    new Handler().postDelayed(() -> startActivity(new Intent(MainActivity.this,
                            HistoryActivity.class)), 250);

                    break;
                case R.id.nav_withdraw:
                    drawer.closeDrawers();
                    new Handler().postDelayed(() -> startActivity(new Intent(MainActivity.this,
                            WithdrawActivity.class)), 250);

                    break;
                case R.id.nav_notification:
                    drawer.closeDrawers();
                    new Handler().postDelayed(() -> startActivity(new Intent(MainActivity.this,
                            NotificationTab.class)), 250);

                    break;
                case R.id.nav_wallet:
                    navItemIndex = 2;
                    CURRENT_TAG = TAG_SUMMARY;
                    fragment = new SummaryFragment();
                    drawer.closeDrawers();
                    new Handler().postDelayed(() -> {
                        FragmentManager manager2 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction1 = manager2.beginTransaction();
                        transaction1.replace(R.id.content, fragment);
                        transaction1.addToBackStack(null);
                        transaction1.commit();
                    }, 250);

                    //GoToFragment();
                    break;
                case R.id.nav_help:
                    navItemIndex = 3;
                    CURRENT_TAG = TAG_HELP;
                    fragment = new Help();
                    drawer.closeDrawers();
                    new Handler().postDelayed(() -> {
                        FragmentManager manager4 = getSupportFragmentManager();
                        @SuppressLint("CommitTransaction")
                        FragmentTransaction transaction2 = manager4.beginTransaction();
                        transaction2.replace(R.id.content, fragment);
                        transaction2.addToBackStack(null);
                        transaction2.commit();
                    }, 250);

                    //GoToFragment();
                    break;
                case R.id.nav_earnings:
                    drawer.closeDrawers();
                    new Handler().postDelayed(() -> startActivity(new Intent(activity,
                            EarningActivity.class)), 250);
                    break;
                case R.id.nav_share:
                    drawer.closeDrawers();
                    navigateToShareScreen(URLHelper.APP_URL);
                    return true;
                case R.id.nav_logout:
                    showLogoutDialog();
                    return true;
                default:
                    navItemIndex = 0;
            }
            return true;
        });

        Menu m = navigationView.getMenu();

        for (int i = 0; i < m.size(); i++) {
            MenuItem menuItem = m.getItem(i);
            applyFontToMenuItem(menuItem);

        }
        ActionBarDrawerToggle actionBarDrawerToggle = new
                ActionBarDrawerToggle(this, drawer, toolbar,
                        R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        super.onDrawerClosed(drawerView);
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        super.onDrawerOpened(drawerView);
                        if (drawer.isDrawerOpen(GravityCompat.START)) {
                            loadNavHeader();
                        }
                    }
                };
        drawer.setDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

    private void applyFontToMenuItem(MenuItem mi) {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/Montserrat-Regular.ttf");
        SpannableString mNewTitle = new SpannableString(mi.getTitle());
        mNewTitle.setSpan(new CustomTypefaceSpan("", font), 0,
                mNewTitle.length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);
        mi.setTitle(mNewTitle);
    }

    private void loadNavHeader() {
        // name, website
        txtName.setText(SharedHelper.getKey(context, "first_name"));
        if (SharedHelper.getKey(context, "approval_status").equals("new") ||
                SharedHelper.getKey(context, "approval_status").equals("onboarding")) {
            approvaltxt.setTextColor(Color.YELLOW);
            approvaltxt.setText(getText(R.string.waiting_for_approval));
            status.setImageResource(R.drawable.newuser);
        } else if (SharedHelper.getKey(context, "approval_status").equals("banned")) {
            approvaltxt.setTextColor(Color.RED);
            approvaltxt.setText(getText(R.string.banned));
            status.setImageResource(R.drawable.banned);
        } else {
            approvaltxt.setTextColor(Color.GREEN);
            approvaltxt.setText(getText(R.string.approved));
            status.setImageResource(R.drawable.approved);
        }

        utils.print("Profile_PIC", "" + SharedHelper.getKey(context, "picture"));
        Picasso.get().load(SharedHelper.getKey(context, "picture"))
                .placeholder(R.drawable.ic_dummy_user)
                .error(R.drawable.ic_dummy_user)
                .into(imgProfile);

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                fragment = new DriverMapFragment();
                GoToFragment();
                return;
            } else {
                System.exit(0);
            }
        }

        super.onBackPressed();
    }


    private void map() {
        MainActivity.this.runOnUiThread(() -> {
            fragment = new DriverMapFragment();
            FragmentManager manager = getSupportFragmentManager();
            @SuppressLint("CommitTransaction")
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content, fragment);
            transaction.commit();
            fragmentManager = getSupportFragmentManager();
        });
    }

    public void GoToFragment() {
        MainActivity.this.runOnUiThread(() -> {
            drawer.closeDrawers();
            FragmentManager manager = getSupportFragmentManager();
            @SuppressLint("CommitTransaction")
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.content, fragment);
            transaction.commit();
        });
    }

    public void navigateToShareScreen(String shareUrl) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name));
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {

                    }

                    @Override
                    public void onConnectionSuspended(int i) {
                        mGoogleApiClient.connect();
                    }
                })
                .addOnConnectionFailedListener(connectionResult ->
                        utils.print("Location error", "Location error " +
                                connectionResult.getErrorCode())).build();
        mGoogleApiClient.connect();

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(30 * 1000);
        locationRequest.setFastestInterval(5 * 1000);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        builder.setAlwaysShow(true);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(result1 -> {
            final Status status = result1.getStatus();
            switch (status.getStatusCode()) {
                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        status.startResolutionForResult(MainActivity.this, REQUEST_LOCATION);
                    } catch (IntentSender.SendIntentException e) {
                        // Ignore the error.
                    }
                    break;
            }
        });
//	        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
       /* if (requestCode == fragment.REQUEST_LOCATION){
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public void logout() {
        JSONObject object = new JSONObject();
        try {
            object.put("id", SharedHelper.getKey(context, "id"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new
                JsonObjectRequest(Request.Method.POST,
                        URLHelper.LOGOUT,
                        object,
                        response -> {
//                            if (isMyServiceRunning(StatusCheckServie.class)) {
////                                stopService(new Intent(getApplicationContext(), StatusCheckServie.class));
//                            }
                            drawer.closeDrawers();
                            if (SharedHelper.getKey(context, "login_by").equals("facebook"))
                                LoginManager.getInstance().logOut();
                            if (SharedHelper.getKey(context, "login_by").equals("google"))
                                signOut();
                            if (!SharedHelper.getKey(MainActivity.this, "account_kit_token").equalsIgnoreCase("")) {
                                Log.e("MainActivity", "Account kit logout: " + SharedHelper.getKey(MainActivity.this, "account_kit_token"));
                                AccountKit.logOut();
                                SharedHelper.putKey(MainActivity.this, "account_kit_token", "");
                            }


                            //SharedHelper.putKey(context, "email", "");
                            SharedHelper.clearSharedPreferences(MainActivity.this);
                            Intent goToLogin = new Intent(activity, Login.class);
                            goToLogin.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(goToLogin);
                            finish();
                        }, error -> {
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {
                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.getString("message"));
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }
                            } else if (response.statusCode == 401) {
                                /*refreshAccessToken();*/
                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
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
                            logout();
                        }
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        Log.e("getHeaders: Token", SharedHelper.getKey(context, "access_token") + SharedHelper.getKey(context, "token_type"));
                        headers.put("Authorization", "" + "Bearer" + " " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void signOut() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //taken from google api console (Web api client id)
//                .requestIdToken("795253286119-p5b084skjnl7sll3s24ha310iotin5k4.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {

                FirebaseAuth.getInstance().signOut();
                if (mGoogleApiClient.isConnected()) {
                    Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(status -> {
                        if (status.isSuccess()) {
                            Log.d("MainAct", "Google User Logged out");
                           /* Intent intent = new Intent(LogoutActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();*/
                        }
                    });
                }
            }

            @Override
            public void onConnectionSuspended(int i) {
                Log.d("MAin", "Google API Client Connection Suspended");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(MainActivity.this, StatusCheckServie.class));
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void showLogoutDialog() {
        if (!isFinishing()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.logout));
            builder.setMessage(getString(R.string.exit_confirm));

            builder.setPositiveButton(R.string.yes,
                    (dialog, which) -> logout());

            builder.setNegativeButton(R.string.no, (dialog, which) -> {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.openDrawer(GravityCompat.START);
            });
            builder.setCancelable(false);
            final AlertDialog dialog = builder.create();
            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
            dialog.setOnShowListener(arg -> {
                dialog.getButton(AlertDialog.BUTTON_POSITIVE)
                        .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
                dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
                        .setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            });
            dialog.show();
        }
    }

}
