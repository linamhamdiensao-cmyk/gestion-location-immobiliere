package com.location.dao;

import com.location.model.Reservation;

import java.util.List;

/**
 * Contrat de persistance des {@link Reservation}.
 */
public interface ReservationDAO extends Repository<Reservation> {

    /**
     * @param idLocataire identifiant du locataire
     * @return l'historique des réservations de ce locataire
     */
    List<Reservation> trouverParLocataire(int idLocataire);

    /**
     * @param idBien identifiant du bien
     * @return les réservations concernant ce bien
     */
    List<Reservation> trouverParBien(int idBien);
}
