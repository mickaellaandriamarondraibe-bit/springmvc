package com.fourrage.service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fourrage.DTO.AlerteRetard;
import com.fourrage.model.Client;
import com.fourrage.model.Commune;
import com.fourrage.model.Demande;
import com.fourrage.model.District;
import com.fourrage.model.Region;
import com.fourrage.repository.ClientRepository;
import com.fourrage.repository.CommuneRepository;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DistrictRepository;
import com.fourrage.repository.RegionRepository;

@Service
public class DemandeService {

    private static final Long STATUS_CREE_ID = 1L;

    private final DemandeRepository demandeRepository;
    private final ClientRepository clientRepository;
    private final CommuneRepository communeRepository;
    private final RegionRepository regionRepository;
    private final DistrictRepository districtRepository;
    private final DemandeStatusService demandeStatusService;

    public DemandeService(
            DemandeRepository demandeRepository,
            ClientRepository clientRepository,
            CommuneRepository communeRepository,
            RegionRepository regionRepository,
            DistrictRepository districtRepository,
            DemandeStatusService demandeStatusService) {
        this.demandeRepository = demandeRepository;
        this.clientRepository = clientRepository;
        this.communeRepository = communeRepository;
        this.regionRepository = regionRepository;
        this.districtRepository = districtRepository;
        this.demandeStatusService = demandeStatusService;
    }

    public List<Client> findAllClients() {
        return clientRepository.findAll();
    }

    public List<Region> findAllRegions() {
        return regionRepository.findAll();
    }

    public List<Demande> findAll() {
        return demandeRepository.findAll();
    }

    @Transactional
    public Demande creer(Demande demande) {
        Demande savedDemande = demandeRepository.save(demande);
        demandeStatusService.creerStatusInitial(savedDemande, STATUS_CREE_ID);
        return savedDemande;
    }

    public Map<Long, List<AlerteRetard>> getAlertesDemandes(List<Demande> demandes) {
        Map<Long, List<AlerteRetard>> alertesDemandes = new LinkedHashMap<>();
        LocalDateTime maintenant = LocalDateTime.now();
        for (Demande demande : demandes) {
            alertesDemandes.put(
                    demande.getId(),
                    demandeStatusService.getAlertesRetardDemande(demande.getId(), maintenant));
        }
        return alertesDemandes;
    }

    public List<Map<String, Object>> findCommunesByDistrict(Long districtId) {
        return communeRepository.findByDistrictId(districtId).stream()
                .map(this::toRow)
                .toList();
    }

    public List<Map<String, Object>> findDistrictsByRegion(Long regionId) {
        return districtRepository.findByRegionId(regionId).stream()
                .map(this::toRow)
                .toList();
    }

    private Map<String, Object> toRow(Commune commune) {
        return createRow(commune.getId(), commune.getLibelle());
    }

    private Map<String, Object> toRow(District district) {
        return createRow(district.getId(), district.getLibelle());
    }

    private Map<String, Object> createRow(Long id, String libelle) {
        Map<String, Object> row = new LinkedHashMap<>();
        row.put("id", id);
        row.put("libelle", libelle);
        return row;
    }
}
