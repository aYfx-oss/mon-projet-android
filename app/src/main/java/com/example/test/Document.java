package com.example.test.models;

public class Document {
    private String titre;
    private String nomFichier;
    private String url;
    private String dateUpload;
    private String description;

    public Document() {} // Obligatoire pour Firestore

    public Document(String titre, String nomFichier, String url, String dateUpload, String description) {
        this.titre = titre;
        this.nomFichier = nomFichier;
        this.url = url;
        this.dateUpload = dateUpload;
        this.description = description;
    }

    public String getTitre() { return titre; }
    public String getNomFichier() { return nomFichier; }
    public String getUrl() { return url; }
    public String getDateUpload() { return dateUpload; }
    public String getDescription() { return description; }
}
