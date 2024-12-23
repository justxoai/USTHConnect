package vn.edu.usth.connect.Schedule.Favorite_RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.Schedule.Course.Event_RecyclerView.List_Class_in_Course_Activity;

public class Favorite_course_Adapter extends RecyclerView.Adapter<Favorite_course_ViewHolder> {

    Context context;
    List<Favorite_course_Item> items;

    public Favorite_course_Adapter(Context context, List<Favorite_course_Item> items){
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public Favorite_course_ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Favorite_course_ViewHolder(LayoutInflater.from(context).inflate(R.layout.double_text_frame, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Favorite_course_ViewHolder holder, int position) {
        Favorite_course_Item item = items.get(position);

        holder.heading.setText(item.getHeading());
        holder.subhead.setText(item.getSubhead());

        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, List_Class_in_Course_Activity.class);
            i.putExtra("Course Name", item.getHeading());
            i.putExtra("Course Lecturer", item.getSubhead());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
