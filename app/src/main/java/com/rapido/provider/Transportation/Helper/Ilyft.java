package com.rapido.provider.Transportation.Helper;

import android.app.Application;
import android.content.Context;

import androidx.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.Crashlytics;
import com.rapido.provider.BuildConfig;
import com.rapido.provider.R;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

import io.fabric.sdk.android.Fabric;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;


/**
 * Created by jayakumar on 29/01/17.
 */

public class Ilyft extends Application {

    public static final String TAG = Ilyft.class
            .getSimpleName();

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private static Ilyft mInstance;

    private static Context context;

    public static Context getContext() {
        return context;
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        initCalligraphyConfig();
        EmojiManager.install(new IosEmojiProvider());
//        FontsOverride.setDefaultFont(this, "DEFAULT", "Montserrat-Regular.ttf");
//        FontsOverride.setDefaultFont(this, "DEFAULT", "ClanPro-Book.otf");
//        FontsOverride.setDefaultFont(this, "MONOSPACE", "ClanPro-Book.otf");
//        FontsOverride.setDefaultFont(this, "SERIF", "ClanPro-Book.otf");
//        FontsOverride.setDefaultFont(this, "SANS_SERIF", "ClanPro-Book.otf");

        context = getApplicationContext();
        //MyNotificationOpenedHandler : This will be called when a notification is tapped on.
        //MyNotificationReceivedHandler : This will be called when a notification is received while your app is running.
//        OneSignal.startInit(this)
//                .setNotificationOpenedHandler(new MessageNotificationHandler())
//                .setNotificationReceivedHandler(new MessageNotificationHandler())
//                .init();


        Fabric fabric = new Fabric.Builder(Ilyft.this)
                .kits(new Crashlytics())
                .debuggable(BuildConfig.DEBUG)
                .build();

        Fabric.with(fabric);

    }

    private void initCalligraphyConfig() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath(getResources().getString(R.string.bariol))
                .setFontAttrId(R.attr.fontPath)
                .build()
        );
    }

    public static synchronized Ilyft getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


    public static String trimMessage(String json){
        String trimmedString = "";

        try{
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = jsonObject.getJSONArray(key);
                    for (int i = 0, size = value.length(); i < size; i++) {
                        Log.e("Errors in Form",""+value.getString(i));
                        trimmedString += value.getString(i);
                        if(i < size-1) {
                            trimmedString += '\n';
                        }
                    }
                } catch (JSONException e) {

                    trimmedString += jsonObject.optString(key);
                }
            }
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        Log.e("Trimmed",""+trimmedString);

        return trimmedString;
    }

//    class MessageNotificationHandler implements OneSignal.NotificationOpenedHandler, OneSignal.NotificationReceivedHandler {
//        @Override
//        public void notificationOpened(OSNotificationOpenResult result) {
//            JSONObject data = result.notification.payload.additionalData;
//
//            data.length();
//            Log.d("response: ", data.toString());
//
//            Intent i = new Intent(getApplicationContext(), MainActivity.class);
//            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(i);
//
//        }
//
//        @Override
//        public void notificationReceived(OSNotification notification) {
//            OneSignal.setSubscription(false);
//        }
//    }
}
