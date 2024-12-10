package com.example.calling_app.Linphone.BoxChat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calling_app.R;

public class BoxChatViewHolder extends RecyclerView.ViewHolder {

    TextView boxchat_name;

    public BoxChatViewHolder(@NonNull View iteView) {
        super(iteView);

        boxchat_name =iteView.findViewById(R.id.user_boxchat);

    }

}
