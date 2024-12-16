package com.example.calling_app.Month_Calender;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.calling_app.R;

import java.time.LocalTime;

public class EventEditActivity extends AppCompatActivity {

    private LocalTime time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);

        time = LocalTime.now();

        Event newEvent = new Event("testing", CalenderUtils.selectedDate, time);

        Event.eventsList.add(newEvent);

        finish();

    }

}