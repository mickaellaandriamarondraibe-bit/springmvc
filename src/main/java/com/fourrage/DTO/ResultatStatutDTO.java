package com.fourrage.DTO;

import com.fourrage.model.DemandeStatus;

public class ResultatStatutDTO {
    private final DemandeStatus trace;
    private final Long dureeTravail;
    private final Long limite;
    private final long difference;
    private final String couleur;
    private final boolean retard;
    private final boolean enCours;

    public ResultatStatutDTO(
            DemandeStatus trace,
            Long dureeTravail,
            Long limite,
            long difference,
            String couleur,
            boolean retard,
            boolean enCours) {
        this.trace = trace;
        this.dureeTravail = dureeTravail;
        this.limite = limite;
        this.difference = difference;
        this.couleur = couleur;
        this.retard = retard;
        this.enCours = enCours;
    }

    public DemandeStatus getTrace() {
        return trace;
    }

    public Long getDureeTravail() {
        return dureeTravail;
    }

    public Long getLimite() {
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

    public boolean isEnCours() {
        return enCours;
    }
}
