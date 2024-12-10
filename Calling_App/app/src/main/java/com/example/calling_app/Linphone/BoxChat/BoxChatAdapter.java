package com.example.calling_app.Linphone.BoxChat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calling_app.Linphone.BoxChatActivity;
import com.example.calling_app.R;

import java.util.List;

public class BoxChatAdapter extends RecyclerView.Adapter<BoxChatViewHolder>{

    Context context;
    List<BoxChatItem> items;

    public BoxChatAdapter(Context context, List<BoxChatItem> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public BoxChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoxChatViewHolder(LayoutInflater.from(context).inflate(R.layout.boxchat_frame, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoxChatViewHolder holder, int position) {
        BoxChatItem item = items.get(position);

        holder.boxchat_name.setText(item.getName());

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, BoxChatActivity.class);
            i.putExtra("BoxChat_Name", item.getName());
            i.putExtra("sip_username", item.getUsername());
            i.putExtra("sip_password", item.getPassword());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
