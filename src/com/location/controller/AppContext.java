package com.location.controller;

import com.location.dao.BienDAO;
import com.location.dao.ReservationDAO;
import com.location.dao.UtilisateurDAO;
import com.location.dao.memory.InMemoryBienDAO;
import com.location.dao.memory.InMemoryReservationDAO;
import com.location.dao.memory.InMemoryUtilisateurDAO;
import com.location.service.BienService;
import com.location.service.ReservationService;
import com.location.service.UtilisateurService;

/**
 * <strong>Point de composition</strong> de l'application (Composition Root).
 *
 * <p>Cette classe câble une seule fois toutes les dépendances : DAO → Services →
 * Contrôleurs. Elle constitue le <em>seul endroit</em> où l'implémentation
 * concrète des DAO est choisie.</p>
 *
 * <p><strong>Migration vers SQL :</strong> pour basculer sur une base de
 * données, il suffira de remplacer ici les trois lignes
 * {@code new InMemoryXxxDAO()} par {@code new JdbcXxxDAO(connexion)}. Aucune
 * autre classe (service, contrôleur, vue) n'aura à être modifiée.</p>
 */
public class AppContext {

    // --- Couche d'accès aux données (interchangeable mémoire / SQL) ---
    private final BienDAO bienDAO;
    private final UtilisateurDAO utilisateurDAO;
    private final ReservationDAO reservationDAO;

    // --- Couche service ---
    private final BienService bienService;
    private final UtilisateurService utilisateurService;
    private final ReservationService reservationService;

    // --- Couche contrôleur ---
    private final BienController bienController;
    private final UtilisateurController utilisateurController;
    private final ReservationController reservationController;

    public AppContext() {
        // 1) Choix de l'implémentation de persistance (UNIQUE point à changer pour SQL).
        this.bienDAO = new InMemoryBienDAO();
        this.utilisateurDAO = new InMemoryUtilisateurDAO();
        this.reservationDAO = new InMemoryReservationDAO();

        // 2) Services métier, dépendants uniquement des interfaces DAO.
        this.bienService = new BienService(bienDAO);
        this.utilisateurService = new UtilisateurService(utilisateurDAO);
        this.reservationService = new ReservationService(reservationDAO, bienDAO);

        // 3) Contrôleurs MVC consommés par les vues Swing.
        this.bienController = new BienController(bienService);
        this.utilisateurController = new UtilisateurController(utilisateurService);
        this.reservationController = new ReservationController(reservationService);
    }

    // --- Accès aux DAO (utilisés par le jeu de données de test) ---
    public BienDAO getBienDAO() {
        return bienDAO;
    }

    public UtilisateurDAO getUtilisateurDAO() {
        return utilisateurDAO;
    }

    public ReservationDAO getReservationDAO() {
        return reservationDAO;
    }

    // --- Accès aux contrôleurs (utilisés par les vues) ---
    public BienController getBienController() {
        return bienController;
    }

    public UtilisateurController getUtilisateurController() {
        return utilisateurController;
    }

    public ReservationController getReservationController() {
        return reservationController;
    }
}
