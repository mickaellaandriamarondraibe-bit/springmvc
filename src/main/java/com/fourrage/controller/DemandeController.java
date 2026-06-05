package com.fourrage.controller;

import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.fourrage.DTO.AlerteRetard;
import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;
import com.fourrage.repository.ClientRepository;
import com.fourrage.repository.CommuneRepository;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DistrictRepository;
import com.fourrage.repository.RegionRepository;
import com.fourrage.repository.StatusRepository;
import com.fourrage.repository.DemandeStatusRepository;
@Controller
@RequestMapping("/demande")
public class DemandeController {

    private static final Long STATUS_Cree_ID = 1L;
    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CommuneRepository communeRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private DistrictRepository districtRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired
    private DemandeStatusRepository demandeStatusRepository;

    @Autowired
    private DemandeStatusControler demandeStatusControler;

    @GetMapping("/form")
    public String showDemandeForm(Model model) {
        model.addAttribute("idclient" , clientRepository.findAll()); 
        model.addAttribute("regions", regionRepository.findAll());
        model.addAttribute("demande", new Demande());
        return "createDemande";
    }


    @PostMapping("/submit")
    public String submitDemande(@ModelAttribute("demande") Demande demande) {
        Demande savedDemande = demandeRepository.save(demande);
        statusRepository.findById(STATUS_Cree_ID).ifPresent(status -> {
            DemandeStatus demandeStatus = new DemandeStatus();
            demandeStatus.setDemande(savedDemande);
            demandeStatus.setStatus(status);
            demandeStatus.setDateChangement(java.time.LocalDateTime.now());
            demandeStatusRepository.save(demandeStatus);
        });
        return "redirect:/demande/list";
    }

    @GetMapping("/list")
    public String listDemandes(Model model) {
        List<Demande> demandes = demandeRepository.findAll();
        Map<Long, List<AlerteRetard>> alertesDemandes = new LinkedHashMap<>();
        LocalDateTime maintenant = LocalDateTime.now();

        for (Demande demande : demandes) {
            alertesDemandes.put(
                    demande.getId(),
                    demandeStatusControler.getAlertesRetardDemande(demande.getId(), maintenant));
        }

        model.addAttribute("demandes", demandes);
        model.addAttribute("alertesDemandes", alertesDemandes);
        return "listDemande";
    }

    
    @GetMapping(value = "/communes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getCommunesByDistrict(@RequestParam("districtId") Long districtId) {
        List<Map<String, Object>> rows = communeRepository.findByDistrictId(districtId)
                .stream()
                .map(commune -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", commune.getId());
                    row.put("libelle", commune.getLibelle());
                    return row;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(toJson(rows));
    }
            

    @GetMapping(value = "/districts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getDistrictsByRegion(@RequestParam("regionId") Long regionId) {
        List<Map<String, Object>> rows = districtRepository.findByRegionId(regionId)
                .stream()
                .map(district -> {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("id", district.getId());
                    row.put("libelle", district.getLibelle());
                    return row;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(toJson(rows));
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "demandeSuccess";
    }

    private String toJson(List<Map<String, Object>> rows) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < rows.size(); i++) {
            Map<String, Object> row = rows.get(i);
            if (i > 0) {
                sb.append(",");
            }
            sb.append("{")
              .append("\"id\":").append(row.get("id"))
              .append(",\"libelle\":\"")
              .append(escapeJson(String.valueOf(row.get("libelle"))))
              .append("\"}");
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }

}
