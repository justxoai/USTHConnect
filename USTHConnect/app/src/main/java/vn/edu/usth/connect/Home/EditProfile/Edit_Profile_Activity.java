package vn.edu.usth.connect.Home.EditProfile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import vn.edu.usth.connect.MainActivity;
import vn.edu.usth.connect.Models.Student.Student;
import vn.edu.usth.connect.Models.Student.StudentDTO;
import vn.edu.usth.connect.Network.RetrofitClient;
import vn.edu.usth.connect.Network.StudentService;
import vn.edu.usth.connect.R;

public class Edit_Profile_Activity extends AppCompatActivity {

    private ImageView avatar_profile_image;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile);

        update_picture();

        setup_function();
    }

    private void setup_function(){
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(View ->{
            onBackPressed();
        });

        Button cancel_button = findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(View ->{
            finish();
        });

        Button updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(view -> updateStudentProfile());


        Button change_picture = findViewById(R.id.change_picture_button);
        change_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Edit_Profile_Activity.this, ChangePicture_Activity.class);
                startActivity(i);
            }
        });

    }

    // Function to edit and update the student's profile
    private void updateStudentProfile() {
        // Get token from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("ToLogin", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("Token", "");
        String studentId = sharedPreferences.getString("StudentId", "");

        if (token.isEmpty() || studentId.isEmpty()) {
            Toast.makeText(this, "Invalid token or student ID", Toast.LENGTH_SHORT).show();
            return;
        }

        String updatedPassword = ((EditText) findViewById(R.id.edit_password)).getText().toString();
        String updatedPhone = ((EditText) findViewById(R.id.edit_pNumber)).getText().toString();

        // Update and prepare the Student Body
        StudentDTO updatedStudent = new StudentDTO();
        updatedStudent.setPassword(updatedPassword);
        updatedStudent.setPhone(updatedPhone);

        // Make the API call
        StudentService studentService = RetrofitClient.getInstance().create(StudentService.class);
        Call<Student> call = studentService.updateStudentProfile("Bearer " + token, studentId, updatedStudent);
        call.enqueue(new Callback<Student>() {
            @Override
            public void onResponse(Call<Student> call, Response<Student> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        Toast.makeText(Edit_Profile_Activity.this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to the previous screen
                    } else {
                        Log.e("UpdateProfile", "Response body is null. Code: " + response.code() + " Message: " + response.message());
                        Toast.makeText(Edit_Profile_Activity.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e("UpdateProfile", "Update failed. Response Code: " + response.code() + " Message: " + response.message());
                    Toast.makeText(Edit_Profile_Activity.this, "Update failed: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Student> call, Throwable t) {
                Log.e("UpdateProfile", "Error: " + t.getMessage(), t);
                Toast.makeText(Edit_Profile_Activity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void update_picture(){
        avatar_profile_image = findViewById(R.id.avatar_profile);

        SharedPreferences sharedPreferences = getSharedPreferences("ProfileImage", MODE_PRIVATE);
        String url = sharedPreferences.getString("Image_URL", null);
        if (url != null) {
            new UpdateImage(url).start();
        }

        Button update_button = findViewById(R.id.update_button);
        update_button.setOnClickListener(view -> {
            SharedPreferences sharedPreferences2 = getSharedPreferences("ProfileImage", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences2.edit();

            editor.putString("Image_URL", url);
            editor.apply();

            Intent i = new Intent(Edit_Profile_Activity.this, MainActivity.class);
            startActivity(i);
            finish();
        });

    }

    class UpdateImage extends Thread {
        private String url;
        private Bitmap bitmap;

        public UpdateImage(String url) {
            this.url = url;
        }

        @Override
        public void run() {
            try {
                URL imageUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection) imageUrl.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                bitmap = BitmapFactory.decodeStream(inputStream);
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.post(() -> {
                if (bitmap != null) {
                    avatar_profile_image.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(Edit_Profile_Activity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}