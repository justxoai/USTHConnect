package com.example.calling_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.calling_app.Linphone.MessageActivity;
import com.example.calling_app.Linphone.RegisterFragment;
import com.example.calling_app.Month_Calender.TestingActivity;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        register_fragment();

//        register_account_browser();

//        Intent i = new Intent(MainActivity.this, RegisterActivity.class);
//        startActivity(i);
//
//        Intent i = new Intent(MainActivity.this, MessageActivity.class);
//        startActivity(i);


    }

    private void register_fragment() {
        Fragment registerFragment = new RegisterFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, registerFragment);
        transaction.commit();
    }

    private void register_account_browser(){
        TextView registerLink = findViewById(R.id.create_email_account);
        Button navigateToLogin = findViewById(R.id.navigate_to_login_button);

        registerLink.setOnClickListener(view -> {
            String url = getString(R.string.register_here);
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } catch (Exception e) {
                Log.e(TAG, "Can't open URL: " + url,e);
            }
        });

        navigateToLogin.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OncomingCallActivity.class);
            startActivity(intent);
        });
    }

}