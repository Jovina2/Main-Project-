package com.example.project;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edtEmail, edtNewPassword, edtConfirmPassword;
    Button btnUpdatePassword;

    private final String RESET_PASSWORD_URL = "http://192.168.1.44:5000/reset-password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        // Initialize views
        edtEmail = findViewById(R.id.edtEmail);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnUpdatePassword = findViewById(R.id.btnUpdatePassword);

        // Button click listener
        btnUpdatePassword.setOnClickListener(v -> resetPassword());
    }

    private void resetPassword() {
        String email = edtEmail.getText().toString().trim();
        String newPass = edtNewPassword.getText().toString().trim();
        String confirmPass = edtConfirmPassword.getText().toString().trim();

        // Validation
        if (email.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        // Prepare JSON body
        JSONObject body = new JSONObject();
        try {
            body.put("email", email);
            body.put("new_password", newPass);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error preparing request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Volley request
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                RESET_PASSWORD_URL,
                body,
                response -> {
                    Log.d("ResetResponse", response.toString());

                    // Success message
                    Toast.makeText(this, "Password updated successfully", Toast.LENGTH_SHORT).show();

                    // ✅ Go back to LoginActivity
                    finish(); // LoginActivity is still in back stack
                },
                error -> {
                    Log.e("ResetError", error.toString());
                    Toast.makeText(this, "Failed to update password. Try again.", Toast.LENGTH_SHORT).show();
                }
        );

        // Add request to queue
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Optional: Override back press if you want custom behavior
    // @Override
    // public void onBackPressed() {
    //     super.onBackPressed(); // Default: goes back to LoginActivity
    // }
}
