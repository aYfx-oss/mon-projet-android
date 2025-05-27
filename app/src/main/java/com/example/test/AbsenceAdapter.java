package com.example.test;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.models.Absence;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class AbsenceAdapter extends RecyclerView.Adapter<AbsenceAdapter.AbsenceViewHolder> {

    private final List<Absence> absenceList;

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
        holder.tvNom.setText("Nom : " + absence.getNomEtudiant());
        holder.tvDate.setText("Date : " + absence.getDate());
        holder.tvMotif.setText("Motif : " + absence.getMotif());
        holder.tvModule.setText("Module : " + absence.getModule());

        holder.btnSupprimer.setOnClickListener(v -> {

            // 🔒 Boîte de confirmation
            new AlertDialog.Builder(holder.itemView.getContext())
                    .setTitle("Confirmation")
                    .setMessage("Supprimer cette absence ?")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("absences")
                                .whereEqualTo("etudiantId", absence.getEtudiantId())
                                .whereEqualTo("date", absence.getDate())
                                .whereEqualTo("professeurId", absence.getProfesseurId())
                                .whereEqualTo("module", absence.getModule())
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    for (DocumentSnapshot doc : querySnapshot) {
                                        doc.getReference().delete();
                                    }
                                    absenceList.remove(position);
                                    notifyItemRemoved(position);

                                    // ✅ Snackbar confirmation
                                    Snackbar.make(holder.itemView, "Absence supprimée", Snackbar.LENGTH_SHORT)
                                            .setBackgroundTint(0xFF4CAF50) // Vert
                                            .setTextColor(0xFFFFFFFF) // Blanc
                                            .show();
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(holder.itemView.getContext(), "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return absenceList.size();
    }

    static class AbsenceViewHolder extends RecyclerView.ViewHolder {
        TextView tvNom, tvDate, tvMotif, tvModule;
        Button btnSupprimer;

        public AbsenceViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNom = itemView.findViewById(R.id.tvNomEtudiant);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvMotif = itemView.findViewById(R.id.tvMotif);
            tvModule = itemView.findViewById(R.id.tvModule);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimer);
        }
    }
}
