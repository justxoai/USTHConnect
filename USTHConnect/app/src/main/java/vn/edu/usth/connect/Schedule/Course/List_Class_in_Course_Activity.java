package vn.edu.usth.connect.Schedule.Course;

import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import vn.edu.usth.connect.R;

public class List_Class_in_Course_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_list_class_in_course.xml
        setContentView(R.layout.activity_list_class_in_course);

        // Button Function
        setup_function();

        // Set text for RecyclerView
        setup_text_recycler();

    }

    private void setup_function(){
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void setup_text_recycler(){
        TextView course_name = findViewById(R.id.course_name_timetable);
        TextView course_lecturer = findViewById(R.id.course_lecturer_timetable);

        String name = getIntent().getStringExtra("Course Name");
        String locate = getIntent().getStringExtra("Course Lecturer");

        course_name.setText(name);
        course_lecturer.setText(locate);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}