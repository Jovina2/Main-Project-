package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class DangerAlertActivity extends AppCompatActivity {

    TextView txtFood, txtRisk, txtAllergens, txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_danger_alert);

        txtFood = findViewById(R.id.txtFood);
        txtRisk = findViewById(R.id.txtRisk);
        txtAllergens = findViewById(R.id.txtAllergens);
        txtMessage = findViewById(R.id.txtMessage);

        Button btnClose = findViewById(R.id.btnClose);
        Button btnRecommendation = findViewById(R.id.btnRecommendation);

        Intent intent = getIntent();

        String foodName = intent.getStringExtra("foodName");
        String risk = intent.getStringExtra("risk");
        String allergens = intent.getStringExtra("allergens");
        String message = intent.getStringExtra("message");

        int userId = intent.getIntExtra("user_id", -1);

        txtFood.setText("Food Item: " + foodName);
        txtRisk.setText("Risk Level: " + risk);
        txtAllergens.setText("Detected Allergens: " + allergens);
        txtMessage.setText(message);

        findViewById(R.id.btnClose).setOnClickListener(v -> finish());

        // 🟢 View Recommendation Button
        findViewById(R.id.btnRecommendation).setOnClickListener(v -> {

            Intent recIntent = new Intent(DangerAlertActivity.this, RecommendationActivity.class);

            recIntent.putExtra("user_id",userId);
            recIntent.putExtra("food_name", foodName);

            ArrayList<String> ingredientList = new ArrayList<>();

            if (allergens != null && !allergens.equals("None")) {
                String[] parts = allergens.split(",");
                for (String part : parts) {
                    ingredientList.add(part.trim());
                }
            }

            recIntent.putStringArrayListExtra("ingredients", ingredientList);

            startActivity(recIntent);
        });
    }
}