package com.example.test;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
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

        // ✅ Animation du fond
        View rootLayout = findViewById(R.id.root_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) rootLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

        // ✅ Initialisation Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // ✅ Initialisation des vues
        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        etEmailr = findViewById(R.id.et_emailr);
        etPasswordr = findViewById(R.id.et_passwordr);
        confPassword = findViewById(R.id.conf_password);
        btnRegister = findViewById(R.id.btn_register);
        TextView tvBackToLogin = findViewById(R.id.tv_back_to_login);

        // ✅ Click : retour vers la page de connexion
        tvBackToLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        });

        // ✅ Click : inscription
        btnRegister.setOnClickListener(v -> registerUser());
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

        if (!email.endsWith("@emsi-edu.ma")) {
            Toast.makeText(this, "Utilisez une adresse email @emsi-edu.ma", Toast.LENGTH_SHORT).show();
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
                            user.sendEmailVerification()
                                    .addOnSuccessListener(unused -> Toast.makeText(this,
                                            "Vérifiez votre email : " + user.getEmail(),
                                            Toast.LENGTH_LONG).show())
                                    .addOnFailureListener(e -> Toast.makeText(this,
                                            "Erreur d'envoi de mail : " + e.getMessage(),
                                            Toast.LENGTH_SHORT).show());

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
                                        Toast.makeText(this, "Profil enregistré. Vérifiez votre email.", Toast.LENGTH_SHORT).show();
                                        mAuth.signOut();
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
