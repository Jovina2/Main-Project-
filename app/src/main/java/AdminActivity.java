package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.example.project.R;

public class AdminActivity extends AppCompatActivity {

    // PRE-DECLARED CREDENTIALS
    private static final String PRE_DECLARED_USER = "admin@allersafe.ai";
    private static final String PRE_DECLARED_PASS = "NeuralAccess2024";

    private EditText etAdminId, etPassword;
    private Button btnLogin;
    private ImageButton btnBack;
    private TextView tvReturnToUserApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // Initialize UI components
        btnBack = findViewById(R.id.btnBack);
        etAdminId = findViewById(R.id.etAdminId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvReturnToUserApp = findViewById(R.id.btnReturn);

        // Back button functionality
        btnBack.setOnClickListener(v -> finish());

        // Return to User App functionality
        tvReturnToUserApp.setOnClickListener(v -> finish());

        // Login Button Listener
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndLogin();
            }
        });
    }

    private void validateAndLogin() {
        String inputUser = etAdminId.getText().toString().trim();
        String inputPass = etPassword.getText().toString().trim();

        // 1. Check if fields are empty
        if (TextUtils.isEmpty(inputUser)) {
            etAdminId.setError("Please enter Admin ID");
            etAdminId.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(inputPass)) {
            etPassword.setError("Please enter password");
            etPassword.requestFocus();
            return;
        }

        // 2. Validate against pre-declared credentials
        if (inputUser.equals(PRE_DECLARED_USER) && inputPass.equals(PRE_DECLARED_PASS)) {
            // SUCCESS
            Toast.makeText(this, "Access Granted. Welcome Administrator.", Toast.LENGTH_LONG).show();

            // Logic to move to the next screen (e.g., Dashboard)
            // Intent intent = new Intent(AdminConsoleActivity.this, DashboardActivity.class);
            // startActivity(intent);

        } else if (!inputUser.equals(PRE_DECLARED_USER)) {
            // WRONG USERNAME
            etAdminId.setError("Invalid Admin ID");
            Toast.makeText(this, "Unauthorized access attempt logged.", Toast.LENGTH_SHORT).show();

        } else {
            // WRONG PASSWORD
            etPassword.setError("Incorrect Password");
            Toast.makeText(this, "Security Alert: Incorrect password entered.", Toast.LENGTH_SHORT).show();
        }
    }
}
