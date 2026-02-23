package com.example.project;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class ImageActivity extends AppCompatActivity {

    private ImageView imgPreview, btnBack;
    private Button btnCamera, btnGallery, btnAnalyze;
    private Uri imageUri; // stores the selected/captured image
    private int user_id = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_upload);

        imgPreview = findViewById(R.id.imgPreview);
        btnCamera = findViewById(R.id.btnCamera);
        btnGallery = findViewById(R.id.btnGallery);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v -> finish());

        btnCamera.setOnClickListener(v -> openCamera());
        btnGallery.setOnClickListener(v -> openGallery());

        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "User ID Missing!", Toast.LENGTH_LONG).show();
        }
        // Analyze button click
        btnAnalyze.setOnClickListener(v -> {
            if (imageUri == null) {
                Toast.makeText(this, "Please upload an image first", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(ImageActivity.this, PredictionResult.class);
                intent.putExtra("imageUri", imageUri.toString());
                intent.putExtra("source", "image"); // optional
                intent.putExtra("user_id", user_id);
                startActivity(intent);
            }
        });
    }

    private void openCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.CAMERA},
                    101
            );
            return;
        }
        cameraLauncher.launch(new Intent(MediaStore.ACTION_IMAGE_CAPTURE));
    }

    private void openGallery() {
        galleryLauncher.launch(
                new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        );
    }

    // Camera result
    private final ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            Bitmap capturedBitmap = (Bitmap) result.getData().getExtras().get("data");
                            imgPreview.setImageBitmap(capturedBitmap);
                            imgPreview.setVisibility(ImageView.VISIBLE);

                            // Save captured image as URI
                            imageUri = Uri.parse(
                                    MediaStore.Images.Media.insertImage(
                                            getContentResolver(),
                                            capturedBitmap,
                                            "camera_image",
                                            null
                                    )
                            );
                        }
                    });

    // Gallery result
    private final ActivityResultLauncher<Intent> galleryLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                            imageUri = result.getData().getData();
                            try {
                                Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                        this.getContentResolver(), imageUri);
                                imgPreview.setImageBitmap(bitmap);
                                imgPreview.setVisibility(ImageView.VISIBLE);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
}
