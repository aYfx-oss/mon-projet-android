package com.example.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapters.HistoriqueAdapter;
import com.example.test.models.Historique;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HistoriqueActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HistoriqueAdapter adapter;
    private List<Historique> historiqueList;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historique);

        recyclerView = findViewById(R.id.recyclerViewHistorique);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        historiqueList = new ArrayList<>();
        adapter = new HistoriqueAdapter(historiqueList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        chargerHistorique();
    }

    private void chargerHistorique() {
        String profId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("Historiques")
                .whereEqualTo("professeur_id", profId)
                .get()
                .addOnSuccessListener(query -> {
                    historiqueList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        String action = doc.getString("action");

                        // 🔸 Gestion de Timestamp
                        Object dateObj = doc.get("date");
                        String dateStr = "Date inconnue";

                        if (dateObj instanceof Timestamp) {
                            Timestamp ts = (Timestamp) dateObj;
                            dateStr = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(ts.toDate());
                        } else if (dateObj instanceof String) {
                            dateStr = (String) dateObj;
                        }

                        if (action != null) {
                            historiqueList.add(new Historique(action, dateStr));
                        }
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
