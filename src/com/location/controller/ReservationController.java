package com.location.controller;

import com.location.model.Reservation;
import com.location.service.ReservationService;

import java.time.LocalDate;
import java.util.List;

/**
 * Contrôleur des réservations (MVC). Expose à l'interface les opérations du
 * cycle de vie d'une réservation.
 */
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    public Reservation creer(int idLocataire, int idBien, LocalDate dateDebut, LocalDate dateFin) {
        return reservationService.creer(idLocataire, idBien, dateDebut, dateFin);
    }

    public Reservation confirmer(int idReservation) {
        return reservationService.confirmer(idReservation);
    }

    public Reservation annuler(int idReservation) {
        return reservationService.annuler(idReservation);
    }

    public Reservation terminer(int idReservation) {
        return reservationService.terminer(idReservation);
    }

    public List<Reservation> listerTout() {
        return reservationService.listerTout();
    }

    public List<Reservation> historiqueLocataire(int idLocataire) {
        return reservationService.historiqueLocataire(idLocataire);
    }
}
