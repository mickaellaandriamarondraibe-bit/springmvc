package com.fourrage.controller;

import java.lang.StackWalker.Option;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.checkerframework.checker.units.qual.m;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;
import com.fourrage.model.Status;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DemandeStatusRepository;
import com.fourrage.repository.ParametreStatus;
import com.fourrage.repository.StatusRepository;

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

    @GetMapping("/demandeStatus/tracabilite")
    public String tracabiliteDemandes(Model model) {
        List<DemandeStatus> traces = demandeStatusRepository.findAllWithStatusAndDemande();
        Map<Long, String> statusColors = new HashMap<>();
        parametreStatusRepository.findAll()
                .forEach(p -> statusColors.putIfAbsent(p.getStatusIdDepart(), normalizeColor(p.getCouleur())));
        model.addAttribute("demandeStatus", traces);
        model.addAttribute("statusColors", statusColors);
        return "tracabiliteDemandes";
    }

    private String normalizeColor(String color) {
        if (color == null || color.isBlank()) {
            return "#F3F4F6";
        }
        String c = color.trim();
        String u = c.toUpperCase();
        if (u.equals("ROUGE") || u.equals("RED")) return "#E53935";
        if (u.equals("VERT") || u.equals("GREEN")) return "#43A047";
        if (u.equals("ORANGE")) return "#FB8C00";
        if (u.equals("JAUNE") || u.equals("YELLOW")) return "#FDD835";
        if (u.equals("BLEU") || u.equals("BLUE")) return "#1E88E5";
        if (u.equals("GRIS") || u.equals("GRAY") || u.equals("GREY")) return "#B0BEC5";
        if (c.matches("^[0-9A-Fa-f]{3}$") || c.matches("^[0-9A-Fa-f]{6}$")) return "#" + c;
        if (c.matches("^#[0-9A-Fa-f]{3}$") || c.matches("^#[0-9A-Fa-f]{6}$")) return c;
        return c;
    }

    @GetMapping("/demandeStatus/dashboard")
    public String dashboard(
            @RequestParam(value = "maintenant", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime maintenant,
            Model model) {
        LocalDateTime reference = (maintenant != null) ? maintenant : LocalDateTime.now();

        List<DashboardRow> rows = new ArrayList<>();
        for (Demande demande : demandeRepository.findAll()) {
            Optional<DemandeStatus> latestOpt = demandeStatusRepository.findTopByDemandeOrderByDateChangementDescIdDesc(demande);
            if (latestOpt.isEmpty()) {
                rows.add(new DashboardRow(demande.getId(), "-", null, 0L, null, "#F3F4F6", "Aucun statut"));
                continue;
            }

            DemandeStatus latest = latestOpt.get();
            long dureeEnHeures = Duration.between(latest.getDateChangement(), reference).toHours();
            if (dureeEnHeures < 0) {
                dureeEnHeures = 0;
            }

            int dureeInt = (int) Math.min(Integer.MAX_VALUE, dureeEnHeures);
            var paramOpt = parametreStatusRepository
                    .findTopByStatusIdDepartAndDureeMinimumLessThanEqualOrderByDureeMinimumDesc(
                            latest.getStatus().getId(), dureeInt)
                    .or(() -> parametreStatusRepository.findTopByStatusIdDepartOrderByDureeMinimumAsc(latest.getStatus().getId()));

            String couleur = paramOpt.map(p -> p.getCouleur()).orElse("#F3F4F6");
            Integer seuil = paramOpt.map(p -> p.getDureeMinimum()).orElse(null);
            String message = (seuil == null)
                    ? "Pas de paramètre"
                    : (dureeEnHeures >= seuil ? "Seuil atteint" : "Dans le délai");

            rows.add(new DashboardRow(
                    demande.getId(),
                    latest.getStatus().getLibelle(),
                    latest.getDateChangement(),
                    dureeEnHeures,
                    seuil,
                    couleur,
                    message));
        }

        model.addAttribute("rows", rows);
        model.addAttribute("maintenant", reference);
        return updateDemandeStatus(null, null, model);
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

    long dureeEnHeures = Duration.between(dateDernierChangement, maintenant).toHours();

    // ici: récupérer la couleur depuis la table parametre selon ta logique
    // String couleurAlerte = parametre.getCouleur();

    model.addAttribute("dureeEnHeures", dureeEnHeures);
    return "devisDemande";
}

class DashboardRow {
    private final Long demandeId;
    private final String statutActuel;
    private final LocalDateTime dateDernierChangement;
    private final Long dureeHeures;
    private final Integer seuilHeures;
    private final String couleur;
    private final String message;

    DashboardRow(Long demandeId, String statutActuel, LocalDateTime dateDernierChangement, Long dureeHeures,
            Integer seuilHeures, String couleur, String message) {
        this.demandeId = demandeId;
        this.statutActuel = statutActuel;
        this.dateDernierChangement = dateDernierChangement;
        this.dureeHeures = dureeHeures;
        this.seuilHeures = seuilHeures;
        this.couleur = couleur;
        this.message = message;
    }

    public Long getDemandeId() { return demandeId; }
    public String getStatutActuel() { return statutActuel; }
    public LocalDateTime getDateDernierChangement() { return dateDernierChangement; }
    public Long getDureeHeures() { return dureeHeures; }
    public Integer getSeuilHeures() { return seuilHeures; }
    public String getCouleur() { return couleur; }
    public String getMessage() { return message; }
}

}
