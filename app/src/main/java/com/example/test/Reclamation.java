package com.example.test.models;

public class Reclamation {
    private String objet;
    private String message;
    private String date;

    public Reclamation() {}

    public Reclamation(String objet, String message, String date) {
        this.objet = objet;
        this.message = message;
        this.date = date;
    }

    public String getObjet() { return objet; }
    public String getMessage() { return message; }
    public String getDate() { return date; }
}
