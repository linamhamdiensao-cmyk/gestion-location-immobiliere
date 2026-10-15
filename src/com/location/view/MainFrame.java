package com.location.view;

import com.location.controller.AppContext;
import com.location.model.Proprietaire;
import com.location.model.Utilisateur;
import com.location.view.components.LogoPanel;
import com.location.view.panels.AccueilPanel;
import com.location.view.panels.GestionLogementsPanel;
import com.location.view.panels.HistoriquePanel;
import com.location.view.panels.LogementsPanel;
import com.location.view.panels.RecherchePanel;
import com.location.view.panels.ReservationPanel;
import com.location.view.panels.UtilisateursPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Fenêtre principale de l'application, <strong>adaptée au rôle</strong> de
 * l'utilisateur connecté.
 *
 * <p>Composition : en-tête (logo, titre, identité de l'utilisateur connecté et
 * bouton de déconnexion) + barre de navigation par onglets. Les onglets et les
 * actions disponibles dépendent du rôle :</p>
 * <ul>
 *   <li><b>Propriétaire</b> (administrateur) : gestion des logements, gestion
 *       des utilisateurs, confirmation / fin des réservations.</li>
 *   <li><b>Locataire</b> (client) : consultation, recherche, réservation et
 *       annulation de ses propres réservations, son historique.</li>
 * </ul>
 */
public class MainFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient AppContext ctx;
    private final transient Utilisateur connecte;
    private final transient Runnable onDeconnexion;
    private final boolean estProprietaire;

    private LogementsPanel logementsPanel;
    private GestionLogementsPanel gestionPanel;
    private RecherchePanel recherchePanel;
    private ReservationPanel reservationPanel;
    private HistoriquePanel historiquePanel;
    private UtilisateursPanel utilisateursPanel;

    /**
     * @param ctx           contexte applicatif
     * @param connecte      utilisateur authentifié
     * @param onDeconnexion action exécutée lors de la déconnexion (réafficher le login)
     */
    public MainFrame(AppContext ctx, Utilisateur connecte, Runnable onDeconnexion) {
        super("Gestion des Locations Immobilières");
        this.ctx = ctx;
        this.connecte = connecte;
        this.onDeconnexion = onDeconnexion;
        this.estProprietaire = connecte instanceof Proprietaire;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(960, 640));
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());
        add(construireEntete(), BorderLayout.NORTH);
        add(construireOnglets(), BorderLayout.CENTER);
    }

    /** En-tête : logo + titre à gauche, identité + déconnexion à droite. */
    private JPanel construireEntete() {
        JPanel entete = new JPanel(new BorderLayout(12, 0));
        entete.setBackground(UITheme.PRIMAIRE);
        entete.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));

        JPanel gauche = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        gauche.setOpaque(false);
        gauche.add(new LogoPanel(48));
        JLabel titre = new JLabel("Location Immobilière");
        titre.setFont(UITheme.TITRE);
        titre.setForeground(UITheme.TEXTE_CLAIR);
        gauche.add(titre);
        entete.add(gauche, BorderLayout.WEST);

        JPanel droite = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        droite.setOpaque(false);
        JLabel identite = new JLabel(connecte.getNomComplet()
                + "  (" + connecte.getTypeUtilisateur().getLibelle() + ")");
        identite.setFont(UITheme.GRAS);
        identite.setForeground(UITheme.TEXTE_CLAIR);
        droite.add(identite);

        JButton btnDeconnexion = new JButton("Déconnexion");
        btnDeconnexion.setFocusPainted(false);
        btnDeconnexion.addActionListener(e -> {
            dispose();
            onDeconnexion.run();
        });
        droite.add(btnDeconnexion);
        entete.add(droite, BorderLayout.EAST);

        return entete;
    }

    /** Barre de navigation : onglets filtrés selon le rôle. */
    private JTabbedPane construireOnglets() {
        JTabbedPane onglets = new JTabbedPane();
        onglets.setFont(UITheme.GRAS);

        logementsPanel = new LogementsPanel(ctx);
        recherchePanel = new RecherchePanel(ctx);
        reservationPanel = new ReservationPanel(ctx, connecte);
        historiquePanel = new HistoriquePanel(ctx, connecte);

        onglets.addTab("Accueil", new AccueilPanel());
        onglets.addTab("Logements", logementsPanel);

        // Onglets réservés au propriétaire (administration).
        if (estProprietaire) {
            gestionPanel = new GestionLogementsPanel(ctx);
            utilisateursPanel = new UtilisateursPanel(ctx);
            onglets.addTab("Gestion des logements", gestionPanel);
        }

        // Recherche : surtout utile au locataire, mais accessible à tous.
        onglets.addTab("Recherche", recherchePanel);
        onglets.addTab("Réservation", reservationPanel);
        onglets.addTab("Historique", historiquePanel);

        if (estProprietaire) {
            onglets.addTab("Utilisateurs", utilisateursPanel);
        }

        // Rafraîchit le panneau affiché à chaque changement d'onglet.
        onglets.addChangeListener(e -> rafraichir(onglets.getSelectedComponent()));
        return onglets;
    }

    private void rafraichir(Component selection) {
        if (selection == logementsPanel) {
            logementsPanel.rafraichir();
        } else if (selection == gestionPanel) {
            gestionPanel.rafraichir();
        } else if (selection == reservationPanel) {
            reservationPanel.rafraichir();
        } else if (selection == historiquePanel) {
            historiquePanel.rafraichir();
        } else if (selection == utilisateursPanel) {
            utilisateursPanel.rafraichir();
        }
    }
}
