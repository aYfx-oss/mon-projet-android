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

    private final List<Planning> planningList;

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

        // Jour abrégé (ex : "Lun", "Mar", etc.)
        String jourAbrege = abrigerJour(p.getDate());
        holder.jour.setText(jourAbrege);

        // Heure de début (ex : "08:30")
        String heureDebut = p.getHeure() != null && p.getHeure().contains("–")
                ? p.getHeure().split("–")[0].trim()
                : p.getHeure();
        holder.heure.setText(heureDebut);

        holder.titre.setText(p.getTitre());
        holder.lieu.setText(p.getLieu());
        holder.description.setText(p.getDescription());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull PlanningViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(400).start();
    }

    @Override
    public int getItemCount() {
        return planningList != null ? planningList.size() : 0;
    }

    private String abrigerJour(String jourComplet) {
        if (jourComplet == null) return "";
        jourComplet = jourComplet.toLowerCase();
        if (jourComplet.startsWith("lun")) return "Lun";
        if (jourComplet.startsWith("mar")) return "Mar";
        if (jourComplet.startsWith("mer")) return "Mer";
        if (jourComplet.startsWith("jeu")) return "Jeu";
        if (jourComplet.startsWith("ven")) return "Ven";
        if (jourComplet.startsWith("sam")) return "Sam";
        if (jourComplet.startsWith("dim")) return "Dim";
        return jourComplet.substring(0, Math.min(3, jourComplet.length()));
    }

    static class PlanningViewHolder extends RecyclerView.ViewHolder {
        TextView jour, heure, titre, lieu, description;

        public PlanningViewHolder(@NonNull View itemView) {
            super(itemView);
            jour = itemView.findViewById(R.id.tvDate);
            heure = itemView.findViewById(R.id.tvHeure);
            titre = itemView.findViewById(R.id.tvTitre);
            lieu = itemView.findViewById(R.id.tvLieu);
            description = itemView.findViewById(R.id.tvDescription);
        }
    }
}
