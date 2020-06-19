package com.rapido.provider.Transportation.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.rapido.provider.Transportation.Constants.AutoCompleteAdapter;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Models.PlacePredictions;
import com.rapido.provider.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class FavouritePlaceSearch extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
ImageView backArrow,imgSourceClose;
String type;
TextView addressType,txtaddressSource;
    double latitude;
    double longitude;
    private ListView mAutoCompleteList;
    private AutoCompleteAdapter mAutoCompleteAdapter;
    private String GETPLACESHIT = "places_hit";
    private PlacePredictions predictions = new PlacePredictions();
    private Handler handler;
    private GoogleApiClient mGoogleApiClient;
    private PlacePredictions placePredictions = new PlacePredictions();
    private Location mLastLocation;
    Boolean isInternet;
    ConnectionHelper helper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_place_search);
        backArrow= findViewById(R.id.backArrow);
        addressType= findViewById(R.id.addressType);
        txtaddressSource= findViewById(R.id.txtaddressSource);
        imgSourceClose= findViewById(R.id.imgSourceClose);

        mAutoCompleteList = findViewById(R.id.searchResultLV);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        backArrow.setOnClickListener(this);

        txtaddressSource.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    imgSourceClose.setVisibility(View.VISIBLE);
                } else {
                    imgSourceClose.setVisibility(View.GONE);
                }
            }
        });
        imgSourceClose.setOnClickListener(v -> {
            txtaddressSource.setText("");
            mAutoCompleteList.setVisibility(View.GONE);
            imgSourceClose.setVisibility(View.GONE);
            txtaddressSource.requestFocus();
        });



        txtaddressSource.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // optimised way is to start searching for laction after user has typed minimum 3 chars

                if (txtaddressSource.getText().length() > 0) {
                    imgSourceClose.setVisibility(View.VISIBLE);

                    if (mAutoCompleteAdapter == null)
                        mAutoCompleteList.setVisibility(View.VISIBLE);
                    Runnable run = new Runnable() {

                        @Override
                        public void run() {
                            // cancel all the previous requests in the queue to optimise your network calls during autocomplete search
//                            Ilyft.getInstance().cancelRequestInQueue(GETPLACESHIT);

                            JSONObject object = new JSONObject();
                            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, getPlaceAutoCompleteUrl(txtaddressSource.getText().toString()),
                                    object, new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Log.v("PayNowRequestResponse", response.toString());
                                    Gson gson = new Gson();
                                    predictions = gson.fromJson(response.toString(), PlacePredictions.class);
                                    if (mAutoCompleteAdapter == null) {
                                        mAutoCompleteAdapter = new AutoCompleteAdapter(FavouritePlaceSearch.this, predictions.getPlaces(), FavouritePlaceSearch.this);
                                        mAutoCompleteList.setAdapter(mAutoCompleteAdapter);
                                    } else {
                                        mAutoCompleteList.setVisibility(View.VISIBLE);
                                        mAutoCompleteAdapter.clear();
                                        mAutoCompleteAdapter.addAll(predictions.getPlaces());
                                        mAutoCompleteAdapter.notifyDataSetChanged();
                                        mAutoCompleteList.invalidate();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.v("PayNowRequestResponse", error.toString());
                                }
                            });
                            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

                        }

                    };

                    // only canceling the network calls will not help, you need to remove all callbacks as well
                    // otherwise the pending callbacks and messages will again invoke the handler and will send the request
                    if (handler != null) {
                        handler.removeCallbacksAndMessages(null);
                    } else {
                        handler = new Handler();
                    }
                    handler.postDelayed(run, 1000);

                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                imgSourceClose.setVisibility(View.VISIBLE);
            }

        });

        mAutoCompleteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setGoogleAddress(position);
            }
        });
        getDataType();
    }

    private void setGoogleAddress(int position) {
        buildGoogleApiClient();

        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
        if (mGoogleApiClient != null) {

            Places.GeoDataApi.getPlaceById(mGoogleApiClient, predictions.getPlaces().get(position).getPlaceID())
                    .setResultCallback(new ResultCallback<PlaceBuffer>() {
                        @Override
                        public void onResult(PlaceBuffer places) {
                            if (places.getStatus().isSuccess()) {
                                Place myPlace = places.get(0);
                                LatLng queriedLocation = myPlace.getLatLng();
                                Log.v("Latitude is", "" + queriedLocation.latitude);
                                Log.v("Longitude is", "" + queriedLocation.longitude);

                                placePredictions.strSourceAddress = myPlace.getAddress().toString();
                                placePredictions.strSourceLatLng = myPlace.getLatLng().toString();
                                placePredictions.strSourceLatitude = myPlace.getLatLng().latitude + "";
                                placePredictions.strSourceLongitude = myPlace.getLatLng().longitude + "";
                                txtaddressSource.setText(placePredictions.strSourceAddress);
                                saveLocationAddress(type,myPlace.getLatLng().latitude+"",myPlace.getLatLng().longitude+"",
                                        myPlace.getAddress().toString());
                            }
                            mAutoCompleteList.setVisibility(View.GONE);


                        }
                    });
        }
    }

    public String getPlaceAutoCompleteUrl(String input) {
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/place/autocomplete/json");
        urlString.append("?input=");
        try {
            urlString.append(URLEncoder.encode(input, "utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        urlString.append("&location=");
        urlString.append(latitude + "," + longitude); // append lat long of current location to show nearby results.
        urlString.append("&radius=500&language=en");
        urlString.append("&key=" + getResources().getString(R.string.google_map_api));

        Log.d("FINAL URL:::   ", urlString.toString());
        return urlString.toString();
    }
    private void getDataType() {
        type=getIntent().getStringExtra("type");
        addressType.setText(type+" "+"Address");
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
    }
    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.backArrow)
        {
            onBackPressed();
            finish();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);

            if (mLastLocation != null) {
                latitude = mLastLocation.getLatitude();
                longitude = mLastLocation.getLongitude();
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void saveLocationAddress(String locationType,String lat,String lng,String address) {
        String addressLat = lat;
        String addressLng = lng;
        String addressLocation = address;
        String type = locationType;
        String id = SharedHelper.getKey(getApplicationContext(), "id");

        if (isInternet) {
          Dialog customDialog = new Dialog(FavouritePlaceSearch.this);
          customDialog.setContentView(R.layout.custom_dialog);
          customDialog.show();


            JSONObject object = new JSONObject();
            try {
                object.put("location_type", type);
                object.put("user_id", id);
                object.put("address", addressLocation);
                object.put("longitude", addressLat);
                object.put("latitude", addressLng);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                    URLHelper.SAVE_LOCATION,
                    object,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if ((customDialog != null) && customDialog.isShowing())
                                customDialog.dismiss();
                            SharedHelper.putKey(FavouritePlaceSearch.this,type+"_address",addressLocation);
                            callSuccess();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            })
            {
                @Override
                public Map<String, String> getHeaders() {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    return headers;
                }
            };
            Ilyft.getInstance().addToRequestQueue(objectRequest);
        }


    }

    private void callSuccess() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","result");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }
}
