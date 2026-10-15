package com.location.view.panels;

import com.location.controller.AppContext;
import com.location.model.Bien;
import com.location.model.Locataire;
import com.location.model.Proprietaire;
import com.location.model.Reservation;
import com.location.model.Utilisateur;
import com.location.view.UITheme;
import com.location.view.table.ReservationTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Panneau de <strong>réservation</strong>.
 *
 * <p>Partie haute : formulaire de création (choix du locataire, du logement et
 * des dates). Partie basse : tableau de toutes les réservations avec les actions
 * du cycle de vie (Confirmer, Annuler, Terminer), réservées notamment au
 * propriétaire pour la confirmation.</p>
 */
public class ReservationPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppContext ctx;

    /** Utilisateur connecté : conditionne les droits affichés. */
    private final transient Utilisateur connecte;
    private final boolean estProprietaire;

    private final JComboBox<Locataire> cbLocataire = new JComboBox<>();
    private final JComboBox<Bien> cbBien = new JComboBox<>();
    private final JComboBox<LocalDate> cbDateDebut = new JComboBox<>();
    private final JComboBox<LocalDate> cbDateFin = new JComboBox<>();
    private final JLabel lblEstimation = new JLabel(" ");

    private final ReservationTableModel tableModel;
    private final JTable table;

    public ReservationPanel(AppContext ctx, Utilisateur connecte) {
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
        add(construireFormulaire(), BorderLayout.NORTH);

        table.setRowHeight(24);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        add(new JScrollPane(table), BorderLayout.CENTER);

        add(construireActions(), BorderLayout.SOUTH);
    }

    private JPanel construireFormulaire() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BLANC);
        form.setBorder(BorderFactory.createTitledBorder("Nouvelle réservation"));

        // Propositions de dates : aujourd'hui à +24 mois (1er de chaque mois).
        LocalDate base = LocalDate.now();
        for (int i = 0; i <= 24; i++) {
            cbDateDebut.addItem(base.plusMonths(i).withDayOfMonth(1));
            cbDateFin.addItem(base.plusMonths(i).withDayOfMonth(1));
        }
        cbDateFin.setSelectedIndex(Math.min(6, cbDateFin.getItemCount() - 1));

        cbBien.addActionListener(e -> majEstimation());
        cbDateDebut.addActionListener(e -> majEstimation());
        cbDateFin.addActionListener(e -> majEstimation());

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Locataire :"), g);
        g.gridx = 1; form.add(cbLocataire, g);
        g.gridx = 2; form.add(new JLabel("Logement :"), g);
        g.gridx = 3; form.add(cbBien, g);

        g.gridx = 0; g.gridy = 1; form.add(new JLabel("Date début :"), g);
        g.gridx = 1; form.add(cbDateDebut, g);
        g.gridx = 2; form.add(new JLabel("Date fin :"), g);
        g.gridx = 3; form.add(cbDateFin, g);

        JButton btnReserver = new JButton("Réserver");
        btnReserver.setBackground(UITheme.SUCCES);
        btnReserver.setForeground(UITheme.BLANC);
        btnReserver.setFont(UITheme.GRAS);
        btnReserver.setFocusPainted(false);
        btnReserver.addActionListener(e -> reserver());

        g.gridx = 4; g.gridy = 0; form.add(btnReserver, g);
        lblEstimation.setFont(UITheme.GRAS);
        lblEstimation.setForeground(UITheme.ACCENT);
        g.gridx = 4; g.gridy = 1; form.add(lblEstimation, g);

        return form;
    }

    private JPanel construireActions() {
        JPanel actions = new JPanel();
        actions.setBackground(UITheme.FOND);

        JButton btnConfirmer = bouton("Confirmer", UITheme.SUCCES);
        JButton btnAnnuler = bouton("Annuler", UITheme.DANGER);
        JButton btnTerminer = bouton("Terminer", UITheme.PRIMAIRE);
        JButton btnRafraichir = bouton("Rafraîchir", UITheme.ACCENT);

        btnConfirmer.addActionListener(e -> agir("CONFIRMER"));
        btnAnnuler.addActionListener(e -> agir("ANNULER"));
        btnTerminer.addActionListener(e -> agir("TERMINER"));
        btnRafraichir.addActionListener(e -> rafraichir());

        // Confirmer / Terminer sont des prérogatives du propriétaire ;
        // le locataire ne voit que l'annulation de ses réservations.
        if (estProprietaire) {
            actions.add(btnConfirmer);
            actions.add(btnTerminer);
        }
        actions.add(btnAnnuler);
        actions.add(btnRafraichir);
        return actions;
    }

    /** Recharge les listes déroulantes et le tableau. */
    public void rafraichir() {
        // Locataires. Un locataire ne peut réserver que pour lui-même : la liste
        // est alors verrouillée sur son propre compte.
        cbLocataire.removeAllItems();
        if (estProprietaire) {
            for (Utilisateur u : ctx.getUtilisateurController().listerTout()) {
                if (u instanceof Locataire) {
                    cbLocataire.addItem((Locataire) u);
                }
            }
        } else if (connecte instanceof Locataire) {
            cbLocataire.addItem((Locataire) connecte);
            cbLocataire.setEnabled(false);
        }
        // Biens disponibles.
        cbBien.removeAllItems();
        for (Bien b : ctx.getBienController().listerTout()) {
            if (b.isDisponible()) {
                cbBien.addItem(b);
            }
        }
        // Tableau : le propriétaire voit toutes les réservations (pour les
        // confirmer) ; le locataire ne voit que les siennes.
        if (estProprietaire) {
            tableModel.setReservations(ctx.getReservationController().listerTout());
        } else {
            tableModel.setReservations(
                    ctx.getReservationController().historiqueLocataire(connecte.getIdUser()));
        }
        majEstimation();
    }

    private void reserver() {
        Locataire locataire = (Locataire) cbLocataire.getSelectedItem();
        Bien bien = (Bien) cbBien.getSelectedItem();
        if (locataire == null || bien == null) {
            JOptionPane.showMessageDialog(this,
                    "Sélectionnez un locataire et un logement disponible.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        try {
            LocalDate debut = (LocalDate) cbDateDebut.getSelectedItem();
            LocalDate fin = (LocalDate) cbDateFin.getSelectedItem();
            Reservation r = ctx.getReservationController()
                    .creer(locataire.getIdUser(), bien.getIdBien(), debut, fin);
            JOptionPane.showMessageDialog(this,
                    "Réservation #" + r.getIdReservation() + " créée (statut : "
                            + r.getStatut().getLibelle() + ").\nMontant total : "
                            + String.format("%.0f MAD", r.getPrixTotal()),
                    "Réservation enregistrée", JOptionPane.INFORMATION_MESSAGE);
            rafraichir();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Format de date invalide.",
                    "Erreur", JOptionPane.WARNING_MESSAGE);
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Réservation impossible", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void agir(String action) {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            JOptionPane.showMessageDialog(this, "Sélectionnez une réservation.",
                    "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        Reservation r = tableModel.getReservationAt(table.convertRowIndexToModel(ligne));
        try {
            switch (action) {
                case "CONFIRMER":
                    ctx.getReservationController().confirmer(r.getIdReservation());
                    break;
                case "ANNULER":
                    ctx.getReservationController().annuler(r.getIdReservation());
                    break;
                case "TERMINER":
                    ctx.getReservationController().terminer(r.getIdReservation());
                    break;
                default:
                    break;
            }
            rafraichir();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Action impossible", JOptionPane.WARNING_MESSAGE);
        }
    }

    /** Met à jour l'estimation du coût total selon le bien et les dates choisis. */
    private void majEstimation() {
        Bien bien = (Bien) cbBien.getSelectedItem();
        LocalDate debut = (LocalDate) cbDateDebut.getSelectedItem();
        LocalDate fin = (LocalDate) cbDateFin.getSelectedItem();
        if (bien == null || debut == null || fin == null || !fin.isAfter(debut)) {
            lblEstimation.setText(" ");
            return;
        }
        Reservation estimation = new Reservation(0, bien.getIdBien(), debut, fin, bien.getPrixParMois());
        lblEstimation.setText(String.format("≈ %d mois — %.0f MAD",
                estimation.getNbMois(), estimation.getPrixTotal()));
    }

    private JButton bouton(String texte, java.awt.Color couleur) {
        JButton b = new JButton(texte);
        b.setBackground(couleur);
        b.setForeground(UITheme.BLANC);
        b.setFont(UITheme.GRAS);
        b.setFocusPainted(false);
        return b;
    }
}
