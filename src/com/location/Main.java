package com.location;

import com.location.controller.AppContext;
import com.location.data.DonneesTest;
import com.location.view.LoginFrame;
import com.location.view.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Point d'entrée de l'application de gestion des locations immobilières.
 *
 * <p>Séquence de démarrage :</p>
 * <ol>
 *   <li>création du {@link AppContext} (câblage DAO → services → contrôleurs) ;</li>
 *   <li>injection du jeu de données de démonstration ({@link DonneesTest}) ;</li>
 *   <li>affichage de l'écran de connexion ({@link LoginFrame}) ;</li>
 *   <li>après authentification, ouverture de la fenêtre principale adaptée au
 *       rôle de l'utilisateur ; la déconnexion ramène à l'écran de connexion.</li>
 * </ol>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        // Câblage de l'application et chargement des données de test.
        AppContext contexte = new AppContext();
        DonneesTest.charger(contexte);

        // Toute manipulation de Swing doit avoir lieu sur l'EDT.
        SwingUtilities.invokeLater(() -> {
            appliquerThemeSysteme();
            try {
                afficherConnexion(contexte);
            } catch (RuntimeException e) {
                JOptionPane.showMessageDialog(null,
                        "Erreur au démarrage de l'application : " + e.getMessage(),
                        "Erreur fatale", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    /**
     * Affiche l'écran de connexion. À la réussite, ouvre la fenêtre principale ;
     * à la déconnexion, ré-affiche cet écran (boucle login → app → login).
     *
     * @param contexte contexte applicatif partagé
     */
    private static void afficherConnexion(AppContext contexte) {
        new LoginFrame(contexte, utilisateur -> {
            MainFrame fenetre = new MainFrame(contexte, utilisateur,
                    () -> afficherConnexion(contexte));
            fenetre.setVisible(true);
        }).setVisible(true);
    }

    /**
     * Applique le look-and-feel natif du système d'exploitation pour une
     * apparence plus moderne. En cas d'échec, on conserve le thème par défaut.
     */
    private static void appliquerThemeSysteme() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Thème par défaut : non bloquant.
            System.err.println("Look-and-feel système indisponible, thème par défaut utilisé.");
        }
    }
}
