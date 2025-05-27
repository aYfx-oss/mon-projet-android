package com.example.test;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MenuActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private FusedLocationProviderClient fusedLocationClient;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    private TextView tvGreeting, tvNom;
    private ImageView imgUser;
    private Button btnVoirProfil; // ✅ Nouveau bouton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        tvGreeting = findViewById(R.id.greetingText);
        tvNom = findViewById(R.id.dashboard_adminName);
        imgUser = findViewById(R.id.userPhoto);
        btnVoirProfil = findViewById(R.id.btnVoirProfil); // ✅ Initialise le bouton

        btnVoirProfil.setOnClickListener(v -> {
            Intent intent = new Intent(this, ModifierProfilActivity.class);
            startActivity(intent);
        });

        chargerInfosProf();
        setupCardListeners();
        requestLocation();
    }

    private void chargerInfosProf() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show();
            return;
        }

        String uid = currentUser.getUid();

        db.collection("professeurs").document(uid)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        String nom = doc.getString("nom");
                        String prenom = doc.getString("prenom");

                        tvGreeting.setText("Bonjour Monsieur/Mme");
                        tvNom.setText(prenom + " " + nom);

                        // ✅ Vérifie si le champ photoUrl existe et est non vide
                        if (doc.contains("photoUrl")) {
                            String photoUrl = doc.getString("photoUrl");
                            if (photoUrl != null && !photoUrl.trim().isEmpty()) {
                                Glide.with(this)
                                        .load(photoUrl)
                                        .circleCrop()
                                        .placeholder(R.drawable.avatar) // si lenteur réseau
                                        .into(imgUser);
                            } else {
                                imgUser.setImageResource(R.drawable.avatar);
                            }
                        } else {
                            imgUser.setImageResource(R.drawable.avatar);
                        }
                    } else {
                        tvNom.setText("Nomutilisateur");
                        imgUser.setImageResource(R.drawable.avatar);
                        Toast.makeText(this, "Aucun profil trouvé", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    tvNom.setText("Nomutilisateur");
                    imgUser.setImageResource(R.drawable.avatar);
                    Toast.makeText(this, "Erreur : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }



    private void setupCardListeners() {
        findViewById(R.id.card_factures).setOnClickListener(v -> startActivity(new Intent(this, RattrapageActivity.class)));
        findViewById(R.id.card_annonces).setOnClickListener(v -> startActivity(new Intent(this, AjouterAbsenceActivity.class)));
        findViewById(R.id.card_historique).setOnClickListener(v -> startActivity(new Intent(this, HistoriqueActivity.class)));
        findViewById(R.id.card_reclamations).setOnClickListener(v -> startActivity(new Intent(this, ReclamationsActivity.class)));
        findViewById(R.id.card_proximite).setOnClickListener(v -> startActivity(new Intent(this, MapActivity.class)));
        findViewById(R.id.card_planning).setOnClickListener(v -> startActivity(new Intent(this, PlanningActivity.class)));
        findViewById(R.id.card_documents).setOnClickListener(v -> startActivity(new Intent(this, DocumentsActivity.class)));
        findViewById(R.id.card_logout).setOnClickListener(v -> {
            mAuth.signOut();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Assistant virtuel
        findViewById(R.id.card_assistant).setOnClickListener(v -> {
            Intent intent = new Intent(this, GeminiChatActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.card_guide).setOnClickListener(v -> {
            Intent intent = new Intent(this, GuideProfActivity.class);
            startActivity(intent);
        });

    }

    private void requestLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        LocationRequest request = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(1);

        fusedLocationClient.requestLocationUpdates(request, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                if (location != null) {
                    String coords = "Lat: " + location.getLatitude() + ", Lng: " + location.getLongitude();
                    Toast.makeText(MenuActivity.this, coords, Toast.LENGTH_SHORT).show();
                }
            }
        }, null);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            requestLocation();
        }
    }
}
