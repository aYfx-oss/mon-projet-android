package com.example.test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Planning;

import java.util.List;

public class PlanningAdapter extends RecyclerView.Adapter<PlanningAdapter.PlanningViewHolder> {

    private List<Planning> planningList;

    public PlanningAdapter(List<Planning> planningList) {
        this.planningList = planningList;
    }

    @NonNull
    @Override
    public PlanningViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planning, parent, false);
        return new PlanningViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PlanningViewHolder holder, int position) {
        Planning p = planningList.get(position);
        holder.titre.setText(p.getTitre());
        holder.date.setText(p.getDate());
        holder.heure.setText(p.getHeure());
        holder.lieu.setText(p.getLieu());
        holder.description.setText(p.getDescription());
    }

    @Override
    public int getItemCount() {
        return planningList.size();
    }

    static class PlanningViewHolder extends RecyclerView.ViewHolder {
        TextView titre, date, heure, lieu, description;

        public PlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.tvTitre);
            date = itemView.findViewById(R.id.tvDate);
            heure = itemView.findViewById(R.id.tvHeure);
            lieu = itemView.findViewById(R.id.tvLieu);
            description = itemView.findViewById(R.id.tvDescription);
        }
    }
}
