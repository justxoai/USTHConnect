package com.example.calling_app.Month_Calender;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.calling_app.R;

import java.util.ArrayList;
import java.time.LocalDate;

public class CalenderAdapter extends RecyclerView.Adapter<CalenderViewHolder>{

    // LocalDate Array
    private final ArrayList<LocalDate> days;
    private final OnItemListener onItemListener;

    public CalenderAdapter(ArrayList<LocalDate> days, OnItemListener onItemListener)
    {
        this.days = days;
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public CalenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.calender_cell, parent, false);
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();


        if(days.size() > 15) //month view
            layoutParams.height = (int) (parent.getHeight() * 0.166666666);
        else // week view
            layoutParams.height = (int) parent.getHeight();

        return new CalenderViewHolder(view, onItemListener, days);
    }

    @Override
    public void onBindViewHolder(@NonNull CalenderViewHolder holder, int position)
    {
        // Day has been click
        final LocalDate date = days.get(position);


//        if(date == null)
//            holder.dayOfMonth.setText("");
//        else
//        {
//            holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));
//            if(date.equals(CalenderUtils.selectedDate))
//                holder.parentView.setBackgroundColor(Color.LTGRAY);
//        }


        holder.dayOfMonth.setText(String.valueOf(date.getDayOfMonth()));

        if(date.equals(CalenderUtils.selectedDate))
            holder.parentView.setBackgroundColor(Color.LTGRAY); // Change color when select

        if(date.getMonth().equals(CalenderUtils.selectedDate.getMonth()))
            holder.dayOfMonth.setTextColor(Color.BLACK); // Color text "in" month
        else
            holder.dayOfMonth.setTextColor(Color.LTGRAY); // Color text not "in" month
    }

    @Override
    public int getItemCount()
    {
        return days.size();
    }

    public interface  OnItemListener
    {
        void onItemClick(int position, LocalDate date);
    }
}
