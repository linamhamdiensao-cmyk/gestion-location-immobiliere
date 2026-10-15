package com.location.view.table;

import com.location.model.Utilisateur;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de table Swing dédié aux {@link Utilisateur}.
 */
public class UtilisateurTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLONNES = {
            "ID", "Type", "Nom", "Prénom", "Email", "Téléphone", "Inscrit le"
    };

    private List<Utilisateur> utilisateurs = new ArrayList<>();

    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = (utilisateurs != null) ? utilisateurs : new ArrayList<>();
        fireTableDataChanged();
    }

    public Utilisateur getUtilisateurAt(int ligne) {
        return utilisateurs.get(ligne);
    }

    @Override
    public int getRowCount() {
        return utilisateurs.size();
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
        Utilisateur u = utilisateurs.get(ligne);
        switch (colonne) {
            case 0: return u.getIdUser();
            case 1: return u.getTypeUtilisateur().getLibelle();
            case 2: return u.getNom();
            case 3: return u.getPrenom();
            case 4: return u.getEmail();
            case 5: return u.getTelephone();
            case 6: return u.getDateInscription();
            default: return "";
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
