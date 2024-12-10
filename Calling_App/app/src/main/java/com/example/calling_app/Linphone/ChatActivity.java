package com.example.calling_app.Linphone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calling_app.Linphone.BoxChat.BoxChatAdapter;
import com.example.calling_app.Linphone.BoxChat.BoxChatItem;
import com.example.calling_app.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        username = intent.getStringExtra("sip_username");
        password = intent.getStringExtra("sip_password");

        RecyclerView recyclerView = findViewById(R.id.chat_recyclerview);

        List<BoxChatItem> items = new ArrayList<>();

        items.add(new BoxChatItem("ducduyvx", username, password));
        items.add(new BoxChatItem("vietanhngx", username, password));
        items.add(new BoxChatItem("quangminhdo", username, password));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        BoxChatAdapter adapter = new BoxChatAdapter(this, items);
        recyclerView.setAdapter(adapter);

    }
}