package com.location.view.panels;

import com.location.controller.AppContext;
import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;
import com.location.service.dto.FiltreRecherche;
import com.location.view.UITheme;
import com.location.view.table.BienTableModel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Panneau de <strong>recherche multicritère</strong> de logements.
 *
 * <p>Filtres proposés : ville, prix minimum, prix maximum et type de logement.
 * Les champs laissés vides ou sur « (Indifférent) » ne filtrent pas. La
 * construction du {@link FiltreRecherche} est déléguée au service via le
 * contrôleur.</p>
 */
public class RecherchePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    /** Valeur sentinelle pour « pas de filtre » dans les listes déroulantes. */
    private static final String INDIFFERENT = "(Indifférent)";

    private final AppContext ctx;

    private final JComboBox<Object> cbVille = new JComboBox<>();
    private final JComboBox<Object> cbType = new JComboBox<>();
    private final JTextField tfPrixMin = new JTextField(8);
    private final JTextField tfPrixMax = new JTextField(8);

    private final BienTableModel tableModel = new BienTableModel();
    private final JTable table = new JTable(tableModel);
    private final JLabel lblResultat = new JLabel(" ");

    public RecherchePanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setBackground(UITheme.FOND);

        construireInterface();
        rechercher(); // affichage initial : tous les biens disponibles
    }

    private void construireInterface() {
        // --- Zone de filtres ---
        JPanel filtres = new JPanel(new GridBagLayout());
        filtres.setBackground(UITheme.BLANC);
        filtres.setBorder(BorderFactory.createTitledBorder("Critères de recherche"));

        cbVille.addItem(INDIFFERENT);
        for (Ville v : Ville.values()) {
            cbVille.addItem(v);
        }
        cbType.addItem(INDIFFERENT);
        for (TypeBien t : TypeBien.values()) {
            cbType.addItem(t);
        }

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 8, 6, 8);
        g.anchor = GridBagConstraints.WEST;

        g.gridx = 0; g.gridy = 0; filtres.add(new JLabel("Ville :"), g);
        g.gridx = 1; filtres.add(cbVille, g);
        g.gridx = 2; filtres.add(new JLabel("Type :"), g);
        g.gridx = 3; filtres.add(cbType, g);

        g.gridx = 0; g.gridy = 1; filtres.add(new JLabel("Prix min :"), g);
        g.gridx = 1; filtres.add(tfPrixMin, g);
        g.gridx = 2; filtres.add(new JLabel("Prix max :"), g);
        g.gridx = 3; filtres.add(tfPrixMax, g);

        JButton btnRechercher = new JButton("Rechercher");
        btnRechercher.setBackground(UITheme.ACCENT);
        btnRechercher.setForeground(UITheme.BLANC);
        btnRechercher.setFont(UITheme.GRAS);
        btnRechercher.setFocusPainted(false);
        btnRechercher.addActionListener(e -> rechercher());

        JButton btnReinit = new JButton("Réinitialiser");
        btnReinit.addActionListener(e -> reinitialiser());

        g.gridx = 4; g.gridy = 0; filtres.add(btnRechercher, g);
        g.gridx = 4; g.gridy = 1; filtres.add(btnReinit, g);

        // --- Tableau de résultats ---
        table.setRowHeight(24);
        table.setAutoCreateRowSorter(true);
        table.getTableHeader().setReorderingAllowed(false);
        JScrollPane scroll = new JScrollPane(table);

        lblResultat.setFont(UITheme.GRAS);
        lblResultat.setForeground(UITheme.PRIMAIRE);

        JPanel sud = new JPanel(new BorderLayout());
        sud.setBackground(UITheme.FOND);
        sud.add(lblResultat, BorderLayout.WEST);

        add(filtres, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(sud, BorderLayout.SOUTH);
    }

    private void rechercher() {
        try {
            FiltreRecherche filtre = new FiltreRecherche();

            Object ville = cbVille.getSelectedItem();
            if (ville instanceof Ville) {
                filtre.avecVille((Ville) ville);
            }
            Object type = cbType.getSelectedItem();
            if (type instanceof TypeBien) {
                filtre.avecTypeBien((TypeBien) type);
            }
            filtre.avecPrixMin(lireBorne(tfPrixMin.getText(), "prix minimum"));
            filtre.avecPrixMax(lireBorne(tfPrixMax.getText(), "prix maximum"));

            var resultats = ctx.getBienController().rechercher(filtre);
            tableModel.setBiens(resultats);
            lblResultat.setText(resultats.size() + " logement(s) trouvé(s).");
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Recherche impossible", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void reinitialiser() {
        cbVille.setSelectedItem(INDIFFERENT);
        cbType.setSelectedItem(INDIFFERENT);
        tfPrixMin.setText("");
        tfPrixMax.setText("");
        rechercher();
    }

    /** Lit une borne de prix optionnelle ; retourne {@code null} si vide. */
    private Double lireBorne(String texte, String champ) {
        String t = texte.trim().replace(',', '.');
        if (t.isEmpty()) {
            return null;
        }
        try {
            double valeur = Double.parseDouble(t);
            if (valeur < 0) {
                throw new com.location.exception.ValidationException(
                        "Le « " + champ + " » ne peut pas être négatif.");
            }
            return valeur;
        } catch (NumberFormatException e) {
            throw new com.location.exception.ValidationException(
                    "Le « " + champ + " » doit être un nombre valide.");
        }
    }
}
