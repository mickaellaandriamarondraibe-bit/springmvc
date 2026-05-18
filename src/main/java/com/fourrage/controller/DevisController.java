package com.fourrage.controller;

import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;
import com.fourrage.model.Devis;
import com.fourrage.model.DevisDetail;
import com.fourrage.model.Status;
import com.fourrage.model.TypeDevis;
import com.fourrage.repository.DemandeStatusRepository;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DevisDetailRepository;
import com.fourrage.repository.DevisRepository;
import com.fourrage.repository.StatusRepository;
import com.fourrage.repository.TypeDevisRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
public class DevisController {
    private Map<Long, Status> statusById = new LinkedHashMap<>();

    @Autowired
    private DevisRepository devisRepository;

    @Autowired
    private DevisDetailRepository devisDetailRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private TypeDevisRepository typeDevisRepository;

    @Autowired
    private DemandeStatusRepository demandeStatusRepository;

    @Autowired
    private StatusRepository statusRepository;

    @PostConstruct
    private void initStatusCache() {
        Map<Long, Status> cache = new LinkedHashMap<>();
        for (Status status : statusRepository.findAll()) {
            cache.put(status.getId(), status);
        }
        statusById = cache;
    }
    
    @GetMapping("/devisDemande")
    public String showForm(@RequestParam(value = "demandeId", required = false) Long demandeId, Model model) {
        prepareDevisFormModel(model, demandeId);
        return "devisDemande";
    }

    @GetMapping("/devisDemande/{id}")
    public String showFormByPath(@PathVariable("id") Long id, Model model) {
        return showForm(id, model);
    }

    @GetMapping(value = "/devisDemande/forageLocked", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> isForageLocked(@RequestParam("demandeId") Long demandeId) {
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        boolean forageLocked = demandeOpt.isPresent()
                && devisRepository.existsByDemandeAndTypeId(demandeOpt.get(), 2L);
        return ResponseEntity.ok("{\"forageLocked\":" + forageLocked + "}");
    }

    @PostMapping("/devisDemande")
    public String submitDevis(
            @RequestParam("observation") String observation,
            @RequestParam("libelle") List<String> libelles,
            @RequestParam("qte") List<Integer> qtes,
            @RequestParam("prixUnitaire") List<Double> prixUnitaires,
            @RequestParam("typeId") Long typeId,
            @RequestParam("demandeId") Long demandeId,
            @RequestParam(value = "statusId", required = false) Long statusId,
            Model model
    ) {
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);

        if (demandeOpt.isEmpty()) {
            model.addAttribute("message", "Demande introuvable.");
            prepareDevisFormModel(model, demandeId);
            return "devisDemande";
        }

        Demande demande = demandeOpt.get();
        boolean forageDejaCree = devisRepository.existsByDemandeAndTypeId(demande, 2L);
        if (Long.valueOf(2L).equals(typeId) && forageDejaCree) {
            model.addAttribute("message", "Un devis Forage existe deja pour cette demande.");
            prepareDevisFormModel(model, demandeId);
            return "devisDemande";
        }


        Optional<TypeDevis> typeDevisOpt = typeDevisRepository.findById(typeId);
        if (typeDevisOpt.isEmpty()) {
            model.addAttribute("message", "Type de devis introuvable.");
            prepareDevisFormModel(model, demandeId);
            return "devisDemande";
        }
        TypeDevis typeDevis = typeDevisOpt.get();

        Devis devis = new Devis();
        devis.setObservation(observation);
        devis.setDateCreation(java.time.LocalDateTime.now());
        devis.setDemande(demande);
        devis.setType(typeDevis);
        Devis savedDevis = devisRepository.save(devis);

        int taille = Math.min(libelles.size(), Math.min(qtes.size(), prixUnitaires.size()));
        List<DevisDetail> details = new ArrayList<>();
        for (int i = 0; i < taille; i++) {
            DevisDetail detail = new DevisDetail();
            detail.setLibelle(libelles.get(i));
            detail.setQte(qtes.get(i));
            detail.setPrixUnitaire(prixUnitaires.get(i));
            detail.setDevis(savedDevis);
            details.add(detail);
        }
        devisDetailRepository.saveAll(details);

        if (statusId != null) {
            Status status = statusById.get(statusId);
            if (status != null) {
                DemandeStatus demandeStatus = new DemandeStatus();
                demandeStatus.setDemande(demande);
                demandeStatus.setStatus(status);
                demandeStatus.setDateChangement(java.time.LocalDateTime.now());
                demandeStatusRepository.save(demandeStatus);
            }
        }

        model.addAttribute("message", "Devis soumis avec succès !");
        prepareDevisFormModel(model, demandeId);

        return "devisDemande";
    }

    @PostMapping("/devisDemande/verifType")
    public String verificationTypeDevis(@RequestParam("typeId") Long typeId, Model model) {
        Optional<TypeDevis> typeDevisOpt = typeDevisRepository.findById(typeId);
        prepareDevisFormModel(model, null);

        if (typeDevisOpt.isEmpty()) {
            model.addAttribute("message", "Type devis introuvable.");
            return "devisDemande";
        }

        if (Long.valueOf(2L).equals(typeId)) {
            model.addAttribute("message", "Type Forage non autorise ici. Utilisez uniquement Type Etude.");
        }

        return "devisDemande";
    }

    private void prepareDevisFormModel(Model model, Long demandeId) {
        model.addAttribute("demandeId", demandeId);
        model.addAttribute("demandes", demandeRepository.findAll());
        model.addAttribute("typesDevis", typeDevisRepository.findAll());
        model.addAttribute("statusMap", statusById);

        boolean forageLocked = false;
        if (demandeId != null) {
            Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
            if (demandeOpt.isPresent()) {
                forageLocked = devisRepository.existsByDemandeAndTypeId(demandeOpt.get(), 2L);
            }
        }
        model.addAttribute("forageLocked", forageLocked);
    }

    @PostMapping("/devisDemande/details")
    public String showDevisDetails(@RequestParam("demandeId") Long demandeId, Model model) {
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        if (demandeOpt.isEmpty()) {
            model.addAttribute("message", "Demande introuvable.");
            return "devisDetails";
        }
        Demande demande = demandeOpt.get();
        List<Devis> devisList = devisRepository.findByDemande(demande);
        java.util.Map<Long, List<DevisDetail>> detailsByDevis = new java.util.LinkedHashMap<>();
        for (Devis devis : devisList) {
            detailsByDevis.put(devis.getId(), devisDetailRepository.findByDevis(devis));
        }
        model.addAttribute("devisList", devisList);
        model.addAttribute("detailsByDevis", detailsByDevis);
        return "devisDetails";  
    }

    @PostMapping("/exporterPDF")
    public String exporterPdf(
            @RequestParam("libelle") List<String> libelles,
            @RequestParam("qte") List<Integer> qtes,
            @RequestParam("prixUnitaire") List<Double> prixUnitaires,
            Model model
    ) {
        model.addAttribute("libelles", libelles);
        model.addAttribute("qtes", qtes);
        model.addAttribute("prixUnitaires", prixUnitaires);
        return "expoterPDF";
    }

    @GetMapping("/devisDemande/list")
    public String listDevis(Model model) {
        model.addAttribute("devisList", devisRepository.findAll());
        return "listeDevis";
    }
}
