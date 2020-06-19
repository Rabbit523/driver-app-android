package com.rapido.provider.Transportation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.google.android.material.snackbar.Snackbar;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ComplaintsAcivity extends AppCompatActivity {

    private static final String[] HEAR_OPTIONS = {
            "Driver Late",
            "Driver Unprofessional",
            "I Had Different Issue",
            "Driver Changed",
    };

    ImageView ivBack;
    TextView tvSubmit;
    MaterialSpinner spRegister;
    EditText etComplaint;
    String complaintName = "Driver Late";

    Utilities utils = new Utilities();
    CustomDialog customDialog;
    Boolean isInternet;
    ConnectionHelper helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints);
        helper = new ConnectionHelper(ComplaintsAcivity.this);
        isInternet = helper.isConnectingToInternet();
        ivBack = findViewById(R.id.ivBack);
        tvSubmit = findViewById(R.id.tvSubmit);
        spRegister = findViewById(R.id.spRegister);
        etComplaint = findViewById(R.id.etComplaint);
        ivBack.setOnClickListener(view -> onBackPressed());

        spRegister.setItems(HEAR_OPTIONS);
        spRegister.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>)
                (view, position, id, item) -> {
                    complaintName = item;
                    Log.d("complaintName", complaintName);
                });
        Log.d("complaintName1", complaintName);

        spRegister.setOnNothingSelectedListener(spinner -> {

        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etComplaint.getText().equals("")) {
                    displayMessage(getString(R.string.complaint_not_empty));
                } else {
                    submitComplaint();
                }
            }
        });
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(getCurrentFocus(), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private void submitComplaint() {
        if (isInternet) {
            customDialog = new CustomDialog(ComplaintsAcivity.this);
            if (customDialog != null)
                customDialog.show();

            String issue = etComplaint.getText().toString();
            String issueType = complaintName;
            String id = SharedHelper.getKey(ComplaintsAcivity.this, "id");

            JSONObject object = new JSONObject();
            try {
                object.put("user_id", id);
                object.put("description", issue);
                object.put("complaint_type", issueType);
                utils.print("input to complaint", "" + object);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.POST,
                    URLHelper.COMPLAINT, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if ((customDialog != null) && customDialog.isShowing())
                        customDialog.dismiss();
                    utils.print("ComplaintResponse", response.toString());
                    displayMessage(getString(R.string.complaint_submitted));
                    onBackPressed();

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
