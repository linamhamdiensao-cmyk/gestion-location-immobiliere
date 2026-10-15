package com.location.view.panels;

import com.location.controller.AppContext;
import com.location.model.Bien;
import com.location.view.UITheme;
import com.location.view.table.BienTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Panneau de <strong>consultation des annonces</strong> (lecture seule).
 *
 * <p>Affiche la liste de tous les logements et, lorsqu'une ligne est
 * sélectionnée, le détail complet du bien (y compris ses caractéristiques
 * spécifiques obtenues par polymorphisme via
 * {@link Bien#getDescriptionSpecifique()}).</p>
 */
public class LogementsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final BienTableModel tableModel = new BienTableModel();
    private final JTable table = new JTable(tableModel);
    private final JTextArea zoneDetail = new JTextArea(8, 30);

    public LogementsPanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(UITheme.FOND);

        construireInterface();
        rafraichir();
    }

    private void construireInterface() {
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                afficherDetail();
            }
        });

        zoneDetail.setEditable(false);
        zoneDetail.setFont(UITheme.NORMAL);
        zoneDetail.setBorder(BorderFactory.createTitledBorder("Détail du logement sélectionné"));
        zoneDetail.setBackground(UITheme.BLANC);

        JScrollPane scrollDetail = new JScrollPane(zoneDetail);
        scrollDetail.setPreferredSize(new Dimension(0, 170));

        JPanel haut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        haut.setBackground(UITheme.FOND);
        JButton btnRafraichir = new JButton("Rafraîchir");
        btnRafraichir.addActionListener(e -> rafraichir());
        haut.add(btnRafraichir);

        add(haut, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
        add(scrollDetail, BorderLayout.SOUTH);
    }

    public void rafraichir() {
        tableModel.setBiens(ctx.getBienController().listerTout());
        zoneDetail.setText("");
    }

    private void afficherDetail() {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            zoneDetail.setText("");
            return;
        }
        Bien b = tableModel.getBienAt(table.convertRowIndexToModel(ligne));
        StringBuilder sb = new StringBuilder();
        sb.append("Type            : ").append(b.getTypeBien().getLibelle()).append('\n');
        sb.append("Ville / quartier: ").append(b.getVille().getLibelle())
                .append(" — ").append(b.getQuartier()).append('\n');
        sb.append("Surface         : ").append(b.getSurface()).append(" m²\n");
        sb.append("Pièces          : ").append(b.getNbPieces())
                .append(" (dont ").append(b.getNbChambres()).append(" chambre(s), ")
                .append(b.getNbSallesDeBain()).append(" SDB)\n");
        sb.append("Année           : ").append(b.getAnneeConstruction()).append('\n');
        sb.append("Meublé          : ").append(b.isMeuble() ? "Oui" : "Non").append('\n');
        sb.append("Loyer mensuel   : ").append(String.format("%.0f MAD", b.getPrixParMois())).append('\n');
        sb.append("Disponibilité   : ").append(b.isDisponible() ? "Disponible" : "Indisponible").append('\n');
        sb.append("Spécificités    : ").append(b.getDescriptionSpecifique());
        zoneDetail.setText(sb.toString());
        zoneDetail.setCaretPosition(0);
    }
}
