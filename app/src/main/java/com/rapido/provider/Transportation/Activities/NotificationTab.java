package com.rapido.provider.Transportation.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import com.rapido.provider.R;
import com.rapido.provider.Transportation.Helper.CustomDialog;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;


import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;

public class NotificationTab extends AppCompatActivity   {
    ImageView backArrow;
    RecyclerView recReview;
    LinearLayout layoutNotification;
     SensorManager sensorManager;
    Jzvd.JZAutoFullscreenListener sensorEventListener;
//    private ExoPlayerHelper mExoPlayerHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_tab);
        layoutNotification = findViewById(R.id.layoutNotification);
        backArrow=findViewById(R.id.backArrow);
        recReview = findViewById(R.id.recReview);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
                if (Jzvd.backPress()) {
                    return;
                }
            }
        });
        getNotifications();


    }

    private void getNotifications() {

        CustomDialog customDialog = new CustomDialog(NotificationTab.this);
        customDialog.setCancelable(false);
        customDialog.show();
        JSONObject object = new JSONObject();
//        try {
//            object.put("provider_id", SharedHelper.getKey(NotificationTab.this,"id"));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URLHelper.GET_NOTIFICATIONS, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Log.v("NotificationResponse",response+" ");
                if (response != null) {
                    PostAdapter postAdapter = null;
                    try {
                        postAdapter = new PostAdapter(response.getJSONArray("Data"));
                        recReview.setHasFixedSize(true);
                        recReview.setLayoutManager(new LinearLayoutManager(NotificationTab.this) {
                            @Override
                            public RecyclerView.LayoutParams generateDefaultLayoutParams() {
                                return new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.WRAP_CONTENT);
                            }
                        });
                        if (postAdapter != null && postAdapter.getItemCount() > 0) {
                            layoutNotification.setVisibility(View.GONE);
                            recReview.setAdapter(postAdapter);
                        } else {
                            layoutNotification.setVisibility(View.VISIBLE);
                        }
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
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(NotificationTab.this, "access_token"));
                return headers;
            }
        };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

    }


    public void displayMessage(String toastString) {
        Snackbar.make(findViewById(R.id.recReview), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }



    private class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder>  {
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
                    .inflate(R.layout.notification_item, parent, false);
            return new PostAdapter.MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(PostAdapter.MyViewHolder holder, int position) {
            try {

                if (!jsonArray.optJSONObject(position).optString("notification_text").isEmpty()) {
                    holder.txtNotification.setText(jsonArray.optJSONObject(position).optString("notification_text"));
                } else {
                    holder.txtNotification.setText("No Comment");
                }

                holder.txtDateTime.setText(jsonArray.optJSONObject(position).optString("expiration_date"));

                if (!jsonArray.optJSONObject(position).optString("expiration_date", "").isEmpty()) {
                    String form = jsonArray.optJSONObject(position).optString("expiration_date");

                }
                if (jsonArray.optJSONObject(position).optString("image").contains(".mp4"))
                {
                    holder.imgNoti.setVisibility(View.GONE);
                    holder.imgPdf.setVisibility(View.GONE);
                    holder.niceVideoPlayer.setVisibility(View.VISIBLE);
                    String videoUrl="http://quickrideja.com/public/user/profile/" + jsonArray.optJSONObject(position).optString("image");
                    holder.niceVideoPlayer.setUp(
                            videoUrl,
                            jsonArray.optJSONObject(position).optString("title"), Jzvd.SCREEN_NORMAL);
                    Glide.with(NotificationTab.this)
                            .load(R.drawable.img_default)
                            .into(holder.niceVideoPlayer.thumbImageView);

                }
                else if (jsonArray.optJSONObject(position).optString("image").contains(".pdf"))
                {
                    holder.niceVideoPlayer.setVisibility(View.GONE);
                    holder.imgPdf.setVisibility(View.VISIBLE);
                    holder.imgNoti.setVisibility(View.GONE);
                    holder.imgPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String pdfLink="http://quickrideja.com/public/user/profile/" + jsonArray.optJSONObject(position).optString("image");
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.parse( pdfLink), "text/html");
                            startActivity(intent);
                        }
                    });

                }
                else if (!jsonArray.optJSONObject(position).isNull("image")
                        && jsonArray.optJSONObject(position).optString("image")!="") {
                    holder.imgNoti.setVisibility(View.VISIBLE);
                    holder.imgPdf.setVisibility(View.GONE);
                    holder.niceVideoPlayer.setVisibility(View.GONE);
                    Picasso.get()
                            .load("http://quickrideja.com/public/user/profile/" + jsonArray.optJSONObject(position).optString("image"))
                            .into(holder.imgNoti);

                    holder.imgNoti.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent =new Intent(NotificationTab.this,FullImage.class);
                            intent.putExtra("title",jsonArray.optJSONObject(position).optString("title"));
                            intent.putExtra("url","http://quickrideja.com/public/user/profile/" + jsonArray.optJSONObject(position).optString("image"));
                            startActivity(intent);
                        }
                    });
                }else {
                    holder.imgPdf.setVisibility(View.GONE);
                    holder.niceVideoPlayer.setVisibility(View.GONE);
                    holder.imgNoti.setVisibility(View.GONE);
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

            TextView txtNotification, txtDateTime;
            ImageView imgNoti,imgPdf;
            JzvdStd niceVideoPlayer;
//            ExoVideoView videoView;
//            SimpleExoPlayerView videoView;


            public MyViewHolder(View itemView) {
                super(itemView);
                niceVideoPlayer = itemView.findViewById(R.id.nice_video_player);
                txtNotification = itemView.findViewById(R.id.txtNotification);
                txtDateTime = itemView.findViewById(R.id.txtDateTime);
                imgNoti = itemView.findViewById(R.id.imgNoti);
                imgPdf = itemView.findViewById(R.id.imgPdf);

//                videoView = itemView.findViewById(R.id.videoView);

            }
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
//        MyLog.i("onActivityPause");
//        mExoPlayerHelper.onActivityPause();
    }




    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        startActivity(new Intent(NotificationTab.this,MainActivity.class));
        finishAffinity();
    }


}
