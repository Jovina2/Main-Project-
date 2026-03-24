package com.example.project;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import org.json.JSONObject;

public class HistoryAdapter extends ArrayAdapter<JSONObject> {

    private Context context;
    private ArrayList<JSONObject> list;

    public HistoryAdapter(Context context, ArrayList<JSONObject> list) {
        super(context, 0, list);
        this.context = context;
        this.list = list;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context)
                    .inflate(R.layout.history_item, parent, false);
        }

        JSONObject obj = list.get(position);

        TextView txtFood = convertView.findViewById(R.id.txtFood);
        TextView txtResult = convertView.findViewById(R.id.txtResult);
        TextView txtDate = convertView.findViewById(R.id.txtDate);
        Button btnDelete = convertView.findViewById(R.id.btnDelete);

        try {
            String food = obj.getString("food_name");
            String result = obj.getString("result");
            String date = obj.getString("checked_date");
            int historyId = obj.getInt("history_id");

            txtFood.setText(food);
            txtDate.setText(date);

            if (result.equalsIgnoreCase("Allergic")) {
                txtResult.setText("⚠ ALLERGIC");
                txtResult.setTextColor(Color.RED);
            } else {
                txtResult.setText("✓ SAFE");
                txtResult.setTextColor(Color.parseColor("#00e676"));
            }

            btnDelete.setOnClickListener(v -> deleteHistory(historyId, position));

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    private void deleteHistory(int historyId, int position) {

        String url = "http://192.168.1.20:5000/delete_history";

        try {
            JSONObject json = new JSONObject();
            json.put("history_id", historyId);

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.POST,
                    url,
                    json,
                    response -> {
                        list.remove(position);
                        notifyDataSetChanged();
                        Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();
                    },
                    error -> Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show()
            );

            Volley.newRequestQueue(context).add(request);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}