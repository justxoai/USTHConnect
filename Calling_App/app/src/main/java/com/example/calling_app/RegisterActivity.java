package com.example.calling_app;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.calling_app.Linphone.RegisterFragment;

import org.linphone.core.*;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, email, password;
    private Core core;

//    private AccountManagerServices accountManagerServices;
    private AuthInfo createdAuthInfo;
    private Account createdAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        core = Factory.instance().createCore(null, null, this);


        register_account_browser();

    }

//    private void startAccountCreation() {
//        String user_name = username.getText().toString().trim();
//        String pass_word = password.getText().toString().trim();
//        String email_ = email.getText().toString().trim();
//
//        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(email)) {
//            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
//            return;
//        }
//
//        progressBar.setVisibility(View.VISIBLE);
//
//        AccountManagerServicesRequest request = accountManagerServices.createSendAccountCreationTokenByEmailRequest(email);
//        request.addListener(new AccountManagerServicesRequestListenerStub() {
//            @Override
//            public void onRequestSuccessful(AccountManagerServicesRequest request, String data) {
//                progressBar.setVisibility(View.GONE);
//                handleAccountCreation(username, password, data);
//            }
//
//            @Override
//            public void onRequestError(AccountManagerServicesRequest request, int statusCode, String errorMessage, org.linphone.core.Dictionary parameterErrors) {
//                progressBar.setVisibility(View.GONE);
//                Toast.makeText(getContext(), "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//        request.submit();
//    }
//
//    private void handleAccountCreation(String username, String password, String token) {
//        AccountManagerServicesRequest createRequest = accountManagerServices.createNewAccountUsingTokenRequest(
//                username,
//                password,
//                "SHA-256", // Use a secure hash algorithm for password storage
//                token
//        );
//
//        createRequest.addListener(new AccountManagerServicesRequestListenerStub() {
//            @Override
//            public void onRequestSuccessful(AccountManagerServicesRequest request, String identity) {
//                storeAccount(identity, username, password);
//            }
//
//            @Override
//            public void onRequestError(AccountManagerServicesRequest request, int statusCode, String errorMessage, org.linphone.core.Dictionary parameterErrors) {
//                Toast.makeText(getContext(), "Account creation failed: " + errorMessage, Toast.LENGTH_SHORT).show();
//            }
//        });
//        createRequest.submit();
//    }

//    private void storeAccount(String identity, String username, String password) {
//        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, core.getDefaultAccount());
//        core.addAuthInfo(authInfo);
//
//        createdAuthInfo = authInfo;
//
//        Account account = core.createAccount(core.createAccountParams());
//        account.getParams().setIdentityAddress(Factory.instance().createAddress(identity));
//        core.addAccount(account);
//
//        createdAccount = account;
//
//        Toast.makeText(this, "Account created successfully!", Toast.LENGTH_SHORT).show();
//
//        // Navigate to the login fragment
//        Fragment registerFragment = new RegisterFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(android.R.id.content, registerFragment);
//        transaction.commit();
//    }
//
//
//
//
//
//
//
//
























    private void register_account_browser(){
        TextView registerLink = findViewById(R.id.create_email_account);
        Button navigateToLogin = findViewById(R.id.navigate_to_login_button);

        registerLink.setOnClickListener(view -> {
            String url = getString(R.string.register_here);
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            } catch (Exception e) {
                Log.e("RegisterActivity", "Can't open URL: " + url,e);
            }
        });

        navigateToLogin.setOnClickListener(view -> {
            Fragment registerFragment = new RegisterFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(android.R.id.content, registerFragment);
            transaction.commit();
        });
    }

}