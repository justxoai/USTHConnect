package com.example.calling_app.Linphone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.ScrollView;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.calling_app.R;


import org.linphone.core.Core;
import org.linphone.core.*;


import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class BoxChatActivity extends AppCompatActivity {


    private Core incoming_core;
    private Core outgoing_core;


    private Core boxchat_core;
    private ChatRoom chatRoom;


    private String username;
    private String password;


    private String box_chat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_box_chat);


        // Get username, password, boxChat name
        Intent intent = getIntent();
        username = intent.getStringExtra("sip_username");
        password = intent.getStringExtra("sip_password");
        box_chat = intent.getStringExtra("BoxChat_Name");


        // Set name for  boxChat
        TextView textView = findViewById(R.id.box_chat_username);
        textView.setText(box_chat);


        // Create Function & Core:
        // BoxChat Core
        // BoxChat
        Factory boxchat_factory = Factory.instance();
        boxchat_factory.setDebugMode(true, "Hello Linphone BoxChat");
        boxchat_core = boxchat_factory.createCore(null, null, this);


        // Incoming Core
        Factory incoming_factory = Factory.instance();
        incoming_factory.setDebugMode(true, "Hello Linphone Incoming");
        incoming_core = incoming_factory.createCore(null, null, this);


        // Outgoing call
        Factory outgoing_factory = Factory.instance();
        outgoing_factory.setDebugMode(true, "Hello Linphone Outcoming");
        outgoing_core = outgoing_factory.createCore(null, null, this);


        // Login BocChat, Incoming, Outgoing
        login(username, password);


        // Incoming Call Function
        findViewById(R.id.incoming_hang_up).setEnabled(false);
        findViewById(R.id.incoming_answer).setEnabled(false);
        findViewById(R.id.incoming_mute_mic).setEnabled(false);
        findViewById(R.id.incoming_toggle_speaker).setEnabled(false);


        findViewById(R.id.incoming_hang_up).setOnClickListener(view -> {
            if (incoming_core.getCurrentCall() != null) {
                incoming_core.getCurrentCall().terminate();
            }
        });


        findViewById(R.id.incoming_answer).setOnClickListener(view -> {
            if (incoming_core.getCurrentCall() != null) {
                incoming_core.getCurrentCall().accept();
            }
        });


        findViewById(R.id.incoming_mute_mic).setOnClickListener(view -> {
            incoming_core.enableMic(!incoming_core.micEnabled());
        });


        // Outgoing Call Function
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


                // Outgoing Call Function
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


        // BoxChat Function
        findViewById(R.id.send_message).setOnClickListener(v -> sendMessage());
        findViewById(R.id.send_message).setEnabled(false);


        findViewById(R.id.send_image).setOnClickListener(v -> sendImage());
        findViewById(R.id.send_image).setEnabled(false);
    }


    // Incoming CoreListenerStub
    private final CoreListenerStub incomingCallCoreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.registration_status)).setText(message);
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


    // Outgoing CoreListener
    private final CoreListenerStub outgoingCallCoreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.outgoing_registration_status)).setText(message);
        }


        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
            if (state == Call.State.OutgoingInit) {
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
            } else if (state == Call.State.Released) {
                ((TextView) findViewById(R.id.outgoing_remote_address)).setText("");


                findViewById(R.id.outgoing_pause).setEnabled(false);
                ((Button) findViewById(R.id.outgoing_pause)).setText("Pause");


                findViewById(R.id.outgoing_toggle_video).setEnabled(false);


                findViewById(R.id.outgoing_hang_up).setEnabled(false);


                findViewById(R.id.outgoing_toggle_camera).setEnabled(false);


                findViewById(R.id.header_layout).setVisibility(View.VISIBLE);
                findViewById(R.id.outgoing_call_layout).setVisibility(View.GONE);


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


    // Box chat CoreListener
    private final CoreListenerStub BoxChatCoreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.box_chat_registration_status)).setText(message);


            if (state == RegistrationState.Ok) {
                if (chatRoom == null) {
                    createBasicChatRoom();
                }


                findViewById(R.id.send_message).setEnabled(true);


                findViewById(R.id.send_image).setEnabled(true);
            }
        }


        @Override
        public void onMessageReceived(Core core, ChatRoom chatRoom, ChatMessage message) {
            if (BoxChatActivity.this.chatRoom == null) {
                if (chatRoom.hasCapability(ChatRoomCapabilities.Basic.toInt())) {
                    BoxChatActivity.this.chatRoom = chatRoom;
                }
            }


            chatRoom.markAsRead();
            addMessageToHistory(message);
        }
    };


    // Message CoreListener
    private final ChatMessageListenerStub chatMessageListener = new ChatMessageListenerStub() {
        @Override
        public void onMsgStateChanged(ChatMessage message, ChatMessage.State state) {
            View messageView = (View) message.getUserData();
            if (messageView != null) {
                switch (state) {
                    case InProgress:
                        messageView.setBackgroundColor(getColor(R.color.yellow));
                        break;
                    case Delivered:
                        messageView.setBackgroundColor(getColor(R.color.orange));
                        break;
                    case DeliveredToUser:
                        messageView.setBackgroundColor(getColor(R.color.blue));
                        break;
                    case Displayed:
                        messageView.setBackgroundColor(getColor(R.color.green));
                        break;
                    case NotDelivered:
                        messageView.setBackgroundColor(getColor(R.color.red));
                        break;
                    case FileTransferDone:
                        if (!message.isOutgoing()) {
                            LinearLayout messages = findViewById(R.id.messages);
                            messages.removeView(messageView);
                            addMessageToHistory(message);
                        }
                        break;
                }
            }
        }
    };


    private void addMessageToHistory(ChatMessage chatMessage) {
        for (Content content : chatMessage.getContents()) {
            if (content.isText()) {
                addTextMessageToHistory(chatMessage, content);
            } else if (content.isFile()) {
                if (content.getName().endsWith(".jpeg") || content.getName().endsWith(".jpg") || content.getName().endsWith(".png")) {
                    addImageMessageToHistory(chatMessage, content);
                }
            } else if (content.isFileTransfer()) {
                addDownloadButtonToHistory(chatMessage, content);
            }
        }
    }


    private void addTextMessageToHistory(ChatMessage chatMessage, Content content) {
        TextView messageView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = chatMessage.isOutgoing() ? Gravity.RIGHT : Gravity.LEFT;
        messageView.setLayoutParams(layoutParams);
        messageView.setText(content.getUtf8Text());


        if (chatMessage.isOutgoing()) {
            messageView.setBackgroundColor(getColor(R.color.white));
        } else {
            messageView.setBackgroundColor(getColor(R.color.purple_200));
        }


        chatMessage.setUserData(messageView);


        LinearLayout messages = findViewById(R.id.messages);
        messages.addView(messageView);
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_DOWN);
    }


    private void addImageMessageToHistory(ChatMessage chatMessage, Content content) {
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = chatMessage.isOutgoing() ? Gravity.RIGHT : Gravity.LEFT;
        imageView.setLayoutParams(layoutParams);


        imageView.setImageBitmap(BitmapFactory.decodeFile(content.getFilePath()));
        chatMessage.setUserData(imageView);


        LinearLayout messages = findViewById(R.id.messages);
        messages.addView(imageView);
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_DOWN);
    }


    private void addDownloadButtonToHistory(ChatMessage chatMessage, Content content) {
        Button buttonView = new Button(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = chatMessage.isOutgoing() ? Gravity.RIGHT : Gravity.LEFT;
        buttonView.setLayoutParams(layoutParams);
        buttonView.setText("Download Image & Load Image");


        chatMessage.setUserData(buttonView);
        buttonView.setOnClickListener(v -> {
            buttonView.setEnabled(false);
            content.setFilePath(getFilesDir().getAbsolutePath() + "/" + content.getName());
            chatMessage.downloadContent(content);


            if (!chatMessage.isOutgoing()) {
                chatMessage.addListener(chatMessageListener);
            }
        });


        LinearLayout messages = findViewById(R.id.messages);
        messages.addView(buttonView);
        ((ScrollView) findViewById(R.id.scroll)).fullScroll(ScrollView.FOCUS_DOWN);
    }


    private void createBasicChatRoom() {
        ChatRoomParams params = boxchat_core.createDefaultChatRoomParams();
        params.setBackend(ChatRoomBackend.Basic);
        params.enableEncryption(false);
        params.enableGroup(false);


        if (params.isValid()) {
            String remoteSipUri = String.format("sip:%s@sip.linphone.org", box_chat);
            Address remoteAddress = Factory.instance().createAddress(remoteSipUri);


            if (remoteAddress != null) {
                Address localAddress = boxchat_core.getDefaultAccount().getParams().getIdentityAddress();
                ChatRoom room = boxchat_core.createChatRoom(params, localAddress, new Address[]{remoteAddress});
                if (room != null) {
                    chatRoom = room;
                }
            }
        }
    }


    private void sendMessage() {
        String messageText = ((EditText) findViewById(R.id.message)).getText().toString();
        ChatMessage chatMessage = chatRoom.createMessageFromUtf8(messageText);
        chatMessage.addListener(chatMessageListener);


        addMessageToHistory(chatMessage);
        chatMessage.send();


        ((EditText) findViewById(R.id.message)).getText().clear();
    }


    private void sendImage() {
        if (chatRoom == null) {
            createBasicChatRoom();
        }


        Content content = Factory.instance().createContent();
        content.setType("image");
        content.setSubtype("png");


        String filePath = getFilesDir().getAbsolutePath() + "/phone.png";
        copy("phone.png", filePath);
        content.setFilePath(filePath);


        ChatMessage chatMessage = chatRoom.createFileTransferMessage(content);
        chatMessage.addListener(chatMessageListener);


        boxchat_core.setFileTransferServer("https://www.linphone.org:444/lft.php");


        addMessageToHistory(chatMessage);
        chatMessage.send();
    }


    private void copy(String from, String to) {
        File outFile = new File(to);
        if (outFile.exists()) {
            return;
        }


        try (InputStream inFile = getAssets().open(from);
             FileOutputStream outStream = new FileOutputStream(outFile)) {


            byte[] buffer = new byte[1024];
            int length;
            while ((length = inFile.read(buffer)) > 0) {
                outStream.write(buffer, 0, length);
            }
        } catch (Exception e) {
            Log.i("MessageActivity", "Failed to copy file");
        }
    }


    private void box_chat_login(String username, String password) {
        String domain = "sip.linphone.org";


        TransportType transportType = TransportType.Tls;


        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);


        AccountParams params = boxchat_core.createAccountParams();
        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);
        params.setIdentityAddress(identity);


        Address address = Factory.instance().createAddress("sip:" + domain);
        if (address != null) {
            address.setTransport(transportType);
        }
        params.setServerAddress(address);
        params.setRegisterEnabled(true);


        Account account = boxchat_core.createAccount(params);
        boxchat_core.addAuthInfo(authInfo);
        boxchat_core.addAccount(account);


        boxchat_core.setDefaultAccount(account);
        boxchat_core.addListener(BoxChatCoreListener);
        boxchat_core.start();
    }


    private void login(String username, String password) {
        String domain = "sip.linphone.org";
        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);


        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);


        Address address = Factory.instance().createAddress("sip:" + domain);


        //
        AccountParams box_chat_param = boxchat_core.createAccountParams();
        box_chat_param.setIdentityAddress(identity);


        if (address != null) {
            address.setTransport(TransportType.Tls);
        }


        box_chat_param.setServerAddress(address);
        box_chat_param.setRegisterEnabled(true);


        Account box_chat_account = boxchat_core.createAccount(box_chat_param);
        boxchat_core.addAuthInfo(authInfo);
        boxchat_core.addAccount(box_chat_account);


        boxchat_core.setDefaultAccount(box_chat_account);
        boxchat_core.addListener(BoxChatCoreListener);
        boxchat_core.start();


        // Incoming And OutGoing
        AccountParams incoming_param = incoming_core.createAccountParams();
        AccountParams outgoing_param = outgoing_core.createAccountParams();


        incoming_param.setIdentityAddress(identity);
        outgoing_param.setIdentityAddress(identity);


        incoming_param.setServerAddress(address);
        outgoing_param.setServerAddress(address);


        incoming_param.setRegisterEnabled(true);
        outgoing_param.setRegisterEnabled(true);


        Account incoming_account = incoming_core.createAccount(incoming_param);
        incoming_core.addAuthInfo(authInfo);
        incoming_core.addAccount(incoming_account);


        incoming_core.setDefaultAccount(incoming_account);
        incoming_core.addListener(incomingCallCoreListener);
        incoming_core.start();


        Account outgoing_account = outgoing_core.createAccount(outgoing_param);
        outgoing_core.addAuthInfo(authInfo);
        outgoing_core.addAccount(outgoing_account);
        // Outgoing Video
        outgoing_core.getConfig().setBool("video", "auto_resize_preview_to_keep_ratio", true);
        outgoing_core.setDefaultAccount(outgoing_account);
        outgoing_core.addListener(outgoingCallCoreListener);
        outgoing_core.start();


        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }
}
