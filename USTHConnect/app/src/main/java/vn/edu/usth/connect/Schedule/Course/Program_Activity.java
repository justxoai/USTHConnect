package vn.edu.usth.connect.Schedule.Course;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import vn.edu.usth.connect.R;

public class Program_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_program.xml
        setContentView(R.layout.activity_program);

        // Set text for RecyclerView
        setup_text_recyclerview();

        // Button Function
        setup_function();
   }

   private void setup_function(){
       ImageButton back_button = findViewById(R.id.back_button);
       back_button.setOnClickListener(view -> {
           onBackPressed();
       });

       LinearLayout second_year = findViewById(R.id.second_year);
       second_year.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(Program_Activity.this, Second_Course_Activity.class);
               i.putExtra("Program Name", getIntent().getStringExtra("Program Name"));
               startActivity(i);
           }
       });

       LinearLayout third_year = findViewById(R.id.third_year);
       third_year.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i = new Intent(Program_Activity.this, Third_Course_Activity.class);
               i.putExtra("Program Name", getIntent().getStringExtra("Program Name"));
               startActivity(i);
           }
       });
   }

   private void setup_text_recyclerview(){
       TextView program_name = findViewById(R.id.program_name);

       String name = getIntent().getStringExtra("Program Name");

      program_name.setText(name);
   }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}