package vn.edu.usth.connect.StudyBuddy.SB_RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.fragment.app.FragmentManager;

import java.util.List;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.StudyBuddy.SB_RecyclerView.Detail.UserDetailActivity;

public class Rcm_UserAdapter extends RecyclerView.Adapter<Rcm_UserViewHolder> {

    Context context;
    List<Rcm_UserItem> items;

    public Rcm_UserAdapter(Context context, List<Rcm_UserItem> item) {
        this.context = context;
        this.items = item;
    }

    @NonNull
    @Override
    public Rcm_UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Rcm_UserViewHolder(LayoutInflater.from(context).inflate(R.layout.rcm_user_frame, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Rcm_UserViewHolder holder, int position) {
        Rcm_UserItem item = items.get(position);

        holder.name.setText(item.getName());
        holder.gender.setText(item.getGender());
        holder.major.setText(item.getMajor());
        holder.looking_for.setText(item.getLooking_for());
        holder.image.setImageResource(item.getImage());

        holder.itemView.setOnClickListener(view -> {
            Intent i = new Intent(context, UserDetailActivity.class);
            i.putExtra("Name", item.getName());
            i.putExtra("Gender", item.getGender());
            i.putExtra("Major", item.getMajor());
            i.putExtra("LookingFor", item.getLooking_for());
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

}
