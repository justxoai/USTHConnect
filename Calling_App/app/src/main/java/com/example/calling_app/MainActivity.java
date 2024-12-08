package com.example.calling_app;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.calling_app.Linphone.BoxChatActivity;
import com.example.calling_app.Linphone.PushNotificationActivity;
import com.example.calling_app.Linphone.RegisterFragment;

import org.linphone.core.Core;
import org.linphone.core.Factory;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

//        Intent i = new Intent(MainActivity.this, BoxChatActivity.class);
//        startActivity(i);

        register_fragment();

    }

    private void register_fragment() {
        Fragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, registerFragment);
        transaction.commit();
    }

}