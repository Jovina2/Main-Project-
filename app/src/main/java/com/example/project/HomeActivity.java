package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ListView;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    ListView recentListView;
    ArrayList<JSONObject> recentList;
    HistoryAdapter adapter;

    int user_id; // ✅ Declare globally

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        recentListView = findViewById(R.id.recentListView);
        recentList = new ArrayList<>();

        adapter = new HistoryAdapter(this, recentList);
        recentListView.setAdapter(adapter);

// ✅ Load recent history
        loadRecentHistory();

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

        findViewById(R.id.nav_history).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, HistoryActivity.class);
            intent.putExtra("user_id", user_id);
            startActivity(intent);
        });

        findViewById(R.id.profileImage).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            intent.putExtra("name", userName); // ✅ correct key
            intent.putExtra("email", getIntent().getStringExtra("email"));
            startActivity(intent);
        });


    }
    private void loadRecentHistory() {

        String url = "http://192.168.1.44:5000/get_history?user_id=" + user_id;

        JsonArrayRequest request = new JsonArrayRequest(url,
                response -> {
                    try {

                        recentList.clear();

                        // ✅ Show only last 3 items (recent)
                        for (int i = 0; i < Math.min(3, response.length()); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            recentList.add(obj);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Failed to load recent activity", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
