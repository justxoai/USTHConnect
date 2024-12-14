package vn.edu.usth.connect.Schedule.TimeTable;

import static vn.edu.usth.connect.Schedule.TimeTable.Calender.CalenderUtils.selectedDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.time.LocalTime;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Locale;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.Schedule.TimeTable.Calender.CalenderUtils;
import vn.edu.usth.connect.Schedule.TimeTable.Event.Event;
import vn.edu.usth.connect.Schedule.TimeTable.Event.EventEditActivity;
import vn.edu.usth.connect.Schedule.TimeTable.Hour.HourAdapter;
import vn.edu.usth.connect.Schedule.TimeTable.Hour.HourEvent;

public class DailyCalenderActivity extends AppCompatActivity {

    private TextView monthDayText;
    private TextView dayOfWeekTV;
    private ListView hourListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daily_calender);

        // Setup TextView
        monthDayText = findViewById(R.id.monthDayText);

        // Setup TextView
        dayOfWeekTV = findViewById(R.id.dayOfWeekTV);

        // List Hour for Event
        hourListView = findViewById(R.id.hourListView);

        // Button Function
        setup_function();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDayView();
    }

    // setText for TextView
    private void setDayView() {
        monthDayText.setText(CalenderUtils.monthDayFromDate(selectedDate));
        String dayOfWeek = selectedDate.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.getDefault());
        dayOfWeekTV.setText(dayOfWeek);
        setHourAdapter();
    }

    // Setup Adapter for ListView for task in hour
    private void setHourAdapter() {
        HourAdapter hourAdapter = new HourAdapter(getApplicationContext(), hourEventList());
        hourListView.setAdapter(hourAdapter);
    }

    private ArrayList<HourEvent> hourEventList() {
        ArrayList<HourEvent> list = new ArrayList<>();

        for (int hour = 0; hour < 24; hour++) {
            LocalTime time = LocalTime.of(hour, 0);
            ArrayList<Event> events = Event.eventsForDateAndTime(selectedDate, time);
            HourEvent hourEvent = new HourEvent(time, events);
            list.add(hourEvent);
        }

        return list;
    }

    // To previous day
    public void previousDayAction(View view) {
        selectedDate = selectedDate.minusDays(1);
        setDayView();
    }

    // To next day
    public void nextDayAction(View view) {
        selectedDate = selectedDate.plusDays(1);
        setDayView();
    }

    // Create Event
    // Folder: Hour: HourEvent and HourAdapter
    public void newEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    private void setup_function(){
        ImageButton back = findViewById(R.id.back_button);
        back.setOnClickListener(view -> {
                onBackPressed();
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}