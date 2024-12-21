package vn.edu.usth.connect.StudyBuddy.Message_RecyclerView;

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

import androidx.appcompat.app.AppCompatActivity;

import org.linphone.core.Account;
import org.linphone.core.AccountParams;
import org.linphone.core.Address;
import org.linphone.core.AuthInfo;
import org.linphone.core.ChatMessage;
import org.linphone.core.ChatMessageListenerStub;
import org.linphone.core.ChatRoom;
import org.linphone.core.ChatRoomBackend;
import org.linphone.core.ChatRoomCapabilities;
import org.linphone.core.ChatRoomParams;
import org.linphone.core.Content;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.RegistrationState;
import org.linphone.core.TransportType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import vn.edu.usth.connect.R;

public class ChatActivity extends AppCompatActivity {

    private Core core;
    private ChatRoom chatRoom;

    private String username; // Username of Sip Account
    private String password; // Password of Sip Account

    private String box_chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_chat.xml
        setContentView(R.layout.activity_chat);

        // Get username, password, boxChat name
        username = getIntent().getStringExtra("sip_username");
        password = getIntent().getStringExtra("sip_password");

        box_chat = getIntent().getStringExtra("BoxChat_Name");

        // Set name for textView
        TextView box_chat_name = findViewById(R.id.boxchat_name);
        box_chat_name.setText(box_chat);

        // Create Function & Core:
        // BoxChat Core
        // BoxChat
        Factory factory = Factory.instance();
        core =factory.createCore(null, null, this);

        // BoxChat Function
        boxchat_function();

        // Login SIP Account for User
        login(username, password);

        // Button Function
        setup_function();
    }
    
    // Box chat CoreListener
    // When Account fully login and Received Message
    private final CoreListenerStub BoxChatCoreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {
            if (state == RegistrationState.Ok) {
                if (chatRoom == null) {
                    createBasicChatRoom();
                }

                // Enable send Message button when login complete
                Button send_message = findViewById(R.id.send_message);
                send_message.setEnabled(true);

                // Enable sen Image button when login complete
                ImageButton send_image = findViewById(R.id.send_image);
                send_image.setEnabled(true);

            }
        }

        @Override
        public void onMessageReceived(Core core, ChatRoom chatRoom, ChatMessage message) {
            if (ChatActivity.this.chatRoom == null) {
                if (chatRoom.hasCapability(ChatRoomCapabilities.Basic.toInt())) {
                    ChatActivity.this.chatRoom = chatRoom;
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
                // Check state of message
                // todo: set a small text below instead of color (?)
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

    // Load Image
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

    // Add Message into Message Layout
    private void addTextMessageToHistory(ChatMessage chatMessage, Content content) {
        // Create TextView for Message
        TextView messageView = new TextView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        // If message outgoing from user: display right side, else left side
        layoutParams.gravity = chatMessage.isOutgoing() ? Gravity.RIGHT : Gravity.LEFT;

        // Set layout and set Text
        messageView.setLayoutParams(layoutParams);
        messageView.setText(content.getUtf8Text());

        if (chatMessage.isOutgoing()) {
            messageView.setBackgroundColor(getColor(R.color.white));
        } else {
            messageView.setBackgroundColor(getColor(R.color.purple_200));
        }

        // Set user data
        chatMessage.setUserData(messageView);

        // Add message in Message Layout
        LinearLayout messages = findViewById(R.id.messages);
        messages.addView(messageView);

        // Set scroll down for message layout
        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);

    }

    // add Image into Message Layout
    private void addImageMessageToHistory(ChatMessage chatMessage, Content content) {
        // Create ImageView hold image
        ImageView imageView = new ImageView(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = chatMessage.isOutgoing() ? Gravity.RIGHT : Gravity.LEFT;
        imageView.setLayoutParams(layoutParams);

        imageView.setImageBitmap(BitmapFactory.decodeFile(content.getFilePath()));
        chatMessage.setUserData(imageView);

        // Add image in Message Layout
        LinearLayout messages = findViewById(R.id.messages);
        messages.addView(imageView);

        // Set scroll down for message layout
        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    // Download or Show image from other send to
    // todo: make it auto instead of click button on it
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

        // Set scroll down for message layout
        ScrollView scrollView = findViewById(R.id.scroll);
        scrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    // Create ChatRoom without saving last chatting data
    // todo: upgrade to advance ChatRoom
    private void createBasicChatRoom() {
        ChatRoomParams params = core.createDefaultChatRoomParams();
        params.setBackend(ChatRoomBackend.Basic);
        params.enableEncryption(false);
        params.enableGroup(false);

        if (params.isValid()) {
            // Connect to the contact automatically
            String remoteSipUri = String.format("sip:%s@sip.linphone.org", box_chat);
            Address remoteAddress = Factory.instance().createAddress(remoteSipUri);

            if (remoteAddress != null) {
                Address localAddress = core.getDefaultAccount().getParams().getIdentityAddress();
                ChatRoom room = core.createChatRoom(params, localAddress, new Address[]{remoteAddress});
                if (room != null) {
                    chatRoom = room;
                }
            }
        }
    }

    // Send Message
    private void sendMessage() {
        // Get message
        EditText editText = findViewById(R.id.message);
        String messageText = editText.getText().toString();

        // Create ChatRoom
        ChatMessage chatMessage = chatRoom.createMessageFromUtf8(messageText);
        chatMessage.addListener(chatMessageListener);

        addMessageToHistory(chatMessage);
        chatMessage.send();

        // Clear Text after send
        editText.getText().clear();
    }

    // Send Image
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
    
    // Login Function
    private void login(String username, String password) {
        String domain = "sip.linphone.org";
        AuthInfo authInfo = Factory.instance().createAuthInfo(username, null, password, null, null, domain, null);

        Address identity = Factory.instance().createAddress("sip:" + username + "@" + domain);

        Address address = Factory.instance().createAddress("sip:" + domain);

        //
        AccountParams box_chat_param = core.createAccountParams();
        box_chat_param.setIdentityAddress(identity);

        if (address != null) {
            address.setTransport(TransportType.Tls);
        }

        box_chat_param.setServerAddress(address);
        box_chat_param.setRegisterEnabled(true);

        Account box_chat_account = core.createAccount(box_chat_param);
        core.addAuthInfo(authInfo);
        core.addAccount(box_chat_account);

        core.setDefaultAccount(box_chat_account);
        core.addListener(BoxChatCoreListener);
        core.start();
        
    }

    private void boxchat_function(){
        // BoxChat Function
        findViewById(R.id.send_message).setOnClickListener(v -> sendMessage());
        findViewById(R.id.send_message).setEnabled(false);

        findViewById(R.id.send_image).setOnClickListener(v -> sendImage());
        findViewById(R.id.send_image).setEnabled(false);
    }

    private void setup_function() {
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            onBackPressed();
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}