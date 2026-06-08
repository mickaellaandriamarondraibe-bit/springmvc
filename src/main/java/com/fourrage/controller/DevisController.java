package com.fourrage.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fourrage.DTO.DevisDTO;
import com.fourrage.model.Demande;
import com.fourrage.model.Devis;
import com.fourrage.service.DevisService;

@Controller
public class DevisController {

    private static final Long TYPE_FORAGE_ID = 2L;

    private final DevisService devisService;

    public DevisController(DevisService devisService) {
        this.devisService = devisService;
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
        return ResponseEntity.ok("{\"forageLocked\":" + devisService.isForageLocked(demandeId) + "}");
    }

    @PostMapping("/devisDemande")
    public String submitDevis(@ModelAttribute DevisDTO devisDTO, Model model) {
        String erreur = devisService.creer(devisDTO);
        model.addAttribute("message", erreur == null ? "Devis soumis avec succès !" : erreur);
        prepareDevisFormModel(model, devisDTO.getDemandeId());
        return "devisDemande";
    }

    @PostMapping("/devisDemande/verifType")
    public String verificationTypeDevis(@RequestParam("typeId") Long typeId, Model model) {
        prepareDevisFormModel(model, null);
        if (!devisService.typeExists(typeId)) {
            model.addAttribute("message", "Type devis introuvable.");
        } else if (TYPE_FORAGE_ID.equals(typeId)) {
            model.addAttribute("message", "Type Forage non autorise ici. Utilisez uniquement Type Etude.");
        }
        return "devisDemande";
    }

    @PostMapping("/devisDemande/details")
    public String showDevisDetails(@RequestParam(value = "demandeId", required = false) Long demandeId, Model model) {
        if (demandeId == null) {
            model.addAttribute("message", "Veuillez selectionner une demande.");
            prepareDevisFormModel(model, null);
            return "devisDemande";
        }

        Optional<Demande> demandeOpt = devisService.findDemande(demandeId);
        if (demandeOpt.isEmpty()) {
            model.addAttribute("message", "Demande introuvable.");
            return "devisDetails";
        }

        List<Devis> devisList = devisService.findByDemande(demandeOpt.get());
        model.addAttribute("devisList", devisList);
        model.addAttribute("detailsByDevis", devisService.getDetailsByDevis(devisList));
        return "devisDetails";
    }

    @PostMapping("/exporterPDF")
    public String exporterPdf(
            @RequestParam("libelle") List<String> libelles,
            @RequestParam("qte") List<Integer> qtes,
            @RequestParam("prixUnitaire") List<Double> prixUnitaires,
            Model model) {
        model.addAttribute("libelles", libelles);
        model.addAttribute("qtes", qtes);
        model.addAttribute("prixUnitaires", prixUnitaires);
        return "expoterPDF";
    }

    @PostMapping("/validationDevis")
    public String validationDevis(@RequestParam("demandeId") Long demandeId, Model model) {
        devisService.findDemande(demandeId);
        return "devisDemande";
    }

    @GetMapping("/devisDemande/list")
    public String listDevis(Model model) {
        model.addAttribute("devisList", devisService.findAll());
        return "listeDevis";
    }

    private void prepareDevisFormModel(Model model, Long demandeId) {
        model.addAttribute("demandeId", demandeId);
        model.addAttribute("demandes", devisService.findAllDemandes());
        model.addAttribute("typesDevis", devisService.findAllTypes());
        model.addAttribute("statusMap", devisService.getStatusMap());
        model.addAttribute("forageLocked", demandeId != null && devisService.isForageLocked(demandeId));
    }
}
