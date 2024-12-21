package vn.edu.usth.connect.StudyBuddy.SB_RecyclerView.Detail;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;


import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.edu.usth.connect.R;

public class UserDetailActivity extends AppCompatActivity {

    // Detail StudyBuddy Profile
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_user)detail.xml
        setContentView(R.layout.activity_user_detail);

        // Call ID
        TextView name = findViewById(R.id.name);
        TextView profile_name = findViewById(R.id.profile_detail);
        TextView gender = findViewById(R.id.gender);
        TextView major = findViewById(R.id.major);
        TextView looking_for = findViewById(R.id.looking_for);

        // SetText
        name.setText(getIntent().getStringExtra("Name"));
        profile_name.setText(getIntent().getStringExtra("Name"));
        gender.setText(getIntent().getStringExtra("Gender"));
        major.setText(getIntent().getStringExtra("Major"));
        looking_for.setText(getIntent().getStringExtra("LookingFor"));

        // TablLayout & ViewPager
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager2 = findViewById(R.id.picture_viewPager2);

        PictureAdapter adapter = new PictureAdapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText("");
            }
        }).attach();

        // Button Function
        setup_function();
    }

    private void setup_function(){
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