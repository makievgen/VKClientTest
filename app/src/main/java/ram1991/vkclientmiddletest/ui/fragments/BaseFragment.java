package ram1991.vkclientmiddletest.ui.fragments;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    private Unbinder mUnBinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayoutId(), container, false);
        mUnBinder = ButterKnife.bind(this, view);
        return view;
    }

    protected abstract void updateUi();

    @LayoutRes
    protected abstract int getLayoutId();

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }

    public interface OnFragmentInteractionListener {

        void onStateShow(int state);

        void onProgressShow(int state);
    }
}
