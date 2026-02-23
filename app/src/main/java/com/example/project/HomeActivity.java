package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    int user_id; // ✅ Declare globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // ✅ Receive user_id from LoginActivity
        user_id = getIntent().getIntExtra("user_id", -1);

        if (user_id == -1) {
            Toast.makeText(this, "User ID Missing!", Toast.LENGTH_LONG).show();
        }

        // Welcome user
        TextView txtWelcome = findViewById(R.id.txtWelcome);
        String userName = getIntent().getStringExtra("name");

        if (userName != null && !userName.isEmpty()) {
            txtWelcome.setText("Welcome, " + userName + " 👋");
        } else {
            txtWelcome.setText("Welcome 👋");
        }

        // ✅ Enter food name
        findViewById(R.id.btnEnterFood).setOnClickListener(v -> {
            Intent intent = new Intent(this, FoodActivity.class);
            intent.putExtra("user_id", user_id); // ✅ Pass user_id
            startActivity(intent);
        });

        // ✅ AI Barcode Scanner
        findViewById(R.id.btnScanner).setOnClickListener(v -> {
            Intent intent = new Intent(this, ScannerActivity.class);
            intent.putExtra("user_id", user_id); // ✅ Pass user_id
            startActivity(intent);
        });

        // ✅ Camera / Gallery Image Scanner
        findViewById(R.id.btnCamera).setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageActivity.class);
            intent.putExtra("user_id", user_id); // ✅ Pass user_id
            startActivity(intent);
        });
    }
}
