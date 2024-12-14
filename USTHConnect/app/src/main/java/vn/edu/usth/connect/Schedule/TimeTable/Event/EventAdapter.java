package vn.edu.usth.connect.Schedule.TimeTable.Event;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.Schedule.TimeTable.Calender.CalenderUtils;

public class EventAdapter extends ArrayAdapter<Event> {

    public EventAdapter(@NonNull Context context, List<Event> events)
    {
        super(context, 0, events);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        Event event = getItem(position);

        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.event_frame, parent, false);

        TextView eventCellTV = convertView.findViewById(R.id.eventCellTV);

        // CalenderUtils.formattedTime(event.getTime(): Get time: 12:12:12 AM
        String eventTitle = "event.getName()" +" "+ CalenderUtils.formattedTime(event.getTime());

        // setText Event title
        eventCellTV.setText(eventTitle);

        return convertView;
    }

}
