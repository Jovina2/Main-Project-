package com.example.project;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

public class AdminUserProfileActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    UserAdapter adapter;
    ArrayList<UserModel> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_user_profile); // ✅ IMPORTANT

        recyclerView = findViewById(R.id.recyclerViewUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());

        list = new ArrayList<>();
        adapter = new UserAdapter(list);
        recyclerView.setAdapter(adapter);

        fetchUsers();
    }
    private void fetchUsers() {

        String url = "http://192.168.1.44:5000/admin/user_profiles";

        JsonObjectRequest request = new JsonObjectRequest(url, null,
                response -> {
                    try {
                        if ("success".equalsIgnoreCase(response.optString("status"))) {

                            JSONArray array = response.getJSONArray("users");
                            list.clear();

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);

                                String name = obj.getString("name");
                                String email = obj.getString("email");

                                list.add(new UserModel(name, email));
                            }

                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(this).add(request);
    }
}