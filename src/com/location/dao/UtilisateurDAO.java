package com.location.dao;

import com.location.model.Utilisateur;

import java.util.Optional;

/**
 * Contrat de persistance des {@link Utilisateur}.
 */
public interface UtilisateurDAO extends Repository<Utilisateur> {

    /**
     * Recherche un utilisateur par son adresse e-mail (utile pour la connexion
     * et pour garantir l'unicité de l'e-mail).
     *
     * @param email e-mail recherché
     * @return un {@link Optional} contenant l'utilisateur, ou vide si absent
     */
    Optional<Utilisateur> trouverParEmail(String email);
}
