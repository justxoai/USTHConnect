package vn.edu.usth.connect.StudyBuddy;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import vn.edu.usth.connect.R;

public class Study_Buddy_Profile_Fragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_study__buddy__profile_, container, false);

        // Button Function
        setup_function(v);

        return v;
    }

    private void setup_function(View v){
        Button edit_button = v.findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), vn.edu.usth.connect.StudyBuddy.EditProfile.Edit_SB_Profile_Activity.class);
                startActivity(i);
            }
        });
    }

}