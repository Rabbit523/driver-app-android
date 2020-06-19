package com.rapido.provider.Transportation.Helper;


public class URLHelper {
    public static final String APP_URL = "http://vps790735.ovh.net/";
    public static final int client_id = 2;
    public static final String client_secret = "WifS1rMi3LvuorP1G2UdtKZairUNSH2iMqrKivPf";

    //    public static final String base = "http://gocab.97pixelsdev.com/";
   // public static String base = "http://m-bebataxi.com/";
    public static String base = "http://vps790735.ovh.net/";
    public static final String REDIRECT_URL = base;
    public static final String STRIPE_TOKEN = "pk_test_LTXZTPA9yepu9dEodKsJm6GA";
//    public static final String APP_URL = "https://play.google.com/store/apps/details?id=com.wedrive.driver";
    public static final String getUserProfileUrl = base + "api/user/details";
    public static final String GET_USERREVIEW = base+"api/provider/review";
    public static final String GET_NOTIFICATIONS= base+"api/provider/notification";
    public static final String UserProfile = base + "api/user/details";
    public static final String UPCOMING_TRIP_DETAILS = base + "api/provider/requests/upcoming/details";
    public static final String UPCOMING_TRIPS = base + "api/provider/requests/upcoming";
    public static final String CANCEL_REQUEST_API = base + "api/provider/cancel";
    public static final String TARGET_API = base + "api/provider/target";
    public static final String RESET_PASSWORD = base + "api/provider/reset/password";
    public static final String FORGET_PASSWORD = base + "api/provider/forgot/password";
    public static final String FACEBOOK_LOGIN = base + "api/provider/auth/facebook";
    public static final String GOOGLE_LOGIN = base + "api/provider/auth/google";
    public static final String LOGOUT = base + "api/provider/logout";
    public static final String SUMMARY = base + "api/provider/summary";
    public static final String HELP = base + "api/provider/help";
    public static final String COMPLAINT = base + "api/user/createComplaint";
    public static final String SAVE_LOCATION = base + "api/user/createDefaultLocation";
    public static final String ADD_COUPON_API = base + "api/user/promocode/add";
    public static final String COUPON_LIST_API = base + "api/user/promocodes";
    public static final String DELETE_CARD_FROM_ACCOUNT_API = base + "api/user/card/destory";
    public static final String CARD_PAYMENT_LIST = base + "api/user/card";
    public static final String GET_WITHDRAW_LIST = base + "api/provider/withdrawaList";

    public static final String GET_CARD_LIST_DETAILS = base + "api/provider/BankList";
    public static final String GET_ADD_BANK_DETAILS = base + "api/provider/addBank?account_name=";
    public static final String WITHDRAW_REQUEST = base + "api/provider/withdrawalRequest?provider_id=";
    public static String HELP_URL = base;
    public static String CALL_PHONE = "1";
    public static String login = base + "api/provider/oauth/token";
    public static String register = base + "api/provider/register";
    public static String email_check = base + "api/provider/check-email";
    public static String mobile_check = base + "api/provider/check-mobile";
    public static String USER_PROFILE_API = base + "api/provider/profile";
    public static String UPDATE_AVAILABILITY_API = base + "api/provider/profile/available";
    public static String GET_HISTORY_API = base + "api/provider/requests/history";
    public static String GET_HISTORY_DETAILS_API = base + "api/provider/requests/history/details";
    public static String CHANGE_PASSWORD_API = base + "api/provider/profile/password";

    public static final String CHECK_DOCUMENT = base + "api/provider/document/checkDocument";
    public static final String COMPLETE_DOCUMENT = base + "api/provider/document/checkDocument?term_n=1";

    public static final  String ChatGetMessage = base + "api/provider/firebase/getChat?request_id=";
    public static final String GET_USER_CHAT_LIST = base + "api/provider/firebase/chatHistory";
}
