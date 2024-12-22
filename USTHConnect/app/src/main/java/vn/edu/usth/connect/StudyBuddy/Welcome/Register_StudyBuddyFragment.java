package vn.edu.usth.connect.StudyBuddy.Welcome;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import vn.edu.usth.connect.Login.LoginFragment;
import vn.edu.usth.connect.R;
import vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity;

public class Register_StudyBuddyFragment extends Fragment {

    private DrawerLayout mDrawerLayout;

    // todo: Save data in the .csv format

    // Register SIP Account
    // Login to Study Buddy using SIP account
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment_register_study_buddy.xml
        View v = inflater.inflate(R.layout.fragment_register__study_buddy, container, false);

        // Call ID EditText and Button
        EditText username = v.findViewById(R.id.editTextUserName);
        EditText user_password = v.findViewById(R.id.editTextPassword);

        Button login_button = v.findViewById(R.id.login_button);
        login_button.setOnClickListener(view -> {
            String email = username.getText().toString();
            String password = user_password.getText().toString();

            if (!email.isEmpty() && !password.isEmpty()){
                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ToRegister12345678904", getContext().MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("IsRegister", true);
                editor.apply();

                Intent i = new Intent(getActivity(), Study_Buddy_Activity.class);
                i.putExtra("sip_username", email);
                i.putExtra("sip_password", password);
                startActivity(i);
                getActivity().finish();
            }
        });

        mDrawerLayout = v.findViewById(R.id.register_sip_account_fragment);

        ImageButton mImageView = v.findViewById(R.id.menu_button);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(GravityCompat.END)) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        // Register SIP Account
        register_sip_browser(v);

        // Side-menu function
        navigator_drawer_function(v);

        return v;
    }

    private void navigator_drawer_function(View v) {
        LinearLayout to_home_activity = v.findViewById(R.id.to_home_page);
        to_home_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), vn.edu.usth.connect.MainActivity.class);
                startActivity(i);
            }
        });

        LinearLayout to_schedule_activity = v.findViewById(R.id.to_schedule_page);
        to_schedule_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), vn.edu.usth.connect.Schedule.Schedule_Activity.class);
                startActivity(i);
            }
        });

        LinearLayout to_campus_activity = v.findViewById(R.id.to_map_page);
        to_campus_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), vn.edu.usth.connect.Campus.Campus_Activity.class);
                startActivity(i);
            }
        });

        LinearLayout to_resource_activity = v.findViewById(R.id.to_resource_page);
        to_resource_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), vn.edu.usth.connect.Resource.CategoryRecyclerView.CategoryActivity.class);
                startActivity(i);
            }
        });

        LinearLayout to_study_activity = v.findViewById(R.id.to_study_page);
        to_study_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), vn.edu.usth.connect.StudyBuddy.Study_Buddy_Activity.class);
                startActivity(i);
            }
        });

        LinearLayout logout = v.findViewById(R.id.to_log_out);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment loginFragment = new LoginFragment();
                FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(android.R.id.content, loginFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

    }

    private void register_sip_browser(View v){
        TextView register_link = v.findViewById(R.id.register_sip_account);
        register_link.setOnClickListener(view -> {
            String url = "https://subscribe.linphone.org/register/email";
            try {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
            catch (Exception e){
                Toast.makeText(requireContext(), "Can't open browser, check connection", Toast.LENGTH_SHORT).show();
            }
        });

    }
}