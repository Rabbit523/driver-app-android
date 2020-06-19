package com.rapido.provider.Transportation.Helper;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.widget.ImageView;


import com.rapido.provider.Transportation.Utilities.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;


public class BitmapWorkerTask extends AsyncTask<Uri, Void, Bitmap> {

    private Activity context;
    private Fragment contextFragment;
    private BitmapCompletion completion;
    private String callerString = null;
    private ProgressDialog progressDialog = null;
    private static final int REQUEST_CODE = 0x11;


    private final WeakReference<ImageView> imageViewReference;
    int orientaion;
    Uri uriText;


    public static final String ADD_REVENUE = "add_revenue";



    private Bitmap result;

    public BitmapWorkerTask(Activity context, ImageView image,
                            String callerString) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.callerString = callerString;
        this.imageViewReference = new WeakReference<ImageView>(image);
    }

    // for fragments call this

    public BitmapWorkerTask(Fragment contextFragment, ImageView image,
                            String callerString, Activity mContext) {
        // TODO Auto-generated constructor stub
        this.context = mContext;
        this.contextFragment = contextFragment;
        this.callerString = callerString;
        this.imageViewReference = new WeakReference<ImageView>(image);
    }

    @Override
    protected void onPreExecute() {
        // TODO Auto-generated method stub
        progressDialog = new ProgressDialog(context);

        progressDialog.show();
        super.onPreExecute();
    }

    @Override
    protected Bitmap doInBackground(Uri... params) {
        // TODO Auto-generated method stub
        Uri data = params[0];
        uriText = data;

        orientaion = Utils.getOrientation(data, context);

        // Bitmap bmp = getBitmap(data);


        return getBitmap(data);

        // return Utils.rotateBitmap(bmp, orientaion);

    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        // TODO Auto-generated method stub
        // super.onPostExecute(bitmap);

        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.contains("Nexus 5") || manufacturer.contains("Nexus 5") || manufacturer.equalsIgnoreCase("samsung")
                || model.contains("SM-G900H")) {

            if (orientaion == 0) {
//                if (Utils.getOrientationfors5(uriText, context) == 90) {
                Matrix matrix = new Matrix();
                matrix.setRotate(90);
                try {
                    Bitmap bmRotated = Bitmap
                            .createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                                    bitmap.getHeight(), matrix, true);
                    bitmap.recycle();
                    bitmap = bmRotated;
                } catch (OutOfMemoryError e) {
                    e.printStackTrace();
                }
            }
//            }

        }

        if (progressDialog != null)
            progressDialog.dismiss();
        if (imageViewReference != null && bitmap != null) {
            final ImageView imageView = imageViewReference.get();
            if (imageView != null) {
                imageView.setImageBitmap(bitmap);
                result = bitmap;
                // post the bitmap to calling activity
//                if (callerString.equals(ADD_REVENUE)) {
//                    completion = (DocumentUpload) context;
//                    completion.onBitmapScaleComplete(bitmap);
//                }


                // ShowDialog(completion, imageView);
            }
        }
    }



    private Bitmap getBitmap(Uri uri) {

        InputStream in = null;
        try {
            final int IMAGE_MAX_SIZE = 800000; // 1.0MP
            in = context.getContentResolver().openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inPreferredConfig = Bitmap.Config.RGB_565;
            o.inDither = true;
            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            while ((o.outWidth * o.outHeight) * (1 / Math.pow(scale, 2)) > IMAGE_MAX_SIZE) {
                scale++;
            }
            Log.d("MYTAG", "scale = " + scale + ", orig-width: "
                    + o.outWidth + ", orig-height: " + o.outHeight);

            Bitmap b = null;
            in = context.getContentResolver().openInputStream(uri);
            if (scale > 1) {
                scale--;
                // scale to max possible inSampleSize that still yields an image
                // larger than target
                o = new BitmapFactory.Options();
                o.inSampleSize = scale;
                b = BitmapFactory.decodeStream(in, null, o);

                // resize to desired dimensions
                int height = b.getHeight();
                int width = b.getWidth();
                Log.d("MYTAG", "1th scale operation dimenions - width: "
                        + width + ",  height: " + height);

                double y = Math.sqrt(IMAGE_MAX_SIZE
                        / (((double) width) / height));
                double x = (y / height) * width;

                Bitmap scaledBitmap = Bitmap.createScaledBitmap(b, (int) x,
                        (int) y, true);
                b.recycle();
                b = scaledBitmap;
                System.gc();
            } else {
                b = BitmapFactory.decodeStream(in);
            }
            in.close();

            Log.d("MYTAG", "bitmap size - width: " + b.getWidth()
                    + ", height: " + b.getHeight());

            return Utils.rotateBitmap(b, orientaion);
        } catch (IOException e) {
            Log.e("MYTAG", e.getMessage(), e);
            return null;
        }

    }

}