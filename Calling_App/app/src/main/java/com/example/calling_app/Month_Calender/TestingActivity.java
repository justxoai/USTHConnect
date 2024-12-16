package com.example.calling_app.Month_Calender;

import static com.example.calling_app.Month_Calender.CalenderUtils.daysInMonthArray;
import static com.example.calling_app.Month_Calender.CalenderUtils.monthYearFromDate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calling_app.R;

import java.time.LocalDate;

import java.util.ArrayList;

public class TestingActivity extends AppCompatActivity implements CalenderAdapter.OnItemListener {

    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        // Call RecyclerView and TextView
        initWidgets();

        // Get Local Time: 2024 - 12 - Day
        CalenderUtils.selectedDate = LocalDate.now();

        // Show days in month in week view(?)
        setMonthView();
    }

    private void initWidgets() {
        // RecyclerView: recycler date
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);

        // Text: Use for setText
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        // SetText to the format MMMM - YYYY
        monthYearText.setText(monthYearFromDate(CalenderUtils.selectedDate));
//        ArrayList<LocalDate> daysInMonth = daysInMonthArray(CalenderUtils.selectedDate);

        // Setup and Show day in month
        ArrayList<LocalDate> daysInMonth = daysInMonthArray();

        CalenderAdapter calendarAdapter = new CalenderAdapter(daysInMonth, this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);

        // Setup View Week in Month
        calendarRecyclerView.setLayoutManager(layoutManager);
        calendarRecyclerView.setAdapter(calendarAdapter);

        // Don't have Event
    }

    // Function Previous and Next Month
    public void previousMonthAction(View view) {
        CalenderUtils.selectedDate = CalenderUtils.selectedDate.minusMonths(1);
        setMonthView();
    }
    public void nextMonthAction(View view) {
        CalenderUtils.selectedDate = CalenderUtils.selectedDate.plusMonths(1);
        setMonthView();
    }

    // Press Day in Month
    @Override
    public void onItemClick(int position, LocalDate date) {
        if (date != null) {
            CalenderUtils.selectedDate = date;
            setMonthView();
        }
    }

    // Button Week Function
    public void weeklyAction(View view) {
        startActivity(new Intent(this, WeekViewActivity.class));
    }
}