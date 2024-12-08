package com.example.calling_app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.*;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.linphone.core.Core;
import org.linphone.core.*;

public class OncomingCallActivity extends AppCompatActivity {

    private Core core;
    private String username;
    private String password;
    private String domain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_oncoming_call);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        core = factory.createCore(null, null, this);

        findViewById(R.id.connect).setOnClickListener(view -> {
            login();
            view.setEnabled(false);
        });

//        core.setNativeVideoWindowId(findViewById(R.id.remote_video_surface));
//        core.setNativePreviewWindowId(findViewById(R.id.local_preview_video_surface));
//        core.enableVideoCapture(true);
//        core.enableVideoDisplay(true);
//        core.getVideoActivationPolicy().setAutomaticallyAccept(true);

        findViewById(R.id.call).setOnClickListener(view -> {
            outgoingCall();
            findViewById(R.id.remote_address).setEnabled(false);
            view.setEnabled(false);
            findViewById(R.id.hang_up).setEnabled(true);
        });

        findViewById(R.id.hang_up).setOnClickListener(view -> hangUp());
        findViewById(R.id.pause).setOnClickListener(view -> pauseOrResume());
        findViewById(R.id.toggle_video).setOnClickListener(view -> toggleVideo());
        findViewById(R.id.toggle_camera).setOnClickListener(view -> toggleCamera());

        findViewById(R.id.pause).setEnabled(false);
        findViewById(R.id.toggle_video).setEnabled(false);
        findViewById(R.id.toggle_camera).setEnabled(false);
        findViewById(R.id.hang_up).setEnabled(false);

    }

    private CoreListener coreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.registration_status)).setText(message);

            if (state == RegistrationState.Failed) {
                findViewById(R.id.connect).setEnabled(true);
            } else if (state == RegistrationState.Ok) {
                findViewById(R.id.register_layout).setVisibility(View.GONE);
                findViewById(R.id.call_layout).setVisibility(View.VISIBLE);
            }
        }

        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
            ((TextView) findViewById(R.id.call_status)).setText(message);

            switch (state) {
                case IncomingReceived:
                    Intent intent = new Intent(OncomingCallActivity.this, IncomingCallActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.putExtra("CALL_ID", call.getCallLog().getCallId());
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("domain", domain);
                    intent.putExtra("transport_type", TransportType.Tls);
                    startActivity(intent);
                    break;
                case StreamsRunning:
                    findViewById(R.id.pause).setEnabled(true);
                    ((Button) findViewById(R.id.pause)).setText("Pause");
                    findViewById(R.id.toggle_video).setEnabled(true);

                    findViewById(R.id.toggle_camera).setEnabled(
                            core.getVideoDevicesList().length > 2 && call.getCurrentParams().videoEnabled()
                    );
                    break;
                case Paused:
                    ((Button) findViewById(R.id.pause)).setText("Resume");
                    findViewById(R.id.toggle_video).setEnabled(false);
                    break;
                case Released:
                    findViewById(R.id.remote_address).setEnabled(true);
                    findViewById(R.id.call).setEnabled(true);
                    findViewById(R.id.pause).setEnabled(false);
                    ((Button) findViewById(R.id.pause)).setText("Pause");
                    findViewById(R.id.toggle_video).setEnabled(false);
                    findViewById(R.id.hang_up).setEnabled(false);
                    findViewById(R.id.toggle_camera).setEnabled(false);
                    break;
                default:
                    break;
            }
        }
    };

    private void login() {
        username = ((EditText) findViewById(R.id.username)).getText().toString();
        password = ((EditText) findViewById(R.id.password)).getText().toString();

        domain = "sip.linphone.org";

        TransportType transportType = TransportType.Tls;


        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);
        AccountParams params = core.createAccountParams();
        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);
        params.setIdentityAddress(identity);
        Address address = Factory.instance().createAddress("sip:" + domain);
        address.setTransport(transportType);
        params.setServerAddress(address);
        params.setRegisterEnabled(true);

        Account account = core.createAccount(params);
        core.addAuthInfo(authInfo);
        core.addAccount(account);

        core.getConfig().setBool("video", "auto_resize_preview_to_keep_ratio", true);
        core.setDefaultAccount(account);
        core.addListener(coreListener);
        core.start();

    }

    private void outgoingCall() {
        // Get the SIP URI of the remote and convert it to an Address
        String remoteSipUri = ((EditText) findViewById(R.id.remote_address)).getText().toString();
        Address remoteAddress = Factory.instance().createAddress(remoteSipUri);
        if (remoteAddress == null) return; // If address parsing fails, exit

        // Create CallParams object
        CallParams params = core.createCallParams(null);
        if (params == null) return; // Exit if params creation fails

        // Configure the call parameters
        params.setMediaEncryption(MediaEncryption.None);
        // Uncomment the following line to enable video for the call
        // params.enableVideo(true);

        // Start the outgoing call
        core.inviteAddressWithParams(remoteAddress, params);
        // Call process can be followed in onCallStateChanged callback from core listener
    }

    private void hangUp() {
        if (core.getCallsNb() == 0) return;

        // Get the current call or fallback to the first call in the list
        Call call = core.getCurrentCall() != null ? core.getCurrentCall() : core.getCalls()[0];
        if (call == null) return;

        // Terminate the call
        call.terminate();
    }

    private void toggleVideo() {
        if (core.getCallsNb() == 0) return;

        Call call = core.getCurrentCall() != null ? core.getCurrentCall() : core.getCalls()[0];
        if (call == null) return;

        // Check for CAMERA permission
//        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            requestPermissions(new String[]{Manifest.permission.CAMERA}, 0);
//            return;
//        }

        // Create new call parameters from the call object
        CallParams params = core.createCallParams(call);
        if (params != null) {
            // Toggle video state
            params.enableVideo(!call.getCurrentParams().videoEnabled());
            // Request the call update
            call.update(params);
        }
    }

    private void toggleCamera() {
        // Get the currently used camera
        String currentDevice = core.getVideoDevice();

        // Iterate through available cameras and switch to another one
        for (String camera : core.getVideoDevicesList()) {
            // Avoid using the "Static picture" fake camera
            if (!camera.equals(currentDevice) && !camera.equals("StaticImage: Static picture")) {
                core.setVideoDevice(camera);
                break;
            }
        }
    }

    private void pauseOrResume() {
        if (core.getCallsNb() == 0) return;

        Call call = core.getCurrentCall() != null ? core.getCurrentCall() : core.getCalls()[0];
        if (call == null) return;

        // Pause or resume the call based on its state
        if (call.getState() != Call.State.Paused && call.getState() != Call.State.Pausing) {
            call.pause();
        } else if (call.getState() != Call.State.Resuming) {
            call.resume();
        }
    }

}