// AjouterDocumentActivity.java (version Java avec Firebase uniquement)

package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.*;

public class AjouterDocumentActivity extends AppCompatActivity {

    private EditText editTitre, editDescription;
    private Button btnChoisirFichier, btnTeleverser;
    private Uri selectedUri;
    private boolean fichierChoisi = false;
    private final int PICK_FILE_REQUEST = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ajouter_document);

        editTitre = findViewById(R.id.editTitre);
        editDescription = findViewById(R.id.editDescription);
        btnChoisirFichier = findViewById(R.id.btnChoisirFichier);
        btnTeleverser = findViewById(R.id.btnTeleverser);

        btnChoisirFichier.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            startActivityForResult(intent, PICK_FILE_REQUEST);
        });

        btnTeleverser.setOnClickListener(v -> {
            if (!fichierChoisi) {
                Toast.makeText(this, "Veuillez choisir un fichier", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadToFirebase();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_FILE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedUri = data.getData();
            fichierChoisi = true;
            Toast.makeText(this, "Fichier choisi", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadToFirebase() {
        String titre = editTitre.getText().toString().trim();
        String description = editDescription.getText().toString().trim();

        if (titre.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Champs requis manquants", Toast.LENGTH_SHORT).show();
            return;
        }

        String fileName = "doc_" + System.currentTimeMillis() + ".pdf";
        StorageReference ref = FirebaseStorage.getInstance().getReference().child("documents/" + fileName);

        ref.putFile(selectedUri)
                .addOnSuccessListener(taskSnapshot -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                    enregistrerDansFirestore(titre, description, uri.toString());
                }))
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur upload: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void enregistrerDansFirestore(String titre, String description, String url) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        Map<String, Object> data = new HashMap<>();
        data.put("titre", titre);
        data.put("description", description);
        data.put("url", url);
        data.put("dateUpload", date);
        data.put("professeur_id", uid);

        db.collection("documents").add(data)
                .addOnSuccessListener(docRef -> {
                    Toast.makeText(this, "Document téléversé", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Erreur Firestore", Toast.LENGTH_SHORT).show());
    }
}