package com.example.test;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProfilActivity extends AppCompatActivity {

    private TextView tvNomPrenom;
    private ImageView imgPhoto;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil);

        tvNomPrenom = findViewById(R.id.tvNomPrenom);
        imgPhoto = findViewById(R.id.imgProfil);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            db.collection("professeurs").document(uid)
                    .get()
                    .addOnSuccessListener(doc -> {
                        if (doc.exists()) {
                            String nom = doc.getString("nom");
                            String prenom = doc.getString("prenom");
                            tvNomPrenom.setText(prenom + " " + nom);

                            String photo = doc.getString("photoUrl");
                            if (photo != null)
                                Glide.with(this).load(photo).into(imgPhoto);
                        }
                    });
        }
    }
}
