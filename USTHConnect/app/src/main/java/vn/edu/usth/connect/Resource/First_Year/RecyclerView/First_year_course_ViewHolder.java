package vn.edu.usth.connect.Resource.First_Year.RecyclerView;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import vn.edu.usth.connect.R;

public class First_year_course_ViewHolder extends RecyclerView.ViewHolder{

    TextView heading, subhead;

    public First_year_course_ViewHolder(@NonNull View itemView){
        super(itemView);
        heading = itemView.findViewById(R.id.first_text);
        subhead = itemView.findViewById(R.id.second_text);
    }
}
