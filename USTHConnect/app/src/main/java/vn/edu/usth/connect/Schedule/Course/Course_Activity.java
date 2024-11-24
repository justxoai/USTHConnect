package vn.edu.usth.connect.Schedule.Course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.usth.connect.R;

public class Course_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_course);

        setup_function();
    }

    private void setup_function(){
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            onBackPressed();
        });

        LinearLayout course_timetable = findViewById(R.id.logic_course_to_timatable);
        course_timetable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(vn.edu.usth.connect.Schedule.Course.Course_Activity.this, vn.edu.usth.connect.Schedule.Course.List_Class_in_Course_Activity.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}