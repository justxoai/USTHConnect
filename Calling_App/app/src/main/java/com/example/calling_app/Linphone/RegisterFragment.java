package com.example.calling_app.Linphone;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.calling_app.R;

import org.linphone.core.Account;
import org.linphone.core.Core;
import org.linphone.core.*;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.RegistrationState;
import org.linphone.core.tools.Log;

public class RegisterFragment extends Fragment {

    private Core core;

    private String username;
    private String password;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_register, container, false);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null, null, requireContext());

        // Make sure the core is configured to use push notification token from Firebase
        core.setPushNotificationEnabled(true);

        v.findViewById(R.id.connect).setOnClickListener(view -> {
            login();
            view.setEnabled(false);
        });

        return v;
    }
    // Create a Core listener to listen for the callback we need
    // In this case, we want to know about the account login status
    private final CoreListenerStub coreListenerStub = new CoreListenerStub(){
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            // If account has been configured correctly, we will go through Progress and Ok states
            // Otherwise, we will be Failed.
//            TextView registrationStatus = getView().findViewById(R.id.registration_status);
//            registrationStatus.setText(message);

            // if setence: state == RegistrationState.Failed || state == RegistrationState.Cleared
            if (state == RegistrationState.Failed) {
                getView().findViewById(R.id.connect).setEnabled(true);
            } else if (state == RegistrationState.Ok) {
                Intent i = new Intent(requireContext(), ChatActivity.class);

                i.putExtra("sip_username", username);
                i.putExtra("sip_password", password);

                startActivity(i);
            }
        }
    };

    private void login() {
        username = ((EditText) getView().findViewById(R.id.username)).getText().toString();
        password = ((EditText) getView().findViewById(R.id.password)).getText().toString();
        String domain = "sip.linphone.org";

        // Get the transport protocol to use.
        // TLS is strongly recommended
        // Only use UDP if you don't have the choice
        // I change it to TLS, no more UDP or TCP :D
        TransportType transportType = TransportType.Tls;

        // To configure a SIP account, we need an Account object and an AuthInfo object
        // The first one is how to connect to the proxy server, the second one stores the credentials

        // The auth info can be created from the Factory as it's only a data class
        // userID is set to null as it's the same as the username in our case
        // ha1 is set to null as we are using the clear text password. Upon first register, the hash will be computed automatically.
        // The realm will be determined automatically from the first register, as well as the algorithm

        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);

        // Account object replaces deprecated ProxyConfig object
        // Account object is configured through an AccountParams object that we can obtain from the Core
        AccountParams accountParams = core.createAccountParams();

        // A SIP account is identified by an identity address that we can construct from the username and domain
        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);
        accountParams.setIdentityAddress(identity);

        // We also need to configure where the proxy server is located
        Address address = Factory.instance().createAddress("sip:" + domain);

        // We use the Address object to easily set the transport protocol
        if (address != null) {
            address.setTransport(transportType);
        }
        accountParams.setServerAddress(address);

        // And we ensure the account will start the registration process
        accountParams.setRegisterEnabled(true);

        // Now that our AccountParams is configured, we can create the Account object
        Account account = core.createAccount(accountParams);

        // Now let's add our objects to the Core
        core.addAuthInfo(authInfo);
        core.addAccount(account);

        // Also set the newly added account as default
        core.setDefaultAccount(account);

        // To be notified of the connection status of our account, we need to add the listener to the Core
        core.addListener(coreListenerStub);

        // We can also register a callback on the Account object
        account.addListener((acct, state, message) ->
                // There is a Log helper in org.linphone.core.tools package
                // How about using Toast (?)
                Log.i("[Account] Registration state changed: " + state + ", " + message)
        );

        // Finally we need the Core to be started for the registration to happen (it could have been started before)
        core.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (core != null) {
            core.removeListener(coreListenerStub);
        }
    }


}