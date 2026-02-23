package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

public class AdminActivity extends AppCompatActivity {

    private static final String PRE_DECLARED_USER = "admin@allersafe.ai";
    private static final String PRE_DECLARED_PASS = "NeuralAccess2024";

    private EditText etAdmin, etPassword;
    private MaterialButton btnLogin;
    private ImageButton btnBack;
    private TextView tvReturn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnBack = findViewById(R.id.btnBack);
        etAdmin = findViewById(R.id.etAdmin);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvReturn = findViewById(R.id.tvReturn);

        btnBack.setOnClickListener(v -> finish());
        tvReturn.setOnClickListener(v -> finish());

        btnLogin.setOnClickListener(v -> validateAndLogin());
    }

    private void validateAndLogin() {

        String inputUser = etAdmin.getText().toString().trim();
        String inputPass = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(inputUser)) {
            etAdmin.setError("Please enter Admin ID");
            etAdmin.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(inputPass)) {
            etPassword.setError("Please enter password");
            etPassword.requestFocus();
            return;
        }

        if (inputUser.equals(PRE_DECLARED_USER) && inputPass.equals(PRE_DECLARED_PASS)) {

            Toast.makeText(this,
                    "Access Granted. Welcome Administrator.",
                    Toast.LENGTH_LONG).show();

            // ✅ Correct navigation
            Intent intent = new Intent(AdminActivity.this, AdminDashboard.class);
            startActivity(intent);
            finish(); // close login only

        } else if (!inputUser.equals(PRE_DECLARED_USER)) {

            etAdmin.setError("Invalid Admin ID");
            Toast.makeText(this,
                    "Unauthorized access attempt logged.",
                    Toast.LENGTH_SHORT).show();

        } else {

            etPassword.setError("Incorrect Password");
            Toast.makeText(this,
                    "Security Alert: Incorrect password entered.",
                    Toast.LENGTH_SHORT).show();
        }
    }
}

