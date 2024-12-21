package vn.edu.usth.connect.StudyBuddy.Welcome;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import vn.edu.usth.connect.R;

public class NameFragment extends Fragment {

    // Get Student Name to show in Study Buddy
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // fragment_name.xml
        View v = inflater.inflate(R.layout.fragment_name, container, false);

        // Button and EditText Function
        setup_function(v);

        return v;
    }

    private void setup_function(View v){
        ImageButton back_button = v.findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        Button next_button = v.findViewById(R.id.next_button);
        next_button.setEnabled(false);
        next_button.setOnClickListener(view -> {
            navigatorToNextFragment();
        });


        EditText editName = v.findViewById(R.id.input_name);
        editName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String name = editName.getText().toString();
                if(name.isEmpty()){
                    next_button.setEnabled(false);
                }else{
                    next_button.setEnabled(true);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    // Move to next Fragment
    private void navigatorToNextFragment(){
        Fragment genderFragment = new GenderFragment();
        FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(android.R.id.content, genderFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
}