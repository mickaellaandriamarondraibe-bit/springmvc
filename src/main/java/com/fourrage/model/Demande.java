package com.fourrage.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "demande")
public class Demande {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "commune_id", nullable = false)
    private Long communeId;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "client_id", nullable = false)
    private Long clientId;

    @ManyToOne
    @JoinColumn(name = "client_id", insertable = false, updatable = false, nullable = false)
    private Client clientDetails;

    
}
    
