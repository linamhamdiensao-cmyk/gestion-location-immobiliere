package com.location.model.enums;

/**
 * Cycle de vie d'une réservation.
 *
 * <p>Les transitions autorisées (voir {@code ReservationService}) sont :</p>
 * <pre>
 *   EN_ATTENTE --confirmer()--&gt; CONFIRME --terminer()--&gt; TERMINE
 *        |                          |
 *        +--------annuler()---------+--&gt; ANNULE
 * </pre>
 *
 * <p>Règle de gestion : une réservation est créée par défaut en
 * {@link #EN_ATTENTE}. Elle ne peut passer en {@link #CONFIRME} qu'après
 * validation explicite du propriétaire. Une réservation {@link #ANNULE} est
 * définitive et ne peut plus être modifiée.</p>
 */
public enum StatutReservation {

    EN_ATTENTE("En attente"),
    CONFIRME("Confirmé"),
    ANNULE("Annulé"),
    TERMINE("Terminé");

    private final String libelle;

    StatutReservation(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
