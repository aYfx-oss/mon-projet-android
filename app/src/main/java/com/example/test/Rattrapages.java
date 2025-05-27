package com.example.test.models;

public class Rattrapages {
    private String matiere;
    private String date;
    private String heure;
    private String salle;

    public Rattrapages() {} // requis par Firestore

    public Rattrapages(String matiere, String date, String heure, String salle) {
        this.matiere = matiere;
        this.date = date;
        this.heure = heure;
        this.salle = salle;
    }

    public String getMatiere() { return matiere; }
    public String getDate() { return date; }
    public String getHeure() { return heure; }
    public String getSalle() { return salle; }
}
