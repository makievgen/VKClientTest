package ram1991.vkclientmiddletest.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKScope;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiGetDialogResponse;
import com.vk.sdk.api.model.VKList;

import java.util.ArrayList;

import butterknife.BindView;
import ram1991.vkclientmiddletest.App;
import ram1991.vkclientmiddletest.R;
import ram1991.vkclientmiddletest.api.ApiFactory;
import ram1991.vkclientmiddletest.ui.activities.BaseActivity;
import ram1991.vkclientmiddletest.ui.activities.DialogActivity;
import ram1991.vkclientmiddletest.ui.activities.MainActivity;
import ram1991.vkclientmiddletest.ui.adapters.RecyclerDialogsAdapter;


public class MainFragment extends BaseFragment implements RecyclerDialogsAdapter.Callbacks {

    @BindView(R.id.recycler_main)
    RecyclerView recyclerMain;

    private String[] scope = new String[]{VKScope.MESSAGES, VKScope.FRIENDS};
    private VKList mVKList;
    private VKList<VKApiDialog> mDialogs;
    private ArrayList<String> mUsersId;
    private OnFragmentInteractionListener mListener;

    private RecyclerDialogsAdapter mDialogsAdapter;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mVKList = new VKList<>();
        mDialogs = new VKList<>();
    }

    @Override
    protected void updateUi() {
        mDialogsAdapter.setDialogs(mVKList, mDialogs, this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        ((MainActivity) getActivity())
                .setSwipeRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        startLoading();
                    }
                });

        setupAdapter();
        startLoading();
    }


    private void startLoading() {
        mListener.onProgressShow(BaseActivity.STATE_HIDE_PROGRESS);
        switch (ApiFactory.getLoginHelper(getActivity(), scope)) {
            case App.LOGGED_IN:
                setVKRes(null);
                break;
            case App.NO_NETWORK:
                mListener.onStateShow(BaseActivity.STATE_NO_NETWORK);
                break;
            case App.ERROR:
                mListener.onStateShow(BaseActivity.STATE_ERROR);
                break;
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void setVKRes(VKAccessToken res) {
        VKApi.messages().getDialogs(VKParameters.from(VKApiConst.COUNT, 50))
                .executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        VKApiGetDialogResponse dialogResponse = (VKApiGetDialogResponse) response.parsedModel;
                        mDialogs = dialogResponse.items;
                        mUsersId = new ArrayList<>(mDialogs.size());
                        for (int i = 0; i < mDialogs.size(); i++) {
                            mUsersId.add(String.valueOf(mDialogs.get(i).message.user_id));
                        }
                    }
                });
        VKApi.users().get(VKParameters.from(VKApiConst.USER_IDS, mUsersId))
                .executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        mVKList = (VKList) response.parsedModel;
                        updateUi();
                    }
                });
    }

    private void setupAdapter() {
        mDialogsAdapter = new RecyclerDialogsAdapter(mVKList, mDialogs, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerMain.setLayoutManager(linearLayoutManager);
        recyclerMain.setAdapter(mDialogsAdapter);
    }

    @Override
    public void onDialogSelected(VKApiDialog dialog) {
        startActivity(DialogActivity.getStartIntent(getActivity(), dialog));
    }
}
