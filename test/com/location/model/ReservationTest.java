package com.location.model;

import com.location.model.enums.StatutReservation;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de la classe {@link Reservation}.
 *
 * <p>Vérifient le calcul automatique du nombre de mois / prix total et les
 * transitions d'état (confirmer, annuler, terminer).</p>
 */
@DisplayName("Réservation - calculs et cycle de vie")
class ReservationTest {

    @Test
    @DisplayName("Une réservation de 6 mois coûte 6 fois le loyer")
    void calculPrixTotalSurSixMois() {
        // ARRANGE
        LocalDate debut = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 7, 1); // ~6 mois (180 jours)
        double loyerMensuel = 5000;

        // ACT
        Reservation r = new Reservation(1, 2, debut, fin, loyerMensuel);

        // ASSERT
        assertEquals(6, r.getNbMois(), "6 mois attendus");
        assertEquals(30000, r.getPrixTotal(), 0.001, "6 × 5000 attendu");
    }

    @Test
    @DisplayName("Le nombre de mois est arrondi au mois supérieur")
    void nombreDeMoisArrondiVersLeHaut() {
        // 40 jours => 2 mois (toute période entamée est due)
        Reservation r = new Reservation(1, 2,
                LocalDate.of(2026, 1, 1), LocalDate.of(2026, 2, 10), 1000);
        assertEquals(2, r.getNbMois());
    }

    @Test
    @DisplayName("Une réservation est créée par défaut EN_ATTENTE")
    void statutInitialEnAttente() {
        Reservation r = new Reservation(1, 2,
                LocalDate.now(), LocalDate.now().plusMonths(3), 2000);
        assertEquals(StatutReservation.EN_ATTENTE, r.getStatut());
    }

    @Test
    @DisplayName("confirmer() puis terminer() font évoluer le statut")
    void transitionsDeStatut() {
        Reservation r = new Reservation(1, 2,
                LocalDate.now(), LocalDate.now().plusMonths(3), 2000);

        r.confirmer();
        assertEquals(StatutReservation.CONFIRME, r.getStatut());
        assertTrue(r.estModifiable(), "Une réservation confirmée reste modifiable");

        r.terminer();
        assertEquals(StatutReservation.TERMINE, r.getStatut());
        assertFalse(r.estModifiable(), "Une réservation terminée n'est plus modifiable");
    }

    @Test
    @DisplayName("Une réservation annulée n'est plus modifiable")
    void reservationAnnuleeNonModifiable() {
        Reservation r = new Reservation(1, 2,
                LocalDate.now(), LocalDate.now().plusMonths(1), 2000);
        r.annuler();
        assertEquals(StatutReservation.ANNULE, r.getStatut());
        assertFalse(r.estModifiable());
    }
}
