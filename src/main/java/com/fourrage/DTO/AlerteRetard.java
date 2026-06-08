package com.fourrage.DTO;

public class AlerteRetard {
    private final String libelle;
    private final long minutesEcoulees;
    private final long limite;
    private final long difference;
    private final String couleur;
    private final boolean retard;

    public AlerteRetard(
            String libelle,
            long minutesEcoulees,
            long limite,
            long difference,
            String couleur,
            boolean retard) {
        this.libelle = libelle;
        this.minutesEcoulees = minutesEcoulees;
        this.limite = limite;
        this.difference = difference;
        this.couleur = couleur;
        this.retard = retard;
    }

    public String getLibelle() {
        return libelle;
    }

    public long getMinutesEcoulees() {
        return minutesEcoulees;
    }

    public long getLimite() {
        return limite;
    }

    public long getDifference() {
        return difference;
    }

    public String getCouleur() {
        return couleur;
    }

    public boolean isRetard() {
        return retard;
    }
}
