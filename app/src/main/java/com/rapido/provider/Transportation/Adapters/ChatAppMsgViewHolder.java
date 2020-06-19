package com.rapido.provider.Transportation.Adapters;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.rapido.provider.R;


public class ChatAppMsgViewHolder extends RecyclerView.ViewHolder {

    LinearLayout leftMsgLayout;

    LinearLayout rightMsgLayout;

    TextView leftMsgTextView;

    TextView rightMsgTextView;
    TextView tmLeft;
    TextView tmRight;

    public ChatAppMsgViewHolder(View itemView) {
        super(itemView);

        if (itemView != null) {
            leftMsgLayout = itemView.findViewById(R.id.chat_left_msg_layout);
            rightMsgLayout = itemView.findViewById(R.id.chat_right_msg_layout);
            leftMsgTextView = itemView.findViewById(R.id.chat_left_msg_text_view);
            rightMsgTextView = itemView.findViewById(R.id.chat_right_msg_text_view);
            tmLeft = itemView.findViewById(R.id.tmLeft);
            tmRight = itemView.findViewById(R.id.tmRight);
        }
    }
}

