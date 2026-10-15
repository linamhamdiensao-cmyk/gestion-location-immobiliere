package com.location.service;

import com.location.dao.BienDAO;
import com.location.dao.ReservationDAO;
import com.location.exception.EntiteIntrouvableException;
import com.location.exception.ReservationException;
import com.location.model.Bien;
import com.location.model.Reservation;
import com.location.model.enums.StatutReservation;
import com.location.util.ValidationUtil;

import java.time.LocalDate;
import java.util.List;
import java.util.Comparator;

/**
 * Service métier des réservations. Concentre les règles de gestion liées au
 * cycle de location.
 *
 * <p>Règles implémentées :</p>
 * <ol>
 *   <li>Une réservation est créée par défaut en {@link StatutReservation#EN_ATTENTE}.</li>
 *   <li>Un bien ne peut avoir qu'une seule réservation confirmée chevauchant une période.</li>
 *   <li>Le passage en {@link StatutReservation#CONFIRME} rend le bien indisponible.</li>
 *   <li>La fin de réservation ({@link StatutReservation#TERMINE}) rend le bien à nouveau disponible.</li>
 *   <li>Une réservation annulée ou terminée n'est plus modifiable.</li>
 * </ol>
 */
public class ReservationService {

    private final ReservationDAO reservationDAO;
    private final BienDAO bienDAO;

    public ReservationService(ReservationDAO reservationDAO, BienDAO bienDAO) {
        this.reservationDAO = reservationDAO;
        this.bienDAO = bienDAO;
    }

    /**
     * Crée une réservation pour un locataire sur un bien et une période.
     *
     * @param idLocataire identifiant du locataire
     * @param idBien      identifiant du bien
     * @param dateDebut   début de location
     * @param dateFin     fin de location
     * @return la réservation créée (statut EN_ATTENTE)
     * @throws ReservationException si le bien est indisponible, les dates
     *                              invalides ou la période en conflit
     */
    public Reservation creer(int idLocataire, int idBien, LocalDate dateDebut, LocalDate dateFin) {
        ValidationUtil.exigerIntervalleValide(dateDebut, dateFin);

        Bien bien = bienDAO.trouverParId(idBien)
                .orElseThrow(() -> new EntiteIntrouvableException("Bien", idBien));

        if (!bien.isDisponible()) {
            throw new ReservationException("Le bien #" + idBien + " n'est pas disponible.");
        }
        // Règle : pas de chevauchement avec une réservation déjà confirmée.
        if (existeChevauchementConfirme(idBien, dateDebut, dateFin, 0)) {
            throw new ReservationException(
                    "Le bien est déjà réservé (confirmé) sur une période qui chevauche celle demandée.");
        }

        Reservation reservation = new Reservation(idLocataire, idBien, dateDebut, dateFin,
                bien.getPrixParMois());
        return reservationDAO.sauvegarder(reservation);
    }

    /**
     * Confirme une réservation (validation explicite du propriétaire) et rend
     * le bien indisponible.
     *
     * @throws ReservationException si la réservation n'est plus en attente ou
     *                              entre en conflit avec une autre confirmation
     */
    public Reservation confirmer(int idReservation) {
        Reservation r = trouver(idReservation);
        if (r.getStatut() != StatutReservation.EN_ATTENTE) {
            throw new ReservationException("Seule une réservation « en attente » peut être confirmée.");
        }
        if (existeChevauchementConfirme(r.getIdBien(), r.getDateDebut(), r.getDateFin(), idReservation)) {
            throw new ReservationException("Conflit : une autre réservation confirmée couvre déjà cette période.");
        }
        r.confirmer();
        reservationDAO.sauvegarder(r);

        // Le bien devient indisponible.
        bienDAO.trouverParId(r.getIdBien()).ifPresent(bien -> {
            bien.setDisponible(false);
            bienDAO.sauvegarder(bien);
        });
        return r;
    }

    /**
     * Annule une réservation. Si elle était confirmée, le bien redevient
     * disponible (sauf s'il reste une autre réservation confirmée active).
     *
     * @throws ReservationException si la réservation est déjà annulée ou terminée
     */
    public Reservation annuler(int idReservation) {
        Reservation r = trouver(idReservation);
        if (!r.estModifiable()) {
            throw new ReservationException("Une réservation annulée ou terminée ne peut plus être modifiée.");
        }
        boolean etaitConfirmee = r.getStatut() == StatutReservation.CONFIRME;
        r.annuler();
        reservationDAO.sauvegarder(r);

        if (etaitConfirmee) {
            libererBienSiPossible(r.getIdBien());
        }
        return r;
    }

    /**
     * Termine une réservation confirmée (période écoulée) et libère le bien.
     */
    public Reservation terminer(int idReservation) {
        Reservation r = trouver(idReservation);
        if (r.getStatut() != StatutReservation.CONFIRME) {
            throw new ReservationException("Seule une réservation confirmée peut être terminée.");
        }
        r.terminer();
        reservationDAO.sauvegarder(r);
        libererBienSiPossible(r.getIdBien());
        return r;
    }

    /**
     * Historique des réservations d'un locataire, trié de la plus récente à la
     * plus ancienne.
     */
    public List<Reservation> historiqueLocataire(int idLocataire) {
        List<Reservation> liste = reservationDAO.trouverParLocataire(idLocataire);
        liste.sort(Comparator.comparing(Reservation::getDateReservation).reversed());
        return liste;
    }

    public List<Reservation> listerTout() {
        return reservationDAO.trouverTout();
    }

    public Reservation trouver(int idReservation) {
        return reservationDAO.trouverParId(idReservation)
                .orElseThrow(() -> new EntiteIntrouvableException("Réservation", idReservation));
    }

    // ----------------------------------------------------------------------
    // Méthodes internes
    // ----------------------------------------------------------------------

    /**
     * Détecte un chevauchement de période avec une réservation déjà confirmée
     * sur le même bien.
     *
     * @param idBien            bien concerné
     * @param debut             début de la période demandée
     * @param fin               fin de la période demandée
     * @param idReservationAExclure réservation à ignorer (celle qu'on confirme), ou 0
     * @return {@code true} en cas de conflit
     */
    private boolean existeChevauchementConfirme(int idBien, LocalDate debut, LocalDate fin,
                                                int idReservationAExclure) {
        return reservationDAO.trouverParBien(idBien).stream()
                .filter(r -> r.getStatut() == StatutReservation.CONFIRME)
                .filter(r -> r.getIdReservation() != idReservationAExclure)
                // Deux périodes [a,b] et [c,d] se chevauchent si a < d ET c < b.
                .anyMatch(r -> debut.isBefore(r.getDateFin()) && r.getDateDebut().isBefore(fin));
    }

    /**
     * Rend un bien disponible s'il ne reste plus aucune réservation confirmée
     * active le concernant.
     */
    private void libererBienSiPossible(int idBien) {
        boolean encoreOccupe = reservationDAO.trouverParBien(idBien).stream()
                .anyMatch(r -> r.getStatut() == StatutReservation.CONFIRME);
        if (!encoreOccupe) {
            bienDAO.trouverParId(idBien).ifPresent(bien -> {
                bien.setDisponible(true);
                bienDAO.sauvegarder(bien);
            });
        }
    }
}
