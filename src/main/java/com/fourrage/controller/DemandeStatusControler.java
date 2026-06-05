package com.fourrage.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fourrage.DTO.DemandeStatusDTO;
import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;
import com.fourrage.model.Parametre;
import com.fourrage.model.Status;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DemandeStatusRepository;
import com.fourrage.repository.ParametreStatus;
import com.fourrage.repository.StatusRepository;
import com.fourrage.DTO.AlerteRetard;

@Controller
public class DemandeStatusControler {

    @Autowired
    private DemandeStatusRepository demandeStatusRepository;

    @Autowired
    private DemandeRepository demandeRepository;

    @Autowired
    private StatusRepository statusRepository;

    @Autowired 
    private ParametreStatus parametreStatusRepository;

    @GetMapping("/demandeStatus")
    public String showDemandeStatusForm(Model model) {
        model.addAttribute("demandes", demandeRepository.findAll());
        model.addAttribute("status", statusRepository.findAll());
        return "demandeStatus";
    }

    @GetMapping("/demandeStatus/tracabilite")
    public String listTracabilite(Model model) {
        List<DemandeStatus> demandeStatus = demandeStatusRepository.findAllWithStatusAndDemande();
        Map<Long, String> statusColors = new HashMap<>();
        for (Parametre parametre : parametreStatusRepository.findAll()) {
            statusColors.put(parametre.getStatusIdArrivee(), parametre.getCouleur());
        }
        model.addAttribute("demandeStatus", demandeStatus);
        model.addAttribute("statusColors", statusColors);
        return "tracabiliteDemandes";
    }

    @PostMapping("/demandeStatus/ajout")
    public String addDemandeStatus(DemandeStatusDTO dto) {
        Optional<Demande> demandeOpt = demandeRepository.findById(dto.getDemandeId());
        if (demandeOpt.isEmpty()) {
            return "redirect:/demandeStatus";
        }

        Optional<Status> statusOpt = statusRepository.findById(dto.getStatusId());
        if (statusOpt.isEmpty()) {
            return "redirect:/demandeStatus";
        }

        LocalDateTime precedent = demandeStatusRepository
                .findTopByDemandeOrderByIdDesc(demandeOpt.get())
                .map(DemandeStatus::getDateChangement)
                .orElse(null);

        DemandeStatus demandeStatus = new DemandeStatus();
        demandeStatus.setDemande(demandeOpt.get());
        demandeStatus.setStatus(statusOpt.get());
        demandeStatus.setObservation(dto.getObservation());

        int dureeTravail = (int) Duration.between(precedent, dto.getDateChangement()).toMinutes();

        demandeStatus.setDureeTravail(dureeTravail);
        demandeStatus.setDateChangement(dto.getDateChangement());
        demandeStatusRepository.save(demandeStatus);
        
        return "redirect:/demandeStatus";
    }



    public List<AlerteRetard> getAlertesRetardDemande(Long demandeId, LocalDateTime maintenant) {
        List<AlerteRetard> alertes = new ArrayList<>();
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        if (demandeOpt.isEmpty()) {
            return alertes;
        }

        Optional<DemandeStatus> dernierStatusOpt =
                demandeStatusRepository.findTopByDemandeOrderByIdDesc(demandeOpt.get());
        if (dernierStatusOpt.isEmpty()) {
            return alertes;
        }

        DemandeStatus dernierStatus = dernierStatusOpt.get();
        long minutesEcoulees = Duration.between(dernierStatus.getDateChangement(), maintenant).toMinutes();
        if (minutesEcoulees < 0) {
            minutesEcoulees = 0;
        }

        List<Parametre> parametres = parametreStatusRepository
                .findByStatusIdDepartOrderByDureeMinimumAsc(dernierStatus.getStatus().getId());

        if (!parametres.isEmpty()) {
            Parametre parametre = parametres.get(0);
            long difference = minutesEcoulees - parametre.getDureeMinimum();
            boolean retard = difference > 0;
            String message = retard ? "Alerte : délai dépassé de " + difference + formatMinutes(difference) : "Délai terminé dans " + Math.abs(difference) + formatMinutes(Math.abs(difference));
            String couleur = retard && parametre.getCouleur() != null && !parametre.getCouleur().isBlank()
                    ? parametre.getCouleur()
                    : "#F3F4F6";

            alertes.add(new AlerteRetard(dernierStatus.getStatus().getLibelle(), message, couleur, retard));
        }

        return alertes;
    }

    @PostMapping("/demandeStatus/modifier-date")
    @Transactional
    public String modifierDateStatus(
            @RequestParam("demandeStatusId") Long demandeStatusId,
            @RequestParam("nouvelleDate") LocalDateTime nouvelleDate) {
        Optional<DemandeStatus> statusAModifierOpt = demandeStatusRepository.findById(demandeStatusId);
        if (statusAModifierOpt.isEmpty()) {
            return "redirect:/demande/list";
        }

        DemandeStatus statusAModifier = statusAModifierOpt.get();
        Demande demande = statusAModifier.getDemande();
        List<DemandeStatus> historiques = demandeStatusRepository.findByDemandeOrderByIdAsc(demande);
        Duration decalage = Duration.between(statusAModifier.getDateChangement(), nouvelleDate);
        boolean appliquerDecalage = false;

        for (DemandeStatus historique : historiques) {
            if (historique.getId().equals(demandeStatusId)) {
                appliquerDecalage = true;
            }
            if (appliquerDecalage) {
                historique.setDateChangement(historique.getDateChangement().plus(decalage));
            }
        }

        recalculerDureesTravail(historiques);
        demandeStatusRepository.saveAll(historiques);
        return "redirect:/demandeStatus/demande?demandeId=" + demande.getId();
    }

    private void recalculerDureesTravail(List<DemandeStatus> historiques) {
        for (int index = 0; index < historiques.size(); index++) {
            int dureeTravail = 0;
            if (index > 0) {
                long minutes = Duration.between(
                        historiques.get(index - 1).getDateChangement(),
                        historiques.get(index).getDateChangement()).toMinutes();
                dureeTravail = (int) Math.min(Integer.MAX_VALUE, Math.max(0, minutes));
            }
            historiques.get(index).setDureeTravail(dureeTravail);
        }
    }

    

    private String formatMinutes(long minutes) {
        return minutes > 1 ? " minutes" : " minute";
    }

    @GetMapping("/demandeStatus/demande")
    public String detailDemandeStatus(@RequestParam("demandeId") Long demandeId, Model model) {
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        if (demandeOpt.isEmpty()) {
            return "redirect:/demande/list";
        }

        Demande demande = demandeOpt.get();
        List<DemandeStatus> historiques = demandeStatusRepository.findByDemandeOrderByIdAsc(demande);
        List<AlerteRetard> alertes = getAlertesRetardDemande(demandeId, LocalDateTime.now());
        AlerteRetard alerteActuelle = alertes.isEmpty() ? null : alertes.get(0);
        Long dernierStatusId = historiques.isEmpty() ? null : historiques.get(historiques.size() - 1).getId();

        model.addAttribute("demande", demande);
        model.addAttribute("demandeStatus", historiques);
        model.addAttribute("alerteActuelle", alerteActuelle);
        model.addAttribute("dernierStatusId", dernierStatusId);
        return "detailDemandeStatus";
    }
}
