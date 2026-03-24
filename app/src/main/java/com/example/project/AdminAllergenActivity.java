package com.example.project;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminAllergenActivity extends AppCompatActivity {

    RecyclerView recyclerAllergens;

    ArrayList<AllergyModel> allergyList;
    AllergenAdapter adapter;

    String URL_FETCH = "http://192.168.1.44:5000/admin/allergy_database";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_allergen);

        recyclerAllergens = findViewById(R.id.recyclerAllergens);

        recyclerAllergens.setLayoutManager(new LinearLayoutManager(this));

        allergyList = new ArrayList<>();
        adapter = new AllergenAdapter(this, allergyList);

        recyclerAllergens.setAdapter(adapter);

        // ✅ Load allergy profile records from database
        loadAllergyDatabase();
    }

    private void loadAllergyDatabase() {

        JsonObjectRequest request = new JsonObjectRequest(
                URL_FETCH,
                null,
                response -> {
                    try {
                        allergyList.clear();

                        if (response.getString("status").equals("success")) {

                            JSONArray allergies = response.getJSONArray("allergies");

                            for (int i = 0; i < allergies.length(); i++) {

                                JSONObject obj = allergies.getJSONObject(i);

                                String userId = obj.getString("user_id");
                                String allergyName = obj.getString("allergy_name");
                                String severity = obj.getString("severity");
                                String remarks = obj.optString("remarks", "-");

                                allergyList.add(new AllergyModel(
                                        userId,
                                        allergyName,
                                        severity,
                                        remarks
                                ));
                            }

                            adapter.notifyDataSetChanged();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error loading allergy data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // ✅ Refresh whenever admin opens this page again
        loadAllergyDatabase();
    }
}
