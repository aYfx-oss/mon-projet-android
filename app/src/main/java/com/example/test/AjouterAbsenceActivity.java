package com.example.test;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.test.models.Absence;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class AjouterAbsenceActivity extends AppCompatActivity {

    private Spinner spinnerGroupes, spinnerEtudiants, spinnerMotif, spinnerModule;
    private EditText etDate;
    private Button btnAjouter, btnVoirListe;

    private FirebaseFirestore db;
    private FirebaseUser currentUser;
    private List<String> groupes = new ArrayList<>();
    private List<DocumentSnapshot> etudiantsDocs = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_absence);

        spinnerGroupes = findViewById(R.id.spinnerGroupes);
        spinnerEtudiants = findViewById(R.id.spinnerEtudiants);
        spinnerMotif = findViewById(R.id.spinnerMotif);
        spinnerModule = findViewById(R.id.spinnerModule);
        etDate = findViewById(R.id.etDate);
        btnAjouter = findViewById(R.id.btnAjouter);
        btnVoirListe = findViewById(R.id.btnVoirListe);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initialiserMotifs();
        chargerGroupesDuProf();
        chargerModulesDuProf();

        spinnerGroupes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String groupe = groupes.get(position);
                chargerEtudiantsParGroupe(groupe);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAjouter.setOnClickListener(v -> ajouterAbsence());
        btnVoirListe.setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ListeAbsencesActivity.class)));
    }

    private void initialiserMotifs() {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.motifs_array,
                android.R.layout.simple_spinner_dropdown_item
        );
        spinnerMotif.setAdapter(adapter);
    }

    private void chargerModulesDuProf() {
        String profId = currentUser.getUid();

        db.collection("professeurs").document(profId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("modules")) {
                        List<String> modules = (List<String>) doc.get("modules");
                        if (modules != null) {
                            spinnerModule.setAdapter(new ArrayAdapter<>(
                                    this,
                                    android.R.layout.simple_spinner_dropdown_item,
                                    modules
                            ));
                        }
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur modules", Toast.LENGTH_SHORT).show());
    }

    private void chargerGroupesDuProf() {
        String profId = currentUser.getUid();

        db.collection("professeurs").document(profId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists() && doc.contains("groupes")) {
                        List<String> profGroupes = (List<String>) doc.get("groupes");
                        groupes.clear();
                        groupes.addAll(profGroupes);
                        spinnerGroupes.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_dropdown_item,
                                groupes
                        ));
                    } else {
                        Toast.makeText(this, "Aucun groupe associé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur groupes", Toast.LENGTH_SHORT).show());
    }

    private void chargerEtudiantsParGroupe(String groupe) {
        db.collection("etudiants")
                .whereEqualTo("groupe", groupe)
                .get()
                .addOnSuccessListener(query -> {
                    etudiantsDocs.clear();
                    List<String> noms = new ArrayList<>();
                    for (DocumentSnapshot doc : query) {
                        noms.add(doc.getString("nom"));
                        etudiantsDocs.add(doc);
                    }
                    spinnerEtudiants.setAdapter(new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_dropdown_item,
                            noms
                    ));
                });
    }

    private void ajouterAbsence() {
        int index = spinnerEtudiants.getSelectedItemPosition();
        if (index < 0 || etudiantsDocs.isEmpty()) {
            Toast.makeText(this, "Sélectionnez un étudiant", Toast.LENGTH_SHORT).show();
            return;
        }

        DocumentSnapshot etudiant = etudiantsDocs.get(index);
        String nom = etudiant.getString("nom");
        String etudiantId = etudiant.getId();
        String date = etDate.getText().toString().trim();
        String motif = spinnerMotif.getSelectedItem().toString();
        String module = spinnerModule.getSelectedItem().toString();
        String profId = currentUser.getUid();

        if (date.isEmpty()) {
            Toast.makeText(this, "Date manquante", Toast.LENGTH_SHORT).show();
            return;
        }

        Absence absence = new Absence(etudiantId, nom, date, motif, module, profId);
        db.collection("absences").add(absence)
                .addOnSuccessListener(docRef -> {
                    // ✅ Utilisation de Timestamp Firebase pour la date
                    Map<String, Object> historique = new HashMap<>();
                    historique.put("action", "Ajout d'une absence pour " + nom);
                    historique.put("date", com.google.firebase.Timestamp.now());
                    historique.put("professeur_id", profId); // 🟢 Assure la cohérence avec l'affichage

                    db.collection("Historiques").add(historique);

                    Toast.makeText(this, "Absence ajoutée", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

}
