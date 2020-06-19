package com.rapido.provider.Transportation.Utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.util.TypedValue;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.VolleyError;
import com.rapido.provider.R;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    private static final int THUMBSIZE = 400;
    private static final int LENGTH_SHORT = Toast.LENGTH_SHORT;
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private static int mActionBarHeight;
    private static TypedValue mTypedValue = new TypedValue();
    public static final String[] Q = new String[]{"", "K", "M", "G", "T", "P", "E"};

    /**
     * Checking Internet Connectivity both WIFI and Moblie
     */
    public static boolean haveNetworkConnection(Context mContext) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }


        return haveConnectedWifi || haveConnectedMobile;
    }


    public static boolean isConnectingToInternet(Context mContext) {
        ConnectivityManager connectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Network[] networks = connectivityManager.getAllNetworks();
            NetworkInfo networkInfo;
            for (Network mNetwork : networks) {
                networkInfo = connectivityManager.getNetworkInfo(mNetwork);
                if (networkInfo.getState().equals(NetworkInfo.State.CONNECTED)) {
                    return true;
                }
            }
        } else {
            if (connectivityManager != null) {
                //noinspection deprecation
                NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
                if (info != null) {
                    for (NetworkInfo anInfo : info) {
                        if (anInfo.getState() == NetworkInfo.State.CONNECTED) {
                            Log.d("Network",
                                    "NETWORKNAME: " + anInfo.getTypeName());
                            return true;
                        }
                    }
                }
            }
        }
        // Toast.makeText(mContext, mContext.getString(R.string.no_internet_connection), Toast.LENGTH_SHORT).show();
        return false;
    }

    public static void internetError(VolleyError error, Context context) {

        if (error instanceof NoConnectionError) {
            Toast.makeText(context.getApplicationContext(), "No Internet Connections", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If it
     * doesn't, display a dialog that allows users to download the APK from the
     * Google Play Store or enable it in the device's system settings.
     */
//    public static boolean checkPlayServices(Activity act) {
//        int resultCode = GooglePlayServicesUtil
//                .isGooglePlayServicesAvailable(act);
//        if (resultCode != ConnectionResult.SUCCESS) {
//            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
//                GooglePlayServicesUtil.getErrorDialog(resultCode, act,
//                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
//            } else {
//                Utils.ShowAlert("", "No Google Play Application was found",
//                        "OK", act);
//
//            }
//            return false;
//        }
//        return true;
//    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */

    public static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;//versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    public static float pixelsToSp(Context context, float px) {
        float scaledDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return px / scaledDensity;
    }


    /**
     * Hide keyboard
     *
     * @param ctx
     * @param et
     */
    public static void hideKeyboardEdit(Context ctx, EditText et) {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Service.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et.getWindowToken(), 0);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
    }

    /**
     * Open Keyboard
     *
     * @param ctx
     */

    public static void openKeyboardEdit(Context ctx) {
        InputMethodManager imm = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }


    /**
     * To read the respose of http post
     *
     * @param is
     * @return
     */
    public static String stringFromInputStream(InputStream is) {

        if (is == null) {
            return null;

        }
        try {
            byte[] bytes = new byte[1024];

            StringBuilder x = new StringBuilder();

            int numRead = 0;
            while ((numRead = is.read(bytes)) >= 0) {
                x.append(new String(bytes, 0, numRead));
            }

            return x.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Show Alert Dialog
     *
     * @param title
     * @param msg
     * @param btnTitle
     * @param mContext
     */
    public static void ShowAlert(String title, String msg, String btnTitle,
                                 Context mContext) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(true).setTitle(title).setMessage(msg)
                .setNeutralButton(btnTitle, new OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        dialog.dismiss();


                    }
                });
        Dialog dialog = builder.create();
        dialog.show();

    }

    /**
     * Show Toast
     *
     * @param mContext
     * @param msg
     * @return
     */

    public static Toast SHOW_TOAST(Context mContext, String msg) {
        Toast toast = Toast.makeText(mContext, msg, LENGTH_SHORT);
        //   toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
        return toast;
    }

    /**
     * Read data from raw
     *
     * @param file : Location of file in rawFile
     * @return
     */
    public static String mReadJsonData(int file, Context ctx) {
        String mResponse = "";
        try {
            InputStream is = ctx.getResources().openRawResource(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            mResponse = new String(buffer);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mResponse;
    }

    /**
     * GPS ON OFF
     *
     * @param ctx
     * @return
     */
    public static boolean isGPSOn(Context ctx) {
        final LocationManager manager = (LocationManager) ctx
                .getSystemService(Context.LOCATION_SERVICE);

        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * Mak a json
     *
     * @param key
     * @param value
     * @return
     */
    public static String makeJson(String[] key, String[] value) {
        JSONObject obj = new JSONObject();
        for (int i = 0; i < key.length; i++) {
            try {
                obj.put(key[i], value[i]);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return obj.toString();
    }

    /**
     * set the desired font true if italic
     *
     * @param tv
     * @param fontName
     * @param mContext
     * @param italic
     */
    public static void setFont(TextView tv, String fontName, Context mContext,
                               boolean italic) {
        Typeface tf = Typeface.createFromAsset(mContext.getAssets(), fontName);
        if (italic)
            tv.setTypeface(tf, Typeface.ITALIC);
        else
            tv.setTypeface(tf);
    }

    /**
     * get the thumbnail of image
     *
     * @param uri
     * @param mContext
     * @return
     */
    public static Bitmap GetThumbNail(Uri uri, Context mContext) {
        return MediaStore.Images.Thumbnails.getThumbnail(
                mContext.getContentResolver(), ContentUris.parseId(uri),
                MediaStore.Images.Thumbnails.FULL_SCREEN_KIND,
                null);
    }

    /**
     * get the thumbnail of image
     *
     * @return
     */
    public static Bitmap GetThumbNailCamera(Bitmap bmp) {
        return ThumbnailUtils.extractThumbnail(bmp, THUMBSIZE, THUMBSIZE);
    }

    /**
     * Email validation
     *
     * @param target
     * @return boolean result
     */
    public final static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.EMAIL_ADDRESS.matcher(target)
                    .matches();
        }
    }

    /**
     * WebUrl validation
     *
     * @param target
     * @return boolean result
     */
    public final static boolean isValidUrl(CharSequence target) {
        if (TextUtils.isEmpty(target)) {
            return false;
        } else {
            return Patterns.WEB_URL.matcher(target).matches();
        }
    }

    /**
     * converter
     *
     * @param is
     * @param os
     */
    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    /**
     * get index from any set
     *
     * @param set
     * @param value
     * @return
     */
    public static int getIndex(HashSet<? extends Object> set, Object value) {
        int result = 0;
        for (Object entry : set) {
            if (entry.equals(value))
                return result;
            result++;
        }
        return -1;
    }

    /**
     * CONVERT BITMAP TO BYTE ARRAY
     *
     * @param bmp
     * @return
     */

    public static byte[] convertBitmapToByte(Bitmap bmp) {
//        String b64;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//        byte[] b = baos.toByteArray();


        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);
        byte[] byteArray = stream.toByteArray();


        return byteArray;
    }

    /**
     * Convert image to b64
     *
     * @param bmp
     * @return
     */
    public static String convertImage2Base64(Bitmap bmp) {
        String b64;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        byte[] b = baos.toByteArray();
        b64 = android.util.Base64
                .encodeToString(b, android.util.Base64.DEFAULT);

        return b64;
    }

    /**
     * Custom hack to fix ListView Length
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = MeasureSpec.makeMeasureSpec(listView.getWidth(),
                MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth,
                        LayoutParams.WRAP_CONTENT));

            view.setLayoutParams(new ViewGroup.LayoutParams(0, 0));

            view.measure(desiredWidth, MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    /**
     * Set custom Height Listview Hack 2
     *
     * @param myListView
     */
    public static void getListViewSize(ListView myListView) {
        ListAdapter myListAdapter = myListView.getAdapter();
        if (myListAdapter == null) {
            // do nothing return null
            return;
        }
        // set listAdapter in loop for getting final size
        int totalHeight = 0;
        for (int size = 0; size < myListAdapter.getCount(); size++) {
            View listItem = myListAdapter.getView(size, null, myListView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        // setting listview item in adapter
        ViewGroup.LayoutParams params = myListView.getLayoutParams();
        params.height = totalHeight
                + (myListView.getDividerHeight() * (myListAdapter.getCount() - 1));
        myListView.setLayoutParams(params);
        // print height of adapter on log
        Log.i("height of listItem:", String.valueOf(totalHeight));
    }

    /**
     * Load bitmap from URL
     *
     * @param URL
     * @param options
     * @return
     */
    public static Bitmap loadBitmapFromURL(String URL,
                                           BitmapFactory.Options options) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {


            in = OpenHttpConnectionForUrl(URL);
            bitmap = BitmapFactory.decodeStream(in, null, options);
            if (in != null)
                in.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {

        }
        return bitmap;

        // return bitmap;
    }

    private static InputStream OpenHttpConnectionForUrl(String strURL)
            throws IOException {
        InputStream inputStream = null;
        URL url = new URL(strURL);
        URLConnection conn = url.openConnection();

        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setRequestMethod("GET");
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = httpConn.getInputStream();
            }
        } catch (Exception ex) {
        }
        return inputStream;
    }

    /**
     * get fb bitmap
     *
     * @param userID
     * @return
     */
    public static Bitmap getFacebookProfilePicture(String userID) {
        URL imageURL;
        Bitmap bitmap = null;
        try {
            imageURL = new URL("http://graph.facebook.com/" + userID
                    + "/picture?type=large");
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection()
                    .getInputStream());
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return bitmap;
    }

    /**
     * String fb id, String size of the image "square small normal large"
     *
     * @param fbId
     * @param size
     * @return
     */
    public static String getFacebookImageUrl(String fbId, String size) {
        /*
         * square small normal large
         */
        return "https://graph.facebook.com/" + fbId + "/picture?type=" + size;
    }

    /**
     * Call Alert
     *
     * @param msg
     * @param tel
     * @param ctx
     */
    public static void CallAlert(String msg, final String tel, final Context ctx)
            throws ActivityNotFoundException {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setTitle("Call");
        builder.setMessage(msg);
        builder.setPositiveButton("Call", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse(tel));
                // ctx.startActivity(intent);

            }
        });
        builder.setNegativeButton("Cancel", new OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        Dialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Get full path from URI
     *
     * @param context
     * @param contentUri
     * @return
     */
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    public static int getActionBarHeight(Context mContext) {
        if (mActionBarHeight != 0) {
            return mActionBarHeight;
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize,
                    mTypedValue, true);
        } else {
            mContext.getTheme().resolveAttribute(android.R.attr.actionBarSize,
                    mTypedValue, true);
        }

        mActionBarHeight = TypedValue.complexToDimensionPixelSize(
                mTypedValue.data, mContext.getResources().getDisplayMetrics());

        return mActionBarHeight;
    }

    /**
     * Get screenshot from an imageview
     *
     * @param view
     * @return
     */
    public static Bitmap screenShot(View view) {

        view.setDrawingCacheEnabled(true);
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bmp = Bitmap.createBitmap(view.getDrawingCache());

        view.setDrawingCacheEnabled(false);

        return bmp;
    }

    public static Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(canvas);
        else
            canvas.drawColor(Color.WHITE);
        view.draw(canvas);
        return returnedBitmap;
    }

    /**
     * Hockey App Integration
     */
    public static void checkForCrashes(Context mContext) {
        //CrashManager.register(mContext, Constant.HOCKEY_APP_ID);
    }

    public static void checkForUpdates(Activity activity) {
        // Remove this for store builds!
        //UpdateManager.register(activity, Constant.HOCKEY_APP_ID);
    }

    /**
     * Get Strings from a particular regex in a List
     *
     * @param text
     * @param ptn
     * @return
     */
    public static List<String> captureValuesFromPattern(String text, Pattern ptn) {
        Matcher mtch = ptn.matcher(text);
        List<String> ips = new ArrayList<String>();
        while (mtch.find()) {
            ips.add(mtch.group().trim());
        }
        return ips;
    }

    /**
     * Set Margins Programatically
     *
     * @param v
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargins(View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v
                    .getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }


    public static int getOrientationfors5(Uri imageUri, Activity act) {
        String[] columns = {MediaStore.Images.Media.DATA, MediaStore.Images.Media.ORIENTATION};

        Cursor cursor = act.getContentResolver().query(imageUri, columns, null, null, null);


        if (cursor == null) {

            return 0;
        }

        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(columns[0]);
        int orientationColumnIndex = cursor.getColumnIndex(columns[0]);


//        String filePath = cursor.getString(columnIndex);
        int orientation = cursor.getInt(orientationColumnIndex);

        Log.d("tag", "got image orientation " + orientation);

        return orientation;
    }

    /**
     * get Image orientation from URI
     *
     * @param selectedImage
     * @param context
     * @return
     */
    public static int getOrientation(Uri selectedImage, Context context) {
        int orientation = 0;

        int rotationInDegrees = 0;
        ExifInterface ei;
        try {
            ei = new ExifInterface(selectedImage.getPath());
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            rotationInDegrees = exifToDegrees(orientation);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return orientation;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static Bitmap rotateBitmap(Bitmap bitmap, int orientation) {

        Matrix matrix = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                return bitmap;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                matrix.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                matrix.setRotate(180);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                matrix.setRotate(90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                matrix.setRotate(-90);
                matrix.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(-90);
                break;
            default:
                return bitmap;
        }
        try {
            Bitmap bmRotated = Bitmap.createBitmap(bitmap, 0, 0,
                    bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            bitmap.recycle();
            return bmRotated;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Get the rotation of the last image added.
     *
     * @param context
     * @param selectedImage
     * @return
     */
    public static int getRotation(Context context, Uri selectedImage) {
        int rotation = 0;
        ContentResolver content = context.getContentResolver();

        Cursor mediaCursor = content.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new String[]{
                        "orientation", "date_added"}, null, null,
                "date_added desc");

        if (mediaCursor != null && mediaCursor.getCount() != 0) {
            while (mediaCursor.moveToNext()) {
                rotation = mediaCursor.getInt(0);
                break;
            }
        }
        mediaCursor.close();
        return rotation;
    }

    public static int getSquareCropDimensionForBitmap(Bitmap bitmap) {
        int dimension = 0;
        // If the bitmap is wider than it is tall
        // use the height as the square crop dimension
        if (bitmap.getWidth() >= bitmap.getHeight()) {
            dimension = bitmap.getHeight();
        }
        // If the bitmap is taller than it is wide
        // use the width as the square crop dimension
        else {
            dimension = bitmap.getWidth();
        }

        return dimension;
    }


    public static Bitmap fastblur(Bitmap sentBitmap, int radius) {


        Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

        if (radius < 1) {
            //return (null);
        }

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int[] pix = new int[w * h];
        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.getPixels(pix, 0, w, 0, 0, w, h);

        int wm = w - 1;
        int hm = h - 1;
        int wh = w * h;
        int div = radius + radius + 1;

        int r[] = new int[wh];
        int g[] = new int[wh];
        int b[] = new int[wh];
        int rsum, gsum, bsum, x, y, i, p, yp, yi, yw;
        int vmin[] = new int[Math.max(w, h)];

        int divsum = (div + 1) >> 1;
        divsum *= divsum;
        int dv[] = new int[256 * divsum];
        for (i = 0; i < 256 * divsum; i++) {
            dv[i] = (i / divsum);
        }

        yw = yi = 0;

        int[][] stack = new int[div][3];
        int stackpointer;
        int stackstart;
        int[] sir;
        int rbs;
        int r1 = radius + 1;
        int routsum, goutsum, boutsum;
        int rinsum, ginsum, binsum;

        for (y = 0; y < h; y++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            for (i = -radius; i <= radius; i++) {
                p = pix[yi + Math.min(wm, Math.max(i, 0))];
                sir = stack[i + radius];
                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);
                rbs = r1 - Math.abs(i);
                rsum += sir[0] * rbs;
                gsum += sir[1] * rbs;
                bsum += sir[2] * rbs;
                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }
            }
            stackpointer = radius;

            for (x = 0; x < w; x++) {

                r[yi] = dv[rsum];
                g[yi] = dv[gsum];
                b[yi] = dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (y == 0) {
                    vmin[x] = Math.min(x + radius + 1, wm);
                }
                p = pix[yw + vmin[x]];

                sir[0] = (p & 0xff0000) >> 16;
                sir[1] = (p & 0x00ff00) >> 8;
                sir[2] = (p & 0x0000ff);

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[(stackpointer) % div];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi++;
            }
            yw += w;
        }
        for (x = 0; x < w; x++) {
            rinsum = ginsum = binsum = routsum = goutsum = boutsum = rsum = gsum = bsum = 0;
            yp = -radius * w;
            for (i = -radius; i <= radius; i++) {
                yi = Math.max(0, yp) + x;

                sir = stack[i + radius];

                sir[0] = r[yi];
                sir[1] = g[yi];
                sir[2] = b[yi];

                rbs = r1 - Math.abs(i);

                rsum += r[yi] * rbs;
                gsum += g[yi] * rbs;
                bsum += b[yi] * rbs;

                if (i > 0) {
                    rinsum += sir[0];
                    ginsum += sir[1];
                    binsum += sir[2];
                } else {
                    routsum += sir[0];
                    goutsum += sir[1];
                    boutsum += sir[2];
                }

                if (i < hm) {
                    yp += w;
                }
            }
            yi = x;
            stackpointer = radius;
            for (y = 0; y < h; y++) {
                // Preserve alpha channel: ( 0xff000000 & pix[yi] )
                pix[yi] = (0xff000000 & pix[yi]) | (dv[rsum] << 16)
                        | (dv[gsum] << 8) | dv[bsum];

                rsum -= routsum;
                gsum -= goutsum;
                bsum -= boutsum;

                stackstart = stackpointer - radius + div;
                sir = stack[stackstart % div];

                routsum -= sir[0];
                goutsum -= sir[1];
                boutsum -= sir[2];

                if (x == 0) {
                    vmin[y] = Math.min(y + r1, hm) * w;
                }
                p = x + vmin[y];

                sir[0] = r[p];
                sir[1] = g[p];
                sir[2] = b[p];

                rinsum += sir[0];
                ginsum += sir[1];
                binsum += sir[2];

                rsum += rinsum;
                gsum += ginsum;
                bsum += binsum;

                stackpointer = (stackpointer + 1) % div;
                sir = stack[stackpointer];

                routsum += sir[0];
                goutsum += sir[1];
                boutsum += sir[2];

                rinsum -= sir[0];
                ginsum -= sir[1];
                binsum -= sir[2];

                yi += w;
            }
        }

        Log.e("pix", w + " " + h + " " + pix.length);
        bitmap.setPixels(pix, 0, w, 0, 0, w, h);


        return bitmap;
        /*topLay.setBackground(new BitmapDrawable(
                getResources(), bitmap));*/

    }

    public static int getSize(int size) {

        int hightDaimention = 0;

        if (size > 2) {
            hightDaimention = 0;
        } else if (size == 2) {
            hightDaimention = 2;
        } else if (size == 1) {
            hightDaimention = 1;
        } else if (size == 0) {
            hightDaimention = 0;
        }

        return hightDaimention;
    }

    public static View getView(Activity act, int size) {
        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        int DEVICE_HEIGHT = metrics.heightPixels;
        int hightDaimention = 0;

        switch (size) {

            case 0:
                hightDaimention = 0;
                break;
            case 1:
                hightDaimention = 350;
                break;
            case 2:
                hightDaimention = 200;
                break;


        }

        // add footer if listview has less number of item by un-commenting below


        View footer = new View(act);
        AbsListView.LayoutParams lpq = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, (DEVICE_HEIGHT / 2 + hightDaimention));

        footer.setLayoutParams(lpq);

        return footer;
    }


    public static String getPhoneFormat(String s) {

        String val = s;
        String a = "";
        String b = "";
        String c = "";
        if (val != null && val.length() > 0) {
            val = val.replace("-", "");
            if (val.length() >= 3) {
                a = val.substring(0, 3);
            } else if (val.length() < 3) {
                a = val;
            }
            if (val.length() >= 6) {
                b = val.substring(3, 6);
                c = val.substring(6, val.length());
            } else if (val.length() > 3 && val.length() < 6) {
                b = val.substring(3, val.length());
            }
            StringBuffer stringBuffer = new StringBuffer();
            if (a != null && a.length() > 0) {
                stringBuffer.append(a);
                if (a.length() == 3) {
                    stringBuffer.append("-");
                }
            }
            if (b != null && b.length() > 0) {
                stringBuffer.append(b);
                if (b.length() == 3) {
                    stringBuffer.append("-");
                }
            }
            if (c != null && c.length() > 0) {
                stringBuffer.append(c);

                s = stringBuffer.toString();
            }


        }
        return s;
    }

    public static View getViewFull(Activity act) {

        DisplayMetrics metrics = new DisplayMetrics();
        act.getWindowManager().getDefaultDisplay()
                .getMetrics(metrics);
        int DEVICE_HEIGHT = metrics.heightPixels;

        // add footer if listview has less number of item by un-commenting below


        // lines
        View footer = new View(act);
        AbsListView.LayoutParams lpq = new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT, DEVICE_HEIGHT);
        footer.setLayoutParams(lpq);

        return footer;
    }


    // A method to find height of the status bar
    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static void addStatusBarColor(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(activity.getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return model;
    }


    /**
     * Turn drawable resource into byte array.
     *
     * @param context parent context
     * @param id      drawable resource id
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, int id) {
        Drawable drawable = ContextCompat.getDrawable(context, id);
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    /**
     * Turn drawable into byte array.
     *
     * @param drawable data
     * @return byte array
     */
    public static byte[] getFileDataFromDrawable(Context context, Drawable drawable) {
        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }


    /**
     * Check if the supplied context can render PDF files via some installed application that reacts to a intent
     * with the pdf mime type and viewing action.
     *
     * @param context
     * @return
     */
    public static boolean canDisplayPdf(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/pdf");
        return packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


    /**
     * Check if the supplied context can render msword files via some installed application that reacts to a intent
     * with the msword mime type and viewing action.
     *
     * @param context
     * @return
     */
    public static boolean canDisplayWord(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent testIntent = new Intent(Intent.ACTION_VIEW);
        testIntent.setType("application/msword");
        return packageManager.queryIntentActivities(testIntent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }


    public static String getagpsAddress(Activity activity, double latitude,
                                        double longitude) {
        String completeAddress = "";

        if (activity != null) {
            Geocoder geocoder = new Geocoder(activity, Locale.ENGLISH);


            try {
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null) {
                    Address returnedAddress = addresses.get(0);
                    StringBuilder strReturnedAddress = new StringBuilder("Address:\n");
                    for (int i = 0; i < returnedAddress.getMaxAddressLineIndex(); i++) {
                        strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                    }

                    String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    completeAddress = address + ", " + city; //city + ", " + state;

                } else {
                    //Toast.makeText(activity, "No Address returned!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                // Toast.makeText(activity, "No Address returned!", Toast.LENGTH_LONG).show();

            }

        }
        return completeAddress;
    }


    public static boolean punchInDelayed(String date) {
        try {
            SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // British format
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            date = dateFormat.format(source.parse(date)); //h:mm
            if (dateFormat.parse(date).after(dateFormat.parse("10:30"))) {
                System.out.println("PunuchIn time is Not OK After 10.30");
                return true;
            } else {
                System.out.println("PunuchIn time is OK Before 10.30");
                return false;
            }
        } catch (Exception er) {
            er.printStackTrace();
        }
        return false;
    }

    public static boolean punchOutBefore(String date) {
        try {
            SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // British format
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            date = dateFormat.format(source.parse(date)); //h:mm
            if (dateFormat.parse(date).before(dateFormat.parse("18:30"))) {
                System.out.println("Current time is less than 06.30");
                return true;
            } else {
                System.out.println("Current time is greater than 06.30");
                return false;
            }
        } catch (Exception er) {
            er.printStackTrace();
        }
        return false;
    }


    //intent.setDataAndType(uri, "application/msword");

    /**
     * This method is used to Add 30 days to current date
     *
     * @return
     */

    public static String getAdd30Days() {

        Calendar c = new GregorianCalendar();
        c.add(Calendar.DATE, 30);
        Date d = c.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String ourformat = formatter.format(d.getTime());
        return ourformat;
    }


    /**
     * This methos is used to get date with specific formate , as you want...
     *
     * @return
     */

    public static String getCurrentDateSpecificDate() {

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date();
        String ourformat = formatter.format(date.getTime());

        return ourformat;
    }

    /**
     * @param datee : datee is string formate
     * @return
     */

    public static String getDataSpecificFormate(String datee) {
        //new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //2016/11/16 12:08:43

        DateFormat dateFormat = new SimpleDateFormat(datee);
        Date date = new Date();
        datee = dateFormat.format(date);

        return datee;
    }


    /**
     * @param datee : date is string formate
     * @return
     */

    public static String getDataSpecificFormate2(String datee) {


        SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat target = new SimpleDateFormat("dd-MM-yyyy");

        try {
            datee = target.format(source.parse(datee));
        } catch (Exception px) {
            px.printStackTrace();
        }
        return datee;
    }

    public static String getTime(String date) {

        try {
            SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  // British format
            SimpleDateFormat target = new SimpleDateFormat("HH:mm");
            date = target.format(source.parse(date)); //h:mm

        } catch (Exception px) {
            px.printStackTrace();
        }

        System.out.println(date);
        return date;
    }


    public static String getChangedDate(String date) {

//
//        SimpleDateFormat source = new SimpleDateFormat("dd/MM/YYYY");  // British format
//        SimpleDateFormat target = new SimpleDateFormat("YYYYMMdd");
//
//        String oldDate = "06/12/2012";
//        String newDate = target.format(source.parse(oldDate));
        //MM/dd/yyyy

        try {
            SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd");  // British format
            SimpleDateFormat target = new SimpleDateFormat("MM/dd/yyyy");
            date = target.format(source.parse(date));

        } catch (Exception px) {
            px.printStackTrace();
        }

        System.out.println(date);
        return date;
    }


    /**
     * this method is return size in MB/kb formate
     * byte array to readable size
     *
     * @param bytes
     * @return
     */

    public static String getAsString(long bytes) {
        for (int i = 6; i > 0; i--) {
            double step = Math.pow(1024, i);
            if (bytes > step) {
                Log.v("your image size id:", String.format("%3.1f %s", bytes / step, Q[i]));
                return String.format("%3.1f %s", bytes / step, Q[i]);
            }
        }


        return Long.toString(bytes);
    }


    public static boolean compareDatesByCompareTo(DateFormat df, Date oldDate, Date newDate, Activity act, String message) {
        //how to check if date1 is equal to date2
        if (oldDate.compareTo(newDate) == 0) {
            System.out.println(df.format(oldDate) + " and " + df.format(newDate) + " are equal to each other");
            return true;
        }

        //checking if date1 is less than date 2
        if (oldDate.compareTo(newDate) < 0) {

            System.out.println(df.format(oldDate) + " is less than " + df.format(newDate));
            return true;
        }

        //how to check if date1 is greater than date2 in java
        if (oldDate.compareTo(newDate) > 0) {

            System.out.println(df.format(oldDate) + " is greater than " + df.format(newDate));

            Toast.makeText(act, message, Toast.LENGTH_SHORT).show();

            return false;

        }
        return true;
    }


    public static String formatDate(int year, int month, int day) {

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(0);
        cal.set(year, month, day);
        Date date = cal.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        return sdf.format(date);
    }


    public static String diffrenceBtwDate(String start, String end) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");//("MM/dd/yyyy");

        Date d1 = null;
        Date d2 = null;
        long diffDays = 0;

        try {
            d1 = format.parse(start);
            d2 = format.parse(end);
            long diff = d2.getTime() - d1.getTime();
            diffDays = diff / (24 * 60 * 60 * 1000);
            System.out.print(diffDays + " days, ");
        } catch (Exception e) {
            e.printStackTrace();
        }


        return diffDays + 1 + " days";
    }

    public static String getTimestamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    public static String sanitizePhoneNumber(String phone) {

        if (phone.equals("")) {
            return "";
        }

        if (phone.length() < 11 & phone.startsWith("0")) {
            String p = phone.replaceFirst("^0", "254");
            return p;
        }
        if (phone.length() == 13 && phone.startsWith("+")) {
            String p = phone.replaceFirst("^+", "");
            return p;
        }
        return phone;
    }

    public static String getPassword(String businessShortCode, String passkey, String timestamp) {
        String str = businessShortCode + passkey + timestamp;
        //encode the password to Base64
        return Base64.encodeToString(str.getBytes(), Base64.NO_WRAP);
    }

}
