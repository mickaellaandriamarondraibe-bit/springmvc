package com.fourrage.DTO;
import java.time.LocalDateTime;

public class DashboardRow {
    private final Long demandeId;
    private final String statutActuel;
    private final LocalDateTime dateDernierChangement;
    private final Long dureeMinutes;
    private final Integer seuilMinutes;
    private final String couleur;
    private final String message;

    public DashboardRow(Long demandeId, String statutActuel, LocalDateTime dateDernierChangement, Long dureeMinutes,
            Integer seuilMinutes, String couleur, String message) {
        this.demandeId = demandeId;
        this.statutActuel = statutActuel;
        this.dateDernierChangement = dateDernierChangement;
        this.dureeMinutes = dureeMinutes;
        this.seuilMinutes = seuilMinutes;
        this.couleur = couleur;
        this.message = message;
    }

    public Long getDemandeId() { return demandeId; }
    public String getStatutActuel() { return statutActuel; }
    public LocalDateTime getDateDernierChangement() { return dateDernierChangement; }
    public Long getDureeMinutes() { return dureeMinutes; }
    public Integer getSeuilMinutes() { return seuilMinutes; }
    public String getCouleur() { return couleur; }
    public String getMessage() { return message; }
}
