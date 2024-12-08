package com.example.calling_app.Linphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calling_app.R;

import org.linphone.core.Core;
import org.linphone.core.*;


public class BoxChatActivity extends AppCompatActivity {

    private Core core;

    private String username;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_box_chat);

        Intent intent = getIntent();
        username = intent.getStringExtra("sip_username");
        password = intent.getStringExtra("sip_password");

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null, null, this);

        login(username, password);

        findViewById(R.id.hang_up).setEnabled(false);
        findViewById(R.id.answer).setEnabled(false);
        findViewById(R.id.mute_mic).setEnabled(false);
        findViewById(R.id.toggle_speaker).setEnabled(false);
        findViewById(R.id.remote_address).setEnabled(false);

        findViewById(R.id.hang_up).setOnClickListener(view -> {
            // Terminates the call, whether it is ringing or running
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().terminate();
            }
        });

        findViewById(R.id.answer).setOnClickListener(view -> {
            // if we wanted, we could create a CallParams object
            // and answer using this object to make changes to the call configuration
            // (see OutgoingCall tutorial)
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().accept();
            }
        });

        findViewById(R.id.mute_mic).setOnClickListener(view -> {
            // The following toggles the microphone, disabling completely / enabling the sound capture
            // from the device microphone
            core.enableMic(!core.micEnabled());
        });

        findViewById(R.id.toggle_speaker).setOnClickListener(view -> toggleSpeaker());

    }

    private final CoreListenerStub coreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.registration_status)).setText(message);

            if (state == RegistrationState.Failed) {
                findViewById(R.id.connect).setEnabled(true);
            } else if (state == RegistrationState.Ok) {
//                findViewById(R.id.call_layout).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onAudioDeviceChanged(Core core, AudioDevice audioDevice) {
            // This callback will be triggered when a successful audio device has been changed
        }

        @Override
        public void onAudioDevicesListUpdated(Core core) {
            // This callback will be triggered when the available devices list has changed,
            // for example after a bluetooth headset has been connected/disconnected.
        }

        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
            ((TextView) findViewById(R.id.call_status)).setText(message);
            findViewById(R.id.call_layout).setVisibility(View.VISIBLE);
            // When a call is received
            if (state == Call.State.IncomingReceived) {
                findViewById(R.id.hang_up).setEnabled(true);
                findViewById(R.id.answer).setEnabled(true);
                ((EditText) findViewById(R.id.remote_address)).setText(call.getRemoteAddress().asStringUriOnly());
            } else if (state == Call.State.Connected) {
                findViewById(R.id.mute_mic).setEnabled(true);
                findViewById(R.id.toggle_speaker).setEnabled(true);
            } else if (state == Call.State.Released) {
                findViewById(R.id.hang_up).setEnabled(false);
                findViewById(R.id.answer).setEnabled(false);
                findViewById(R.id.mute_mic).setEnabled(false);
                findViewById(R.id.toggle_speaker).setEnabled(false);
                ((EditText) findViewById(R.id.remote_address)).getText().clear();
            }
        }
    };


    private void toggleSpeaker() {
        // Get the currently used audio device
        AudioDevice currentAudioDevice = core.getCurrentCall() != null ? core.getCurrentCall().getOutputAudioDevice() : null;
        boolean speakerEnabled = currentAudioDevice != null && currentAudioDevice.getType() == AudioDevice.Type.Speaker;

        // We can get a list of all available audio devices using
        // Note that on tablets for example, there may be no Earpiece device
        for (AudioDevice audioDevice : core.getAudioDevices()) {
            if (speakerEnabled && audioDevice.getType() == AudioDevice.Type.Earpiece) {
                if (core.getCurrentCall() != null) {
                    core.getCurrentCall().setOutputAudioDevice(audioDevice);
                }
                return;
            } else if (!speakerEnabled && audioDevice.getType() == AudioDevice.Type.Speaker) {
                if (core.getCurrentCall() != null) {
                    core.getCurrentCall().setOutputAudioDevice(audioDevice);
                }
                return;
            }
            /* If we wanted to route the audio to a bluetooth headset
            else if (audioDevice.type == AudioDevice.Type.Bluetooth) {
                core.currentCall?.outputAudioDevice = audioDevice
            }*/
        }
    }

    private void login(String username, String password) {
        String domain = "sip.linphone.org";

        TransportType transportType = TransportType.Tls;

        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);

        AccountParams params = core.createAccountParams();
        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);
        params.setIdentityAddress(identity);

        Address address = Factory.instance().createAddress("sip:" + domain);
        if (address != null) {
            address.setTransport(transportType);
        }
        params.setServerAddress(address);
        params.setRegisterEnabled(true);

        Account account = core.createAccount(params);

        core.addAuthInfo(authInfo);
        core.addAccount(account);

        core.setDefaultAccount(account);
        core.addListener(coreListener);
        core.start();

        // We will need the RECORD_AUDIO permission for video call
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }
}