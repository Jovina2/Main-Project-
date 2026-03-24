package com.example.project;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AllergyActivity extends AppCompatActivity {

    LinearLayout layoutAllergens;
    Button btnSave, btnAddAllergen;
    AutoCompleteTextView edtSearch;
    int userId;

    private final String SAVE_ALLERGY_URL = "http://192.168.1.44:5000/save_allergy";
    private final String GET_ALLERGY_URL = "http://192.168.1.44:5000/get_allergy/";

    // Suggestions for common allergens
    private final String[] commonAllergens = {"Peanuts", "Soy", "Milk", "Eggs", "Wheat", "Fish", "Shellfish"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allergy);

        layoutAllergens = findViewById(R.id.layoutAllergens);
        btnSave = findViewById(R.id.btnSave);
        btnAddAllergen = findViewById(R.id.btnAddAllergen);
        edtSearch = findViewById(R.id.edtSearch);

        // Setup dropdown suggestions
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, commonAllergens);
        edtSearch.setAdapter(adapter);

        // Get userId from intent
        userId = getIntent().getIntExtra("user_id", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not found!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Load existing allergy data
        loadAllergyProfile();

        // Add allergen button
        btnAddAllergen.setOnClickListener(v -> {
            String allergenName = edtSearch.getText().toString().trim();
            if (!TextUtils.isEmpty(allergenName)) {
                addAllergenCard(allergenName);
                edtSearch.setText(""); // clear input
            } else {
                Toast.makeText(this, "Enter allergen name", Toast.LENGTH_SHORT).show();
            }
        });

        // Save button
        btnSave.setOnClickListener(v -> saveAllergyProfile());
    }

    // Add a new allergen card dynamically
    private void addAllergenCard(String allergenName) {
        LayoutInflater inflater = LayoutInflater.from(this);
        LinearLayout card = (LinearLayout) inflater.inflate(R.layout.item_allergen_card, layoutAllergens, false);

        CheckBox chkAllergy = card.findViewById(R.id.chkAllergen);
        RadioGroup groupSeverity = card.findViewById(R.id.groupSeverity);
        EditText edtRemarks = card.findViewById(R.id.edtRemarks);

        chkAllergy.setText(allergenName);
        chkAllergy.setChecked(true); // default checked when added

        layoutAllergens.addView(card, 0);
    }

    // Load existing allergy profile from backend
    private void loadAllergyProfile() {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                GET_ALLERGY_URL + userId,
                null,
                response -> {
                    try {
                        JSONArray allergies = response.getJSONArray("allergies");
                        for (int i = 0; i < allergies.length(); i++) {
                            JSONObject obj = allergies.getJSONObject(i);
                            String name = obj.getString("allergy_name");
                            String severity = obj.getString("severity");
                            String remarks = obj.optString("remarks", "");

                            addAllergenCard(name);

                            // Set severity and remarks for the newly added card
                            LinearLayout card = (LinearLayout) layoutAllergens.getChildAt(layoutAllergens.getChildCount() - 1);
                            RadioGroup groupSeverity = card.findViewById(R.id.groupSeverity);
                            EditText edtRemarks = card.findViewById(R.id.edtRemarks);

                            for (int k = 0; k < groupSeverity.getChildCount(); k++) {
                                RadioButton rb = (RadioButton) groupSeverity.getChildAt(k);
                                if (rb.getText().toString().equalsIgnoreCase(severity)) {
                                    rb.setChecked(true);
                                    break;
                                }
                            }
                            edtRemarks.setText(remarks);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Failed to load allergy profile", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server error: " + error.getMessage(), Toast.LENGTH_SHORT).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    // Save all allergens to backend
    private void saveAllergyProfile() {
        JSONArray jsonAllergies = new JSONArray();

        for (int i = 0; i < layoutAllergens.getChildCount(); i++) {
            LinearLayout card = (LinearLayout) layoutAllergens.getChildAt(i);
            CheckBox chkAllergy = card.findViewById(R.id.chkAllergen);
            RadioGroup groupSeverity = card.findViewById(R.id.groupSeverity);
            EditText edtRemarks = card.findViewById(R.id.edtRemarks);

            if (chkAllergy.isChecked()) {
                int selectedId = groupSeverity.getCheckedRadioButtonId();
                if (selectedId == -1) {
                    Toast.makeText(this, "Select severity for " + chkAllergy.getText(), Toast.LENGTH_SHORT).show();
                    return;
                }
                RadioButton rb = card.findViewById(selectedId);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("allergy_name", chkAllergy.getText().toString());
                    obj.put("severity", rb.getText().toString());
                    obj.put("remarks", edtRemarks.getText().toString());
                    jsonAllergies.put(obj);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (jsonAllergies.length() == 0) {
            Toast.makeText(this, "Select at least one allergen", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject body = new JSONObject();
        try {
            body.put("user_id", userId);
            body.put("allergies", jsonAllergies);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                SAVE_ALLERGY_URL,
                body,
                response -> {
                    try {
                        Toast.makeText(
                                AllergyActivity.this,
                                response.getString("message"),
                                Toast.LENGTH_SHORT
                        ).show();

// ✅ Go to Home Dashboard
                        Intent intent = new Intent(AllergyActivity.this, HomeActivity.class);
                        intent.putExtra("user_id", userId);
// Clear allergy screen from back stack
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        finish(); // close AllergyActivity

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(
                        AllergyActivity.this,
                        "Server error: " + error.getMessage(),
                        Toast.LENGTH_SHORT
                ).show()
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
};
