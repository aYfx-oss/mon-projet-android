package com.example.test.models;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Rattrapages {
    private String matiere;
    private String date;
    private String heure;
    private String salle;
    private String professeur_id;

    // Constructeur vide obligatoire pour Firestore
    public Rattrapages() {}

    public Rattrapages(String matiere, String date, String heure, String salle, String professeur_id) {
        this.matiere = matiere;
        this.date = date;
        this.heure = heure;
        this.salle = salle;
        this.professeur_id = professeur_id;
    }

    public String getMatiere() { return matiere; }
    public void setMatiere(String matiere) { this.matiere = matiere; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getHeure() { return heure; }
    public void setHeure(String heure) { this.heure = heure; }

    public String getSalle() { return salle; }
    public void setSalle(String salle) { this.salle = salle; }

    public String getProfesseur_id() { return professeur_id; }
    public void setProfesseur_id(String professeur_id) { this.professeur_id = professeur_id; }
}
