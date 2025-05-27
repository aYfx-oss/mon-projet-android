package com.example.test;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class ModifierProfilActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView imgProfil;
    private Button btnModifierPhoto, btnSauvegarder, btnResetPhoto;
    private EditText etNom, etPrenom;
    private Uri selectedImageUri;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modifier_profil);

        imgProfil = findViewById(R.id.imgProfil);
        btnModifierPhoto = findViewById(R.id.btnModifierPhoto);
        btnSauvegarder = findViewById(R.id.btnSauvegarder);
        btnResetPhoto = findViewById(R.id.btnResetPhoto);
        etNom = findViewById(R.id.etNom);
        etPrenom = findViewById(R.id.etPrenom);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        btnModifierPhoto.setOnClickListener(v -> openImagePicker());
        btnResetPhoto.setOnClickListener(v -> reinitialiserPhotoProfil());
        btnSauvegarder.setOnClickListener(v -> sauvegarderProfil());

        chargerInfosActuelles();
    }

    private void chargerInfosActuelles() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        db.collection("professeurs").document(uid).get()
                .addOnSuccessListener(doc -> {
                    etNom.setText(doc.getString("nom"));
                    etPrenom.setText(doc.getString("prenom"));

                    String photoUrl = doc.getString("photoUrl");
                    if (photoUrl != null && !photoUrl.isEmpty()) {
                        Glide.with(this).load(photoUrl).circleCrop().into(imgProfil);
                    } else {
                        imgProfil.setImageResource(R.drawable.avatar); // default avatar
                    }
                });
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imgProfil.setImageURI(selectedImageUri);
        }
    }

    private void sauvegarderProfil() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        Map<String, Object> updates = new HashMap<>();
        updates.put("nom", nom);
        updates.put("prenom", prenom);

        if (selectedImageUri != null) {
            StorageReference ref = storage.getReference().child("profils/" + uid + ".jpg");
            ref.putFile(selectedImageUri)
                    .addOnSuccessListener(task -> ref.getDownloadUrl().addOnSuccessListener(uri -> {
                        updates.put("photoUrl", uri.toString());
                        mettreAJourFirestore(uid, updates);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(this, "Erreur d'upload : " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            mettreAJourFirestore(uid, updates);
        }
    }

    private void mettreAJourFirestore(String uid, Map<String, Object> data) {
        db.collection("professeurs").document(uid).update(data)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Profil mis à jour", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, MenuActivity.class));
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void reinitialiserPhotoProfil() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();

        new AlertDialog.Builder(this)
                .setTitle("Réinitialiser la photo")
                .setMessage("Voulez-vous supprimer votre photo de profil ?")
                .setPositiveButton("Oui", (dialog, which) -> {
                    StorageReference ref = storage.getReference().child("profils/" + uid + ".jpg");

                    ref.delete()
                            .addOnSuccessListener(unused -> {
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("photoUrl", null);

                                db.collection("professeurs").document(uid).update(updates)
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(this, "Photo supprimée", Toast.LENGTH_SHORT).show();
                                            imgProfil.setImageResource(R.drawable.avatar);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(this, "Erreur Firestore", Toast.LENGTH_SHORT).show());
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Erreur suppression : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Non", null)
                .show();
    }
}
