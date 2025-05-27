package com.example.test;

import android.os.Bundle;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GuideProfActivity extends AppCompatActivity {

    private TextView textGuide;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide_prof);

        textGuide = findViewById(R.id.textGuide);
        db = FirebaseFirestore.getInstance();

        chargerGuideDepuisFirestore();
    }

    private void chargerGuideDepuisFirestore() {
        db.collection("guides").document("guide")
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && documentSnapshot.contains("texte_prof")) {
                        String guideText = documentSnapshot.getString("texte_prof");
                        if (guideText != null && !guideText.trim().isEmpty()) {
                            textGuide.setText(guideText);
                        } else {
                            afficherTexteParDefaut("Le champ 'texte_prof' est vide.");
                        }
                    } else {
                        afficherTexteParDefaut("Champ ou document manquant.");
                    }
                })
                .addOnFailureListener(e -> {
                    afficherTexteParDefaut("Erreur réseau : " + e.getMessage());
                });
    }

    private void afficherTexteParDefaut(String message) {
        textGuide.setText(
                "🧭 Guide d’utilisation – Professeur\n\n" +
                        "1️⃣ Accueil – Tableau de bord\n" +
                        "- Accédez à toutes les sections via les cartes du tableau de bord.\n" +
                        "- Votre nom, prénom et photo de profil sont affichés en haut.\n\n" +
                        "2️⃣ Absences des étudiants\n" +
                        "- ➕ Ajouter : Remplissez le nom de l’étudiant, la date, le module et le motif.\n" +
                        "- 👁️ Voir : Vous ne voyez que les absences liées à vos propres modules.\n\n" +
                        "3️⃣ Réclamations\n" +
                        "- Consultez les réclamations déposées par les étudiants.\n\n" +
                        "4️⃣ Historique des connexions\n" +
                        "- Suivez vos dernières connexions à l’application.\n\n" +
                        "5️⃣ Planning\n" +
                        "- Consultez votre emploi du temps à jour.\n\n" +
                        "6️⃣ Documents\n" +
                        "- Téléchargez ou envoyez des supports pédagogiques.\n\n" +
                        "7️⃣ Assistant IA\n" +
                        "- Posez vos questions à l’assistant intégré.\n\n" +
                        "8️⃣ Profil\n" +
                        "- Modifiez votre nom, photo, mot de passe.\n\n" +
                        "9️⃣ À proximité\n" +
                        "- Localisez les campus et services utiles.\n\n" +
                        "🔟 Déconnexion\n" +
                        "- Quittez l’application de façon sécurisée.\n\n" +
                        "📝 Bon usage de l’application !"
        );

        Toast.makeText(this, "Chargement local : " + message, Toast.LENGTH_SHORT).show();
    }
}
