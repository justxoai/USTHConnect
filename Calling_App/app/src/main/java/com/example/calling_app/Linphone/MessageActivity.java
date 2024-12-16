package com.example.calling_app.Linphone;

import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.calling_app.R;

import org.linphone.core.Core;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.*;

import org.linphone.core.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class MessageActivity extends AppCompatActivity {

    private Core core;
    private ChatRoom chatRoom;

    private String username = "ducduyvx";
    private String password = "1234567890";

    private String box_chat = "hoaianhngx";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_message);

        Factory factory = Factory.instance();
        factory.setDebugMode(true, "Hello Linphone");
        factory.setLogCollectionPath(getFilesDir().getAbsolutePath());
        factory.enableLogCollection(LogCollectionState.Enabled);

        new File(getFilesDir() + "/linphone.db").delete();
        new File(getFilesDir() + "/x3dh.c25519.sqlite3").delete();
        new File(getFilesDir() + "/zrtp-secrets.db").delete();

        core = factory.createCore(null, null, this);

        login(username, password);


        createFlexisipChatRoom();


        findViewById(R.id.send_message).setOnClickListener(v -> sendMessage());
        findViewById(R.id.send_message).setEnabled(false);

        findViewById(R.id.send_image).setOnClickListener(v -> sendImage());
        findViewById(R.id.send_image).setEnabled(false);
    }

    private final CoreListenerStub coreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            ((TextView) findViewById(R.id.registration_status)).setText(message);
        }

        @Override
        public void onMessageReceived(Core core, ChatRoom chatRoom, ChatMessage message) {
            if (MessageActivity.this.chatRoom == null) {
                if (chatRoom.hasCapability(ChatRoomCapabilities.OneToOne.toInt()) &&
                        chatRoom.hasCapability(ChatRoomCapabilities.Encrypted.toInt())) {
                    MessageActivity.this.chatRoom = chatRoom;
                    chatRoom.addListener(chatRoomListener);
                    enableEphemeral();
                }
            }

            chatRoom.markAsRead();
            addMessageToHistory(message);
        }
    };

    private final ChatRoomListener chatRoomListener = new ChatRoomListenerStub() {
        @Override
        public void onStateChanged(ChatRoom chatRoom, ChatRoom.State newState) {
            if (newState == ChatRoom.State.Created) {
                findViewById(R.id.send_message).setEnabled(true);
                enableEphemeral();
            }
        }

        @Override
        public void onEphemeralEvent(ChatRoom chatRoom, EventLog eventLog) {}

        @Override
        public void onEphemeralMessageDeleted(ChatRoom chatRoom, EventLog eventLog) {
            ChatMessage message = eventLog.getChatMessage();
            View messageView = (View) message.getUserData();
            ((LinearLayout) findViewById(R.id.messages)).removeView(messageView);
        }

        @Override
        public void onEphemeralMessageTimerStarted(ChatRoom chatRoom, EventLog eventLog) {
            ChatMessage message = eventLog.getChatMessage();
            View messageView = (View) message.getUserData();
            if (messageView != null) {
                messageView.setBackgroundColor(getColor(R.color.purple_500));
            }
        }
    };

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
        buttonView.setText("Dow Image/Load Image");

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

    private void createFlexisipChatRoom() {
        ChatRoomParams params = core.createDefaultChatRoomParams();
        params.setBackend(ChatRoomBackend.FlexisipChat);
        params.enableGroup(false);
        params.enableEncryption(true);
        params.setEncryptionBackend(ChatRoomEncryptionBackend.Lime);
        params.setSubject("dummy subject");

        if (params.isValid()) {
            String remoteSipUri = String.format("sip:%s@sip.linphone.org", box_chat);
            Address remoteAddress = Factory.instance().createAddress(remoteSipUri);

            if (remoteAddress != null) {
                Address localAddress = core.getDefaultAccount().getParams().getIdentityAddress();
                ChatRoom room = core.createChatRoom(params, localAddress, new Address[]{remoteAddress});
                if (room != null) {
                    room.addListener(chatRoomListener);
                    chatRoom = room;

                    if (room.getState() == ChatRoom.State.Created) {
                        findViewById(R.id.send_message).setEnabled(true);
                        enableEphemeral();
                    }
                }
            }
        }
    }

    private void enableEphemeral() {
        if (chatRoom != null) {
            chatRoom.enableEphemeral(true);
            chatRoom.setEphemeralLifetime(60);
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
        Content content = Factory.instance().createContent();
        content.setType("image");
        content.setSubtype("png");

        String filePath = getFilesDir().getAbsolutePath() + "/phone.png";
        copy("phone.png", filePath);
        content.setFilePath(filePath);

        ChatMessage chatMessage = chatRoom.createFileTransferMessage(content);
        chatMessage.addListener(chatMessageListener);

        core.setFileTransferServer("https://www.linphone.org:444/lft.php");

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

        params.setConferenceFactoryUri("sip:conference-factory@sip.linphone.org");



        Account account = core.createAccount(params);
        core.addAuthInfo(authInfo);
        core.addAccount(account);

        core.setLimeX3DhServerUrl("https://lime.linphone.org/lime-server/lime-server.php");

        core.setDefaultAccount(account);
        core.addListener(coreListener);
        core.start();

    }
}