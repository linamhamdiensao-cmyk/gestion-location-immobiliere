package com.location.service;

import com.location.dao.UtilisateurDAO;
import com.location.exception.EntiteIntrouvableException;
import com.location.exception.ValidationException;
import com.location.model.Utilisateur;
import com.location.util.ValidationUtil;

import java.util.List;
import java.util.Optional;

/**
 * Service métier de gestion des utilisateurs : inscription, mise à jour,
 * suppression, connexion et garantie d'unicité de l'e-mail.
 */
public class UtilisateurService {

    private final UtilisateurDAO utilisateurDAO;

    public UtilisateurService(UtilisateurDAO utilisateurDAO) {
        this.utilisateurDAO = utilisateurDAO;
    }

    /**
     * Enregistre un nouvel utilisateur après validation et contrôle d'unicité
     * de l'adresse e-mail.
     *
     * @throws ValidationException si les données sont invalides ou l'e-mail déjà pris
     */
    public Utilisateur ajouter(Utilisateur utilisateur) {
        valider(utilisateur);
        Optional<Utilisateur> existant = utilisateurDAO.trouverParEmail(utilisateur.getEmail());
        if (existant.isPresent()) {
            throw new ValidationException("Un utilisateur utilise déjà l'e-mail « "
                    + utilisateur.getEmail() + " ».");
        }
        return utilisateurDAO.sauvegarder(utilisateur);
    }

    /**
     * Met à jour un utilisateur existant. Vérifie que l'e-mail, s'il change,
     * n'est pas déjà utilisé par un autre compte.
     */
    public Utilisateur modifier(Utilisateur utilisateur) {
        if (!utilisateurDAO.existe(utilisateur.getIdUser())) {
            throw new EntiteIntrouvableException("Utilisateur", utilisateur.getIdUser());
        }
        valider(utilisateur);
        Optional<Utilisateur> homonyme = utilisateurDAO.trouverParEmail(utilisateur.getEmail());
        if (homonyme.isPresent() && homonyme.get().getIdUser() != utilisateur.getIdUser()) {
            throw new ValidationException("Cet e-mail est déjà utilisé par un autre compte.");
        }
        return utilisateurDAO.sauvegarder(utilisateur);
    }

    public void supprimer(int idUser) {
        if (!utilisateurDAO.supprimer(idUser)) {
            throw new EntiteIntrouvableException("Utilisateur", idUser);
        }
    }

    public List<Utilisateur> listerTout() {
        return utilisateurDAO.trouverTout();
    }

    public Utilisateur trouver(int idUser) {
        return utilisateurDAO.trouverParId(idUser)
                .orElseThrow(() -> new EntiteIntrouvableException("Utilisateur", idUser));
    }

    /**
     * Tente d'authentifier un utilisateur par e-mail / mot de passe.
     *
     * @return l'utilisateur connecté si les identifiants sont valides
     * @throws ValidationException si l'authentification échoue
     */
    public Utilisateur seConnecter(String email, String motDePasse) {
        ValidationUtil.exigerNonVide(email, "email");
        ValidationUtil.exigerNonVide(motDePasse, "mot de passe");
        Utilisateur u = utilisateurDAO.trouverParEmail(email)
                .orElseThrow(() -> new ValidationException("E-mail ou mot de passe incorrect."));
        if (!u.seConnecter(email, motDePasse)) {
            throw new ValidationException("E-mail ou mot de passe incorrect.");
        }
        return u;
    }

    /**
     * Valide les informations de base d'un utilisateur.
     */
    private void valider(Utilisateur utilisateur) {
        ValidationUtil.exigerNonNull(utilisateur, "utilisateur");
        ValidationUtil.exigerNonVide(utilisateur.getNom(), "nom");
        ValidationUtil.exigerNonVide(utilisateur.getPrenom(), "prénom");
        ValidationUtil.exigerEmailValide(utilisateur.getEmail());
        ValidationUtil.exigerTelephoneValide(utilisateur.getTelephone());
        ValidationUtil.exigerNonVide(utilisateur.getMotDePasseHache(), "mot de passe");
    }
}
