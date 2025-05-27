package com.example.test.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;

import java.util.List;

public class RattrapageAdapter extends RecyclerView.Adapter<RattrapageAdapter.RattrapageViewHolder> {

    private final List<com.example.test.models.Rattrapages> rattrapageList;

    public RattrapageAdapter(List<com.example.test.models.Rattrapages> rattrapageList) {
        this.rattrapageList = rattrapageList;
    }

    @NonNull
    @Override
    public RattrapageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rattrapage, parent, false); // ou item_rattrapage.xml
        return new RattrapageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RattrapageViewHolder holder, int position) {
        com.example.test.models.Rattrapages r = rattrapageList.get(position);
        holder.titre.setText("Matière : " + r.getMatiere());
        holder.montant.setText("Heure : " + r.getHeure() + " | Salle : " + r.getSalle());
        holder.date.setText("Date : " + r.getDate());
    }

    @Override
    public int getItemCount() {
        return rattrapageList != null ? rattrapageList.size() : 0;
    }

    static class RattrapageViewHolder extends RecyclerView.ViewHolder {
        TextView titre, montant, date;

        public RattrapageViewHolder(@NonNull View itemView) {
            super(itemView);
            titre = itemView.findViewById(R.id.tvTitre);
            montant = itemView.findViewById(R.id.tvMontant);
            date = itemView.findViewById(R.id.tvDate);
        }
    }
}
