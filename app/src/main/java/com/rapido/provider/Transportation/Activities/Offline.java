package com.rapido.provider.Transportation.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rapido.provider.Login;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Fragment.DriverMapFragment;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


public class Offline extends Fragment {

    Activity activity;
    Context context;
    ConnectionHelper helper;
    Boolean isInternet;
    View rootView;
    CustomDialog customDialog;
    String token;
    Button goOnlineBtn;
    //menu icon
    ImageView menuIcon;
    int NAV_DRAWER = 0;
    DrawerLayout drawer;

    Utilities utils = new Utilities();


    public Offline() {
        // Required empty public constructor
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView =  inflater.inflate(R.layout.activity_offline, container, false);
        findViewByIdAndInitialize();
        return  rootView;
    }

    public void findViewByIdAndInitialize(){
        helper = new ConnectionHelper(activity);
        isInternet = helper.isConnectingToInternet();
        token = SharedHelper.getKey(activity, "access_token");
        goOnlineBtn = rootView.findViewById(R.id.goOnlineBtn);
        menuIcon = rootView.findViewById(R.id.menuIcon);
        drawer = activity.findViewById(R.id.drawer_layout);
        goOnlineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    goOnline();
            }
        });
        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NAV_DRAWER == 0) {
                    drawer.openDrawer(Gravity.LEFT);
                }else {
                    NAV_DRAWER = 0;
                    drawer.closeDrawers();
                }
            }
        });
    }


    public void goOnline(){
        customDialog = new CustomDialog(activity);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject param = new JSONObject();
        try {
            param.put("service_status", "active");
        } catch (JSONException e) {
            e.printStackTrace();
        }
       JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.UPDATE_AVAILABILITY_API ,param, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        customDialog.dismiss();
                            if (response.optJSONObject("service").optString("status").equalsIgnoreCase("active")) {
//                            Intent intent = new Intent(context, MainActivity.class);
//                            context.startActivity(intent);
                                FragmentManager manager = MainActivity.fragmentManager;
                                FragmentTransaction transaction = manager.beginTransaction();
                                transaction.replace(R.id.content, new DriverMapFragment());
                                transaction.commitAllowingStateLoss();
                            } else {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else{
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
                if(response != null && response.data != null){

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500){
                            try{
                                displayMessage(errorObj.optString("message"));
                            }catch (Exception e){
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        }else if(response.statusCode == 401){
                            SharedHelper.putKey(context,"loggedIn",getString(R.string.False));
                            GoToBeginActivity();
                        }else if(response.statusCode == 422){
                            json = Ilyft.trimMessage(new String(response.data));
                            if(json !="" && json != null) {
                                displayMessage(json);
                            }else{
                                displayMessage(getString(R.string.please_try_again));
                            }

                        }else if(response.statusCode == 503){
                            displayMessage(getString(R.string.server_down));
                        }else{
                            displayMessage(getString(R.string.please_try_again));
                        }

                    }catch (Exception e){
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

    public void displayMessage(String toastString){
        utils.print("displayMessage",""+toastString);
        Snackbar.make(getView(),toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void GoToBeginActivity(){
        SharedHelper.putKey(activity,"loggedIn",getString(R.string.False));
        Intent mainIntent = new Intent(activity, Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        activity.finish();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
