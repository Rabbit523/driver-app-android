package com.rapido.provider.Transportation.Activities;


import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;

import org.json.JSONObject;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class WaitingForApproval extends AppCompatActivity {
    Button logoutBtn;

    private String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_waiting_for_approval);

        token = SharedHelper.getKey(WaitingForApproval.this, "access_token");
        logoutBtn = findViewById(R.id.logoutBtn);



         Handler handler = new Handler(Looper.getMainLooper());
         Runnable runnable = new Runnable() {
            @Override
            public void run() {
                // Do what ever you want
                checkStatus();
            }
        };
        handler.postDelayed(runnable, 2000);


        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WaitingForApproval.this,
                        MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
//                if (isMyServiceRunning(StatusCheckServie.class)) {
//                    stopService(new Intent(getApplicationContext(), StatusCheckServie.class));
//                }
//                handler.removeCallbacks(runnable);
//                SharedHelper.clearSharedPreferences(getApplicationContext());
//                Intent mainIntent = new Intent(WaitingForApproval.this,
//                        Login.class);
//                mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//                startActivity(mainIntent);
//                WaitingForApproval.this.finish();
            }
        });
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

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    public void onBackPressed() {

    }

    private void checkStatus() {
        String url = URLHelper.base + "api/provider/trip";

        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.e("CheckStatus",""+response.toString());
                //SharedHelper.putKey(context, "currency", response.optString("currency"));

                if (response.optString("account_status").equals("approved")) {

                    startActivity(new Intent(WaitingForApproval.this, MainActivity.class));
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Error", error.toString());

                if (error instanceof NoConnectionError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof NetworkError) {
                    displayMessage(getString(R.string.oops_connect_your_internet));
                } else if (error instanceof TimeoutError) {
                    checkStatus();
                }


            }
        }){
            @Override
            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization","Bearer "+token);
                return headers;
            }
        };
        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    public void displayMessage(String toastString) {
        Toast.makeText(WaitingForApproval.this, toastString, Toast.LENGTH_SHORT).show();
    }

}
