package com.example.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.adapters.ReclamationAdapter;
import com.example.test.models.Reclamation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ReclamationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private List<Reclamation> reclamationList;
    private ReclamationAdapter adapter;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reclamations);

        recyclerView = findViewById(R.id.recyclerViewReclamations);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        reclamationList = new ArrayList<>();
        adapter = new ReclamationAdapter(reclamationList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        chargerReclamations();
    }

    private void chargerReclamations() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        db.collection("reclamations")
                .whereEqualTo("professeur_id", uid)
                .get()
                .addOnSuccessListener(query -> {
                    reclamationList.clear();
                    for (QueryDocumentSnapshot doc : query) {
                        String objet = doc.getString("reclamations");
                        String message = doc.getString("message");
                        String date = doc.getString("date");

                        reclamationList.add(new Reclamation(objet, message, date));
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
