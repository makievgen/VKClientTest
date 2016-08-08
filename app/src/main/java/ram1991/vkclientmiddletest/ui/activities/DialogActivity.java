package ram1991.vkclientmiddletest.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;

import com.vk.sdk.api.model.VKApiDialog;

import butterknife.BindView;
import ram1991.vkclientmiddletest.R;
import ram1991.vkclientmiddletest.ui.fragments.DialogFragment;

public class DialogActivity extends BaseActivity {

    public static final String BUNDLE_DIALOG = "dialog";

    @BindView(R.id.coordinator_dialog)
    CoordinatorLayout coordinator;

    private VKApiDialog mDialog;

    public static Intent getStartIntent(Context context, VKApiDialog dialog) {
        Intent intent = new Intent(context, DialogActivity.class);
        if (dialog != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(BUNDLE_DIALOG, dialog);
            intent.putExtras(bundle);
        }
        return intent;
    }

    @Override
    protected void createFragment() {
        if (fragmentManager.findFragmentById(R.id.fragment_container_dialog) == null) {
            Fragment fragment = DialogFragment.newInstance(mDialog);
            fragmentManager.beginTransaction().add(R.id.fragment_container_dialog, fragment).commit();
        }
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_dialog;
    }

    @Override
    protected void initComponents() {
        mDialog = getIntent().getParcelableExtra(BUNDLE_DIALOG);
    }

    @Override
    public void onStateShow(int state) {
        switch (state) {
            case BaseActivity.STATE_NO_NETWORK:
                Snackbar.make(coordinator, R.string.no_internet, Snackbar.LENGTH_LONG).show();
                break;
            case BaseActivity.STATE_ERROR:
                Snackbar.make(coordinator, R.string.error, Snackbar.LENGTH_LONG).show();
                break;
        }
    }

    @Override
    public void onProgressShow(int state) {

    }
}
