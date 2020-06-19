package com.rapido.provider.Transportation.FCM;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.rapido.provider.Transportation.Activities.MainActivity;

public class MyBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // assumes WordService is a registered service
        Log.v("fireboadcast","firebroadcast");
        Intent launch_intent = new  Intent(context, MainActivity.class);
        launch_intent.setComponent(new ComponentName("com.rapido.provider","com.rapido.provider.Transportation.Activities.MainActivity"));
        launch_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP |Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        launch_intent.addCategory(Intent.CATEGORY_LAUNCHER);
        context.startActivity(launch_intent);
    }
}
