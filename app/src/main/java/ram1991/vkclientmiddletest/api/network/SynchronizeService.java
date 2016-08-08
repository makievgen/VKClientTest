package ram1991.vkclientmiddletest.api.network;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiMessage;

import org.json.JSONArray;
import org.json.JSONException;

public class SynchronizeService extends Service {
    public static final String ACTION_FILTER = "ram1991.vkclientmiddletest.api.network.SynchronizeService";
    public Handler mHandler = new Handler();
    private SyncBinder mSyncBinder = new SyncBinder();
    private int mDelay = 3000;
    private VKApiDialog mDialog;
    private OnServiceInteractionListener mListener;

    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, SynchronizeService.class);
        intent.setAction(ACTION_FILTER);
        return intent;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mSyncBinder;
    }

    public boolean onUnbind(Intent intent) {
        Log.e("SynchronizeService", " onUnbind");
        mHandler.removeCallbacks(mRunnable);
        return super.onUnbind(intent);
    }

    public void startMonitoring(VKApiDialog dialog, OnServiceInteractionListener listener) {
        mDialog = dialog;
        mListener = listener;
        mHandler.postDelayed(mRunnable, mDelay);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            new VKRequest("messages.getHistory",
                    VKParameters.from(VKApiConst.USER_ID,
                            mDialog.message.user_id)).executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    try {
                        JSONArray array = response.json.getJSONObject("response").getJSONArray("items");
                        final VKApiMessage[] listOfMessages = new VKApiMessage[array.length()];
                        for (int i = 0; i < array.length(); i++) {
                            VKApiMessage message = new VKApiMessage(array.getJSONObject(i));
                            listOfMessages[i] = message;
                        }
                        mListener.onMessagesReceive(listOfMessages);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
            mHandler.postDelayed(this, mDelay);
        }
    };

    public class SyncBinder extends Binder {
        public SynchronizeService getService() {
            return SynchronizeService.this;
        }
    }

    public interface OnServiceInteractionListener {

        void onMessagesReceive(VKApiMessage[] messages);

    }

}
