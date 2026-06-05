package com.fourrage.DTO;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DemandeStatusDTO {
    private Long demandeId;
    private Long statusId;
    private String observation;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime dateChangement;
}
