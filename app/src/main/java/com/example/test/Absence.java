package com.example.test.models;

public class Absence {
    private String etudiantId;
    private String nomEtudiant;
    private String date;
    private String motif;
    private String module;
    private String professeurId; // ✅ Ajout du champ pour identifier le prof

    // 🔸 Constructeur vide requis par Firestore
    public Absence() {}

    // 🔸 Constructeur complet
    public Absence(String etudiantId, String nomEtudiant, String date, String motif, String module, String professeurId) {
        this.etudiantId = etudiantId;
        this.nomEtudiant = nomEtudiant;
        this.date = date;
        this.motif = motif;
        this.module = module;
        this.professeurId = professeurId;
    }

    // 🔸 Getters et Setters
    public String getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(String etudiantId) {
        this.etudiantId = etudiantId;
    }

    public String getNomEtudiant() {
        return nomEtudiant;
    }

    public void setNomEtudiant(String nomEtudiant) {
        this.nomEtudiant = nomEtudiant;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getModule() {
        return module;
    }

    public void setModule(String module) {
        this.module = module;
    }

    public String getProfesseurId() {
        return professeurId;
    }

    public void setProfesseurId(String professeurId) {
        this.professeurId = professeurId;
    }
}
