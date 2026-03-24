package com.example.project;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.util.ArrayList;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

public class HistoryActivity extends AppCompatActivity {

    ListView listView;
    ArrayList<JSONObject> historyList;
    HistoryAdapter adapter;
    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        listView = findViewById(R.id.historyListView);
        historyList = new ArrayList<>();

        adapter = new HistoryAdapter(this, historyList);
        listView.setAdapter(adapter);

        // ✅ Get user_id from previous activity
        userId = getIntent().getIntExtra("user_id", -1);

        loadHistory();
    }

    private void loadHistory() {

        String url = "http://192.168.1.44:5000/get_history?user_id=" + userId;

        JsonArrayRequest request = new JsonArrayRequest(url,
                response -> {
                    try {

                        historyList.clear();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject obj = response.getJSONObject(i);
                            historyList.add(obj);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error loading history", Toast.LENGTH_SHORT).show());

        Volley.newRequestQueue(this).add(request);
    }
}