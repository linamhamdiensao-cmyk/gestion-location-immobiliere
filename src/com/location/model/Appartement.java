package com.location.model;

import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;

/**
 * Un appartement : bien situé dans un immeuble, à un étage donné.
 *
 * <p>Sert également de classe parente à {@link Studio} (et donc indirectement à
 * {@link Chambre}), conformément à la hiérarchie du modèle.</p>
 */
public class Appartement extends Bien {

    /** Étage où se situe l'appartement (0 = rez-de-chaussée). */
    private int etage;

    /** Présence d'un balcon. */
    private boolean avecBalcon;

    /** Présence d'une place de parking. */
    private boolean avecParking;

    /** Présence d'un ascenseur dans l'immeuble. */
    private boolean avecAscenseur;

    public Appartement() {
        super();
    }

    /**
     * Constructeur complet d'un appartement.
     */
    public Appartement(double surface, int nbPieces, int nbChambres, int nbSallesDeBain,
                       Ville ville, String quartier, int anneeConstruction,
                       boolean meuble, double prixParMois, int idProprietaire,
                       int etage, boolean avecBalcon, boolean avecParking, boolean avecAscenseur) {
        super(surface, nbPieces, nbChambres, nbSallesDeBain, ville, quartier,
                anneeConstruction, meuble, prixParMois, idProprietaire);
        this.etage = etage;
        this.avecBalcon = avecBalcon;
        this.avecParking = avecParking;
        this.avecAscenseur = avecAscenseur;
    }

    @Override
    public TypeBien getTypeBien() {
        return TypeBien.APPARTEMENT;
    }

    @Override
    public String getDescriptionSpecifique() {
        StringBuilder sb = new StringBuilder();
        sb.append("Étage ").append(etage);
        if (avecBalcon) {
            sb.append(", balcon");
        }
        if (avecParking) {
            sb.append(", parking");
        }
        if (avecAscenseur) {
            sb.append(", ascenseur");
        }
        return sb.toString();
    }

    // ------------------------- Getters / Setters -------------------------

    public int getEtage() {
        return etage;
    }

    public void setEtage(int etage) {
        this.etage = etage;
    }

    public boolean isAvecBalcon() {
        return avecBalcon;
    }

    public void setAvecBalcon(boolean avecBalcon) {
        this.avecBalcon = avecBalcon;
    }

    public boolean isAvecParking() {
        return avecParking;
    }

    public void setAvecParking(boolean avecParking) {
        this.avecParking = avecParking;
    }

    public boolean isAvecAscenseur() {
        return avecAscenseur;
    }

    public void setAvecAscenseur(boolean avecAscenseur) {
        this.avecAscenseur = avecAscenseur;
    }
}
