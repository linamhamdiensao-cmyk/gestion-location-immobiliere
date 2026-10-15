package com.location.service;

import com.location.dao.BienDAO;
import com.location.dao.ReservationDAO;
import com.location.dao.memory.InMemoryBienDAO;
import com.location.dao.memory.InMemoryReservationDAO;
import com.location.exception.ReservationException;
import com.location.model.Appartement;
import com.location.model.Bien;
import com.location.model.Reservation;
import com.location.model.enums.StatutReservation;
import com.location.model.enums.Ville;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de {@link ReservationService} : c'est la classe la plus riche
 * en règles de gestion (disponibilité, conflits, cycle de vie).
 */
@DisplayName("ReservationService - règles de gestion")
class ReservationServiceTest {

    private BienDAO bienDAO;
    private ReservationService service;
    private int idBien;

    @BeforeEach
    void initialiser() {
        bienDAO = new InMemoryBienDAO();
        ReservationDAO reservationDAO = new InMemoryReservationDAO();
        service = new ReservationService(reservationDAO, bienDAO);

        // Un bien disponible pour les tests.
        Bien bien = new Appartement(80, 3, 2, 1, Ville.CASABLANCA, "Maârif", 2018,
                true, 5000, 1, 2, true, false, true);
        bienDAO.sauvegarder(bien);
        idBien = bien.getIdBien();
    }

    @Test
    @DisplayName("Une réservation est créée avec le statut EN_ATTENTE")
    void creationEnAttente() {
        Reservation r = service.creer(10, idBien,
                LocalDate.now().plusDays(1), LocalDate.now().plusMonths(3));
        assertEquals(StatutReservation.EN_ATTENTE, r.getStatut());
    }

    @Test
    @DisplayName("Confirmer une réservation rend le bien indisponible")
    void confirmationRendIndisponible() {
        Reservation r = service.creer(10, idBien,
                LocalDate.now().plusDays(1), LocalDate.now().plusMonths(3));
        service.confirmer(r.getIdReservation());

        assertEquals(StatutReservation.CONFIRME, service.trouver(r.getIdReservation()).getStatut());
        assertFalse(bienDAO.trouverParId(idBien).orElseThrow().isDisponible(),
                "Le bien doit être indisponible après confirmation");
    }

    @Test
    @DisplayName("Impossible de réserver un bien déjà indisponible")
    void refusBienIndisponible() {
        Reservation r = service.creer(10, idBien,
                LocalDate.now().plusDays(1), LocalDate.now().plusMonths(3));
        service.confirmer(r.getIdReservation()); // bien -> indisponible

        assertThrows(ReservationException.class, () -> service.creer(11, idBien,
                LocalDate.now().plusDays(2), LocalDate.now().plusMonths(2)));
    }

    @Test
    @DisplayName("Terminer une réservation libère le bien")
    void terminaisonLibereLeBien() {
        Reservation r = service.creer(10, idBien,
                LocalDate.now().plusDays(1), LocalDate.now().plusMonths(3));
        service.confirmer(r.getIdReservation());
        service.terminer(r.getIdReservation());

        assertEquals(StatutReservation.TERMINE, service.trouver(r.getIdReservation()).getStatut());
        assertTrue(bienDAO.trouverParId(idBien).orElseThrow().isDisponible(),
                "Le bien doit redevenir disponible après la fin");
    }

    @Test
    @DisplayName("Annuler une réservation confirmée libère le bien")
    void annulationLibereLeBien() {
        Reservation r = service.creer(10, idBien,
                LocalDate.now().plusDays(1), LocalDate.now().plusMonths(3));
        service.confirmer(r.getIdReservation());
        service.annuler(r.getIdReservation());

        assertEquals(StatutReservation.ANNULE, service.trouver(r.getIdReservation()).getStatut());
        assertTrue(bienDAO.trouverParId(idBien).orElseThrow().isDisponible());
    }

    @Test
    @DisplayName("Des dates incohérentes (fin avant début) sont rejetées")
    void datesIncoherentes() {
        assertThrows(RuntimeException.class, () -> service.creer(10, idBien,
                LocalDate.now().plusMonths(3), LocalDate.now().plusDays(1)));
    }

    @Test
    @DisplayName("Confirmer deux réservations chevauchantes est impossible")
    void conflitDeuxConfirmations() {
        // Première réservation confirmée sur janvier-juin.
        Reservation r1 = service.creer(10, idBien,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 6, 1));
        service.confirmer(r1.getIdReservation());

        // Le bien est désormais indisponible : impossible d'en créer une 2e.
        assertThrows(ReservationException.class, () -> service.creer(11, idBien,
                LocalDate.of(2026, 3, 1), LocalDate.of(2026, 8, 1)));
    }
}
