package com.location.dao;

import com.location.model.Bien;

import java.util.List;

/**
 * Contrat de persistance des {@link Bien}.
 *
 * <p>Étend le {@link Repository} générique en ajoutant les requêtes spécifiques
 * au domaine des biens. Une future implémentation JDBC traduira simplement ces
 * méthodes en requêtes SQL.</p>
 */
public interface BienDAO extends Repository<Bien> {

    /**
     * @param idProprietaire identifiant du propriétaire
     * @return les biens appartenant à ce propriétaire
     */
    List<Bien> trouverParProprietaire(int idProprietaire);

    /**
     * @return uniquement les biens actuellement disponibles à la location
     */
    List<Bien> trouverDisponibles();
}
