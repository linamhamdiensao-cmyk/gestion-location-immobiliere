package com.location.view.panels;

import com.location.controller.AppContext;
import com.location.model.Utilisateur;
import com.location.view.UITheme;
import com.location.view.dialog.UtilisateurDialog;
import com.location.view.table.UtilisateurTableModel;

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

/**
 * Panneau de <strong>gestion des utilisateurs</strong> (propriétaires et
 * locataires) : tableau, ajout, modification et suppression.
 */
public class UtilisateursPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final AppContext ctx;
    private final UtilisateurTableModel tableModel = new UtilisateurTableModel();
    private final JTable table = new JTable(tableModel);

    public UtilisateursPanel(AppContext ctx) {
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
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    public void rafraichir() {
        tableModel.setUtilisateurs(ctx.getUtilisateurController().listerTout());
    }

    private void ajouter() {
        UtilisateurDialog dialog = new UtilisateurDialog(fenetre(), null);
        dialog.setVisible(true);
        Utilisateur u = dialog.getUtilisateur();
        if (u != null) {
            try {
                ctx.getUtilisateurController().ajouter(u);
                rafraichir();
            } catch (RuntimeException ex) {
                erreur(ex);
            }
        }
    }

    private void modifier() {
        Utilisateur selection = selection();
        if (selection == null) {
            return;
        }
        UtilisateurDialog dialog = new UtilisateurDialog(fenetre(), selection);
        dialog.setVisible(true);
        Utilisateur modifie = dialog.getUtilisateur();
        if (modifie != null) {
            try {
                ctx.getUtilisateurController().modifier(modifie);
                rafraichir();
            } catch (RuntimeException ex) {
                erreur(ex);
            }
        }
    }

    private void supprimer() {
        Utilisateur selection = selection();
        if (selection == null) {
            return;
        }
        int reponse = JOptionPane.showConfirmDialog(this,
                "Supprimer l'utilisateur « " + selection.getNomComplet() + " » ?",
                "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (reponse == JOptionPane.YES_OPTION) {
            try {
                ctx.getUtilisateurController().supprimer(selection.getIdUser());
                rafraichir();
            } catch (RuntimeException ex) {
                erreur(ex);
            }
        }
    }

    private Utilisateur selection() {
        int ligne = table.getSelectedRow();
        if (ligne < 0) {
            JOptionPane.showMessageDialog(this, "Veuillez sélectionner un utilisateur.",
                    "Aucune sélection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return tableModel.getUtilisateurAt(table.convertRowIndexToModel(ligne));
    }

    private Frame fenetre() {
        return (Frame) SwingUtilities.getWindowAncestor(this);
    }

    private void erreur(RuntimeException ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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
