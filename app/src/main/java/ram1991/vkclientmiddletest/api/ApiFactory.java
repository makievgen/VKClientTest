package ram1991.vkclientmiddletest.api;

import android.app.Activity;

import com.vk.sdk.VKSdk;

import ram1991.vkclientmiddletest.App;

public class ApiFactory {

    public static final String REQUEST_SEND = "messages.send";
    public static final String REQUEST_GET_HISTORY = "messages.getHistory";
    public static final String RESPONSE = "response";
    public static final String ITEMS = "items";
    public static int getLoginHelper(Activity activity, String[] scope) {
        if (App.getNetworkConnection(activity.getApplicationContext()) == App.CONNECTED) {
            if (!VKSdk.isLoggedIn()) {
                VKSdk.login(activity, scope);
                return App.CONNECTED;
            } else {
                return App.LOGGED_IN;
            }
        } else {
            return App.NO_NETWORK;
        }
    }
}
