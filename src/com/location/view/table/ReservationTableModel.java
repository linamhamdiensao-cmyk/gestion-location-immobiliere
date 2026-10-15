package com.location.view.table;

import com.location.controller.BienController;
import com.location.controller.UtilisateurController;
import com.location.model.Bien;
import com.location.model.Reservation;
import com.location.model.Utilisateur;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de table Swing dédié aux {@link Reservation}.
 *
 * <p>Résout à la volée les libellés du bien et du locataire (à partir de leurs
 * identifiants) grâce aux contrôleurs fournis, pour un affichage lisible plutôt
 * que de simples numéros.</p>
 */
public class ReservationTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLONNES = {
            "ID", "Bien", "Locataire", "Début", "Fin", "Mois", "Total (MAD)", "Statut"
    };

    private List<Reservation> reservations = new ArrayList<>();
    private final BienController bienController;
    private final UtilisateurController utilisateurController;

    public ReservationTableModel(BienController bienController,
                                 UtilisateurController utilisateurController) {
        this.bienController = bienController;
        this.utilisateurController = utilisateurController;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = (reservations != null) ? reservations : new ArrayList<>();
        fireTableDataChanged();
    }

    public Reservation getReservationAt(int ligne) {
        return reservations.get(ligne);
    }

    @Override
    public int getRowCount() {
        return reservations.size();
    }

    @Override
    public int getColumnCount() {
        return COLONNES.length;
    }

    @Override
    public String getColumnName(int column) {
        return COLONNES[column];
    }

    @Override
    public Object getValueAt(int ligne, int colonne) {
        Reservation r = reservations.get(ligne);
        switch (colonne) {
            case 0: return r.getIdReservation();
            case 1: return libelleBien(r.getIdBien());
            case 2: return libelleLocataire(r.getIdLocataire());
            case 3: return r.getDateDebut();
            case 4: return r.getDateFin();
            case 5: return r.getNbMois();
            case 6: return r.getPrixTotal();
            case 7: return r.getStatut().getLibelle();
            default: return "";
        }
    }

    private String libelleBien(int idBien) {
        try {
            Bien b = bienController.trouver(idBien);
            return b.getTypeBien().getLibelle() + " à " + b.getVille().getLibelle();
        } catch (RuntimeException e) {
            return "Bien #" + idBien;
        }
    }

    private String libelleLocataire(int idLocataire) {
        try {
            Utilisateur u = utilisateurController.trouver(idLocataire);
            return u.getNomComplet();
        } catch (RuntimeException e) {
            return "Locataire #" + idLocataire;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
