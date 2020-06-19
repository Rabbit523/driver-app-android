package com.rapido.provider.Transportation.Activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.material.snackbar.Snackbar;
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Helper.Ilyft;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;


public class UserReview extends AppCompatActivity implements View.OnClickListener {
    ImageView backArrow;
    RecyclerView recReview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_review);

        recReview = findViewById(R.id.recReview);

        backArrow = findViewById(R.id.backArrow);
        backArrow.setOnClickListener(this);
        getReview();
    }

    private void getReview() {
               CustomDialog customDialog = new CustomDialog(UserReview.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
        try {
            object.put("provider_id", SharedHelper.getKey(UserReview.this,"id"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.GET_USERREVIEW, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    PostAdapter postAdapter = null;
                    try {
                        postAdapter = new PostAdapter(response.getJSONArray("Data"));
                        recReview.setHasFixedSize(true);
                        recReview.setLayoutManager(new LinearLayoutManager(UserReview.this) {
                            @Override
                            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                            }
                        });
                        recReview.setAdapter(postAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                } else {
//                    errorLayout.setVisibility(View.VISIBLE);
                }

                customDialog.dismiss();
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if ((customDialog != null) && (customDialog.isShowing()))
                    customDialog.dismiss();
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;
                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.getString("message"));
                            } catch (Exception e) {
                                displayMessage(errorObj.optString("error"));
                                //displayMessage(getString(R.string.something_went_wrong));
                            }
//                            utils.print("MyTest", "" + errorObj.toString());
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

                    }
                }
            }
        }) {
            @Override

            public java.util.Map<String, String> getHeaders() {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(UserReview.this, "access_token"));
                return headers;
            }
        };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);


    }


    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.recReview), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    private String getYear(String date) throws ParseException {
        Date d = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        String yearName = new SimpleDateFormat("dd MMM yyyy").format(cal.getTime());
        return yearName;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.backArrow) {
            onBackPressed();
            finish();
        }
    }

    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {
        JSONArray jsonArray;

        public PostAdapter(JSONArray array) {
            this.jsonArray = array;
        }

        public void append(JSONArray array) {
            try {
                for (int i = 0; i < array.length(); i++) {
                    this.jsonArray.put(array.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public PostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.review_item, parent, false);
            return new PostAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            try {

                if (!jsonArray.optJSONObject(position).optString("user_comment").isEmpty()) {
                    holder.txtComment.setText(jsonArray.optJSONObject(position).optString("user_comment"));
                } else {
                    holder.txtComment.setText("No Comment");
                }
                holder.userRating.setRating(Float.parseFloat(jsonArray.optJSONObject(position).optString("user_rating")));

                if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("created_at");
                    try {
                        holder.txtDateTime.setText(getYear(form));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return jsonArray.length();
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {

            TextView txtComment, txtDateTime;
            RatingBar userRating;

            public MyViewHolder(View itemView) {
                super(itemView);

                txtComment = itemView.findViewById(R.id.txtComment);
                userRating = itemView.findViewById(R.id.userRating);
                txtDateTime = itemView.findViewById(R.id.txtDateTime);


            }
        }
    }
}
