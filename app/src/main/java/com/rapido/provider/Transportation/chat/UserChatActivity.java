package com.rapido.provider.Transportation.chat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import com.rapido.provider.R;
import com.rapido.provider.Transportation.Adapters.ChatAppMsgAdapter;
import com.rapido.provider.Transportation.Bean.ChatAppMsgDTO;
import com.rapido.provider.Transportation.Bean.ChatList;
import com.rapido.provider.Transportation.Helper.ConnectionHelper;
import com.rapido.provider.Transportation.Helper.Ilyft;
import com.rapido.provider.Transportation.Helper.SharedHelper;
import com.rapido.provider.Transportation.Helper.URLHelper;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.rapido.provider.Transportation.Helper.Ilyft.trimMessage;


public class UserChatActivity extends AppCompatActivity {

    public Context context = UserChatActivity.this;
    public Activity activity = UserChatActivity.this;
    List<ChatAppMsgDTO> msgDtoList;
    ChatAppMsgAdapter chatAppMsgAdapter;
    private RecyclerView recyclerChat;
    private TextView lblTitle;
    private String requestId;
    private String providerId;
    private String userName;
//    private CustomDialog customDialog;
    private ConnectionHelper helper;
    private boolean isInternet;
    private ArrayList<ChatList> chatListArrayList;
    private ImageView backArrow;
    private String userID;
    private MyBroadcastReceiver receiver;
    private String messageBackGround;
    private String messageRecieveFrombackground;
    private IntentFilter intentFilter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    ImageView emojiButton;
    EmojiPopup emojiPopup;
    ViewGroup rootView;
    EmojiEditText msgInputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_chat);
        rootView = findViewById(R.id.main_activity_root_view);
        initViews();
        Intent intent = getIntent();
        if (intent != null) {
            if (intent.hasExtra("requestId")) {
                requestId = intent.getExtras().getString("requestId");
                providerId = intent.getExtras().getString("providerId");
                userName = intent.getExtras().getString("userName");
                userID = intent.getExtras().getString("userId");
                if (userName != null) {
                    lblTitle.setText(userName);
                }
            } else {
                messageBackGround = intent.getStringExtra("message");
                if (messageBackGround != null) {
                    Log.e("messsgae", messageBackGround + "messsage");
                    String requestIdBew = intent.getStringExtra("request_id");
                    requestId = requestIdBew;
                    String userNameChat = intent.getStringExtra("userName");
                    lblTitle.setText(userNameChat);
                    //updateView(messageBackGround);// update your textView in the main layout
                }
            }
        }
        intentFilter = new IntentFilter();
        intentFilter.addAction("com.my.app.onMessageReceived");
        receiver = new MyBroadcastReceiver();


    }

    private void initViews() {
        recyclerChat = findViewById(R.id.recyclerChat);
        lblTitle = findViewById(R.id.lblTitle);
        backArrow = findViewById(R.id.backArrow);
        msgInputText = findViewById(R.id.editWriteMessage);
        emojiButton = findViewById(R.id.main_activity_emoji);
        emojiButton.setOnClickListener(ignore -> emojiPopup.toggle());
        // Set RecyclerView layout manager.
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerChat.setLayoutManager(linearLayoutManager);
        // Create the initial data list.
        msgDtoList = new ArrayList<ChatAppMsgDTO>();

        helper = new ConnectionHelper(context);
        isInternet = helper.isConnectingToInternet();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (userName != null) {
            lblTitle.setText(userName);
        }


        mSwipeRefreshLayout = findViewById(R.id.swipeToRefresh);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                msgDtoList.clear();
                getChatDetails();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });





        msgInputText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//                if (Utilities.keyboardShown(msgInputText.getRootView())) {
//                    Log.d("keyboard", "keyboard UP");
//
//                    recyclerChat.scrollToPosition(chatAppMsgAdapter.getItemCount() - 1);
//                } else {
//                    Log.d("keyboard", "keyboard Down");
//                }
            }
        });


    /*    msgInputText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerChat.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        recyclerChat.scrollToPosition(chatAppMsgAdapter.getItemCount() - 1);
                    }
                }, 10);
            }
        });
*/
        ImageButton msgSendButton = findViewById(R.id.btnSend);

        msgSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msgContent = msgInputText.getText().toString();
                if (!TextUtils.isEmpty(msgContent)) {
                    // Add a new sent message to the list.
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = df.format(c.getTime());
                    ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, msgContent, formattedDate + "");
                    msgDtoList.add(msgDto);
                    getChatDetailsp_u(msgContent);
                    int newMsgPosition = msgDtoList.size() - 1;
                    // Notify recycler view insert one new data.
                    try {
                        chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);

                        // Set data adapter to RecyclerView.
                        recyclerChat.setAdapter(chatAppMsgAdapter);
                        recyclerChat.scrollToPosition(chatAppMsgAdapter.getItemCount() - 1);
                        // Notify recycler view insert one new data.
                        chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
                        // Scroll RecyclerView to the last message.
                        recyclerChat.scrollToPosition(newMsgPosition);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // Empty the input edit text box.
                    msgInputText.setText("");
                }
            }
        });

        setUpEmojiPopup();
    }

    String TAG="USERCHATACTIVITY";
    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiBackspaceClickListener(ignore -> Log.d(TAG, "Clicked on Backspace"))
                .setOnEmojiClickListener((ignore, ignore2) -> Log.d(TAG, "Clicked on emoji"))
                .setOnEmojiPopupShownListener(() -> emojiButton.setImageResource(R.drawable.ic_keyboard))
                .setOnSoftKeyboardOpenListener(ignore -> Log.d(TAG, "Opened soft keyboard"))
                .setOnEmojiPopupDismissListener(() -> emojiButton.setImageResource(R.drawable.emoji_ios_category_smileysandpeople))
                .setOnSoftKeyboardCloseListener(() -> Log.d(TAG, "Closed soft keyboard"))
                .setKeyboardAnimationStyle(R.style.emoji_fade_animation_style)
                .setPageTransformer(new PageTransformer())
                .build(msgInputText);
    }
    private void updateView(String message) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, message, formattedDate + "");
        msgDtoList.add(msgDto);
        try {
            int newMsgPosition = msgDtoList.size() - 1;
            // Notify recycler view insert one new data.
            chatAppMsgAdapter.notifyItemInserted(newMsgPosition);
            // Scroll RecyclerView to the last message.
            recyclerChat.scrollToPosition(newMsgPosition);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateViewBackground(String message) {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, message, formattedDate + "");
        msgDtoList.add(msgDto);
        int newMsgPosition = msgDtoList.size() - 1;

        // Notify recycler view insert one new data.
        chatAppMsgAdapter.notifyItemInserted(newMsgPosition);

        // Scroll RecyclerView to the last message.
        recyclerChat.scrollToPosition(newMsgPosition);
    }

    @Override
    public void onResume() {
        super.onResume();
        msgDtoList.clear();
        if (requestId != "" && requestId != null) {
            getChatDetails();
        }
        // put your code here...
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    public void getChatDetails() {
        if (isInternet) {
//            customDialog = new CustomDialog(UserChatActivity.this);
//            customDialog.setCancelable(true);
//            if (customDialog != null)
//                customDialog.show();
            String url;
            if (requestId != "" && requestId != null) {
                url = URLHelper.ChatGetMessage + requestId;
            } else {
                url = URLHelper.ChatGetMessage + userID + "@" + providerId;
            }
            JSONObject object = new JSONObject();
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
//                    if ((customDialog != null) && (customDialog.isShowing()))
//                        customDialog.dismiss();
                    Log.d("TAG", "chatListResponse" + response.toString());
                    if (response.toString() != null) {
                        JSONObject object = null;
                        try {
                            object = new JSONObject(response.toString());
                            String status = object.getString("status");
                            if (status.equalsIgnoreCase("1")) {
                                JSONArray jsonArray = object.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    //   msgDtoList.clear();
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    SharedHelper.putKey(context, "current_chat_provider_id", "" + jo.getString("provider_id"));
                                    SharedHelper.putKey(context, "current_chat_user_id", "" + jo.getString("user_id"));
                                    SharedHelper.putKey(context, "current_chat_request_id", "" + jo.getString("request_id"));
                                    ChatList chatList = new ChatList();
                                    chatList.setProviderId(jo.getString("provider_id"));
                                    chatList.setUserId(jo.getString("user_id"));
                                    chatList.setRequestId(jo.getString("request_id"));
                                    chatList.setMessage(jo.getString("message"));
                                    chatList.setType(jo.getString("type"));
                                    if (!jo.getString("created_at").contains("null")) {
                                        if (jo.getString("type").contains("pu")) {
                                            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_SENT, jo.getString("message"), jo.getString("created_at"));
                                            msgDtoList.add(msgDto);
                                        } else {
                                            ChatAppMsgDTO msgDto = new ChatAppMsgDTO(ChatAppMsgDTO.MSG_TYPE_RECEIVED, jo.getString("message"), jo.getString("created_at"));
                                            msgDtoList.add(msgDto);
                                        }
                                    }
                                    // Create the data adapter with above data list.

                                }
                                chatAppMsgAdapter = new ChatAppMsgAdapter(msgDtoList);

                                // Set data adapter to RecyclerView.
                                recyclerChat.setAdapter(chatAppMsgAdapter);
                                recyclerChat.scrollToPosition(chatAppMsgAdapter.getItemCount() - 1);
                                chatAppMsgAdapter.notifyDataSetChanged();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
//                    if ((customDialog != null) && (customDialog.isShowing()))
//                        customDialog.dismiss();
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
                                try {
                                    if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                        refreshAccessToken();
                                    } else {
                                        displayMessage(errorObj.optString("message"));
                                    }
                                } catch (Exception e) {
                                    displayMessage(getString(R.string.something_went_wrong));
                                }

                            } else if (response.statusCode == 422) {

                                json = trimMessage(new String(response.data));
                                if (json != "" && json != null) {
                                    displayMessage(json);
                                } else {
                                    displayMessage(getString(R.string.please_try_again));
                                }

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
                            getChatDetails();
                        }
                    }
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    headers.put("X-Requested-With", "XMLHttpRequest");
                    headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                    return headers;
                }
            };

            Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);
        } else {
            displayMessage(getString(R.string.something_went_wrong_net));
        }
    }

    public void displayMessage(String toastString) {

        Snackbar.make(findViewById(R.id.lblTitle), toastString, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();
    }

    public void getChatDetailsp_u(String message) {
//        customDialog = new CustomDialog(UserChatActivity.this);
//        customDialog.setCancelable(true);
//        if (customDialog != null)
//            customDialog.show();


        String url;
        if (providerId != null && userID != null) {
            if (requestId != null) {
                url = URLHelper.ChatGetMessage + requestId + "&message=" + message + "&provider_id=" + providerId+ "&user_id=" + Integer.parseInt(userID) + "&type=pu";
            } else {
                url = URLHelper.ChatGetMessage + userID + "@" + providerId + "&message=" + message + "&provider_id=" + providerId + "&user_id=" + Integer.parseInt(userID) + "&type=pu";
            }
        } else {
            url = URLHelper.ChatGetMessage + SharedHelper.getKey(context, "current_chat_request_id") + "&message=" + message + "&provider_id=" + SharedHelper.getKey(context, "current_chat_provider_id") +
                    "&user_id=" + SharedHelper.getKey(context, "current_chat_user_id") + "&type=pu";
        }

            Log.v("chatUrl",url+" ");
        JSONObject object = new JSONObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
//                if ((customDialog != null) && (customDialog.isShowing()))
//                    customDialog.dismiss();

                Log.e("TAG+pu", "chatListResponse+pu" + response.toString());

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("error", error.toString() + "error");
//                if ((customDialog != null) && (customDialog.isShowing()))
//                    customDialog.dismiss();
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
                            try {
                                if (errorObj.optString("message").equalsIgnoreCase("invalid_token")) {
                                    refreshAccessToken();
                                } else {
                                    displayMessage(errorObj.optString("message"));
                                }
                            } catch (Exception e) {
                                displayMessage(getString(R.string.something_went_wrong));
                            }

                        } else if (response.statusCode == 422) {

                            json = trimMessage(new String(response.data));
                            if (json != "" && json != null) {
                                displayMessage(json);
                            } else {
                                displayMessage(getString(R.string.please_try_again));
                            }

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
                        getChatDetails();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(context, "access_token"));
                return headers;
            }
        };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    private void refreshAccessToken() {


        JSONObject object = new JSONObject();
        try {

            object.put("grant_type", "refresh_token");
            object.put("client_id", URLHelper.client_id);
            object.put("client_secret", URLHelper.client_secret);
            object.put("refresh_token", SharedHelper.getKey(context, "refresh_token"));
            object.put("scope", "");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.login, object, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                Log.v("SignUpResponse", response.toString());
                SharedHelper.putKey(context, "access_token", response.optString("access_token"));
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"));
                SharedHelper.putKey(context, "token_type", response.optString("token_type"));
                getChatDetails();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {
                    SharedHelper.putKey(context, "loggedIn", getString(R.string.False));
                    // GoToBeginActivity();
                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        refreshAccessToken();
                    }
                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                return headers;
            }
        };

        Ilyft.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public class MyBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String message = extras.getString("message");
            Log.e("messsgae", message + "messsage");
            if (message != null) {
                updateView(message);// update your textView in the main layout
            }
        }
    }
    @Override protected void onStop() {
        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }

        super.onStop();
    }

}
