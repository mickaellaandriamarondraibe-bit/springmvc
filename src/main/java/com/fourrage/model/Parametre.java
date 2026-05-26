package com.fourrage.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
@Entity 
@Table(name = "parametre")
public class Parametre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libelle", nullable = false)
    private String libelle;

    @Column(name = "duree_minimum", nullable = false)
    private Integer dureeMinimum;

    @Column(name = "couleur", nullable = false)
    private String couleur;

    @Column(name = "status_id_depart", nullable = false)
    private Long statusIdDepart;

    @Column(name = "status_id_arrivee", nullable = false)
    private Long statusIdArrivee;
    
}
