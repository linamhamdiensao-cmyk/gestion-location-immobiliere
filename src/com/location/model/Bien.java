package com.location.model;

import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;

import java.util.Objects;

/**
 * Classe <strong>abstraite</strong> représentant un bien immobilier mis en location.
 *
 * <p>C'est la racine de la hiérarchie d'héritage :</p>
 * <pre>
 *   Bien (abstrait)
 *    ├── Maison
 *    └── Appartement
 *          └── Studio
 *                └── Chambre
 * </pre>
 *
 * <p><strong>Encapsulation :</strong> tous les attributs sont {@code private} ;
 * l'accès se fait exclusivement par les getters/setters, ce qui permet de
 * contrôler les invariants (ex. surface positive) lors des évolutions futures.</p>
 *
 * <p><strong>Polymorphisme :</strong> la méthode {@link #getTypeBien()} est
 * {@code abstract}. Chaque sous-classe déclare son propre type, ce qui permet
 * de traiter une collection de {@code Bien} de façon uniforme tout en obtenant
 * un comportement spécialisé.</p>
 */
public abstract class Bien {

    // ----------------------------------------------------------------------
    // Attributs (encapsulés)
    // ----------------------------------------------------------------------

    /** Identifiant unique, attribué par la couche de persistance (DAO). */
    private int idBien;

    /** Surface habitable en m². */
    private double surface;

    /** Nombre total de pièces. */
    private int nbPieces;

    /** Nombre de chambres (bedrooms). */
    private int nbChambres;

    /** Nombre de salles de bain (bathrooms). */
    private int nbSallesDeBain;

    /** Ville où se situe le bien (valeur contrôlée par l'énumération {@link Ville}). */
    private Ville ville;

    /** Quartier précis dans la ville. */
    private String quartier;

    /** Année de construction du bâtiment (ex. 2008). */
    private int anneeConstruction;

    /** Bien loué meublé ou non. */
    private boolean meuble;

    /** Loyer mensuel en dirhams (MAD). */
    private double prixParMois;

    /** Disponibilité à la location (passe à {@code false} après confirmation d'une réservation). */
    private boolean disponible;

    /** Identifiant du propriétaire (clé étrangère {@code id_user} du MLD). */
    private int idProprietaire;

    // ----------------------------------------------------------------------
    // Constructeurs
    // ----------------------------------------------------------------------

    /**
     * Constructeur par défaut (utile pour les formulaires Swing qui
     * construisent l'objet champ par champ).
     */
    protected Bien() {
        this.disponible = true;
    }

    /**
     * Constructeur complet des attributs communs à tous les biens.
     *
     * @param surface           surface habitable en m²
     * @param nbPieces          nombre total de pièces
     * @param nbChambres        nombre de chambres
     * @param nbSallesDeBain    nombre de salles de bain
     * @param ville             ville du bien
     * @param quartier          quartier du bien
     * @param anneeConstruction année de construction
     * @param meuble            meublé ou non
     * @param prixParMois       loyer mensuel
     * @param idProprietaire    identifiant du propriétaire
     */
    protected Bien(double surface, int nbPieces, int nbChambres, int nbSallesDeBain,
                   Ville ville, String quartier, int anneeConstruction,
                   boolean meuble, double prixParMois, int idProprietaire) {
        this.surface = surface;
        this.nbPieces = nbPieces;
        this.nbChambres = nbChambres;
        this.nbSallesDeBain = nbSallesDeBain;
        this.ville = ville;
        this.quartier = quartier;
        this.anneeConstruction = anneeConstruction;
        this.meuble = meuble;
        this.prixParMois = prixParMois;
        this.idProprietaire = idProprietaire;
        this.disponible = true;
    }

    // ----------------------------------------------------------------------
    // Méthode polymorphe
    // ----------------------------------------------------------------------

    /**
     * Retourne le type concret du bien. Implémentée par chaque sous-classe.
     *
     * @return le {@link TypeBien} correspondant
     */
    public abstract TypeBien getTypeBien();

    /**
     * Résumé des caractéristiques propres à la sous-classe, destiné à
     * l'affichage détaillé. Chaque sous-classe enrichit ce texte.
     *
     * @return description spécifique (jardin, étage, balcon...)
     */
    public abstract String getDescriptionSpecifique();

    // ----------------------------------------------------------------------
    // Getters / Setters (encapsulation)
    // ----------------------------------------------------------------------

    public int getIdBien() {
        return idBien;
    }

    public void setIdBien(int idBien) {
        this.idBien = idBien;
    }

    public double getSurface() {
        return surface;
    }

    public void setSurface(double surface) {
        this.surface = surface;
    }

    public int getNbPieces() {
        return nbPieces;
    }

    public void setNbPieces(int nbPieces) {
        this.nbPieces = nbPieces;
    }

    public int getNbChambres() {
        return nbChambres;
    }

    public void setNbChambres(int nbChambres) {
        this.nbChambres = nbChambres;
    }

    public int getNbSallesDeBain() {
        return nbSallesDeBain;
    }

    public void setNbSallesDeBain(int nbSallesDeBain) {
        this.nbSallesDeBain = nbSallesDeBain;
    }

    public Ville getVille() {
        return ville;
    }

    public void setVille(Ville ville) {
        this.ville = ville;
    }

    public String getQuartier() {
        return quartier;
    }

    public void setQuartier(String quartier) {
        this.quartier = quartier;
    }

    public int getAnneeConstruction() {
        return anneeConstruction;
    }

    public void setAnneeConstruction(int anneeConstruction) {
        this.anneeConstruction = anneeConstruction;
    }

    public boolean isMeuble() {
        return meuble;
    }

    public void setMeuble(boolean meuble) {
        this.meuble = meuble;
    }

    public double getPrixParMois() {
        return prixParMois;
    }

    public void setPrixParMois(double prixParMois) {
        this.prixParMois = prixParMois;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public int getIdProprietaire() {
        return idProprietaire;
    }

    public void setIdProprietaire(int idProprietaire) {
        this.idProprietaire = idProprietaire;
    }

    // ----------------------------------------------------------------------
    // Méthodes utilitaires
    // ----------------------------------------------------------------------

    /**
     * Deux biens sont considérés égaux s'ils partagent le même identifiant.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Bien)) {
            return false;
        }
        Bien autre = (Bien) o;
        return idBien == autre.idBien;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idBien);
    }

    @Override
    public String toString() {
        return String.format("[#%d] %s à %s (%s) - %.0f m², %d pièce(s) - %.0f MAD/mois - %s",
                idBien,
                getTypeBien().getLibelle(),
                ville != null ? ville.getLibelle() : "?",
                quartier,
                surface,
                nbPieces,
                prixParMois,
                disponible ? "Disponible" : "Indisponible");
    }
}
