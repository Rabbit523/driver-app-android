package com.rapido.provider.Transportation.Fragment;


import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.LayerDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.rapido.provider.BuildConfig;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Activities.DocUploadActivity;
import com.rapido.provider.Transportation.Activities.Profile;
import com.rapido.provider.Transportation.Activities.ShowProfile;
import com.rapido.provider.Transportation.Activities.SplashScreen;
import com.rapido.provider.Transportation.Activities.WaitingForApproval;
import com.rapido.provider.Transportation.Adapters.DocListAdapter;
import com.rapido.provider.Transportation.Bean.DocListItemModel;
import com.rapido.provider.Transportation.Bean.User;
import com.rapido.provider.Transportation.Helper.AppHelper;
import com.rapido.provider.Transportation.Helper.BitmapWorkerTask;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.SharedHelperImage;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Helper.VolleyMultipartRequest;
import com.rapido.provider.Transportation.Utilities.LocationTracking;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.rapido.provider.Transportation.Utilities.Utils;
import com.rapido.provider.Transportation.chat.UserChatActivity;
import com.squareup.picasso.Picasso;
import com.wedrive.driver.Helper.DataParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;

public class DriverMapFragment extends Fragment implements
        OnMapReadyCallback,
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,
        GoogleMap.OnCameraMoveListener {

    String docopen = "";
    Uri documentUri;
    public static final int REQUEST_LOCATION = 1450;
    private static final int MEDIA_TYPE_IMAGE = 1;
    private static final int TAKE_PICTURE = 0;
    private static final int GET_PICTURE = 1;
    public static SupportMapFragment mapFragment = null;
    public static String TAG = "DriverMapFragment";
    public static int deviceHeight;
    public static int deviceWidth;
    public Handler ha;
    public String myLat = "";
    public String myLng = "";
    public double old_lat = 0, old_lng = 0, new_lat, new_lng;
    public float speed;
    public double distance = 0;
    String firstTime = "";
    LinearLayout lnrNotApproved;
    String CurrentStatus = " ";
    String PreviousStatus = " ";
    String request_id = " ";
    int method;
    Activity activity;
    Context context;
    CountDownTimer countDownTimer;
    int value = 0;
    Marker currentMarker;
    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;
    ParserTask parserTask;
    ImageView imgCurrentLoc;
    boolean normalPlay = false;
    boolean push = false;
    ArrayList<LatLng> points;
    PolylineOptions lineOptions;
    //content layout 01
    TextView txt01Pickup,txtDropOff;
    TextView txt01Timer;
    ImageView img01User;
    TextView txt01UserName,tvDistance;
    TextView txtSchedule;
    RatingBar rat01UserRating;
    //content layer 02
    ImageView img02User;
    TextView txt02UserName;
    RatingBar rat02UserRating;
    TextView txt02ScheduledTime;
    TextView txt02From;
    TextView txt02To;
    TextView topSrcDestTxtLbl;
    //content layer 03
    CircleImageView img03User;
    TextView txt03UserName;
    TextView lblCmfrmSourceAddress,lblCmfrmDestAddress;
    RatingBar rat03UserRating;
    ImageButton img03Call;
    ImageButton img_chat;
    ImageView img03Status1;
    ImageView img03Status2;
    ImageView img03Status3;
    //content layer 04
    TextView txt04InvoiceId, txtTotal;
    TextView txt04BasePrice;
    TextView txt04Distance;
    TextView txt04Tax;
    TextView txt04Total;
    TextView txt04PaymentMode;
    TextView txt04Commision;
    TextView lblProviderName;
    ImageView paymentTypeImg;
    //content layer 05
    ImageView img05User;
    RatingBar rat05UserRating;
    EditText edt05Comment;
    //Button layer 01
    Button btn_01_status, btn_confirm_payment, btn_rate_submit;
    Button btn_go_offline, btn_go_online;
    TextView activeStatus;
    LinearLayout lnrGoOffline, layoutinfo;
    //Button layer 02
    Button btn_02_accept;
    TextView btn_02_reject;
    Button btn_cancel_ride;
    //map layout
    LinearLayout ll_01_mapLayer, driverArrived, driverPicked, driveraccepted;
    //content layout
    LinearLayout ll_01_contentLayer_accept_or_reject_now;
    LinearLayout ll_02_contentLayer_accept_or_reject_later;
    LinearLayout ll_03_contentLayer_service_flow;
    LinearLayout ll_04_contentLayer_payment;
    LinearLayout ll_05_contentLayer_feedback;
    LinearLayout errorLayout;
    //menu icon
    ImageView menuIcon;
    int NAV_DRAWER = 0;
    DrawerLayout drawer;
    Boolean isInternet;
    Utilities utils = new Utilities();
    MediaPlayer mPlayer;
    ImageView imgNavigationToSource;
    String crt_lat = "", crt_lng = "";
    boolean isRunning = false, timerCompleted = false;
    TextView destination;
    ConnectionHelper helper;
    LinearLayout destinationLayer;
    View view;
    boolean doubleBackToExitPressedOnce = false;
    //Animation
    Animation slide_down, slide_up;
    //Distance calculation
    Intent service_intent;
    TextView  active_Status;
    boolean scheduleTrip = false;
    boolean currFragment = true;
    String type = null, datas = null;
    String getStatusVariable;
    CircleImageView img_profile;
    TextView txtTotalEarning , user_name , user_type, user_total_ride_distanse;
    CardView total_earn_layout;
    ProgressDialog pDialog;
    String uploadTag;
    String providerId="";
    String userID="";
    String userFirstName="";

    ArrayList<DocListItemModel> documentItemArrayList;
    String docIds = "";
    String adapterPosi = "";
    DocListAdapter docListAdapter;
    ImageView imgTest;
    Button btMotorInspectCertificate, btPsvInsurenceCertificate, btGoodConduct,
            btPSVLicense, btpersonalId, btPersonalPic;
    ImageView imgPersonal, imgPersonalId, imgPSVLicense, imgGoodConduct,
            imgMotorInspectCertificate, imgPsvInsurenceCertificate;
    private String token;
    //map variable

    private GoogleMap mMap;
    private double srcLatitude = 0;
    private double srcLongitude = 0;
    private double destLatitude = 0;
    private double destLongitude = 0;
    private LatLng sourceLatLng;
    private LatLng destLatLng;
    private LatLng currentLatLng;
    private String bookingId;
    private String address;
    private String daddress;
    private User user = new User();
    private ImageView sos;
    //Button layout
    private CustomDialog customDialog;
    private Object previous_request_id = " ";
    private String count;
    private JSONArray statusResponses;
    private String feedBackRating;
    private String feedBackComment;
    private android.app.AlertDialog Waintingdialog;
    private android.app.AlertDialog imageShowDialog;
    private String first = "", boring_depth = "";
    private String fileExt = "";
    private byte[] b, b1, b2;
    private File file;
    private Bitmap bmp = null;
    private  String earning = "";
    Switch online_offline_switch;
    RelativeLayout offline_layout;
    private Uri cameraImageUri = null;

    TextView tvCommision,tvEarning,tvTrips;
    public DriverMapFragment() {

    }

    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        assert parcelFileDescriptor != null;
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
        int maxSize = Math.min(deviceHeight, deviceWidth);
        if (image != null) {
            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
            int inWidth = image.getWidth();
            int inHeight = image.getHeight();
            int outWidth;
            int outHeight;
            if (inWidth > inHeight) {
                outWidth = maxSize;
                outHeight = (inHeight * maxSize) / inWidth;
            } else {
                outHeight = maxSize;
                outWidth = (inWidth * maxSize) / inHeight;
            }
            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
        } else {
            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
            return null;
        }

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Bundle bundle = getArguments();

        if (bundle != null) {
            push = bundle.getBoolean("push");
        }

        if (push) {
            isRunning = false;
        }

        Intent i = getActivity().getIntent();
        type = i.getStringExtra("type");
        datas = i.getStringExtra("datas");
        if (type != null) {
            checkStatusSchedule();
        } else {
            checkStatus();
        }

        Log.e(TAG, "TYPE: " + type);
        Log.e(TAG, "DATAS: " + datas);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_map, container, false);
        }
        findViewById(view);
        if (activity == null) {
            activity = getActivity();
        }
        service_intent = new Intent(getActivity(), LocationTracking.class);
        if (context == null) {
            context = getContext();
        }

        token = SharedHelper.getKey(activity, "access_token");
        helper = new ConnectionHelper(context);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        deviceHeight = displayMetrics.heightPixels;
        deviceWidth = displayMetrics.widthPixels;
        customDialog = new CustomDialog(getActivity());
        customDialog.setCancelable(false);
        customDialog.show();
        //permission to access location
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Android M Permission check
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else {

            setUpMapIfNeeded();
            MapsInitializer.initialize(activity);
        }


//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
//                mMessageReceiver, new IntentFilter("statusresponse"));
        ha = new Handler();

        if (type != null) {
            checkStatusSchedule();
        } else {
            checkStatus();
        }

        //check status every 3 sec
        ha.postDelayed(new Runnable() {
            @Override
            public void run() {
                //call function

                if (type != null) {
                    checkStatusSchedule();
                } else {
//                    checkStatus();
                }

                ha.postDelayed(this, 3000);
            }
        }, 3000);

        btn_01_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(CurrentStatus, request_id);
            }
        });
        btn_confirm_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(CurrentStatus, request_id);
            }
        });

        btn_rate_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                update(CurrentStatus, request_id);
            }
        });

        btn_go_offline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                update(CurrentStatus, request_id);
            }
        });
        btn_go_online.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                goOnline();
            }
        });

        errorLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        imgCurrentLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Double crtLat, crtLng;
                if (!crt_lat.equalsIgnoreCase("") && !crt_lng.equalsIgnoreCase("")) {
                    crtLat = Double.parseDouble(crt_lat);
                    crtLng = Double.parseDouble(crt_lng);
                    if (crtLat != null && crtLng != null) {
                        LatLng loc = new LatLng(crtLat, crtLng);
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(14).build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                    }
                }
            }
        });

        btn_02_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }
                handleIncomingRequest("Accept", request_id);
            }
        });


        btn_02_reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                countDownTimer.cancel();
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }
                handleIncomingRequest("Reject", request_id);
            }
        });

        btn_cancel_ride.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCancelDialog();
            }
        });

        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (NAV_DRAWER == 0) {
                    drawer.openDrawer(Gravity.LEFT);
                } else {
                    NAV_DRAWER = 0;
                    drawer.closeDrawers();
                }
            }
        });

        img_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentChat = new Intent(context, UserChatActivity.class);
                intentChat.putExtra("requestId", request_id);
                intentChat.putExtra("providerId", providerId);
                intentChat.putExtra("userId", userID);
                intentChat.putExtra("userName", userFirstName);
                context.startActivity(intentChat);
            }
        });
        img03Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobile = SharedHelper.getKey(context, "provider_mobile_no");
                if (mobile != null && !mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                        startActivity(intent);
                    }
                } else {
                    displayMessage(getString(R.string.user_no_mobile));
                }
            }
        });

        imgNavigationToSource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String url = "http://maps.google.com/maps?"
                        + "saddr=" + address
                        + "&daddr=" + daddress;
                Log.e("url", url + "url");
//                Intent intent = new Intent(getActivity(), TrackActivity.class);
//                intent.putExtra("address", url);
//                startActivity(intent);
                if (btn_01_status.getText().toString().equalsIgnoreCase("ARRIVED")) {
                    Uri naviUri = Uri.parse("http://maps.google.com/maps?"
                            + "saddr=" + crt_lat + "," + crt_lng
                            + "&daddr=" + srcLatitude + "," + srcLongitude);

                    Intent intent = new Intent(Intent.ACTION_VIEW, naviUri);
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                } else {
                    Uri naviUri2 = Uri.parse("http://maps.google.com/maps?"
                            + "saddr=" + srcLatitude + "," + srcLongitude
                            + "&daddr=" + destLatitude + "," + destLongitude);

                    Intent intent = new Intent(Intent.ACTION_VIEW, naviUri2);
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    startActivity(intent);
                }
            }
        });

        online_offline_switch.setChecked(true);
        active_Status.setText("Online");
        online_offline_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if(isChecked){

                    active_Status.setText("Online");
                    offline_layout.setVisibility(View.GONE);
                    goOnline();

                }else {

                    active_Status.setText("Offline");
                    offline_layout.setVisibility(View.VISIBLE);
                    update(CurrentStatus, request_id);

                }
            }
        });



        statusCheck();
        return view;
    }

    public void statusCheck() {
        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            enableLoc();
        }
    }

    private void enableLoc() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
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
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult connectionResult) {

                        utils.print("Location error", "Location error " + connectionResult.getErrorCode());
                    }
                }).build();
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
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                            e.printStackTrace();
                        }
                        break;
                }
            }
        });
//	        }

    }

    private void findViewById(View view) {
        //Menu Icon
        menuIcon = view.findViewById(R.id.menuIcon);
        imgCurrentLoc = view.findViewById(R.id.imgCurrentLoc);
        drawer = activity.findViewById(R.id.drawer_layout);

        //map layer
        ll_01_mapLayer = view.findViewById(R.id.ll_01_mapLayer);
        driverArrived = view.findViewById(R.id.driverArrived);
        driverPicked = view.findViewById(R.id.driverPicked);
        driveraccepted = view.findViewById(R.id.driveraccepted);


        tvTrips = view.findViewById(R.id.tvTrips);
        tvCommision = view.findViewById(R.id.tvCommision);
        tvEarning  =  view.findViewById(R.id.tvEarning);
        //Button layer 01
        btn_01_status = view.findViewById(R.id.btn_01_status);
        btn_rate_submit = view.findViewById(R.id.btn_rate_submit);
        btn_confirm_payment = view.findViewById(R.id.btn_confirm_payment);
        img_profile = view.findViewById(R.id.img_profile);
        txtTotalEarning = view.findViewById(R.id.txtTotalEarning);
        total_earn_layout = view.findViewById(R.id.total_earn_layout);
        //Button layer 02
        btn_02_accept = view.findViewById(R.id.btn_02_accept);
        btn_02_reject = view.findViewById(R.id.btn_02_reject);
        btn_cancel_ride = view.findViewById(R.id.btn_cancel_ride);
        btn_go_offline = view.findViewById(R.id.btn_go_offline);
        btn_go_online = view.findViewById(R.id.btn_go_online);
        activeStatus = view.findViewById(R.id.activeStatus);
        offline_layout = view.findViewById(R.id.offline_layout);
//        Button btn_tap_when_arrived, btn_tap_when_pickedup,btn_tap_when_dropped,  btn_tap_when_paid, btn_rate_user
        //content layer
        ll_01_contentLayer_accept_or_reject_now = view.findViewById(R.id.ll_01_contentLayer_accept_or_reject_now);
        ll_02_contentLayer_accept_or_reject_later = view.findViewById(R.id.ll_02_contentLayer_accept_or_reject_later);
        ll_03_contentLayer_service_flow = view.findViewById(R.id.ll_03_contentLayer_service_flow);
        ll_04_contentLayer_payment = view.findViewById(R.id.ll_04_contentLayer_payment);
        ll_05_contentLayer_feedback = view.findViewById(R.id.ll_05_contentLayer_feedback);
        lnrGoOffline = view.findViewById(R.id.lnrGoOffline);
        layoutinfo = view.findViewById(R.id.layoutinfo);
        imgNavigationToSource = view.findViewById(R.id.imgNavigationToSource);

        lnrNotApproved = view.findViewById(R.id.lnrNotApproved);

        //content layout 01
        txt01Pickup = view.findViewById(R.id.txtPickup);
        txtDropOff = view.findViewById(R.id.txtDropOff);
        txt01Timer = view.findViewById(R.id.txt01Timer);
        img01User = view.findViewById(R.id.img01User);
        txt01UserName = view.findViewById(R.id.txt01UserName);
        tvDistance = view.findViewById(R.id.tvDistance);
        txtSchedule = view.findViewById(R.id.txtSchedule);
        rat01UserRating = view.findViewById(R.id.rat01UserRating);
        sos = view.findViewById(R.id.sos);
//        navigate=(ImageView)view.findViewById(R.id.navigate);
        LayerDrawable drawable = (LayerDrawable) rat01UserRating.getProgressDrawable();
        drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);

        //content layer 02
        img02User = view.findViewById(R.id.img02User);
        txt02UserName = view.findViewById(R.id.txt02UserName);
        rat02UserRating = view.findViewById(R.id.rat02UserRating);
        LayerDrawable stars02 = (LayerDrawable) rat02UserRating.getProgressDrawable();
        stars02.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
        txt02ScheduledTime = view.findViewById(R.id.txt02ScheduledTime);
//        lblDistanceTravelled = view.findViewById(R.id.lblDistanceTravelled);
        txt02From = view.findViewById(R.id.txt02From);
        txt02To = view.findViewById(R.id.txt02To);

        //content layer 03
        img03User = view.findViewById(R.id.img03User);
        txt03UserName = view.findViewById(R.id.txt03UserName);
        lblCmfrmDestAddress = view.findViewById(R.id.lblCmfrmDestAddress);
        lblCmfrmSourceAddress = view.findViewById(R.id.lblCmfrmSourceAddress);
        rat03UserRating = view.findViewById(R.id.rat03UserRating);
        LayerDrawable drawable_02 = (LayerDrawable) rat03UserRating.getProgressDrawable();
        drawable_02.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        drawable_02.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
        drawable_02.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
        img03Call = view.findViewById(R.id.img03Call);
        img_chat = view.findViewById(R.id.img_chat);
        img03Status1 = view.findViewById(R.id.img03Status1);
        img03Status2 = view.findViewById(R.id.img03Status2);
        img03Status3 = view.findViewById(R.id.img03Status3);

        //content layer 04
        txt04InvoiceId = view.findViewById(R.id.invoice_txt);
        txtTotal = view.findViewById(R.id.txtTotal);
        txt04BasePrice = view.findViewById(R.id.txt04BasePrice);
        txt04Distance = view.findViewById(R.id.txt04Distance);
        txt04Tax = view.findViewById(R.id.txt04Tax);
        txt04Total = view.findViewById(R.id.txt04Total);
        txt04PaymentMode = view.findViewById(R.id.txt04PaymentMode);
        txt04Commision = view.findViewById(R.id.txt04Commision);
        destination = view.findViewById(R.id.destination);
        lblProviderName = view.findViewById(R.id.lblProviderName);
        paymentTypeImg = view.findViewById(R.id.paymentTypeImg);
        errorLayout = view.findViewById(R.id.lnrErrorLayout);
        destinationLayer = view.findViewById(R.id.destinationLayer);

        //content layer 05
        img05User = view.findViewById(R.id.img05User);
        rat05UserRating = view.findViewById(R.id.rat05UserRating);
        user_name = view.findViewById(R.id.user_name);
        user_type = view.findViewById(R.id.user_type);
        user_total_ride_distanse = view.findViewById(R.id.user_total_ride_distanse);




        // new Design

        online_offline_switch = view.findViewById(R.id.online_offline_switch);
        active_Status = view.findViewById(R.id.active_Status);


        LayerDrawable stars05 = (LayerDrawable) rat05UserRating.getProgressDrawable();
        stars05.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
        stars05.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
        stars05.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
        edt05Comment = view.findViewById(R.id.edt05Comment);

        topSrcDestTxtLbl = view.findViewById(R.id.src_dest_txt);

        earning  = SharedHelper.getKey(getActivity(),"totalearning");
        total_earn_layout.setVisibility(View.VISIBLE);
        if(earning!=null && !earning.isEmpty() && !earning.equals("")){

            txtTotalEarning.setText(earning);
            //total_earn_layout.setVisibility(View.VISIBLE);
        }else
            txtTotalEarning.setText(SharedHelper.getKey(getActivity(),"currency")+" 0.00");
        // total_earn_layout.setVisibility(View.GONE);
        //Load animation
        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);

        user_name.setText(SharedHelper.getKey(getActivity(),"first_name"));
        user_type.setText(SharedHelper.getKey(getActivity(), "service"));
        // user_total_ride_distanse.setText(SharedHelper.getKey(getActivity(), "service") + "Km");

        Picasso.get().load(SharedHelper.getKey(context, "picture")).placeholder(R.drawable.ic_dummy_user)
                .error(R.drawable.ic_dummy_user)
                .into(img_profile);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Profile.class));
            }
        });
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() != KeyEvent.ACTION_DOWN)
                    return true;

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (doubleBackToExitPressedOnce) {
                        getActivity().finish();
                        return false;
                    }

                    doubleBackToExitPressedOnce = true;
                    Toast.makeText(getActivity(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                    new Handler().postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            doubleBackToExitPressedOnce = false;
                        }
                    }, 5000);
                    return true;
                }
                return false;
            }
        });

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSosDialog();
            }
        });
//        navigate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                redirectMap(sourceLatLng.latitude+"",sourceLatLng.longitude+"",destLatLng.latitude+"", destLatLng.longitude+"");
//            }
//        });

        destinationLayer.setOnClickListener(this);
        ll_01_contentLayer_accept_or_reject_now.setOnClickListener(this);
        ll_03_contentLayer_service_flow.setOnClickListener(this);
        ll_04_contentLayer_payment.setOnClickListener(this);
        ll_05_contentLayer_feedback.setOnClickListener(this);
        lnrGoOffline.setOnClickListener(this);
        errorLayout.setOnClickListener(this);

        lnrGoOffline.setVisibility(View.GONE);
    }

    //    public void redirectMap(String lat1,String lng1,String lat2,String lng2)
//    {
//        String urls="http://maps.google.com/maps?saddr="+lat1+","+lng1+"&daddr="+lat2+","+lng2;
////        String urls="http://maps.google.com/maps?saddr="+source_address+"&daddr="+dest_address;
//        Log.e("urls",urls+"urls");
//        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                Uri.parse(urls));
//        startActivity(intent);
//    }
    private void mapClear() {
        if (mMap != null) {
            if (parserTask != null) {
                parserTask.cancel(true);
                parserTask = null;
            }

            if (!crt_lat.equalsIgnoreCase("") && !crt_lat.equalsIgnoreCase("")) {
                LatLng myLocation = new LatLng(Double.parseDouble(crt_lat), Double.parseDouble(crt_lng));
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            mMap.clear();
            srcLatitude = 0;
            srcLongitude = 0;
            destLatitude = 0;
            destLongitude = 0;
        }
    }

    public void clearVisibility() {

        if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.VISIBLE) {
            ll_01_contentLayer_accept_or_reject_now.startAnimation(slide_down);
        } else if (ll_02_contentLayer_accept_or_reject_later.getVisibility() == View.VISIBLE) {
            ll_02_contentLayer_accept_or_reject_later.startAnimation(slide_down);
        } else if (ll_03_contentLayer_service_flow.getVisibility() == View.VISIBLE) {
            //ll_03_contentLayer_service_flow.startAnimation(slide_down);
        } else if (ll_04_contentLayer_payment.getVisibility() == View.VISIBLE) {
            ll_04_contentLayer_payment.startAnimation(slide_down);
        } else if (ll_04_contentLayer_payment.getVisibility() == View.VISIBLE) {
            ll_04_contentLayer_payment.startAnimation(slide_down);
        } else if (ll_05_contentLayer_feedback.getVisibility() == View.VISIBLE) {
            ll_05_contentLayer_feedback.startAnimation(slide_down);
        }

        ll_01_contentLayer_accept_or_reject_now.setVisibility(View.GONE);
        ll_02_contentLayer_accept_or_reject_later.setVisibility(View.GONE);
        ll_03_contentLayer_service_flow.setVisibility(View.GONE);
        ll_04_contentLayer_payment.setVisibility(View.GONE);
        ll_05_contentLayer_feedback.setVisibility(View.GONE);
        lnrGoOffline.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        setUpMapIfNeeded();
                        MapsInitializer.initialize(activity);

                        if (ContextCompat.checkSelfPermission(context,
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED) {

                            if (mGoogleApiClient == null) {
                                buildGoogleApiClient();
                            }
                            setUpMapIfNeeded();
                            MapsInitializer.initialize(activity);

                        }
                    } else {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                    }
                }
                break;
            case 2:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
                        startActivity(intent);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
                    }
                }
                break;
            case 3:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
                        startActivity(intent);
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void setUpMapIfNeeded() {


        if (mMap == null) {
            FragmentManager fm = getChildFragmentManager();
            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
            mapFragment.getMapAsync(this);
        }
        if (mMap != null) {
            setupMap();
        }
    }

    private void setSourceLocationOnMap(LatLng latLng) {

        if (latLng != null) {
            mMap.clear();
            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
            MarkerOptions options = new MarkerOptions().position(latLng).anchor(0.5f, 0.75f);
            options.position(latLng).isDraggable();
            mMap.addMarker(options);
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }

    private void setPickupLocationOnMap() {
        if (mMap != null) {
            mMap.clear();
        }
        sourceLatLng = currentLatLng;
        destLatLng = new LatLng(srcLatitude, srcLongitude);
        CameraPosition cameraPosition = new CameraPosition.Builder().target(destLatLng).zoom(15).build();
        MarkerOptions options = new MarkerOptions();
        options.anchor(0.5f, 0.5f).position(destLatLng).isDraggable();
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        if (sourceLatLng != null && destLatLng != null) {
            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.execute(url);
        }
    }

    private void setDestinationLocationOnMap() {
        if (currentLatLng != null) {
            sourceLatLng = currentLatLng;
            destLatLng = new LatLng(destLatitude, destLongitude);
            //CameraPosition cameraPosition = new CameraPosition.Builder().target(destLatLng).zoom(16).build();
       /* MarkerOptions options = new MarkerOptions();
        options.position(destLatLng).isDraggable();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
       /* DownloadTask downloadTask = new DownloadTask();
        // Start downloading json data from Google Directions API
        downloadTask.execute(url);*/
            FetchUrl fetchUrl = new FetchUrl();
            fetchUrl.execute(url);
        }
    }

    @SuppressWarnings("MissingPermission")
    private void setupMap() {

        mMap.setMyLocationEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.setOnCameraMoveListener(this);

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            activity, R.raw.style_json));

            if (!success) {
                Log.e("DriverMapFragment:Style", "Style parsing failed.");
            } else {
                Log.e("DriverMapFragment:Style", "Style Applied.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("DriverMapFragment:Style", "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        // do other tasks here
        setupMap();


        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                buildGoogleApiClient();
//                mMap.setMyLocationEnabled(true);
            } else {
                //Request Location Permission
                checkLocationPermission();
            }
        } else {
            buildGoogleApiClient();
//            mMap.setMyLocationEnabled(true);
        }
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(context)
                        .setTitle("Location Permission Needed")
                        .setMessage("This app needs the Location permission, please accept to use location functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        1);
                            }
                        })
                        .create()
                        .show();
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        1);
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setFastestInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        //GPSTracker gps = new GPSTracker(getActivity());
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
        if (mMap != null) {
            if (currentMarker != null) {
                currentMarker.remove();
            }

            MarkerOptions markerOptions1 = new MarkerOptions()
                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
                    .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_map_current_location));
            currentMarker = mMap.addMarker(markerOptions1);

            Log.e("DriverSide", "DriveronLocationChanged: " + location.getLatitude());
            Log.e("DriverSide", "DriveronLocationChanged: " + location.getLongitude());

            if (value == 0) {
                myLat = String.valueOf(location.getLatitude());
                myLng = String.valueOf(location.getLongitude());

                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(myLocation).anchor(0.5f, 0.75f);
                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                value++;
            }

            crt_lat = String.valueOf(location.getLatitude());
            Log.e(TAG, "crt_lat" + crt_lat);
            crt_lng = String.valueOf(location.getLongitude());
            Log.e(TAG, "crt_lng" + crt_lng);
            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());

            if (type != null) {
                checkStatusSchedule();
            } else {
                checkStatus();
            }

        }



    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCameraMove() {
        utils.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
        if (currentMarker != null) {
            if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(currentMarker.getPosition())) {
                utils.print("Current marker", "Current Marker is not visible");
                if (imgCurrentLoc.getVisibility() == View.GONE) {
                    imgCurrentLoc.setVisibility(View.VISIBLE);
                }
            } else {
                utils.print("Current marker", "Current Marker is visible");
                if (imgCurrentLoc.getVisibility() == View.VISIBLE) {
                    imgCurrentLoc.setVisibility(View.GONE);
                }
                if (mMap.getCameraPosition().zoom < 15.0f) {
                    if (imgCurrentLoc.getVisibility() == View.GONE) {
                        imgCurrentLoc.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();
            Log.d("downloadUrl", data);
            br.close();

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude) {

        // Origin of route
        String str_origin = "origin=" + source_latitude + "," + source_longitude;

        // Destination of route
        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;


        // Sensor enabled
        String sensor = "sensor=false" + "&key=" + "AIzaSyDYbRQKyMGHPP-hh1jOTaPv3f3jSXQScUg";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        Log.v("directionUrl",url+" ");
        return url;
    }

    private void checkDocumentStatus() {
        try {

            if (helper.isConnectingToInternet()) {
                String url = URLHelper.CHECK_DOCUMENT;
                utils.print("Destination Current Lat", "" + crt_lat);
                final JsonObjectRequest jsonObjectRequest = new
                        JsonObjectRequest(Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.e("checkDocumentStatus", response + "Document");
                                        try {
                                            if (response.getString("status").equalsIgnoreCase("0")) {

                                                JSONArray jsonArray = response.getJSONArray("data");
                                                for (int i = 0; i < jsonArray.length(); i++) {
                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                                    SharedHelper.putKey(getActivity(), jsonObject.optString("name"),
                                                            jsonObject.optString("id"));
                                                }
                                                if (Waintingdialog == null) {

                                                    if(docopen.equalsIgnoreCase("")) {
                                                        docopen = "yes";
                                                        Intent intent1 = new Intent(activity, DocUploadActivity.class);
                                                        activity.startActivity(intent1);
                                                    }
//                                                    documentUploadFirstDialog();
                                                }
                                            } else {
//                                                if (isMyServiceRunning(StatusCheckServie.class)) {
//                                                    activity.stopService(new Intent(activity, StatusCheckServie.class));
                                                ha.removeMessages(0);
                                                lnrGoOffline.setVisibility(View.GONE);
                                                lnrNotApproved.setVisibility(View.VISIBLE);

//                                                    Intent intent1 = new Intent(activity, DocumentUpload.class);
//                                                    Intent intent1 = new Intent(activity, WaitingForApproval.class);
//                                                    intent1.putExtra("account_status", "account_status_new");
//                                                    activity.startActivity(intent1);
//                                                    activity.finish();
//                                                }
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                utils.print("Error", error.toString());

                            }
                        }) {
                            @Override
                            public java.util.Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("X-Requested-With", "XMLHttpRequest");
                                headers.put("Authorization", "Bearer " + token);
                                return headers;
                            }
                        };
                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                displayMessage(getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkStatus() {
        try {

            if (helper.isConnectingToInternet()) {
                String url = URLHelper.base + "api/provider/trip?latitude=" + crt_lat + "&longitude=" + crt_lng;

                utils.print("Destination Current Lat", "" + crt_lat);
                utils.print("Destination Current Lng", "" + crt_lng);
                Log.i(TAG, "checkStatus url : " + url);

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {
                        if (errorLayout.getVisibility() == View.VISIBLE) {
                            errorLayout.setVisibility(View.GONE);
                        }
                        Log.e("CheckStatus", "" + response.toString());
                        //SharedHelper.putKey(context, "currency", response.optString("currency"));
                        try {
                            if (response.optString("service_status").equalsIgnoreCase("offline")) {

                                online_offline_switch.setChecked(false);
                                active_Status.setText("Offline");
                                offline_layout.setVisibility(View.VISIBLE);
//                                update(CurrentStatus, request_id);
//                               if (isAdded())activeStatus.setText(getActivity().getString(R.string.offline));

                            }
                            try {
                                tvCommision.setText(response.optString("commision"));
                                tvEarning.setText(response.optString("earnings"));
                                tvTrips.setText(response.optString("trips"));
                                txtTotalEarning.setText(SharedHelper.getKey(getActivity(), "currency") + response.optString("earnings"));
                            }catch (Exception e)
                            {
                                e.printStackTrace();
                            }
                            if (response.optJSONArray("requests").length() > 0) {

                                providerId=response.optJSONArray("requests")
                                        .getJSONObject(0).optJSONObject("request")
                                        .optString("provider_id");
                                userID=response.optJSONArray("requests")
                                        .getJSONObject(0).optJSONObject("request")
                                        .optString("user_id");

                                JSONObject jsonObject = response.optJSONArray("requests")
                                        .getJSONObject(0).optJSONObject("request").optJSONObject("user");
                                userFirstName=jsonObject.optString("first_name");
                                user.setFirstName(jsonObject.optString("first_name"));
//                                user.setLastName(jsonObject.optString("last_name"));
                                user.setEmail(jsonObject.optString("email"));
                                if (jsonObject.optString("picture").startsWith("http"))
                                    user.setImg(jsonObject.optString("picture"));
                                else
                                    user.setImg(URLHelper.base + "storage/app/public/" + jsonObject.optString("picture"));
                                user.setRating(jsonObject.optString("rating"));
                                user.setMobile(jsonObject.optString("mobile"));
                                bookingId = response.optJSONArray("requests").getJSONObject(0)
                                        .optJSONObject("request").optString("booking_id");
                                address = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("s_address");
                                daddress = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("d_address");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (response.optString("account_status").equals("new") ||
                                response.optString("account_status").equals("onboarding")) {
                            ha.removeMessages(0);
                            checkDocumentStatus();
                        } else {

                            if (response.optString("service_status").equals("offline")) {
                                ha.removeMessages(0);
//                    Intent intent = new Intent(activity, Offline.class);
//                    activity.startActivity(intent);
                                goOffline();
                            } else {

                                if (response.optJSONArray("requests") != null && response.optJSONArray("requests").length() > 0) {
                                    JSONObject statusResponse = null;
                                    try {
                                        statusResponses = response.optJSONArray("requests");
                                        statusResponse = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request");
                                        request_id = response.optJSONArray("requests").getJSONObject(0).optString("request_id");
                                        Log.e("request_idjson", request_id + "");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                        lblDistanceTravelled.setText("Distance Travelled :"
//                                                + String.format("%f", Float.parseFloat(LocationTracking.distance * 0.001 + "")) + " Km");
                                    }
                                    if ((statusResponse != null) && (request_id != null)) {
                                        if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" ")) && mMap != null) {
                                            previous_request_id = request_id;
                                            srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
                                            srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));

                                            destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
                                            destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
                                            //noinspection deprecation
                                            setSourceLocationOnMap(currentLatLng);
                                            setPickupLocationOnMap();
                                            sos.setVisibility(View.GONE);

                                        }
                                        utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
//                                        String ok = "ok";
//                                        if (ok.equals(ok))
                                        if (!PreviousStatus.equals(statusResponse.optString("status"))) {
//                                            || statusResponse.optString("paid").equals("1") || statusResponse.optString("paid").equals("0")
                                            PreviousStatus = statusResponse.optString("status");
                                            clearVisibility();
                                            utils.print("responseObj(" + request_id + ")", statusResponse.toString());
                                            utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
                                            if (!statusResponse.optString("status").equals("SEARCHING")) {
                                                timerCompleted = false;
                                                if (mPlayer != null && mPlayer.isPlaying()) {
                                                    mPlayer.stop();
                                                    mPlayer = null;
                                                    countDownTimer.cancel();
                                                }
                                            }
                                            if (statusResponse.optString("status").equals("SEARCHING")) {
                                                scheduleTrip = false;
                                                if (!timerCompleted) {
                                                    setValuesTo_ll_01_contentLayer_accept_or_reject_now(statusResponses);
                                                    if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.GONE) {
                                                        ll_01_contentLayer_accept_or_reject_now.startAnimation(slide_up);
                                                    }
                                                    ll_01_contentLayer_accept_or_reject_now.setVisibility(View.VISIBLE);
                                                }
                                                CurrentStatus = "STARTED";
                                            } else if (statusResponse.optString("status").equals("STARTED")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses,response);
                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
                                                }
                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
                                                btn_01_status.setText("DÉBUT DE COURSE");
                                                CurrentStatus = "ARRIVED";
                                                sos.setVisibility(View.GONE);
                                                if (srcLatitude == 0 && srcLongitude == 0 && destLatitude == 0 && destLongitude == 0) {
                                                    mapClear();
                                                    srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
                                                    srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
                                                    destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
                                                    destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
                                                    //noinspection deprecation
                                                    //
                                                    setSourceLocationOnMap(currentLatLng);
                                                    setPickupLocationOnMap();
                                                }
                                                sos.setVisibility(View.GONE);
                                                btn_cancel_ride.setVisibility(View.VISIBLE);
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                layoutinfo.setVisibility(View.GONE);
                                                String address = statusResponse.optString("s_address");
                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
                                                    destination.setText(address);
                                                else
                                                    destination.setText(getAddress(statusResponse.optString("s_latitude"),
                                                            statusResponse.optString("s_longitude")));
                                                topSrcDestTxtLbl.setText("Pick up Location");


                                            } else if (statusResponse.optString("status").equals("ARRIVED")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses,response);
                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
                                                }
                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
                                                btn_01_status.setText("RAMASSAGE");
                                                sos.setVisibility(View.GONE);
                                                img03Status1.setImageResource(R.drawable.arrived_select);
                                                img03Status2.setImageResource(R.drawable.pickeddisable);
                                                driveraccepted.setVisibility(View.VISIBLE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.GONE);
                                                CurrentStatus = "PICKEDUP";
                                                driveraccepted.setVisibility(View.GONE);
                                                driverArrived.setVisibility(View.VISIBLE);
                                                driverPicked.setVisibility(View.GONE);

                                                btn_cancel_ride.setVisibility(View.VISIBLE);
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                String address = statusResponse.optString("d_address");
                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
                                                    destination.setText(address);
                                                else
                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
                                                            statusResponse.optString("d_longitude")));
                                                topSrcDestTxtLbl.setText("Drop Location");


                                            } else if (statusResponse.optString("status").equals("PICKEDUP")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses,response);
                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
                                                }
                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
                                                btn_01_status.setText("FIN DE COURSE");
                                                sos.setVisibility(View.VISIBLE);
//                                                navigate.setVisibility(View.VISIBLE);
                                                img03Status1.setImageResource(R.drawable.arrived_select);
                                                img03Status2.setImageResource(R.drawable.pickup_select);
                                                driveraccepted.setVisibility(View.GONE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.VISIBLE);
                                                CurrentStatus = "DROPPED";
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                layoutinfo.setVisibility(View.GONE);
                                                btn_cancel_ride.setVisibility(View.GONE);
                                                String address = statusResponse.optString("d_address");
                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
                                                    destination.setText(address);
                                                else
                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
                                                            statusResponse.optString("d_longitude")));
                                                topSrcDestTxtLbl.setText("Drop Location");

                                                mapClear();
                                                srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
                                                srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
                                                destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
                                                destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
                                                //noinspection deprecation
                                                //
                                                setSourceLocationOnMap(currentLatLng);
                                                setPickupLocationOnMap();




                                            } else if (statusResponse.optString("status").equals("DROPPED")
                                                    && statusResponse.optString("paid").equals("0")) {
                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
                                                }
                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
                                                img03Status1.setImageResource(R.drawable.arriveddisable);
                                                img03Status2.setImageResource(R.drawable.pickeddisable);
                                                driveraccepted.setVisibility(View.VISIBLE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.GONE);

                                                btn_01_status.setText("CONFIRM PAYMENT");
                                                sos.setVisibility(View.VISIBLE);
//                                                navigate.setVisibility(View.GONE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                CurrentStatus = "COMPLETED";

                                                LocationTracking.distance = 0.0f;
                                            } else if (statusResponse.optString("status").equals("DROPPED")
                                                    && statusResponse.optString("paid").equals("0")) {
                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
                                                }
                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
                                                img03Status1.setImageResource(R.drawable.arriveddisable);
                                                img03Status2.setImageResource(R.drawable.pickeddisable);
                                                driveraccepted.setVisibility(View.VISIBLE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.GONE);

                                                btn_01_status.setText("CONFIRM PAYMENT");
                                                sos.setVisibility(View.VISIBLE);
//                                                navigate.setVisibility(View.GONE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                CurrentStatus = "COMPLETED";

                                                LocationTracking.distance = 0.0f;
                                            } else if (statusResponse.optString("status").equals("COMPLETED")
                                                    && statusResponse.optString("paid").equals("0")) {

                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
                                                }
                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
                                                img03Status1.setImageResource(R.drawable.arriveddisable);
                                                img03Status2.setImageResource(R.drawable.pickeddisable);
                                                driveraccepted.setVisibility(View.VISIBLE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.GONE);

                                                btn_01_status.setText("CONFIRM PAYMENT");
                                                sos.setVisibility(View.VISIBLE);
//                                                navigate.setVisibility(View.GONE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                CurrentStatus = "COMPLETED";

                                                LocationTracking.distance = 0.0f;
                                            } else if (statusResponse.optString("status").equals("COMPLETED")
                                                    && statusResponse.optString("paid").equals("1")) {
//                                                ok = "not";
                                                if (ll_04_contentLayer_payment.getVisibility() == View.VISIBLE) {
                                                    ll_04_contentLayer_payment.setVisibility(View.GONE);
                                                }

                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
                                                if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
                                                    ll_05_contentLayer_feedback.startAnimation(slide_up);
                                                }
                                                ll_04_contentLayer_payment.setVisibility(View.GONE);
                                                edt05Comment.setText("");
                                                ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
                                                sos.setVisibility(View.GONE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                btn_01_status.setText("SUBMIT");
                                                CurrentStatus = "RATE";

                                                LocationTracking.distance = 0.0f;
                                            } else if (statusResponse.optString("status").equals("SCHEDULED")) {
                                                if (mMap != null) {
                                                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    mMap.clear();
                                                }
                                                clearVisibility();
                                                CurrentStatus = "SCHEDULED";
                                                utils.print("statusResponse", "null");
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);

                                                LocationTracking.distance = 0.0f;
                                            }
                                        }
                                    } else {
                                        if (mMap != null) {
                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            timerCompleted = false;
                                            mMap.clear();
                                            if (mPlayer != null && mPlayer.isPlaying()) {
                                                mPlayer.stop();
                                                mPlayer = null;
                                                countDownTimer.cancel();
                                            }

                                        }

                                        LocationTracking.distance = 0.0f;

                                        clearVisibility();
                                        //lnrGoOffline.setVisibility(View.VISIBLE);
                                        destinationLayer.setVisibility(View.GONE);
                                        layoutinfo.setVisibility(View.VISIBLE);
                                        CurrentStatus = "ONLINE";
                                        PreviousStatus = "NULL";
                                        utils.print("statusResponse", "null");
                                    }

                                } else {
                                    timerCompleted = false;
                                    if (!PreviousStatus.equalsIgnoreCase("NULL")) {
                                        utils.print("response", "null");
                                        if (mMap != null) {
                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            mMap.clear();
                                        }
                                        if (mPlayer != null && mPlayer.isPlaying()) {
                                            mPlayer.stop();
                                            mPlayer = null;
                                            countDownTimer.cancel();
                                        }
                                        clearVisibility();
                                        //mapClear();
                                        lnrGoOffline.setVisibility(View.VISIBLE);
                                        destinationLayer.setVisibility(View.GONE);
                                        layoutinfo.setVisibility(View.VISIBLE);
                                        CurrentStatus = "ONLINE";
                                        PreviousStatus = "NULL";
                                        utils.print("statusResponse", "null");

                                        LocationTracking.distance = 0.0f;
                                    }

                                }
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());
                        //errorHandler(error);
                        timerCompleted = false;
                        mapClear();
                        clearVisibility();
                        CurrentStatus = "ONLINE";
                        PreviousStatus = "NULL";
                        //  lnrGoOffline.setVisibility(View.VISIBLE);
                        destinationLayer.setVisibility(View.GONE);
                        layoutinfo.setVisibility(View.VISIBLE);
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer = null;
                            countDownTimer.cancel();
                        }
                        displayMessage(error.toString());
//                        if (errorLayout.getVisibility() != View.VISIBLE) {
//                            errorLayout.setVisibility(View.VISIBLE);
//                            sos.setVisibility(View.GONE);
//                        }
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + token);
                        return headers;
                    }
                };
                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                displayMessage(getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkStatusSchedule() {

        try {

            if (helper.isConnectingToInternet()) {

                String url = URLHelper.base + "api/provider/trip?latitude=" + crt_lat + "&longitude=" + crt_lng;
                Log.e(TAG, "URL:" + url);

                utils.print("Destination Current Lat", "" + crt_lat);
                utils.print("Destination Current Lng", "" + crt_lng);

                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (errorLayout.getVisibility() == View.VISIBLE) {
                            errorLayout.setVisibility(View.GONE);
                        }
                        Log.e("Schedule CheckStatus", "" + response.toString());
                        //SharedHelper.putKey(context, "currency", response.optString("currency"));

                        if (response.optJSONArray("requests").length() > 0) {

                            try {
                                if (response.optJSONArray("requests").length() > 0) {
                                    JSONObject jsonObject = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optJSONObject("user");
                                    user.setFirstName(jsonObject.optString("first_name"));
//                                    user.setLastName(jsonObject.optString("last_name"));
                                    user.setEmail(jsonObject.optString("email"));
                                    if (jsonObject.optString("picture").startsWith("http"))
                                        user.setImg(jsonObject.optString("picture"));
                                    else
                                        user.setImg(URLHelper.base + "storage/app/public/" + jsonObject.optString("picture"));
                                    user.setRating(jsonObject.optString("rating"));
                                    user.setMobile(jsonObject.optString("mobile"));
                                    bookingId = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("booking_id");
                                    address = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("s_address");
                                    daddress = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("d_address");

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            if (response.optString("service_status").equals("offline")) {
                                ha.removeMessages(0);
//                    Intent intent = new Intent(activity, Offline.class);
//                    activity.startActivity(intent);
                                goOffline();
                            } else {

                                if (response.optJSONArray("requests") != null && response.optJSONArray("requests").length() > 0) {
                                    JSONObject statusResponse = null;
                                    try {
                                        statusResponses = response.optJSONArray("requests");
                                        statusResponse = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request");
                                        request_id = response.optJSONArray("requests").getJSONObject(0).optString("request_id");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                    if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                        lblDistanceTravelled.setText("Distance Travelled :"
//                                                + String.format("%f", Float.parseFloat(LocationTracking.distance * 0.001 + "")) + " Km");
                                    }
                                    if ((statusResponse != null) && (request_id != null)) {
                                        if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" ")) && mMap != null) {
                                            previous_request_id = request_id;
                                            srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
                                            srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
                                            destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
                                            destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
                                            //noinspection deprecation
                                            setSourceLocationOnMap(currentLatLng);
                                            setPickupLocationOnMap();
                                            sos.setVisibility(View.GONE);
                                        }
                                        utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
                                        if (!PreviousStatus.equals(statusResponse.optString("status"))) {
                                            PreviousStatus = statusResponse.optString("status");
                                            clearVisibility();
                                            utils.print("responseObj(" + request_id + ")", statusResponse.toString());
                                            utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
                                            if (!statusResponse.optString("status").equals("SEARCHING")) {
                                                timerCompleted = false;
                                                if (mPlayer != null && mPlayer.isPlaying()) {
                                                    mPlayer.stop();
                                                    mPlayer = null;
                                                    countDownTimer.cancel();
                                                }
                                            }
                                            if (statusResponse.optString("status").equals("SEARCHING")) {
                                                scheduleTrip = false;
                                                if (!timerCompleted) {
                                                    setValuesTo_ll_01_contentLayer_accept_or_reject_now(statusResponses);
                                                    if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.GONE) {
                                                        ll_01_contentLayer_accept_or_reject_now.startAnimation(slide_up);
                                                    }
                                                    ll_01_contentLayer_accept_or_reject_now.setVisibility(View.VISIBLE);
                                                }
                                                CurrentStatus = "STARTED";
                                            } else if (statusResponse.optString("status").equals("STARTED")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses,response);
                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
                                                }
                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
                                                btn_01_status.setText(getString(R.string.tap_when_arrived));
                                                CurrentStatus = "ARRIVED";
                                                sos.setVisibility(View.GONE);
                                                if (srcLatitude == 0 && srcLongitude == 0 && destLatitude == 0 && destLongitude == 0) {
                                                    mapClear();
                                                    srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
                                                    srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
                                                    destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
                                                    destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
                                                    //noinspection deprecation
                                                    //
                                                    setSourceLocationOnMap(currentLatLng);
                                                    setPickupLocationOnMap();
                                                }
                                                sos.setVisibility(View.GONE);
                                                btn_cancel_ride.setVisibility(View.VISIBLE);
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                String address = statusResponse.optString("s_address");
                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
                                                    destination.setText(address);
                                                else
                                                    destination.setText(getAddress(statusResponse.optString("s_latitude"),
                                                            statusResponse.optString("s_longitude")));
                                                topSrcDestTxtLbl.setText(getString(R.string.pick_up));


                                            } else if (statusResponse.optString("status").equals("ARRIVED")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses,response);
                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
                                                }
                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
                                                btn_01_status.setText(getString(R.string.tap_when_pickedup));
                                                sos.setVisibility(View.GONE);
                                                img03Status1.setImageResource(R.drawable.arrived_select);
                                                driveraccepted.setVisibility(View.GONE);
                                                driverArrived.setVisibility(View.VISIBLE);
                                                driverPicked.setVisibility(View.GONE);
                                                CurrentStatus = "PICKEDUP";

                                                btn_cancel_ride.setVisibility(View.VISIBLE);
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                String address = statusResponse.optString("d_address");
                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
                                                    destination.setText(address);
                                                else
                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
                                                            statusResponse.optString("d_longitude")));
                                                topSrcDestTxtLbl.setText(getString(R.string.drop_at));


                                            } else if (statusResponse.optString("status").equals("PICKEDUP")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses,response);
                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
                                                }
                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
                                                btn_01_status.setText(getString(R.string.tap_when_dropped));
                                                sos.setVisibility(View.VISIBLE);
//                                                navigate.setVisibility(View.VISIBLE);
                                                img03Status1.setImageResource(R.drawable.arrived_select);
                                                img03Status2.setImageResource(R.drawable.pickup_select);
                                                CurrentStatus = "DROPPED";
                                                driveraccepted.setVisibility(View.GONE);
                                                driverArrived.setVisibility(View.GONE);
                                                driverPicked.setVisibility(View.VISIBLE);
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                layoutinfo.setVisibility(View.GONE);
                                                btn_cancel_ride.setVisibility(View.GONE);
                                                String address = statusResponse.optString("d_address");
                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
                                                    destination.setText(address);
                                                else
                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
                                                            statusResponse.optString("d_longitude")));
                                                topSrcDestTxtLbl.setText(getString(R.string.drop_at));
//
                                                srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
                                                srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
                                                destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
                                                destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));

                                                setSourceLocationOnMap(currentLatLng);
                                                setDestinationLocationOnMap();



                                            } else if (statusResponse.optString("status").equals("DROPPED")
                                                    && statusResponse.optString("paid").equals("0")) {
                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
                                                }
                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
                                                img03Status1.setImageResource(R.drawable.arrived);
                                                img03Status2.setImageResource(R.drawable.pickup);
                                                btn_01_status.setText(getString(R.string.tap_when_paid));
                                                sos.setVisibility(View.VISIBLE);
//                                                navigate.setVisibility(View.GONE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                CurrentStatus = "COMPLETED";

                                                LocationTracking.distance = 0.0f;
                                            } else if (statusResponse.optString("status").equals("COMPLETED")
                                                    && statusResponse.optString("paid").equals("0")) {
                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
                                                }
                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
                                                img03Status1.setImageResource(R.drawable.arrived);
                                                img03Status2.setImageResource(R.drawable.pickup);
                                                btn_01_status.setText(getString(R.string.tap_when_paid));
                                                sos.setVisibility(View.VISIBLE);
//                                                navigate.setVisibility(View.GONE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                CurrentStatus = "COMPLETED";

                                                LocationTracking.distance = 0.0f;
                                            } else if (statusResponse.optString("status").equals("DROPPED") && statusResponse.optString("paid").equals("1")) {
                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
                                                if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
                                                    ll_05_contentLayer_feedback.startAnimation(slide_up);
                                                }
                                                ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
                                                btn_01_status.setText(getString(R.string.rate_user));
                                                sos.setVisibility(View.VISIBLE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                CurrentStatus = "RATE";

                                                LocationTracking.distance = 0.0f;
                                            } else if (statusResponse.optString("status").equals("COMPLETED") && statusResponse.optString("paid").equals("1")) {

                                                if (ll_05_contentLayer_feedback.getVisibility() == View.VISIBLE) {
                                                    ll_05_contentLayer_feedback.setVisibility(View.GONE);
                                                }
                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
                                                if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
                                                    ll_05_contentLayer_feedback.startAnimation(slide_up);
                                                }
                                                ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
                                                edt05Comment.setText("");
                                                sos.setVisibility(View.GONE);
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);
                                                btn_01_status.setText(getString(R.string.rate_user));
                                                CurrentStatus = "RATE";

                                                LocationTracking.distance = 0.0f;
                                                type = null;

                                            } else if (statusResponse.optString("status").equals("SCHEDULED")) {
                                                if (mMap != null) {
                                                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                        return;
                                                    }
                                                    mMap.clear();
                                                }
                                                clearVisibility();
                                                CurrentStatus = "SCHEDULED";
                                                if (lnrGoOffline.getVisibility() == View.GONE) {
                                                    //// lnrGoOffline.startAnimation(slide_up);
                                                }
                                                ///  lnrGoOffline.setVisibility(View.VISIBLE);
                                                utils.print("statusResponse", "null");
                                                destinationLayer.setVisibility(View.GONE);
                                                layoutinfo.setVisibility(View.VISIBLE);

                                                LocationTracking.distance = 0.0f;
                                            }
                                        }
                                    } else {
                                        if (mMap != null) {
                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            timerCompleted = false;
                                            mMap.clear();
                                            if (mPlayer != null && mPlayer.isPlaying()) {
                                                mPlayer.stop();
                                                mPlayer = null;
                                                countDownTimer.cancel();
                                            }

                                        }

                                        LocationTracking.distance = 0.0f;

                                        clearVisibility();
                                        //lnrGoOffline.setVisibility(View.VISIBLE);
                                        destinationLayer.setVisibility(View.GONE);
                                        layoutinfo.setVisibility(View.VISIBLE);
                                        CurrentStatus = "ONLINE";
                                        PreviousStatus = "NULL";
                                        utils.print("statusResponse", "null");
                                    }

                                } else {
                                    timerCompleted = false;
                                    if (!PreviousStatus.equalsIgnoreCase("NULL")) {
                                        utils.print("response", "null");
                                        if (mMap != null) {
                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                                return;
                                            }
                                            mMap.clear();
                                        }
                                        if (mPlayer != null && mPlayer.isPlaying()) {
                                            mPlayer.stop();
                                            mPlayer = null;
                                            countDownTimer.cancel();
                                        }
                                        clearVisibility();
                                        //mapClear();
                                        //    lnrGoOffline.setVisibility(View.VISIBLE);
                                        destinationLayer.setVisibility(View.GONE);
                                        layoutinfo.setVisibility(View.VISIBLE);
                                        CurrentStatus = "ONLINE";
                                        PreviousStatus = "NULL";
                                        utils.print("statusResponse", "null");

                                        LocationTracking.distance = 0.0f;
                                    }

                                }
                            }

                        } else {
                            try {
                                JSONArray statusResponses = new JSONArray(datas);
                                Log.e(TAG, "new_array: " + statusResponses);
                                for (int i = 0; i < statusResponses.length(); i++) {

                                    JSONObject getjsonobj = statusResponses.getJSONObject(i);
                                    JSONObject jsonobj = getjsonobj.getJSONObject("request");
                                    Log.e(TAG, "jsonobj: " + jsonobj);
                                    getStatusVariable = jsonobj.optString("status");
                                    request_id = jsonobj.optString("id");
                                    Log.e(TAG, "REQ_ID: " + request_id);
                                    Log.e(TAG, "getStatusVariable: " + getStatusVariable);


                                    if ((jsonobj != null) && (request_id != null)) {
                                        if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" ")) && mMap != null) {
                                            Log.e(TAG, "Previous req");
                                            previous_request_id = request_id;
                                            srcLatitude = Double.valueOf(jsonobj.optString("s_latitude"));
                                            srcLongitude = Double.valueOf(jsonobj.optString("s_longitude"));
                                            destLatitude = Double.valueOf(jsonobj.optString("d_latitude"));
                                            destLongitude = Double.valueOf(jsonobj.optString("d_longitude"));
                                            //noinspection deprecation
                                            setSourceLocationOnMap(currentLatLng);
                                            setPickupLocationOnMap();
                                            sos.setVisibility(View.GONE);
                                        }
                                        utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + jsonobj.optString("status"));
                                        if (!PreviousStatus.equals(jsonobj.optString("status"))) {
                                            Log.e(TAG, "Previous req1111");
                                            PreviousStatus = getStatusVariable;
                                            clearVisibility();
                                            utils.print("responseObj(" + request_id + ")", jsonobj.toString());
                                            utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + jsonobj.optString("status"));
                                            if (!getStatusVariable.equals("SEARCHING")) {
                                                timerCompleted = false;
                                                if (mPlayer != null && mPlayer.isPlaying()) {
                                                    mPlayer.stop();
                                                    mPlayer = null;
                                                    countDownTimer.cancel();
                                                }
                                            }

                                            if (getStatusVariable.equals("SCHEDULED")) {
                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses,response);
                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
                                                }
                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
                                                btn_01_status.setText(getString(R.string.tap_when_arrived));
                                                CurrentStatus = "ARRIVED";
                                                sos.setVisibility(View.GONE);
                                                if (srcLatitude == 0 && srcLongitude == 0 && destLatitude == 0 && destLongitude == 0) {
                                                    mapClear();
                                                    srcLatitude = Double.valueOf(jsonobj.optString("s_latitude"));
                                                    srcLongitude = Double.valueOf(jsonobj.optString("s_longitude"));
                                                    destLatitude = Double.valueOf(jsonobj.optString("d_latitude"));
                                                    destLongitude = Double.valueOf(jsonobj.optString("d_longitude"));
                                                    //noinspection deprecation
                                                    //
                                                    setSourceLocationOnMap(currentLatLng);
                                                    setPickupLocationOnMap();
                                                }
                                                sos.setVisibility(View.GONE);
                                                btn_cancel_ride.setVisibility(View.VISIBLE);
                                                destinationLayer.setVisibility(View.VISIBLE);
                                                layoutinfo.setVisibility(View.GONE);
                                                String address = jsonobj.optString("s_address");
                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
                                                    destination.setText(address);
                                                else
                                                    destination.setText(getAddress(jsonobj.optString("s_latitude"),
                                                            jsonobj.optString("s_longitude")));
                                                topSrcDestTxtLbl.setText(getString(R.string.pick_up));


                                            }
                                        }
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        utils.print("Error", error.toString());
                        //errorHandler(error);
                        timerCompleted = false;
                        mapClear();
                        clearVisibility();
                        CurrentStatus = "ONLINE";
                        PreviousStatus = "NULL";
                        //   lnrGoOffline.setVisibility(View.VISIBLE);
                        destinationLayer.setVisibility(View.GONE);
                        layoutinfo.setVisibility(View.VISIBLE);
                        if (mPlayer != null && mPlayer.isPlaying()) {
                            mPlayer.stop();
                            mPlayer = null;
                            countDownTimer.cancel();
                        }
                        if (errorLayout.getVisibility() != View.VISIBLE) {
                            errorLayout.setVisibility(View.VISIBLE);
                            sos.setVisibility(View.GONE);
                        }
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + token);
                        Log.e(TAG, "HEADERS: " + headers.toString());
                        return headers;
                    }
                };
                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                displayMessage(getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setValuesTo_ll_01_contentLayer_accept_or_reject_now(JSONArray status) {
        JSONObject statusResponse = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            if (!status.getJSONObject(0).optString("time_left_to_respond").equals("")) {
                count = status.getJSONObject(0).getString("time_left_to_respond");
            } else {
                count = "0";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        countDownTimer = new CountDownTimer(Integer.parseInt(count) * 1000, 1000) {

            @SuppressLint("SetTextI18n")
            public void onTick(long millisUntilFinished) {
                txt01Timer.setText("" + millisUntilFinished / 1000);
                if (mPlayer == null) {
                    mPlayer = MediaPlayer.create(context, R.raw.alert_tone);
                } else {
                    if (!mPlayer.isPlaying()) {
                        mPlayer.start();
                    }
                }
                isRunning = true;
                timerCompleted = false;

            }

            public void onFinish() {
                txt01Timer.setText("0");
                mapClear();
                clearVisibility();
                if (mMap != null) {
                    mMap.clear();
                }
                if (mPlayer != null && mPlayer.isPlaying()) {
                    mPlayer.stop();
                    mPlayer = null;
                }
                ll_01_contentLayer_accept_or_reject_now.setVisibility(View.GONE);
                CurrentStatus = "ONLINE";
                PreviousStatus = "NULL";
                // lnrGoOffline.setVisibility(View.VISIBLE);
                destinationLayer.setVisibility(View.GONE);
                isRunning = false;
                timerCompleted = true;
                handleIncomingRequest("Reject", request_id);
            }
        };


        countDownTimer.start();

        try {
            if (!statusResponse.optString("schedule_at").trim().equalsIgnoreCase("") && !statusResponse.optString("schedule_at").equalsIgnoreCase("null")) {
                txtSchedule.setVisibility(View.VISIBLE);
                String strSchedule = "";
                try {
                    strSchedule = getDate(statusResponse.optString("schedule_at")) + "th " + getMonth(statusResponse.optString("schedule_at"))
                            + " " + getYear(statusResponse.optString("schedule_at")) + " at " + getTime(statusResponse.optString("schedule_at"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                txtSchedule.setText("Scheduled at : " + strSchedule);
            } else {
                txtSchedule.setVisibility(View.GONE);
            }

            final JSONObject user = statusResponse.getJSONObject("user");
            if (user != null) {
                if (!user.optString("picture").equals("null")) {
//                    new DownloadImageTask(img01User).execute(user.getString("picture"));
                    //Glide.with(activity).load(URLHelper.base+"storage/app/public/"+user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img01User);
                    if (user.optString("picture").startsWith("http"))
                        Picasso.get().load(user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img01User);
                    else
                        Picasso.get().load(URLHelper.base + "storage/app/public/" + user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img01User);
                } else {
                    img01User.setImageResource(R.drawable.ic_dummy_user);
                }
                final User userProfile = this.user;
                img01User.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShowProfile.class);
                        intent.putExtra("user", userProfile);
                        startActivity(intent);
                    }
                });
                txt01UserName.setText(user.optString("first_name"));
                if (!statusResponse.isNull("distance")) {
                    Double d = Double.parseDouble(statusResponse.optString("distance"));
                    tvDistance.setText(Math.round(d) +"KM");
                }
//                + " " + user.optString("last_name"
                if (statusResponse.getJSONObject("user").getString("rating") != null) {
                    rat01UserRating.setRating(Float.valueOf(user.getString("rating")));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        txt01Pickup.setText(address);
        txtDropOff.setText(daddress);
    }

    private void setValuesTo_ll_03_contentLayer_service_flow(JSONArray status,JSONObject responess) {
        JSONObject statusResponse = new JSONObject();
        Log.e(TAG, "statusResponse: " + statusResponse);
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
            lblCmfrmSourceAddress.setText(statusResponse.optString("s_address"));
            lblCmfrmDestAddress.setText(statusResponse.optString("d_address"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            JSONObject user = statusResponse.getJSONObject("user");
            if (user != null) {
                if (!user.optString("mobile").equals("null")) {
                    SharedHelper.putKey(context, "provider_mobile_no", "" + user.optString("mobile"));
                } else {
                    SharedHelper.putKey(context, "provider_mobile_no", "");
                }

                if (!user.optString("picture").equals("null")) {
                    //Glide.with(activity).load(URLHelper.base+"storage/app/public/"+user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img03User);
                    if (user.optString("picture").startsWith("http"))
                        Picasso.get().load(user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img03User);
                    else
                        Picasso.get().load(URLHelper.base + "storage/app/public/" + user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img03User);
                } else {
                    img03User.setImageResource(R.drawable.ic_dummy_user);
                }
                final User userProfile = this.user;
                img03User.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShowProfile.class);
                        intent.putExtra("user", userProfile);
                        startActivity(intent);
                    }
                });

                txt03UserName.setText(user.optString("first_name"));

                if (statusResponse.getJSONObject("user").getString("rating") != null) {
                    rat03UserRating.setRating(Float.valueOf(user.getString("rating")));
                } else {
                    rat03UserRating.setRating(0);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setValuesTo_ll_04_contentLayer_payment(JSONArray status) {
        JSONObject statusResponse = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            txt04InvoiceId.setText(bookingId);
            txt04BasePrice.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("fixed"));
            txt04Distance.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("distance"));
            txt04Tax.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("tax"));
            txt04Total.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("total"));
            txt04PaymentMode.setText(statusResponse.getString("payment_mode"));
            txt04Commision.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("commision"));
            txtTotal.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("total"));
            if (statusResponse.getString("payment_mode").equals("CASH")) {
                paymentTypeImg.setImageResource(R.drawable.money1);
                btn_confirm_payment.setVisibility(View.VISIBLE);
            } else {
                paymentTypeImg.setImageResource(R.drawable.visa_icon);
                btn_confirm_payment.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setValuesTo_ll_05_contentLayer_feedback(JSONArray status) {
        rat05UserRating.setRating(1.0f);
        feedBackRating = "1";
        rat05UserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar ratingBar, float rating,
                                        boolean fromUser) {
                utils.print("rating", rating + "");
                if (rating < 1.0f) {
                    rat05UserRating.setRating(1.0f);
                    feedBackRating = "1";
                }
                feedBackRating = String.valueOf((int) rating);
            }
        });
        JSONObject statusResponse = new JSONObject();
        try {
            statusResponse = status.getJSONObject(0).getJSONObject("request");
            JSONObject user = statusResponse.getJSONObject("user");
            if (user != null) {
                lblProviderName.setText(user.optString("first_name"));
//                + " "+user.optString("last_name")
                if (!user.optString("picture").equals("null")) {
//                    new DownloadImageTask(img05User).execute(user.getString("picture"));
                    //Glide.with(activity).load(URLHelper.base+"storage/app/public/"+user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img05User);
                    if (user.optString("picture").startsWith("http"))
                        Picasso.get().load(user.getString("picture"))
                                .placeholder(R.drawable.ic_dummy_user)
                                .error(R.drawable.ic_dummy_user).into(img05User);
                    else
                        Picasso.get().load(URLHelper.base + "storage/app/public/" + user
                                .getString("picture")).placeholder(R.drawable.ic_dummy_user)
                                .error(R.drawable.ic_dummy_user).into(img05User);
                } else {
                    img05User.setImageResource(R.drawable.ic_dummy_user);
                }
                final User userProfile = this.user;
                img05User.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ShowProfile.class);
                        intent.putExtra("user", userProfile);
                        startActivity(intent);
                    }
                });

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        feedBackComment = edt05Comment.getText().toString();
    }

    private void update(final String status, String id) {
        Log.v("Status",status+" ");
        customDialog = new CustomDialog(activity);
        customDialog.setCancelable(false);
        customDialog.show();
        if (status.equals("ONLINE")) {

            JSONObject param = new JSONObject();
            try {
                param.put("service_status", "offline");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_AVAILABILITY_API, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    if (response != null) {
                        if (response.optJSONObject("service").optString("status").equalsIgnoreCase("offline")) {
                            goOffline();
                            activeStatus.setText(getActivity().getString(R.string.offline));
                        } else {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    utils.print("Error", error.toString());
                    errorHandler(error);
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            String url;
            JSONObject param = new JSONObject();
            if (status.equals("RATE")) {
                url = URLHelper.base + "api/provider/trip/" + id + "/rate";
                try {
                    param.put("rating", feedBackRating);
                    param.put("comment", edt05Comment.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                utils.print("Input", param.toString());
            } else {
                url = URLHelper.base + "api/provider/trip/" + id;
                try {
                    param.put("_method", "PATCH");
                    param.put("status", status);
                    if (status.equalsIgnoreCase("DROPPED")) {
                        param.put("latitude", crt_lat);
                        param.put("longitude", crt_lng);
                        param.put("distance", LocationTracking.distance * 0.001);
                    }
                    utils.print("Input", param.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    if (response.optJSONObject("requests") != null) {
                        utils.print("request", response.optJSONObject("requests").toString());
                    }
                    if (status.equals("RATE")) {
                        //  lnrGoOffline.setVisibility(View.VISIBLE);
                        destinationLayer.setVisibility(View.GONE);
                        layoutinfo.setVisibility(View.VISIBLE);
                        LatLng myLocation = new LatLng(Double.parseDouble(crt_lat), Double.parseDouble(crt_lng));
                        CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        mapClear();
                        clearVisibility();
                        mMap.clear();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    utils.print("Error", error.toString());
                    if (status.equals("RATE")) {
                        //lnrGoOffline.setVisibility(View.VISIBLE);
                        destinationLayer.setVisibility(View.GONE);
                        layoutinfo.setVisibility(View.VISIBLE);
                    }
                    //errorHandler(error);
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + token);
                    return headers;
                }
            };
            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
        }
    }

    public void cancelRequest(String id, String reason) {

        customDialog = new CustomDialog(context);
        customDialog.setCancelable(false);
        customDialog.show();

        try {
            JSONObject object = new JSONObject();
            object.put("request_id", id);
            object.put("cancel_reason", reason);
            Log.e("", "request_id" + id);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    customDialog.dismiss();
                    utils.print("CancelRequestResponse", response.toString());
                    Toast.makeText(context, "" + "You have cancelled the request", Toast.LENGTH_SHORT).show();
                    mapClear();
                    clearVisibility();
                    //lnrGoOffline.setVisibility(View.VISIBLE);
                    layoutinfo.setVisibility(View.VISIBLE);
                    destinationLayer.setVisibility(View.GONE);
                    CurrentStatus = "ONLINE";
                    PreviousStatus = "NULL";
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    customDialog.dismiss();
                    String json = null;
                    String Message;
                    NetworkResponse response = error.networkResponse;
                    if (response != null && response.data != null) {

                        try {
                            JSONObject errorObj = new JSONObject(new String(response.data));

                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"));
                                } catch (Exception e) {
                                    displayMessage("Something went wrong");
                                    e.printStackTrace();
                                }
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity();
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
                            displayMessage("Something went wrong");
                            e.printStackTrace();
                        }

                    } else {
                        displayMessage(getString(R.string.please_try_again));
                    }
                }
            }) {
                @Override
                public java.util.Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                    Log.e("", "Access_Token" + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleIncomingRequest(final String status, String id) {
        if (!((Activity) context).isFinishing()) {
            customDialog = new CustomDialog(activity);
            customDialog.setCancelable(false);
            customDialog.show();
        }
        String url = URLHelper.base + "api/provider/trip/" + id;

        if (status.equals("Accept")) {
            method = Request.Method.POST;
        } else {
            method = Request.Method.DELETE;
        }

        Log.v("handlerequest",url+" "+method);
        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(method, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (isAdded()) {
                    customDialog.dismiss();
                    if (status.equals("Accept")) {
                        Toast.makeText(context, "Request accepted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        if (!timerCompleted) {
                            if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.VISIBLE) {


                                mapClear();
                                clearVisibility();
                                if (mMap != null) {
                                    mMap.clear();
                                }
                                ll_01_contentLayer_accept_or_reject_now.setVisibility(View.GONE);
                                CurrentStatus = "ONLINE";
                                PreviousStatus = "NULL";
                                //  lnrGoOffline.setVisibility(View.VISIBLE);
                                destinationLayer.setVisibility(View.GONE);
                                layoutinfo.setVisibility(View.VISIBLE);
                                isRunning = false;
                                timerCompleted = true;
                                handleIncomingRequest("Reject", request_id);
                            }
                            Toast.makeText(context, "Request rejected successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Request Timeout", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                utils.print("Error", error.toString());
                //errorHandler(error);

            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void errorHandler(VolleyError error) {
        utils.print("Error", error.toString());
        String json = null;
        String Message;
        NetworkResponse response = error.networkResponse;
        if (response != null && response.data != null) {

            try {
                JSONObject errorObj = new JSONObject(new String(response.data));
                utils.print("ErrorHandler", "" + errorObj.toString());
                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                    try {
                        displayMessage(errorObj.optString("message"));
                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }
                } else if (response.statusCode == 401) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    GoToBeginActivity();
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
            displayMessage(getString(R.string.please_try_again));
        }
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
//        Snackbar.make(getActivity().findViewById(android.R.id.content), toastString, Snackbar.LENGTH_SHORT)
//                .setAction("Action", null).show();
    }

    public void GoToBeginActivity() {
        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
        Intent mainIntent = new Intent(activity, SplashScreen.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    public void goOffline() {
        try {
            //  btn_go_offline.setVisibility(View.GONE);
            //  btn_go_online.setVisibility(View.VISIBLE);
//            FragmentManager manager = MainActivity.fragmentManager;
//            FragmentTransaction transaction = manager.beginTransaction();
//            transaction.replace(R.id.content, new Offline());
//            transaction.commitAllowingStateLoss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void goOnline() {
        customDialog = new CustomDialog(activity);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("service_status", "active");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_AVAILABILITY_API, param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        customDialog.dismiss();
                        if (response.optJSONObject("service").optString("status").equalsIgnoreCase("active")) {
//                            Intent intent = new Intent(context, MainActivity.class);
//                            context.startActivity(intent);
                            activeStatus.setText(getActivity().getString(R.string.online));
                            //  btn_go_offline.setVisibility(View.VISIBLE);
                            //  btn_go_online.setVisibility(View.GONE);
                        } else {
                            displayMessage(getString(R.string.something_went_wrong));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    customDialog.dismiss();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                customDialog.dismiss();
                utils.print("Error", error.toString());
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                            GoToBeginActivity();
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
                        goOnline();
                    }
                }
            }
        }) {
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    @Override
    public void onDestroy() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.stop();
            mPlayer = null;
        }
        ha.removeCallbacksAndMessages(null);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    private String getMonth(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        return monthName;
    }

    private String getDate(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
        return dateName;
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
        return yearName;
    }

    private String getTime(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
        return timeName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_LOCATION) {
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
            }
        }

        switch (requestCode) {
            case TAKE_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    //  cameraImageUri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
                    if (cameraImageUri != null) {
                        getActivity().getContentResolver()
                                .notifyChange(cameraImageUri, null);

                        fileExt = ".png";
                        file = new File(cameraImageUri.getPath().toString());
                        first = file.getName() + fileExt;
//                        galleryImageUri = null;
                        try {
//                            if ((docIds != "") && (docIds != null)) {
//                                new BitmapWorkerTask(getActivity(), imgTest,
//                                        "add_revenue").execute(cameraImageUri);
//
//                                SharedHelper.putKey(activity, "ImageURI"+adapterPosi ,first);
//
//                             /*   Handler handler = new Handler();
//                               Runnable runnable = new Runnable() {
//                                   @Override
//                                   public void run() {
//
//
//                                   }
//                               }).handler.postDelayed(runnable,1000);;*/
//                                saveProfileAccount("LogBook",
//                                        AppHelper.getFileDataFromDrawable(imgTest.getDrawable()),
//                                        docIds);
//                            }

                            if (uploadTag == "LogBook") {
                                documentUri = cameraImageUri;
                                btPersonalPic.setVisibility(View.GONE);
                                imgPersonal.setVisibility(View.VISIBLE);
                                new BitmapWorkerTask(getActivity(), imgPersonal,
                                        "add_revenue").execute(cameraImageUri);
                                showImage(cameraImageUri);

//                                saveProfileAccount("LogBook",
//                                        AppHelper.getFileDataFromDrawable(imgPersonal.getDrawable()),
//                                        SharedHelper.getKey(getActivity(), uploadTag));


                            }
                            if (uploadTag == "National ID / Passport") {
                                documentUri = cameraImageUri;
                                btpersonalId.setVisibility(View.GONE);
                                imgPersonalId.setVisibility(View.VISIBLE);
                                new BitmapWorkerTask(getActivity(), imgPersonalId,
                                        "add_revenue").execute(cameraImageUri);

                                saveProfileAccount("NationalIDPassport",
                                        AppHelper.getFileDataFromDrawable(imgPersonalId.getDrawable()),
                                        SharedHelper.getKey(getActivity(), uploadTag));
                            }
                            if (uploadTag == "PSV License") {
                                documentUri = cameraImageUri;
                                btPSVLicense.setVisibility(View.GONE);
                                imgPSVLicense.setVisibility(View.VISIBLE);
                                new BitmapWorkerTask(getActivity(), imgPSVLicense,
                                        "add_revenue").execute(cameraImageUri);

                                saveProfileAccount("PSVLicense",
                                        AppHelper.getFileDataFromDrawable(imgPSVLicense.getDrawable()),
                                        SharedHelper.getKey(getActivity(), uploadTag));
                            }

                            if (uploadTag == "Certificate Of Good Conduct") {
                                documentUri = cameraImageUri;
                                btGoodConduct.setVisibility(View.GONE);
                                imgGoodConduct.setVisibility(View.VISIBLE);
                                new BitmapWorkerTask(getActivity(), imgGoodConduct,
                                        "add_revenue").execute(cameraImageUri);

                                saveProfileAccount("CertificateOfGoodConduct",
                                        AppHelper.getFileDataFromDrawable(imgGoodConduct.getDrawable()),
                                        SharedHelper.getKey(getActivity(), uploadTag));
                            }


                            if (uploadTag == "Motor Vehicle Inspection Certificate") {
                                documentUri = cameraImageUri;
                                btMotorInspectCertificate.setVisibility(View.GONE);
                                imgMotorInspectCertificate.setVisibility(View.VISIBLE);
                                new BitmapWorkerTask(getActivity(), imgMotorInspectCertificate,
                                        "add_revenue").execute(cameraImageUri);
                                saveProfileAccount("MotorVehicleInspectionCertificate",
                                        AppHelper.getFileDataFromDrawable(imgMotorInspectCertificate.getDrawable()),
                                        SharedHelper.getKey(getActivity(), uploadTag));
                            }
                            if (uploadTag == "PSV Insurance Certificate") {
                                documentUri = cameraImageUri;
                                btPsvInsurenceCertificate.setVisibility(View.GONE);
                                imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
                                new BitmapWorkerTask(getActivity(), imgPsvInsurenceCertificate,
                                        "add_revenue").execute(cameraImageUri);
                                saveProfileAccount("PSVInsuranceCertificate",
                                        AppHelper.getFileDataFromDrawable(imgPsvInsurenceCertificate.getDrawable()),
                                        SharedHelper.getKey(getActivity(), uploadTag));
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;

            case GET_PICTURE:
                if (resultCode == Activity.RESULT_OK) {
                    {
                        Uri uri = data.getData();
                        File f1 = new File(uri.getPath());
                        try {
                            Bitmap resizeImg = getBitmapFromUri(getActivity(), uri);
//                            if (resizeImg != null) {
//                                if ((docIds != "") && (docIds != null)) {
//                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
//                                            AppHelper.getPath(getActivity(), uri));
//                                    imgTest.setImageBitmap(reRotateImg);
//
//                                   // Uri selectedImageURI = data.getData();
//                                    DocListItemModel placeWorkModel = new DocListItemModel(); // the model between activity and adapter
//                                   // placeWorkModel.setImage(Integer.parseInt(convertImage2Base64()));  // here i pass the photo
//                                    placeWorkModel.setImage(String.valueOf(f1));
//                                    documentItemArrayList.add(placeWorkModel);
//
//                                    docListAdapter.updateList(documentItemArrayList); // add this
//
//                                    docListAdapter.notifyDataSetChanged();
//
//                                    SharedHelper.putKey(activity, "ImageURI"+adapterPosi ,first);
//                                    saveProfileAccount("LogBook",
//                                            AppHelper.getFileDataFromDrawable(imgTest.getDrawable()),
//                                            docIds);
//                                }
//                            }

                            if (resizeImg != null) {
                                if (uploadTag == "LogBook") {
//                                    btPersonalPic.setVisibility(View.GONE);
//                                    imgPersonal.setVisibility(View.VISIBLE);
                                    documentUri = uri;
                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
                                            AppHelper.getPath(getActivity(), uri));

                                    showImage1(reRotateImg);
//                                    imgPersonal.setImageBitmap(reRotateImg);
//                                    saveProfileAccount("LogBook",
//                                            AppHelper.getFileDataFromDrawable(imgPersonal.getDrawable()),
//                                            SharedHelper.getKey(getActivity(), uploadTag));
                                }
                            }
                            if (resizeImg != null) {
                                if (uploadTag == "National ID / Passport") {
                                    documentUri = uri;
                                    btpersonalId.setVisibility(View.GONE);
                                    imgPersonalId.setVisibility(View.VISIBLE);
                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
                                            AppHelper.getPath(getActivity(), uri));
                                    imgPersonalId.setImageBitmap(reRotateImg);
                                    saveProfileAccount("NationalIDPassport",
                                            AppHelper.getFileDataFromDrawable(imgPersonalId.getDrawable()),
                                            SharedHelper.getKey(getActivity(), uploadTag));
                                }
                            }
                            if (resizeImg != null) {
                                if (uploadTag == "PSV License") {
                                    documentUri = uri;
                                    btPSVLicense.setVisibility(View.GONE);
                                    imgPSVLicense.setVisibility(View.VISIBLE);
                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
                                            AppHelper.getPath(getActivity(), uri));
                                    imgPSVLicense.setImageBitmap(reRotateImg);
                                    saveProfileAccount("PSVLicense",
                                            AppHelper.getFileDataFromDrawable(imgPSVLicense.getDrawable()),
                                            SharedHelper.getKey(getActivity(), uploadTag));
                                }
                            }
                            if (resizeImg != null) {
                                if (uploadTag == "Certificate Of Good Conduct") {
                                    documentUri = uri;
                                    btGoodConduct.setVisibility(View.GONE);
                                    imgGoodConduct.setVisibility(View.VISIBLE);
                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
                                            AppHelper.getPath(getActivity(), uri));
                                    imgGoodConduct.setImageBitmap(reRotateImg);
                                    saveProfileAccount("CertificateOfGoodConduct",
                                            AppHelper.getFileDataFromDrawable(imgGoodConduct.getDrawable()),
                                            SharedHelper.getKey(getActivity(), uploadTag));
                                }
                            }
                            if (resizeImg != null) {
                                if (uploadTag == "Motor Vehicle Inspection Certificate") {
                                    documentUri = uri;
                                    btMotorInspectCertificate.setVisibility(View.GONE);
                                    imgMotorInspectCertificate.setVisibility(View.VISIBLE);
                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
                                            AppHelper.getPath(getActivity(), uri));
                                    imgMotorInspectCertificate.setImageBitmap(reRotateImg);
                                    saveProfileAccount("MotorVehicleInspectionCertificate",
                                            AppHelper.getFileDataFromDrawable(imgMotorInspectCertificate.getDrawable()),
                                            SharedHelper.getKey(getActivity(), uploadTag));
                                }
                            }

                            if (resizeImg != null) {
                                if (uploadTag == "PSV Insurance Certificate") {
                                    documentUri = uri;
                                    btPsvInsurenceCertificate.setVisibility(View.GONE);
                                    imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
                                            AppHelper.getPath(getActivity(), uri));
                                    imgPsvInsurenceCertificate.setImageBitmap(reRotateImg);
                                    saveProfileAccount("PSVInsuranceCertificate",
                                            AppHelper.getFileDataFromDrawable(imgPsvInsurenceCertificate.getDrawable()),
                                            SharedHelper.getKey(getActivity(), uploadTag));
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }

                break;

        }
    }

    public void saveProfileAccount(String filename, byte[] bytes, String docid) {
        if (Utils.isConnectingToInternet(getActivity())) {
            pDialog = new ProgressDialog(getActivity());
            // pDialog.setTitle("Loading...");
            pDialog.setCancelable(false);
            pDialog.setMessage("Loading...");
            pDialog.show();
            final ProgressDialog finalPDialog = pDialog;
            VolleyMultipartRequest multipartRequest = new
                    VolleyMultipartRequest(Request.Method.POST,
                            URLHelper.base + "api/provider/document/upload",
                            new Response.Listener<NetworkResponse>() {
                                @Override
                                public void onResponse(NetworkResponse response) {
                                    pDialog.dismiss();
                                    if (uploadTag == "LogBook") {
                                        SharedHelperImage.putKey(getActivity(),
                                                "LogBook", String.valueOf(documentUri));
                                    }
                                    if (uploadTag == "National ID / Passport") {
                                        SharedHelperImage.putKey(getActivity(),
                                                "NationalID/Passport", String.valueOf(documentUri));
                                    }
                                    if (uploadTag == "PSV License") {
                                        SharedHelperImage.putKey(getActivity(),
                                                "PSVLicense", String.valueOf(documentUri));
                                    }
                                    if (uploadTag == "Certificate Of Good Conduct") {
                                        SharedHelperImage.putKey(getActivity(),
                                                "CertificateOfGoodConduct",
                                                String.valueOf(documentUri));
                                    }
                                    if (uploadTag == "Motor Vehicle Inspection Certificate") {
                                        SharedHelperImage.putKey(getActivity(),
                                                "MotorVehicleInspectionCertificate",
                                                String.valueOf(documentUri));
                                    }

                                    if (uploadTag == "PSV Insurance Certificate") {
                                        SharedHelperImage.putKey(getActivity(),
                                                "PSVInsuranceCertificate", String.valueOf(documentUri));
                                    }
                                    String resultResponse = new String(response.data);
                                    Log.e("uploadtest", resultResponse.toString() + "");
                                    Toast.makeText(getActivity(), "File is Uploaded Successfully",
                                            Toast.LENGTH_LONG).show();
                                    pDialog.dismiss();

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            NetworkResponse networkResponse = error.networkResponse;
                            String errorMessage = "Unknown error";
                            if (networkResponse == null) {
                                if (error.getClass().equals(TimeoutError.class)) {
                                    errorMessage = "Request timeout";
                                } else if (error.getClass().equals(NoConnectionError.class)) {
                                    errorMessage = "Failed to connect server";
                                }
                            }
                            pDialog.dismiss();

                            Log.e("Error Block", errorMessage);
                            error.printStackTrace();
                        }
                    }) {
                        @Override
                        protected java.util.Map<String, String> getParams() {
                            java.util.Map<String, String> param = new HashMap<>();
                            param.put("document_id", docid);
                            param.put("provider_id", SharedHelper.getKey(getActivity(), "id"));


                            return param;
                        }

                        @Override
                        public java.util.Map<String, String> getHeaders() throws AuthFailureError {
                            HashMap<String, String> headers = new HashMap<String, String>();
                            headers.put("X-Requested-With", "XMLHttpRequest");
                            headers.put("Authorization", "Bearer " + SharedHelper.getKey(getActivity(), "access_token"));

                            return headers;
                        }


                        @Override
                        protected java.util.Map<String, DataPart> getByteData() {
                            java.util.Map<String, DataPart> params = new HashMap<>();
                            // file name could found file base or direct access from real path
                            // for now just get bitmap data from ImageView

                            params.put("document", new DataPart(filename, bytes, "image/jpeg"));

                            return params;
                        }
                    };


            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            RequestQueue queue = Volley.newRequestQueue(getActivity());
            queue.add(multipartRequest);
        } else {

            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();


        }

        // VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
    }

    public String getAddress(String strLatitude, String strLongitude) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        double latitude = Double.parseDouble(strLatitude);
        double longitude = Double.parseDouble(strLongitude);
        String address = "", city = "", state = "";
        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (address.length() > 0 || city.length() > 0)
            return address + ", " + city;
        else
            return getString(R.string.no_address);
    }


    @Override
    public void onPause() {

        super.onPause();
        Utilities.onMap = false;
        if (customDialog != null) {
            if (customDialog.isShowing()) {
                customDialog.dismiss();
            }
        }
        if (ha != null) {
            isRunning = true;
            if (mPlayer != null && mPlayer.isPlaying()) {
                normalPlay = true;
                mPlayer.stop();
            } else {
                normalPlay = false;
            }
            ha.removeCallbacksAndMessages(null);
        }
    }

    private void showCancelDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.cancel_confirm));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showReasonDialog();
            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
            }
        });
        dialog.show();
    }

    String cancaltype="";
    String cancalReason = "";
    private void showReasonDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
        Button submitBtn = view.findViewById(R.id.submit_btn);
        RadioGroup radioCancel = view.findViewById(R.id.radioCancel);
        radioCancel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.driver) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getResources().getString(R.string.plan_changed);
                }
                if (checkedId == R.id.vehicle) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getResources().getString(R.string.booked_another_cab);
                }
                if (checkedId == R.id.app) {
                    reasonEtxt.setVisibility(View.VISIBLE);
                    cancaltype = getResources().getString(R.string.my_reason_is_not_listed);
                }
            }
        });
        builder.setView(view)
                .setCancelable(true);
        final AlertDialog dialog = builder.create();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cancaltype.isEmpty()) {
                    Toast.makeText(context, getResources().getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();

                } else {
                    cancalReason = reasonEtxt.getText().toString();
                    if (cancalReason.isEmpty()) {
                        reasonEtxt.setError(getResources().getString(R.string.please_specify_reason));
                    } else {
                        if (reasonEtxt.getText().toString().length() > 0)
                            cancelRequest(request_id, reasonEtxt.getText().toString());
                        else
                            cancelRequest(request_id, "");
                        dialog.dismiss();
                    }
                }
            }
        });
        dialog.show();


//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View view = inflater.inflate(R.layout.cancel_dialog, null);
//
//
//        Button submitBtn = view.findViewById(R.id.submit_btn);
//        final EditText reason = view.findViewById(R.id.reason_etxt);
//
//        builder.setView(view);
//        final android.app.AlertDialog dialog = builder.create();
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//        submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                dialog.dismiss();
//                if (reason.getText().toString().length() > 0)
//                    cancelRequest(request_id, reason.getText().toString());
//                else
//                    cancelRequest(request_id, "");
//            }
//        });
//
//
//        dialog.show();
    }

    private void showSosDialog() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(context);
        builder.setTitle(getString(R.string.sos_confirm));

        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //cancelRequest(request_id);
                dialog.dismiss();
                String mobile = SharedHelper.getKey(context, "sos");
                if (mobile != null && !mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + mobile));
                        startActivity(intent);
                    }
                } else {
                    displayMessage(getString(R.string.user_no_mobile));
                }

            }
        });

        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Reset to previous seletion menu in navigation
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        final AlertDialog dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface arg) {
            }
        });
        dialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Utilities.onMap = true;
        if (Utilities.clearSound) {
            NotificationManager notificationManager = (NotificationManager) getActivity()
                    .getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();
        }
        utils.print(TAG, "onResume: Handler Call out" + isRunning);
        if (isRunning) {
            if (mPlayer != null && normalPlay) {
                mPlayer = MediaPlayer.create(context, R.raw.alert_tone);
                mPlayer.start();
            }
            utils.print(TAG, "onResume: Handler Call" + isRunning);
            ha.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //call function
                    if (type != null) {
                        checkStatusSchedule();
                    } else {
                        checkStatus();
                    }
                    ha.postDelayed(this, 3000);
                }
            }, 3000);
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @SuppressLint("WrongConstant")
    private void documentUploadFirstDialog() {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.upload_document_popup, null);
        builder.setView(view);

//        RecyclerView rvDocList = view.findViewById(R.id.rvDocList);
//        imgTest = view.findViewById(R.id.imgTest);
//        rvDocList.setLayoutManager(new LinearLayoutManager(getActivity(),
//                LinearLayoutManager.VERTICAL, false));
//        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//        progressDialog.setTitle("Getting Documents type....");
//        progressDialog.setCancelable(false);
//        progressDialog.show();
//
//        JSONObject object = new JSONObject();
//        JsonObjectRequest jsonObjectRequest = new
//                JsonObjectRequest(Request.Method.GET,
//                        URLHelper.base + "api/provider/document/types",
//                        null,
//                        new Response.Listener<JSONObject>() {
//                            @Override
//                            public void onResponse(JSONObject response) {
//                                Log.e("documentListResponse", response + "document");
//                                progressDialog.dismiss();
//                                documentItemArrayList = new ArrayList<>();
//                                try {
//                                    JSONArray jsonArray = response.getJSONArray("document");
//                                    for (int i = 0; i < jsonArray.length(); i++) {
//                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                        DocListItemModel docListItemModel = new DocListItemModel();
//                                        docListItemModel.setName(jsonObject.optString("name"));
//                                        docListItemModel.setId(jsonObject.optInt("id"));
//                                        docListItemModel.setType(jsonObject.optString("type"));
//                                        documentItemArrayList.add(docListItemModel);
//
//                                    }
//                                    docListAdapter = new DocListAdapter(getActivity(),
//                                            documentItemArrayList);
//                                    rvDocList.setLayoutManager(new LinearLayoutManager(getActivity(),
//                                            LinearLayoutManager.VERTICAL, false));
//                                    rvDocList.setAdapter(docListAdapter);
//                                    docListAdapter.setUploadEventClick(new DocListAdapter.UploadEventClick() {
//                                        @Override
//                                        public void onViewDetailClick(View v, int position) {
//                                            DocListItemModel listItemModel = documentItemArrayList.get(position);
//                                            docIds = String.valueOf(listItemModel.getId());
//                                            adapterPosi = String.valueOf(documentItemArrayList.get(position));
//                                            //                uploadTag = "LogBook";
//                                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                                                    != PackageManager.PERMISSION_GRANTED) {
//                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                                            5);
//                                                }
//                                            }
//                                            Dialog d = ImageChoose();
//                                            d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                                            d.show();
//                                        }
//                                    });
//
//
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        progressDialog.dismiss();
//                        Log.e("errorupload", error.toString() + "");
//                    }
//                }) {
//                    @Override
//                    public java.util.Map<String, String> getHeaders() {
//                        HashMap<String, String> headers = new HashMap<String, String>();
//                        headers.put("X-Requested-With", "XMLHttpRequest");
//                        headers.put("Authorization", "Bearer " + SharedHelper.getKey(getActivity(),
//                                "access_token"));
//                        Log.e("", "Access_Token" + SharedHelper.getKey(getActivity(),
//                                "access_token"));
//                        return headers;
//                    }
//                };
//
//        getInstance().addToRequestQueue(jsonObjectRequest);

//        builder.setView(view);
        Waintingdialog = builder.create();
        Waintingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        Waintingdialog.setCanceledOnTouchOutside(false);

        imgPersonal = view.findViewById(R.id.imgPersonal);
        imgPersonalId = view.findViewById(R.id.imgPersonalId);
        imgPSVLicense = view.findViewById(R.id.imgPSVLicense);
        imgGoodConduct = view.findViewById(R.id.imgGoodConduct);
        imgMotorInspectCertificate = view.findViewById(R.id.imgMotorInspectCertificate);
        imgPsvInsurenceCertificate = view.findViewById(R.id.imgPsvInsurenceCertificate);

        Button btnDone = view.findViewById(R.id.btnDone);

        btMotorInspectCertificate = view.findViewById(R.id.btMotorInspectCertificate);
        btPsvInsurenceCertificate = view.findViewById(R.id.btPsvInsurenceCertificate);
        btGoodConduct = view.findViewById(R.id.btGoodConduct);
        btPSVLicense = view.findViewById(R.id.btPSVLicense);
        btpersonalId = view.findViewById(R.id.btpersonalId);
        btPersonalPic = view.findViewById(R.id.btPersonalPic);

//        if (SharedHelperImage.getKey(getActivity(), "LogBook") != "") {
//            btPersonalPic.setVisibility(View.GONE);
//            imgPersonal.setVisibility(View.VISIBLE);
//            File file = new File(SharedHelperImage.getKey(getActivity(), "LogBook"));
//            Picasso.get().load(file).into(imgPersonal);
//        }
//        if (SharedHelperImage.getKey(getActivity(), "NationalID/Passport") != "") {
//            btpersonalId.setVisibility(View.GONE);
//            imgPersonalId.setVisibility(View.VISIBLE);
//            File file = new File(SharedHelperImage.getKey(getActivity(), "NationalID/Passport"));
//            Picasso.get().load(file).into(imgPersonalId);
//        }
//        if (SharedHelperImage.getKey(getActivity(), "PSVLicense") != null) {
//            btPSVLicense.setVisibility(View.GONE);
//            imgPSVLicense.setVisibility(View.VISIBLE);
//            File file = new File(SharedHelperImage.getKey(getActivity(), "PSVLicense"));
//            Picasso.get().load(file).into(imgPSVLicense);
//        }
//        if (SharedHelperImage.getKey(getActivity(), "CertificateOfGoodConduct") != null) {
//            btGoodConduct.setVisibility(View.GONE);
//            imgGoodConduct.setVisibility(View.VISIBLE);
//            File file = new File(SharedHelperImage.getKey(getActivity(), "CertificateOfGoodConduct"));
//            Picasso.get().load(file).into(imgGoodConduct);
//        }
//        if (SharedHelperImage.getKey(getActivity(), "MotorVehicleInspectionCertificate") != null) {
//            btMotorInspectCertificate.setVisibility(View.GONE);
//            imgMotorInspectCertificate.setVisibility(View.VISIBLE);
//            File file = new File(SharedHelperImage.getKey(getActivity(), "MotorVehicleInspectionCertificate"));
//            Picasso.get().load(file).into(imgMotorInspectCertificate);
//        }
//        if (SharedHelperImage.getKey(getActivity(), "PSVInsuranceCertificate") != null) {
//            btPsvInsurenceCertificate.setVisibility(View.GONE);
//            imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
//            File file = new File(SharedHelperImage.getKey(getActivity(), "PSVInsuranceCertificate"));
//            Picasso.get().load(file).into(imgPsvInsurenceCertificate);
//        }

//        if (uploadTag == "National ID / Passport") {
//            SharedHelperImage.putKey(getActivity(),
//                    "NationalID/Passport", String.valueOf(documentUri));
//        }
//        if (uploadTag == "PSV License") {
//            SharedHelperImage.putKey(getActivity(),
//                    "PSVLicense", String.valueOf(documentUri));
//        }
//        if (uploadTag == "Certificate Of Good Conduct") {
//            SharedHelperImage.putKey(getActivity(),
//                    "CertificateOfGoodConduct",
//                    String.valueOf(documentUri));
//        }
//        if (uploadTag == "Motor Vehicle Inspection Certificate") {
//            SharedHelperImage.putKey(getActivity(),
//                    "MotorVehicleInspectionCertificate",
//                    String.valueOf(documentUri));
//        }
//
//        if (uploadTag == "PSV Insurance Certificate") {
//            SharedHelperImage.putKey(getActivity(),
//                    "PSVInsuranceCertificate", String.valueOf(documentUri));
//        }

//        CheckBox chkPersonalPic = view.findViewById(R.id.chkPersonalPic);
//        CheckBox chkpersonalId = view.findViewById(R.id.chkpersonalId);
//        CheckBox chkPSVLicense = view.findViewById(R.id.chkPSVLicense);
//        CheckBox chkGoodConduct = view.findViewById(R.id.chkGoodConduct);
//        CheckBox chkMotorInspectCertificate = view.findViewById(R.id.chkMotorInspectCertificate);
//        CheckBox chkPsvInsurenceCertificate = view.findViewById(R.id.chkPsvInsurenceCertificate);
        CheckBox chkTerm = view.findViewById(R.id.chkTerm);

        btPersonalPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTag = "LogBook";
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                5);
                    }
                }

//                imgPersonal.setVisibility(View.VISIBLE);
                Dialog d = ImageChoose();
                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                d.show();
            }
        });

//        chkPersonalPic.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                uploadTag = "LogBook";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//                imgPersonal.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });

        btpersonalId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTag = "National ID / Passport";
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                5);
                    }
                }

//                imgPersonalId.setVisibility(View.VISIBLE);
                Dialog d = ImageChoose();
                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                d.show();
            }
        });

//        chkpersonalId.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                uploadTag = "National ID / Passport";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//                imgPersonalId.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });

        btPSVLicense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTag = "PSV License";
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                5);
                    }
                }

//                imgPSVLicense.setVisibility(View.VISIBLE);
                Dialog d = ImageChoose();
                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                d.show();
            }
        });

//        chkPSVLicense.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                uploadTag = "PSV License";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//                imgPSVLicense.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });

        btGoodConduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTag = "Certificate Of Good Conduct";
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                5);
                    }
                }

//                imgGoodConduct.setVisibility(View.VISIBLE);
                Dialog d = ImageChoose();
                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                d.show();
            }
        });

//        chkGoodConduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                uploadTag = "Certificate Of Good Conduct";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//                imgGoodConduct.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });

        btPsvInsurenceCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTag = "PSV Insurance Certificate";
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                5);
                    }
                }

//                imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
                Dialog d = ImageChoose();
                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                d.show();
            }
        });

//        chkMotorInspectCertificate.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                uploadTag = "Motor Vehicle Inspection Certificate";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//                imgMotorInspectCertificate.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });


        btMotorInspectCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadTag = "Motor Vehicle Inspection Certificate";
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.CAMERA},
                                5);
                    }
                }

//                imgMotorInspectCertificate.setVisibility(View.VISIBLE);
                Dialog d = ImageChoose();
                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
                d.show();


            }
        });

//        chkPsvInsurenceCertificate.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                uploadTag = "PSV Insurance Certificate";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//                imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });


//        chkPersonalPic.isChecked() &&
//                chkpersonalId.isChecked() &&
//                chkPSVLicense.isChecked() &&
//                chkGoodConduct.isChecked() &&
//                chkMotorInspectCertificate.isChecked() &&
//                chkPsvInsurenceCertificate.isChecked() &&

//        btnDone.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (chkTerm.isChecked()){
//                    completeDocumentStatus();
//                }
//                else {
//                    chkTerm.setChecked(false);
//                    Toast.makeText(getActivity(), "Upload all documents",
//                            Toast.LENGTH_LONG).show();
//                }
//            }
//        });

        btnDone.setOnClickListener(v -> {
            if (chkTerm.isChecked() &&
                    (imgPersonal.getDrawable() != null)
                    && (imgPersonalId.getDrawable() != null)
                    && (imgPSVLicense.getDrawable() != null)
                    && (imgGoodConduct.getDrawable() != null)
                    && (imgMotorInspectCertificate.getDrawable() != null)
                    && (imgPsvInsurenceCertificate.getDrawable() != null)) {
                completeDocumentStatus();
            } else {
//                chkPersonalPic.setChecked(false);
//                chkpersonalId.setChecked(false);
//                chkPSVLicense.setChecked(false);
//                chkGoodConduct.setChecked(false);
//                chkMotorInspectCertificate.setChecked(false);
//                chkPsvInsurenceCertificate.setChecked(false);
                chkTerm.setChecked(false);
                Toast.makeText(getActivity(), "Upload all documents",
                        Toast.LENGTH_LONG).show();
                displayMessage(getActivity().getString(R.string.upload_all_documents_and_check_term));
            }
//             if ((imgPersonal.getDrawable() != null)
//                     && (imgPersonalId.getDrawable() != null)
//                     && (imgPSVLicense.getDrawable() != null)
//                     && (imgGoodConduct.getDrawable() != null)
//                     && (imgMotorInspectCertificate.getDrawable() != null)
//                     && (imgPsvInsurenceCertificate.getDrawable() != null))
//            {
//                displayMessage(getActivity().getString(R.string.upload_all_documents_and_check_term));
//            }

              /*  else {

            }*/
        });

        if (Waintingdialog.isShowing()) {

        } else {
            Waintingdialog.show();
        }
    }


    private void completeDocumentStatus() {
        try {
            if (helper.isConnectingToInternet()) {
                String url = URLHelper.COMPLETE_DOCUMENT;
                final JsonObjectRequest jsonObjectRequest = new
                        JsonObjectRequest(Request.Method.GET,
                                url,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        Log.e("completeDoc", response.toString());
                                        Waintingdialog.dismiss();
                                        Intent intent1 = new Intent(activity, WaitingForApproval.class);
                                        intent1.putExtra("account_status", "account_status_new");
                                        activity.startActivity(intent1);
                                        activity.finish();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("completeDoc", error.getLocalizedMessage().toString());
                                utils.print("Error", error.toString());

                            }
                        }) {
                            @Override
                            public java.util.Map<String, String> getHeaders() {
                                HashMap<String, String> headers = new HashMap<>();
                                headers.put("X-Requested-With", "XMLHttpRequest");
                                headers.put("Authorization", "Bearer " + token);
                                return headers;
                            }
                        };
                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {
                displayMessage(getString(R.string.oops_connect_your_internet));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dialog ImageChoose() {
        androidx.appcompat.app.AlertDialog.Builder builder = new
                androidx.appcompat.app.AlertDialog.Builder(getActivity());

        if (bmp == null) {
            CharSequence[] ch = {};
            ch = new CharSequence[]{"Gallery", "Camera"};
            builder.setTitle("Choose Image :").setItems(
                    ch,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            switch (which) {
                                case 0:
                                    getPhoto();
                                    break;
                                case 1:
                                    if (ContextCompat.checkSelfPermission(getActivity(),
                                            android.Manifest.permission.CAMERA) ==
                                            PackageManager.PERMISSION_GRANTED) {
                                        takePhoto();
                                    } else {
                                        Toast.makeText(getActivity(), "You have denied camera access permission.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
        } else {
            builder.setTitle("Choose Image :").setItems(
                    new CharSequence[]{"Gallery", "Camera"},
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // TODO Auto-generated method stub
                            switch (which) {
                                case 0:
                                    getPhoto();
                                    break;
                                case 1:
                                    if (ContextCompat.checkSelfPermission(getActivity(),
                                            android.Manifest.permission.CAMERA) ==
                                            PackageManager.PERMISSION_GRANTED) {
                                        takePhoto();
                                    } else {
                                        Toast.makeText(getActivity(), "You have denied camera access permission.",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    break;


                                default:
                                    break;
                            }
                        }
                    });
        }


        return builder.create();
    }

    private void getPhoto() {
        Intent i = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, GET_PICTURE);
    }

    private void takePhoto() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
        startActivityForResult(intent, TAKE_PICTURE);
    }

    private Uri getOutputMediaFileUri(int type) {
        Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
//        Uri contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, getOutputMediaFile(type));
        return uri;
    }

    @SuppressLint("SimpleDateFormat")
    private File getOutputMediaFile(int type) {

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "APP");

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {

                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".png");
        } else {
            return null;
        }

        return mediaFile;
    }


    // Fetches data from url passed
    private class FetchUrl extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try {
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("Background Task data", data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);

        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                Log.d("ParserTask", jsonData[0]);
                DataParser parser = new DataParser();
                Log.d("ParserTask", parser.toString());

                // Starts parsing data
                routes = parser.parse(jObject);
                Log.d("ParserTask", "Executing routes");
                Log.d("ParserTask", routes.toString());

            } catch (Exception e) {
                Log.d("ParserTask", e.toString());
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            try {

                ArrayList<LatLng> points = null;
                PolylineOptions lineOptions = null;

                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }
                    mMap.clear();
                    MarkerOptions markerOptions = new MarkerOptions().title("Source").anchor(0.5f, 0.75f)
                            .position(sourceLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_location));
                    mMap.addMarker(markerOptions);
                    MarkerOptions markerOptions1 = new MarkerOptions().title("Destination").anchor(0.5f, 0.75f)
                            .position(destLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_provider_marker));
                    mMap.addMarker(markerOptions);
                    mMap.addMarker(markerOptions1);


                    LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    LatLngBounds bounds;
                    builder.include(sourceLatLng);
                    builder.include(destLatLng);
                    bounds = builder.build();
                    if (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("ARRIVED")) {
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 80);
//                    mMap.moveCamera(cu);
                    } else {
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 600, 600, 20);
                        mMap.moveCamera(cu);
                    }
                    mMap.getUiSettings().setMapToolbarEnabled(false);


                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(5);
                    lineOptions.color(Color.BLACK);

                    Log.d("onPostExecute", "onPostExecute lineoptions decoded");

                }

                // Drawing polyline in the Google DriverMapFragment for the i-th route
                if (lineOptions != null && points != null) {
                    mMap.addPolyline(lineOptions);
                } else {
                    Log.d("onPostExecute", "without Polylines drawn");
                }

            }catch (Exception e)
            {e.printStackTrace();}

        }
    }

    private void showImage(Uri uri){
        Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.image_show_layout);
        ImageView ivShow = dialog.findViewById(R.id.ivShow);
        Button btCancel = dialog.findViewById(R.id.btCancel);
        Button btOk = dialog.findViewById(R.id.btOk);
        dialog.show();
        btCancel.setOnClickListener(v -> dialog.dismiss());

        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        new BitmapWorkerTask(getActivity(), ivShow,
                "add_revenue").execute(uri);
    }

    void showImage1(Bitmap uri){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.image_show_layout, null);
        builder.setView(view);

        imageShowDialog = builder.create();
        imageShowDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        imageShowDialog.setCanceledOnTouchOutside(false);

//        final Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.image_show_layout);
        ImageView ivShow = view.findViewById(R.id.ivShow);
        Button btCancel = view.findViewById(R.id.btCancel);
        Button btOk = view.findViewById(R.id.btOk);
//        view.show();


        btOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivShow.setImageBitmap(uri);
        if (imageShowDialog.isShowing()) {

        } else {
            imageShowDialog.show();
        }
    }
}







//public class DriverMapFragment extends Fragment implements
//        OnMapReadyCallback,
//        LocationListener,
//        GoogleApiClient.ConnectionCallbacks,
//        GoogleApiClient.OnConnectionFailedListener,
//        View.OnClickListener,
//        GoogleMap.OnCameraMoveListener {
//
//    String docopen = "";
//    Uri documentUri;
//    public static final int REQUEST_LOCATION = 1450;
//    private static final int MEDIA_TYPE_IMAGE = 1;
//    private static final int TAKE_PICTURE = 0;
//    private static final int GET_PICTURE = 1;
//    public static SupportMapFragment mapFragment = null;
//    public static String TAG = "DriverMapFragment";
//    public static int deviceHeight;
//    public static int deviceWidth;
//    public Handler ha;
//    public String myLat = "";
//    public String myLng = "";
//    public double old_lat = 0, old_lng = 0, new_lat, new_lng;
//    public float speed;
//    public double distance = 0;
//    String firstTime = "";
//    LinearLayout lnrNotApproved;
//    String CurrentStatus = " ";
//    String PreviousStatus = " ";
//    String request_id = " ";
//    int method;
//    Activity activity;
//    Context context;
//    CountDownTimer countDownTimer;
//    int value = 0;
//    Marker currentMarker;
//    GoogleApiClient mGoogleApiClient;
//    LocationRequest mLocationRequest;
//    ParserTask parserTask;
//    ImageView imgCurrentLoc;
//    boolean normalPlay = false;
//    boolean push = false;
//    ArrayList<LatLng> points;
//    PolylineOptions lineOptions;
//    //content layout 01
//    TextView txt01Pickup,txtDropOff;
//    TextView txt01Timer;
//    ImageView img01User;
//    TextView txt01UserName;
//    TextView txtSchedule;
//    RatingBar rat01UserRating;
//    //content layer 02
//    ImageView img02User;
//    TextView txt02UserName;
//    RatingBar rat02UserRating;
//    TextView txt02ScheduledTime;
//    TextView txt02From;
//    TextView txt02To;
//    TextView topSrcDestTxtLbl;
//    //content layer 03
//    ImageView img03User;
//    TextView txt03UserName;
//    RatingBar rat03UserRating;
//    ImageButton img03Call;
//    ImageButton img_chat;
//    ImageView img03Status1;
//    ImageView img03Status2;
//    ImageView img03Status3;
//    //content layer 04
//    TextView txt04InvoiceId, txtTotal;
//    TextView txt04BasePrice;
//    TextView txt04Distance;
//    TextView txt04Tax;
//    TextView txt04Total;
//    TextView txt04PaymentMode;
//    TextView txt04Commision;
//    TextView lblProviderName;
//    ImageView paymentTypeImg;
//    //content layer 05
//    Intent service_intent;
//    ImageView img05User;
//    RatingBar rat05UserRating;
//    EditText edt05Comment;
//    //Button layer 01
//    Button btn_01_status, btn_confirm_payment, btn_rate_submit;
//    Button btn_go_offline, btn_go_online;
//    TextView activeStatus;
//    LinearLayout lnrGoOffline, layoutinfo;
//    //Button layer 02
//    Button btn_02_accept;
//    Button btn_02_reject;
//    Button btn_cancel_ride;
//    //map layout
//    LinearLayout ll_01_mapLayer, driverArrived, driverPicked, driveraccepted;
//    //content layout
//    LinearLayout ll_01_contentLayer_accept_or_reject_now;
//    LinearLayout ll_02_contentLayer_accept_or_reject_later;
//    LinearLayout ll_03_contentLayer_service_flow;
//    LinearLayout ll_04_contentLayer_payment;
//    LinearLayout ll_05_contentLayer_feedback;
//    LinearLayout errorLayout;
//    //menu icon
//    ImageView menuIcon;
//    int NAV_DRAWER = 0;
//    DrawerLayout drawer;
//    Boolean isInternet;
//    Utilities utils = new Utilities();
//    MediaPlayer mPlayer;
//    ImageView imgNavigationToSource;
//    String crt_lat = "", crt_lng = "";
//    boolean isRunning = false, timerCompleted = false;
//    TextView destination;
//    ConnectionHelper helper;
//    LinearLayout destinationLayer;
//    View view;
//    boolean doubleBackToExitPressedOnce = false;
//    //Animation
//    Animation slide_down, slide_up;
//    //Distance calculation
////    Intent service_intent;
//    TextView lblDistanceTravelled;
//    boolean scheduleTrip = false;
//    boolean currFragment = true;
//    String type = null, datas = null;
//    String getStatusVariable;
//    CircleImageView img_profile;
//    TextView txtTotalEarning;
//    CardView total_earn_layout;
//    ProgressDialog pDialog;
//    String uploadTag;
//    String providerId="";
//    String userID="";
//    String userFirstName="";
//
//    ArrayList<DocListItemModel> documentItemArrayList;
//    String docIds = "";
//    String adapterPosi = "";
//    DocListAdapter docListAdapter;
//    ImageView imgTest;
//    MyButton btMotorInspectCertificate, btPsvInsurenceCertificate, btGoodConduct,
//            btPSVLicense, btpersonalId, btPersonalPic;
//    ImageView imgPersonal, imgPersonalId, imgPSVLicense, imgGoodConduct,
//            imgMotorInspectCertificate, imgPsvInsurenceCertificate;
//    private String token;
//    //map variable
//    private GoogleMap mMap;
//    private double srcLatitude = 0;
//    private double srcLongitude = 0;
//    private double destLatitude = 0;
//    private double destLongitude = 0;
//    private LatLng sourceLatLng;
//    private LatLng destLatLng;
//    private LatLng currentLatLng;
//    private String bookingId;
//    private String address;
//    private String daddress;
//    private User user = new User();
//    private ImageView sos;
//    //Button layout
//    private CustomDialog customDialog;
//    private Object previous_request_id = " ";
//    private String count;
//    private JSONArray statusResponses;
//    private String feedBackRating;
//    private String feedBackComment;
//    private android.app.AlertDialog Waintingdialog;
//    private android.app.AlertDialog imageShowDialog;
//    private String first = "", boring_depth = "";
//    private String fileExt = "";
//    private byte[] b, b1, b2;
//    private File file;
//    private Bitmap bmp = null;
//    private  String earning = "";
//   
//    private Uri cameraImageUri = null;
//
//    public DriverMapFragment() {
//
//    }
//
//    private static Bitmap getBitmapFromUri(@NonNull Context context, @NonNull Uri uri) throws IOException {
//        Log.e(TAG, "getBitmapFromUri: Resize uri" + uri);
//        ParcelFileDescriptor parcelFileDescriptor =
//                context.getContentResolver().openFileDescriptor(uri, "r");
//        assert parcelFileDescriptor != null;
//        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
//        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
//        parcelFileDescriptor.close();
//        Log.e(TAG, "getBitmapFromUri: Height" + deviceHeight);
//        Log.e(TAG, "getBitmapFromUri: width" + deviceWidth);
//        int maxSize = Math.min(deviceHeight, deviceWidth);
//        if (image != null) {
//            Log.e(TAG, "getBitmapFromUri: Width" + image.getWidth());
//            Log.e(TAG, "getBitmapFromUri: Height" + image.getHeight());
//            int inWidth = image.getWidth();
//            int inHeight = image.getHeight();
//            int outWidth;
//            int outHeight;
//            if (inWidth > inHeight) {
//                outWidth = maxSize;
//                outHeight = (inHeight * maxSize) / inWidth;
//            } else {
//                outHeight = maxSize;
//                outWidth = (inWidth * maxSize) / inHeight;
//            }
//            return Bitmap.createScaledBitmap(image, outWidth, outHeight, false);
//        } else {
//            Toast.makeText(context, context.getString(R.string.valid_image), Toast.LENGTH_SHORT).show();
//            return null;
//        }
//
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this.context = context;
//    }
//
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//        this.activity = activity;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//
//        Bundle bundle = getArguments();
//
//        if (bundle != null) {
//            push = bundle.getBoolean("push");
//        }
//
//        if (push) {
//            isRunning = false;
//        }
//
//        Intent i = getActivity().getIntent();
//        type = i.getStringExtra("type");
//        datas = i.getStringExtra("datas");
//        if (type != null) {
//            checkStatusSchedule();
//        } else {
//            checkStatus();
//        }
//
//        Log.e(TAG, "TYPE: " + type);
//        Log.e(TAG, "DATAS: " + datas);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//
//        // Inflate the layout for this fragment
//        if (view == null) {
//            view = inflater.inflate(R.layout.fragment_map, container, false);
//        }
//        findViewById(view);
//        if (activity == null) {
//            activity = getActivity();
//        }
//
//        if (context == null) {
//            context = getContext();
//        }
//
//        token = SharedHelper.getKey(activity, "access_token");
//        helper = new ConnectionHelper(getActivity());
//
//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
//        deviceHeight = displayMetrics.heightPixels;
//        deviceWidth = displayMetrics.widthPixels;
//        customDialog = new CustomDialog(getActivity());
//        customDialog.setCancelable(true);
//        customDialog.show();
//        //permission to access location
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            // Android M Permission check
//            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//        } else {
//
//            setUpMapIfNeeded();
//            MapsInitializer.initialize(activity);
//        }
//
//        service_intent = new Intent(getActivity(), LocationTracking.class);
//        activity.startService(service_intent);
//        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
//                mMessageReceiver, new IntentFilter("statusresponse"));
//        ha = new Handler();
//
//        if (type != null) {
//            checkStatusSchedule();
//        } else {
//            checkStatus();
//        }
//
//        //check status every 3 sec
//        ha.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //call function
//
//                if (type != null) {
//                    checkStatusSchedule();
//                } else {
////                    checkStatus();
//                }
//
//                ha.postDelayed(this, 3000);
//            }
//        }, 3000);
//
//        btn_01_status.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update(CurrentStatus, request_id);
//            }
//        });
//        btn_confirm_payment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update(CurrentStatus, request_id);
//            }
//        });
//
//        btn_rate_submit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update(CurrentStatus, request_id);
//            }
//        });
//
//        btn_go_offline.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                update(CurrentStatus, request_id);
//            }
//        });
//        btn_go_online.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                goOnline();
//            }
//        });
//
//        errorLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//
//        imgCurrentLoc.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Double crtLat, crtLng;
//                if (!crt_lat.equalsIgnoreCase("") && !crt_lng.equalsIgnoreCase("")) {
//                    crtLat = Double.parseDouble(crt_lat);
//                    crtLng = Double.parseDouble(crt_lng);
//                    if (crtLat != null && crtLng != null) {
//                        LatLng loc = new LatLng(crtLat, crtLng);
//                        CameraPosition cameraPosition = new CameraPosition.Builder().target(loc).zoom(14).build();
//                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                    }
//                }
//            }
//        });
//
//        btn_02_accept.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                countDownTimer.cancel();
//                if (mPlayer != null && mPlayer.isPlaying()) {
//                    mPlayer.stop();
//                    mPlayer = null;
//                }
//                handleIncomingRequest("Accept", request_id);
//            }
//        });
//
//
//        btn_02_reject.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                countDownTimer.cancel();
//                if (mPlayer != null && mPlayer.isPlaying()) {
//                    mPlayer.stop();
//                    mPlayer = null;
//                }
//                handleIncomingRequest("Reject", request_id);
//            }
//        });
//
//        btn_cancel_ride.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showCancelDialog();
//            }
//        });
//
//        menuIcon.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (NAV_DRAWER == 0) {
//                    drawer.openDrawer(Gravity.LEFT);
//                } else {
//                    NAV_DRAWER = 0;
//                    drawer.closeDrawers();
//                }
//            }
//        });
//
//        img_chat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intentChat = new Intent(context, UserChatActivity.class);
//                intentChat.putExtra("requestId", request_id);
//                intentChat.putExtra("providerId", providerId);
//                intentChat.putExtra("userId", userID);
//                intentChat.putExtra("userName", userFirstName);
//                context.startActivity(intentChat);
//            }
//        });
//        img03Call.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String mobile = SharedHelper.getKey(context, "provider_mobile_no");
//                if (mobile != null && !mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
//                    } else {
//                        Intent intent = new Intent(Intent.ACTION_CALL);
//                        intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
//                        startActivity(intent);
//                    }
//                } else {
//                    displayMessage(getString(R.string.user_no_mobile));
//                }
//            }
//        });
//
//        imgNavigationToSource.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                String url = "http://maps.google.com/maps?"
//                        + "saddr=" + address
//                        + "&daddr=" + daddress;
//                Log.e("url", url + "url");
////                Intent intent = new Intent(getActivity(), TrackActivity.class);
////                intent.putExtra("address", url);
////                startActivity(intent);
//                if (btn_01_status.getText().toString().equalsIgnoreCase("ARRIVED")
//                ||btn_01_status.getText().toString().equalsIgnoreCase("PICKEDUP")) {
//                    Log.v("status_navigate",btn_01_status.getText().toString());
////                    Uri naviUri = Uri.parse("http://maps.google.com/maps?"
////                            + "saddr=" + crt_lat + "," + crt_lng
////                            + "&daddr=" + srcLatitude + "," + srcLongitude);
//
//                    Uri naviUri = Uri.parse("http://maps.google.com/maps?"
//                            + "saddr=" + crt_lat + "," + crt_lng
//                            + "&daddr=" + address);
//                    Intent intent = new Intent(Intent.ACTION_VIEW, naviUri);
//                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//                    startActivity(intent);
//                } else {
//                    Log.v("status_navigate",btn_01_status.getText().toString());
//                    Uri naviUri2 = Uri.parse("http://maps.google.com/maps?"
//                            + "saddr=" + srcLatitude + "," + srcLongitude
//                            + "&daddr=" + destLatitude + "," + destLongitude);
//
//                    Intent intent = new Intent(Intent.ACTION_VIEW, naviUri2);
//                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
//                    startActivity(intent);
//                }
//            }
//        });
//        statusCheck();
//        return view;
//    }
//
//    public void statusCheck() {
//        final LocationManager manager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//
//        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//            enableLoc();
//        }
//    }
//
//    private void enableLoc() {
//        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
//                .addApi(LocationServices.API)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle bundle) {
//
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int i) {
//                        mGoogleApiClient.connect();
//                    }
//                })
//                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//                    @Override
//                    public void onConnectionFailed(ConnectionResult connectionResult) {
//
//                        utils.print("Location error", "Location error " + connectionResult.getErrorCode());
//                    }
//                }).build();
//        mGoogleApiClient.connect();
//
//        LocationRequest locationRequest = LocationRequest.create();
//        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(30 * 1000);
//        locationRequest.setFastestInterval(5 * 1000);
//        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest);
//
//        builder.setAlwaysShow(true);
//
//        PendingResult<LocationSettingsResult> result =
//                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
//        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
//            @Override
//            public void onResult(LocationSettingsResult result) {
//                final Status status = result.getStatus();
//                switch (status.getStatusCode()) {
//                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
//                        try {
//                            // Show the dialog by calling startResolutionForResult(),
//                            // and check the result in onActivityResult().
//                            status.startResolutionForResult(getActivity(), REQUEST_LOCATION);
//                        } catch (IntentSender.SendIntentException e) {
//                            // Ignore the error.
//                            e.printStackTrace();
//                        }
//                        break;
//                }
//            }
//        });
////	        }
//
//    }
//
//    private void findViewById(View view) {
//        //Menu Icon
//        menuIcon = view.findViewById(R.id.menuIcon);
//        imgCurrentLoc = view.findViewById(R.id.imgCurrentLoc);
//        drawer = activity.findViewById(R.id.drawer_layout);
//
//        //map layer
//        ll_01_mapLayer = view.findViewById(R.id.ll_01_mapLayer);
//        driverArrived = view.findViewById(R.id.driverArrived);
//        driverPicked = view.findViewById(R.id.driverPicked);
//        driveraccepted = view.findViewById(R.id.driveraccepted);
//
//
//        //Button layer 01
//        btn_01_status = view.findViewById(R.id.btn_01_status);
//        btn_rate_submit = view.findViewById(R.id.btn_rate_submit);
//        btn_confirm_payment = view.findViewById(R.id.btn_confirm_payment);
//        img_profile = view.findViewById(R.id.img_profile);
//        txtTotalEarning = view.findViewById(R.id.txtTotalEarning);
//        total_earn_layout = view.findViewById(R.id.total_earn_layout);
//        //Button layer 02
//        btn_02_accept = view.findViewById(R.id.btn_02_accept);
//        btn_02_reject = view.findViewById(R.id.btn_02_reject);
//        btn_cancel_ride = view.findViewById(R.id.btn_cancel_ride);
//        btn_go_offline = view.findViewById(R.id.btn_go_offline);
//        btn_go_online = view.findViewById(R.id.btn_go_online);
//        activeStatus = view.findViewById(R.id.activeStatus);
////        Button btn_tap_when_arrived, btn_tap_when_pickedup,btn_tap_when_dropped,  btn_tap_when_paid, btn_rate_user
//        //content layer
//        ll_01_contentLayer_accept_or_reject_now = view.findViewById(R.id.ll_01_contentLayer_accept_or_reject_now);
//        ll_02_contentLayer_accept_or_reject_later = view.findViewById(R.id.ll_02_contentLayer_accept_or_reject_later);
//        ll_03_contentLayer_service_flow = view.findViewById(R.id.ll_03_contentLayer_service_flow);
//        ll_04_contentLayer_payment = view.findViewById(R.id.ll_04_contentLayer_payment);
//        ll_05_contentLayer_feedback = view.findViewById(R.id.ll_05_contentLayer_feedback);
//        lnrGoOffline = view.findViewById(R.id.lnrGoOffline);
//        layoutinfo = view.findViewById(R.id.layoutinfo);
//        imgNavigationToSource = view.findViewById(R.id.imgNavigationToSource);
//
//        lnrNotApproved = view.findViewById(R.id.lnrNotApproved);
//
//        //content layout 01
//        txt01Pickup = view.findViewById(R.id.txtPickup);
//        txtDropOff = view.findViewById(R.id.txtDropOff);
//        txt01Timer = view.findViewById(R.id.txt01Timer);
//        img01User = view.findViewById(R.id.img01User);
//        txt01UserName = view.findViewById(R.id.txt01UserName);
//        txtSchedule = view.findViewById(R.id.txtSchedule);
//        rat01UserRating = view.findViewById(R.id.rat01UserRating);
//        sos = view.findViewById(R.id.sos);
////        navigate=(ImageView)view.findViewById(R.id.navigate);
//        LayerDrawable drawable = (LayerDrawable) rat01UserRating.getProgressDrawable();
//        drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
//        drawable.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
//        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
//
//        //content layer 02
//        img02User = view.findViewById(R.id.img02User);
//        txt02UserName = view.findViewById(R.id.txt02UserName);
//        rat02UserRating = view.findViewById(R.id.rat02UserRating);
//        LayerDrawable stars02 = (LayerDrawable) rat02UserRating.getProgressDrawable();
//        stars02.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);
//        txt02ScheduledTime = view.findViewById(R.id.txt02ScheduledTime);
//        lblDistanceTravelled = view.findViewById(R.id.lblDistanceTravelled);
//        txt02From = view.findViewById(R.id.txt02From);
//        txt02To = view.findViewById(R.id.txt02To);
//
//        //content layer 03
//        img03User = view.findViewById(R.id.img03User);
//        txt03UserName = view.findViewById(R.id.txt03UserName);
//        rat03UserRating = view.findViewById(R.id.rat03UserRating);
//        LayerDrawable drawable_02 = (LayerDrawable) rat03UserRating.getProgressDrawable();
//        drawable_02.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
//        drawable_02.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
//        drawable_02.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
//        img03Call = view.findViewById(R.id.img03Call);
//        img_chat = view.findViewById(R.id.img_chat);
//        img03Status1 = view.findViewById(R.id.img03Status1);
//        img03Status2 = view.findViewById(R.id.img03Status2);
//        img03Status3 = view.findViewById(R.id.img03Status3);
//
//        //content layer 04
//        txt04InvoiceId = view.findViewById(R.id.invoice_txt);
//        txtTotal = view.findViewById(R.id.txtTotal);
//        txt04BasePrice = view.findViewById(R.id.txt04BasePrice);
//        txt04Distance = view.findViewById(R.id.txt04Distance);
//        txt04Tax = view.findViewById(R.id.txt04Tax);
//        txt04Total = view.findViewById(R.id.txt04Total);
//        txt04PaymentMode = view.findViewById(R.id.txt04PaymentMode);
//        txt04Commision = view.findViewById(R.id.txt04Commision);
//        destination = view.findViewById(R.id.destination);
//        lblProviderName = view.findViewById(R.id.lblProviderName);
//        paymentTypeImg = view.findViewById(R.id.paymentTypeImg);
//        errorLayout = view.findViewById(R.id.lnrErrorLayout);
//        destinationLayer = view.findViewById(R.id.destinationLayer);
//
//        //content layer 05
//        img05User = view.findViewById(R.id.img05User);
//        rat05UserRating = view.findViewById(R.id.rat05UserRating);
//
//        LayerDrawable stars05 = (LayerDrawable) rat05UserRating.getProgressDrawable();
//        stars05.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP);
//        stars05.getDrawable(1).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
//        stars05.getDrawable(2).setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP);
//        edt05Comment = view.findViewById(R.id.edt05Comment);
//
//        topSrcDestTxtLbl = view.findViewById(R.id.src_dest_txt);
//        earning  = SharedHelper.getKey(getActivity(),"totalearning");
//        total_earn_layout.setVisibility(View.GONE);
//        if(earning!=null && !earning.isEmpty() && !earning.equals("")){
//
//            txtTotalEarning.setText(earning);
//            total_earn_layout.setVisibility(View.VISIBLE);
//        }else
//
//            total_earn_layout.setVisibility(View.GONE);
//        //Load animation
//        slide_down = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_down);
//        slide_up = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_up);
//
//        Picasso.get().load(SharedHelper.getKey(context, "picture"))
//                .placeholder(R.drawable.ic_dummy_user)
//                .error(R.drawable.ic_dummy_user)
//                .into(img_profile);
//        view.setFocusableInTouchMode(true);
//        view.requestFocus();
//        img_profile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), Profile.class));
//            }
//        });
//        view.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                if (event.getAction() != KeyEvent.ACTION_DOWN)
//                    return true;
//
//                if (keyCode == KeyEvent.KEYCODE_BACK) {
//                    if (doubleBackToExitPressedOnce) {
//                        getActivity().finish();
//                        return false;
//                    }
//
//                    doubleBackToExitPressedOnce = true;
//                    Toast.makeText(getActivity(), "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
//
//                    new Handler().postDelayed(new Runnable() {
//
//                        @Override
//                        public void run() {
//                            doubleBackToExitPressedOnce = false;
//                        }
//                    }, 5000);
//                    return true;
//                }
//                return false;
//            }
//        });
//
//        sos.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                showSosDialog();
//            }
//        });
////        navigate.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                redirectMap(sourceLatLng.latitude+"",sourceLatLng.longitude+"",destLatLng.latitude+"", destLatLng.longitude+"");
////            }
////        });
//
//        destinationLayer.setOnClickListener(this);
//        ll_01_contentLayer_accept_or_reject_now.setOnClickListener(this);
//        ll_03_contentLayer_service_flow.setOnClickListener(this);
//        ll_04_contentLayer_payment.setOnClickListener(this);
//        ll_05_contentLayer_feedback.setOnClickListener(this);
//        lnrGoOffline.setOnClickListener(this);
//        errorLayout.setOnClickListener(this);
//
//
//    }
//
//    //    public void redirectMap(String lat1,String lng1,String lat2,String lng2)
////    {
////        String urls="http://maps.google.com/maps?saddr="+lat1+","+lng1+"&daddr="+lat2+","+lng2;
//////        String urls="http://maps.google.com/maps?saddr="+source_address+"&daddr="+dest_address;
////        Log.e("urls",urls+"urls");
////        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
////                Uri.parse(urls));
////        startActivity(intent);
////    }
//    private void mapClear() {
//        if (mMap != null) {
//            if (parserTask != null) {
//                parserTask.cancel(true);
//                parserTask = null;
//            }
//
//            if (!crt_lat.equalsIgnoreCase("") && !crt_lat.equalsIgnoreCase("")) {
//                LatLng myLocation = new LatLng(Double.parseDouble(crt_lat), Double.parseDouble(crt_lng));
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//            }
//
//            mMap.clear();
//            srcLatitude = 0;
//            srcLongitude = 0;
//            destLatitude = 0;
//            destLongitude = 0;
//        }
//    }
//
//    public void clearVisibility() {
//
//        if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.VISIBLE) {
//            ll_01_contentLayer_accept_or_reject_now.startAnimation(slide_down);
//        } else if (ll_02_contentLayer_accept_or_reject_later.getVisibility() == View.VISIBLE) {
//            ll_02_contentLayer_accept_or_reject_later.startAnimation(slide_down);
//        } else if (ll_03_contentLayer_service_flow.getVisibility() == View.VISIBLE) {
//            //ll_03_contentLayer_service_flow.startAnimation(slide_down);
//        } else if (ll_04_contentLayer_payment.getVisibility() == View.VISIBLE) {
//            ll_04_contentLayer_payment.startAnimation(slide_down);
//        } else if (ll_04_contentLayer_payment.getVisibility() == View.VISIBLE) {
//            ll_04_contentLayer_payment.startAnimation(slide_down);
//        } else if (ll_05_contentLayer_feedback.getVisibility() == View.VISIBLE) {
//            ll_05_contentLayer_feedback.startAnimation(slide_down);
//        }
//
//        ll_01_contentLayer_accept_or_reject_now.setVisibility(View.GONE);
//        ll_02_contentLayer_accept_or_reject_later.setVisibility(View.GONE);
//        ll_03_contentLayer_service_flow.setVisibility(View.GONE);
//        ll_04_contentLayer_payment.setVisibility(View.GONE);
//        ll_05_contentLayer_feedback.setVisibility(View.GONE);
//        lnrGoOffline.setVisibility(View.GONE);
//    }
//
//    @TargetApi(Build.VERSION_CODES.M)
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        switch (requestCode) {
//            case 1:
//                if (grantResults.length > 0) {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        setUpMapIfNeeded();
//                        MapsInitializer.initialize(activity);
//
//                        if (ContextCompat.checkSelfPermission(context,
//                                Manifest.permission.ACCESS_FINE_LOCATION)
//                                == PackageManager.PERMISSION_GRANTED) {
//
//                            if (mGoogleApiClient == null) {
//                                buildGoogleApiClient();
//                            }
//                            setUpMapIfNeeded();
//                            MapsInitializer.initialize(activity);
//
//                        }
//                    } else {
//                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                    }
//                }
//                break;
//            case 2:
//                if (grantResults.length > 0) {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        // Permission Granted
//                        //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(Intent.ACTION_CALL);
//                        intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "provider_mobile_no")));
//                        startActivity(intent);
//                    } else {
//                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 2);
//                    }
//                }
//                break;
//            case 3:
//                if (grantResults.length > 0) {
//                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                        // Permission Granted
//                        //Toast.makeText(SignInActivity.this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(Intent.ACTION_CALL);
//                        intent.setData(Uri.parse("tel:" + SharedHelper.getKey(context, "sos")));
//                        startActivity(intent);
//                    } else {
//                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
//                    }
//                }
//                break;
//            default:
//                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        }
//    }
//
//    private void setUpMapIfNeeded() {
//
//
//        if (mMap == null) {
//            FragmentManager fm = getChildFragmentManager();
//            mapFragment = ((SupportMapFragment) fm.findFragmentById(R.id.provider_map));
//            mapFragment.getMapAsync(this);
//        }
//        if (mMap != null) {
//            setupMap();
//        }
//    }
//
//    private void setSourceLocationOnMap(LatLng latLng) {
//
//        if (latLng != null) {
//            mMap.clear();
//            CameraPosition cameraPosition = new CameraPosition.Builder().target(latLng).zoom(14).build();
//            MarkerOptions options = new MarkerOptions().position(latLng).anchor(0.5f, 0.75f);
//            options.position(latLng).isDraggable();
//            mMap.addMarker(options);
//            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        }
//    }
//
//    private void setPickupLocationOnMap() {
//        if (mMap != null) {
//            mMap.clear();
//        }
//        sourceLatLng = currentLatLng;
//        destLatLng = new LatLng(srcLatitude, srcLongitude);
//        CameraPosition cameraPosition = new CameraPosition.Builder().target(destLatLng).zoom(15).build();
//        MarkerOptions options = new MarkerOptions();
//        options.anchor(0.5f, 0.5f).position(destLatLng).isDraggable();
////        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//        if (sourceLatLng != null && destLatLng != null) {
//            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
//            FetchUrl fetchUrl = new FetchUrl();
//            fetchUrl.execute(url);
//        }
//    }
//
//    private void setDestinationLocationOnMap() {
//        if (currentLatLng != null) {
//            sourceLatLng = currentLatLng;
//            destLatLng = new LatLng(destLatitude, destLongitude);
//            //CameraPosition cameraPosition = new CameraPosition.Builder().target(destLatLng).zoom(16).build();
//       /* MarkerOptions options = new MarkerOptions();
//        options.position(destLatLng).isDraggable();
//        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));*/
//            String url = getUrl(sourceLatLng.latitude, sourceLatLng.longitude, destLatLng.latitude, destLatLng.longitude);
//       /* DownloadTask downloadTask = new DownloadTask();
//        // Start downloading json data from Google Directions API
//        downloadTask.execute(url);*/
//            FetchUrl fetchUrl = new FetchUrl();
//            fetchUrl.execute(url);
//        }
//    }
//
//    @SuppressWarnings("MissingPermission")
//    private void setupMap() {
//
//        mMap.setMyLocationEnabled(false);
//        mMap.getUiSettings().setMyLocationButtonEnabled(false);
//        mMap.setBuildingsEnabled(true);
//        mMap.getUiSettings().setCompassEnabled(false);
//        mMap.getUiSettings().setRotateGesturesEnabled(false);
//        mMap.getUiSettings().setTiltGesturesEnabled(false);
//        mMap.setOnCameraMoveListener(this);
//
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//    }
//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        try {
//            // Customise the styling of the base map using a JSON object defined
//            // in a raw resource file.
//            boolean success = googleMap.setMapStyle(
//                    MapStyleOptions.loadRawResourceStyle(
//                            activity, R.raw.style_json));
//
//            if (!success) {
//                Log.e("DriverMapFragment:Style", "Style parsing failed.");
//            } else {
//                Log.e("DriverMapFragment:Style", "Style Applied.");
//            }
//        } catch (Resources.NotFoundException e) {
//            Log.e("DriverMapFragment:Style", "Can't find style. Error: ", e);
//        }
//        mMap = googleMap;
//        // do other tasks here
//        setupMap();
//
//
//        //Initialize Google Play Services
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(context,
//                    Manifest.permission.ACCESS_FINE_LOCATION)
//                    == PackageManager.PERMISSION_GRANTED) {
//                //Location Permission already granted
//                buildGoogleApiClient();
////                mMap.setMyLocationEnabled(true);
//            } else {
//                //Request Location Permission
//                checkLocationPermission();
//            }
//        } else {
//            buildGoogleApiClient();
////            mMap.setMyLocationEnabled(true);
//        }
//    }
//
//    private void checkLocationPermission() {
//        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
//                != PackageManager.PERMISSION_GRANTED) {
//            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.ACCESS_FINE_LOCATION)) {
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//                new AlertDialog.Builder(context)
//                        .setTitle("Location Permission Needed")
//                        .setMessage("This app needs the Location permission, please accept to use location functionality")
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //Prompt the user once explanation has been shown
//                                ActivityCompat.requestPermissions(getActivity(),
//                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                                        1);
//                            }
//                        })
//                        .create()
//                        .show();
//            } else {
//                // No explanation needed, we can request the permission.
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
//                        1);
//            }
//        }
//    }
//
//    @Override
//    public void onConnected(@Nullable Bundle bundle) {
//        mLocationRequest = new LocationRequest();
//        mLocationRequest.setInterval(3000);
//        mLocationRequest.setFastestInterval(3000);
//        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
//        if (ContextCompat.checkSelfPermission(context,
//                Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED && mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
//        }
//    }
//
//    @Override
//    public void onConnectionSuspended(int i) {
//
//    }
//
//    @Override
//    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//
//    }
//
//    @Override
//    public void onLocationChanged(Location location) {
//        //GPSTracker gps = new GPSTracker(getActivity());
//        if ((customDialog != null) && (customDialog.isShowing()))
//            customDialog.dismiss();
//        if (mMap != null) {
//            if (currentMarker != null) {
//                currentMarker.remove();
//            }
//
//            MarkerOptions markerOptions1 = new MarkerOptions()
//                    .position(new LatLng(location.getLatitude(), location.getLongitude()))
//                    .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_currentlocation));
//            currentMarker = mMap.addMarker(markerOptions1);
//
//            Log.e("DriverSide", "DriveronLocationChanged: " + location.getLatitude());
//            Log.e("DriverSide", "DriveronLocationChanged: " + location.getLongitude());
//
//            if (value == 0) {
//                myLat = String.valueOf(location.getLatitude());
//                myLng = String.valueOf(location.getLongitude());
//
//                LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
//                CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(16).build();
//                MarkerOptions markerOptions = new MarkerOptions();
//                markerOptions.position(myLocation).anchor(0.5f, 0.75f);
//                mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                value++;
//            }
//
//            crt_lat = String.valueOf(location.getLatitude());
//            Log.e(TAG, "crt_lat" + crt_lat);
//            crt_lng = String.valueOf(location.getLongitude());
//            Log.e(TAG, "crt_lng" + crt_lng);
//            currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
//
//            if (type != null) {
//                checkStatusSchedule();
//            } else {
//                checkStatus();
//            }
//
//        }
//
//
//
//    }
//
//    @Override
//    public void onClick(View v) {
//
//    }
//
//    @Override
//    public void onCameraMove() {
//        utils.print("Current marker", "Zoom Level " + mMap.getCameraPosition().zoom);
//        if (currentMarker != null) {
//            if (!mMap.getProjection().getVisibleRegion().latLngBounds.contains(currentMarker.getPosition())) {
//                utils.print("Current marker", "Current Marker is not visible");
//                if (imgCurrentLoc.getVisibility() == View.GONE) {
//                    imgCurrentLoc.setVisibility(View.VISIBLE);
//                }
//            } else {
//                utils.print("Current marker", "Current Marker is visible");
//                if (imgCurrentLoc.getVisibility() == View.VISIBLE) {
//                    imgCurrentLoc.setVisibility(View.GONE);
//                }
//                if (mMap.getCameraPosition().zoom < 15.0f) {
//                    if (imgCurrentLoc.getVisibility() == View.GONE) {
//                        imgCurrentLoc.setVisibility(View.VISIBLE);
//                    }
//                }
//            }
//        }
//    }
//
//    private String downloadUrl(String strUrl) throws IOException {
//        String data = "";
//        InputStream iStream = null;
//        HttpURLConnection urlConnection = null;
//        try {
//            URL url = new URL(strUrl);
//
//            // Creating an http connection to communicate with url
//            urlConnection = (HttpURLConnection) url.openConnection();
//
//            // Connecting to url
//            urlConnection.connect();
//
//            // Reading data from url
//            iStream = urlConnection.getInputStream();
//
//            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
//
//            StringBuffer sb = new StringBuffer();
//
//            String line = "";
//            while ((line = br.readLine()) != null) {
//                sb.append(line);
//            }
//
//            data = sb.toString();
//            Log.d("downloadUrl", data);
//            br.close();
//
//        } catch (Exception e) {
//            Log.d("Exception", e.toString());
//        } finally {
//            iStream.close();
//            urlConnection.disconnect();
//        }
//        return data;
//    }
//
//    private String getUrl(double source_latitude, double source_longitude, double dest_latitude, double dest_longitude) {
//
//        // Origin of route
//        String str_origin = "origin=" + source_latitude + "," + source_longitude;
//
//        // Destination of route
//        String str_dest = "destination=" + dest_latitude + "," + dest_longitude;
//
//
//        // Sensor enabled
//        String sensor = "sensor=false" + "&key=" + "AIzaSyD0gzQ43R7S8iiJLL-oUjesnc6hu-EvCII";
//
//        // Building the parameters to the web service
//        String parameters = str_origin + "&" + str_dest + "&" + sensor;
//
//        // Output format
//        String output = "json";
//
//        // Building the url to the web service
//        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
//
//        Log.v("directionUrl",url+" ");
//        return url;
//    }
//
//    private void checkDocumentStatus() {
//        try {
//
//            if (helper.isConnectingToInternet()) {
//                String url = URLHelper.CHECK_DOCUMENT;
//                utils.print("Destination Current Lat", "" + crt_lat);
//                final JsonObjectRequest jsonObjectRequest = new
//                        JsonObjectRequest(Request.Method.GET,
//                                url,
//                                null,
//                                new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        Log.e("checkDocumentStatus", response + "Document");
//                                        try {
//                                            if (response.getString("status").equalsIgnoreCase("0")) {
//
//                                                JSONArray jsonArray = response.getJSONArray("data");
//                                                for (int i = 0; i < jsonArray.length(); i++) {
//                                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                                    SharedHelper.putKey(getActivity(), jsonObject.optString("name"),
//                                                            jsonObject.optString("id"));
//                                                }
//                                                if (Waintingdialog == null) {
//
//                                                    if(docopen.equalsIgnoreCase("")) {
//                                                        docopen = "yes";
//                                                        Intent intent1 = new Intent(activity, DocUploadActivity.class);
//                                                        activity.startActivity(intent1);
//                                                    }
////                                                    documentUploadFirstDialog();
//                                                }
//                                            } else {
//                                                if (isMyServiceRunning(StatusCheckServie.class)) {
//                                                    activity.stopService(new Intent(activity, StatusCheckServie.class));
//                                                    ha.removeMessages(0);
//                                                    lnrGoOffline.setVisibility(View.GONE);
//                                                    lnrNotApproved.setVisibility(View.VISIBLE);
//
////                                                    Intent intent1 = new Intent(activity, DocumentUpload.class);
////                                                    Intent intent1 = new Intent(activity, WaitingForApproval.class);
////                                                    intent1.putExtra("account_status", "account_status_new");
////                                                    activity.startActivity(intent1);
////                                                    activity.finish();
//                                                }
//                                            }
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                utils.print("Error", error.toString());
//
//                            }
//                        }) {
//                            @Override
//                            public java.util.Map<String, String> getHeaders() {
//                                HashMap<String, String> headers = new HashMap<>();
//                                headers.put("X-Requested-With", "XMLHttpRequest");
//                                headers.put("Authorization", "Bearer " + token);
//                                return headers;
//                            }
//                        };
//                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//            } else {
//                displayMessage(getString(R.string.oops_connect_your_internet));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void checkStatus() {
//        try {
//
//            if (helper.isConnectingToInternet()) {
//                String url = URLHelper.base + "api/provider/trip?latitude=" + crt_lat +
//                        "&longitude=" + crt_lng;
//
//                utils.print("Destination Current Lat", "" + crt_lat);
//                utils.print("Destination Current Lng", "" + crt_lng);
//
//                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                    @SuppressLint("SetTextI18n")
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        if (errorLayout.getVisibility() == View.VISIBLE) {
//                            errorLayout.setVisibility(View.GONE);
//                        }
//                        Log.e("CheckStatus", "" + response.toString());
//                        //SharedHelper.putKey(context, "currency", response.optString("currency"));
//                        try {
//                            if (response.optString("service_status").equalsIgnoreCase("offline")) {
//
//                               if (isAdded())activeStatus.setText(getActivity().getString(R.string.offline));
//                            }
//                            if (response.optJSONArray("requests").length() > 0) {
//
//                                providerId=response.optJSONArray("requests")
//                                        .getJSONObject(0).optJSONObject("request")
//                                   .optString("provider_id");
//                                userID=response.optJSONArray("requests")
//                                        .getJSONObject(0).optJSONObject("request")
//                                .optString("user_id");
//
//                                JSONObject jsonObject = response.optJSONArray("requests")
//                                        .getJSONObject(0).optJSONObject("request").optJSONObject("user");
//                                userFirstName=jsonObject.optString("first_name");
//                                user.setFirstName(jsonObject.optString("first_name"));
////                                user.setLastName(jsonObject.optString("last_name"));
//                                user.setEmail(jsonObject.optString("email"));
//                                if (jsonObject.optString("picture").startsWith("http"))
//                                    user.setImg(jsonObject.optString("picture"));
//                                else
//                                    user.setImg(URLHelper.base + "storage/app/public/" + jsonObject.optString("picture"));
//                                user.setRating(jsonObject.optString("rating"));
//                                user.setMobile(jsonObject.optString("mobile"));
//                                bookingId = response.optJSONArray("requests").getJSONObject(0)
//                                        .optJSONObject("request").optString("booking_id");
//                                address = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("s_address");
//                                daddress = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("d_address");
//                            }
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                        if (response.optString("account_status").equals("new") ||
//                                response.optString("account_status").equals("onboarding")) {
//                            ha.removeMessages(0);
//                            checkDocumentStatus();
//                        } else {
//
//                            if (response.optString("service_status").equals("offline")) {
//                                ha.removeMessages(0);
////                    Intent intent = new Intent(activity, Offline.class);
////                    activity.startActivity(intent);
//                                goOffline();
//                            } else {
//
//                                if (response.optJSONArray("requests") != null && response.optJSONArray("requests").length() > 0) {
//                                    JSONObject statusResponse = null;
//                                    try {
//                                        statusResponses = response.optJSONArray("requests");
//                                        statusResponse = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request");
//                                        request_id = response.optJSONArray("requests").getJSONObject(0).optString("request_id");
//                                        Log.e("request_idjson", request_id + "");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                        lblDistanceTravelled.setText("Distance Travelled :"
//                                                + String.format("%f", Float.parseFloat(LocationTracking.distance * 0.001 + "")) + " Km");
//                                    }
//                                    if ((statusResponse != null) && (request_id != null)) {
//                                        if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" ")) && mMap != null) {
//                                            previous_request_id = request_id;
//                                            srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                            srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                            
//                                            destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                            destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//                                            //noinspection deprecation
//                                            setSourceLocationOnMap(currentLatLng);
//                                            setPickupLocationOnMap();
//                                            sos.setVisibility(View.GONE);
//
//                                        }
//                                        utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
////                                        String ok = "ok";
////                                        if (ok.equals(ok))
//                                        if (!PreviousStatus.equals(statusResponse.optString("status"))) {
////                                            || statusResponse.optString("paid").equals("1") || statusResponse.optString("paid").equals("0")
//                                            PreviousStatus = statusResponse.optString("status");
//                                            clearVisibility();
//                                            utils.print("responseObj(" + request_id + ")", statusResponse.toString());
//                                            utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
//                                            if (!statusResponse.optString("status").equals("SEARCHING")) {
//                                                timerCompleted = false;
//                                                if (mPlayer != null && mPlayer.isPlaying()) {
//                                                    mPlayer.stop();
//                                                    mPlayer = null;
//                                                    countDownTimer.cancel();
//                                                }
//                                            }
//                                            if (statusResponse.optString("status").equals("SEARCHING")) {
//                                                scheduleTrip = false;
//                                                if (!timerCompleted) {
//                                                    setValuesTo_ll_01_contentLayer_accept_or_reject_now(statusResponses);
//                                                    if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.GONE) {
//                                                        ll_01_contentLayer_accept_or_reject_now.startAnimation(slide_up);
//                                                    }
//                                                    ll_01_contentLayer_accept_or_reject_now.setVisibility(View.VISIBLE);
//                                                }
//                                                CurrentStatus = "STARTED";
//                                            } else if (statusResponse.optString("status").equals("STARTED")) {
//                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                                }
//                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText("ARRIVED");
//                                                CurrentStatus = "ARRIVED";
//                                                sos.setVisibility(View.GONE);
//                                                if (srcLatitude == 0 && srcLongitude == 0 && destLatitude == 0 && destLongitude == 0) {
//                                                    mapClear();
//                                                    srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                                    srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                                    destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                                    destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//                                                    //noinspection deprecation
//                                                    //
//                                                    setSourceLocationOnMap(currentLatLng);
//                                                    setPickupLocationOnMap();
//                                                }
//
//                                                setSourceLocationOnMap(currentLatLng);
//                                                setPickupLocationOnMap();
//
//                                                sos.setVisibility(View.GONE);
//                                                btn_cancel_ride.setVisibility(View.VISIBLE);
//                                                destinationLayer.setVisibility(View.VISIBLE);
//                                                layoutinfo.setVisibility(View.GONE);
//                                                String address = statusResponse.optString("s_address");
//                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                                    destination.setText(address);
//                                                else
//                                                    destination.setText(getAddress(statusResponse.optString("s_latitude"),
//                                                            statusResponse.optString("s_longitude")));
//                                                topSrcDestTxtLbl.setText("Pick up Location");
//                                               
//
//                                            } else if (statusResponse.optString("status").equals("ARRIVED")) {
//                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                                }
//                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText("PICKEDUP");
//                                                sos.setVisibility(View.GONE);
//                                                img03Status1.setImageResource(R.drawable.arrived_select);
//                                                img03Status2.setImageResource(R.drawable.pickeddisable);
//                                                driveraccepted.setVisibility(View.VISIBLE);
//                                                driverArrived.setVisibility(View.GONE);
//                                                driverPicked.setVisibility(View.GONE);
//                                                CurrentStatus = "PICKEDUP";
//                                                driveraccepted.setVisibility(View.GONE);
//                                                driverArrived.setVisibility(View.VISIBLE);
//                                                driverPicked.setVisibility(View.GONE);
//
//                                                btn_cancel_ride.setVisibility(View.VISIBLE);
//                                                destinationLayer.setVisibility(View.VISIBLE);
//                                                String address = statusResponse.optString("d_address");
//                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                                    destination.setText(address);
//                                                else
//                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
//                                                            statusResponse.optString("d_longitude")));
//                                                topSrcDestTxtLbl.setText("Drop Location");
//
//
//                                                setSourceLocationOnMap(currentLatLng);
//                                                setPickupLocationOnMap();
//
//                                            } else if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                                }
//                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText("TAP WHEN DROPPED");
//                                                sos.setVisibility(View.VISIBLE);
//                                                mapClear();
//                                                srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                                srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                                destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                                destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//                                                //noinspection deprecation
//                                                //
//                                                setSourceLocationOnMap(currentLatLng);
//                                                setDestinationLocationOnMap();
//
////                                                navigate.setVisibility(View.VISIBLE);
//                                                img03Status1.setImageResource(R.drawable.arrived_select);
//                                                img03Status2.setImageResource(R.drawable.pickup_select);
//                                                driveraccepted.setVisibility(View.GONE);
//                                                driverArrived.setVisibility(View.GONE);
//                                                driverPicked.setVisibility(View.VISIBLE);
//                                                CurrentStatus = "DROPPED";
//                                                destinationLayer.setVisibility(View.VISIBLE);
//                                                layoutinfo.setVisibility(View.GONE);
//                                                btn_cancel_ride.setVisibility(View.GONE);
//                                                String address = statusResponse.optString("d_address");
//                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                                    destination.setText(address);
//                                                else
//                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
//                                                            statusResponse.optString("d_longitude")));
//                                                topSrcDestTxtLbl.setText("Drop Location");
//                                              
//
//
//
//
//
//                                            } else if (statusResponse.optString("status").equals("DROPPED")
//                                                    && statusResponse.optString("paid").equals("0")) {
//                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
//                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
//                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
//                                                }
//                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
//                                                img03Status1.setImageResource(R.drawable.arriveddisable);
//                                                img03Status2.setImageResource(R.drawable.pickeddisable);
//                                                driveraccepted.setVisibility(View.VISIBLE);
//                                                driverArrived.setVisibility(View.GONE);
//                                                driverPicked.setVisibility(View.GONE);
//
//                                                btn_01_status.setText("CONFIRM PAYMENT");
//                                                sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.GONE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                CurrentStatus = "COMPLETED";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            } else if (statusResponse.optString("status").equals("DROPPED")
//                                                    && statusResponse.optString("paid").equals("0")) {
//                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
//                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
//                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
//                                                }
//                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
//                                                img03Status1.setImageResource(R.drawable.arriveddisable);
//                                                img03Status2.setImageResource(R.drawable.pickeddisable);
//                                                driveraccepted.setVisibility(View.VISIBLE);
//                                                driverArrived.setVisibility(View.GONE);
//                                                driverPicked.setVisibility(View.GONE);
//
//                                                btn_01_status.setText("CONFIRM PAYMENT");
//                                                sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.GONE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                CurrentStatus = "COMPLETED";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            } else if (statusResponse.optString("status").equals("COMPLETED")
//                                                    && statusResponse.optString("paid").equals("0")) {
//
//                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
//                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
//                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
//                                                }
//                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
//                                                img03Status1.setImageResource(R.drawable.arriveddisable);
//                                                img03Status2.setImageResource(R.drawable.pickeddisable);
//                                                driveraccepted.setVisibility(View.VISIBLE);
//                                                driverArrived.setVisibility(View.GONE);
//                                                driverPicked.setVisibility(View.GONE);
//
//                                                btn_01_status.setText("CONFIRM PAYMENT");
//                                                sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.GONE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                CurrentStatus = "COMPLETED";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            } else if (statusResponse.optString("status").equals("COMPLETED")
//                                                    && statusResponse.optString("paid").equals("1")) {
////                                                ok = "not";
//                                                if (ll_04_contentLayer_payment.getVisibility() == View.VISIBLE) {
//                                                    ll_04_contentLayer_payment.setVisibility(View.GONE);
//                                                }
//
//                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
//                                                if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
//                                                    ll_05_contentLayer_feedback.startAnimation(slide_up);
//                                                }
//                                                ll_04_contentLayer_payment.setVisibility(View.GONE);
//                                                edt05Comment.setText("");
//                                                ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
//                                                sos.setVisibility(View.GONE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText("SUBMIT");
//                                                CurrentStatus = "RATE";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            } else if (statusResponse.optString("status").equals("SCHEDULED")) {
//                                                if (mMap != null) {
//                                                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                        return;
//                                                    }
//                                                    mMap.clear();
//                                                }
//                                                clearVisibility();
//                                                CurrentStatus = "SCHEDULED";
//                                                if (lnrGoOffline.getVisibility() == View.GONE) {
//                                                    lnrGoOffline.startAnimation(slide_up);
//                                                }
//                                                lnrGoOffline.setVisibility(View.VISIBLE);
//                                                utils.print("statusResponse", "null");
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            }
//                                        }
//                                    } else {
//                                        if (mMap != null) {
//                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                return;
//                                            }
//                                            timerCompleted = false;
//                                            mMap.clear();
//                                            if (mPlayer != null && mPlayer.isPlaying()) {
//                                                mPlayer.stop();
//                                                mPlayer = null;
//                                                countDownTimer.cancel();
//                                            }
//
//                                        }
//                                       
//                                        LocationTracking.distance = 0.0f;
//
//                                        clearVisibility();
//                                        lnrGoOffline.setVisibility(View.VISIBLE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        CurrentStatus = "ONLINE";
//                                        PreviousStatus = "NULL";
//                                        utils.print("statusResponse", "null");
//                                    }
//
//                                } else {
//                                    timerCompleted = false;
//                                    if (!PreviousStatus.equalsIgnoreCase("NULL")) {
//                                        utils.print("response", "null");
//                                        if (mMap != null) {
//                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                return;
//                                            }
//                                            mMap.clear();
//                                        }
//                                        if (mPlayer != null && mPlayer.isPlaying()) {
//                                            mPlayer.stop();
//                                            mPlayer = null;
//                                            countDownTimer.cancel();
//                                        }
//                                        clearVisibility();
//                                        //mapClear();
//                                        lnrGoOffline.setVisibility(View.VISIBLE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        CurrentStatus = "ONLINE";
//                                        PreviousStatus = "NULL";
//                                        utils.print("statusResponse", "null");
//                                       
//                                        LocationTracking.distance = 0.0f;
//                                    }
//
//                                }
//                            }
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        utils.print("Error", error.toString());
//                        //errorHandler(error);
//                        timerCompleted = false;
//                        mapClear();
//                        clearVisibility();
//                        CurrentStatus = "ONLINE";
//                        PreviousStatus = "NULL";
//                        lnrGoOffline.setVisibility(View.VISIBLE);
//                        destinationLayer.setVisibility(View.GONE);
//                        layoutinfo.setVisibility(View.VISIBLE);
//                        if (mPlayer != null && mPlayer.isPlaying()) {
//                            mPlayer.stop();
//                            mPlayer = null;
//                            countDownTimer.cancel();
//                        }
//                        displayMessage(error.toString());
////                        if (errorLayout.getVisibility() != View.VISIBLE) {
////                            errorLayout.setVisibility(View.VISIBLE);
////                            sos.setVisibility(View.GONE);
////                        }
//                    }
//                }) {
//                    @Override
//                    public java.util.Map<String, String> getHeaders() {
//                        HashMap<String, String> headers = new HashMap<>();
//                        headers.put("X-Requested-With", "XMLHttpRequest");
//                        headers.put("Authorization", "Bearer " + token);
//                        return headers;
//                    }
//                };
//                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//            } else {
//                displayMessage(getString(R.string.oops_connect_your_internet));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void checkStatusSchedule() {
//
//        try {
//
//            if (helper.isConnectingToInternet()) {
//
//                String url = URLHelper.base + "api/provider/trip?latitude=" + crt_lat + "&longitude=" + crt_lng;
//                Log.e(TAG, "URL:" + url);
//
//                utils.print("Destination Current Lat", "" + crt_lat);
//                utils.print("Destination Current Lng", "" + crt_lng);
//
//                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//                        if (errorLayout.getVisibility() == View.VISIBLE) {
//                            errorLayout.setVisibility(View.GONE);
//                        }
//                        Log.e("Schedule CheckStatus", "" + response.toString());
//                        //SharedHelper.putKey(context, "currency", response.optString("currency"));
//
//                        if (response.optJSONArray("requests").length() > 0) {
//
//                            try {
//                                if (response.optJSONArray("requests").length() > 0) {
//                                    JSONObject jsonObject = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optJSONObject("user");
//                                    user.setFirstName(jsonObject.optString("first_name"));
////                                    user.setLastName(jsonObject.optString("last_name"));
//                                    user.setEmail(jsonObject.optString("email"));
//                                    if (jsonObject.optString("picture").startsWith("http"))
//                                        user.setImg(jsonObject.optString("picture"));
//                                    else
//                                        user.setImg(URLHelper.base + "storage/app/public/" + jsonObject.optString("picture"));
//                                    user.setRating(jsonObject.optString("rating"));
//                                    user.setMobile(jsonObject.optString("mobile"));
//                                    bookingId = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("booking_id");
//                                    address = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("s_address");
//                                    daddress = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("d_address");
//
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            if (response.optString("service_status").equals("offline")) {
//                                ha.removeMessages(0);
////                    Intent intent = new Intent(activity, Offline.class);
////                    activity.startActivity(intent);
//                                goOffline();
//                            } else {
//
//                                if (response.optJSONArray("requests") != null && response.optJSONArray("requests").length() > 0) {
//                                    JSONObject statusResponse = null;
//                                    try {
//                                        statusResponses = response.optJSONArray("requests");
//                                        statusResponse = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request");
//                                        request_id = response.optJSONArray("requests").getJSONObject(0).optString("request_id");
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//
//                                    if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                        lblDistanceTravelled.setText("Distance Travelled :"
//                                                + String.format("%f", Float.parseFloat(LocationTracking.distance * 0.001 + "")) + " Km");
//                                    }
//                                    if ((statusResponse != null) && (request_id != null)) {
//                                        if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" ")) && mMap != null) {
//                                            previous_request_id = request_id;
//                                            srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                            srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                            destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                            destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//                                            //noinspection deprecation
//                                            setSourceLocationOnMap(currentLatLng);
//                                            setPickupLocationOnMap();
//                                            sos.setVisibility(View.GONE);
//                                        }
//                                        utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
//                                        if (!PreviousStatus.equals(statusResponse.optString("status"))) {
//                                            PreviousStatus = statusResponse.optString("status");
//                                            clearVisibility();
//                                            utils.print("responseObj(" + request_id + ")", statusResponse.toString());
//                                            utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
//                                            if (!statusResponse.optString("status").equals("SEARCHING")) {
//                                                timerCompleted = false;
//                                                if (mPlayer != null && mPlayer.isPlaying()) {
//                                                    mPlayer.stop();
//                                                    mPlayer = null;
//                                                    countDownTimer.cancel();
//                                                }
//                                            }
//                                            if (statusResponse.optString("status").equals("SEARCHING")) {
//                                                scheduleTrip = false;
//                                                if (!timerCompleted) {
//                                                    setValuesTo_ll_01_contentLayer_accept_or_reject_now(statusResponses);
//                                                    if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.GONE) {
//                                                        ll_01_contentLayer_accept_or_reject_now.startAnimation(slide_up);
//                                                    }
//                                                    ll_01_contentLayer_accept_or_reject_now.setVisibility(View.VISIBLE);
//                                                }
//                                                CurrentStatus = "STARTED";
//                                            } else if (statusResponse.optString("status").equals("STARTED")) {
//                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                                }
//                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText(getString(R.string.tap_when_arrived));
//                                                CurrentStatus = "ARRIVED";
//                                                sos.setVisibility(View.GONE);
//                                                if (srcLatitude == 0 && srcLongitude == 0 && destLatitude == 0 && destLongitude == 0) {
//                                                    mapClear();
//                                                    srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                                    srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                                    destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                                    destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//                                                    //noinspection deprecation
//                                                    //
//                                                    setSourceLocationOnMap(currentLatLng);
//                                                    setPickupLocationOnMap();
//                                                }
//                                                sos.setVisibility(View.GONE);
//                                                btn_cancel_ride.setVisibility(View.VISIBLE);
//                                                destinationLayer.setVisibility(View.VISIBLE);
//                                                String address = statusResponse.optString("s_address");
//                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                                    destination.setText(address);
//                                                else
//                                                    destination.setText(getAddress(statusResponse.optString("s_latitude"),
//                                                            statusResponse.optString("s_longitude")));
//                                                topSrcDestTxtLbl.setText(getString(R.string.pick_up));
//                                               
//
//                                            } else if (statusResponse.optString("status").equals("ARRIVED")) {
//                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                                }
//                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText(getString(R.string.tap_when_pickedup));
//                                                sos.setVisibility(View.GONE);
//                                                img03Status1.setImageResource(R.drawable.arrived_select);
//                                                driveraccepted.setVisibility(View.GONE);
//                                                driverArrived.setVisibility(View.VISIBLE);
//                                                driverPicked.setVisibility(View.GONE);
//                                                CurrentStatus = "PICKEDUP";
//
//                                                btn_cancel_ride.setVisibility(View.VISIBLE);
//                                                destinationLayer.setVisibility(View.VISIBLE);
//                                                String address = statusResponse.optString("d_address");
//                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                                    destination.setText(address);
//                                                else
//                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
//                                                            statusResponse.optString("d_longitude")));
//                                                topSrcDestTxtLbl.setText(getString(R.string.drop_at));
//                                               
//
//                                            } else if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                                }
//                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText(getString(R.string.tap_when_dropped));
//                                                sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.VISIBLE);
//                                                img03Status1.setImageResource(R.drawable.arrived_select);
//                                                img03Status2.setImageResource(R.drawable.pickup_select);
//                                                CurrentStatus = "DROPPED";
//                                                driveraccepted.setVisibility(View.GONE);
//                                                driverArrived.setVisibility(View.GONE);
//                                                driverPicked.setVisibility(View.VISIBLE);
//                                                destinationLayer.setVisibility(View.VISIBLE);
//                                                layoutinfo.setVisibility(View.GONE);
//                                                btn_cancel_ride.setVisibility(View.GONE);
//                                                String address = statusResponse.optString("d_address");
//                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                                    destination.setText(address);
//                                                else
//                                                    destination.setText(getAddress(statusResponse.optString("d_latitude"),
//                                                            statusResponse.optString("d_longitude")));
//                                                topSrcDestTxtLbl.setText(getString(R.string.drop_at));
////
//                                               srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                        srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                        destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                        destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//
//                                        setSourceLocationOnMap(currentLatLng);
//                                        setDestinationLocationOnMap();
//
//
//
//                                            } else if (statusResponse.optString("status").equals("DROPPED")
//                                                    && statusResponse.optString("paid").equals("0")) {
//                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
//                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
//                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
//                                                }
//                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
//                                                img03Status1.setImageResource(R.drawable.arrived);
//                                                img03Status2.setImageResource(R.drawable.pickup);
//                                                btn_01_status.setText(getString(R.string.tap_when_paid));
//                                                sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.GONE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                CurrentStatus = "COMPLETED";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            } else if (statusResponse.optString("status").equals("COMPLETED")
//                                                    && statusResponse.optString("paid").equals("0")) {
//                                                setValuesTo_ll_04_contentLayer_payment(statusResponses);
//                                                if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
//                                                    ll_04_contentLayer_payment.startAnimation(slide_up);
//                                                }
//                                                ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
//                                                img03Status1.setImageResource(R.drawable.arrived);
//                                                img03Status2.setImageResource(R.drawable.pickup);
//                                                btn_01_status.setText(getString(R.string.tap_when_paid));
//                                                sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.GONE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                CurrentStatus = "COMPLETED";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            } else if (statusResponse.optString("status").equals("DROPPED") && statusResponse.optString("paid").equals("1")) {
//                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
//                                                if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
//                                                    ll_05_contentLayer_feedback.startAnimation(slide_up);
//                                                }
//                                                ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText(getString(R.string.rate_user));
//                                                sos.setVisibility(View.VISIBLE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                CurrentStatus = "RATE";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            } else if (statusResponse.optString("status").equals("COMPLETED") && statusResponse.optString("paid").equals("1")) {
//
//                                                if (ll_05_contentLayer_feedback.getVisibility() == View.VISIBLE) {
//                                                    ll_05_contentLayer_feedback.setVisibility(View.GONE);
//                                                }
//                                                setValuesTo_ll_05_contentLayer_feedback(statusResponses);
//                                                if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
//                                                    ll_05_contentLayer_feedback.startAnimation(slide_up);
//                                                }
//                                                ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
//                                                edt05Comment.setText("");
//                                                sos.setVisibility(View.GONE);
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText(getString(R.string.rate_user));
//                                                CurrentStatus = "RATE";
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                                type = null;
//
//                                            } else if (statusResponse.optString("status").equals("SCHEDULED")) {
//                                                if (mMap != null) {
//                                                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                        return;
//                                                    }
//                                                    mMap.clear();
//                                                }
//                                                clearVisibility();
//                                                CurrentStatus = "SCHEDULED";
//                                                if (lnrGoOffline.getVisibility() == View.GONE) {
//                                                    lnrGoOffline.startAnimation(slide_up);
//                                                }
//                                                lnrGoOffline.setVisibility(View.VISIBLE);
//                                                utils.print("statusResponse", "null");
//                                                destinationLayer.setVisibility(View.GONE);
//                                                layoutinfo.setVisibility(View.VISIBLE);
//                                               
//                                                LocationTracking.distance = 0.0f;
//                                            }
//                                        }
//                                    } else {
//                                        if (mMap != null) {
//                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                return;
//                                            }
//                                            timerCompleted = false;
//                                            mMap.clear();
//                                            if (mPlayer != null && mPlayer.isPlaying()) {
//                                                mPlayer.stop();
//                                                mPlayer = null;
//                                                countDownTimer.cancel();
//                                            }
//
//                                        }
//                                       
//                                        LocationTracking.distance = 0.0f;
//
//                                        clearVisibility();
//                                        lnrGoOffline.setVisibility(View.VISIBLE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        CurrentStatus = "ONLINE";
//                                        PreviousStatus = "NULL";
//                                        utils.print("statusResponse", "null");
//                                    }
//
//                                } else {
//                                    timerCompleted = false;
//                                    if (!PreviousStatus.equalsIgnoreCase("NULL")) {
//                                        utils.print("response", "null");
//                                        if (mMap != null) {
//                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                return;
//                                            }
//                                            mMap.clear();
//                                        }
//                                        if (mPlayer != null && mPlayer.isPlaying()) {
//                                            mPlayer.stop();
//                                            mPlayer = null;
//                                            countDownTimer.cancel();
//                                        }
//                                        clearVisibility();
//                                        //mapClear();
//                                        lnrGoOffline.setVisibility(View.VISIBLE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        CurrentStatus = "ONLINE";
//                                        PreviousStatus = "NULL";
//                                        utils.print("statusResponse", "null");
//                                       
//                                        LocationTracking.distance = 0.0f;
//                                    }
//
//                                }
//                            }
//
//                        } else {
//                            try {
//                                JSONArray statusResponses = new JSONArray(datas);
//                                Log.e(TAG, "new_array: " + statusResponses);
//                                for (int i = 0; i < statusResponses.length(); i++) {
//
//                                    JSONObject getjsonobj = statusResponses.getJSONObject(i);
//                                    JSONObject jsonobj = getjsonobj.getJSONObject("request");
//                                    Log.e(TAG, "jsonobj: " + jsonobj);
//                                    getStatusVariable = jsonobj.optString("status");
//                                    request_id = jsonobj.optString("id");
//                                    Log.e(TAG, "REQ_ID: " + request_id);
//                                    Log.e(TAG, "getStatusVariable: " + getStatusVariable);
//
//
//                                    if ((jsonobj != null) && (request_id != null)) {
//                                        if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" ")) && mMap != null) {
//                                            Log.e(TAG, "Previous req");
//                                            previous_request_id = request_id;
//                                            srcLatitude = Double.valueOf(jsonobj.optString("s_latitude"));
//                                            srcLongitude = Double.valueOf(jsonobj.optString("s_longitude"));
//                                            destLatitude = Double.valueOf(jsonobj.optString("d_latitude"));
//                                            destLongitude = Double.valueOf(jsonobj.optString("d_longitude"));
//                                            //noinspection deprecation
//                                            setSourceLocationOnMap(currentLatLng);
//                                            setPickupLocationOnMap();
//                                            sos.setVisibility(View.GONE);
//                                        }
//                                        utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + jsonobj.optString("status"));
//                                        if (!PreviousStatus.equals(jsonobj.optString("status"))) {
//                                            Log.e(TAG, "Previous req1111");
//                                            PreviousStatus = getStatusVariable;
//                                            clearVisibility();
//                                            utils.print("responseObj(" + request_id + ")", jsonobj.toString());
//                                            utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + jsonobj.optString("status"));
//                                            if (!getStatusVariable.equals("SEARCHING")) {
//                                                timerCompleted = false;
//                                                if (mPlayer != null && mPlayer.isPlaying()) {
//                                                    mPlayer.stop();
//                                                    mPlayer = null;
//                                                    countDownTimer.cancel();
//                                                }
//                                            }
//
//                                            if (getStatusVariable.equals("SCHEDULED")) {
//                                                setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                                if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                                    //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                                }
//                                                ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                                btn_01_status.setText(getString(R.string.tap_when_arrived));
//                                                CurrentStatus = "ARRIVED";
//                                                sos.setVisibility(View.GONE);
//                                                if (srcLatitude == 0 && srcLongitude == 0 && destLatitude == 0 && destLongitude == 0) {
//                                                    mapClear();
//                                                    srcLatitude = Double.valueOf(jsonobj.optString("s_latitude"));
//                                                    srcLongitude = Double.valueOf(jsonobj.optString("s_longitude"));
//                                                    destLatitude = Double.valueOf(jsonobj.optString("d_latitude"));
//                                                    destLongitude = Double.valueOf(jsonobj.optString("d_longitude"));
//                                                    //noinspection deprecation
//                                                    //
//                                                    setSourceLocationOnMap(currentLatLng);
//                                                    setPickupLocationOnMap();
//                                                }
//                                                sos.setVisibility(View.GONE);
//                                                btn_cancel_ride.setVisibility(View.VISIBLE);
//                                                destinationLayer.setVisibility(View.VISIBLE);
//                                                layoutinfo.setVisibility(View.GONE);
//                                                String address = jsonobj.optString("s_address");
//                                                if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                                    destination.setText(address);
//                                                else
//                                                    destination.setText(getAddress(jsonobj.optString("s_latitude"),
//                                                            jsonobj.optString("s_longitude")));
//                                                topSrcDestTxtLbl.setText(getString(R.string.pick_up));
//                                               
//
//                                            }
//                                        }
//                                    }
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        utils.print("Error", error.toString());
//                        //errorHandler(error);
//                        timerCompleted = false;
//                        mapClear();
//                        clearVisibility();
//                        CurrentStatus = "ONLINE";
//                        PreviousStatus = "NULL";
//                        lnrGoOffline.setVisibility(View.VISIBLE);
//                        destinationLayer.setVisibility(View.GONE);
//                        layoutinfo.setVisibility(View.VISIBLE);
//                        if (mPlayer != null && mPlayer.isPlaying()) {
//                            mPlayer.stop();
//                            mPlayer = null;
//                            countDownTimer.cancel();
//                        }
//                        if (errorLayout.getVisibility() != View.VISIBLE) {
//                            errorLayout.setVisibility(View.VISIBLE);
//                            sos.setVisibility(View.GONE);
//                        }
//                    }
//                }) {
//                    @Override
//                    public java.util.Map<String, String> getHeaders() {
//                        HashMap<String, String> headers = new HashMap<>();
//                        headers.put("X-Requested-With", "XMLHttpRequest");
//                        headers.put("Authorization", "Bearer " + token);
//                        Log.e(TAG, "HEADERS: " + headers.toString());
//                        return headers;
//                    }
//                };
//                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//            } else {
//                displayMessage(getString(R.string.oops_connect_your_internet));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setValuesTo_ll_01_contentLayer_accept_or_reject_now(JSONArray status) {
//        JSONObject statusResponse = new JSONObject();
//        try {
//            statusResponse = status.getJSONObject(0).getJSONObject("request");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            if (!status.getJSONObject(0).optString("time_left_to_respond").equals("")) {
//                count = status.getJSONObject(0).getString("time_left_to_respond");
//            } else {
//                count = "0";
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        countDownTimer = new CountDownTimer(Integer.parseInt(count) * 1000, 1000) {
//
//            @SuppressLint("SetTextI18n")
//            public void onTick(long millisUntilFinished) {
//                txt01Timer.setText("" + millisUntilFinished / 1000);
//                if (mPlayer == null) {
//                    mPlayer = MediaPlayer.create(context, R.raw.alert_tone);
//                } else {
//                    if (!mPlayer.isPlaying()) {
//                        mPlayer.start();
//                    }
//                }
//                isRunning = true;
//                timerCompleted = false;
//
//            }
//
//            public void onFinish() {
//                txt01Timer.setText("0");
//                mapClear();
//                clearVisibility();
//                if (mMap != null) {
//                    mMap.clear();
//                }
//                if (mPlayer != null && mPlayer.isPlaying()) {
//                    mPlayer.stop();
//                    mPlayer = null;
//                }
//                ll_01_contentLayer_accept_or_reject_now.setVisibility(View.GONE);
//                CurrentStatus = "ONLINE";
//                PreviousStatus = "NULL";
//                lnrGoOffline.setVisibility(View.VISIBLE);
//                destinationLayer.setVisibility(View.GONE);
//                isRunning = false;
//                timerCompleted = true;
//                handleIncomingRequest("Reject", request_id);
//            }
//        };
//
//
//        countDownTimer.start();
//
//        try {
//            if (!statusResponse.optString("schedule_at").trim().equalsIgnoreCase("") && !statusResponse.optString("schedule_at").equalsIgnoreCase("null")) {
//                txtSchedule.setVisibility(View.VISIBLE);
//                String strSchedule = "";
//                try {
//                    strSchedule = getDate(statusResponse.optString("schedule_at")) + "th " + getMonth(statusResponse.optString("schedule_at"))
//                            + " " + getYear(statusResponse.optString("schedule_at")) + " at " + getTime(statusResponse.optString("schedule_at"));
//                } catch (ParseException e) {
//                    e.printStackTrace();
//                }
//                txtSchedule.setText("Scheduled at : " + strSchedule);
//            } else {
//                txtSchedule.setVisibility(View.GONE);
//            }
//
//            final JSONObject user = statusResponse.getJSONObject("user");
//            if (user != null) {
//                if (!user.optString("picture").equals("null")) {
////                    new DownloadImageTask(img01User).execute(user.getString("picture"));
//                    //Glide.with(activity).load(URLHelper.base+"storage/app/public/"+user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img01User);
//                    if (user.optString("picture").startsWith("http"))
//                        Picasso.get().load(user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img01User);
//                    else
//                        Picasso.get().load(URLHelper.base + "storage/app/public/" + user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img01User);
//                } else {
//                    img01User.setImageResource(R.drawable.ic_dummy_user);
//                }
//                final User userProfile = this.user;
//                img01User.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, ShowProfile.class);
//                        intent.putExtra("user", userProfile);
//                        startActivity(intent);
//                    }
//                });
//                txt01UserName.setText(user.optString("first_name"));
//
////                + " " + user.optString("last_name"
//                if (statusResponse.getJSONObject("user").getString("rating") != null) {
//                    rat01UserRating.setRating(Float.valueOf(user.getString("rating")));
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        txt01Pickup.setText(address);
//        txtDropOff.setText(daddress);
//    }
//
//    private void setValuesTo_ll_03_contentLayer_service_flow(JSONArray status) {
//        JSONObject statusResponse = new JSONObject();
//        Log.e(TAG, "statusResponse: " + statusResponse);
//        try {
//            statusResponse = status.getJSONObject(0).getJSONObject("request");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            JSONObject user = statusResponse.getJSONObject("user");
//            if (user != null) {
//                if (!user.optString("mobile").equals("null")) {
//                    SharedHelper.putKey(context, "provider_mobile_no", "" + user.optString("mobile"));
//                } else {
//                    SharedHelper.putKey(context, "provider_mobile_no", "");
//                }
//
//                if (!user.optString("picture").equals("null")) {
//                    //Glide.with(activity).load(URLHelper.base+"storage/app/public/"+user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img03User);
//                    if (user.optString("picture").startsWith("http"))
//                        Picasso.get().load(user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img03User);
//                    else
//                        Picasso.get().load(URLHelper.base + "storage/app/public/" + user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img03User);
//                } else {
//                    img03User.setImageResource(R.drawable.ic_dummy_user);
//                }
//                final User userProfile = this.user;
//                img03User.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, ShowProfile.class);
//                        intent.putExtra("user", userProfile);
//                        startActivity(intent);
//                    }
//                });
//
//                txt03UserName.setText(user.optString("first_name"));
//                if (statusResponse.getJSONObject("user").getString("rating") != null) {
//                    rat03UserRating.setRating(Float.valueOf(user.getString("rating")));
//                } else {
//                    rat03UserRating.setRating(0);
//                }
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void setValuesTo_ll_04_contentLayer_payment(JSONArray status) {
//        JSONObject statusResponse = new JSONObject();
//        try {
//            statusResponse = status.getJSONObject(0).getJSONObject("request");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        try {
//            txt04InvoiceId.setText(bookingId);
//            txt04BasePrice.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("fixed"));
//            txt04Distance.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("distance"));
//            txt04Tax.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("tax"));
//            txt04Total.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("total"));
//            txt04PaymentMode.setText(statusResponse.getString("payment_mode"));
//            txt04Commision.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("commision"));
//            txtTotal.setText(SharedHelper.getKey(context, "currency") + "" + statusResponse.getJSONObject("payment").optString("total"));
//            if (statusResponse.getString("payment_mode").equals("CASH")) {
//                paymentTypeImg.setImageResource(R.drawable.money1);
//                btn_confirm_payment.setVisibility(View.VISIBLE);
//            } else {
//                paymentTypeImg.setImageResource(R.drawable.visa_icon);
//                btn_confirm_payment.setVisibility(View.GONE);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private void setValuesTo_ll_05_contentLayer_feedback(JSONArray status) {
//        rat05UserRating.setRating(1.0f);
//        feedBackRating = "1";
//        rat05UserRating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
//            public void onRatingChanged(RatingBar ratingBar, float rating,
//                                        boolean fromUser) {
//                utils.print("rating", rating + "");
//                if (rating < 1.0f) {
//                    rat05UserRating.setRating(1.0f);
//                    feedBackRating = "1";
//                }
//                feedBackRating = String.valueOf((int) rating);
//            }
//        });
//        JSONObject statusResponse = new JSONObject();
//        try {
//            statusResponse = status.getJSONObject(0).getJSONObject("request");
//            JSONObject user = statusResponse.getJSONObject("user");
//            if (user != null) {
//                lblProviderName.setText(user.optString("first_name"));
////                + " "+user.optString("last_name")
//                if (!user.optString("picture").equals("null")) {
////                    new DownloadImageTask(img05User).execute(user.getString("picture"));
//                    //Glide.with(activity).load(URLHelper.base+"storage/app/public/"+user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img05User);
//                    if (user.optString("picture").startsWith("http"))
//                        Picasso.get().load(user.getString("picture"))
//                                .placeholder(R.drawable.ic_dummy_user)
//                                .error(R.drawable.ic_dummy_user).into(img05User);
//                    else
//                        Picasso.get().load(URLHelper.base + "storage/app/public/" + user
//                                .getString("picture")).placeholder(R.drawable.ic_dummy_user)
//                                .error(R.drawable.ic_dummy_user).into(img05User);
//                } else {
//                    img05User.setImageResource(R.drawable.ic_dummy_user);
//                }
//                final User userProfile = this.user;
//                img05User.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(context, ShowProfile.class);
//                        intent.putExtra("user", userProfile);
//                        startActivity(intent);
//                    }
//                });
//
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        feedBackComment = edt05Comment.getText().toString();
//    }
//
//    private void update(final String status, String id) {
//        Log.v("Status",status+" ");
//        customDialog = new CustomDialog(activity);
//        customDialog.setCancelable(false);
//        customDialog.show();
//        if (status.equals("ONLINE")) {
//
//            JSONObject param = new JSONObject();
//            try {
//                param.put("service_status", "offline");
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_AVAILABILITY_API, param, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    customDialog.dismiss();
//                    if (response != null) {
//                        if (response.optJSONObject("service").optString("status").equalsIgnoreCase("offline")) {
//                            goOffline();
//                            activeStatus.setText(getActivity().getString(R.string.offline));
//                        } else {
//                            displayMessage(getString(R.string.something_went_wrong));
//                        }
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    customDialog.dismiss();
//                    utils.print("Error", error.toString());
//                    errorHandler(error);
//                }
//            }) {
//                @Override
//                public java.util.Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("X-Requested-With", "XMLHttpRequest");
//                    headers.put("Authorization", "Bearer " + token);
//                    return headers;
//                }
//            };
//            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//        } else {
//            String url;
//            JSONObject param = new JSONObject();
//            if (status.equals("RATE")) {
//                url = URLHelper.base + "api/provider/trip/" + id + "/rate";
//                try {
//                    param.put("rating", feedBackRating);
//                    param.put("comment", edt05Comment.getText().toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                utils.print("Input", param.toString());
//            } else {
//                url = URLHelper.base + "api/provider/trip/" + id;
//                try {
//                    param.put("_method", "PATCH");
//                    param.put("status", status);
//                    if (status.equalsIgnoreCase("DROPPED")) {
//                        param.put("latitude", crt_lat);
//                        param.put("longitude", crt_lng);
//                        param.put("distance", LocationTracking.distance * 0.001);
//                    }
//                    utils.print("Input", param.toString());
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, param, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    customDialog.dismiss();
//                    if (response.optJSONObject("requests") != null) {
//                        utils.print("request", response.optJSONObject("requests").toString());
//                    }
//                    if (status.equalsIgnoreCase("RATE")) {
//                        lnrGoOffline.setVisibility(View.VISIBLE);
//                        destinationLayer.setVisibility(View.GONE);
//                        layoutinfo.setVisibility(View.VISIBLE);
//                        if (crt_lat!=null&& crt_lat!="") {
//                            LatLng myLocation = new LatLng(Double.parseDouble(crt_lat), Double.parseDouble(crt_lng));
//                            CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(14).build();
//                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
//                        }
//                        mapClear();
//                        clearVisibility();
//                    }
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    customDialog.dismiss();
//                    utils.print("Error", error.toString());
//                    if (status.equals("RATE")) {
//                        lnrGoOffline.setVisibility(View.VISIBLE);
//                        destinationLayer.setVisibility(View.GONE);
//                        layoutinfo.setVisibility(View.VISIBLE);
//                    }
//                    //errorHandler(error);
//                }
//            }) {
//                @Override
//                public java.util.Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<>();
//                    headers.put("X-Requested-With", "XMLHttpRequest");
//                    headers.put("Authorization", "Bearer " + token);
//                    return headers;
//                }
//            };
//            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//        }
//    }
//
//    public void cancelRequest(String id, String reason) {
//
//        customDialog = new CustomDialog(context);
//        customDialog.setCancelable(false);
//        customDialog.show();
//
//        try {
//            JSONObject object = new JSONObject();
//            object.put("request_id", id);
//            object.put("cancel_reason", reason);
//            Log.e("", "request_id" + id);
//
//            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.CANCEL_REQUEST_API, object, new Response.Listener<JSONObject>() {
//                @Override
//                public void onResponse(JSONObject response) {
//                    customDialog.dismiss();
//                    utils.print("CancelRequestResponse", response.toString());
//                    Toast.makeText(context, "" + "You have cancelled the request", Toast.LENGTH_SHORT).show();
//                    mapClear();
//                    clearVisibility();
//                    lnrGoOffline.setVisibility(View.VISIBLE);
//                    destinationLayer.setVisibility(View.GONE);
//                    CurrentStatus = "ONLINE";
//                    PreviousStatus = "NULL";
//                }
//            }, new Response.ErrorListener() {
//                @Override
//                public void onErrorResponse(VolleyError error) {
//                    customDialog.dismiss();
//                    String json = null;
//                    String Message;
//                    NetworkResponse response = error.networkResponse;
//                    if (response != null && response.data != null) {
//
//                        try {
//                            JSONObject errorObj = new JSONObject(new String(response.data));
//
//                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                                try {
//                                    displayMessage(errorObj.optString("message"));
//                                } catch (Exception e) {
//                                    displayMessage("Something went wrong");
//                                    e.printStackTrace();
//                                }
//                            } else if (response.statusCode == 401) {
//                                GoToBeginActivity();
//                            } else if (response.statusCode == 422) {
//
//                                json = trimMessage(new String(response.data));
//                                if (json != "" && json != null) {
//                                    displayMessage(json);
//                                } else {
//                                    displayMessage(getString(R.string.please_try_again));
//                                }
//                            } else if (response.statusCode == 503) {
//                                displayMessage(getString(R.string.server_down));
//                            } else {
//                                displayMessage(getString(R.string.please_try_again));
//                            }
//
//                        } catch (Exception e) {
//                            displayMessage("Something went wrong");
//                            e.printStackTrace();
//                        }
//
//                    } else {
//                        displayMessage(getString(R.string.please_try_again));
//                    }
//                }
//            }) {
//                @Override
//                public java.util.Map<String, String> getHeaders() {
//                    HashMap<String, String> headers = new HashMap<String, String>();
//                    headers.put("X-Requested-With", "XMLHttpRequest");
//                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
//                    Log.e("", "Access_Token" + SharedHelper.getKey(context, "access_token"));
//                    return headers;
//                }
//            };
//
//            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void handleIncomingRequest(final String status, String id) {
//        if (!((Activity) context).isFinishing()) {
//            customDialog = new CustomDialog(activity);
//            customDialog.setCancelable(true);
//            customDialog.show();
//        }
//        String url = URLHelper.base + "api/provider/trip/" + id;
//
//        if (status.equals("Accept")) {
//            method = Request.Method.POST;
//        } else {
//            method = Request.Method.DELETE;
//        }
//
//        Log.v("handlerequest",url+" "+method);
//        final JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(method, url, null, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray response) {
//                if (isAdded()) {
//                    customDialog.dismiss();
//                    if (status.equals("Accept")) {
//                        Toast.makeText(context, "Request accepted successfully", Toast.LENGTH_SHORT).show();
//                    } else {
//                        if (!timerCompleted) {
//                            if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.VISIBLE) {
//
//
//                                mapClear();
//                                clearVisibility();
//                                if (mMap != null) {
//                                    mMap.clear();
//                                }
//                                ll_01_contentLayer_accept_or_reject_now.setVisibility(View.GONE);
//                                CurrentStatus = "ONLINE";
//                                PreviousStatus = "NULL";
//                                lnrGoOffline.setVisibility(View.VISIBLE);
//                                destinationLayer.setVisibility(View.GONE);
//                                layoutinfo.setVisibility(View.VISIBLE);
//                                isRunning = false;
//                                timerCompleted = true;
//                                handleIncomingRequest("Reject", request_id);
//                            }
//                            Toast.makeText(context, "Request rejected successfully", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(context, "Request Timeout", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                customDialog.dismiss();
//                utils.print("Error", error.toString());
//                //errorHandler(error);
//
//            }
//        }) {
//            @Override
//            public java.util.Map<String, String> getHeaders() {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//    }
//
//    public void errorHandler(VolleyError error) {
//        utils.print("Error", error.toString());
//        String json = null;
//        String Message;
//        NetworkResponse response = error.networkResponse;
//        if (response != null && response.data != null) {
//
//            try {
//                JSONObject errorObj = new JSONObject(new String(response.data));
//                utils.print("ErrorHandler", "" + errorObj.toString());
//                if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                    try {
//                        displayMessage(errorObj.optString("message"));
//                    } catch (Exception e) {
//                        displayMessage(getString(R.string.something_went_wrong));
//                    }
//                } else if (response.statusCode == 401) {
//                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
//                    GoToBeginActivity();
//                } else if (response.statusCode == 422) {
//                    json = Ilyft.trimMessage(new String(response.data));
//                    if (json != "" && json != null) {
//                        displayMessage(json);
//                    } else {
//                        displayMessage(getString(R.string.please_try_again));
//                    }
//
//                } else if (response.statusCode == 503) {
//                    displayMessage(getString(R.string.server_down));
//                } else {
//                    displayMessage(getString(R.string.please_try_again));
//                }
//
//            } catch (Exception e) {
//                displayMessage(getString(R.string.something_went_wrong));
//            }
//
//        } else {
//            displayMessage(getString(R.string.please_try_again));
//        }
//    }
//
//    public void displayMessage(String toastString) {
//        utils.print("displayMessage", "" + toastString);
////        Snackbar.make(getActivity().findViewById(android.R.id.content), toastString, Snackbar.LENGTH_SHORT)
////                .setAction("Action", null).show();
//    }
//
//    public void GoToBeginActivity() {
//        SharedHelper.putKey(activity, "loggedIn", getString(R.string.False));
//        Intent mainIntent = new Intent(activity, SplashScreen.class);
//        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        startActivity(mainIntent);
//        activity.finish();
//    }
//
//    public void goOffline() {
//        try {
//            btn_go_offline.setVisibility(View.GONE);
//            btn_go_online.setVisibility(View.VISIBLE);
////            FragmentManager manager = MainActivity.fragmentManager;
////            FragmentTransaction transaction = manager.beginTransaction();
////            transaction.replace(R.id.content, new Offline());
////            transaction.commitAllowingStateLoss();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public void goOnline() {
//        customDialog = new CustomDialog(activity);
//        customDialog.setCancelable(true);
//        customDialog.show();
//        JSONObject param = new JSONObject();
//        try {
//            param.put("service_status", "active");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_AVAILABILITY_API, param, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                customDialog.dismiss();
//                if (response != null) {
//                    try {
//
//                        if (response.optJSONObject("service").optString("status").equalsIgnoreCase("active")) {
////                            Intent intent = new Intent(context, MainActivity.class);
////                            context.startActivity(intent);
//                            activeStatus.setText(getActivity().getString(R.string.online));
//                            btn_go_offline.setVisibility(View.VISIBLE);
//                            btn_go_online.setVisibility(View.GONE);
//                        } else {
//                            displayMessage(getString(R.string.something_went_wrong));
//                        }
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                } else {
//                    customDialog.dismiss();
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                customDialog.dismiss();
//                utils.print("Error", error.toString());
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//                if (response != null && response.data != null) {
//
//                    try {
//                        JSONObject errorObj = new JSONObject(new String(response.data));
//
//                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                            try {
//                                displayMessage(errorObj.optString("message"));
//                            } catch (Exception e) {
//                                displayMessage(getString(R.string.something_went_wrong));
//                            }
//                        } else if (response.statusCode == 401) {
//                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
//                            GoToBeginActivity();
//                        } else if (response.statusCode == 422) {
//                            json = Ilyft.trimMessage(new String(response.data));
//                            if (json != "" && json != null) {
//                                displayMessage(json);
//                            } else {
//                                displayMessage(getString(R.string.please_try_again));
//                            }
//
//                        } else if (response.statusCode == 503) {
//                            displayMessage(getString(R.string.server_down));
//                        } else {
//                            displayMessage(getString(R.string.please_try_again));
//                        }
//
//                    } catch (Exception e) {
//                        displayMessage(getString(R.string.something_went_wrong));
//                    }
//
//                } else {
//                    if (error instanceof NoConnectionError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof NetworkError) {
//                        displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof TimeoutError) {
//                        goOnline();
//                    }
//                }
//            }
//        }) {
//            @Override
//            public java.util.Map<String, String> getHeaders() {
//                HashMap<String, String> headers = new HashMap<>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "Bearer " + token);
//                return headers;
//            }
//        };
//        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//    }
//
//    @Override
//    public void onDestroy() {
//        if (mPlayer != null && mPlayer.isPlaying()) {
//            mPlayer.stop();
//            mPlayer = null;
//        }
//        ha.removeCallbacksAndMessages(null);
//        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
//        super.onDestroy();
//    }
//
//    private String getMonth(String date) throws ParseException {
//        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(d);
//        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
//        return monthName;
//    }
//
//    private String getDate(String date) throws ParseException {
//        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(d);
//        String dateName = new SimpleDateFormat("dd").format(cal.getTime());
//        return dateName;
//    }
//
//    private String getYear(String date) throws ParseException {
//        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(d);
//        String yearName = new SimpleDateFormat("yyyy").format(cal.getTime());
//        return yearName;
//    }
//
//    private String getTime(String date) throws ParseException {
//        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
//        Calendar cal = Calendar.getInstance();
//        cal.setTime(d);
//        String timeName = new SimpleDateFormat("hh:mm a").format(cal.getTime());
//        return timeName;
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == REQUEST_LOCATION) {
//            if (resultCode == Activity.RESULT_CANCELED) {
//                Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//        switch (requestCode) {
//            case TAKE_PICTURE:
//                if (resultCode == Activity.RESULT_OK) {
//                    //  cameraImageUri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
//                    if (cameraImageUri != null) {
//                        getActivity().getContentResolver()
//                                .notifyChange(cameraImageUri, null);
//
//                        fileExt = ".png";
//                        file = new File(cameraImageUri.getPath().toString());
//                        first = file.getName() + fileExt;
////                        galleryImageUri = null;
//                        try {
////                            if ((docIds != "") && (docIds != null)) {
////                                new BitmapWorkerTask(getActivity(), imgTest,
////                                        "add_revenue").execute(cameraImageUri);
////
////                                SharedHelper.putKey(activity, "ImageURI"+adapterPosi ,first);
////
////                             /*   Handler handler = new Handler();
////                               Runnable runnable = new Runnable() {
////                                   @Override
////                                   public void run() {
////
////
////                                   }
////                               }).handler.postDelayed(runnable,1000);;*/
////                                saveProfileAccount("LogBook",
////                                        AppHelper.getFileDataFromDrawable(imgTest.getDrawable()),
////                                        docIds);
////                            }
//
//                            if (uploadTag == "LogBook") {
//                                documentUri = cameraImageUri;
//                                btPersonalPic.setVisibility(View.GONE);
//                                imgPersonal.setVisibility(View.VISIBLE);
//                                new BitmapWorkerTask(getActivity(), imgPersonal,
//                                        "add_revenue").execute(cameraImageUri);
//                                showImage(cameraImageUri);
//
////                                saveProfileAccount("LogBook",
////                                        AppHelper.getFileDataFromDrawable(imgPersonal.getDrawable()),
////                                        SharedHelper.getKey(getActivity(), uploadTag));
//
//
//                            }
//                            if (uploadTag == "National ID / Passport") {
//                                documentUri = cameraImageUri;
//                                btpersonalId.setVisibility(View.GONE);
//                                imgPersonalId.setVisibility(View.VISIBLE);
//                                new BitmapWorkerTask(getActivity(), imgPersonalId,
//                                        "add_revenue").execute(cameraImageUri);
//
//                                saveProfileAccount("NationalIDPassport",
//                                        AppHelper.getFileDataFromDrawable(imgPersonalId.getDrawable()),
//                                        SharedHelper.getKey(getActivity(), uploadTag));
//                            }
//                            if (uploadTag == "PSV License") {
//                                documentUri = cameraImageUri;
//                                btPSVLicense.setVisibility(View.GONE);
//                                imgPSVLicense.setVisibility(View.VISIBLE);
//                                new BitmapWorkerTask(getActivity(), imgPSVLicense,
//                                        "add_revenue").execute(cameraImageUri);
//
//                                saveProfileAccount("PSVLicense",
//                                        AppHelper.getFileDataFromDrawable(imgPSVLicense.getDrawable()),
//                                        SharedHelper.getKey(getActivity(), uploadTag));
//                            }
//
//                            if (uploadTag == "Certificate Of Good Conduct") {
//                                documentUri = cameraImageUri;
//                                btGoodConduct.setVisibility(View.GONE);
//                                imgGoodConduct.setVisibility(View.VISIBLE);
//                                new BitmapWorkerTask(getActivity(), imgGoodConduct,
//                                        "add_revenue").execute(cameraImageUri);
//
//                                saveProfileAccount("CertificateOfGoodConduct",
//                                        AppHelper.getFileDataFromDrawable(imgGoodConduct.getDrawable()),
//                                        SharedHelper.getKey(getActivity(), uploadTag));
//                            }
//
//
//                            if (uploadTag == "Motor Vehicle Inspection Certificate") {
//                                documentUri = cameraImageUri;
//                                btMotorInspectCertificate.setVisibility(View.GONE);
//                                imgMotorInspectCertificate.setVisibility(View.VISIBLE);
//                                new BitmapWorkerTask(getActivity(), imgMotorInspectCertificate,
//                                        "add_revenue").execute(cameraImageUri);
//                                saveProfileAccount("MotorVehicleInspectionCertificate",
//                                        AppHelper.getFileDataFromDrawable(imgMotorInspectCertificate.getDrawable()),
//                                        SharedHelper.getKey(getActivity(), uploadTag));
//                            }
//                            if (uploadTag == "PSV Insurance Certificate") {
//                                documentUri = cameraImageUri;
//                                btPsvInsurenceCertificate.setVisibility(View.GONE);
//                                imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
//                                new BitmapWorkerTask(getActivity(), imgPsvInsurenceCertificate,
//                                        "add_revenue").execute(cameraImageUri);
//                                saveProfileAccount("PSVInsuranceCertificate",
//                                        AppHelper.getFileDataFromDrawable(imgPsvInsurenceCertificate.getDrawable()),
//                                        SharedHelper.getKey(getActivity(), uploadTag));
//                            }
//
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//                }
//                break;
//
//            case GET_PICTURE:
//                if (resultCode == Activity.RESULT_OK) {
//                    {
//                        Uri uri = data.getData();
//                        File f1 = new File(uri.getPath());
//                        try {
//                            Bitmap resizeImg = getBitmapFromUri(getActivity(), uri);
////                            if (resizeImg != null) {
////                                if ((docIds != "") && (docIds != null)) {
////                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
////                                            AppHelper.getPath(getActivity(), uri));
////                                    imgTest.setImageBitmap(reRotateImg);
////
////                                   // Uri selectedImageURI = data.getData();
////                                    DocListItemModel placeWorkModel = new DocListItemModel(); // the model between activity and adapter
////                                   // placeWorkModel.setImage(Integer.parseInt(convertImage2Base64()));  // here i pass the photo
////                                    placeWorkModel.setImage(String.valueOf(f1));
////                                    documentItemArrayList.add(placeWorkModel);
////
////                                    docListAdapter.updateList(documentItemArrayList); // add this
////
////                                    docListAdapter.notifyDataSetChanged();
////
////                                    SharedHelper.putKey(activity, "ImageURI"+adapterPosi ,first);
////                                    saveProfileAccount("LogBook",
////                                            AppHelper.getFileDataFromDrawable(imgTest.getDrawable()),
////                                            docIds);
////                                }
////                            }
//
//                            if (resizeImg != null) {
//                                if (uploadTag == "LogBook") {
////                                    btPersonalPic.setVisibility(View.GONE);
////                                    imgPersonal.setVisibility(View.VISIBLE);
//                                    documentUri = uri;
//                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
//                                            AppHelper.getPath(getActivity(), uri));
//
//                                    showImage1(reRotateImg);
////                                    imgPersonal.setImageBitmap(reRotateImg);
////                                    saveProfileAccount("LogBook",
////                                            AppHelper.getFileDataFromDrawable(imgPersonal.getDrawable()),
////                                            SharedHelper.getKey(getActivity(), uploadTag));
//                                }
//                            }
//                            if (resizeImg != null) {
//                                if (uploadTag == "National ID / Passport") {
//                                    documentUri = uri;
//                                    btpersonalId.setVisibility(View.GONE);
//                                    imgPersonalId.setVisibility(View.VISIBLE);
//                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
//                                            AppHelper.getPath(getActivity(), uri));
//                                    imgPersonalId.setImageBitmap(reRotateImg);
//                                    saveProfileAccount("NationalIDPassport",
//                                            AppHelper.getFileDataFromDrawable(imgPersonalId.getDrawable()),
//                                            SharedHelper.getKey(getActivity(), uploadTag));
//                                }
//                            }
//                            if (resizeImg != null) {
//                                if (uploadTag == "PSV License") {
//                                    documentUri = uri;
//                                    btPSVLicense.setVisibility(View.GONE);
//                                    imgPSVLicense.setVisibility(View.VISIBLE);
//                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
//                                            AppHelper.getPath(getActivity(), uri));
//                                    imgPSVLicense.setImageBitmap(reRotateImg);
//                                    saveProfileAccount("PSVLicense",
//                                            AppHelper.getFileDataFromDrawable(imgPSVLicense.getDrawable()),
//                                            SharedHelper.getKey(getActivity(), uploadTag));
//                                }
//                            }
//                            if (resizeImg != null) {
//                                if (uploadTag == "Certificate Of Good Conduct") {
//                                    documentUri = uri;
//                                    btGoodConduct.setVisibility(View.GONE);
//                                    imgGoodConduct.setVisibility(View.VISIBLE);
//                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
//                                            AppHelper.getPath(getActivity(), uri));
//                                    imgGoodConduct.setImageBitmap(reRotateImg);
//                                    saveProfileAccount("CertificateOfGoodConduct",
//                                            AppHelper.getFileDataFromDrawable(imgGoodConduct.getDrawable()),
//                                            SharedHelper.getKey(getActivity(), uploadTag));
//                                }
//                            }
//                            if (resizeImg != null) {
//                                if (uploadTag == "Motor Vehicle Inspection Certificate") {
//                                    documentUri = uri;
//                                    btMotorInspectCertificate.setVisibility(View.GONE);
//                                    imgMotorInspectCertificate.setVisibility(View.VISIBLE);
//                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
//                                            AppHelper.getPath(getActivity(), uri));
//                                    imgMotorInspectCertificate.setImageBitmap(reRotateImg);
//                                    saveProfileAccount("MotorVehicleInspectionCertificate",
//                                            AppHelper.getFileDataFromDrawable(imgMotorInspectCertificate.getDrawable()),
//                                            SharedHelper.getKey(getActivity(), uploadTag));
//                                }
//                            }
//
//                            if (resizeImg != null) {
//                                if (uploadTag == "PSV Insurance Certificate") {
//                                    documentUri = uri;
//                                    btPsvInsurenceCertificate.setVisibility(View.GONE);
//                                    imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
//                                    Bitmap reRotateImg = AppHelper.modifyOrientation(resizeImg,
//                                            AppHelper.getPath(getActivity(), uri));
//                                    imgPsvInsurenceCertificate.setImageBitmap(reRotateImg);
//                                    saveProfileAccount("PSVInsuranceCertificate",
//                                            AppHelper.getFileDataFromDrawable(imgPsvInsurenceCertificate.getDrawable()),
//                                            SharedHelper.getKey(getActivity(), uploadTag));
//                                }
//                            }
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                }
//
//                break;
//
//        }
//    }
//
//    public void saveProfileAccount(String filename, byte[] bytes, String docid) {
//        if (Utils.isConnectingToInternet(getActivity())) {
//            pDialog = new ProgressDialog(getActivity());
//            // pDialog.setTitle("Loading...");
//            pDialog.setCancelable(false);
//            pDialog.setMessage("Loading...");
//            pDialog.show();
//            final ProgressDialog finalPDialog = pDialog;
//            VolleyMultipartRequest multipartRequest = new
//                    VolleyMultipartRequest(Request.Method.POST,
//                            URLHelper.base + "api/provider/document/upload",
//                            new Response.Listener<NetworkResponse>() {
//                                @Override
//                                public void onResponse(NetworkResponse response) {
//                                    pDialog.dismiss();
//                                    if (uploadTag == "LogBook") {
//                                        SharedHelperImage.putKey(getActivity(),
//                                                "LogBook", String.valueOf(documentUri));
//                                    }
//                                    if (uploadTag == "National ID / Passport") {
//                                        SharedHelperImage.putKey(getActivity(),
//                                                "NationalID/Passport", String.valueOf(documentUri));
//                                    }
//                                    if (uploadTag == "PSV License") {
//                                        SharedHelperImage.putKey(getActivity(),
//                                                "PSVLicense", String.valueOf(documentUri));
//                                    }
//                                    if (uploadTag == "Certificate Of Good Conduct") {
//                                        SharedHelperImage.putKey(getActivity(),
//                                                "CertificateOfGoodConduct",
//                                                String.valueOf(documentUri));
//                                    }
//                                    if (uploadTag == "Motor Vehicle Inspection Certificate") {
//                                        SharedHelperImage.putKey(getActivity(),
//                                                "MotorVehicleInspectionCertificate",
//                                                String.valueOf(documentUri));
//                                    }
//
//                                    if (uploadTag == "PSV Insurance Certificate") {
//                                        SharedHelperImage.putKey(getActivity(),
//                                                "PSVInsuranceCertificate", String.valueOf(documentUri));
//                                    }
//                                    String resultResponse = new String(response.data);
//                                    Log.e("uploadtest", resultResponse.toString() + "");
//                                    Toast.makeText(getActivity(), "File is Uploaded Successfully",
//                                            Toast.LENGTH_LONG).show();
//                                    pDialog.dismiss();
//
//                                }
//                            }, new Response.ErrorListener() {
//                        @Override
//                        public void onErrorResponse(VolleyError error) {
//                            NetworkResponse networkResponse = error.networkResponse;
//                            String errorMessage = "Unknown error";
//                            if (networkResponse == null) {
//                                if (error.getClass().equals(TimeoutError.class)) {
//                                    errorMessage = "Request timeout";
//                                } else if (error.getClass().equals(NoConnectionError.class)) {
//                                    errorMessage = "Failed to connect server";
//                                }
//                            }
//                            pDialog.dismiss();
//
//                            Log.e("Error Block", errorMessage);
//                            error.printStackTrace();
//                        }
//                    }) {
//                        @Override
//                        protected java.util.Map<String, String> getParams() {
//                            java.util.Map<String, String> param = new HashMap<>();
//                            param.put("document_id", docid);
//                            param.put("provider_id", SharedHelper.getKey(getActivity(), "id"));
//
//
//                            return param;
//                        }
//
//                        @Override
//                        public java.util.Map<String, String> getHeaders() throws AuthFailureError {
//                            HashMap<String, String> headers = new HashMap<String, String>();
//                            headers.put("X-Requested-With", "XMLHttpRequest");
//                            headers.put("Authorization", "Bearer " + SharedHelper.getKey(getActivity(), "access_token"));
//
//                            return headers;
//                        }
//
//
//                        @Override
//                        protected java.util.Map<String, DataPart> getByteData() {
//                            java.util.Map<String, DataPart> params = new HashMap<>();
//                            // file name could found file base or direct access from real path
//                            // for now just get bitmap data from ImageView
//
//                            params.put("document", new DataPart(filename, bytes, "image/jpeg"));
//
//                            return params;
//                        }
//                    };
//
//
//            multipartRequest.setRetryPolicy(new DefaultRetryPolicy(
//                    0,
//                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//
//            RequestQueue queue = Volley.newRequestQueue(getActivity());
//            queue.add(multipartRequest);
//        } else {
//
//            Toast.makeText(getActivity(), "No Internet Connection", Toast.LENGTH_SHORT).show();
//
//
//        }
//
//        // VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(multipartRequest);
//    }
//
//    public String getAddress(String strLatitude, String strLongitude) {
//        Geocoder geocoder;
//        List<Address> addresses;
//        geocoder = new Geocoder(getActivity(), Locale.getDefault());
//        double latitude = Double.parseDouble(strLatitude);
//        double longitude = Double.parseDouble(strLongitude);
//        String address = "", city = "", state = "";
//        try {
//            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            city = addresses.get(0).getLocality();
//            state = addresses.get(0).getAdminArea();
//            String country = addresses.get(0).getCountryName();
//            String postalCode = addresses.get(0).getPostalCode();
//            String knownName = addresses.get(0).getFeatureName();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (address.length() > 0 || city.length() > 0)
//            return address + ", " + city;
//        else
//            return getString(R.string.no_address);
//    }
//
//
//    @Override
//    public void onPause() {
//
//        super.onPause();
//        Utilities.onMap = false;
//        if (customDialog != null) {
//            if (customDialog.isShowing()) {
//                customDialog.dismiss();
//            }
//        }
//        if (ha != null) {
//            isRunning = true;
//            if (mPlayer != null && mPlayer.isPlaying()) {
//                normalPlay = true;
//                mPlayer.stop();
//            } else {
//                normalPlay = false;
//            }
//            ha.removeCallbacksAndMessages(null);
//        }
//    }
//
//    private void showCancelDialog() {
//        AlertDialog.Builder builder;
//        builder = new AlertDialog.Builder(context);
//        builder.setTitle(getString(R.string.cancel_confirm));
//
//        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                showReasonDialog();
//            }
//        });
//
//        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Reset to previous seletion menu in navigation
//                dialog.dismiss();
//            }
//        });
//        builder.setCancelable(false);
//        final AlertDialog dialog = builder.create();
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//            }
//        });
//        dialog.show();
//    }
//
//    String cancaltype="";
//    String cancalReason = "";
//    private void showReasonDialog() {
//
//        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        View view = LayoutInflater.from(context).inflate(R.layout.cancel_dialog, null);
//        final EditText reasonEtxt = view.findViewById(R.id.reason_etxt);
//        Button submitBtn = view.findViewById(R.id.submit_btn);
//        RadioGroup radioCancel = view.findViewById(R.id.radioCancel);
//        radioCancel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                if (checkedId == R.id.driver) {
//                    reasonEtxt.setVisibility(View.VISIBLE);
//                    cancaltype = getResources().getString(R.string.plan_changed);
//                }
//                if (checkedId == R.id.vehicle) {
//                    reasonEtxt.setVisibility(View.VISIBLE);
//                    cancaltype = getResources().getString(R.string.booked_another_cab);
//                }
//                if (checkedId == R.id.app) {
//                    reasonEtxt.setVisibility(View.VISIBLE);
//                    cancaltype = getResources().getString(R.string.my_reason_is_not_listed);
//                }
//            }
//        });
//        builder.setView(view)
//                .setCancelable(true);
//        final AlertDialog dialog = builder.create();
//        submitBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (cancaltype.isEmpty()) {
//                    Toast.makeText(context, getResources().getString(R.string.please_select_reason), Toast.LENGTH_SHORT).show();
//
//                } else {
//                    cancalReason = reasonEtxt.getText().toString();
//                    if (cancalReason.isEmpty()) {
//                        reasonEtxt.setError(getResources().getString(R.string.please_specify_reason));
//                    } else {
//                        if (reasonEtxt.getText().toString().length() > 0)
//                            cancelRequest(request_id, reasonEtxt.getText().toString());
//                        else
//                            cancelRequest(request_id, "");
//                        dialog.dismiss();
//                    }
//                }
//            }
//        });
//        dialog.show();
//
//
////        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
////        LayoutInflater inflater = activity.getLayoutInflater();
////        View view = inflater.inflate(R.layout.cancel_dialog, null);
////
////
////        Button submitBtn = view.findViewById(R.id.submit_btn);
////        final EditText reason = view.findViewById(R.id.reason_etxt);
////
////        builder.setView(view);
////        final android.app.AlertDialog dialog = builder.create();
////        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
////
////        submitBtn.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View view) {
////                dialog.dismiss();
////                if (reason.getText().toString().length() > 0)
////                    cancelRequest(request_id, reason.getText().toString());
////                else
////                    cancelRequest(request_id, "");
////            }
////        });
////
////
////        dialog.show();
//    }
//
//    private void showSosDialog() {
//        AlertDialog.Builder builder;
//        builder = new AlertDialog.Builder(context);
//        builder.setTitle(getString(R.string.sos_confirm));
//
//        builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //cancelRequest(request_id);
//                dialog.dismiss();
//                String mobile = SharedHelper.getKey(context, "sos");
//                if (mobile != null && !mobile.equalsIgnoreCase("null") && mobile.length() > 0) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 3);
//                    } else {
//                        Intent intent = new Intent(Intent.ACTION_CALL);
//                        intent.setData(Uri.parse("tel:" + mobile));
//                        startActivity(intent);
//                    }
//                } else {
//                    displayMessage(getString(R.string.user_no_mobile));
//                }
//
//            }
//        });
//
//        builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                //Reset to previous seletion menu in navigation
//                dialog.dismiss();
//            }
//        });
//        builder.setCancelable(false);
//        final AlertDialog dialog = builder.create();
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @Override
//            public void onShow(DialogInterface arg) {
//            }
//        });
//        dialog.show();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Utilities.onMap = true;
//        if (Utilities.clearSound) {
//            NotificationManager notificationManager = (NotificationManager) getActivity()
//                    .getSystemService(NOTIFICATION_SERVICE);
//            notificationManager.cancelAll();
//        }
//        utils.print(TAG, "onResume: Handler Call out" + isRunning);
//        if (isRunning) {
//            if (mPlayer != null && normalPlay) {
//                mPlayer = MediaPlayer.create(context, R.raw.alert_tone);
//                mPlayer.start();
//            }
//            utils.print(TAG, "onResume: Handler Call" + isRunning);
//            ha.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    //call function
//                    if (type != null) {
//                        checkStatusSchedule();
//                    } else {
//                        checkStatus();
//                    }
//                    ha.postDelayed(this, 3000);
//                }
//            }, 3000);
//        }
//        try {
//            if (((MainActivity) getActivity()).statustg.equalsIgnoreCase("offline")) {
//                goOnline();
//            }
//        }catch (Exception e)
//        {
//             e.printStackTrace();
//        }
//    }
//
//    protected synchronized void buildGoogleApiClient() {
//        mGoogleApiClient = new GoogleApiClient.Builder(context)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        mGoogleApiClient.connect();
//    }
//
//    @SuppressLint("WrongConstant")
//    private void documentUploadFirstDialog() {
//
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View view = inflater.inflate(R.layout.upload_document_popup, null);
//        builder.setView(view);
//
////        RecyclerView rvDocList = view.findViewById(R.id.rvDocList);
////        imgTest = view.findViewById(R.id.imgTest);
////        rvDocList.setLayoutManager(new LinearLayoutManager(getActivity(),
////                LinearLayoutManager.VERTICAL, false));
////        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
////        progressDialog.setTitle("Getting Documents type....");
////        progressDialog.setCancelable(false);
////        progressDialog.show();
////
////        JSONObject object = new JSONObject();
////        JsonObjectRequest jsonObjectRequest = new
////                JsonObjectRequest(Request.Method.GET,
////                        URLHelper.base + "api/provider/document/types",
////                        null,
////                        new Response.Listener<JSONObject>() {
////                            @Override
////                            public void onResponse(JSONObject response) {
////                                Log.e("documentListResponse", response + "document");
////                                progressDialog.dismiss();
////                                documentItemArrayList = new ArrayList<>();
////                                try {
////                                    JSONArray jsonArray = response.getJSONArray("document");
////                                    for (int i = 0; i < jsonArray.length(); i++) {
////                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
////                                        DocListItemModel docListItemModel = new DocListItemModel();
////                                        docListItemModel.setName(jsonObject.optString("name"));
////                                        docListItemModel.setId(jsonObject.optInt("id"));
////                                        docListItemModel.setType(jsonObject.optString("type"));
////                                        documentItemArrayList.add(docListItemModel);
////
////                                    }
////                                    docListAdapter = new DocListAdapter(getActivity(),
////                                            documentItemArrayList);
////                                    rvDocList.setLayoutManager(new LinearLayoutManager(getActivity(),
////                                            LinearLayoutManager.VERTICAL, false));
////                                    rvDocList.setAdapter(docListAdapter);
////                                    docListAdapter.setUploadEventClick(new DocListAdapter.UploadEventClick() {
////                                        @Override
////                                        public void onViewDetailClick(View v, int position) {
////                                            DocListItemModel listItemModel = documentItemArrayList.get(position);
////                                            docIds = String.valueOf(listItemModel.getId());
////                                            adapterPosi = String.valueOf(documentItemArrayList.get(position));
////                                            //                uploadTag = "LogBook";
////                                            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
////                                                    != PackageManager.PERMISSION_GRANTED) {
////                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                                                    requestPermissions(new String[]{Manifest.permission.CAMERA},
////                                                            5);
////                                                }
////                                            }
////                                            Dialog d = ImageChoose();
////                                            d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
////                                            d.show();
////                                        }
////                                    });
////
////
////                                } catch (JSONException e) {
////                                    e.printStackTrace();
////                                }
////                            }
////                        }, new Response.ErrorListener() {
////                    @Override
////                    public void onErrorResponse(VolleyError error) {
////                        progressDialog.dismiss();
////                        Log.e("errorupload", error.toString() + "");
////                    }
////                }) {
////                    @Override
////                    public java.util.Map<String, String> getHeaders() {
////                        HashMap<String, String> headers = new HashMap<String, String>();
////                        headers.put("X-Requested-With", "XMLHttpRequest");
////                        headers.put("Authorization", "Bearer " + SharedHelper.getKey(getActivity(),
////                                "access_token"));
////                        Log.e("", "Access_Token" + SharedHelper.getKey(getActivity(),
////                                "access_token"));
////                        return headers;
////                    }
////                };
////
////        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//
////        builder.setView(view);
//        Waintingdialog = builder.create();
//        Waintingdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        Waintingdialog.setCanceledOnTouchOutside(false);
//
//        imgPersonal = view.findViewById(R.id.imgPersonal);
//        imgPersonalId = view.findViewById(R.id.imgPersonalId);
//        imgPSVLicense = view.findViewById(R.id.imgPSVLicense);
//        imgGoodConduct = view.findViewById(R.id.imgGoodConduct);
//        imgMotorInspectCertificate = view.findViewById(R.id.imgMotorInspectCertificate);
//        imgPsvInsurenceCertificate = view.findViewById(R.id.imgPsvInsurenceCertificate);
//
//        Button btnDone = view.findViewById(R.id.btnDone);
//
//        btMotorInspectCertificate = view.findViewById(R.id.btMotorInspectCertificate);
//        btPsvInsurenceCertificate = view.findViewById(R.id.btPsvInsurenceCertificate);
//        btGoodConduct = view.findViewById(R.id.btGoodConduct);
//        btPSVLicense = view.findViewById(R.id.btPSVLicense);
//        btpersonalId = view.findViewById(R.id.btpersonalId);
//        btPersonalPic = view.findViewById(R.id.btPersonalPic);
//
////        if (SharedHelperImage.getKey(getActivity(), "LogBook") != "") {
////            btPersonalPic.setVisibility(View.GONE);
////            imgPersonal.setVisibility(View.VISIBLE);
////            File file = new File(SharedHelperImage.getKey(getActivity(), "LogBook"));
////            Picasso.get().load(file).into(imgPersonal);
////        }
////        if (SharedHelperImage.getKey(getActivity(), "NationalID/Passport") != "") {
////            btpersonalId.setVisibility(View.GONE);
////            imgPersonalId.setVisibility(View.VISIBLE);
////            File file = new File(SharedHelperImage.getKey(getActivity(), "NationalID/Passport"));
////            Picasso.get().load(file).into(imgPersonalId);
////        }
////        if (SharedHelperImage.getKey(getActivity(), "PSVLicense") != null) {
////            btPSVLicense.setVisibility(View.GONE);
////            imgPSVLicense.setVisibility(View.VISIBLE);
////            File file = new File(SharedHelperImage.getKey(getActivity(), "PSVLicense"));
////            Picasso.get().load(file).into(imgPSVLicense);
////        }
////        if (SharedHelperImage.getKey(getActivity(), "CertificateOfGoodConduct") != null) {
////            btGoodConduct.setVisibility(View.GONE);
////            imgGoodConduct.setVisibility(View.VISIBLE);
////            File file = new File(SharedHelperImage.getKey(getActivity(), "CertificateOfGoodConduct"));
////            Picasso.get().load(file).into(imgGoodConduct);
////        }
////        if (SharedHelperImage.getKey(getActivity(), "MotorVehicleInspectionCertificate") != null) {
////            btMotorInspectCertificate.setVisibility(View.GONE);
////            imgMotorInspectCertificate.setVisibility(View.VISIBLE);
////            File file = new File(SharedHelperImage.getKey(getActivity(), "MotorVehicleInspectionCertificate"));
////            Picasso.get().load(file).into(imgMotorInspectCertificate);
////        }
////        if (SharedHelperImage.getKey(getActivity(), "PSVInsuranceCertificate") != null) {
////            btPsvInsurenceCertificate.setVisibility(View.GONE);
////            imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
////            File file = new File(SharedHelperImage.getKey(getActivity(), "PSVInsuranceCertificate"));
////            Picasso.get().load(file).into(imgPsvInsurenceCertificate);
////        }
//
////        if (uploadTag == "National ID / Passport") {
////            SharedHelperImage.putKey(getActivity(),
////                    "NationalID/Passport", String.valueOf(documentUri));
////        }
////        if (uploadTag == "PSV License") {
////            SharedHelperImage.putKey(getActivity(),
////                    "PSVLicense", String.valueOf(documentUri));
////        }
////        if (uploadTag == "Certificate Of Good Conduct") {
////            SharedHelperImage.putKey(getActivity(),
////                    "CertificateOfGoodConduct",
////                    String.valueOf(documentUri));
////        }
////        if (uploadTag == "Motor Vehicle Inspection Certificate") {
////            SharedHelperImage.putKey(getActivity(),
////                    "MotorVehicleInspectionCertificate",
////                    String.valueOf(documentUri));
////        }
////
////        if (uploadTag == "PSV Insurance Certificate") {
////            SharedHelperImage.putKey(getActivity(),
////                    "PSVInsuranceCertificate", String.valueOf(documentUri));
////        }
//
////        CheckBox chkPersonalPic = view.findViewById(R.id.chkPersonalPic);
////        CheckBox chkpersonalId = view.findViewById(R.id.chkpersonalId);
////        CheckBox chkPSVLicense = view.findViewById(R.id.chkPSVLicense);
////        CheckBox chkGoodConduct = view.findViewById(R.id.chkGoodConduct);
////        CheckBox chkMotorInspectCertificate = view.findViewById(R.id.chkMotorInspectCertificate);
////        CheckBox chkPsvInsurenceCertificate = view.findViewById(R.id.chkPsvInsurenceCertificate);
//        CheckBox chkTerm = view.findViewById(R.id.chkTerm);
//
//        btPersonalPic.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadTag = "LogBook";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//
////                imgPersonal.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });
//
////        chkPersonalPic.setOnCheckedChangeListener((buttonView, isChecked) -> {
////            if (isChecked) {
////                uploadTag = "LogBook";
////                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
////                        != PackageManager.PERMISSION_GRANTED) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        requestPermissions(new String[]{Manifest.permission.CAMERA},
////                                5);
////                    }
////                }
////                imgPersonal.setVisibility(View.VISIBLE);
////                Dialog d = ImageChoose();
////                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
////                d.show();
////            }
////        });
//
//        btpersonalId.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadTag = "National ID / Passport";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//
////                imgPersonalId.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });
//
////        chkpersonalId.setOnCheckedChangeListener((buttonView, isChecked) -> {
////            if (isChecked) {
////                uploadTag = "National ID / Passport";
////                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
////                        != PackageManager.PERMISSION_GRANTED) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        requestPermissions(new String[]{Manifest.permission.CAMERA},
////                                5);
////                    }
////                }
////                imgPersonalId.setVisibility(View.VISIBLE);
////                Dialog d = ImageChoose();
////                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
////                d.show();
////            }
////        });
//
//        btPSVLicense.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadTag = "PSV License";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//
////                imgPSVLicense.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });
//
////        chkPSVLicense.setOnCheckedChangeListener((buttonView, isChecked) -> {
////            if (isChecked) {
////                uploadTag = "PSV License";
////                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
////                        != PackageManager.PERMISSION_GRANTED) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        requestPermissions(new String[]{Manifest.permission.CAMERA},
////                                5);
////                    }
////                }
////                imgPSVLicense.setVisibility(View.VISIBLE);
////                Dialog d = ImageChoose();
////                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
////                d.show();
////            }
////        });
//
//        btGoodConduct.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadTag = "Certificate Of Good Conduct";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//
////                imgGoodConduct.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });
//
////        chkGoodConduct.setOnCheckedChangeListener((buttonView, isChecked) -> {
////            if (isChecked) {
////                uploadTag = "Certificate Of Good Conduct";
////                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
////                        != PackageManager.PERMISSION_GRANTED) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        requestPermissions(new String[]{Manifest.permission.CAMERA},
////                                5);
////                    }
////                }
////                imgGoodConduct.setVisibility(View.VISIBLE);
////                Dialog d = ImageChoose();
////                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
////                d.show();
////            }
////        });
//
//        btPsvInsurenceCertificate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadTag = "PSV Insurance Certificate";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//
////                imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//            }
//        });
//
////        chkMotorInspectCertificate.setOnCheckedChangeListener((buttonView, isChecked) -> {
////            if (isChecked) {
////                uploadTag = "Motor Vehicle Inspection Certificate";
////                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
////                        != PackageManager.PERMISSION_GRANTED) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        requestPermissions(new String[]{Manifest.permission.CAMERA},
////                                5);
////                    }
////                }
////                imgMotorInspectCertificate.setVisibility(View.VISIBLE);
////                Dialog d = ImageChoose();
////                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
////                d.show();
////            }
////        });
//
//
//        btMotorInspectCertificate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                uploadTag = "Motor Vehicle Inspection Certificate";
//                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
//                        != PackageManager.PERMISSION_GRANTED) {
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        requestPermissions(new String[]{Manifest.permission.CAMERA},
//                                5);
//                    }
//                }
//
////                imgMotorInspectCertificate.setVisibility(View.VISIBLE);
//                Dialog d = ImageChoose();
//                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
//                d.show();
//
//
//            }
//        });
//
////        chkPsvInsurenceCertificate.setOnCheckedChangeListener((buttonView, isChecked) -> {
////            if (isChecked) {
////                uploadTag = "PSV Insurance Certificate";
////                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
////                        != PackageManager.PERMISSION_GRANTED) {
////                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
////                        requestPermissions(new String[]{Manifest.permission.CAMERA},
////                                5);
////                    }
////                }
////                imgPsvInsurenceCertificate.setVisibility(View.VISIBLE);
////                Dialog d = ImageChoose();
////                d.getWindow().getAttributes().windowAnimations = R.style.dialog_animation;
////                d.show();
////            }
////        });
//
//
////        chkPersonalPic.isChecked() &&
////                chkpersonalId.isChecked() &&
////                chkPSVLicense.isChecked() &&
////                chkGoodConduct.isChecked() &&
////                chkMotorInspectCertificate.isChecked() &&
////                chkPsvInsurenceCertificate.isChecked() &&
//
////        btnDone.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                if (chkTerm.isChecked()){
////                    completeDocumentStatus();
////                }
////                else {
////                    chkTerm.setChecked(false);
////                    Toast.makeText(getActivity(), "Upload all documents",
////                            Toast.LENGTH_LONG).show();
////                }
////            }
////        });
//
//        btnDone.setOnClickListener(v -> {
//            if (chkTerm.isChecked() &&
//                    (imgPersonal.getDrawable() != null)
//                    && (imgPersonalId.getDrawable() != null)
//                    && (imgPSVLicense.getDrawable() != null)
//                    && (imgGoodConduct.getDrawable() != null)
//                    && (imgMotorInspectCertificate.getDrawable() != null)
//                    && (imgPsvInsurenceCertificate.getDrawable() != null)) {
//                completeDocumentStatus();
//            } else {
////                chkPersonalPic.setChecked(false);
////                chkpersonalId.setChecked(false);
////                chkPSVLicense.setChecked(false);
////                chkGoodConduct.setChecked(false);
////                chkMotorInspectCertificate.setChecked(false);
////                chkPsvInsurenceCertificate.setChecked(false);
//                chkTerm.setChecked(false);
//                Toast.makeText(getActivity(), "Upload all documents",
//                        Toast.LENGTH_LONG).show();
//                displayMessage(getActivity().getString(R.string.upload_all_documents_and_check_term));
//            }
////             if ((imgPersonal.getDrawable() != null)
////                     && (imgPersonalId.getDrawable() != null)
////                     && (imgPSVLicense.getDrawable() != null)
////                     && (imgGoodConduct.getDrawable() != null)
////                     && (imgMotorInspectCertificate.getDrawable() != null)
////                     && (imgPsvInsurenceCertificate.getDrawable() != null))
////            {
////                displayMessage(getActivity().getString(R.string.upload_all_documents_and_check_term));
////            }
//
//              /*  else {
//
//            }*/
//        });
//
//        if (Waintingdialog.isShowing()) {
//
//        } else {
//            Waintingdialog.show();
//        }
//    }
//
//
//    private void completeDocumentStatus() {
//        try {
//            if (helper.isConnectingToInternet()) {
//                String url = URLHelper.COMPLETE_DOCUMENT;
//                final JsonObjectRequest jsonObjectRequest = new
//                        JsonObjectRequest(Request.Method.GET,
//                                url,
//                                null,
//                                new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        Log.e("completeDoc", response.toString());
//                                        Waintingdialog.dismiss();
//                                        Intent intent1 = new Intent(activity, WaitingForApproval.class);
//                                        intent1.putExtra("account_status", "account_status_new");
//                                        activity.startActivity(intent1);
//                                        activity.finish();
//                                    }
//                                }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                Log.e("completeDoc", error.getLocalizedMessage().toString());
//                                utils.print("Error", error.toString());
//
//                            }
//                        }) {
//                            @Override
//                            public java.util.Map<String, String> getHeaders() {
//                                HashMap<String, String> headers = new HashMap<>();
//                                headers.put("X-Requested-With", "XMLHttpRequest");
//                                headers.put("Authorization", "Bearer " + token);
//                                return headers;
//                            }
//                        };
//                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//            } else {
//                displayMessage(getString(R.string.oops_connect_your_internet));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private Dialog ImageChoose() {
//        androidx.appcompat.app.AlertDialog.Builder builder = new
//                androidx.appcompat.app.AlertDialog.Builder(getActivity());
//
//        if (bmp == null) {
//            CharSequence[] ch = {};
//            ch = new CharSequence[]{"Gallery", "Camera"};
//            builder.setTitle("Choose Image :").setItems(
//                    ch,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            switch (which) {
//                                case 0:
//                                    getPhoto();
//                                    break;
//                                case 1:
//                                    if (ContextCompat.checkSelfPermission(getActivity(),
//                                            android.Manifest.permission.CAMERA) ==
//                                            PackageManager.PERMISSION_GRANTED) {
//                                        takePhoto();
//                                    } else {
//                                        Toast.makeText(getActivity(), "You have denied camera access permission.",
//                                                Toast.LENGTH_LONG).show();
//                                    }
//                                    break;
//                                default:
//                                    break;
//                            }
//                        }
//                    });
//        } else {
//            builder.setTitle("Choose Image :").setItems(
//                    new CharSequence[]{"Gallery", "Camera"},
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // TODO Auto-generated method stub
//                            switch (which) {
//                                case 0:
//                                    getPhoto();
//                                    break;
//                                case 1:
//                                    if (ContextCompat.checkSelfPermission(getActivity(),
//                                            android.Manifest.permission.CAMERA) ==
//                                            PackageManager.PERMISSION_GRANTED) {
//                                        takePhoto();
//                                    } else {
//                                        Toast.makeText(getActivity(), "You have denied camera access permission.",
//                                                Toast.LENGTH_LONG).show();
//                                    }
//                                    break;
//
//
//                                default:
//                                    break;
//                            }
//                        }
//                    });
//        }
//
//
//        return builder.create();
//    }
//
//    private void getPhoto() {
//        Intent i = new Intent(Intent.ACTION_PICK,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, GET_PICTURE);
//    }
//
//    private void takePhoto() {
//        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
//        StrictMode.setVmPolicy(builder.build());
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        cameraImageUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
//        startActivityForResult(intent, TAKE_PICTURE);
//    }
//
//    private Uri getOutputMediaFileUri(int type) {
//        Uri uri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID + ".provider", getOutputMediaFile(type));
////        Uri contentUri = FileProvider.getUriForFile(getActivity(), BuildConfig.APPLICATION_ID, getOutputMediaFile(type));
//        return uri;
//    }
//
//    @SuppressLint("SimpleDateFormat")
//    private File getOutputMediaFile(int type) {
//
//        File mediaStorageDir = new File(
//                Environment
//                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
//                "APP");
//
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//
//                return null;
//            }
//        }
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
//                .format(new Date());
//        File mediaFile;
//        if (type == MEDIA_TYPE_IMAGE) {
//            mediaFile = new File(mediaStorageDir.getPath() + File.separator
//                    + "IMG_" + timeStamp + ".png");
//        } else {
//            return null;
//        }
//
//        return mediaFile;
//    }
//
//
//    // Fetches data from url passed
//    private class FetchUrl extends AsyncTask<String, Void, String> {
//
//        @Override
//        protected String doInBackground(String... url) {
//
//            // For storing data from web service
//            String data = "";
//
//            try {
//                // Fetching the data from web service
//                data = downloadUrl(url[0]);
//                Log.d("Background Task data", data);
//            } catch (Exception e) {
//                Log.d("Background Task", e.toString());
//            }
//            return data;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            super.onPostExecute(result);
//
//            ParserTask parserTask = new ParserTask();
//
//            // Invokes the thread for parsing the JSON data
//            parserTask.execute(result);
//
//        }
//    }
//
//    /**
//     * A class to parse the Google Places in JSON format
//     */
//    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
//
//        // Parsing the data in non-ui thread
//        @Override
//        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {
//
//            JSONObject jObject;
//            List<List<HashMap<String, String>>> routes = null;
//
//            try {
//                jObject = new JSONObject(jsonData[0]);
//                Log.d("ParserTask", jsonData[0]);
//                DataParser parser = new DataParser();
//                Log.d("ParserTask", parser.toString());
//
//                // Starts parsing data
//                routes = parser.parse(jObject);
//                Log.d("ParserTask", "Executing routes");
//                Log.d("ParserTask", routes.toString());
//
//            } catch (Exception e) {
//                Log.d("ParserTask", e.toString());
//                e.printStackTrace();
//            }
//            return routes;
//        }
//
//        // Executes in UI thread, after the parsing process
//        @Override
//        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
//            try {
//
//            ArrayList<LatLng> points = null;
//            PolylineOptions lineOptions = null;
//
//            // Traversing through all the routes
//            for (int i = 0; i < result.size(); i++) {
//                points = new ArrayList<>();
//                lineOptions = new PolylineOptions();
//
//                // Fetching i-th route
//                List<HashMap<String, String>> path = result.get(i);
//
//                // Fetching all the points in i-th route
//                for (int j = 0; j < path.size(); j++) {
//                    HashMap<String, String> point = path.get(j);
//
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//
//                    points.add(position);
//                }
//                mMap.clear();
//                MarkerOptions markerOptions = new MarkerOptions().title("Source").anchor(0.5f, 0.75f)
//                        .position(sourceLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_user_location));
//                mMap.addMarker(markerOptions);
//                MarkerOptions markerOptions1 = new MarkerOptions().title("Destination").anchor(0.5f, 0.75f)
//                        .position(destLatLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_provider_marker));
//                mMap.addMarker(markerOptions);
//                mMap.addMarker(markerOptions1);
//
//
//                LatLngBounds.Builder builder = new LatLngBounds.Builder();
//                LatLngBounds bounds;
//                builder.include(sourceLatLng);
//                builder.include(destLatLng);
//                bounds = builder.build();
//                if (CurrentStatus.equalsIgnoreCase("STARTED") || CurrentStatus.equalsIgnoreCase("ARRIVED")) {
////                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 200, 200, 80);
////                    mMap.moveCamera(cu);
//                } else {
//                    CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 600, 600, 20);
//                    mMap.moveCamera(cu);
//                }
//                mMap.getUiSettings().setMapToolbarEnabled(false);
//
//
//                // Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(5);
//                lineOptions.color(Color.BLACK);
//
//                Log.d("onPostExecute", "onPostExecute lineoptions decoded");
//
//            }
//
//            // Drawing polyline in the Google DriverMapFragment for the i-th route
//            if (lineOptions != null && points != null) {
//                mMap.addPolyline(lineOptions);
//            } else {
//                Log.d("onPostExecute", "without Polylines drawn");
//            }
//
//        }catch (Exception e)
//            {e.printStackTrace();}
//
//    }
//    }
//
//    private void showImage(Uri uri){
//        Dialog dialog = new Dialog(getActivity());
//        dialog.setContentView(R.layout.image_show_layout);
//        ImageView ivShow = dialog.findViewById(R.id.ivShow);
//        MyButton btCancel = dialog.findViewById(R.id.btCancel);
//        MyButton btOk = dialog.findViewById(R.id.btOk);
//        dialog.show();
//        btCancel.setOnClickListener(v -> dialog.dismiss());
//
//        btOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        new BitmapWorkerTask(getActivity(), ivShow,
//                "add_revenue").execute(uri);
//    }
//
//    void showImage1(Bitmap uri){
//        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
//        LayoutInflater inflater = activity.getLayoutInflater();
//        View view = inflater.inflate(R.layout.image_show_layout, null);
//        builder.setView(view);
//
//        imageShowDialog = builder.create();
//        imageShowDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        imageShowDialog.setCanceledOnTouchOutside(false);
//
////        final Dialog dialog = new Dialog(getActivity());
////        dialog.setContentView(R.layout.image_show_layout);
//        ImageView ivShow = view.findViewById(R.id.ivShow);
//        MyButton btCancel = view.findViewById(R.id.btCancel);
//        MyButton btOk = view.findViewById(R.id.btOk);
////        view.show();
//
//
//        btOk.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });
//        ivShow.setImageBitmap(uri);
//        if (imageShowDialog.isShowing()) {
//
//        } else {
//            imageShowDialog.show();
//        }
//    }
//
//
//
//    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            // Get extra data included in the Intent
//            String statusresponse = intent.getStringExtra("statusresponse");
//            Log.e("map" + "statusresponse", statusresponse + "res");
//
//            try {
//
//                JSONObject response = new JSONObject(statusresponse);
//                if (errorLayout.getVisibility() == View.VISIBLE) {
//                    errorLayout.setVisibility(View.GONE);
//                }
//                Log.e("CheckStatus Brodcast", "" + response.toString());
//                //SharedHelper.putKey(context, "currency", response.optString("currency"));
//                try {
//                    if (response.optJSONArray("requests").length() > 0) {
//                        JSONObject jsonObject = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optJSONObject("user");
//                        user.setFirstName(jsonObject.optString("first_name"));
////                        user.setLastName(jsonObject.optString("last_name"));
//                        user.setEmail(jsonObject.optString("email"));
//                        if (jsonObject.optString("picture").startsWith("http"))
//                            user.setImg(jsonObject.optString("picture"));
//                        else
//                            user.setImg(URLHelper.base + "storage/app/public/" + jsonObject.optString("picture"));
//                        user.setRating(jsonObject.optString("rating"));
//                        user.setMobile(jsonObject.optString("mobile"));
//                        bookingId = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("booking_id");
//                        address = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("s_address");
//                        daddress = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request").optString("d_address");
//
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                if (response.optString("account_status").equals("new") ||
//                        response.optString("account_status").equals("onboarding")) {
//                    ha.removeMessages(0);
//                    checkDocumentStatus();
//                } else {
//
//                    if (response.optString("service_status").equals("offline")) {
//                        ha.removeMessages(0);
//                        goOffline();
//                    } else {
//
//                        if (response.optJSONArray("requests") != null && response.optJSONArray("requests").length() > 0) {
//                            JSONObject statusResponse = null;
//                            try {
//                                statusResponses = response.optJSONArray("requests");
//                                statusResponse = response.optJSONArray("requests").getJSONObject(0).optJSONObject("request");
//                                request_id = response.optJSONArray("requests").getJSONObject(0).optString("request_id");
//                                Log.e("request_idjson", request_id + "");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//
//                            if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                lblDistanceTravelled.setText("Distance Travelled :"
//                                        + String.format("%f", Float.parseFloat(LocationTracking.distance * 0.001 + "")) + " Km");
//                            }
//                            if ((statusResponse != null) && (request_id != null)) {
//                                if ((!previous_request_id.equals(request_id) || previous_request_id.equals(" ")) && mMap != null) {
//                                    previous_request_id = request_id;
//                                    srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                    srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                    destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                    destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//                                    //noinspection deprecation
//                                    setSourceLocationOnMap(currentLatLng);
//                                    setPickupLocationOnMap();
//                                    sos.setVisibility(View.GONE);
//
//                                }
//                                utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
//                                if (!PreviousStatus.equals(statusResponse.optString("status"))) {
//                                    PreviousStatus = statusResponse.optString("status");
//                                    clearVisibility();
//                                    utils.print("responseObj(" + request_id + ")", statusResponse.toString());
//                                    utils.print("Cur_and_New_status :", "" + CurrentStatus + "," + statusResponse.optString("status"));
//                                    if (!statusResponse.optString("status").equals("SEARCHING")) {
//                                        timerCompleted = false;
//                                        if (mPlayer != null && mPlayer.isPlaying()) {
//                                            mPlayer.stop();
//                                            mPlayer = null;
//                                            countDownTimer.cancel();
//                                        }
//                                    }
//                                    if (statusResponse.optString("status").equals("SEARCHING")) {
//                                        scheduleTrip = false;
//                                        if (!timerCompleted) {
//                                            setValuesTo_ll_01_contentLayer_accept_or_reject_now(statusResponses);
//                                            if (ll_01_contentLayer_accept_or_reject_now.getVisibility() == View.GONE) {
//                                                ll_01_contentLayer_accept_or_reject_now.startAnimation(slide_up);
//                                            }
//                                            ll_01_contentLayer_accept_or_reject_now.setVisibility(View.VISIBLE);
//                                        }
//                                        CurrentStatus = "STARTED";
//                                    } else if (statusResponse.optString("status").equals("STARTED")) {
//                                        setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                        if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                            //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                        }
//                                        ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                        btn_01_status.setText("ARRIVED");
//                                        CurrentStatus = "ARRIVED";
//                                        sos.setVisibility(View.GONE);
//                                        if (srcLatitude == 0 && srcLongitude == 0 && destLatitude == 0 && destLongitude == 0) {
//                                            mapClear();
//                                            srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                            srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                            destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                            destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//                                            //noinspection deprecation
//                                            //
//                                            setSourceLocationOnMap(currentLatLng);
//                                            setPickupLocationOnMap();
//                                        }
//                                        setSourceLocationOnMap(currentLatLng);
//                                        setPickupLocationOnMap();
//                                        sos.setVisibility(View.GONE);
//                                        btn_cancel_ride.setVisibility(View.VISIBLE);
//                                        destinationLayer.setVisibility(View.VISIBLE);
//                                        layoutinfo.setVisibility(View.GONE);
//                                        String address = statusResponse.optString("s_address");
//                                        if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                            destination.setText(address);
//                                        else
//                                            destination.setText(getAddress(statusResponse.optString("s_latitude"),
//                                                    statusResponse.optString("s_longitude")));
//                                        topSrcDestTxtLbl.setText("Pick up Location");
//                                        if (!isMyServiceRunning(LocationTracking.class)) {
//                                            activity.startService(service_intent);
//                                        }
//
//                                    } else if (statusResponse.optString("status").equals("ARRIVED")) {
//                                        setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                        if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                            //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                        }
//                                        ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                        btn_01_status.setText("PICKEDUP");
//                                        sos.setVisibility(View.GONE);
//                                        img03Status1.setImageResource(R.drawable.arrived_select);
//                                        CurrentStatus = "PICKEDUP";
//
//                                        btn_cancel_ride.setVisibility(View.VISIBLE);
//                                        destinationLayer.setVisibility(View.VISIBLE);
//
//                                        layoutinfo.setVisibility(View.GONE);
//                                        String address = statusResponse.optString("d_address");
//                                        if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                            destination.setText(address);
//                                        else
//                                            destination.setText(getAddress(statusResponse.optString("d_latitude"),
//                                                    statusResponse.optString("d_longitude")));
//                                        topSrcDestTxtLbl.setText("Drop Location ");
//                                        if (!isMyServiceRunning(LocationTracking.class)) {
//                                            activity.startService(service_intent);
//                                        }
//
//                                    } else if (statusResponse.optString("status").equals("PICKEDUP")) {
//                                        setValuesTo_ll_03_contentLayer_service_flow(statusResponses);
//                                        if (ll_03_contentLayer_service_flow.getVisibility() == View.GONE) {
//                                            //ll_03_contentLayer_service_flow.startAnimation(slide_up);
//                                        }
//                                        ll_03_contentLayer_service_flow.setVisibility(View.VISIBLE);
//                                        btn_01_status.setText("TAP WHEN DROPPED");
//                                        sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.VISIBLE);
//                                        img03Status1.setImageResource(R.drawable.arrived_select);
//                                        img03Status2.setImageResource(R.drawable.pickup_select);
//                                        CurrentStatus = "DROPPED";
//                                        destinationLayer.setVisibility(View.VISIBLE);
//                                        btn_cancel_ride.setVisibility(View.GONE);
//                                        String address = statusResponse.optString("d_address");
//                                        if (address != null && !address.equalsIgnoreCase("null") && address.length() > 0)
//                                            destination.setText(address);
//                                        else
//                                            destination.setText(getAddress(statusResponse.optString("d_latitude"),
//                                                    statusResponse.optString("d_longitude")));
//                                        topSrcDestTxtLbl.setText("Drop Location ");
//                                        if (!isMyServiceRunning(LocationTracking.class)) {
//                                            activity.startService(service_intent);
//                                        }
//                                        mapClear();
//
//                                        srcLatitude = Double.valueOf(statusResponse.optString("s_latitude"));
//                                        srcLongitude = Double.valueOf(statusResponse.optString("s_longitude"));
//                                        destLatitude = Double.valueOf(statusResponse.optString("d_latitude"));
//                                        destLongitude = Double.valueOf(statusResponse.optString("d_longitude"));
//
//                                        setSourceLocationOnMap(currentLatLng);
//                                        setDestinationLocationOnMap();
//
//                                    } else if (statusResponse.optString("status").equals("DROPPED")
//                                            && statusResponse.optString("paid").equals("0")) {
//                                        setValuesTo_ll_04_contentLayer_payment(statusResponses);
//                                        if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
//                                            ll_04_contentLayer_payment.startAnimation(slide_up);
//                                        }
//                                        ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
//                                        img03Status1.setImageResource(R.drawable.arrived);
//                                        img03Status2.setImageResource(R.drawable.pickup);
//                                        btn_01_status.setText("CONFIRM PAYMENT");
//                                        sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.GONE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        CurrentStatus = "COMPLETED";
//                                        if (isMyServiceRunning(LocationTracking.class)) {
//                                            activity.stopService(service_intent);
//                                        }
//                                        LocationTracking.distance = 0.0f;
//                                    } else if (statusResponse.optString("status").equals("DROPPED") && statusResponse.optString("paid").equals("1")) {
//                                        setValuesTo_ll_05_contentLayer_feedback(statusResponses);
//                                        if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
//                                            ll_05_contentLayer_feedback.startAnimation(slide_up);
//                                        }
//                                        ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
//                                        btn_01_status.setText("SUBMIT");
//                                        sos.setVisibility(View.VISIBLE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        CurrentStatus = "RATE";
//                                        if (isMyServiceRunning(LocationTracking.class)) {
//                                            activity.stopService(service_intent);
//                                        }
//                                        LocationTracking.distance = 0.0f;
//                                    } else if (statusResponse.optString("status").equals("COMPLETED")
//                                            && statusResponse.optString("paid").equals("0")) {
//
//                                        setValuesTo_ll_04_contentLayer_payment(statusResponses);
//                                        if (ll_04_contentLayer_payment.getVisibility() == View.GONE) {
//                                            ll_04_contentLayer_payment.startAnimation(slide_up);
//                                        }
//                                        ll_04_contentLayer_payment.setVisibility(View.VISIBLE);
//                                        img03Status1.setImageResource(R.drawable.arriveddisable);
//                                        img03Status2.setImageResource(R.drawable.pickeddisable);
//                                        driveraccepted.setVisibility(View.VISIBLE);
//                                        driverArrived.setVisibility(View.GONE);
//                                        driverPicked.setVisibility(View.GONE);
//
//                                        btn_01_status.setText("CONFIRM PAYMENT");
//                                        sos.setVisibility(View.VISIBLE);
////                                                navigate.setVisibility(View.GONE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        CurrentStatus = "COMPLETED";
//                                        if (isMyServiceRunning(LocationTracking.class)) {
//                                            activity.stopService(service_intent);
//                                        }
//                                        LocationTracking.distance = 0.0f;
//                                    } else if (statusResponse.optString("status").equals("COMPLETED")
//                                            && statusResponse.optString("paid").equals("1")) {
//                                        setValuesTo_ll_05_contentLayer_feedback(statusResponses);
//                                        if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
//                                            ll_05_contentLayer_feedback.startAnimation(slide_up);
//                                        }
////                                        ll_04_contentLayer_payment.setVisibility(View.GONE);
//                                        edt05Comment.setText("");
//                                        ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
//                                        sos.setVisibility(View.GONE);
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        btn_01_status.setText("SUBMIT");
//                                        CurrentStatus = "RATE";
//                                        if (isMyServiceRunning(LocationTracking.class)) {
//                                            activity.stopService(service_intent);
//                                        }
//                                        LocationTracking.distance = 0.0f;
//                                    }
////                                    else if (statusResponse.optString("status").equals("COMPLETED")
////                                            && statusResponse.optString("paid").equals("1")) {
////                                        setValuesTo_ll_05_contentLayer_feedback(statusResponses);
////                                        if (ll_05_contentLayer_feedback.getVisibility() == View.GONE) {
////                                            ll_05_contentLayer_feedback.startAnimation(slide_up);
////                                        }
////                                        edt05Comment.setText("");
////                                        ll_05_contentLayer_feedback.setVisibility(View.VISIBLE);
////                                        sos.setVisibility(View.GONE);
////                                        destinationLayer.setVisibility(View.GONE);
////                                        layoutinfo.setVisibility(View.VISIBLE);
////                                        btn_01_status.setText("");
////                                        CurrentStatus = "RATE";
////                                        if (isMyServiceRunning(LocationTracking.class)) {
////                                            activity.stopService(service_intent);
////                                        }
////                                        LocationTracking.distance = 0.0f;
////                                    }
//                                    else if (statusResponse.optString("status").equals("SCHEDULED")) {
//                                        if (mMap != null) {
//                                            if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                                return;
//                                            }
//                                            mMap.clear();
//                                        }
//                                        clearVisibility();
//                                        CurrentStatus = "SCHEDULED";
//                                        if (lnrGoOffline.getVisibility() == View.GONE) {
//                                            lnrGoOffline.startAnimation(slide_up);
//                                        }
//                                        lnrGoOffline.setVisibility(View.VISIBLE);
//                                        utils.print("statusResponse", "null");
//                                        destinationLayer.setVisibility(View.GONE);
//                                        layoutinfo.setVisibility(View.VISIBLE);
//                                        if (isMyServiceRunning(LocationTracking.class)) {
//                                            activity.stopService(service_intent);
//                                        }
//                                        LocationTracking.distance = 0.0f;
//                                    }
//                                }
//                            } else {
//                                if (mMap != null) {
//                                    if (ActivityCompat.checkSelfPermission(activity,
//                                            Manifest.permission.ACCESS_FINE_LOCATION) !=
//                                            PackageManager.PERMISSION_GRANTED &&
//                                            ActivityCompat.checkSelfPermission(activity,
//                                                    Manifest.permission.ACCESS_COARSE_LOCATION) !=
//                                                    PackageManager.PERMISSION_GRANTED) {
//                                        return;
//                                    }
//                                    timerCompleted = false;
//                                    mMap.clear();
//                                    if (mPlayer != null && mPlayer.isPlaying()) {
//                                        mPlayer.stop();
//                                        mPlayer = null;
//                                        countDownTimer.cancel();
//                                    }
//
//                                }
//                                if (isMyServiceRunning(LocationTracking.class)) {
//                                    activity.stopService(service_intent);
//                                }
//                                LocationTracking.distance = 0.0f;
//
//                                clearVisibility();
//                                lnrGoOffline.setVisibility(View.VISIBLE);
//                                destinationLayer.setVisibility(View.GONE);
//                                layoutinfo.setVisibility(View.VISIBLE);
//                                CurrentStatus = "ONLINE";
//                                PreviousStatus = "NULL";
//                                utils.print("statusResponse", "null");
//                            }
//
//                        } else {
//                            timerCompleted = false;
//                            if (!PreviousStatus.equalsIgnoreCase("NULL")) {
//                                utils.print("response", "null");
//                                if (mMap != null) {
//                                    if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                                        return;
//                                    }
//                                    mMap.clear();
//                                }
//                                if (mPlayer != null && mPlayer.isPlaying()) {
//                                    mPlayer.stop();
//                                    mPlayer = null;
//                                    countDownTimer.cancel();
//                                }
//                                clearVisibility();
//                                //mapClear();
//                                lnrGoOffline.setVisibility(View.VISIBLE);
//                                destinationLayer.setVisibility(View.GONE);
//                                layoutinfo.setVisibility(View.VISIBLE);
//                                CurrentStatus = "ONLINE";
//                                PreviousStatus = "NULL";
//                                utils.print("statusResponse", "null");
//                                if (isMyServiceRunning(LocationTracking.class)) {
//                                    activity.stopService(service_intent);
//                                }
//                                LocationTracking.distance = 0.0f;
//                            }
//
//                        }
//                    }
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//
//        }
//    };
//
//
//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) activity.getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//}
//
//
//
//
//
