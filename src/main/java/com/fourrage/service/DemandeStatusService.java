package com.fourrage.service;

import java.time.Duration;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fourrage.DTO.AlerteRetard;
import com.fourrage.DTO.DemandeStatusDTO;
import com.fourrage.DTO.ResultatStatutDTO;
import com.fourrage.model.Demande;
import com.fourrage.model.DemandeStatus;
import com.fourrage.model.Parametre;
import com.fourrage.model.Status;
import com.fourrage.repository.DemandeRepository;
import com.fourrage.repository.DemandeStatusRepository;
import com.fourrage.repository.ParametreStatus;
import com.fourrage.repository.StatusRepository;

@Service
public class DemandeStatusService {

    private final DemandeStatusRepository demandeStatusRepository;
    private final DemandeRepository demandeRepository;
    private final StatusRepository statusRepository;
    private final ParametreStatus parametreStatusRepository;
    private static final LocalTime HEURE_DEBUT_TRAVAIL = LocalTime.of(8, 0);
    private static final LocalTime HEURE_FIN_TRAVAIL = LocalTime.of(16, 0);

    public DemandeStatusService(
            DemandeStatusRepository demandeStatusRepository,
            DemandeRepository demandeRepository,
            StatusRepository statusRepository,
            ParametreStatus parametreStatusRepository) {
        this.demandeStatusRepository = demandeStatusRepository;
        this.demandeRepository = demandeRepository;
        this.statusRepository = statusRepository;
        this.parametreStatusRepository = parametreStatusRepository;
    }

    public List<Demande> findAllDemandes() {
        return demandeRepository.findAll();
    }

    public List<Status> findAllStatus() {
        return statusRepository.findAll();
    }

    public List<DemandeStatus> findAllTracabilite() {
        return demandeStatusRepository.findAllWithStatusAndDemande();
    }

    public Optional<Demande> findDemande(Long demandeId) {
        return demandeRepository.findById(demandeId);
    }

    public List<DemandeStatus> findHistorique(Demande demande) {
        return demandeStatusRepository.findByDemandeOrderByIdAsc(demande);
    }


    public Map<Long, String> getStatusColors() {
        Map<Long, String> statusColors = new HashMap<>();
        for (Parametre parametre : parametreStatusRepository.findAll()) {
            statusColors.put(parametre.getStatusIdArrivee(), parametre.getCouleur());
        }
        return statusColors;
    }

    public boolean verifierJourDeTravail(LocalDateTime date) {
        return estJourOuvre(date.toLocalDate())
                && !date.toLocalTime().isBefore(HEURE_DEBUT_TRAVAIL)
                && date.toLocalTime().isBefore(HEURE_FIN_TRAVAIL);
    }

    public LocalDateTime verifierSiHeureDeTravail(LocalDateTime date) {
        if (verifierJourDeTravail(date)) {
            return date;
        }

        LocalDate prochaineDate = date.toLocalDate();
        if (estJourOuvre(prochaineDate) && date.toLocalTime().isBefore(HEURE_DEBUT_TRAVAIL)) {
            return LocalDateTime.of(prochaineDate, HEURE_DEBUT_TRAVAIL);
        }

        do {
            prochaineDate = prochaineDate.plusDays(1);
        } while (!estJourOuvre(prochaineDate));

        return LocalDateTime.of(prochaineDate, HEURE_DEBUT_TRAVAIL);
    }

    @Transactional
    public String ajouter(DemandeStatusDTO dto) {
        Optional<Demande> demandeOpt = demandeRepository.findById(dto.getDemandeId());
        Optional<Status> statusOpt = statusRepository.findById(dto.getStatusId());
        if (demandeOpt.isEmpty() || statusOpt.isEmpty()) {
            return "Demande ou statut introuvable.";
        }

        Demande demande = demandeOpt.get();
        Status status = statusOpt.get();
        Optional<DemandeStatus> precedentOpt = demandeStatusRepository.findTopByDemandeOrderByIdDesc(demande);
        LocalDateTime precedent = null;

        if (precedentOpt.isPresent()) {
            DemandeStatus precedentStatus = precedentOpt.get();
            precedent = precedentStatus.getDateChangement();

            if (precedentStatus.getStatus().getId().equals(status.getId())) {
                return "Le nouveau statut doit être différent du statut actuel.";
            }
            if (!dto.getDateChangement().isAfter(precedent)) {
                return "La date du nouveau statut doit être postérieure à la date du statut actuel.";
            }
            if (parametreStatusRepository
                    .findFirstByStatusIdDepartAndStatusIdArriveeOrderByDureeMinimumAsc(
                            precedentStatus.getStatus().getId(),
                            status.getId())
                    .isEmpty()) {
                return "La transition " + precedentStatus.getStatus().getLibelle()
                        + " → " + status.getLibelle() + " n'est pas configurée.";
            }
        }

        DemandeStatus demandeStatus = new DemandeStatus();
        demandeStatus.setDemande(demande);
        demandeStatus.setStatus(status);
        demandeStatus.setObservation(dto.getObservation());
        demandeStatus.setDureeTravail(calculerDureeTravail(precedent, dto.getDateChangement()));
        demandeStatus.setDateChangement(dto.getDateChangement());
        demandeStatusRepository.save(demandeStatus);
        return null;
    }

    @Transactional
    public void creerStatusInitial(Demande demande, Long statusId) {
        statusRepository.findById(statusId).ifPresent(status -> {
            DemandeStatus demandeStatus = new DemandeStatus();
            demandeStatus.setDemande(demande);
            demandeStatus.setStatus(status);
            demandeStatus.setDateChangement(LocalDateTime.now());
            demandeStatus.setDureeTravail(0);
            demandeStatusRepository.save(demandeStatus);
        });
    }

    @Transactional
    public void creerStatus(Demande demande, Long statusId) {
        statusRepository.findById(statusId).ifPresent(status -> {
            LocalDateTime dateChangement = LocalDateTime.now();
            Optional<DemandeStatus> precedentOpt = demandeStatusRepository.findTopByDemandeOrderByIdDesc(demande);
            LocalDateTime precedent = null;

            if (precedentOpt.isPresent()) {
                DemandeStatus precedentStatus = precedentOpt.get();
                if (precedentStatus.getStatus().getId().equals(status.getId())
                        || !dateChangement.isAfter(precedentStatus.getDateChangement())
                        || parametreStatusRepository
                                .findFirstByStatusIdDepartAndStatusIdArriveeOrderByDureeMinimumAsc(
                                        precedentStatus.getStatus().getId(),
                                        status.getId())
                                .isEmpty()) {
                    return;
                }
                precedent = precedentStatus.getDateChangement();
            }

            DemandeStatus demandeStatus = new DemandeStatus();
            demandeStatus.setDemande(demande);
            demandeStatus.setStatus(status);
            demandeStatus.setDateChangement(dateChangement);
            demandeStatus.setDureeTravail(calculerDureeTravail(precedent, dateChangement));
            demandeStatusRepository.save(demandeStatus);
        });
    }

    public List<AlerteRetard> getAlertesRetardDemande(Long demandeId, LocalDateTime maintenant) {
        List<AlerteRetard> alertes = new ArrayList<>();
        Optional<Demande> demandeOpt = demandeRepository.findById(demandeId);
        if (demandeOpt.isEmpty()) {
            return alertes;
        }

        Optional<DemandeStatus> dernierStatusOpt = demandeStatusRepository.findTopByDemandeOrderByIdDesc(demandeOpt.get());
        if (dernierStatusOpt.isEmpty()) {
            return alertes;
        }

        DemandeStatus dernierStatus = dernierStatusOpt.get();
        long minutesEcoulees = calculerMinutesTravail(dernierStatus.getDateChangement(), maintenant);

        List<Parametre> parametres = parametreStatusRepository
                .findByStatusIdDepartOrderByDureeMinimumAsc(dernierStatus.getStatus().getId());

        if (!parametres.isEmpty()) {
            Parametre parametre = parametres.get(0);
            long difference = minutesEcoulees - parametre.getDureeMinimum();
            boolean retard = difference > 0;

            alertes.add(new AlerteRetard(
                    dernierStatus.getStatus().getLibelle(),
                    minutesEcoulees,
                    parametre.getDureeMinimum(),
                    difference,
                    parametre.getCouleur(),
                    retard));
        }

        return alertes;
    }

    public List<ResultatStatutDTO> construireResultatsHistorique(List<DemandeStatus> historiques) {
        List<ResultatStatutDTO> resultats = new ArrayList<>();

        for (int index = 0; index < historiques.size(); index++) {
            DemandeStatus trace = historiques.get(index);
            boolean enCours = index == historiques.size() - 1;
            Long dureeTravail = null;
            Long limite = null;
            long difference = 0;
            String couleur = null;
            boolean retard = false;

            if (!enCours) {
                DemandeStatus suivant = historiques.get(index + 1);
                dureeTravail = suivant.getDureeTravail() == null
                        ? 0L
                        : suivant.getDureeTravail().longValue();
                Optional<Parametre> parametreOpt = parametreStatusRepository
                        .findFirstByStatusIdDepartAndStatusIdArriveeOrderByDureeMinimumAsc(
                                trace.getStatus().getId(),
                                suivant.getStatus().getId());

                if (parametreOpt.isPresent()) {
                    Parametre parametre = parametreOpt.get();
                    limite = parametre.getDureeMinimum().longValue();
                    difference = dureeTravail - limite;
                    retard = difference > 0;
                    couleur = parametre.getCouleur();
                }
            }

            resultats.add(new ResultatStatutDTO(
                    trace,
                    dureeTravail,
                    limite,
                    difference,
                    couleur,
                    retard,
                    enCours));
        }

        return resultats;
    }

    @Transactional
    public Optional<Long> modifierDateStatus(Long demandeStatusId, LocalDateTime nouvelleDate) {
        if (nouvelleDate == null) {
            return Optional.empty();
        }

        Optional<DemandeStatus> statusAModifierOpt = demandeStatusRepository.findById(demandeStatusId);
        if (statusAModifierOpt.isEmpty()) {
            return Optional.empty();
        }

        DemandeStatus statusAModifier = statusAModifierOpt.get();
        Demande demande = statusAModifier.getDemande();
        List<DemandeStatus> historiques = demandeStatusRepository.findByDemandeOrderByIdAsc(demande);
        int index = historiques.indexOf(statusAModifier);

        if (index > 0 && !nouvelleDate.isAfter(historiques.get(index - 1).getDateChangement())) {
            return Optional.empty();
        }
        if (index < historiques.size() - 1
                && !nouvelleDate.isBefore(historiques.get(index + 1).getDateChangement())) {
            return Optional.empty();
        }

        statusAModifier.setDateChangement(nouvelleDate);

        recalculerDureesTravail(historiques);
        demandeStatusRepository.saveAll(historiques);
        return Optional.of(demande.getId());
    }

    
    private void recalculerDureesTravail(List<DemandeStatus> historiques) {
        for (int index = 0; index < historiques.size(); index++) {
            LocalDateTime precedent = index > 0 ? historiques.get(index - 1).getDateChangement() : null;
            historiques.get(index).setDureeTravail(
                    calculerDureeTravail(precedent, historiques.get(index).getDateChangement()));
        }
    }

    private int calculerDureeTravail(LocalDateTime precedent, LocalDateTime dateChangement) {
        if (precedent == null || dateChangement == null || dateChangement.isBefore(precedent)) {
            return 0;
        }

        long minutes = calculerMinutesTravail(precedent, dateChangement);
        return (int) Math.min(Integer.MAX_VALUE, minutes);
    }

    private long calculerMinutesTravail(LocalDateTime debut, LocalDateTime fin) {
        if (debut == null || fin == null || fin.isBefore(debut)) {
            return 0;
        }

        long minutes = 0;
        LocalDate date = debut.toLocalDate();
        LocalDate dateFin = fin.toLocalDate();

        while (!date.isAfter(dateFin)) {
            if (estJourOuvre(date)) {
                LocalDateTime debutJournee = LocalDateTime.of(date, HEURE_DEBUT_TRAVAIL);
                LocalDateTime finJournee = LocalDateTime.of(date, HEURE_FIN_TRAVAIL);
                LocalDateTime debutEffectif = debut.isAfter(debutJournee) ? debut : debutJournee;
                LocalDateTime finEffective = fin.isBefore(finJournee) ? fin : finJournee;

                if (finEffective.isAfter(debutEffectif)) {
                    minutes += Duration.between(debutEffectif, finEffective).toMinutes();
                }
            }
            date = date.plusDays(1);
        }

        return minutes;
    }

    private boolean estJourOuvre(LocalDate date) {
        DayOfWeek jour = date.getDayOfWeek();
        return jour != DayOfWeek.SATURDAY && jour != DayOfWeek.SUNDAY;
    }

}
