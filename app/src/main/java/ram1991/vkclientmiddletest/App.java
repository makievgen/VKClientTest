package ram1991.vkclientmiddletest;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKAccessTokenTracker;
import com.vk.sdk.VKSdk;

import ram1991.vkclientmiddletest.ui.activities.MainActivity;


public class App extends Application {
    public static final int ERROR = -1;
    public static final int NO_NETWORK = 0;
    public static final int CONNECTED = 1;
    public static final int LOGGED_IN = 2;

    @Override
    public void onCreate() {
        super.onCreate();
        vkAccessTokenTracker.startTracking();
        VKSdk.initialize(this);
    }

    @NonNull
    public static int getNetworkConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return CONNECTED;
        }
        return NO_NETWORK;
    }

    VKAccessTokenTracker vkAccessTokenTracker = new VKAccessTokenTracker() {
        @Override
        public void onVKAccessTokenChanged(VKAccessToken oldToken, VKAccessToken newToken) {
            if (newToken == null) {
                Intent intent = MainActivity.getStartIntent(getApplicationContext());
                startActivity(intent);
            }
        }
    };


}
