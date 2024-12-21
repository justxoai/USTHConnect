package vn.edu.usth.connect.StudyBuddy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.StudyBuddy.SB_RecyclerView.Rcm_UserAdapter;
import vn.edu.usth.connect.StudyBuddy.SB_RecyclerView.Rcm_UserItem;

public class Study_Buddy_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment_study_buddy_.xml
        View v = inflater.inflate(R.layout.fragment_study__buddy_, container, false);

        // Setup Horizontal Rcm_User
        setup_Rcm_User(v);

        return v;
    }

    // Setup RecyclerView
    // Folder: SB_Recycler: Rcm_UserAdapter, Rcm_UserItem, Rcm_UserViewHolder
    private void setup_Rcm_User(View v) {
        // RecyclerView point to Detail.UserDetailActivity
        RecyclerView recyclerView = v.findViewById(R.id.horizontal_recyclerview);

        List<Rcm_UserItem> items = new ArrayList<Rcm_UserItem>();
        items.add(new Rcm_UserItem("Name1", "Female", "BIT", "Study Supporter", R.drawable.test_image_1));
        items.add(new Rcm_UserItem("Name2", "Female", "DS", "Chit-chatting", R.drawable.test_image_2));
        items.add(new Rcm_UserItem("Name3", "Male", "ICT", "Share Knowledge", R.drawable.test_image_3));
        items.add(new Rcm_UserItem("Name4", "Male", "FST", "Chit-chatting", R.drawable.test_image_4));

        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        Rcm_UserAdapter adapter = new Rcm_UserAdapter(requireContext(), items);
        recyclerView.setAdapter(adapter);
    }
}