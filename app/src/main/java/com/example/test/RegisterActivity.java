package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText etNom, etPrenom, etEmailr, etPasswordr, confPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        etEmailr = findViewById(R.id.et_emailr);
        etPasswordr = findViewById(R.id.et_passwordr);
        confPassword = findViewById(R.id.conf_password);
        btnRegister = findViewById(R.id.btn_register);
        TextView tvBackToLogin = findViewById(R.id.tv_back_to_login);

        btnRegister.setOnClickListener(v -> registerUser());

        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });
    }

    private void registerUser() {
        String nom = etNom.getText().toString().trim();
        String prenom = etPrenom.getText().toString().trim();
        String email = etEmailr.getText().toString().trim();
        String password = etPasswordr.getText().toString().trim();
        String confirm = confPassword.getText().toString().trim();

        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirm)) {
            Toast.makeText(this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // ✅ Envoyer mail de vérification
                            user.sendEmailVerification()
                                    .addOnSuccessListener(unused -> Toast.makeText(this,
                                            "Mail de vérification envoyé à : " + user.getEmail(),
                                            Toast.LENGTH_LONG).show())
                                    .addOnFailureListener(e -> Toast.makeText(this,
                                            "Erreur d'envoi du mail : " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());

                            // ✅ Ajout dans Firestore
                            String uid = user.getUid();
                            Map<String, Object> profData = new HashMap<>();
                            profData.put("email", email);
                            profData.put("nom", nom);
                            profData.put("prenom", prenom);
                            profData.put("filiere", "Informatique");
                            profData.put("modules", Arrays.asList("Java", "BD"));
                            profData.put("site", "Casablanca");
                            profData.put("professeur_id", uid);

                            db.collection("professeurs").document(uid)
                                    .set(profData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(this, "Profil enregistré, veuillez vérifier votre email", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut(); // Important : forcer la vérification avant accès
                                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(this, "Erreur Firestore : " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        }
                    } else {
                        Toast.makeText(this, "Erreur Auth : " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
