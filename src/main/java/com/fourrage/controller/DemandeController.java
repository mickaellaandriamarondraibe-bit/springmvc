package com.fourrage.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fourrage.model.Demande;
import com.fourrage.service.DemandeService;

@Controller
@RequestMapping("/demande")
public class DemandeController {

    private final DemandeService demandeService;

    public DemandeController(DemandeService demandeService) {
        this.demandeService = demandeService;
    }

    @GetMapping("/form")
    public String showDemandeForm(Model model) {
        model.addAttribute("idclient", demandeService.findAllClients());
        model.addAttribute("regions", demandeService.findAllRegions());
        model.addAttribute("demande", new Demande());
        return "createDemande";
    }

    @PostMapping("/submit")
    public String submitDemande(@ModelAttribute("demande") Demande demande) {
        demandeService.creer(demande);
        return "redirect:/demande/list";
    }

    @GetMapping("/list")
    public String listDemandes(Model model) {
        List<Demande> demandes = demandeService.findAll();
        model.addAttribute("demandes", demandes);
        model.addAttribute("alertesDemandes", demandeService.getAlertesDemandes(demandes));
        return "listDemande";
    }

    @GetMapping(value = "/communes", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getCommunesByDistrict(
            @RequestParam("districtId") Long districtId) {
        return ResponseEntity.ok(demandeService.findCommunesByDistrict(districtId));
    }

    @GetMapping(value = "/districts", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<Map<String, Object>>> getDistrictsByRegion(
            @RequestParam("regionId") Long regionId) {
        return ResponseEntity.ok(demandeService.findDistrictsByRegion(regionId));
    }

    @GetMapping("/success")
    public String showSuccessPage() {
        return "demandeSuccess";
    }
}
