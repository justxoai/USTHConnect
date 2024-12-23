package vn.edu.usth.connect.StudyBuddy.Audio;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.usth.connect.R;
import org.linphone.core.*;
public class IncomingActivity extends AppCompatActivity {

    private Core core;

    private String username; // Username of Sip Account
    private String password; // Password of Sip Account

    private String domain;

    private Button hang_up, answer, mute_mic, toogle_speaker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_incoming.xml
        setContentView(R.layout.activity_incoming);

        // Create Factory and Core
        // IncomingCall
        Factory factory = Factory.instance();
        core = factory.createCore(null, null, this);

        // Get username, password and domain
        username = getIntent().getStringExtra("username");
        password = getIntent().getStringExtra("password");
        domain = getIntent().getStringExtra("domain");

        // Check if username, password and domain getExtra not null
        // then login
        if (username != null && !username.isEmpty() &&
                password != null && !password.isEmpty() &&
                domain != null && !domain.isEmpty()) {
            login(username, password, domain);
        }

        // Call ID
        hang_up = findViewById(R.id.incoming_hang_up);
        answer = findViewById(R.id.incoming_answer);
        mute_mic = findViewById(R.id.incoming_mute_mic);
        toogle_speaker = findViewById(R.id.incoming_toggle_speaker);

        // SetEnable
        hang_up.setEnabled(false);
        answer.setEnabled(false);
        mute_mic.setEnabled(false);
        toogle_speaker.setEnabled(false);

        // Setup Function
        hang_up.setOnClickListener(v -> {
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().terminate();
            }
        });

        answer.setOnClickListener(v -> {
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().accept();
            }
        });

        mute_mic.setOnClickListener(v -> {
            core.enableMic(!core.micEnabled());
        });

        toogle_speaker.setOnClickListener(v -> toggleSpeaker());
    }

    // Incoming CoreListener
    private final CoreListenerStub coreListener = new CoreListenerStub() {
        @Override
        public void onAudioDeviceChanged(Core core, AudioDevice audioDevice) {
        }

        // Received a call from another User
        // When connected
        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {

            // Call Received
            if (state != null) {
                switch (state) {
                    case IncomingReceived:
                        // Enable Button
                        hang_up.setEnabled(true);
                        answer.setEnabled(true);

                        // Set TextView
                        TextView contact_name = findViewById(R.id.incoming_remote_address);
                        contact_name.setText(call.getRemoteAddress().getUsername());
                        break;
                    case Connected:
                        // Enable Button
                        mute_mic.setEnabled(true);
                        toogle_speaker.setEnabled(true);

                        answer.setEnabled(false);

                        break;
                    case Released:
                        // Disable Button
                        hang_up.setEnabled(false);
                        mute_mic.setEnabled(false);
                        toogle_speaker.setEnabled(false);

                        // Set TextView
                        TextView contact_name1 = findViewById(R.id.incoming_remote_address);
                        contact_name1.setText("");
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
    }
}