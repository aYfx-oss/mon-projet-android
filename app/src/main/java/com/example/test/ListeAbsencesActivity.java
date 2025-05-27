package com.example.test;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.models.Absence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class ListeAbsencesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AbsenceAdapter adapter;
    private List<Absence> absenceList;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_absences);

        recyclerView = findViewById(R.id.recyclerAbsences);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        absenceList = new ArrayList<>();
        adapter = new AbsenceAdapter(absenceList);
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        chargerAbsencesProfesseur();
    }

    private void chargerAbsencesProfesseur() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String profUid = currentUser.getUid();

        db.collection("absences")
                .whereEqualTo("professeurId", profUid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    absenceList.clear();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Absence absence = doc.toObject(Absence.class);
                        absenceList.add(absence);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur lors du chargement : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
