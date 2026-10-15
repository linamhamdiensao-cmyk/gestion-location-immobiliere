package com.location.controller;

import com.location.model.Utilisateur;
import com.location.service.UtilisateurService;

import java.util.List;

/**
 * Contrôleur des utilisateurs (MVC). Relaie les actions de l'interface de
 * gestion des utilisateurs vers le {@link UtilisateurService}.
 */
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    public UtilisateurController(UtilisateurService utilisateurService) {
        this.utilisateurService = utilisateurService;
    }

    public Utilisateur ajouter(Utilisateur utilisateur) {
        return utilisateurService.ajouter(utilisateur);
    }

    public Utilisateur modifier(Utilisateur utilisateur) {
        return utilisateurService.modifier(utilisateur);
    }

    public void supprimer(int idUser) {
        utilisateurService.supprimer(idUser);
    }

    public List<Utilisateur> listerTout() {
        return utilisateurService.listerTout();
    }

    public Utilisateur trouver(int idUser) {
        return utilisateurService.trouver(idUser);
    }

    public Utilisateur seConnecter(String email, String motDePasse) {
        return utilisateurService.seConnecter(email, motDePasse);
    }
}
