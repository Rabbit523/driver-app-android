package com.rapido.provider.Transportation;

import android.widget.ImageView;

import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.accountkit.ui.UIManager;
import com.rapido.provider.Transportation.Utilities.Utilities;

import org.json.JSONObject;

public class SocialLogin {

    Utilities utils = new Utilities();
    private static final int REQ_SIGN_IN_REQUIRED = 100;
    /*----------Facebook Login---------*/
    CallbackManager callbackManager;
    ImageView backArrow;
    AccessTokenTracker accessTokenTracker;
    String UserName, UserEmail, result, FBUserID, FBImageURLString;
    JSONObject json;

    UIManager uiManager;

}
