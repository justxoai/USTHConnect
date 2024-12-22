package vn.edu.usth.connect.StudyBuddy;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import org.linphone.core.Core;

import java.util.ArrayList;
import java.util.List;

import vn.edu.usth.connect.R;
import vn.edu.usth.connect.StudyBuddy.Audio.ContactAdapter;
import vn.edu.usth.connect.StudyBuddy.Audio.ContactItem;

public class AudioFragment extends Fragment {

    private String username; // Username of SIP Account
    private String password; // Password of SIP Account
    
    private List<ContactItem> items;
    private ContactAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment_audio.xml
        View v = inflater.inflate(R.layout.fragment_audio, container, false);

        // Get username and password
        // Idea: Get Username and Password after RegisterStudyBuddyFragment to login
        username = requireActivity().getIntent().getStringExtra("sip_username");
        password = requireActivity().getIntent().getStringExtra("sip_password");

        // Setup RecyclerView
        setup_recyclerview_function(v);

        return v;
    }

    // SetUp RecyclerView and SearchView
    // Folder: Audio: ContactItem, ContactAdapter, ContactViewHolder
    private void setup_recyclerview_function (View v){
        // RecyclerView point to Audio.OutgoingCall
        RecyclerView recyclerView = v.findViewById(R.id.contact_recyclerview);

        items = new ArrayList<ContactItem>();

        items.add(new ContactItem("ducduyvx", username, password)); // Test: ducduyvx - 1234567890
        items.add(new ContactItem("vietanhngx", username, password)); // Test: vietanhngx - 1234567890
        items.add(new ContactItem("quangminhdo", username, password)); // Test: quangminhdo - 1234567890
        items.add(new ContactItem("hoanglanvx", username, password)); // Account don't exist
        items.add(new ContactItem("hoanganhpham", username, password)); // Account don't exist
        items.add(new ContactItem("dangnguyennguyen", username, password)); // Account don't exist

        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ContactAdapter(requireContext(), items);
        recyclerView.setAdapter(adapter);
        
        // SearchView
        SearchView searchView = v.findViewById(R.id.contact_searchView);
        searchView.clearFocus();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    // Filter for SearchView
    private void filterList(String text) {
        List<ContactItem> filteredItems = new ArrayList<>();

        for (ContactItem item : items) {
            if (item.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredItems.add(item);
            }
        }

        if (filteredItems.isEmpty()) {
            Toast.makeText(requireContext(), "No results found", Toast.LENGTH_SHORT).show();
        }
        else{
            adapter.setFilter(filteredItems);
        }

    }
}