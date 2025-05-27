package com.example.test.models;

public class Facture {
    private String titre;
    private String montant;
    private String date;

    public Facture() {} // Nécessaire pour Firestore

    public Facture(String titre, String montant, String date) {
        this.titre = titre;
        this.montant = montant;
        this.date = date;
    }

    public String getTitre() {
        return titre;
    }

    public String getMontant() {
        return montant;
    }

    public String getDate() {
        return date;
    }
}
