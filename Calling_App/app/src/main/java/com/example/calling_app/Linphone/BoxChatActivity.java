package com.example.calling_app.Linphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

    private Core incoming_core;
    private Core outgoing_core;

    private String username;
    private String password;
    private String box_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_box_chat);

        // Incoming Call
        Intent intent = getIntent();
        username = intent.getStringExtra("sip_username");
        password = intent.getStringExtra("sip_password");
        box_chat = intent.getStringExtra("BoxChat_Name");

        TextView textView = findViewById(R.id.box_chat_username);
        textView.setText(box_chat);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone Incoming");
        incoming_core = factory.createCore(null, null, this);

        incoming_login(username, password);

        findViewById(R.id.incoming_hang_up).setEnabled(false);
        findViewById(R.id.incoming_answer).setEnabled(false);
        findViewById(R.id.incoming_mute_mic).setEnabled(false);
        findViewById(R.id.incoming_toggle_speaker).setEnabled(false);

        findViewById(R.id.incoming_hang_up).setOnClickListener(view -> {
            // Terminates the call, whether it is ringing or running
            if (incoming_core.getCurrentCall() != null) {
                incoming_core.getCurrentCall().terminate();
            }
        });

        findViewById(R.id.incoming_answer).setOnClickListener(view -> {
            // if we wanted, we could create a CallParams object
            // and answer using this object to make changes to the call configuration
            // (see OutgoingCall tutorial)
            if (incoming_core.getCurrentCall() != null) {
                incoming_core.getCurrentCall().accept();
            }
        });

        findViewById(R.id.incoming_mute_mic).setOnClickListener(view -> {
            // The following toggles the microphone, disabling completely / enabling the sound capture
            // from the device microphone
            incoming_core.enableMic(!incoming_core.micEnabled());
        });

        findViewById(R.id.incoming_toggle_speaker).setOnClickListener(view -> toggleSpeaker());


        // Outgoing call
        Factory factory2 = Factory.instance();
        factory2.setDebugMode(true, "Hello Linphone Outcoming");
        outgoing_core = factory2.createCore(null, null, this);

        outgoing_login(username, password);

        ImageButton call_button = findViewById(R.id.calling_button);
        call_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                findViewById(R.id.header_layout).setVisibility(View.GONE);
                findViewById(R.id.outgoing_call_layout).setVisibility(View.VISIBLE);

                outgoing_core.setNativeVideoWindowId(findViewById(R.id.remote_video_surface));
                outgoing_core.setNativePreviewWindowId(findViewById(R.id.local_preview_video_surface));
                outgoing_core.enableVideoCapture(true);
                outgoing_core.enableVideoDisplay(true);

                outgoing_core.getVideoActivationPolicy().setAutomaticallyAccept(true);

                outgoingCall();

                ((TextView) findViewById(R.id.outgoing_remote_address)).setText(box_chat);

                findViewById(R.id.outgoing_hang_up).setVisibility(View.VISIBLE);
                findViewById(R.id.outgoing_hang_up).setEnabled(true);

                findViewById(R.id.outgoing_hang_up).setOnClickListener(v -> hangUp());
                findViewById(R.id.outgoing_pause).setOnClickListener(v -> pauseOrResume());
                findViewById(R.id.outgoing_toggle_video).setOnClickListener(v -> toggleVideo());
                findViewById(R.id.outgoing_toggle_camera).setOnClickListener(v -> toggleCamera());

                findViewById(R.id.outgoing_pause).setEnabled(false);
                findViewById(R.id.outgoing_toggle_video).setEnabled(false);
                findViewById(R.id.outgoing_toggle_camera).setEnabled(false);


            }
        });
    }

    // Incoming CoreListenerStub
    private final CoreListenerStub incomingCallCoreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.registration_status)).setText(message);
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
            findViewById(R.id.header_layout).setVisibility(View.GONE);

            // When a call is received
            if (state == Call.State.IncomingReceived) {
                findViewById(R.id.incoming_call_layout).setVisibility(View.VISIBLE);

                findViewById(R.id.incoming_toggle_speaker).setVisibility(View.GONE);
                findViewById(R.id.incoming_mute_mic).setVisibility(View.GONE);

                findViewById(R.id.incoming_hang_up).setVisibility(View.VISIBLE);
                findViewById(R.id.incoming_answer).setVisibility(View.VISIBLE);

                findViewById(R.id.incoming_hang_up).setEnabled(true);
                findViewById(R.id.incoming_answer).setEnabled(true);

                ((TextView) findViewById(R.id.incoming_remote_address)).setText(call.getRemoteAddress().getUsername());
            } else if (state == Call.State.Connected) {
                findViewById(R.id.incoming_mute_mic).setEnabled(true);
                findViewById(R.id.incoming_toggle_speaker).setEnabled(true);

                findViewById(R.id.incoming_toggle_speaker).setVisibility(View.VISIBLE);
                findViewById(R.id.incoming_mute_mic).setVisibility(View.VISIBLE);

            } else if (state == Call.State.Released) {

                findViewById(R.id.incoming_hang_up).setEnabled(false);
                findViewById(R.id.incoming_answer).setEnabled(false);
                findViewById(R.id.incoming_mute_mic).setEnabled(false);
                findViewById(R.id.incoming_toggle_speaker).setEnabled(false);

                ((TextView) findViewById(R.id.incoming_remote_address)).setText("");

                findViewById(R.id.incoming_call_layout).setVisibility(View.GONE);
                findViewById(R.id.header_layout).setVisibility(View.VISIBLE);
            }
        }

    };

    // Incoming toggleSpeaker
    private void toggleSpeaker() {
        // Get the currently used audio device
        AudioDevice currentAudioDevice = incoming_core.getCurrentCall() != null ? incoming_core.getCurrentCall().getOutputAudioDevice() : null;
        boolean speakerEnabled = currentAudioDevice != null && currentAudioDevice.getType() == AudioDevice.Type.Speaker;

        // We can get a list of all available audio devices using
        // Note that on tablets for example, there may be no Earpiece device
        for (AudioDevice audioDevice : incoming_core.getAudioDevices()) {
            if (speakerEnabled && audioDevice.getType() == AudioDevice.Type.Earpiece) {
                if (incoming_core.getCurrentCall() != null) {
                    incoming_core.getCurrentCall().setOutputAudioDevice(audioDevice);
                }
                return;
            } else if (!speakerEnabled && audioDevice.getType() == AudioDevice.Type.Speaker) {
                if (incoming_core.getCurrentCall() != null) {
                    incoming_core.getCurrentCall().setOutputAudioDevice(audioDevice);
                }
                return;
            }
            /* If we wanted to route the audio to a bluetooth headset
            else if (audioDevice.type == AudioDevice.Type.Bluetooth) {
                core.currentCall?.outputAudioDevice = audioDevice
            }*/
        }
    }

    // Outgoing CoreListener
    private final CoreListenerStub outgoingCallCoreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.outgoing_registration_status)).setText(message);

        }

        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {

            if (state == Call.State.OutgoingInit) {
            } else if (state == Call.State.OutgoingProgress) {
            } else if (state == Call.State.OutgoingRinging) {
            } else if (state == Call.State.Connected) {
            } else if (state == Call.State.StreamsRunning) {
                findViewById(R.id.outgoing_hang_up).setVisibility(View.VISIBLE);
                findViewById(R.id.outgoing_hang_up).setEnabled(true);

                findViewById(R.id.outgoing_pause).setVisibility(View.VISIBLE);
                findViewById(R.id.outgoing_pause).setEnabled(true);
                ((Button) findViewById(R.id.outgoing_pause)).setText("Pause");

                findViewById(R.id.outgoing_toggle_video).setVisibility(View.VISIBLE);
                findViewById(R.id.outgoing_toggle_video).setEnabled(true);

                findViewById(R.id.outgoing_toggle_camera).setVisibility(View.VISIBLE);
                findViewById(R.id.outgoing_toggle_camera).setEnabled(
                        core.getVideoDevicesList().length > 2 && call.getCurrentParams().videoEnabled()
                );
            } else if (state == Call.State.Paused) {
                ((Button) findViewById(R.id.outgoing_pause)).setText("Resume");
                findViewById(R.id.outgoing_toggle_video).setEnabled(false);
            } else if (state == Call.State.PausedByRemote) {
            } else if (state == Call.State.Updating) {
            } else if (state == Call.State.UpdatedByRemote) {
            } else if (state == Call.State.Released) {
                ((TextView) findViewById(R.id.outgoing_remote_address)).setText("");

                findViewById(R.id.outgoing_pause).setEnabled(false);
                ((Button) findViewById(R.id.outgoing_pause)).setText("Pause");

                findViewById(R.id.outgoing_toggle_video).setEnabled(false);

                findViewById(R.id.outgoing_hang_up).setEnabled(false);

                findViewById(R.id.outgoing_toggle_camera).setEnabled(false);

                findViewById(R.id.header_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.outgoing_call_layout).setVisibility(View.GONE);

            } else if (state == Call.State.Error) {
            }
        }
    };

    // OutgoingCall
    private void outgoingCall() {
        String remoteSipUri = String.format("sip:%s@sip.linphone.org", box_chat);
        Address remoteAddress = Factory.instance().createAddress(remoteSipUri);
        if (remoteAddress == null) return;

        CallParams params = outgoing_core.createCallParams(null);
        if (params == null) return;

        params.setMediaEncryption(MediaEncryption.None);
        outgoing_core.inviteAddressWithParams(remoteAddress, params);
    }

    // Outgoing Hangup
    private void hangUp() {
        if (outgoing_core.getCallsNb() == 0) return;

        // Get the current call or fallback to the first call in the list
        Call call = outgoing_core.getCurrentCall() != null ? outgoing_core.getCurrentCall() : outgoing_core.getCalls()[0];
        if (call == null) return;

        // Terminate the call
        call.terminate();
    }

    // Outgoing ToggleVideo
    private void toggleVideo() {
        if (outgoing_core.getCallsNb() == 0) return;

        Call call = outgoing_core.getCurrentCall() != null ? outgoing_core.getCurrentCall() : outgoing_core.getCalls()[0];
        if (call == null) return;

        // Check for CAMERA permission
        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
            return;
        }

        // Create new call parameters from the call object
        CallParams params = outgoing_core.createCallParams(call);
        if (params != null) {
            // Toggle video state
            params.enableVideo(!call.getCurrentParams().videoEnabled());
            // Request the call update
            call.update(params);
        }
    }

    // Outgoing toggleCamera
    private void toggleCamera() {
        String currentDevice = outgoing_core.getVideoDevice();

        for (String camera : outgoing_core.getVideoDevicesList()) {
            if (!camera.equals(currentDevice) && !camera.equals("StaticImage: Static picture")) {
                outgoing_core.setVideoDevice(camera);
                break;
            }
        }
    }

    // Outgoing Pause/Resume => Wait
    private void pauseOrResume() {
        if (outgoing_core.getCallsNb() == 0) return;

        Call call = outgoing_core.getCurrentCall() != null ? outgoing_core.getCurrentCall() : outgoing_core.getCalls()[0];
        if (call == null) return;

        // Pause or resume the call based on its state
        if (call.getState() != Call.State.Paused && call.getState() != Call.State.Pausing) {
            call.pause();
        } else if (call.getState() != Call.State.Resuming) {
            call.resume();
        }
    }

    // Incoming Login
    private void incoming_login(String username, String password) {
        String domain = "sip.linphone.org";

        TransportType transportType = TransportType.Tls;

        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);

        AccountParams params = incoming_core.createAccountParams();
        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);
        params.setIdentityAddress(identity);

        Address address = Factory.instance().createAddress("sip:" + domain);
        if (address != null) {
            address.setTransport(transportType);
        }

        params.setServerAddress(address);
        params.setRegisterEnabled(true);

        Account account = incoming_core.createAccount(params);

        incoming_core.addAuthInfo(authInfo);
        incoming_core.addAccount(account);

        incoming_core.setDefaultAccount(account);
        incoming_core.addListener(incomingCallCoreListener);
        incoming_core.start();

        // We will need the RECORD_AUDIO permission for video call
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    // Outgoing Login
    private void outgoing_login(String username, String password) {
        String domain = "sip.linphone.org";

        TransportType transportType = TransportType.Tls;

        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);

        AccountParams params = outgoing_core.createAccountParams();
        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);
        params.setIdentityAddress(identity);

        Address address = Factory.instance().createAddress("sip:" + domain);
        if (address != null) {
            address.setTransport(transportType);
        }

        params.setServerAddress(address);
        params.setRegisterEnabled(true);

        Account account = outgoing_core.createAccount(params);

        outgoing_core.addAuthInfo(authInfo);
        outgoing_core.addAccount(account);

        // Outgoing Video
        outgoing_core.getConfig().setBool("video", "auto_resize_preview_to_keep_ratio", true);

        outgoing_core.setDefaultAccount(account);
        outgoing_core.addListener(outgoingCallCoreListener);
        outgoing_core.start();

        // We will need the RECORD_AUDIO permission for video call
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }
}