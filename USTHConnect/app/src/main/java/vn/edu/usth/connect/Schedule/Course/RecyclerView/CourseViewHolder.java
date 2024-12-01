package vn.edu.usth.connect.Schedule.Course.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.usth.connect.R;

public class CourseViewHolder extends RecyclerView.ViewHolder{

    TextView heading, subhead;

    public CourseViewHolder(@NonNull View itemView){
        super(itemView);
        heading = itemView.findViewById(R.id.first_text);
        subhead = itemView.findViewById(R.id.second_text);
    }
}