package ram1991.vkclientmiddletest.ui.fragments;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKApiMessage;

import org.json.JSONArray;
import org.json.JSONException;

import butterknife.BindView;
import butterknife.OnClick;
import ram1991.vkclientmiddletest.R;
import ram1991.vkclientmiddletest.api.ApiFactory;
import ram1991.vkclientmiddletest.api.network.SynchronizeService;
import ram1991.vkclientmiddletest.ui.activities.BaseActivity;
import ram1991.vkclientmiddletest.ui.activities.DialogActivity;
import ram1991.vkclientmiddletest.ui.adapters.RecyclerMessagesAdapter;

public class DialogFragment extends BaseFragment {

    @BindView(R.id.recycler_dialog)
    RecyclerView recyclerDialog;
    @BindView(R.id.button_send_message)
    Button buttonSend;
    @BindView(R.id.edit_message)
    EditText editTextMessage;

    private RecyclerMessagesAdapter mMessagesAdapter;
    private VKApiDialog mDialog;
    private VKApiMessage[] mListOfMessages;
    private SynchronizeService mSynchronizeService;
    private OnFragmentInteractionListener mListener;

    public static DialogFragment newInstance(VKApiDialog dialog) {
        DialogFragment fragment = new DialogFragment();
        if (dialog != null) {
            Bundle bundle = new Bundle();
            bundle.putParcelable(DialogActivity.BUNDLE_DIALOG, dialog);
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mListOfMessages = new VKApiMessage[]{};
        if (getArguments() != null) {
            mDialog = getArguments().getParcelable(DialogActivity.BUNDLE_DIALOG);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        setupPolling();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getHistory();
        setupAdapter();
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

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mSynchronizeService = ((SynchronizeService.SyncBinder) binder).getService();
            mSynchronizeService.startMonitoring(mDialog, new SynchronizeService.OnServiceInteractionListener() {
                @Override
                public void onMessagesReceive(VKApiMessage[] messages) {
                    mListOfMessages = messages;
                    updateUi();
                }
            });
        }

        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void updateUi() {
        if (recyclerDialog != null) {
            mMessagesAdapter.setMessages(mListOfMessages);
            recyclerDialog.scrollToPosition(0);
        }
    }

    @OnClick(R.id.button_send_message)
    public void sendMessage() {
        if (!TextUtils.isEmpty(editTextMessage.getText())) {
            new VKRequest(ApiFactory.REQUEST_SEND,
                    VKParameters.from(VKApiConst.USER_ID,
                            mDialog.message.user_id,
                            VKApiConst.MESSAGE,
                            editTextMessage.getText().toString())).executeWithListener(new VKRequest.VKRequestListener() {
                @Override
                public void onComplete(VKResponse response) {
                    super.onComplete(response);
                    getHistory();
                    editTextMessage.getText().clear();
                    updateUi();
                }

                @Override
                public void onError(VKError error) {
                    super.onError(error);
                    mListener.onStateShow(BaseActivity.STATE_NO_NETWORK);
                }
            });
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unbindService(mServiceConnection);
    }

    private void getHistory() {
        new VKRequest(ApiFactory.REQUEST_GET_HISTORY,
                VKParameters.from(VKApiConst.USER_ID,
                        mDialog.message.user_id)).executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                super.onComplete(response);
                try {
                    JSONArray array = response.json.getJSONObject(ApiFactory.RESPONSE).getJSONArray(ApiFactory.ITEMS);
                    mListOfMessages = new VKApiMessage[array.length()];
                    for (int i = 0; i < array.length(); i++) {
                        VKApiMessage message = new VKApiMessage(array.getJSONObject(i));
                        mListOfMessages[i] = message;
                    }
                    updateUi();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VKError error) {
                super.onError(error);
                mListener.onStateShow(BaseActivity.STATE_NO_NETWORK);
            }
        });

    }

    private void setupPolling() {
        getActivity().bindService(
                SynchronizeService.getStartIntent(getActivity()),
                mServiceConnection,
                Activity.BIND_AUTO_CREATE);
    }

    private void setupAdapter() {
        mMessagesAdapter = new RecyclerMessagesAdapter(mListOfMessages);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        recyclerDialog.setLayoutManager(linearLayoutManager);
        recyclerDialog.setAdapter(mMessagesAdapter);
    }
}
