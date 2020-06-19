package com.rapido.provider.Transportation.Constants;

/**
 * Created by jayakumar on 11/02/17.
 */

import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.rapido.provider.R;
import com.rapido.provider.Transportation.Bean.CardDetails;


import java.util.ArrayList;


public class PaymentListAdapter extends ArrayAdapter<CardDetails> {

    public ArrayList<CardDetails> list;
    int vg;
    Context context;

    public PaymentListAdapter(Context context, int vg, ArrayList<CardDetails> list) {

        super(context, vg, list);
        this.context = context;
        this.vg = vg;
        this.list = list;

    }

    @Override
    public int getCount() {
        return list.size();
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(vg, parent, false);
        ImageView paymentTypeImg = (ImageView) itemView.findViewById(R.id.paymentTypeImg);
        RadioButton radioButton = (RadioButton) itemView.findViewById(R.id.radioButton);

        TextView accountNumber =  itemView.findViewById(R.id.accountNumber);
        TextView accountName =  itemView.findViewById(R.id.accountName);


        ImageView tickImg = (ImageView) itemView.findViewById(R.id.img_tick);
        tickImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setTitle("Delete")
                        .setMessage("Do you really want to delete?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(context, "Yaay", Toast.LENGTH_SHORT).show();
                            }})
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        try {
            Log.d(this.getClass().getName(), "CARDS_SIZE" + list.size());

            if (list.get(position).getType().equalsIgnoreCase("paypal"))
            {
                accountNumber.setText("Paypal" + "");
                //accountNumber.setText("xxxx - xxxx - xxxx - "+list.get(position).getAccountNumber());
                accountName.setText(list.get(position).getPaypal_id());
            }
            else {
                accountNumber.setText(list.get(position).getAccountNumber() + "");
                //accountNumber.setText("xxxx - xxxx - xxxx - "+list.get(position).getAccountNumber());
                accountName.setText(list.get(position).getBankName());
            }

            radioButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (list.get(position).getIs_default() == 0) {

                        //  makeDefaultCard(list.get(position).getId());
                    } else {
                        Toast.makeText(context, "already default", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemView;
    }


//    public void makeDefaultCard(int cardnoId) {
//
//        JSONObject object = new JSONObject();
//        String url = URLHelper.CARETAKER_SERVICE_LIST + cardnoId;
//        Log.e("URL_GET_DEFAULT", "" + url);
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                  /*  if (dialog != null && dialog.isShowing())
//                        dialog.dismiss();*/
//                Log.e("getDefaultCardREwsponse", "RESPONSE_DEFAULT_CARD" + response.toString());
//                try {
//                    int status = response.optInt("status");
//                    if (status == 1) {
//                        //GoToPrescriptionActivity();
//                        Intent intent = new Intent(context, WithdrawAmountActivity.class);
//                        context.startActivity(intent);
//                    } else if (status == 0) {
//                        //displayMessage(response.getString("msg"));
//                        Intent intent = new Intent(context, WithdrawAmountActivity.class);
//                        context.startActivity(intent);
//
//                        //startActivity(new Intent(CountDownActivity.this, MainActivity.class));
//                    }
//
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                  /*  if (dialog != null && dialog.isShowing())
//                        dialog.dismiss();*/
//                String json = null;
//                String Message;
//                NetworkResponse response = error.networkResponse;
//                if (response != null && response.data != null) {
//                    try {
//                        JSONObject errorObj = new JSONObject(new String(response.data));
//
//                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
//                            // displayMessage(getString(R.string.something_went_wrong));
//                        } else if (response.statusCode == 401) {
//                            SharedHelper.putKey(context, "loggedIn", context.getString(R.string.False));
//                            // GoToBeginActivity();
//                        } else if (response.statusCode == 422) {
//
//                            json = trimMessage(new String(response.data));
//                            if (json != "" && json != null) {
//                                //displayMessage(json);
//                            } else {
//                                // displayMessage(getString(R.string.please_try_again));
//                            }
//
//                        } else {
//                            // displayMessage(getString(R.string.please_try_again));
//                        }
//
//                    } catch (Exception e) {
//                        //displayMessage(getString(R.string.something_went_wrong));
//                    }
//
//
//                } else {
//                    if (error instanceof NoConnectionError) {
//                        //displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof NetworkError) {
//                        // displayMessage(getString(R.string.oops_connect_your_internet));
//                    } else if (error instanceof TimeoutError) {
//                        makeDefaultCard(cardnoId);
//                    }
//                }
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
//                return headers;
//            }
//        };
//
//        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
//    }

}