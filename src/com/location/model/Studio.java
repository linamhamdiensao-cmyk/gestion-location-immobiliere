package com.location.model;

import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;

/**
 * Un studio : cas particulier d'{@link Appartement} comportant une seule pièce.
 *
 * <p>Règle de gestion : « Un studio est un appartement avec nb_pieces = 1 ».
 * Cette contrainte est <strong>forcée dans le constructeur</strong> et dans le
 * setter, ce qui illustre l'intérêt de l'encapsulation : l'objet garantit
 * lui-même la cohérence de son invariant métier.</p>
 */
public class Studio extends Appartement {

    public Studio() {
        super();
        super.setNbPieces(1);
    }

    /**
     * Constructeur d'un studio. Le nombre de pièces est ignoré et forcé à 1.
     */
    public Studio(double surface, int nbChambres, int nbSallesDeBain,
                  Ville ville, String quartier, int anneeConstruction,
                  boolean meuble, double prixParMois, int idProprietaire,
                  int etage, boolean avecBalcon, boolean avecParking, boolean avecAscenseur) {
        super(surface, 1, nbChambres, nbSallesDeBain, ville, quartier,
                anneeConstruction, meuble, prixParMois, idProprietaire,
                etage, avecBalcon, avecParking, avecAscenseur);
    }

    @Override
    public TypeBien getTypeBien() {
        return TypeBien.STUDIO;
    }

    /**
     * Empêche de modifier le nombre de pièces : un studio reste à 1 pièce.
     */
    @Override
    public void setNbPieces(int nbPieces) {
        super.setNbPieces(1);
    }

    @Override
    public String getDescriptionSpecifique() {
        return "Studio (1 pièce) - " + super.getDescriptionSpecifique();
    }
}
