package com.rapido.provider.Transportation.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.GeolocationPermissions;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.rapido.provider.R;

public class TrackActivity extends AppCompatActivity {


    /**
     * WebViewClient subclass loads all hyperlinks in the existing WebView
     */
    public class GeoWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
// When user clicks a hyperlink, load in the existing WebView
            view.loadUrl(url);
            return true;
        }
    }

    /**
     * WebChromeClient subclass handles UI-related calls
     * Note: think chrome as in decoration, not the Chrome browser
     */
    public class GeoWebChromeClient extends WebChromeClient {
        @Override
        public void onGeolocationPermissionsShowPrompt(String origin,
                                                       GeolocationPermissions.Callback callback) {
// Always grant permission since the app itself requires location
// permission and the user has therefore already granted it
            callback.invoke(origin, true, false);
        }
    }
        String url;
    private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);
        myWebView = findViewById(R.id.webView);
        url=getIntent().getStringExtra("address");
//        url="<iframe src="+url+"></iframe>";
        Log.e("frameurl",url+"url");
        WebView myWebView = findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        myWebView.setWebViewClient(new GeoWebViewClient());

        webSettings.setJavaScriptEnabled(true);
        myWebView.getSettings().setGeolocationEnabled(true);
        CookieManager.getInstance().setAcceptCookie(true);
//webSettings.setDomStorageEnabled(true);
        myWebView.clearHistory();
        myWebView.clearFormData();
        myWebView.clearCache(true);

        myWebView.setWebViewClient(new WebViewClient(){

            @Override
            public void onPageFinished(WebView view, String url)
            {

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {


                if( url.startsWith("http:") || url.startsWith("https:") ) {
                    return false;
                }

                if(url.contains("http://maps.google.com/maps"))
                {
                    Uri gmmIntentUri = Uri.parse(url);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    if (mapIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(mapIntent);
                    }
                    return true;
                }


                // Otherwise allow the OS to handle things like tel, mailto, etc.
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity( intent );
                return true;
            }
        });

        myWebView.loadUrl(url);


//THIS IS TO DOWNLOAD THE PDF FILES OR OTHER DOWNLOAD LINKS
        myWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimetype,
                                        long contentLength) {

                startActivity(new Intent(Intent.ACTION_VIEW,  Uri.parse(url)));
            }
        });
    }
    @Override
    public void onBackPressed() {
        if (myWebView.canGoBack()) {
            myWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

}
