package vn.edu.usth.connect.Home.EditProfile;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.edu.usth.connect.R;

public class ChangePicture_Activity extends AppCompatActivity {

    private EditText url_image;
    private ImageView avatar_profile_image;
    private Button save_button, confirm_button;
    private Handler handler = new Handler();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Activity_change_picture.xml
        setContentView(R.layout.activity_change_picture);

        // Function to load image from url
        // And Save Button to load Image and Refresh
        // again to show Profile's Image
        load_image();

        // Button Function: Cancel Button and Back Button
        setup_function();
    }

    private void load_image(){
        // LoadImage
        url_image = findViewById(R.id.image_url);
        avatar_profile_image = findViewById(R.id.avatar_profile);

        save_button = findViewById(R.id.save_button);
        confirm_button = findViewById(R.id.load_image_button);

        confirm_button.setOnClickListener(view -> {
            String url = url_image.getText().toString();
            if (!url.isEmpty()) {
                new LoadImage(url).start();
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show();
            }
        });

        Button save_button = findViewById(R.id.save_button);
        save_button.setOnClickListener(view -> {
            String url = url_image.getText().toString();
            if (!url.isEmpty()) {

                SharedPreferences sharedPreferences = getSharedPreferences("ProfileImage", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Image_URL", url);
                editor.apply();

                Intent intent = new Intent(ChangePicture_Activity.this, Edit_Profile_Activity.class);
                startActivity(intent);
                finish();

            } else {
                onBackPressed();
            }
        });

    }

    private void setup_function() {
        ImageButton back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(view -> {
            onBackPressed();
        });

        Button cancel_button = findViewById(R.id.cancel_button);
        cancel_button.setOnClickListener(view ->{
            onBackPressed();
        });

    }

    class LoadImage extends Thread {
        private String url;
        private Bitmap bitmap;

        public LoadImage(String url) {
            this.url = url;
        }

        @Override
        public void run() {
           handler.post(() -> {
                progressDialog = new ProgressDialog(ChangePicture_Activity.this);
                progressDialog.setMessage("Loading image...");
                progressDialog.setCancelable(false);
                progressDialog.show();
            });

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
                if (progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                if (bitmap != null) {
                    avatar_profile_image.setImageBitmap(bitmap);
                } else {
                    Toast.makeText(ChangePicture_Activity.this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}