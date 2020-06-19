package com.rapido.provider.Transportation.Service;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.rapido.provider.Transportation.Activities.MainActivity;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Helper.Ilyft;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class StatusCheckServie extends Service implements LocationListener{

    Context context;
    boolean isGPSEnabled = false;

    // flag for network status
    boolean isNetworkEnabled = false;


    ConnectionHelper helper;
    // flag for GPS status
    boolean canGetLocation = false;

    Location location; // location
    double latitude; // latitude
    double longitude; // longitude

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 1 meters

    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 1 * 1; // 1 sec

    // Declaring a Location Manager
    protected LocationManager locationManager;

    private Timer timer = new Timer();
//    CommunicationListner listener;
    public class LocalBinder extends Binder {
        public StatusCheckServie getService() {
            // Return this instance of LocalService so clients can call public methods
            return StatusCheckServie.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
//    @Override
//    public IBinder onBind(Intent intent)
//    {
//        throw new UnsupportedOperationException("Not yet implemented");
//        return null;
//    }

    @Override
    public void onCreate() {
        Log.v("serviceCreate","Service create");
        super.onCreate();
         context = this;
        getLocation();
        helper = new ConnectionHelper(this);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Log.v("latservice",latitude+"lat");
                Log.v("lonservice",longitude+"lat");
                checkStatus();
            }
        }, 0, 5000);//5 Seconds
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.v("Servicestart","servicestart");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
//        timer.cancel();

                Log.v("servicedestroy","servicedestroy");
        super.onDestroy();
    }

    public StatusCheckServie() {
    }




    @SuppressLint("MissingPermission")
    public Location getLocation() {
        try {
            locationManager = (LocationManager) this
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            isGPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            isNetworkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);


            if (!isGPSEnabled && !isNetworkEnabled) {
                // no network provider is enabled
            } else {
                this.canGetLocation = true;
                if (isNetworkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                    Log.d("Network", "Network");
                    if (locationManager != null) {
                        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                        if (location != null) {
                            latitude = location.getLatitude();
                            longitude = location.getLongitude();
                        }
                    }
                }
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        Log.d("GPS Enabled", "GPS Enabled");
                        if (locationManager != null) {
                            location = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (location != null) {
                                latitude = location.getLatitude();
                                longitude = location.getLongitude();
                            }
                        }
                    }
                }
            }

        } catch (Exception e) {
        }
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        float bestAccuracy = -1f;
        if (location.getAccuracy() != 0.0f
                && (location.getAccuracy() < bestAccuracy) || bestAccuracy == -1f) {
            locationManager.removeUpdates(this);
        }
        bestAccuracy = location.getAccuracy();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private void checkStatus() {
        try {

            if (helper.isConnectingToInternet()) {
                String url = URLHelper.base + "api/provider/trip?latitude=" + latitude + "&longitude=" + longitude;

                Log.v("Destination Current Lat", "" + latitude);
               Log.v("backUrl", url+"" + longitude);
                final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.e("serviceCheckStatus", "" + response.toString());
                        Log.e("isRunning", "" + isRunning(context)+"is");
                        //SharedHelper.putKey(context, "currency", response.optString("currency"));
                        try {
                            if (response.optJSONArray("requests").length() >0) {
                                if (response.optJSONArray("requests").getJSONObject(0) != null) {
                                    if (response.optJSONArray("requests").getJSONObject(0).optString("request_id") != null) {
                                        if (isRunning(context)) {
                                            sendMessageToActivity(response);
                                        } else {
                                            startActivity(new Intent(context, MainActivity.class));
                                            sendMessageToActivity(response);
                                        }
                                    }
                                }
                            }
                            else {
                                sendMessageToActivity(response);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            sendMessageToActivity(response);
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                   Log.e("error",error.toString()+"serviceerror");
                    }
                }) {
                    @Override
                    public java.util.Map<String, String> getHeaders() {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("X-Requested-With", "XMLHttpRequest");
                        headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                        return headers;
                    }
                };
                Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private  void sendMessageToActivity(JSONObject response) {
        Intent intent = new Intent("statusresponse");
        intent.putExtra("statusresponse", response+"");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }
    public static boolean isRunning(Context ctx) {
        ActivityManager activityManager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

        for (ActivityManager.RunningTaskInfo task : tasks) {
            if (ctx.getPackageName().equalsIgnoreCase(task.baseActivity.getPackageName()))
                return true;
        }
        return false;
    }



}