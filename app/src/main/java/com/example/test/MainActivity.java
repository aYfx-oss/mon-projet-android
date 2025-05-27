package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnLogin = findViewById(R.id.btn_login);
        tvGoToRegister = findViewById(R.id.tv_go_to_register);

        btnLogin.setOnClickListener(v -> authenticateUser());

        tvGoToRegister.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void authenticateUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
            return;
        }

        // Vérifie si l'adresse e-mail se termine bien par @emsi-edu.ma
        if (!email.endsWith("@emsi-edu.ma")) {
            Toast.makeText(this, "Seules les adresses @emsi-edu.ma sont autorisées.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            if (user.isEmailVerified()) {
                                String uid = user.getUid();
                                String userEmail = user.getEmail();

                                DocumentReference docRef = db.collection("professeurs").document(uid);
                                docRef.get().addOnSuccessListener(snapshot -> {
                                    if (!snapshot.exists()) {
                                        Map<String, Object> data = new HashMap<>();
                                        data.put("email", userEmail);
                                        data.put("nom", "Nom");
                                        data.put("prenom", "Prénom");
                                        data.put("filiere", "Informatique");
                                        data.put("modules", Arrays.asList("Java", "BD"));
                                        data.put("site", "Casablanca");
                                        data.put("professeur_id", uid);

                                        docRef.set(data)
                                                .addOnSuccessListener(unused ->
                                                        Toast.makeText(this, "Profil ajouté à Firestore", Toast.LENGTH_SHORT).show())
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(this, "Erreur Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                                    }

                                    // Redirection vers le menu principal
                                    Intent intent = new Intent(this, MenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                });

                            } else {
                                Toast.makeText(this, "Veuillez vérifier votre email avant de vous connecter.", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        }
                    } else {
                        Toast.makeText(this, "Erreur: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
