package vn.edu.usth.connect.Campus.Detail;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import vn.edu.usth.connect.Campus.Date_adapter;
import vn.edu.usth.connect.R;

public class Detail_Building_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_detail_building.xml
        setContentView(R.layout.activity_detail_building);

        // Set text for RecyclerView
        setup_text_recyclerview();

        // Tablayout: Campus.TabLayout_Fragment, 7 days fragments :D
        date_tablayout();

        // Function for backbutton
        setup_function();
    }

    private void date_tablayout(){
        TabLayout tabLayout = findViewById(R.id.building_detail_tablayout);
        ViewPager2 viewPager2 = findViewById(R.id.building_detail_viewPager2);

        Date_adapter adapter = new Date_adapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Mon");
                        break;
                    case 1:
                        tab.setText("Tue");
                        break;
                    case 2:
                        tab.setText("Wes");
                        break;
                    case 3:
                        tab.setText("Thu");
                        break;
                    case 4:
                        tab.setText("Fri");
                        break;
                    case 5:
                        tab.setText("Sat");
                        break;
                    case 6:
                        tab.setText("Sun");
                        break;
                }
            }
        }).attach();
    }

    private void setup_function(){
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    // Set text building's name in Header and building's location
    private void setup_text_recyclerview(){
        TextView building_name = findViewById(R.id.detail_building_name);
        TextView building_locate = findViewById(R.id.detail_building_locate);

        String name = getIntent().getStringExtra("Building Name");
        String locate = getIntent().getStringExtra("Building Locate");

        building_name.setText(name);
        building_locate.setText(locate);
    }

    // BackPress: backto BuildingFragment
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}