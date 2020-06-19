//package com.quickridejadriver.provider.Transportation.Service;
//
//import android.graphics.Bitmap;
//import android.graphics.BitmapFactory;
//import androidx.core.app.NotificationCompat;
//import android.util.Log;
//
//import com.quickridejadriver.provider.R;
//import com.quickridejadriver.provider.Transportation.Helper.Ilyft;
//import com.onesignal.NotificationExtenderService;
//import com.onesignal.OSNotificationDisplayedResult;
//import com.onesignal.OSNotificationReceivedResult;
//
//import java.math.BigInteger;
//
///**
// * Created by Admin on 7/12/2018.
// */
//
//public class MyNotificationExtenderService extends NotificationExtenderService {
//    @Override
//    protected boolean onNotificationProcessing(OSNotificationReceivedResult receivedResult) {
//        OverrideSettings overrideSettings = new OverrideSettings();
//        overrideSettings.extender = new NotificationCompat.Extender() {
//            @Override
//            public NotificationCompat.Builder extend(NotificationCompat.Builder builder) {
//                // Sets the background notification color to Red on Android 5.0+ devices.
//                Bitmap icon = BitmapFactory.decodeResource(Ilyft.getContext().getResources(),
//                        R.drawable.logo1);
//                builder.setLargeIcon(icon);
//                return builder.setColor(new BigInteger("FF0000FF", 16).intValue());
//            }
//        };
//
//        OSNotificationDisplayedResult displayedResult = displayNotification(overrideSettings);
//        Log.d("OneSignalExample", "Notification displayed with id: " + displayedResult.androidNotificationId);
//
//        return true;
//    }
//}
