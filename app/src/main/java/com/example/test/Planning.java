package com.example.test.models;

public class Planning {
    private String titre;
    private String date;
    private String heure;
    private String lieu;
    private String description;

    public Planning() {
        // Nécessaire pour Firestore
    }

    public Planning(String titre, String date, String heure, String lieu, String description) {
        this.titre = titre;
        this.date = date;
        this.heure = heure;
        this.lieu = lieu;
        this.description = description;
    }

    public String getTitre() { return titre; }
    public String getDate() { return date; }
    public String getHeure() { return heure; }
    public String getLieu() { return lieu; }
    public String getDescription() { return description; }

    public void setTitre(String titre) { this.titre = titre; }
    public void setDate(String date) { this.date = date; }
    public void setHeure(String heure) { this.heure = heure; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public void setDescription(String description) { this.description = description; }
}
