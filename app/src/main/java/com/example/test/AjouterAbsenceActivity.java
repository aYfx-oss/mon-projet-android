package com.example.test;

import android.app.DatePickerDialog;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.test.models.Absence;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.*;

public class AjouterAbsenceActivity extends AppCompatActivity {

    private Spinner spinnerGroupes, spinnerEtudiants, spinnerMotif, spinnerModule;
    private EditText etDate;
    private Button btnAjouter, btnVoirListe;
    private View headerLayout;

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
        headerLayout = findViewById(R.id.headerLayout);

        db = FirebaseFirestore.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        initialiserMotifs();
        chargerGroupesDuProf();
        chargerModulesDuProf();
        activerDegradeAnime();
        activerDatePicker();

        spinnerGroupes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                chargerEtudiantsParGroupe(groupes.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAjouter.setOnClickListener(v -> ajouterAbsence());

        btnVoirListe.setOnClickListener(v -> {
            startActivity(new android.content.Intent(this, AbsencesActivity.class));
        });
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
                    List<String> modules = (List<String>) doc.get("modules");
                    if (modules != null) {
                        spinnerModule.setAdapter(new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_dropdown_item,
                                modules
                        ));
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur modules", Toast.LENGTH_SHORT).show());
    }

    private void chargerGroupesDuProf() {
        String profId = currentUser.getUid();
        db.collection("professeurs").document(profId).get()
                .addOnSuccessListener(doc -> {
                    List<String> profGroupes = (List<String>) doc.get("groupes");
                    if (profGroupes != null) {
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
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur groupes", Toast.LENGTH_SHORT).show());
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
        String nomEtudiant = etudiant.getString("nom");
        String etudiantId = etudiant.getId();
        String date = etDate.getText().toString().trim();
        String motif = spinnerMotif.getSelectedItem().toString();
        String module = spinnerModule.getSelectedItem().toString();
        String profId = currentUser.getUid();

        if (date.isEmpty()) {
            Toast.makeText(this, "Date manquante", Toast.LENGTH_SHORT).show();
            return;
        }

        Absence absence = new Absence(etudiantId, nomEtudiant, date, motif, module, profId);

        db.collection("absences").add(absence)
                .addOnSuccessListener(docRef -> {
                    Map<String, Object> historique = new HashMap<>();
                    historique.put("action", "Ajout d'une absence pour " + nomEtudiant);
                    historique.put("date", Timestamp.now());
                    historique.put("professeur_id", profId);

                    db.collection("Historiques").add(historique);

                    Toast.makeText(this, "Absence ajoutée", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("AjouterAbsence", "Erreur Firestore", e);
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    private void activerDegradeAnime() {
        if (headerLayout != null && headerLayout.getBackground() instanceof AnimationDrawable) {
            AnimationDrawable animationDrawable = (AnimationDrawable) headerLayout.getBackground();
            animationDrawable.setEnterFadeDuration(1500);
            animationDrawable.setExitFadeDuration(1500);
            animationDrawable.start();
        }
    }

    private void activerDatePicker() {
        etDate.setFocusable(false);
        etDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(this,
                    (view, selectedYear, selectedMonth, selectedDay) -> {
                        String formattedDate = selectedYear + "-" +
                                String.format("%02d", selectedMonth + 1) + "-" +
                                String.format("%02d", selectedDay);
                        etDate.setText(formattedDate);
                    }, year, month, day);

            dpd.show();
        });
    }
}
