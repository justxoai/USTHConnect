package vn.edu.usth.connect.Schedule.TimeTable.Event;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalTime;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.Schedule.TimeTable.Calender.CalenderUtils;

public class EventEditActivity extends AppCompatActivity {

    private LocalTime time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_event_edit);

        // Use LocalTime right now
        time = LocalTime.now();

        // Event: name event: "testing", time: tại thời điểm bấm, date = ngày click
        Event newEvent = new Event("testing", CalenderUtils.selectedDate, time);

        // Add into ListView in Event
        Event.eventsList.add(newEvent);

        // End Task
        finish();
    }
}