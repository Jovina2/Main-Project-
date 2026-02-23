package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class AllergenAdapter extends RecyclerView.Adapter<AllergenAdapter.ViewHolder> {

    Context context;
    ArrayList<AllergyModel> allergyList;

    public AllergenAdapter(Context context, ArrayList<AllergyModel> allergyList) {
        this.context = context;
        this.allergyList = allergyList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_allergy, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AllergyModel model = allergyList.get(position);

        holder.txtUserId.setText("User ID: " + model.getUserId());
        holder.txtAllergyName.setText("Allergy: " + model.getAllergyName());
        holder.txtSeverity.setText("Severity: " + model.getSeverity());
        holder.txtRemarks.setText("Remarks: " + model.getRemarks());
    }

    @Override
    public int getItemCount() {
        return allergyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtUserId, txtAllergyName, txtSeverity, txtRemarks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUserId = itemView.findViewById(R.id.txtUserId);
            txtAllergyName = itemView.findViewById(R.id.txtAllergyName);
            txtSeverity = itemView.findViewById(R.id.txtSeverity);
            txtRemarks = itemView.findViewById(R.id.txtRemarks);
        }
    }
}
