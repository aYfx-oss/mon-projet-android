package com.example.test.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.R;
import com.example.test.models.Document;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.*;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocViewHolder> {

    private Context context;
    private List<Document> documentList;

    public DocumentAdapter(Context context, List<Document> documentList) {
        this.context = context;
        this.documentList = documentList;
    }

    @NonNull
    @Override
    public DocViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_document, parent, false);
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DocViewHolder holder, int position) {
        Document doc = documentList.get(position);

        holder.tvTitre.setText("📄 " + doc.getTitre());
        holder.tvDescription.setText("📝 " + doc.getDescription());
        holder.tvDate.setText("📅 " + doc.getDateUpload());

        // ✅ Ouvrir le PDF dans le navigateur
        holder.btnTelecharger.setOnClickListener(v -> {
            String url = doc.getUrl();

            if (url == null || url.trim().isEmpty()) {
                Toast.makeText(context, "Lien du document introuvable", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Impossible d’ouvrir le lien", Toast.LENGTH_SHORT).show();
            }
        });

        // ✅ Bouton Supprimer
        holder.btnSupprimer.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Supprimer ce document ?")
                    .setMessage("Cette action est irréversible.")
                    .setPositiveButton("Oui", (dialog, which) -> {
                        FirebaseFirestore.getInstance()
                                .collection("documents")
                                .whereEqualTo("url", doc.getUrl())
                                .get()
                                .addOnSuccessListener(query -> {
                                    for (DocumentSnapshot snapshot : query) {
                                        snapshot.getReference().delete();
                                    }

                                    FirebaseStorage.getInstance().getReferenceFromUrl(doc.getUrl())
                                            .delete()
                                            .addOnSuccessListener(aVoid -> {
                                                documentList.remove(position);
                                                notifyItemRemoved(position);

                                                Snackbar.make(holder.itemView, "Document supprimé", Snackbar.LENGTH_SHORT)
                                                        .setBackgroundTint(0xFF4CAF50)
                                                        .setTextColor(0xFFFFFFFF)
                                                        .show();
                                            })
                                            .addOnFailureListener(e ->
                                                    Toast.makeText(context, "Erreur suppression Storage", Toast.LENGTH_SHORT).show());
                                });
                    })
                    .setNegativeButton("Annuler", null)
                    .show();
        });
    }

    @Override
    public int getItemCount() {
        return documentList.size();
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitre, tvDescription, tvDate;
        Button btnTelecharger, btnSupprimer;

        public DocViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitre = itemView.findViewById(R.id.tvTitre);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvDate = itemView.findViewById(R.id.tvDate);
            btnTelecharger = itemView.findViewById(R.id.btnTelecharger);
            btnSupprimer = itemView.findViewById(R.id.btnSupprimer);
        }
    }
}
