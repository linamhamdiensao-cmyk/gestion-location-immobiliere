package com.location.exception;

/**
 * Levée lorsqu'une opération de réservation viole une règle de gestion :
 * <ul>
 *   <li>chevauchement avec une réservation déjà confirmée sur la même période ;</li>
 *   <li>tentative de modification d'une réservation annulée ou terminée ;</li>
 *   <li>dates incohérentes (date de fin antérieure à la date de début) ;</li>
 *   <li>réservation d'un bien indisponible.</li>
 * </ul>
 */
public class ReservationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ReservationException(String message) {
        super(message);
    }
}
