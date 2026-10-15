package com.location.model;

import com.location.model.enums.StatutReservation;

import java.time.LocalDate;
import java.time.Period;
import java.util.Objects;

/**
 * Réservation d'un bien par un locataire sur une période donnée.
 *
 * <p>Liens stockés sous forme d'identifiants ({@code idLocataire},
 * {@code idBien}) pour rester aligné sur le modèle relationnel (MLD).</p>
 *
 * <p>Le nombre de mois est <strong>calculé automatiquement</strong> à partir des
 * dates de début et de fin, et le prix total en découle. Le statut initial est
 * {@link StatutReservation#EN_ATTENTE} (règle de gestion).</p>
 */
public class Reservation {

    private int idReservation;
    private int idLocataire;
    private int idBien;
    private LocalDate dateDebut;
    private LocalDate dateFin;
    private LocalDate dateReservation;
    private StatutReservation statut;

    /** Nombre de mois de location, dérivé des dates. */
    private int nbMois;

    /** Prix total = nbMois × loyer mensuel du bien au moment de la réservation. */
    private double prixTotal;

    public Reservation() {
        this.dateReservation = LocalDate.now();
        this.statut = StatutReservation.EN_ATTENTE;
    }

    /**
     * Construit une réservation et calcule automatiquement le nombre de mois et
     * le prix total.
     *
     * @param idLocataire    identifiant du locataire
     * @param idBien         identifiant du bien réservé
     * @param dateDebut      début de la location
     * @param dateFin        fin de la location
     * @param prixMensuel    loyer mensuel du bien (figé dans la réservation)
     */
    public Reservation(int idLocataire, int idBien, LocalDate dateDebut, LocalDate dateFin,
                       double prixMensuel) {
        this.idLocataire = idLocataire;
        this.idBien = idBien;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.dateReservation = LocalDate.now();
        this.statut = StatutReservation.EN_ATTENTE;
        recalculer(prixMensuel);
    }

    /**
     * (Re)calcule le nombre de mois et le prix total à partir des dates.
     *
     * <p>On compte les <strong>mois calendaires complets</strong> entre les deux
     * dates ; toute fraction de mois entamée est facturée comme un mois entier
     * (un loyer étant dû au mois). Exemples :</p>
     * <ul>
     *   <li>du 01/01 au 01/07 → 6 mois pile ;</li>
     *   <li>du 01/01 au 10/02 → 1 mois + 9 jours → 2 mois.</li>
     * </ul>
     *
     * @param prixMensuel loyer mensuel à appliquer
     */
    public void recalculer(double prixMensuel) {
        if (dateDebut != null && dateFin != null && dateFin.isAfter(dateDebut)) {
            Period periode = Period.between(dateDebut, dateFin);
            int mois = periode.getYears() * 12 + periode.getMonths();
            if (periode.getDays() > 0) {
                mois++; // toute période entamée est due
            }
            this.nbMois = Math.max(mois, 1);
        } else {
            this.nbMois = 0;
        }
        this.prixTotal = this.nbMois * prixMensuel;
    }

    // ----------------------------------------------------------------------
    // Transitions d'état (cf. règles de gestion)
    // ----------------------------------------------------------------------

    /** Passe la réservation à l'état confirmé (validation du propriétaire). */
    public void confirmer() {
        this.statut = StatutReservation.CONFIRME;
    }

    /** Annule la réservation. */
    public void annuler() {
        this.statut = StatutReservation.ANNULE;
    }

    /** Termine la réservation (période écoulée). */
    public void terminer() {
        this.statut = StatutReservation.TERMINE;
    }

    /** @return {@code true} si la réservation est encore modifiable. */
    public boolean estModifiable() {
        return statut == StatutReservation.EN_ATTENTE || statut == StatutReservation.CONFIRME;
    }

    // ----------------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------------

    public int getIdReservation() {
        return idReservation;
    }

    public void setIdReservation(int idReservation) {
        this.idReservation = idReservation;
    }

    public int getIdLocataire() {
        return idLocataire;
    }

    public void setIdLocataire(int idLocataire) {
        this.idLocataire = idLocataire;
    }

    public int getIdBien() {
        return idBien;
    }

    public void setIdBien(int idBien) {
        this.idBien = idBien;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }

    public LocalDate getDateReservation() {
        return dateReservation;
    }

    public void setDateReservation(LocalDate dateReservation) {
        this.dateReservation = dateReservation;
    }

    public StatutReservation getStatut() {
        return statut;
    }

    public void setStatut(StatutReservation statut) {
        this.statut = statut;
    }

    public int getNbMois() {
        return nbMois;
    }

    public double getPrixTotal() {
        return prixTotal;
    }

    public void setPrixTotal(double prixTotal) {
        this.prixTotal = prixTotal;
    }

    // ----------------------------------------------------------------------
    // Méthodes utilitaires
    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Reservation)) {
            return false;
        }
        return idReservation == ((Reservation) o).idReservation;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idReservation);
    }

    @Override
    public String toString() {
        return String.format("Réservation #%d : bien #%d par locataire #%d du %s au %s (%d mois, %.0f MAD) - %s",
                idReservation, idBien, idLocataire, dateDebut, dateFin, nbMois, prixTotal, statut);
    }
}
