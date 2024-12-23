package vn.edu.usth.connect;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import vn.edu.usth.connect.Home.Fragment_home_changing;
import vn.edu.usth.connect.Home.NotificationRecyclerView.NotificationFragment;
import vn.edu.usth.connect.Resource.CategoryRecyclerView.CategoryActivity;
import vn.edu.usth.connect.Utils.NotificationUtils;
import vn.edu.usth.connect.Workers.FetchEventsWorker;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 mviewPager;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout mDrawerLayout;

    private ImageView avatar_profile_image;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // SharePreferences: Save Login
//        SharedPreferences sharedPreferences = getSharedPreferences("ToLogin", MODE_PRIVATE);
//        boolean isLoggedIn = sharedPreferences.getBoolean("IsLoggedIn", false);
//        String token = sharedPreferences.getString("Token", null);
//
//        if (!isLoggedIn || token == null || isTokenExpired(token)) {
//            navigateToLoginFragment();
//            return;
//        }

        // Create notification channel (for devices running Android 8.0 and above)
//        NotificationUtils.createNotificationChannel(this);

        setContentView(R.layout.activity_main); // Set layout first

        // Schedule background tasks
        scheduleEventFetchWorker();


        // ViewPager2: Change fragments: DashboardFragment, NotificationFragment, ProfileFragment
        mviewPager = findViewById(R.id.view_pager);

        // BottomNavigation: Bottom Menu :D
        bottomNavigationView = findViewById(R.id.home_bottom_navigation);

        // Adapter: Fragment_home_changing
        vn.edu.usth.connect.Home.Fragment_home_changing adapter = new  vn.edu.usth.connect.Home.Fragment_home_changing(getSupportFragmentManager(), getLifecycle());
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
                switch (position)
                {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.home_page).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.noti_page).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.profile_page).setChecked(true);
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
                if (item.getItemId() == R.id.home_page) {
                    mviewPager.setCurrentItem(0, true); // Switch to the first fragment
                    return true;
                }
                if (item.getItemId() == R.id.noti_page) {
                    mviewPager.setCurrentItem(1, true); // Switch to the first fragment
                    return true;
                }
                if (item.getItemId() == R.id.profile_page) {
                    mviewPager.setCurrentItem(2, true); // Switch to the first fragment
                    return true;
                }
                return false;
            }
        });

        // Setup Side-menu for Activity
        mDrawerLayout = findViewById(R.id.home_activity);

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
    }

    // Check if the token is Expired
    private boolean isTokenExpired(String token) {
        try {
            String[] split = token.split("\\.");
            String payload = split[1];
            String json = new String(android.util.Base64.decode(payload, android.util.Base64.URL_SAFE));
            JSONObject jsonObject = new JSONObject(json);
            long exp = jsonObject.getLong("exp");
            long currentTime = System.currentTimeMillis() / 1000;
            return currentTime > exp;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Treat as expired if error occurs
        }
    }

    private void navigator_drawer_function(){
        LinearLayout to_home_activity = findViewById(R.id.to_home_page);
        to_home_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.MainActivity.this, vn.edu.usth.connect.MainActivity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_schedule_activity = findViewById(R.id.to_schedule_page);
        to_schedule_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                Intent i = new Intent(vn.edu.usth.connect.MainActivity.this, vn.edu.usth.connect.Schedule.Schedule_Activity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_campus_activity = findViewById(R.id.to_map_page);
        to_campus_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.MainActivity.this, vn.edu.usth.connect.Campus.Campus_Activity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_resource_activity = findViewById(R.id.to_resource_page);
        to_resource_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.MainActivity.this, CategoryActivity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout to_study_activity = findViewById(R.id.to_study_page);
        to_study_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.MainActivity.this, vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.class);
                startActivity(i);
                finish();
            }
        });

        LinearLayout logout = findViewById(R.id.to_log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Update SharedPreferences to set isLoggedIn to false
                SharedPreferences sharedPreferences = getSharedPreferences("ToLogin", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IsLoggedIn", false);
                editor.apply();

                Fragment loginFragment = new vn.edu.usth.connect.Login.LoginFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(android.R.id.content, loginFragment);
                transaction.commit();
            }
        });

    }

    private void update_picture(){
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
                    Toast.makeText(MainActivity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void navigateToLoginFragment() {
        Fragment loginFragment = new vn.edu.usth.connect.Login.LoginFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(android.R.id.content, loginFragment);
        transaction.commit();
    }

    // Fetch events every 10 minutes
    private void scheduleEventFetchWorker() {
        // Always trigger the One-time event fetch worker immediately
        OneTimeWorkRequest fetchEventsOneTimeRequest = new OneTimeWorkRequest.Builder(FetchEventsWorker.class)
                .build();
        WorkManager.getInstance(this).enqueue(fetchEventsOneTimeRequest);
        Log.d("MainActivity", "One-time event fetch worker triggered.");

        // If it's the first start-up or after login, schedule the periodic work
        WorkRequest fetchEventsRequest = new PeriodicWorkRequest.Builder(FetchEventsWorker.class, 10, TimeUnit.MINUTES)
                .build();
        WorkManager.getInstance(this).enqueue(fetchEventsRequest);
        Log.d("MainActivity", "Periodic event fetch worker scheduled.");
    }

}