package com.rapido.provider.Transportation.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Helper.VolleyMultipartRequest;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;




import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;

public class UpdateProfile extends AppCompatActivity implements View.OnClickListener {
ImageView backArrow;
TextView toolName;
    String parameter,value;
    EditText editText;
    TextInputLayout text_input_layout;
    Button btnUpdate;
    Boolean isInternet;
    ConnectionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        toolName= findViewById(R.id.toolName);
        backArrow= findViewById(R.id.backArrow);
        editText= findViewById(R.id.editText);
        btnUpdate= findViewById(R.id.btnUpdate);
        text_input_layout= findViewById(R.id.text_input_layout);
        backArrow.setOnClickListener(this);
        btnUpdate.setOnClickListener(this);
        helper = new ConnectionHelper(getApplicationContext());
        isInternet = helper.isConnectingToInternet();
        getIntentData();


    }

    private void getIntentData() {
         parameter=getIntent().getStringExtra("parameter");
         value=getIntent().getStringExtra("value");
         if(parameter.equalsIgnoreCase("first_name"))
         {

             toolName.setText("Update Name");
             text_input_layout.setHelperText("This name will be shown to the driver during ride pickup");
             editText.setHint("Name");
             text_input_layout.setHint("Enter  Name");
             editText.setText(value);
             editText.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
         }

        if(parameter.equalsIgnoreCase("email"))
        {

            toolName.setText("Update Account Email");
            text_input_layout.setHelperText("It is updated to the your account");
            editText.setHint("Email");
            text_input_layout.setHint("Enter Email");
            editText.setText(value);
            editText.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        }
        if(parameter.equalsIgnoreCase("mobile"))
        {

            toolName.setText("Update Mobile No");
            editText.setHint("Mobile No");
            text_input_layout.setHint("Enter Mobile No");
            editText.setText(value);
            editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.backArrow)
        {
            onBackPressed();
            finish();
        }
        if (v.getId()==R.id.btnUpdate)
        {
            if (editText.getText().toString().equals(""))
            {
                text_input_layout.setError("This field is not empty");
            }
            else {
                if (isInternet) {
                    if (parameter.equals("first_name"))
                    {

                        SharedHelper.putKey(getApplicationContext(), "first_name",editText.getText().toString() );
                        updateProfileWithoutImage();
                    }
                   else  {
                        SharedHelper.putKey(getApplicationContext(), parameter, editText.getText().toString());
                        updateProfileWithoutImage();
                    }




                }
            }
        }
    }


    private void updateProfileWithoutImage() {
        Dialog dialogCustom=new Dialog(UpdateProfile.this);
        dialogCustom.setContentView(R.layout.custom_dialog);
        dialogCustom.setCancelable(false);
        dialogCustom.show();
        VolleyMultipartRequest volleyMultipartRequest = new VolleyMultipartRequest(Request.Method.POST, URLHelper.USER_PROFILE_API, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                dialogCustom.dismiss();
                String res = new String(response.data);
                try {
                    JSONObject jsonObject = new JSONObject(res);
                    SharedHelper.putKey(getApplicationContext(), "id", jsonObject.optString("id"));
                    SharedHelper.putKey(getApplicationContext(), "first_name", jsonObject.optString("first_name"));
//                    SharedHelper.putKey(getApplicationContext(), "last_name", jsonObject.optString("last_name"));
                    SharedHelper.putKey(getApplicationContext(), "email", jsonObject.optString("email"));
                    if (jsonObject.optString("avatar").equals("") || jsonObject.optString("avatar") == null) {
                        SharedHelper.putKey(getApplicationContext(), "picture", "");
                    } else {
                        if (jsonObject.optString("avatar").startsWith("http"))
                            SharedHelper.putKey(getApplicationContext(), "picture", jsonObject.optString("avatar"));
                        else
                            SharedHelper.putKey(getApplicationContext(), "picture", URLHelper.base + "storage/app/public/" + jsonObject.optString("avatar"));
                    }
                    SharedHelper.putKey(getApplicationContext(), "sos", jsonObject.optString("sos"));
                    SharedHelper.putKey(getApplicationContext(), "gender", jsonObject.optString("gender"));
                    SharedHelper.putKey(getApplicationContext(), "mobile", jsonObject.optString("mobile"));
//                        SharedHelper.putKey(context, "wallet_balance", jsonObject.optString("wallet_balance"));
//                        SharedHelper.putKey(context, "payment_mode", jsonObject.optString("payment_mode"));
                    callSuccess();
                    Toast.makeText(UpdateProfile.this, getString(R.string.update_success), Toast.LENGTH_SHORT).show();
                    //displayMessage(getString(R.string.update_success));

                } catch (JSONException e) {
                    e.printStackTrace();
                    displayMessage(getString(R.string.something_went_wrong));
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((dialogCustom != null) && dialogCustom.isShowing())
                    dialogCustom.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {
                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));
                        if (response.getClass().equals(TimeoutError.class)) {
                            updateProfileWithoutImage();
                            return;
                        }
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"));
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }
                        } else if (response.statusCode == 401) {

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
                        updateProfileWithoutImage();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("first_name",SharedHelper.getKey(getApplicationContext(),"first_name"));
                params.put("last_name", "");
                params.put("email", SharedHelper.getKey(getApplicationContext(),"email"));
                params.put("mobile", SharedHelper.getKey(getApplicationContext(),"mobile"));
                params.put("avatar", "");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                return headers;
            }
        };
        Ilyft.getInstance().addToRequestQueue(volleyMultipartRequest);
    }

    private void callSuccess() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result","result");
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    public void displayMessage(String toastString) {
        Log.e("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }
}
