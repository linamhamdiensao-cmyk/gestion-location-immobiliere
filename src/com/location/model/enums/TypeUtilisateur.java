package com.location.model.enums;

/**
 * Rôle d'un utilisateur dans l'application.
 *
 * <p>Correspond à l'attribut {@code type_user} du modèle relationnel (MLD).
 * Il permet de distinguer, sans avoir à tester le type Java concret de l'objet,
 * les deux familles d'utilisateurs :</p>
 * <ul>
 *   <li>{@link #PROPRIETAIRE} : publie et gère des biens (rôle administrateur).</li>
 *   <li>{@link #LOCATAIRE} : recherche et réserve des biens (rôle client).</li>
 * </ul>
 */
public enum TypeUtilisateur {

    PROPRIETAIRE("Propriétaire"),
    LOCATAIRE("Locataire");

    private final String libelle;

    TypeUtilisateur(String libelle) {
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
