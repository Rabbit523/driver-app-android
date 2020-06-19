package com.rapido.provider.Transportation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.rapido.provider.Login;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Bean.WithdrawAmount;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.stripe.android.Stripe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;

public class WithdrawActivity extends AppCompatActivity implements View.OnClickListener {

    ImageView backArrow;
    String locale;
    WithdrawAdapter withdrawAdapter;
    ArrayList<WithdrawAmount> withdrawAmountArrayList;
    private Button addAccountDetailsBtn;
    private Stripe stripe;
    private CustomDialog customDialog;
    private LinearLayout layoutMainId;
    private String bankAccount;
    private String totalAmountTransfer;
    private Button addAmountBtn;
    private EditText amountEditText;
    private TextView earnedMoneyTxtView;
    private String providerId;
    private RecyclerView recyclerWithdraw;
    private LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw);

        stripe = new Stripe(this);
        stripe.setDefaultPublishableKey(URLHelper.STRIPE_TOKEN);
        initViews();

        //SharedHelper.getKey(WithdrawActivity.this,"user_provider_id");
        providerId = SharedHelper.getKey(WithdrawActivity.this, "id");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            locale = this.getResources().getConfiguration().getLocales().get(0).getCountry();
        } else {
            locale = this.getResources().getConfiguration().locale.getCountry();
        }


        Log.v("Country_Code", "Locale" + locale);
        getWithdrawList();

    }

    private void initViews() {
        backArrow = (ImageView) findViewById(R.id.backArrow);
        addAccountDetailsBtn = (Button) findViewById(R.id.addAccountDetailsBtn);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        recyclerWithdraw = (RecyclerView) findViewById(R.id.recyclerWithdraw);
        layoutMainId = (LinearLayout) findViewById(R.id.layoutMainId);
        earnedMoneyTxtView = (TextView) findViewById(R.id.earnedMoneyTxtView);
        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerWithdraw.setLayoutManager(mLayoutManager);
        recyclerWithdraw.setItemAnimator(new DefaultItemAnimator());
        addAccountDetailsBtn.setOnClickListener(this);

        backArrow.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backArrow:
                onBackPressed();
                break;
            case R.id.addAccountDetailsBtn:
                Intent intent = new Intent(this, WithdrawAmountActivity.class);
                startActivityForResult(intent,1);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                getWithdrawList();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }//onActivityResult
    public void getWithdrawList() {

        customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        customDialog.show();
        String urlAddMoney = URLHelper.GET_WITHDRAW_LIST;

        Log.v("Money_TRANSFER_URL", "URL" + urlAddMoney);
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlAddMoney, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        Log.v("TAG", "RESPONSE" + response);
                        if (response.optInt("status") == 1) {
                            int totalEarn = response.optInt("totalEarn");
                            earnedMoneyTxtView.setText(SharedHelper.getKey(getApplicationContext(), "currency") + totalEarn + "");
                            withdrawAdapter = new WithdrawAdapter(response);
                            recyclerWithdraw.setAdapter(withdrawAdapter);


                        } else {
                            int totalEarn = response.optInt("totalEarn");
                            earnedMoneyTxtView.setText("$ " + totalEarn + "");
                        }

                    } else {
                        displayMessage(response.optString("msg"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                customDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
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
                            SharedHelper.putKey(getApplicationContext(), "loggedIn", getString(R.string.False));
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
                        e.printStackTrace();
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getWithdrawList();
                    }
                }
                customDialog.dismiss();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(getApplicationContext(), "access_token"));
                return headers;
            }
        };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public void GoToBeginActivity() {
        Intent mainIntent = new Intent(getApplicationContext(), Login.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(mainIntent);
        WithdrawActivity.this.finish();
    }

    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.layoutMainId), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private class WithdrawAdapter extends RecyclerView.Adapter<WithdrawAdapter.MyViewHolder> {
        JSONObject jsonResponse;

        public WithdrawAdapter(JSONObject jsonResponse) {
            this.jsonResponse = jsonResponse;
        }

        @Override
        public WithdrawAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.withdraw_item_adapter, parent, false);
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(WithdrawAdapter.MyViewHolder holder, final int position) {
            JSONArray jsonArray = jsonResponse.optJSONArray("data");
            try {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                holder.lblWithdrawAmount.setText(SharedHelper.getKey(getApplicationContext(), "currency") + jsonObject.optString("amount"));
                String from = null;
                try {
                    from = Utilities.getDateFormate(jsonObject.optString("created_at"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                holder.lblWithdrawDateTime.setText(from);
                holder.lblWithdrawStatus.setText(jsonObject.optString("status"));

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getItemCount() {
            return jsonResponse.optJSONArray("data").length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {
            TextView lblWithdrawAmount, lblWithdrawDateTime, lblWithdrawStatus;

            public MyViewHolder(View itemView) {
                super(itemView);
                lblWithdrawAmount = (TextView) itemView.findViewById(R.id.lblWithdrawAmount);
                lblWithdrawDateTime = (TextView) itemView.findViewById(R.id.lblWithdrawDateTime);
                lblWithdrawStatus = (TextView) itemView.findViewById(R.id.lblWithdrawStatus);
            }
        }
    }


}
