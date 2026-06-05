package com.fourrage.DTO;

public class AlerteRetard {
    private final String libelle;
    private final String message;
    private final String couleur;
    private final boolean retard;

    public AlerteRetard(String libelle, String message, String couleur, boolean retard) {
        this.libelle = libelle;
        this.message = message;
        this.couleur = couleur;
        this.retard = retard;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getMessage() {
        return message;
    }

    public String getCouleur() {
        return couleur;
    }

    public boolean isRetard() {
        return retard;
    }
}
