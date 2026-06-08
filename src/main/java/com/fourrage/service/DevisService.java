package com.fourrage.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fourrage.DTO.DevisDTO;
import com.fourrage.model.Demande;
import com.fourrage.model.Devis;
import com.fourrage.model.DevisDetail;
import com.fourrage.model.Status;
import com.fourrage.model.TypeDevis;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DevisDetailRepository;
import com.fourrage.repository.DevisRepository;
import com.fourrage.repository.StatusRepository;
import com.fourrage.repository.TypeDevisRepository;

@Service
public class DevisService {

    private static final Long TYPE_FORAGE_ID = 2L;

    private final DevisRepository devisRepository;
    private final DevisDetailRepository devisDetailRepository;
    private final DemandeRepository demandeRepository;
    private final TypeDevisRepository typeDevisRepository;
    private final StatusRepository statusRepository;
    private final DemandeStatusService demandeStatusService;

    public DevisService(
            DevisRepository devisRepository,
            DevisDetailRepository devisDetailRepository,
            DemandeRepository demandeRepository,
            TypeDevisRepository typeDevisRepository,
            StatusRepository statusRepository,
            DemandeStatusService demandeStatusService) {
        this.devisRepository = devisRepository;
        this.devisDetailRepository = devisDetailRepository;
        this.demandeRepository = demandeRepository;
        this.typeDevisRepository = typeDevisRepository;
        this.statusRepository = statusRepository;
        this.demandeStatusService = demandeStatusService;
    }

    public List<Demande> findAllDemandes() {
        return demandeRepository.findAll();
    }

    public List<TypeDevis> findAllTypes() {
        return typeDevisRepository.findAll();
    }

    public Map<Long, Status> getStatusMap() {
        Map<Long, Status> statusMap = new LinkedHashMap<>();
        for (Status status : statusRepository.findAll()) {
            statusMap.put(status.getId(), status);
        }
        return statusMap;
    }

    public boolean isForageLocked(Long demandeId) {
        return demandeRepository.findById(demandeId)
                .map(demande -> devisRepository.existsByDemandeAndTypeId(demande, TYPE_FORAGE_ID))
                .orElse(false);
    }

    @Transactional
    public String creer(DevisDTO dto) {
        if (dto.getDemandeId() == null || dto.getTypeId() == null) {
            return "Demande et type de devis sont obligatoires.";
        }
        if (dto.getLibelles() == null || dto.getQtes() == null || dto.getPrixUnitaires() == null) {
            return "Les lignes du devis sont invalides.";
        }

        Optional<Demande> demandeOpt = demandeRepository.findById(dto.getDemandeId());
        if (demandeOpt.isEmpty()) {
            return "Demande introuvable.";
        }

        Demande demande = demandeOpt.get();
        if (TYPE_FORAGE_ID.equals(dto.getTypeId())
                && devisRepository.existsByDemandeAndTypeId(demande, TYPE_FORAGE_ID)) {
            return "Un devis Forage existe deja pour cette demande.";
        }

        Optional<TypeDevis> typeDevisOpt = typeDevisRepository.findById(dto.getTypeId());
        if (typeDevisOpt.isEmpty()) {
            return "Type de devis introuvable.";
        }

        Devis devis = new Devis();
        devis.setObservation(dto.getObservation());
        devis.setDateCreation(LocalDateTime.now());
        devis.setDemande(demande);
        devis.setType(typeDevisOpt.get());
        Devis savedDevis = devisRepository.save(devis);

        int taille = Math.min(dto.getLibelles().size(), Math.min(dto.getQtes().size(), dto.getPrixUnitaires().size()));
        List<DevisDetail> details = new ArrayList<>();
        for (int index = 0; index < taille; index++) {
            DevisDetail detail = new DevisDetail();
            detail.setLibelle(dto.getLibelles().get(index));
            detail.setQte(dto.getQtes().get(index));
            detail.setPrixUnitaire(dto.getPrixUnitaires().get(index));
            detail.setDevis(savedDevis);
            details.add(detail);
        }
        devisDetailRepository.saveAll(details);

        if (dto.getStatusId() != null) {
            demandeStatusService.creerStatus(demande, dto.getStatusId());
        }
        return null;
    }

    public boolean typeExists(Long typeId) {
        return typeDevisRepository.existsById(typeId);
    }

    public Optional<Demande> findDemande(Long demandeId) {
        return demandeRepository.findById(demandeId);
    }

    public List<Devis> findByDemande(Demande demande) {
        return devisRepository.findByDemande(demande);
    }

    public Map<Long, List<DevisDetail>> getDetailsByDevis(List<Devis> devisList) {
        Map<Long, List<DevisDetail>> detailsByDevis = new LinkedHashMap<>();
        for (Devis devis : devisList) {
            detailsByDevis.put(devis.getId(), devisDetailRepository.findByDevis(devis));
        }
        return detailsByDevis;
    }

    public List<Devis> findAll() {
        return devisRepository.findAll();
    }
}
