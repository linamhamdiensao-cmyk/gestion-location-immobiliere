package com.location.model;

import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;

/**
 * Une maison : bien individuel pouvant disposer d'étages, d'un jardin et de garages.
 *
 * <p>Hérite directement de {@link Bien} et ajoute les attributs spécifiques aux
 * maisons.</p>
 */
public class Maison extends Bien {

    /** Nombre d'étages de la maison. */
    private int nbEtages;

    /** Surface du jardin en m² (0 si pas de jardin). */
    private double surfaceJardin;

    /** Nombre de garages. */
    private int nbGarages;

    public Maison() {
        super();
    }

    /**
     * Constructeur complet d'une maison.
     */
    public Maison(double surface, int nbPieces, int nbChambres, int nbSallesDeBain,
                  Ville ville, String quartier, int anneeConstruction,
                  boolean meuble, double prixParMois, int idProprietaire,
                  int nbEtages, double surfaceJardin, int nbGarages) {
        super(surface, nbPieces, nbChambres, nbSallesDeBain, ville, quartier,
                anneeConstruction, meuble, prixParMois, idProprietaire);
        this.nbEtages = nbEtages;
        this.surfaceJardin = surfaceJardin;
        this.nbGarages = nbGarages;
    }

    @Override
    public TypeBien getTypeBien() {
        return TypeBien.MAISON;
    }

    @Override
    public String getDescriptionSpecifique() {
        return String.format("%d étage(s), jardin de %.0f m², %d garage(s)",
                nbEtages, surfaceJardin, nbGarages);
    }

    // ------------------------- Getters / Setters -------------------------

    public int getNbEtages() {
        return nbEtages;
    }

    public void setNbEtages(int nbEtages) {
        this.nbEtages = nbEtages;
    }

    public double getSurfaceJardin() {
        return surfaceJardin;
    }

    public void setSurfaceJardin(double surfaceJardin) {
        this.surfaceJardin = surfaceJardin;
    }

    public int getNbGarages() {
        return nbGarages;
    }

    public void setNbGarages(int nbGarages) {
        this.nbGarages = nbGarages;
    }
}
