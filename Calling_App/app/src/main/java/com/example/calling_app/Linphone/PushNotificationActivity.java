package com.example.calling_app.Linphone;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calling_app.R;

import org.linphone.core.Core;
import org.linphone.core.*;

public class PushNotificationActivity extends AppCompatActivity {

    private Core core;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_push_notification);

        // For push notifications to work, you have to copy your google-services.json in the app/ folder
        // And you must declare our FirebaseMessaging service in the Manifest
        // You also have to make some changes in your build.gradle files, see the ones in this project

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null, null, this);

        // Make sure the core is configured to use push notification token from Firebase
        core.setPushNotificationEnabled(true);

        Button connectButton = findViewById(R.id.connect);
        connectButton.setOnClickListener(v -> {
            login();
            v.setEnabled(false);
        });

    }

    // Core listener
    private final CoreListenerStub coreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {

            TextView registrationStatus = findViewById(R.id.registration_status);
            registrationStatus.setText(message);

            if (state == RegistrationState.Failed) {
                Button connectButton = findViewById(R.id.connect);
                connectButton.setEnabled(true);
            } else if (state == RegistrationState.Ok) {
                LinearLayout registerLayout = findViewById(R.id.register_layout);
                registerLayout.setVisibility(View.GONE);

                // This will display the push information stored in the contact URI parameters
                TextView pushInfo = findViewById(R.id.push_info);
                pushInfo.setText(account.getParams().getContactUriParameters());
            }
        }
    };

    private void login() {
        EditText usernameEditText = findViewById(R.id.username);
        EditText passwordEditText = findViewById(R.id.password);

        String username = usernameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        String domain = "sip.linphone.org";

        TransportType transportType = TransportType.Tls;

        // Create auth info
        Factory factory = Factory.instance();
        AuthInfo authInfo = factory.createAuthInfo(username, null, password, null, null, domain, null);

        // Create account params
        AccountParams params = core.createAccountParams();
        Address identity = factory.createAddress("sip:" + username + "@" + domain);
        params.setIdentityAddress(identity);

        Address address = factory.createAddress("sip:" + domain);
        if (address != null) {
            address.setTransport(transportType);
        }
        params.setServerAddress(address);
        params.setRegisterEnabled(true);

        // Ensure push notification is enabled for this account
        params.setPushNotificationAllowed(true);

        // Add the authentication info and account
        core.addAuthInfo(authInfo);
        Account account = core.createAccount(params);
        core.addAccount(account);

        core.setDefaultAccount(account);
        core.addListener(coreListener);
        core.start();

        if (!core.isPushNotificationAvailable()) {
            Toast.makeText(this, "Something is wrong with the push setup!", Toast.LENGTH_LONG).show();
        }

        // And that's it!
        // You can kill this app and send a message or initiate a call to the identity you registered and you'll see the toast.

        // When a push notification will be received by your app, either:
        // - the Core is alive and it will check it is properly registered & connected to the proxy
        // - the Core isn't available and a broadcast on org.linphone.core.action.PUSH_RECEIVED will be fired

        // Another way is to create your own Application object and create the Core in it
        // This way, when a push will be received, the Core will be created before the push being handled
        // so the first case above will always be true. See our linphone-android app for an example of that.
    }

}