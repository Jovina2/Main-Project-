package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import org.json.JSONObject;

public class AdminDashboard extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    // TextViews for live stats
    TextView txtActiveUsersCount, txtAlertsTodayCount;

    private final Handler handler = new Handler(); // For auto-refresh

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.admin_dashboard);

        // Card Clicks
        findViewById(R.id.cardFood).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, FoodTable.class);
            startActivity(intent);
        });

        findViewById(R.id.cardAllergen).setOnClickListener(v -> {
            Intent intent = new Intent(AdminDashboard.this, AdminAllergenActivity.class);
            startActivity(intent);
        });

        // Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.nav_dashboard);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_dashboard) return true;
            else if (id == R.id.nav_database) return true;
            else if (id == R.id.nav_users) return true;
            else if (id == R.id.nav_settings) return true;
            return false;
        });

        // Bind live stats TextViews
        txtActiveUsersCount = findViewById(R.id.tvActiveUsers);
        txtAlertsTodayCount = findViewById(R.id.tvAlerts);

        // Fetch stats initially
        fetchDashboardStats();

        // Auto-refresh every 30 seconds
        startAutoRefresh();
    }

    private void fetchDashboardStats() {
        String url = "http://192.168.1.208:5000/admin/dashboard_stats"; // Flask endpoint

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                response -> {
                    try {
                        if (response.getString("status").equalsIgnoreCase("success")) {
                            int activeUsers = response.getInt("active_users");
                            int alertsToday = response.getInt("alerts_today");

                            txtActiveUsersCount.setText(String.valueOf(activeUsers));
                            txtAlertsTodayCount.setText(String.valueOf(alertsToday));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        txtActiveUsersCount.setText("-");
                        txtAlertsTodayCount.setText("-");
                    }
                },
                error -> {
                    txtActiveUsersCount.setText("-");
                    txtAlertsTodayCount.setText("-");
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void startAutoRefresh() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                fetchDashboardStats();
                handler.postDelayed(this, 30000); // refresh every 30 seconds
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null); // Stop auto-refresh
    }
}
