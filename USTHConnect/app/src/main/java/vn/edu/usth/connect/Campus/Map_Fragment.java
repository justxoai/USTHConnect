package vn.edu.usth.connect.Campus;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vn.edu.usth.connect.R;

public class Map_Fragment extends Fragment {

    // Show Campus, call from google map
    // Don't have function yet :)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment_map.xml
        View v = inflater.inflate(R.layout.fragment_map_, container, false);

        return v;
    }

}