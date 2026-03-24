package com.example.project;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
public class RecommendationAdapter extends RecyclerView.Adapter<RecommendationAdapter.ViewHolder> {

    ArrayList<RecommendItem> list;
    Context context;

    public RecommendationAdapter(ArrayList<RecommendItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_recoomendations, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        RecommendItem item = list.get(position);

        holder.txtTitle.setText(item.title);
        holder.txtSubtitle.setText(item.subtitle);
        holder.progressBar.setProgress(item.confidence);
        holder.txtConfidence.setText(item.confidence + "%");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtTitle, txtSubtitle, txtConfidence;
        ProgressBar progressBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtFoodName);
            txtSubtitle = itemView.findViewById(R.id.txtStatus);
            txtConfidence = itemView.findViewById(R.id.txtConfidence);
            progressBar = itemView.findViewById(R.id.progressConfidence);
        }
    }
}