package com.location.model.enums;

/**
 * Type de bien immobilier géré par l'application.
 *
 * <p>Cette énumération reflète la hiérarchie d'héritage des modèles
 * ({@code Maison}, {@code Appartement}, {@code Studio}, {@code Chambre}).
 * Elle sert notamment de critère de filtrage dans la recherche et de libellé
 * d'affichage dans les tableaux.</p>
 */
public enum TypeBien {

    MAISON("Maison"),
    APPARTEMENT("Appartement"),
    STUDIO("Studio"),
    CHAMBRE("Chambre");

    private final String libelle;

    TypeBien(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    @Override
    public String toString() {
        return libelle;
    }
}
