package com.example.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapters.PlanningAdapter;
import com.example.test.models.Planning;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlanningActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PlanningAdapter adapter;
    private List<Planning> planningList;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planning);

        recyclerView = findViewById(R.id.recyclerViewPlanning);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        planningList = new ArrayList<>();
        adapter = new PlanningAdapter(planningList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        chargerPlannings();
    }

    private void chargerPlannings() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        db.collection("plannings")
                .whereEqualTo("professeur_id", uid)
                .get()
                .addOnSuccessListener(query -> {
                    planningList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        Planning p = doc.toObject(Planning.class);
                        planningList.add(p);
                    }

                    // 🔽 TRI PAR JOUR ET HEURE
                    Collections.sort(planningList, new Comparator<Planning>() {
                        final Map<String, Integer> jours = new HashMap<String, Integer>() {{
                            put("lundi", 1);
                            put("mardi", 2);
                            put("mercredi", 3);
                            put("jeudi", 4);
                            put("vendredi", 5);
                            put("samedi", 6);
                            put("dimanche", 7);
                        }};

                        @Override
                        public int compare(Planning p1, Planning p2) {
                            int jour1 = jours.getOrDefault(p1.getDate().toLowerCase(), 0);
                            int jour2 = jours.getOrDefault(p2.getDate().toLowerCase(), 0);

                            if (jour1 != jour2) {
                                return jour1 - jour2;
                            }

                            // Comparer l'heure de début
                            String h1 = p1.getHeure().split("–")[0].trim();
                            String h2 = p2.getHeure().split("–")[0].trim();
                            return h1.compareTo(h2);
                        }
                    });

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
