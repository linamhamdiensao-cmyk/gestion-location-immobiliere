package com.location.view.panels;

import com.location.controller.AppContext;
import com.location.model.Bien;
import com.location.model.Proprietaire;
import com.location.view.UITheme;
import com.location.view.dialog.BienDialog;
import com.location.view.table.BienTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.List;

/**
 * Panneau de <strong>gestion des logements</strong> (rôle administrateur /
 * propriétaire).
 *
 * <p>Présente le tableau des annonces et les actions Ajouter / Modifier /
 * Supprimer / Rafraîchir. Chaque action passe par le contrôleur ; les erreurs
 * métier sont interceptées et présentées dans des boîtes de dialogue claires.</p>
 */
public class GestionLogementsPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final BienTableModel tableModel = new BienTableModel();
    private final JTable table = new JTable(tableModel);

    public GestionLogementsPanel(AppContext ctx) {
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

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UITheme.PRIMAIRE.brighter()));

        // --- Barre d'actions ---
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        actions.setBackground(UITheme.FOND);
        JButton btnAjouter = bouton("Ajouter", UITheme.SUCCES);
        JButton btnModifier = bouton("Modifier", UITheme.ACCENT);
        JButton btnSupprimer = bouton("Supprimer", UITheme.DANGER);
        JButton btnRafraichir = bouton("Rafraîchir", UITheme.PRIMAIRE);

        btnAjouter.addActionListener(e -> ajouter());
        btnModifier.addActionListener(e -> modifier());
        btnSupprimer.addActionListener(e -> supprimer());
        btnRafraichir.addActionListener(e -> rafraichir());

        actions.add(btnAjouter);
        actions.add(btnModifier);
        actions.add(btnSupprimer);
        actions.add(btnRafraichir);

        add(actions, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }

    /** Recharge la liste des biens depuis le contrôleur. */
    public void rafraichir() {
        tableModel.setBiens(ctx.getBienController().listerTout());
    }

    private void ajouter() {
        List<Proprietaire> proprios = BienDialog.filtrerProprietaires(
                ctx.getUtilisateurController().listerTout());
        if (proprios.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez d'abord créer au moins un propriétaire.",
                    "Information", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        BienDialog dialog = new BienDialog(fenetre(), proprios, null);
        dialog.setVisible(true);
        Bien bien = dialog.getBien();
        if (bien != null) {
            try {
                ctx.getBienController().ajouter(bien);
                rafraichir();
            } catch (RuntimeException ex) {
                erreur(ex);
            }
        }
    }

    private void modifier() {
        Bien selection = bienSelectionne();
        if (selection == null) {
            return;
        }
        List<Proprietaire> proprios = BienDialog.filtrerProprietaires(
                ctx.getUtilisateurController().listerTout());
        BienDialog dialog = new BienDialog(fenetre(), proprios, selection);
        dialog.setVisible(true);
        Bien modifie = dialog.getBien();
        if (modifie != null) {
            try {
                ctx.getBienController().modifier(modifie);
                rafraichir();
            } catch (RuntimeException ex) {
                erreur(ex);
            }
        }
    }

    private void supprimer() {
        Bien selection = bienSelectionne();
        if (selection == null) {
            return;
        }
        int reponse = JOptionPane.showConfirmDialog(this,
                "Supprimer définitivement le logement #" + selection.getIdBien() + " ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (reponse == JOptionPane.YES_OPTION) {
            try {
                ctx.getBienController().supprimer(selection.getIdBien());
                rafraichir();
            } catch (RuntimeException ex) {
                erreur(ex);
            }
        }
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    private Bien bienSelectionne() {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            JOptionPane.showMessageDialog(this,
                    "Veuillez sélectionner un logement dans le tableau.",
                    "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return tableModel.getBienAt(table.convertRowIndexToModel(ligne));
    }

    private Frame fenetre() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void erreur(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
    }

    private JButton bouton(String texte, java.awt.Color couleur) {
        JButton b = new JButton(texte);
        b.setBackground(couleur);
        b.setForeground(UITheme.BLANC);
        b.setFocusPainted(false);
        b.setFont(UITheme.GRAS);
        return b;
    }
}
