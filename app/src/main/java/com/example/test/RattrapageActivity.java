package com.example.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapters.RattrapageAdapter;
import com.example.test.models.Rattrapages;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RattrapageActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RattrapageAdapter adapter;
    private List<Rattrapages> rattrapageList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rattrapage);

        recyclerView = findViewById(R.id.recyclerViewRattrapages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        rattrapageList = new ArrayList<>();
        adapter = new RattrapageAdapter(rattrapageList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        chargerEtAfficherRattrapages();
    }

    private void chargerEtAfficherRattrapages() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = user.getUid();

        db.collection("professeurs").document(uid).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("modules")) {
                        List<String> modules = (List<String>) doc.get("modules");
                        afficherRattrapages(uid, modules);
                    } else {
                        Toast.makeText(this, "Modules non trouvés", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur chargement modules : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void afficherRattrapages(String profUid, List<String> modules) {
        db.collection("Rattrapages")
                .whereEqualTo("professeur_id", profUid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    rattrapageList.clear();

                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        String matiere = doc.getString("matiere");

                        if (modules.contains(matiere)) {
                            Rattrapages r = doc.toObject(Rattrapages.class);
                            rattrapageList.add(r);
                        }
                    }

                    // 🔽 TRI : par date puis par heure
                    Collections.sort(rattrapageList, (r1, r2) -> {
                        int compareDate = r1.getDate().compareTo(r2.getDate());
                        if (compareDate != 0) return compareDate;

                        String h1 = r1.getHeure().split("–")[0].trim(); // gère format "08:00 – 09:30"
                        String h2 = r2.getHeure().split("–")[0].trim();
                        return h1.compareTo(h2);
                    });

                    adapter.notifyDataSetChanged();

                    if (rattrapageList.isEmpty()) {
                        Toast.makeText(this, "Aucun rattrapage trouvé.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
}
