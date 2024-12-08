package com.example.calling_app;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.widget.*;
import org.linphone.core.*;

public class IncomingCallActivity extends AppCompatActivity {

    private Core core;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_incoming_call);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null, null, this);

        String username = getIntent().getStringExtra("username");
        String password = getIntent().getStringExtra("password");
        String domain = getIntent().getStringExtra("domain");

        if (username != null && !username.isEmpty() &&
                password != null && !password.isEmpty() &&
                domain != null && !domain.isEmpty()) {
            login(username, password, domain);
        }

        findViewById(R.id.hang_up).setEnabled(false);
        findViewById(R.id.answer).setEnabled(false);
        findViewById(R.id.mute_mic).setEnabled(false);
        findViewById(R.id.toggle_speaker).setEnabled(false);
        findViewById(R.id.remote_address).setEnabled(false);

        findViewById(R.id.hang_up).setOnClickListener(v -> {
            // Terminates the call, whether it is ringing or running
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().terminate();
            }
        });

        findViewById(R.id.answer).setOnClickListener(v -> {
            // Answer the call
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().accept();
            }
        });

        findViewById(R.id.mute_mic).setOnClickListener(v -> {
            // Toggle the microphone
            core.enableMic(!core.micEnabled());
        });

        findViewById(R.id.toggle_speaker).setOnClickListener(v -> toggleSpeaker());
    }

    private final CoreListenerStub coreListener = new CoreListenerStub() {
        @Override
        public void onAudioDeviceChanged(Core core, AudioDevice audioDevice) {
            // This callback will be triggered when a successful audio device has been changed
        }

        @Override
        public void onAudioDevicesListUpdated(Core core) {
            // This callback will be triggered when the available devices list has changed,
            // for example after a Bluetooth headset has been connected/disconnected.
        }

        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
            ((TextView) findViewById(R.id.call_status)).setText(message);

            // When a call is received
            if (state != null) {
                switch (state) {
                    case IncomingReceived:
                        findViewById(R.id.hang_up).setEnabled(true);
                        findViewById(R.id.answer).setEnabled(true);
                        ((EditText) findViewById(R.id.remote_address)).setText(call.getRemoteAddress().asStringUriOnly());
                        break;
                    case Connected:
                        findViewById(R.id.mute_mic).setEnabled(true);
                        findViewById(R.id.toggle_speaker).setEnabled(true);
                        break;
                    case Released:
                        findViewById(R.id.hang_up).setEnabled(false);
                        findViewById(R.id.answer).setEnabled(false);
                        findViewById(R.id.mute_mic).setEnabled(false);
                        findViewById(R.id.toggle_speaker).setEnabled(false);
                        ((EditText) findViewById(R.id.remote_address)).getText().clear();
                        break;
                }
            }
        }
    };

    private void toggleSpeaker() {
        Call currentCall = core.getCurrentCall();
//        if (currentCall == null) return;

        AudioDevice currentAudioDevice = currentCall.getOutputAudioDevice();
        boolean speakerEnabled = currentAudioDevice != null && currentAudioDevice.getType() == AudioDevice.Type.Speaker;

        for (AudioDevice audioDevice : core.getAudioDevices()) {
            if (speakerEnabled && audioDevice.getType() == AudioDevice.Type.Earpiece) {
                currentCall.setOutputAudioDevice(audioDevice);
                return;
            } else if (!speakerEnabled && audioDevice.getType() == AudioDevice.Type.Speaker) {
                currentCall.setOutputAudioDevice(audioDevice);
                return;
            }
            // If you wanted to route the audio to a Bluetooth headset:
            // else if (audioDevice.getType() == AudioDevice.Type.Bluetooth) {
            //     currentCall.setOutputAudioDevice(audioDevice);
            // }
        }
    }

    private void login(String username, String password, String domain) {
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

        // Request RECORD_AUDIO permission for audio calls
//        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
//        }
    }
}