package vn.edu.usth.connect.StudyBuddy;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.linphone.core.Account;
import org.linphone.core.Core;
import org.linphone.core.CoreListenerStub;
import org.linphone.core.Factory;
import org.linphone.core.*;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.Resource.CategoryRecyclerView.CategoryActivity;
import vn.edu.usth.connect.StudyBuddy.Welcome.Register_StudyBuddyFragment;
import vn.edu.usth.connect.StudyBuddy.Welcome.WelcomeFragment;

public class Study_Buddy_Activity extends AppCompatActivity {

    private ViewPager2 mviewPager;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout mDrawerLayout;

    private ImageView avatar_profile_image;
    private Handler handler = new Handler();

    private Core core;

    private String username; // Username of Sip Account
    private String password; // Password of Sip Account

    private String contact;

    private Button mute_mic, toggle_speak, answer, hang_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_study_buddy.xml
        setContentView(R.layout.activity_study_buddy);

        // SharedPreference for the 1st time
//        SharedPreferences sharedPreferences = getSharedPreferences("ToRegister12345678904", MODE_PRIVATE);
//        boolean isRegister = sharedPreferences.getBoolean("IsRegister", false);
//
//        if (!isRegister) {
//            // Move to WelcomeFragment
//            navigatorToRegister();
//        }

        // ViewPager2: Change fragments: MessageFragment, StudyBuddyFragment, StudyBuddyProfileFragment
        mviewPager = findViewById(R.id.view_study_buddy_pager);

        // BottomNavigation: Bottom Menu :D
        bottomNavigationView = findViewById(R.id.study_buddy_bottom_navigation);

        // Adapter: Fragment_Study_Buddy_changing
        Fragment_Study_Buddy_changeing adapter = new Fragment_Study_Buddy_changeing(getSupportFragmentManager(), getLifecycle());
        mviewPager.setAdapter(adapter);
        mviewPager.setUserInputEnabled(false);

        // ViewPager2 setup Function
        mviewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.sb_page).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.chat_page).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.audio_page).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.sb_profile_page).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });

        // BottomNavigation setup Function
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.sb_page) {
                    mviewPager.setCurrentItem(0, true);
                    return true;
                }
                if (item.getItemId() == R.id.chat_page) {
                    mviewPager.setCurrentItem(1, true);
                    return true;
                }
                if (item.getItemId() == R.id.audio_page) {
                    mviewPager.setCurrentItem(2, true);
                    return true;
                }
                if (item.getItemId() == R.id.sb_profile_page) {
                    mviewPager.setCurrentItem(3, true);
                    return true;
                }
                return false;
            }
        });

        // Setup Side-menu for Activity
        mDrawerLayout = findViewById(R.id.sb_activity);

        // Function to open Side-menu
        ImageButton mImageView = findViewById(R.id.menu_button);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Side-menu function
        navigator_drawer_function();

        // Load image in the Side-menu
        update_picture();

        // Test IncomingCall
        // GetString
        Intent intent = getIntent();
        username = intent.getStringExtra("sip_username");
        password = intent.getStringExtra("sip_password");
        contact  = intent.getStringExtra("Contact_Name");

        // Create Function & Core
        // IncomingCall
        Factory incoming_factory = Factory.instance();
        core = incoming_factory.createCore(null, null, this);

        // Login BocChat, Incoming, Outgoing
        login(username, password);

        // Incoming Call Function
        // Call ID
        hang_up = findViewById(R.id.incoming_hang_up);
        answer = findViewById(R.id.incoming_answer);
        mute_mic = findViewById(R.id.incoming_mute_mic);
        toggle_speak = findViewById(R.id.incoming_toggle_speaker);

        // Button Enable
        hang_up.setEnabled(false);
        answer.setEnabled(false);
        mute_mic.setEnabled(false);
        toggle_speak.setEnabled(false);

        // Button Function
        hang_up.setOnClickListener(view -> {
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().terminate();
            }
        });

        answer.setOnClickListener(view -> {
            if (core.getCurrentCall() != null) {
                core.getCurrentCall().accept();
            }
        });

        mute_mic.setOnClickListener(view -> {
            core.enableMic(!core.micEnabled());
        });

        toggle_speak.setOnClickListener(view -> {
            toggleSpeaker();
        });

    }

    // Incoming CoreListener
    private final CoreListenerStub incomingCallCoreListener = new CoreListenerStub() {
        @Override
        public void onAccountRegistrationStateChanged(Core core, Account account, RegistrationState state, String message) {}

        // Received a call from another User
        // When connected
        @Override
        public void onCallStateChanged(Core core, Call call, Call.State state, String message) {
            findViewById(R.id.sb_layout).setVisibility(View.GONE);

            // When a call is received
            if (state == Call.State.IncomingReceived) {
                // Incoming call Visible, Hide Sb Layout
                findViewById(R.id.incoming_call_layout).setVisibility(View.VISIBLE);

                // Enable HangUp and Answer Button
                hang_up.setEnabled(true);
                answer.setEnabled(true);

                // Set TextView
                TextView contact_name = findViewById(R.id.incoming_remote_address);
                contact_name.setText(call.getRemoteAddress().getUsername());
            } else if (state == Call.State.Connected) {
                // Visible Button
                toggle_speak.setVisibility(View.VISIBLE);
                mute_mic.setVisibility(View.VISIBLE);

                // Enable mic and speaker
                mute_mic.setEnabled(true);
                toggle_speak.setEnabled(true);
            } else if (state == Call.State.Released) {
                findViewById(R.id.incoming_call_layout).setVisibility(View.GONE);
                findViewById(R.id.sb_layout).setVisibility(View.VISIBLE);
            }
        }
    };

    // Incoming toggleSpeaker
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
        }
    }

    // Incoming Login
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
        core.addListener(incomingCallCoreListener);
        core.start();

        // We will need the RECORD_AUDIO permission for video call
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
        }
    }

    private void navigator_drawer_function() {
        LinearLayout to_home_activity = findViewById(R.id.to_home_page);
        to_home_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.this, vn.edu.usth.connect.MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_schedule_activity = findViewById(R.id.to_schedule_page);
        to_schedule_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.this, vn.edu.usth.connect.Schedule.Schedule_Activity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_campus_activity = findViewById(R.id.to_map_page);
        to_campus_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.this, vn.edu.usth.connect.Campus.Campus_Activity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_resource_activity = findViewById(R.id.to_resource_page);
        to_resource_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.this, CategoryActivity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_study_activity = findViewById(R.id.to_study_page);
        to_study_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.this, vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout logout = findViewById(R.id.to_log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment loginFragment = new vn.edu.usth.connect.Login.LoginFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, loginFragment);
                transaction.commit();
            }
        });

    }

    private void update_picture() {
        avatar_profile_image = findViewById(R.id.avatar_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("ProfileImage", MODE_PRIVATE);
        String url = sharedPreferences.getString("Image_URL", null);
        if (url != null) {
            new UpdateImage(url).start();
        }
    }

    class UpdateImage extends Thread {
        private String url;
        private Bitmap bitmap;

        public UpdateImage(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                if (bitmap != null) {
                    avatar_profile_image.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(Study_Buddy_Activity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // Use for test
    // WelcomeFrag -> RegisterFrag
    private void navigatorToRegister() {
        Fragment welcomeFragment = new WelcomeFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, welcomeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}