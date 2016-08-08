package ram1991.vkclientmiddletest.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiDialog;
import com.vk.sdk.api.model.VKList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ram1991.vkclientmiddletest.R;
import ram1991.vkclientmiddletest.util.DateFormatter;

public class RecyclerDialogsAdapter extends RecyclerView.Adapter<RecyclerDialogsAdapter.DialogsHolder> {
    private VKList mVKList;
    private VKList<VKApiDialog> mDialogs;
    private Callbacks mCallbacks;

    public RecyclerDialogsAdapter(VKList vkList, VKList<VKApiDialog> dialogs, Callbacks callbacks) {
        this.mVKList = vkList;
        this.mDialogs = dialogs;
        this.mCallbacks = callbacks;
    }

    @Override
    public DialogsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_view_dialog, parent, false);
        return new DialogsHolder(view);
    }

    @Override
    public void onBindViewHolder(DialogsHolder holder, int position) {
        holder.bindDialog(mVKList, mDialogs.get(position));
    }

    public void setDialogs(VKList vkList, VKList<VKApiDialog> dialogs, Callbacks callbacks) {
        this.mVKList = vkList;
        this.mDialogs = dialogs;
        this.mCallbacks = callbacks;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDialogs.size();
    }

    public class DialogsHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_icon)
        ImageView icon;
        @BindView(R.id.text_friend)
        TextView nameOfFriend;
        @BindView(R.id.text_last_date)
        TextView date;
        @BindView(R.id.text_message)
        TextView message;
        private VKList mVKList;
        private VKApiDialog mDialog;

        public DialogsHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setClickable(true);
        }

        public void bindDialog(VKList vkList, VKApiDialog dialog) {
            mVKList = vkList;
            mDialog = dialog;
            nameOfFriend.setText(String.valueOf(mVKList.getById(mDialog.message.user_id)));
            date.setText(DateFormatter.timeMillisToString(mDialog.message.date));
            message.setText(String.valueOf(mDialog.message.body));
        }

        @OnClick(R.id.grid_dialog)
        public void onClick() {
            mCallbacks.onDialogSelected(mDialog);
        }
    }

    public interface Callbacks {
        void onDialogSelected(VKApiDialog dialog);
    }
}

