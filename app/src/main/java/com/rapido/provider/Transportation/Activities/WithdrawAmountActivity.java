package com.rapido.provider.Transportation.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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
import com.rapido.provider.Transportation.Bean.CardDetails;
import com.rapido.provider.Transportation.Constants.PaymentListAdapter;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Utilities.Utilities;
import com.stripe.android.Stripe;
import com.ybs.countrypicker.CountryPicker;
import com.ybs.countrypicker.CountryPickerListener;


import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;

public class WithdrawAmountActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<JSONObject> listItems;
    Utilities utils = new Utilities();
    private CustomDialog customDialog;
    private ArrayList<CardDetails> cardArrayList;
    private ListView payment_list_view;
    private PaymentListAdapter paymentAdapter;
    private Button addBankAccountBtn;
    private Button addAmountBtn;
    private Stripe stripe;
    private EditText addAccountName;
    private ImageView backArrow;
    private EditText amountEditText;
    private String providerID;
    private int accountId;
    private LinearLayout withdrawLayout;
    private EditText addBankName;
    private TextView selectAmountTxt;
    private LinearLayout noBankDetailsFoundLayout;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_amount);
        stripe = new Stripe(this);
        stripe.setDefaultPublishableKey(URLHelper.STRIPE_TOKEN);

        providerID = SharedHelper.getKey(WithdrawAmountActivity.this, "id");

        initViews();
        getCardList();


        payment_list_view.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String accountHolderName = cardArrayList.get(position).getAccountName();
                String bankName = cardArrayList.get(position).getBankName();
                int accountNumber = cardArrayList.get(position).getAccountNumber();
                int routingNumber = cardArrayList.get(position).getRoutingNumber();
                String countryName = cardArrayList.get(position).getCountryName();
                String currency = cardArrayList.get(position).getCurrency();


                bankDetailsPoupUp(accountHolderName, bankName, accountNumber, routingNumber, countryName);

                return true;
            }
        });

    }

    private void bankDetailsPoupUp(String accountHName, String bankName, int accountNumber, int routingNumber, String countryName) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawAmountActivity.this);
        View customView = LayoutInflater.from(WithdrawAmountActivity.this).inflate(R.layout.bank_details_popup, null);
        // View customView = layoutInflater.inflate(R.layout.bank_details_popup,null);
        builder.setIcon(R.mipmap.ic_launcher)
                .setTitle(R.string.bank_details)
                .setView(customView)
                .setCancelable(true);
        final AlertDialog dialog = builder.create();

        Button oKBtn = (Button) customView.findViewById(R.id.oKBtn);
        //instantiate popup window
        //popupWindow = new PopupWindow(customView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
        TextView textViewBankName = (TextView) customView.findViewById(R.id.textViewBankName);
        TextView textViewAccountNumber = (TextView) customView.findViewById(R.id.textViewAccountNumber);
        TextView textViewRoutingNumber = (TextView) customView.findViewById(R.id.textViewRoutingNumber);
        TextView textViewAccountName = (TextView) customView.findViewById(R.id.textViewAccountName);
        TextView textViewCountry = (TextView) customView.findViewById(R.id.textViewCountry);

        textViewAccountName.setText(accountHName);
        textViewBankName.setText(bankName);
        textViewAccountNumber.setText(accountNumber + "");
        textViewRoutingNumber.setText(routingNumber + "");
        textViewCountry.setText(countryName);
        //close the popup window on button click
        oKBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void initViews() {
        payment_list_view = (ListView) findViewById(R.id.payment_list_view);
        addBankAccountBtn = (Button) findViewById(R.id.addBankAccountBtn);
        addAmountBtn = (Button) findViewById(R.id.addAmountBtn);
        backArrow = (ImageView) findViewById(R.id.backArrow);
        amountEditText = (EditText) findViewById(R.id.amountEditText);
        withdrawLayout = (LinearLayout) findViewById(R.id.withdrawLayout);
        noBankDetailsFoundLayout = (LinearLayout) findViewById(R.id.noBankDetailsFoundLayout);
        selectAmountTxt = (TextView) findViewById(R.id.selectAmountTxt);
        addBankAccountBtn.setOnClickListener(this);
        backArrow.setOnClickListener(this);
        addAmountBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addBankAccountBtn:
                addAccountDialog();
                break;
            case R.id.backArrow:
                onBackPressed();
                break;
            case R.id.addAmountBtn:
                getWithDrawAmount();
                break;

        }
    }
    String type="";
    private void addAccountDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(WithdrawAmountActivity.this);
        View view = LayoutInflater.from(WithdrawAmountActivity.this).inflate(R.layout.add_bank_account_item, null);
        Button continue_btn = (Button) view.findViewById(R.id.addAccountDetailsBtn);
        addAccountName = (EditText) view.findViewById(R.id.addAccountName);
        addBankName = (EditText) view.findViewById(R.id.addBankName);
        EditText addAccountNumber = (EditText) view.findViewById(R.id.addAccountNumber);
        EditText addCountryName = (EditText) view.findViewById(R.id.addCountryName);
        EditText paypalId=view.findViewById(R.id.paypalId);
        Button cancelBtn = (Button) view.findViewById(R.id.cancelBtn);
        RadioGroup rg =view.findViewById(R.id.rg);
        LinearLayout layoutbank =view.findViewById(R.id.layoutbank);
        LinearLayout layoutPaypal = view.findViewById(R.id.layoutPaypal);
        RadioButton radioBank = view.findViewById(R.id.radioBank);
        RadioButton radioPaypal= view.findViewById(R.id.radioPaypal);
        addCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CountryPicker picker = CountryPicker.newInstance("Select Country");  // dialog title
                picker.setListener(new CountryPickerListener() {
                    @Override
                    public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                       addCountryName.setText(name);
                        picker.dismiss();
                    }
                });
                picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
            }
        });
        radioBank.setChecked(true);
        layoutbank.setVisibility(View.VISIBLE);
        layoutPaypal.setVisibility(View.GONE);
        type="bank";
        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioBank)
                {
                    radioBank.setChecked(true);
                    layoutbank.setVisibility(View.VISIBLE);
                    layoutPaypal.setVisibility(View.GONE);
                    type="bank";
                }
                else if (checkedId == R.id.radioPaypal)
                {
                    radioPaypal.setChecked(true);
                    layoutbank.setVisibility(View.GONE);
                    layoutPaypal.setVisibility(View.VISIBLE);
                    type="paypal";
                }
            }
        });

        builder.setTitle(R.string.add_account_details)
                .setView(view)
                .setCancelable(true);
        final AlertDialog dialog = builder.create();
        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (type.equals("bank")) {
                    if (addBankName.getText().toString().matches("")) {
                        Toast.makeText(WithdrawAmountActivity.this,
                                getApplicationContext().getResources()
                                        .getString(R.string.bank_name), Toast.LENGTH_SHORT).show();
                    } else if (addAccountName.getText().toString().matches("")) {
                        Toast.makeText(WithdrawAmountActivity.this,
                                getApplicationContext().getResources()
                                        .getString(R.string.account_holder_name), Toast.LENGTH_SHORT).show();
                    } else if (addAccountNumber.getText().toString().matches("")) {
                        Toast.makeText(WithdrawAmountActivity.this,
                                getApplicationContext().getResources()
                                        .getString(R.string.account_number), Toast.LENGTH_SHORT).show();
                    }
               /* else if (addCountryName.getText().toString().matches(""))
                {
                    Toast.makeText(WithdrawAmountActivity.this, "please enter Country Name", Toast.LENGTH_SHORT).show();
                }*/
                    else {

                        addAccountDetails(addAccountNumber.getText().toString(), addCountryName.getText().toString(),"",type);

                    }
                }
                else {
                    if (paypalId.getText().toString().isEmpty())
                    {
                        Toast.makeText(WithdrawAmountActivity.this,
                              "Enter Paypal id", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        addAccountDetails("", "",paypalId.getText().toString(),type);
                    }
                }
            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void addAccountDetails(String accountNumber, String countryCode,String paypalId,String type) {

        getAddAccountDetails(addBankName.getText().toString(), addAccountName.getText().toString(), accountNumber, "110000000",paypalId,type);

    }


    // get added bank and card list api
    public void getCardList() {
        customDialog = new CustomDialog(WithdrawAmountActivity.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_CARD_LIST_DETAILS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (customDialog != null && customDialog.isShowing())
                        customDialog.dismiss();
                    Log.e(this.getClass().getName(), "RESPONSE_CARD" + response);
                    if (response != null) {
                        cardArrayList = new ArrayList<>();
                        if (response.optInt("status") == 1) {
                            JSONArray jsonArray = response.getJSONArray("data");
                            if (jsonArray != null) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    CardDetails cardDetails = new CardDetails();
                                    cardDetails.setAccountName(jsonObject.optString("account_name"));
                                    cardDetails.setAccountNumber(jsonObject.optInt("account_number"));
                                    cardDetails.setBankName(jsonObject.optString("bank_name"));
                                    cardDetails.setRoutingNumber(jsonObject.optInt("routing_number"));
                                    cardDetails.setCountryName(jsonObject.optString("country"));
                                    cardDetails.setCurrency(jsonObject.optString("currency"));
                                    cardDetails.setType(jsonObject.optString("type"));
                                    cardDetails.setPaypal_id(jsonObject.optString("paypal_id"));
                                    SharedHelper.putKey(WithdrawAmountActivity.this, "AccountId_SP", jsonObject.getString("id"));
                                    cardArrayList.add(cardDetails);
                                }
                            }
                            paymentAdapter = new PaymentListAdapter(getApplicationContext(), R.layout.payment_list_item, cardArrayList);
                            payment_list_view.setAdapter(paymentAdapter);
                        } else {
                            Log.e("TAG", "NO_BANK_DETAILS_FOUND");
                            withdrawLayout.setVisibility(View.GONE);
                            selectAmountTxt.setVisibility(View.GONE);
                            noBankDetailsFoundLayout.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Log.e("TAG", "NO_BANK_DETAILS_FOUND");
                        withdrawLayout.setVisibility(View.GONE);
                        selectAmountTxt.setVisibility(View.GONE);
                        noBankDetailsFoundLayout.setVisibility(View.VISIBLE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("TAG", "EXCEPTION==" + e.getMessage());
                }

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
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        // getWithDrawAmount();
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







    /* *//*Pop up dialog for account details *//*
    public PopupWindow popupWindowDogs() {

        // initialize a pop up window type
        PopupWindow popupWindow = new PopupWindow(this);

        // the drop down list is a list view
        ListView listViewDogs = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewDogs.setAdapter(dogsAdapter(popUpContents));

        // set the item click listener
        listViewDogs.setOnItemClickListener(new DogsDropdownOnItemClickListener());

        // some other visual settings
        popupWindow.setFocusable(true);
        popupWindow.setWidth(250);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // set the list view as pop up window content
        popupWindow.setContentView(listViewDogs);

        return popupWindow;
    }
*/


    // add bank account details
    public void getAddAccountDetails(String bankName, String accountName, String accountNumber, String routingNumber,String paypalId,String type) {

        customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        customDialog.show();
        String url="";
        if (type.equalsIgnoreCase("bank"))
        {
             url = URLHelper.GET_ADD_BANK_DETAILS + accountName + "&account_number=" + Integer.parseInt(accountNumber) + "&routing_number=" + routingNumber +  "&country=" + "United States" + "&currency=USD" + "&bank_name=" + bankName+
                    "&type=" + type+"&paypal_id=" + paypalId;
        }
        else {
             url = URLHelper.GET_ADD_BANK_DETAILS + accountName + "&account_number=" + 0 + "&routing_number=" + routingNumber  + "&country=" + "United States" + "&currency=USD" + "&bank_name=" + bankName+
                    "&type=" + type+"&paypal_id=" + paypalId;
        }
        // http://testapplication.xyz/api/provider/addBank?account_name=hello sir&account_number=223&routing_number=22&provider_id=22146229&country=United States&currency=USD

        Log.e("TAG", "URLS:" + url);
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (customDialog.isShowing())
                        customDialog.dismiss();
                    if (response != null) {
                        if (response.optInt("status") == 1) {
                            JSONObject jsonObject = response.getJSONObject("data");
                            SharedHelper.putKey(WithdrawAmountActivity.this, "AccountName_SP", jsonObject.getString("account_name"));
                            SharedHelper.putKey(WithdrawAmountActivity.this, "AccountId_SP", jsonObject.getString("id"));
                            startActivity(new Intent(WithdrawAmountActivity.this, WithdrawAmountActivity.class));
                        } else {
                            displayMessage(getApplicationContext().getResources()
                                    .getString(R.string.you_not_have_an_account));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getAddAccountDetails(bankName, accountName, accountNumber, routingNumber,paypalId,type);
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

    // get amount withdraw api
    public void getWithDrawAmount() {

        customDialog = new CustomDialog(this);
        customDialog.setCancelable(false);
        customDialog.show();
        // http://testapplication.xyz/api/provider/withdrawalRequest?provider_id=323&bank_account_id=22&amount=43
        String accountIdSp = SharedHelper.getKey(WithdrawAmountActivity.this, "AccountId_SP");
        String urlWithDrawMoney = URLHelper.WITHDRAW_REQUEST + providerID + "&bank_account_id=" + accountIdSp + "&amount=" + amountEditText.getText().toString();

        Log.e("Money_TRANSFER_URL", "URL" + urlWithDrawMoney);
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, urlWithDrawMoney, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response != null) {
                        Log.e("Money_TRANSFER_URL", "Response" + response+" ");
                        if (response.optInt("status") == 1) {
                            Toast.makeText(WithdrawAmountActivity.this,getApplicationContext().getResources()
                                    .getString(R.string.fifteen_days),Toast.LENGTH_LONG).show();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK,returnIntent);
                            finish();
//                            displayMessage(getApplicationContext().getResources()
//                                    .getString(R.string.fifteen_days));
//
                        }
                        else {
                            displayMessage(response.optString("msg"));
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
                        displayMessage(getString(R.string.something_went_wrong));
                    }

                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        getWithDrawAmount();
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
        WithdrawAmountActivity.this.finish();
    }

    public void displayMessage(String toastString) {
        utils.print("displayMessage", "" + toastString);
        Snackbar.make(findViewById(R.id.withdrawLayout), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }


}
