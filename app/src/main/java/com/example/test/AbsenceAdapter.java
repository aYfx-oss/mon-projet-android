package com.example.test;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.models.Absence;

import java.util.List;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.AbsenceViewHolder> {

    private List<Absence> absenceList;

    public AbsenceAdapter(List<Absence> absenceList) {
        this.absenceList = absenceList;
    }

    @NonNull
    @Override
    public AbsenceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_absence, parent, false);
        return new AbsenceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AbsenceViewHolder holder, int position) {
        Absence absence = absenceList.get(position);

        holder.tvNomEtudiant.setText(absence.getNomEtudiant());
        holder.tvDate.setText(absence.getDate());
        holder.tvMotif.setText(absence.getMotif());
        holder.tvModule.setText(absence.getModule());

        // Action du bouton supprimer
        holder.btnSupprimer.setOnClickListener(v -> {
            absenceList.remove(position);
            notifyItemRemoved(position);
            // TODO : Ajouter suppression dans Firestore si nécessaire
        });
    }

    @Override
    public int getItemCount() {
        return absenceList.size();
    }

    static class AbsenceViewHolder extends RecyclerView.ViewHolder {

        TextView tvNomEtudiant, tvDate, tvMotif, tvModule;
        ImageButton btnSupprimer;

        public AbsenceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNomEtudiant = itemView.findViewById(R.id.tvNomEtudiant);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMotif = itemView.findViewById(R.id.tvMotif);
            tvModule = itemView.findViewById(R.id.tvModule);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimer); // ✅ Corrigé ici
        }
    }
}
