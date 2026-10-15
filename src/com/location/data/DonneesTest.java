package com.location.data;

import com.location.controller.AppContext;
import com.location.model.Appartement;
import com.location.model.Bien;
import com.location.model.Chambre;
import com.location.model.Locataire;
import com.location.model.Maison;
import com.location.model.Proprietaire;
import com.location.model.Studio;
import com.location.model.enums.Ville;

/**
 * Jeu de données de démonstration injecté au démarrage de l'application.
 *
 * <p>Remplace temporairement le contenu d'une base de données. Crée quelques
 * propriétaires, locataires et biens variés (maison, appartement, studio,
 * chambre) afin que l'interface ne soit pas vide au lancement.</p>
 *
 * <p>Les entités sont insérées directement via les DAO pour conserver des
 * identifiants maîtrisés et lier proprement biens et propriétaires.</p>
 */
public final class DonneesTest {

    private DonneesTest() {
    }

    /**
     * Peuple le contexte applicatif avec des données d'exemple.
     *
     * @param ctx contexte applicatif à alimenter
     */
    public static void charger(AppContext ctx) {
        chargerUtilisateurs(ctx);
        chargerBiens(ctx);
        chargerReservations(ctx);
    }

    private static void chargerUtilisateurs(AppContext ctx) {
        // --- Propriétaires (rôle administrateur) ---
        Proprietaire p1 = new Proprietaire("El Amrani", "Karim", "karim.proprio@loc.ma", "+212600000001", "admin123");
        Proprietaire p2 = new Proprietaire("Benjelloun", "Salma", "salma.proprio@loc.ma", "+212600000002", "admin123");
        ctx.getUtilisateurDAO().sauvegarder(p1); // id = 1
        ctx.getUtilisateurDAO().sauvegarder(p2); // id = 2

        // --- Locataires (rôle client) ---
        Locataire l1 = new Locataire("Tazi", "Yassine", "yassine.client@loc.ma", "+212611111111", "client123");
        Locataire l2 = new Locataire("Idrissi", "Imane", "imane.client@loc.ma", "+212622222222", "client123");
        ctx.getUtilisateurDAO().sauvegarder(l1); // id = 3
        ctx.getUtilisateurDAO().sauvegarder(l2); // id = 4
    }

    private static void chargerBiens(AppContext ctx) {
        // Propriétaire #1 : une maison et un appartement.
        // Signature : surface, nbPieces, nbChambres, nbSdb, ville, quartier,
        //             annee, meuble, prix, idProprietaire, <champs spécifiques...>
        Maison maison = new Maison(180, 6, 4, 2, Ville.RABAT, "Agdal", 2010,
                true, 9000, 1, /* nbEtages */ 2, /* jardin */ 120, /* garages */ 1);
        Appartement appart = new Appartement(95, 4, 2, 1, Ville.CASABLANCA, "Maârif", 2016,
                true, 6500, 1, /* etage */ 3, true, true, true);

        // Propriétaire #2 : un studio et une chambre.
        Studio studio = new Studio(35, 1, 1, Ville.OUJDA, "Centre-ville", 2019,
                true, 2800, 2, /* etage */ 2, true, false, true);
        Chambre chambre = new Chambre(18, 1, Ville.NADOR, "Quartier des Oliviers", 2021,
                true, 1500, 2, /* etage */ 0, false, false, false, true, true);

        sauvegarderEtRattacher(ctx, maison, 1);
        sauvegarderEtRattacher(ctx, appart, 1);
        sauvegarderEtRattacher(ctx, studio, 2);
        sauvegarderEtRattacher(ctx, chambre, 2);
    }

    /**
     * Sauvegarde un bien puis le rattache à la liste des biens de son propriétaire.
     */
    private static void sauvegarderEtRattacher(AppContext ctx, Bien bien, int idProprietaire) {
        ctx.getBienDAO().sauvegarder(bien);
        ctx.getUtilisateurDAO().trouverParId(idProprietaire).ifPresent(u -> {
            if (u instanceof Proprietaire) {
                ((Proprietaire) u).ajouterBien(bien.getIdBien());
            }
        });
    }

    private static void chargerReservations(AppContext ctx) {
        // Une réservation d'exemple, créée puis confirmée, pour illustrer
        // l'historique et l'indisponibilité d'un bien.
        try {
            ctx.getReservationController().creer(
                    3, // locataire Yassine
                    2, // appartement Maârif
                    java.time.LocalDate.now().plusDays(7),
                    java.time.LocalDate.now().plusMonths(6).plusDays(7));
            // On laisse cette réservation en attente pour la démonstration de la
            // confirmation depuis l'interface.
        } catch (RuntimeException e) {
            System.err.println("Données de test : réservation non créée (" + e.getMessage() + ")");
        }
    }
}
