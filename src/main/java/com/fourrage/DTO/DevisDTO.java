package com.fourrage.DTO;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DevisDTO {
    private String observation;
    private List<String> libelles;
    private List<Integer> qtes;
    private List<Double> prixUnitaires;
    private Long typeId;
    private Long demandeId;
    private Long statusId;

}
