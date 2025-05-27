package com.example.test.models;

public class Historique {
    private String action;
    private String date;

    public Historique() {} // Obligatoire pour Firestore

    public Historique(String action, String date) {
        this.action = action;
        this.date = date;
    }

    public String getAction() {
        return action;
    }

    public String getDate() {
        return date;
    }
}
