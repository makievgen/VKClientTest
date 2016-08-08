package ram1991.vkclientmiddletest.ui.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vk.sdk.api.model.VKApiMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
import ram1991.vkclientmiddletest.R;

public class RecyclerMessagesAdapter extends RecyclerView.Adapter<RecyclerMessagesAdapter.MessagesHolder> {

    private VKApiMessage[] mListOfMessages;

    public RecyclerMessagesAdapter(VKApiMessage[] listOfMessages) {
        mListOfMessages = listOfMessages;
    }

    @Override
    public MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.item_view_message, parent, false);
        return new MessagesHolder(view);
    }

    @Override
    public void onBindViewHolder(MessagesHolder holder, int position) {
        holder.bindDialog(mListOfMessages[position]);
    }

    public void setMessages(VKApiMessage[] listOfMessages) {
        mListOfMessages = listOfMessages;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mListOfMessages.length;
    }

    public class MessagesHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.card_friends_message)
        CardView cardFriendMessage;
        @BindView(R.id.card_your_message)
        CardView cardSenderMessage;
        @BindView(R.id.text_friends_message)
        TextView friendMessage;
        @BindView(R.id.text_your_message)
        TextView senderMessage;

        private VKApiMessage mMessage;

        public MessagesHolder(View v) {
            super(v);
            ButterKnife.bind(this, v);
            v.setClickable(true);
        }

        public void bindDialog(VKApiMessage message) {
            mMessage = message;

            if (mMessage.out) {
                cardFriendMessage.setVisibility(View.GONE);
                cardSenderMessage.setVisibility(View.VISIBLE);
                senderMessage.setText(String.valueOf(mMessage.body));
            } else {
                cardSenderMessage.setVisibility(View.GONE);
                cardFriendMessage.setVisibility(View.VISIBLE);
                friendMessage.setText(String.valueOf(mMessage.body));
            }
        }
    }
}