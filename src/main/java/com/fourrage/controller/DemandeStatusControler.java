package com.fourrage.controller;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;
import com.fourrage.model.Parametre;
import com.fourrage.model.Status;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DemandeStatusRepository;
import com.fourrage.repository.ParametreStatus;
import com.fourrage.repository.StatusRepository;
import com.fourrage.DTO.DashboardRow;

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

    private Map<Long, Status> loadStatusMap() {
        Map<Long, Status> statusMap = new LinkedHashMap<>();
        for (Status status : statusRepository.findAll()) {
            statusMap.put(status.getId(), status);
        }
        return statusMap;
    }

    @GetMapping("/demandeStatus")
    public String updateDemandeStatus(Long demandeId, Long statusId, Model model) {
        List<DemandeStatus> demandeStatus = demandeStatusRepository.findAll();
        List<Demande> demandes = demandeRepository.findAll();
        List<Status> statuses = statusRepository.findAll();
        model.addAttribute("demandeStatus", demandeStatus);
        model.addAttribute("demandes", demandes);
        model.addAttribute("status", statuses);
        return "demandeStatus";
    }

   
@GetMapping("demandeStatus/api/dashboard")
@ResponseBody
public List<DashboardRow> dashboardApi() {
    LocalDateTime now = LocalDateTime.now();
    List<DashboardRow> rows = new ArrayList<>();
    List<Parametre> parametres = parametreStatusRepository.findAll();
    List<DemandeStatus> historiques = demandeStatusRepository.findAllWithStatusAndDemande();

    for (DemandeStatus historique : historiques) {
        Long statusId = historique.getStatus().getId();
        long dureeEnMinutes = Duration.between(historique.getDateChangement(), now).toMinutes();
        if (dureeEnMinutes < 0) dureeEnMinutes = 0;

        Parametre parametreTrouve = null;
        for (Parametre p : parametres) {
            if (p.getStatusIdDepart().equals(statusId)) {
                parametreTrouve = p;
                break;
            }
        }

        Integer seuil = null;
        String couleur = "#F3F4F6";
        String message = "Pas de paramètre";
        if (parametreTrouve != null) {
            seuil = parametreTrouve.getDureeMinimum();
            boolean depasse = dureeEnMinutes >= seuil;
            message = depasse ? "Seuil dépassé" : "Dans le délai";
            if (depasse) couleur = parametreTrouve.getCouleur();
        }

        rows.add(new DashboardRow(
            historique.getDemande().getId(),
            historique.getStatus().getLibelle(),
            historique.getDateChangement(),
            dureeEnMinutes,
            seuil,
            couleur,
            message
        ));
    }

    return rows;
}

    @GetMapping ("/demandeStatus/form")
    public String showForm(Model model) {
        List<Demande> demandes = demandeRepository.findAll();
        List<Status> statuses = statusRepository.findAll();
        model.addAttribute("demandes", demandes);
        model.addAttribute("status", statuses);
        return "demandeStatus";
    }

    @PostMapping("/demandeStatus/ajout")
    public String addDemandeStatus(
            @RequestParam("demandeId") Long demandeId,
            @RequestParam("statusId") Long statusId,
            @RequestParam("observation") String observation,
            @RequestParam("dateChangement") LocalDateTime dateChangement,
            Model model) {
        LocalDateTime now = demandeStatusRepository.findLatestByDemandeId(demandeId);
        if (now == null) {
            now = LocalDateTime.now();
        }
        LocalDateTime durreDechangement = now.plusMinutes(dateChangement.getMinute()).plusHours(dateChangement.getHour()).plusDays(dateChangement.getDayOfMonth());
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        if (demandeOpt.isEmpty()) {
            return "redirect:/demandeStatus";
        }

        Map<Long, Status> statusMap = loadStatusMap();
        Status status = statusMap.get(statusId);
        if (status == null) {
            return "redirect:/demandeStatus";
        }
        verifierDemandeStatus(demandeId,dateChangement,model);
        DemandeStatus demandeStatus = new DemandeStatus();
        demandeStatus.setDemande(demandeOpt.get());
        demandeStatus.setStatus(status); // utilise la map
        demandeStatus.setObservation(observation);
        demandeStatus.setDureeTravail(durreDechangement.getHour() * 60 + durreDechangement.getMinute());
        demandeStatus.setDateChangement(dateChangement);

        demandeStatusRepository.save(demandeStatus);
        return "redirect:/demandeStatus";
    }

   @PostMapping("/verifierDemandeStatus")
    public String verifierDemandeStatus(
            @RequestParam("demandeId") Long demandeId,
            @RequestParam("maintenant")
            @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime maintenant,
            Model model) {

        Optional<DemandeStatus> opt = demandeStatusRepository.findById(demandeId);
        if (opt.isEmpty()) {
            model.addAttribute("couleurAlerte", "GRIS");
            model.addAttribute("messageAlerte", "Aucun status trouvé.");
            return "devisDemande";
        }

        DemandeStatus dernier = opt.get();
        LocalDateTime dateDernierChangement = dernier.getDateChangement();

        long dureeEnMinutes = Duration.between(dateDernierChangement, maintenant).toMinutes();
        model.addAttribute("dureeEnMinutes", dureeEnMinutes);
        return "devisDemande";
    }



}
