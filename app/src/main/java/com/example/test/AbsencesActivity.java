package com.example.test;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.test.models.Absence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AbsencesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AbsenceAdapter adapter;
    private List<Absence> absenceList; // <- Corrigé ici
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_absences);

        recyclerView = findViewById(R.id.recyclerAbsences);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        absenceList = new ArrayList<>();
        adapter = new AbsenceAdapter(absenceList); // <- OK maintenant
        recyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        chargerAbsences();
    }

    private void chargerAbsences() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String profUid = currentUser.getUid();

        db.collection("absences").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    absenceList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Absence absence = doc.toObject(Absence.class);
                        absenceList.add(absence);
                    }
                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }
}
