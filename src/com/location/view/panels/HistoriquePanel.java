package com.location.view.panels;

import com.location.controller.AppContext;
import com.location.model.Locataire;
import com.location.model.Proprietaire;
import com.location.model.Utilisateur;
import com.location.view.UITheme;
import com.location.view.table.ReservationTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import java.awt.BorderLayout;
import java.awt.FlowLayout;

/**
 * Panneau de consultation de l'<strong>historique des réservations</strong>
 * d'un locataire sélectionné.
 */
public class HistoriquePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final transient Utilisateur connecte;
    private final boolean estProprietaire;
    private final JComboBox<Locataire> cbLocataire = new JComboBox<>();
    private final ReservationTableModel tableModel;
    private final JTable table;

    public HistoriquePanel(AppContext ctx, Utilisateur connecte) {
        this.ctx = ctx;
        this.connecte = connecte;
        this.estProprietaire = connecte instanceof Proprietaire;
        this.tableModel = new ReservationTableModel(
                ctx.getBienController(), ctx.getUtilisateurController());
        this.table = new JTable(tableModel);

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(UITheme.FOND);

        construireInterface();
        rafraichir();
    }

    private void construireInterface() {
        JPanel haut = new JPanel(new FlowLayout(FlowLayout.LEFT));
        haut.setBackground(UITheme.FOND);
        JLabel lbl = new JLabel("Locataire :");
        lbl.setFont(UITheme.GRAS);
        haut.add(lbl);
        haut.add(cbLocataire);

        JButton btnRafraichir = new JButton("Rafraîchir");
        btnRafraichir.addActionListener(e -> rafraichir());
        haut.add(btnRafraichir);

        cbLocataire.addActionListener(e -> afficherHistorique());

        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);

        add(haut, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    /**
     * Recharge la liste des locataires puis affiche l'historique.
     *
     * <p>Le propriétaire peut choisir n'importe quel locataire ; un locataire
     * connecté est verrouillé sur son propre historique.</p>
     */
    public void rafraichir() {
        Locataire selectionPrecedente = (Locataire) cbLocataire.getSelectedItem();
        cbLocataire.removeAllItems();
        if (estProprietaire) {
            for (Utilisateur u : ctx.getUtilisateurController().listerTout()) {
                if (u instanceof Locataire) {
                    cbLocataire.addItem((Locataire) u);
                }
            }
            if (selectionPrecedente != null) {
                cbLocataire.setSelectedItem(selectionPrecedente);
            }
        } else if (connecte instanceof Locataire) {
            cbLocataire.addItem((Locataire) connecte);
            cbLocataire.setEnabled(false);
        }
        afficherHistorique();
    }

    private void afficherHistorique() {
        Locataire locataire = (Locataire) cbLocataire.getSelectedItem();
        if (locataire == null) {
            tableModel.setReservations(java.util.Collections.emptyList());
            return;
        }
        tableModel.setReservations(
                ctx.getReservationController().historiqueLocataire(locataire.getIdUser()));
    }
}
