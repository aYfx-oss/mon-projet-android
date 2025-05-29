package com.example.test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Rattrapages;

import java.util.List;

public class RattrapageAdapter extends RecyclerView.Adapter<RattrapageAdapter.RattrapageViewHolder> {

    private final List<Rattrapages> rattrapageList;

    public RattrapageAdapter(List<Rattrapages> rattrapageList) {
        this.rattrapageList = rattrapageList;
    }

    @NonNull
    @Override
    public RattrapageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rattrapage, parent, false);
        return new RattrapageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RattrapageViewHolder holder, int position) {
        Rattrapages r = rattrapageList.get(position);

        // Heure de début dans le cercle
        String heure = r.getHeure();
        String heureDebut = heure.contains("–") ? heure.split("–")[0].trim() : heure;
        holder.heure.setText(heureDebut);

        holder.titre.setText("Matière : " + r.getMatiere());
        holder.date.setText("Date : " + r.getDate());
        holder.montant.setText("Heure : " + r.getHeure() + " | Salle : " + r.getSalle());
    }

    @Override
    public int getItemCount() {
        return rattrapageList != null ? rattrapageList.size() : 0;
    }

    @Override
    public void onViewAttachedToWindow(@NonNull RattrapageViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        holder.itemView.setAlpha(0f);
        holder.itemView.animate().alpha(1f).setDuration(400).start();
    }

    static class RattrapageViewHolder extends RecyclerView.ViewHolder {
        TextView heure, titre, montant, date;

        public RattrapageViewHolder(@NonNull View itemView) {
            super(itemView);
            heure = itemView.findViewById(R.id.tvHeure);
            titre = itemView.findViewById(R.id.tvTitre);
            montant = itemView.findViewById(R.id.tvMontant);
            date = itemView.findViewById(R.id.tvDate);
        }
    }
}
