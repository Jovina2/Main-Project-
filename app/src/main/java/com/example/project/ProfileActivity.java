package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ProfileActivity extends AppCompatActivity {

    TextView tvUserName;
    EditText etFullName, etEmail;
    Switch switchAlerts, switchSummary;
    Button btnSave, btnLogout;
    ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // CONNECT XML VIEWS
        tvUserName = findViewById(R.id.tvUserName);
        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etEmail);
        switchAlerts = findViewById(R.id.switchAlerts);
        switchSummary = findViewById(R.id.switchSummary);
        btnSave = findViewById(R.id.btnSave);
        btnLogout = findViewById(R.id.btnLogout);
        btnBack = findViewById(R.id.btnBack);

        // ✅ GET DATA FROM INTENT (IMPORTANT FIX)
        Intent intent = getIntent();

        String userName = intent.getStringExtra("user_name");
        String email = intent.getStringExtra("email");

        // ✅ SET AUTOMATICALLY WHEN OPENING SCREEN
        if (userName != null) {
            tvUserName.setText(userName);
            etFullName.setText(userName);
        }

        if (email != null) {
            etEmail.setText(email);
        }

        // BACK BUTTON
        btnBack.setOnClickListener(v -> finish());

        // SAVE PROFILE
        btnSave.setOnClickListener(v -> {

            String name = etFullName.getText().toString().trim();
            String userEmail = etEmail.getText().toString().trim();

            if (name.isEmpty() || userEmail.isEmpty()) {
                Toast.makeText(ProfileActivity.this, "Fill all fields", Toast.LENGTH_SHORT).show();
            } else {
                tvUserName.setText(name);
                Toast.makeText(ProfileActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
            }
        });

        // ALERT SWITCH
        switchAlerts.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked)
                Toast.makeText(ProfileActivity.this, "High Risk Alerts Enabled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ProfileActivity.this, "High Risk Alerts Disabled", Toast.LENGTH_SHORT).show();

        });

        // SUMMARY SWITCH
        switchSummary.setOnCheckedChangeListener((buttonView, isChecked) -> {

            if (isChecked)
                Toast.makeText(ProfileActivity.this, "Weekly AI Summary Enabled", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(ProfileActivity.this, "Weekly AI Summary Disabled", Toast.LENGTH_SHORT).show();

        });

        // LOGOUT BUTTON
        btnLogout.setOnClickListener(v -> {

            Intent intent1 = new Intent(ProfileActivity.this, LoginActivity.class);
            startActivity(intent1);
            finish();

        });
    }
}