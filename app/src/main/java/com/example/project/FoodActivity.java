package com.example.project;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class FoodActivity extends AppCompatActivity {

    ImageView btnBack, btnSearch;
    EditText etFoodName;
    Button btnAnalyze;
    TextView btnClear;

    // Recent chips
    TextView chip1, chip2, chip3;

    // ✅ Store user details
    int userId = -1;
    String username = "User";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_name);

        // ✅ Initialize Views
        btnBack = findViewById(R.id.btnBack);
        btnSearch = findViewById(R.id.btnSearch);
        etFoodName = findViewById(R.id.etFoodName);
        btnAnalyze = findViewById(R.id.btnAnalyze);
        btnClear = findViewById(R.id.btnClear);

        chip1 = findViewById(R.id.chipPadThai);
        chip2 = findViewById(R.id.chipAlmondMilk);
        chip3 = findViewById(R.id.chipCaesarSalad);

        // ✅ Receive user_id + username
        userId = getIntent().getIntExtra("user_id", -1);
        username = getIntent().getStringExtra("username");
        if (username == null) username = "User";

        // ✅ Back button
        btnBack.setOnClickListener(v -> finish());

        // ✅ Search + Analyze button
        btnSearch.setOnClickListener(v -> analyzeFood());
        btnAnalyze.setOnClickListener(v -> analyzeFood());

        // ✅ Chip clicks
        chip1.setOnClickListener(v -> etFoodName.setText(chip1.getText().toString()));
        chip2.setOnClickListener(v -> etFoodName.setText(chip2.getText().toString()));
        chip3.setOnClickListener(v -> etFoodName.setText(chip3.getText().toString()));

        // ✅ Clear chips
        btnClear.setOnClickListener(v -> {
            chip1.setVisibility(View.GONE);
            chip2.setVisibility(View.GONE);
            chip3.setVisibility(View.GONE);

            Toast.makeText(this, "Recent checks cleared", Toast.LENGTH_SHORT).show();
        });
    }

    // ✅ Backend AI Analyze Function
    private void analyzeFood() {

        String foodName = etFoodName.getText().toString().trim();

        if (TextUtils.isEmpty(foodName)) {
            Toast.makeText(this, "Please enter a food name", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Analyzing " + foodName + "...", Toast.LENGTH_SHORT).show();

        // ✅ Update Recent Chips dynamically
        updateRecentChecks(foodName);

        // ✅ Flask Backend URL
        String url = "http://192.168.1.44:5000/predict";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("foodName", foodName);
            jsonBody.put("user_id", userId); // optional
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                jsonBody,

                response -> {
                    try {
                        String risk = response.getString("risk");
                        double probability = response.getDouble("probability");
                        String trigger = response.getString("trigger");

                        // ✅ Open PredictionResult Page
                        Intent intent = new Intent(FoodActivity.this, PredictionResult.class);

                        intent.putExtra("foodName", foodName);
                        intent.putExtra("risk", risk);
                        intent.putExtra("probability", probability);
                        intent.putExtra("trigger", trigger);

                        // ✅ MOST IMPORTANT FIX
                        intent.putExtra("username", username);
                        intent.putExtra("user_id", userId);

                        startActivity(intent);

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Response Error!", Toast.LENGTH_SHORT).show();
                    }
                },

                error -> Toast.makeText(this,
                        "Backend Error: " + error.getMessage(),
                        Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }

    // ✅ Recent Checks Update Function
    private void updateRecentChecks(String newFood) {

        chip3.setText(chip2.getText().toString());
        chip2.setText(chip1.getText().toString());

        chip1.setText(newFood);

        chip1.setVisibility(View.VISIBLE);
        chip2.setVisibility(View.VISIBLE);
        chip3.setVisibility(View.VISIBLE);
    }
}
