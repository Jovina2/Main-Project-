package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class RecommendationActivity extends AppCompatActivity {

    TextView txtGreeting;
    EditText searchFood;
    RecyclerView recyclerRecommendations;

    RecommendationAdapter adapter;
    ArrayList<RecommendItem> recommendationList = new ArrayList<>();

    int userId;
    String username;
    String scannedFood;
    ArrayList<String> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        // Bind UI
        txtGreeting = findViewById(R.id.txtGreeting);
        searchFood = findViewById(R.id.searchFood);
        recyclerRecommendations = findViewById(R.id.recyclerRecommendations);

        recyclerRecommendations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecommendationAdapter(recommendationList, this);
        recyclerRecommendations.setAdapter(adapter);

        // Get data from previous screen
        Intent intent = getIntent();
        userId = intent.getIntExtra("user_id", -1);
        username = intent.getStringExtra("username");
        scannedFood = intent.getStringExtra("food_name");

        ingredients = intent.getStringArrayListExtra("ingredients");

        if (username == null || username.isEmpty()) {
            username = "User";
        }

        txtGreeting.setText("Hi " + username + ",\nHere are your safe picks.");

        if (userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (ingredients != null && scannedFood != null) {
            callRecommendationAPI();
        }
    }

    // ---------------------------------------------------
    // CALL AI SMART BACKEND
    // ---------------------------------------------------
    private void callRecommendationAPI() {

        String url = "http://192.168.1.44:5000/recommend";

        RequestQueue queue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();

        try {
            json.put("user_id", userId);
            json.put("food_name", scannedFood);

            JSONArray ingArray = new JSONArray();
            for (String ing : ingredients) {
                ingArray.put(ing);
            }

            json.put("ingredients", ingArray);

        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                url,
                json,
                this::handleResponse,
                error -> Toast.makeText(this, "Failed to load recommendations!", Toast.LENGTH_LONG).show()
        );

        queue.add(request);
    }

    // ---------------------------------------------------
    // HANDLE BACKEND RESPONSE
    // ---------------------------------------------------
    private void handleResponse(JSONObject response) {

        try {

            recommendationList.clear();

            // SAFE RECOMMENDATIONS
            JSONArray safeFoods = response.optJSONArray("safe_foods");

            if (safeFoods != null && safeFoods.length() > 0) {

                for (int i = 0; i < safeFoods.length(); i++) {

                    JSONObject obj = safeFoods.optJSONObject(i);

                    if (obj != null) {
                        String name = obj.optString("name", "Unknown");
                        int confidence = obj.optInt("confidence", 0);

                        recommendationList.add(
                                new RecommendItem(
                                        name,
                                        "Safe Pick ✅",
                                        confidence,
                                        false
                                )
                        );
                    }
                }

            } else {
                // ✅ Show empty message instead of crash
                recommendationList.add(
                        new RecommendItem(
                                "No safe alternatives found",
                                "Try another food",
                                0,
                                false
                        )
                );
            }

            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error parsing recommendations", Toast.LENGTH_SHORT).show();
        }
    }
}
