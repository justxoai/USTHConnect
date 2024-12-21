package vn.edu.usth.connect.Schedule;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import vn.edu.usth.connect.Schedule.Course.RecyclerView.Course_Fragment;
import vn.edu.usth.connect.Schedule.Favorite_RecyclerView.Favorite_Fragment;
import vn.edu.usth.connect.Schedule.TimeTable.Timetable_Fragment;

public class Fragment_schedule_changing extends FragmentStateAdapter {
    public Fragment_schedule_changing(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);

    }@NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new Timetable_Fragment();
            case 1:
                return new Favorite_Fragment();
            case 2:
                return new Course_Fragment();
            default:
                return new Timetable_Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}