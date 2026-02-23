package com.example.project;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class PredictionResult extends AppCompatActivity {

    // UI Components
    TextView txtRisk, txtMessage, txtConfidence;
    TextView txtTriggerName, txtTriggerLevel, txtPersonalized;
    ImageView foodImage;
    ProgressBar progressConfidence;
    Button btnReport, btnScanAgain;

    int userId = -1;
    Bitmap selectedBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prediction_result);

        // ---------------- Bind UI ----------------
        txtRisk = findViewById(R.id.txtRisk);
        txtMessage = findViewById(R.id.txtMessage);
        txtConfidence = findViewById(R.id.txtConfidence);
        txtTriggerName = findViewById(R.id.txtTriggerName);
        txtTriggerLevel = findViewById(R.id.txtTriggerLevel);
        txtPersonalized = findViewById(R.id.txtPersonalized);

        foodImage = findViewById(R.id.foodImage);
        progressConfidence = findViewById(R.id.progressConfidence);

        btnReport = findViewById(R.id.btnReport);
        btnScanAgain = findViewById(R.id.btnScanAgain);

        // ---------------- Receive Intent ----------------
        Intent intent = getIntent();

        String foodName = intent.getStringExtra("foodName");
        String barcode = intent.getStringExtra("barcode");
        String username = intent.getStringExtra("username");

        if (username == null) username = "User";

        userId = intent.getIntExtra("user_id", -1);

        txtPersonalized.setText("Prediction personalized for " + username);

        // Default UI
        txtRisk.setText("Processing...");
        txtMessage.setText("Please wait...");
        txtConfidence.setText("0%");
        progressConfidence.setProgress(0);

        // ---------------- Load Image if available ----------------
        String imageUriString = intent.getStringExtra("imageUri");

        if (imageUriString != null) {
            try {
                Uri imageUri = Uri.parse(imageUriString);

                selectedBitmap = MediaStore.Images.Media.getBitmap(
                        this.getContentResolver(), imageUri);

                foodImage.setImageBitmap(selectedBitmap);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image!", Toast.LENGTH_SHORT).show();
            }
        }

        // ---------------- Validate userId ----------------
        if (userId == -1) {
            showError("User ID Missing!");
            return;
        }

        // ---------------- Priority Order ----------------
        if (selectedBitmap != null) {
            txtMessage.setText("Analyzing uploaded image...");
            callPredictionAPIWithImage(selectedBitmap, userId);

        } else if (barcode != null && !barcode.trim().isEmpty()) {
            txtMessage.setText("Looking up barcode product...");
            callBarcodeAPI(barcode.trim(), userId);

        } else if (foodName != null && !foodName.trim().isEmpty()) {
            txtMessage.setText("Analyzing food: " + foodName);
            callPredictionAPI(foodName.trim(), userId);

        } else {
            showError("No input received!");
        }

        // ---------------- Scan Again ----------------
        btnScanAgain.setOnClickListener(v -> {
            startActivity(new Intent(this, ScannerActivity.class));
            finish();
        });

        // ---------------- Report Button ----------------
        btnReport.setOnClickListener(v ->
                Toast.makeText(this, "Report submitted successfully!", Toast.LENGTH_SHORT).show()
        );
    }

    // ---------------- Show Error ----------------
    private void showError(String message) {
        txtRisk.setText("ERROR");
        txtMessage.setText(message);
        txtConfidence.setText("0%");
        progressConfidence.setProgress(0);

        txtTriggerName.setText("None");
        txtTriggerLevel.setText("");

        foodImage.setImageResource(R.drawable.ic_food);

        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    // ---------------- Text Prediction ----------------
    private void callPredictionAPI(String foodName, int userId) {

        String url = "http://192.168.1.208:5000/predict";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();
        try {
            json.put("foodName", foodName);
            json.put("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                this::handleResponse,
                error -> showError("Prediction Failed!\n" + error.toString())
        );

        // ✅ Timeout Fix
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                2,
                1f
        ));

        queue.add(request);
    }

    // ---------------- Image Prediction ----------------
    private void callPredictionAPIWithImage(Bitmap bitmap, int userId) {

        String url = "http://192.168.1.208:5000/predict_image";

        RequestQueue queue = Volley.newRequestQueue(this);

        // ✅ Resize Image Before Upload
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

        JSONObject json = new JSONObject();
        try {
            json.put("imageBase64", bitmapToBase64(resized));
            json.put("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                this::handleResponse,
                error -> showError("Image Prediction Failed!\n" + error.toString())
        );

        // ✅ Timeout Fix
        request.setRetryPolicy(new DefaultRetryPolicy(
                25000,
                2,
                1f
        ));

        queue.add(request);
    }

    // ---------------- Barcode Lookup ----------------
    private void callBarcodeAPI(String barcode, int userId) {

        String url = "http://192.168.1.208:5000/barcode_lookup";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();
        try {
            json.put("barcode", barcode);
            json.put("user_id", userId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST, url, json,
                this::handleResponse,
                error -> showError("Barcode Lookup Failed!\n" + error.toString())
        );

        // ✅ Timeout Fix
        request.setRetryPolicy(new DefaultRetryPolicy(
                20000,
                2,
                1f
        ));

        queue.add(request);
    }

    // ---------------- Bitmap → Base64 ----------------
    private String bitmapToBase64(Bitmap bitmap) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        bitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream);

        byte[] bytes = outputStream.toByteArray();

        // ✅ NO_WRAP Fix (Removes newline issue)
        return Base64.encodeToString(bytes, Base64.NO_WRAP);
    }

    // ---------------- Handle Backend Response ----------------
    private void handleResponse(JSONObject response) {

        try {
            // Fail Case
            if (response.has("status") &&
                    response.getString("status").equalsIgnoreCase("fail")) {

                showError(response.optString("message", "Unknown Error"));
                return;
            }

            // Extract Values
            String risk = response.optString("risk", "UNKNOWN");

            int probability = response.optInt("probability", 0);

            int confidence = response.optInt("confidence", probability);

            // Trigger Extraction
            String triggerText = "None";

            if (response.has("trigger")) {
                Object triggerObj = response.get("trigger");

                if (triggerObj instanceof JSONArray) {

                    JSONArray arr = (JSONArray) triggerObj;

                    StringBuilder sb = new StringBuilder();

                    for (int i = 0; i < arr.length(); i++) {
                        sb.append(arr.getString(i)).append(", ");
                    }

                    triggerText = sb.length() > 2
                            ? sb.substring(0, sb.length() - 2)
                            : "None";
                }
            }

            // Food Name
            String foodName = response.optString(
                    "foodName",
                    response.optString("foodDetected", "Unknown Food")
            );

            txtMessage.setText("Food: " + foodName);

            // Update UI
            txtRisk.setText(risk);
            txtConfidence.setText(confidence + "%");

            progressConfidence.setProgress(confidence);

            txtTriggerName.setText(triggerText);

            // Risk Message
            if (risk.equalsIgnoreCase("SAFE")) {

                txtTriggerLevel.setText("No Allergy Risk ✅");

            } else if (risk.contains("MODERATE")) {

                txtTriggerLevel.setText("⚠ Moderate Allergy Risk!");

            } else {

                txtTriggerLevel.setText("🚨 HIGH Allergy Risk!");
            }

            // Load Product Image if URL Exists
            String imageUrl = response.optString("imageUrl", "");

            if (!imageUrl.isEmpty()) {
                Glide.with(this)
                        .load(imageUrl)
                        .placeholder(R.drawable.ic_food)
                        .into(foodImage);
            }

        } catch (Exception e) {
            e.printStackTrace();
            showError("Response Parsing Failed!");
        }
    }
}
