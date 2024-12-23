package vn.edu.usth.connect.Login;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.connect.MainActivity;
import vn.edu.usth.connect.Models.AuthResponse;
import vn.edu.usth.connect.Models.LoginRequest;
import vn.edu.usth.connect.Network.AuthService;
import vn.edu.usth.connect.Network.RetrofitClient;
import vn.edu.usth.connect.R;
import vn.edu.usth.connect.Workers.FetchEventsWorker;

public class LoginFragment extends Fragment {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;

    private static final String TAG = "LoginFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        editTextEmail = view.findViewById(R.id.editTextEmail);
        editTextPassword = view.findViewById(R.id.editTextPassword);
        buttonLogin = view.findViewById(R.id.login_button);


        // Remember Login using SharePreference
        buttonLogin.setOnClickListener(v -> {
            String email = editTextEmail.getText().toString();
            String password = editTextPassword.getText().toString();

            if(email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getActivity(), "Please enter email and password", Toast.LENGTH_SHORT).show();
            } else {
                authenticateUser(email, password);
            }
        });

        button_function(view);

        return view;
    }

    private void authenticateUser(String username, String password) {
        AuthService authService = RetrofitClient.getInstance().create(AuthService.class);
        LoginRequest loginRequest = new LoginRequest(username, password);

        authService.login(loginRequest).enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();
                    Log.d(TAG, "Login successful, token received: " + token);

                    // Save the token in SharedPreferences
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("ToLogin", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Save the state and String of the token
                    editor.putBoolean("IsLoggedIn", true);
                    editor.putString("Token", token);
                    editor.putString("StudentId", username);
                    editor.apply();
                    // Redirect to MainActivity
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    try {
                        // Log the server's error response
                        String errorBody = response.errorBody().string();
                        Log.e("LoginError", "Error response: " + errorBody);
                        Toast.makeText(getActivity(), "Invalid credentials: " + errorBody, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(TAG, "Error reading error response", e);
                    }
                }
            }

            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                Toast.makeText(getActivity(), "Login failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void button_function(View view) {
        // Change Password
        LinearLayout forgot_password = view.findViewById(R.id.forgot_password);
        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(requireContext(), Change_Password_Activity.class);
                startActivity(i);
            }
        });
    }

}