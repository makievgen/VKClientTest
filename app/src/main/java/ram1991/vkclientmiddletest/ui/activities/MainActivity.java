package ram1991.vkclientmiddletest.ui.activities;


import android.content.Context;
import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import butterknife.BindView;
import ram1991.vkclientmiddletest.R;
import ram1991.vkclientmiddletest.ui.fragments.BaseFragment;
import ram1991.vkclientmiddletest.ui.fragments.MainFragment;

public class MainActivity extends BaseActivity {

    @BindView(R.id.swipe_main)
    SwipeRefreshLayout swipeMain;

    @BindView(R.id.coordinator_main)
    CoordinatorLayout coordinator;


    public static Intent getStartIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }

    @Override
    protected void createFragment() {
        if (fragmentManager.findFragmentById(R.id.fragment_container_main) == null) {
            Fragment fragment = MainFragment.newInstance();
            fragmentManager.beginTransaction().add(R.id.fragment_container_main, fragment).commit();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    protected void initComponents() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                ((MainFragment) getCurrentFragment()).setVKRes(res);
            }

            @Override
            public void onError(VKError error) {
                MainActivity.this.finish();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setSwipeRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        swipeMain.setOnRefreshListener(listener);
    }

    @Override
    public void onStateShow(int state) {
        switch (state) {
            case BaseActivity.STATE_NO_NETWORK:
                Snackbar.make(coordinator, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                break;
            case BaseActivity.STATE_CONNECTED:
                Snackbar.make(coordinator, R.string.success, Snackbar.LENGTH_LONG).show();
                break;
            case BaseActivity.STATE_ERROR:
                Snackbar.make(coordinator, R.string.error, Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onProgressShow(int state) {
        switch (state) {
            case BaseActivity.STATE_SHOW_PROGRESS:
                swipeMain.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeMain.setRefreshing(true);
                    }
                });
                break;
            case BaseActivity.STATE_HIDE_PROGRESS:
                swipeMain.post(new Runnable() {
                    @Override
                    public void run() {
                        swipeMain.setRefreshing(false);
                    }
                });
                break;
        }
    }

    private BaseFragment getCurrentFragment() {
        return (BaseFragment) fragmentManager.findFragmentById(R.id.fragment_container_main);
    }
}
