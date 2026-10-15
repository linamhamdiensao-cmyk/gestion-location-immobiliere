package com.location.view.table;

import com.location.model.Bien;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Modèle de table Swing dédié aux {@link Bien}.
 *
 * <p>Préférer un {@link AbstractTableModel} typé à un {@code DefaultTableModel}
 * (à base de {@code Object[][]}) est une bonne pratique : la table reste liée à
 * de vrais objets métier, ce qui permet de récupérer le {@link Bien} sélectionné
 * via {@link #getBienAt(int)} sans reparser des cellules.</p>
 */
public class BienTableModel extends AbstractTableModel {

    private static final long serialVersionUID = 1L;

    private static final String[] COLONNES = {
            "ID", "Type", "Ville", "Quartier", "Surface (m²)",
            "Pièces", "Chambres", "Prix/mois (MAD)", "Meublé", "Disponible"
    };

    private List<Bien> biens = new ArrayList<>();

    /**
     * Remplace l'intégralité des données affichées et rafraîchit la table.
     */
    public void setBiens(List<Bien> biens) {
        this.biens = (biens != null) ? biens : new ArrayList<>();
        fireTableDataChanged();
    }

    /**
     * @param ligne index de ligne (vue)
     * @return le bien correspondant à la ligne
     */
    public Bien getBienAt(int ligne) {
        return biens.get(ligne);
    }

    @Override
    public int getRowCount() {
        return biens.size();
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
        Bien b = biens.get(ligne);
        switch (colonne) {
            case 0: return b.getIdBien();
            case 1: return b.getTypeBien().getLibelle();
            case 2: return b.getVille() != null ? b.getVille().getLibelle() : "";
            case 3: return b.getQuartier();
            case 4: return b.getSurface();
            case 5: return b.getNbPieces();
            case 6: return b.getNbChambres();
            case 7: return b.getPrixParMois();
            case 8: return b.isMeuble() ? "Oui" : "Non";
            case 9: return b.isDisponible() ? "Oui" : "Non";
            default: return "";
        }
    }

    /**
     * Le tableau est en lecture seule : l'édition passe par les boutons /
     * formulaires, pas par les cellules.
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
