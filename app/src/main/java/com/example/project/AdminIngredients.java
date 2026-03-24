package com.example.project;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.util.ArrayList;
public class AdminIngredients extends AppCompatActivity {

    ListView listView;
    ArrayList<String> ingredientsList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ingredients);

        listView = findViewById(R.id.listView);
        ingredientsList = new ArrayList<>();

        adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1,
                ingredientsList);

        listView.setAdapter(adapter);

        loadIngredients();
    }

    private void loadIngredients() {

        int user_id = getIntent().getIntExtra("user_id", -1);
        String url = "http://192.168.1.44:5000/all_ingredients"+ user_id;;

        JsonArrayRequest request = new JsonArrayRequest(url,
                response -> {
                    ingredientsList.clear();

                    for (int i = 0; i < response.length(); i++) {
                        ingredientsList.add(response.optString(i));
                    }

                    adapter.notifyDataSetChanged();
                },
                error -> Toast.makeText(this, "Error loading ingredients", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}