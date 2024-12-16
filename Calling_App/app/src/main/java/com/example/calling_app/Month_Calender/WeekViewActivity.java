package com.example.calling_app.Month_Calender;

import static com.example.calling_app.Month_Calender.CalenderUtils.daysInWeekArray;
import static com.example.calling_app.Month_Calender.CalenderUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calling_app.R;

import java.time.LocalDate;
import java.util.ArrayList;

public class WeekViewActivity extends AppCompatActivity implements CalenderAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private ListView eventListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_view);
        // Init
        initWidgets();
        //
        setWeekView();
    }

    private void initWidgets() {
        // RecyclerView
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);

        monthYearText = findViewById(R.id.monthYearTV);

        // List Event happen in day
        eventListView = findViewById(R.id.eventListView);
    }

    //
    private void setWeekView() {
        monthYearText.setText(monthYearFromDate(CalenderUtils.selectedDate));
        ArrayList<LocalDate> days = daysInWeekArray(CalenderUtils.selectedDate);

        CalenderAdapter calendarAdapter = new CalenderAdapter(days, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        // Setup Event
        setEventAdpater();
    }


    public void previousWeekAction(View view) {
        CalenderUtils.selectedDate = CalenderUtils.selectedDate.minusWeeks(1);
        setWeekView();
    }

    public void nextWeekAction(View view) {
        CalenderUtils.selectedDate = CalenderUtils.selectedDate.plusWeeks(1);
        setWeekView();
    }

    @Override
    public void onItemClick(int position, LocalDate date) {
        CalenderUtils.selectedDate = date;
        setWeekView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setEventAdpater();
    }

    private void setEventAdpater() {
        ArrayList<Event> dailyEvents = Event.eventsForDate(CalenderUtils.selectedDate);
        EventAdapter eventAdapter = new EventAdapter(getApplicationContext(), dailyEvents);
        eventListView.setAdapter(eventAdapter);
    }

    public void newEventAction(View view) {
        startActivity(new Intent(this, EventEditActivity.class));
    }

    public void dailyAction(View view) {
        startActivity(new Intent(this, DailyCalenderActivity.class));
    }
}