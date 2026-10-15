package com.location.model.enums;

/**
 * Liste fermée des villes gérées par l'application.
 *
 * <p>Utiliser une énumération plutôt qu'une simple chaîne de caractères apporte
 * deux garanties fortes :</p>
 * <ul>
 *   <li>Impossible de créer un bien dans une ville non gérée (sécurité de typage).</li>
 *   <li>Les filtres de recherche s'appuient sur des valeurs sûres et comparables.</li>
 * </ul>
 *
 * <p>Chaque constante porte un libellé d'affichage lisible pour l'interface graphique.</p>
 */
public enum Ville {

    CASABLANCA("Casablanca"),
    RABAT("Rabat"),
    NADOR("Nador"),
    OUJDA("Oujda");

    /** Libellé présenté à l'utilisateur (peut différer du nom technique). */
    private final String libelle;

    Ville(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    /**
     * Affiche le libellé dans les composants Swing (JComboBox, JTable...).
     */
    @Override
    public String toString() {
        return libelle;
    }
}
