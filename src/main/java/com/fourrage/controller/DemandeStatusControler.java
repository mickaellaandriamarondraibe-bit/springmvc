package com.fourrage.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fourrage.DTO.AlerteRetard;
import com.fourrage.DTO.DemandeStatusDTO;
import com.fourrage.DTO.ResultatStatutDTO;
import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;
import com.fourrage.service.DemandeStatusService;

@Controller
public class DemandeStatusControler {

    private final DemandeStatusService demandeStatusService;

    public DemandeStatusControler(DemandeStatusService demandeStatusService) {
        this.demandeStatusService = demandeStatusService;
    }

    @GetMapping("/demandeStatus")
    public String showDemandeStatusForm(Model model) {
        model.addAttribute("demandes", demandeStatusService.findAllDemandes());
        model.addAttribute("status", demandeStatusService.findAllStatus());
        return "demandeStatus";
    }

    @GetMapping("/demandeStatus/tracabilite")
    public String listTracabilite(Model model) {
        model.addAttribute("demandeStatus", demandeStatusService.findAllTracabilite());
        model.addAttribute("statusColors", demandeStatusService.getStatusColors());
        return "tracabiliteDemandes";
    }

    @PostMapping("/demandeStatus/ajout")
    public String addDemandeStatus(DemandeStatusDTO dto, RedirectAttributes redirectAttributes) {
        String erreur = demandeStatusService.ajouter(dto);
        if (erreur != null) {
            redirectAttributes.addFlashAttribute("message", erreur);
            redirectAttributes.addFlashAttribute("demandeId", dto.getDemandeId());
        }
        return "redirect:/demandeStatus";
    }

    @PostMapping("/demandeStatus/modifier-date")
    public String modifierDateStatus(
            @RequestParam("demandeStatusId") Long demandeStatusId,
            @RequestParam("demandeId") Long demandeId,
            @RequestParam("nouvelleDate") LocalDateTime nouvelleDate,
            RedirectAttributes redirectAttributes) {
        Optional<Long> demandeModifieeId = demandeStatusService.modifierDateStatus(demandeStatusId, nouvelleDate);
        if (demandeModifieeId.isEmpty()) {
            redirectAttributes.addFlashAttribute(
                    "message",
                    "Date refusée : elle doit être comprise entre les statuts précédent et suivant.");
        }
        return demandeModifieeId
                .map(id -> "redirect:/demandeStatus/demande?demandeId=" + id)
                .orElse("redirect:/demandeStatus/demande?demandeId=" + demandeId);
    }

    @GetMapping("/demandeStatus/demande")
    public String detailDemandeStatus(
            @RequestParam("demandeId") Long demandeId,
            @RequestParam(value = "dateSimulation", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateSimulation,
            Model model) {
        Optional<Demande> demandeOpt = demandeStatusService.findDemande(demandeId);
        if (demandeOpt.isEmpty()) {
            return "redirect:/demande/list";
        }

        Demande demande = demandeOpt.get();
        List<DemandeStatus> historiques = demandeStatusService.findHistorique(demande);
        LocalDateTime dateCalcul = dateSimulation == null ? LocalDateTime.now() : dateSimulation;

        if (dateSimulation != null
                && !historiques.isEmpty()
                && dateCalcul.isBefore(historiques.get(historiques.size() - 1).getDateChangement())) {
            model.addAttribute(
                    "erreurSimulation",
                    "La date de simulation doit être postérieure à l'entrée dans le statut actuel.");
            dateCalcul = LocalDateTime.now();
            dateSimulation = null;
        }

        List<AlerteRetard> alertes = demandeStatusService.getAlertesRetardDemande(demandeId, dateCalcul);

        model.addAttribute("demande", demande);
        model.addAttribute("resultatsHistorique", demandeStatusService.construireResultatsHistorique(historiques));
        model.addAttribute("alerteActuelle", alertes.isEmpty() ? null : alertes.get(0));
        model.addAttribute("dateSimulation", dateSimulation);
        model.addAttribute("dateCalcul", dateCalcul);
        model.addAttribute(
                "dernierStatusId",
                historiques.isEmpty() ? null : historiques.get(historiques.size() - 1).getId());
        return "detailDemandeStatus";
    }

    @GetMapping(value = "/api/demandeStatus/demande", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiDetailDemandeStatus(
            @RequestParam("demandeId") Long demandeId,
            @RequestParam(value = "dateSimulation", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateSimulation) {
        Optional<Demande> demandeOpt = demandeStatusService.findDemande(demandeId);
        if (demandeOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        List<DemandeStatus> historiques = demandeStatusService.findHistorique(demandeOpt.get());
        LocalDateTime dateCalcul = dateSimulation == null ? LocalDateTime.now() : dateSimulation;
        String erreurSimulation = null;

        if (dateSimulation != null
                && !historiques.isEmpty()
                && dateCalcul.isBefore(historiques.get(historiques.size() - 1).getDateChangement())) {
            erreurSimulation = "La date de simulation doit être postérieure à l'entrée dans le statut actuel.";
            dateCalcul = LocalDateTime.now();
            dateSimulation = null;
        }

        List<AlerteRetard> alertes = demandeStatusService.getAlertesRetardDemande(demandeId, dateCalcul);
        List<Map<String, Object>> resultats = new ArrayList<>();

        for (ResultatStatutDTO resultat : demandeStatusService.construireResultatsHistorique(historiques)) {
            Map<String, Object> ligne = new LinkedHashMap<>();
            ligne.put("demandeStatusId", resultat.getTrace().getId());
            ligne.put("statut", resultat.getTrace().getStatus().getLibelle());
            ligne.put("dateChangement", resultat.getTrace().getDateChangement().toString());
            ligne.put("dureeTravail", resultat.getDureeTravail());
            ligne.put("limite", resultat.getLimite());
            ligne.put("difference", resultat.getDifference());
            ligne.put("couleur", resultat.getCouleur());
            ligne.put("retard", resultat.isRetard());
            ligne.put("enCours", resultat.isEnCours());
            resultats.add(ligne);
        }

        Map<String, Object> reponse = new LinkedHashMap<>();
        reponse.put("demandeId", demandeId);
        reponse.put("dateCalcul", dateCalcul.toString());
        reponse.put("dateSimulation", dateSimulation == null ? null : dateSimulation.toString());
        reponse.put("erreurSimulation", erreurSimulation);
        reponse.put("alerteActuelle", alertes.isEmpty() ? null : creerAlerteApi(alertes.get(0)));
        reponse.put("resultatsHistorique", resultats);
        return ResponseEntity.ok(reponse);
    }

    @PostMapping(value = "/api/demandeStatus/modifier-date", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> apiModifierDateStatus(
            @RequestParam("demandeStatusId") Long demandeStatusId,
            @RequestParam("nouvelleDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime nouvelleDate) {
        Optional<Long> demandeId = demandeStatusService.modifierDateStatus(demandeStatusId, nouvelleDate);
        Map<String, Object> reponse = new LinkedHashMap<>();

        if (demandeId.isEmpty()) {
            reponse.put("succes", false);
            reponse.put("message", "Date refusée : elle doit être comprise entre les statuts précédent et suivant.");
            return ResponseEntity.badRequest().body(reponse);
        }

        reponse.put("succes", true);
        reponse.put("demandeId", demandeId.get());
        return ResponseEntity.ok(reponse);
    }

    private Map<String, Object> creerAlerteApi(AlerteRetard alerte) {
        Map<String, Object> resultat = new LinkedHashMap<>();
        resultat.put("libelle", alerte.getLibelle());
        resultat.put("minutesEcoulees", alerte.getMinutesEcoulees());
        resultat.put("limite", alerte.getLimite());
        resultat.put("difference", alerte.getDifference());
        resultat.put("couleur", alerte.getCouleur());
        resultat.put("retard", alerte.isRetard());
        return resultat;
    }
}
