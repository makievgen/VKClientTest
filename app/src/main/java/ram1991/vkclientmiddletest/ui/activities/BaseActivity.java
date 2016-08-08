package ram1991.vkclientmiddletest.ui.activities;


import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import ram1991.vkclientmiddletest.R;
import ram1991.vkclientmiddletest.ui.fragments.BaseFragment;

public abstract class BaseActivity extends AppCompatActivity implements BaseFragment.OnFragmentInteractionListener {
    public static final int STATE_ERROR = -1;
    public static final int STATE_NO_NETWORK = 0;
    public static final int STATE_CONNECTED = 1;
    public static final int STATE_SHOW_PROGRESS = 2;
    public static final int STATE_HIDE_PROGRESS = 3;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    protected FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutRes());
        ButterKnife.bind(this);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        initComponents();
        fragmentManager = getSupportFragmentManager();
        createFragment();
    }

    @LayoutRes
    protected abstract void createFragment();

    @LayoutRes
    protected abstract int getLayoutRes();

    protected abstract void initComponents();
}
