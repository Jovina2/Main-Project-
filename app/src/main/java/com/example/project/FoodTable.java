package com.example.project;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class FoodTable extends AppCompatActivity {

    RecyclerView recyclerFood;
    FloatingActionButton btnAddFood;

    ArrayList<FoodModel> foodList;
    FoodAdapter adapter;

    String URL_FETCH = "http://192.168.1.208:5000/admin/food_database";
    String URL_ADD   = "http://192.168.1.208:5000/food/add";

    String selectedBarcode = "";
    String selectedImageBase64 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_table);

        recyclerFood = findViewById(R.id.recyclerFood);
        btnAddFood = findViewById(R.id.btnAddFood);

        recyclerFood.setLayoutManager(new LinearLayoutManager(this));

        foodList = new ArrayList<>();
        adapter = new FoodAdapter(this, foodList);
        recyclerFood.setAdapter(adapter);

        loadFoodDatabase();

        btnAddFood.setOnClickListener(v -> showAddFoodDialog());
    }

    // ---------------- LOAD FOOD DATABASE ----------------
    private void loadFoodDatabase() {

        JsonObjectRequest request = new JsonObjectRequest(
                URL_FETCH,
                null,
                response -> {
                    try {
                        foodList.clear();

                        JSONArray foods = response.getJSONArray("foods");

                        for (int i = 0; i < foods.length(); i++) {

                            JSONObject obj = foods.getJSONObject(i);

                            String name = obj.getString("food_name");
                            String barcode = obj.optString("barcode", "MANUAL");

                            foodList.add(new FoodModel(name, barcode));
                        }

                        adapter.notifyDataSetChanged();

                    } catch (Exception e) {
                        Toast.makeText(this, "Error loading foods", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server Error", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ---------------- ADD FOOD POPUP ----------------
    private void showAddFoodDialog() {

        selectedBarcode = "";
        selectedImageBase64 = "";

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this)
                .inflate(R.layout.dialog_add_food, null);

        EditText edtName = view.findViewById(R.id.edtFoodName);
        ImageView btnScan = view.findViewById(R.id.edtBarcode);
        ImageView btnUpload = view.findViewById(R.id.btnUploadImage);

        btnScan.setOnClickListener(v -> startBarcodeScanner());
        btnUpload.setOnClickListener(v -> pickImage());

        builder.setView(view);
        builder.setPositiveButton("Save", null); // IMPORTANT
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {

            String foodName = edtName.getText().toString().trim();

            if (foodName.isEmpty()) {
                Toast.makeText(this, "Enter food name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (selectedBarcode.isEmpty())
                selectedBarcode = "MANUAL";

            // 🔥 Call API
            saveFoodToDatabase(foodName, selectedBarcode, selectedImageBase64);

            dialog.dismiss(); // close AFTER API call
        });
    }

    // ---------------- SAVE FOOD INTO MYSQL ----------------
    private void saveFoodToDatabase(String foodName, String barcode, String imageBase64) {

        JSONObject params = new JSONObject();
        try {
            params.put("food_name", foodName);
            params.put("barcode", barcode);
            params.put("food_image", imageBase64);
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                URL_ADD,
                params,
                response -> {
                    Toast.makeText(this, "Food Added Successfully!", Toast.LENGTH_SHORT).show();
                    loadFoodDatabase();
                },
                error -> Toast.makeText(this, "Insert Failed!", Toast.LENGTH_LONG).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    // ---------------- BARCODE SCAN ----------------
    private void startBarcodeScanner() {

        ScanOptions options = new ScanOptions();
        options.setPrompt("Scan Barcode");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);

        barcodeLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> barcodeLauncher =
            registerForActivityResult(new ScanContract(), result -> {

                if (result.getContents() != null) {
                    selectedBarcode = result.getContents();
                    Toast.makeText(this, "Barcode: " + selectedBarcode, Toast.LENGTH_SHORT).show();
                }
            });

    // ---------------- IMAGE PICK ----------------
    private void pickImage() {
        imagePicker.launch("image/*");
    }

    ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {

                if (uri != null) {
                    convertImageToBase64(uri);
                }
            });

    private void convertImageToBase64(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);

            byte[] bytes = baos.toByteArray();
            selectedImageBase64 = Base64.encodeToString(bytes, Base64.DEFAULT);

            Toast.makeText(this, "Image Selected!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
