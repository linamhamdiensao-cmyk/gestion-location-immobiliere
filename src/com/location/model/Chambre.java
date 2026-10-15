package com.location.model;

import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;

/**
 * Une chambre : cas particulier de {@link Studio} (donc à une seule pièce),
 * typiquement louée en colocation ou en résidence.
 *
 * <p>Ajoute deux informations propres : la salle de bain est-elle privée et les
 * charges (eau / électricité / WIFI) sont-elles incluses dans le loyer.</p>
 */
public class Chambre extends Studio {

    /** Salle de bain privée ({@code true}) ou partagée ({@code false}). */
    private boolean salleDeBainPrivee;

    /** Charges (eau / électricité / WIFI) incluses dans le prix. */
    private boolean chargesIncluses;

    public Chambre() {
        super();
    }

    /**
     * Constructeur complet d'une chambre.
     */
    public Chambre(double surface, int nbSallesDeBain,
                   Ville ville, String quartier, int anneeConstruction,
                   boolean meuble, double prixParMois, int idProprietaire,
                   int etage, boolean avecBalcon, boolean avecParking, boolean avecAscenseur,
                   boolean salleDeBainPrivee, boolean chargesIncluses) {
        super(surface, 1, nbSallesDeBain, ville, quartier, anneeConstruction,
                meuble, prixParMois, idProprietaire, etage, avecBalcon, avecParking, avecAscenseur);
        this.salleDeBainPrivee = salleDeBainPrivee;
        this.chargesIncluses = chargesIncluses;
    }

    @Override
    public TypeBien getTypeBien() {
        return TypeBien.CHAMBRE;
    }

    @Override
    public String getDescriptionSpecifique() {
        return String.format("Chambre - salle de bain %s, charges %s",
                salleDeBainPrivee ? "privée" : "partagée",
                chargesIncluses ? "incluses" : "non incluses");
    }

    // ------------------------- Getters / Setters -------------------------

    public boolean isSalleDeBainPrivee() {
        return salleDeBainPrivee;
    }

    public void setSalleDeBainPrivee(boolean salleDeBainPrivee) {
        this.salleDeBainPrivee = salleDeBainPrivee;
    }

    public boolean isChargesIncluses() {
        return chargesIncluses;
    }

    public void setChargesIncluses(boolean chargesIncluses) {
        this.chargesIncluses = chargesIncluses;
    }
}
