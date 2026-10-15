package com.location.view.dialog;

import com.location.exception.ValidationException;
import com.location.model.Appartement;
import com.location.model.Bien;
import com.location.model.Chambre;
import com.location.model.Maison;
import com.location.model.Proprietaire;
import com.location.model.Studio;
import com.location.model.Utilisateur;
import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;
import com.location.view.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Boîte de dialogue modale d'ajout / modification d'un {@link Bien}.
 *
 * <p>Le formulaire s'adapte dynamiquement au type de bien sélectionné grâce à un
 * {@link CardLayout} : les champs spécifiques (jardin, étage, balcon, charges…)
 * apparaissent selon qu'il s'agit d'une maison, d'un appartement, d'un studio ou
 * d'une chambre.</p>
 *
 * <p>À la validation, les saisies sont contrôlées puis l'objet métier concret
 * est construit. Le résultat est récupérable via {@link #getBien()} ; il vaut
 * {@code null} si l'utilisateur a annulé.</p>
 */
public class BienDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    // --- Champs communs ---
    private final JComboBox<TypeBien> cbType = new JComboBox<>(TypeBien.values());
    private final JComboBox<Ville> cbVille = new JComboBox<>(Ville.values());
    private final JComboBox<Proprietaire> cbProprietaire = new JComboBox<>();
    private final JTextField tfQuartier = new JTextField();
    private final JTextField tfSurface = new JTextField();
    private final JTextField tfNbPieces = new JTextField();
    private final JTextField tfNbChambres = new JTextField();
    private final JTextField tfNbSdb = new JTextField();
    private final JTextField tfAnnee = new JTextField();
    private final JTextField tfPrix = new JTextField();
    private final JCheckBox chkMeuble = new JCheckBox("Meublé");

    // --- Champs spécifiques : Maison ---
    private final JTextField tfNbEtages = new JTextField();
    private final JTextField tfSurfaceJardin = new JTextField();
    private final JTextField tfNbGarages = new JTextField();

    // --- Champs spécifiques : Appartement / Studio ---
    private final JTextField tfEtage = new JTextField();
    private final JCheckBox chkBalcon = new JCheckBox("Balcon");
    private final JCheckBox chkParking = new JCheckBox("Parking");
    private final JCheckBox chkAscenseur = new JCheckBox("Ascenseur");

    // --- Champs spécifiques : Chambre (en plus des champs appartement) ---
    private final JTextField tfEtageCh = new JTextField();
    private final JCheckBox chkBalconCh = new JCheckBox("Balcon");
    private final JCheckBox chkParkingCh = new JCheckBox("Parking");
    private final JCheckBox chkAscenseurCh = new JCheckBox("Ascenseur");
    private final JCheckBox chkSdbPrivee = new JCheckBox("Salle de bain privée");
    private final JCheckBox chkChargesIncluses = new JCheckBox("Charges incluses");

    private final JPanel cartesSpecifiques = new JPanel(new CardLayout());

    /** Bien éventuellement édité (null si création). Sert à conserver l'id. */
    private final Bien bienExistant;

    /** Résultat : le bien validé, ou {@code null} si annulation. */
    private Bien resultat;

    /**
     * @param parent        fenêtre parente
     * @param proprietaires liste des propriétaires sélectionnables
     * @param bienAEditer   bien à modifier, ou {@code null} pour une création
     */
    public BienDialog(Frame parent, List<Proprietaire> proprietaires, Bien bienAEditer) {
        super(parent, bienAEditer == null ? "Ajouter un logement" : "Modifier le logement", true);
        this.bienExistant = bienAEditer;

        proprietaires.forEach(cbProprietaire::addItem);

        construireInterface();
        if (bienAEditer != null) {
            preremplir(bienAEditer);
        }
        mettreAJourCarte();

        pack();
        setMinimumSize(new Dimension(460, getHeight()));
        setLocationRelativeTo(parent);
    }

    private void construireInterface() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        principal.setBackground(UITheme.BLANC);

        // --- Formulaire commun ---
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BLANC);
        int y = 0;
        ajouterLigne(form, y++, "Type :", cbType);
        ajouterLigne(form, y++, "Propriétaire :", cbProprietaire);
        ajouterLigne(form, y++, "Ville :", cbVille);
        ajouterLigne(form, y++, "Quartier :", tfQuartier);
        ajouterLigne(form, y++, "Surface (m²) :", tfSurface);
        ajouterLigne(form, y++, "Nombre de pièces :", tfNbPieces);
        ajouterLigne(form, y++, "Nombre de chambres :", tfNbChambres);
        ajouterLigne(form, y++, "Salles de bain :", tfNbSdb);
        ajouterLigne(form, y++, "Année de construction :", tfAnnee);
        ajouterLigne(form, y++, "Prix / mois (MAD) :", tfPrix);
        ajouterLigne(form, y++, "", chkMeuble);

        // --- Cartes spécifiques ---
        cartesSpecifiques.setBackground(UITheme.BLANC);
        cartesSpecifiques.add(construireCarteMaison(), TypeBien.MAISON.name());
        cartesSpecifiques.add(construireCarteAppartement(), TypeBien.APPARTEMENT.name());
        // Studio réutilise la carte appartement (mêmes champs).
        cartesSpecifiques.add(construireCarteChambre(), TypeBien.CHAMBRE.name());

        JPanel zoneSpecifique = new JPanel(new BorderLayout());
        zoneSpecifique.setBackground(UITheme.BLANC);
        zoneSpecifique.setBorder(BorderFactory.createTitledBorder("Caractéristiques spécifiques"));
        zoneSpecifique.add(cartesSpecifiques, BorderLayout.CENTER);

        // --- Boutons ---
        JPanel boutons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        boutons.setBackground(UITheme.BLANC);
        JButton btnValider = new JButton("Enregistrer");
        JButton btnAnnuler = new JButton("Annuler");
        btnValider.addActionListener(e -> valider());
        btnAnnuler.addActionListener(e -> {
            resultat = null;
            dispose();
        });
        boutons.add(btnAnnuler);
        boutons.add(btnValider);

        // Réactions dynamiques.
        cbType.addActionListener(e -> mettreAJourCarte());

        principal.add(form, BorderLayout.NORTH);
        principal.add(zoneSpecifique, BorderLayout.CENTER);
        principal.add(boutons, BorderLayout.SOUTH);
        setContentPane(principal);
    }

    private JPanel construireCarteMaison() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BLANC);
        int y = 0;
        ajouterLigne(p, y++, "Nombre d'étages :", tfNbEtages);
        ajouterLigne(p, y++, "Surface jardin (m²) :", tfSurfaceJardin);
        ajouterLigne(p, y++, "Nombre de garages :", tfNbGarages);
        return p;
    }

    private JPanel construireCarteAppartement() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BLANC);
        int y = 0;
        ajouterLigne(p, y++, "Étage :", tfEtage);
        ajouterLigne(p, y++, "", chkBalcon);
        ajouterLigne(p, y++, "", chkParking);
        ajouterLigne(p, y++, "", chkAscenseur);
        return p;
    }

    private JPanel construireCarteChambre() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(UITheme.BLANC);
        int y = 0;
        ajouterLigne(p, y++, "Étage :", tfEtageCh);
        ajouterLigne(p, y++, "", chkBalconCh);
        ajouterLigne(p, y++, "", chkParkingCh);
        ajouterLigne(p, y++, "", chkAscenseurCh);
        ajouterLigne(p, y++, "", chkSdbPrivee);
        ajouterLigne(p, y++, "", chkChargesIncluses);
        return p;
    }

    /**
     * Affiche la carte correspondant au type sélectionné et ajuste le champ
     * « nombre de pièces » (forcé à 1 et désactivé pour studio et chambre).
     */
    private void mettreAJourCarte() {
        TypeBien type = (TypeBien) cbType.getSelectedItem();
        CardLayout cl = (CardLayout) cartesSpecifiques.getLayout();
        String carte;
        switch (type) {
            case MAISON:
                carte = TypeBien.MAISON.name();
                break;
            case CHAMBRE:
                carte = TypeBien.CHAMBRE.name();
                break;
            default: // APPARTEMENT et STUDIO partagent la même carte.
                carte = TypeBien.APPARTEMENT.name();
                break;
        }
        cl.show(cartesSpecifiques, carte);

        boolean unePiece = (type == TypeBien.STUDIO || type == TypeBien.CHAMBRE);
        if (unePiece) {
            tfNbPieces.setText("1");
        }
        tfNbPieces.setEnabled(!unePiece);
    }

    /**
     * Lit le formulaire, valide les saisies et construit l'objet métier concret.
     */
    private void valider() {
        try {
            TypeBien type = (TypeBien) cbType.getSelectedItem();
            Ville ville = (Ville) cbVille.getSelectedItem();
            Proprietaire prop = (Proprietaire) cbProprietaire.getSelectedItem();
            if (prop == null) {
                throw new ValidationException("Aucun propriétaire disponible : créez d'abord un propriétaire.");
            }

            String quartier = tfQuartier.getText().trim();
            double surface = lireDouble(tfSurface.getText(), "surface");
            int nbChambres = lireInt(tfNbChambres.getText(), "nombre de chambres");
            int nbSdb = lireInt(tfNbSdb.getText(), "salles de bain");
            int annee = lireInt(tfAnnee.getText(), "année de construction");
            double prix = lireDouble(tfPrix.getText(), "prix par mois");
            boolean meuble = chkMeuble.isSelected();
            int idProp = prop.getIdUser();

            Bien bien;
            switch (type) {
                case MAISON:
                    bien = new Maison(surface, lireInt(tfNbPieces.getText(), "nombre de pièces"),
                            nbChambres, nbSdb, ville, quartier, annee, meuble, prix, idProp,
                            lireInt(tfNbEtages.getText(), "nombre d'étages"),
                            lireDouble(tfSurfaceJardin.getText(), "surface jardin"),
                            lireInt(tfNbGarages.getText(), "nombre de garages"));
                    break;
                case STUDIO:
                    bien = new Studio(surface, nbChambres, nbSdb, ville, quartier, annee, meuble, prix, idProp,
                            lireInt(tfEtage.getText(), "étage"),
                            chkBalcon.isSelected(), chkParking.isSelected(), chkAscenseur.isSelected());
                    break;
                case CHAMBRE:
                    bien = new Chambre(surface, nbSdb, ville, quartier, annee, meuble, prix, idProp,
                            lireInt(tfEtageCh.getText(), "étage"),
                            chkBalconCh.isSelected(), chkParkingCh.isSelected(), chkAscenseurCh.isSelected(),
                            chkSdbPrivee.isSelected(), chkChargesIncluses.isSelected());
                    break;
                case APPARTEMENT:
                default:
                    bien = new Appartement(surface, lireInt(tfNbPieces.getText(), "nombre de pièces"),
                            nbChambres, nbSdb, ville, quartier, annee, meuble, prix, idProp,
                            lireInt(tfEtage.getText(), "étage"),
                            chkBalcon.isSelected(), chkParking.isSelected(), chkAscenseur.isSelected());
                    break;
            }

            // Conserve l'identifiant et la disponibilité en cas de modification.
            if (bienExistant != null) {
                bien.setIdBien(bienExistant.getIdBien());
                bien.setDisponible(bienExistant.isDisponible());
            }

            this.resultat = bien;
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Pré-remplit le formulaire à partir d'un bien existant (mode édition).
     */
    private void preremplir(Bien b) {
        cbType.setSelectedItem(b.getTypeBien());
        cbType.setEnabled(false); // on ne change pas le type d'un bien existant
        cbVille.setSelectedItem(b.getVille());
        selectionnerProprietaire(b.getIdProprietaire());
        tfQuartier.setText(b.getQuartier());
        tfSurface.setText(String.valueOf(b.getSurface()));
        tfNbPieces.setText(String.valueOf(b.getNbPieces()));
        tfNbChambres.setText(String.valueOf(b.getNbChambres()));
        tfNbSdb.setText(String.valueOf(b.getNbSallesDeBain()));
        tfAnnee.setText(String.valueOf(b.getAnneeConstruction()));
        tfPrix.setText(String.valueOf(b.getPrixParMois()));
        chkMeuble.setSelected(b.isMeuble());

        if (b instanceof Maison) {
            Maison m = (Maison) b;
            tfNbEtages.setText(String.valueOf(m.getNbEtages()));
            tfSurfaceJardin.setText(String.valueOf(m.getSurfaceJardin()));
            tfNbGarages.setText(String.valueOf(m.getNbGarages()));
        } else if (b instanceof Chambre) {
            Chambre c = (Chambre) b;
            tfEtageCh.setText(String.valueOf(c.getEtage()));
            chkBalconCh.setSelected(c.isAvecBalcon());
            chkParkingCh.setSelected(c.isAvecParking());
            chkAscenseurCh.setSelected(c.isAvecAscenseur());
            chkSdbPrivee.setSelected(c.isSalleDeBainPrivee());
            chkChargesIncluses.setSelected(c.isChargesIncluses());
        } else if (b instanceof Appartement) { // couvre aussi Studio
            Appartement a = (Appartement) b;
            tfEtage.setText(String.valueOf(a.getEtage()));
            chkBalcon.setSelected(a.isAvecBalcon());
            chkParking.setSelected(a.isAvecParking());
            chkAscenseur.setSelected(a.isAvecAscenseur());
        }
    }

    private void selectionnerProprietaire(int idProp) {
        for (int i = 0; i < cbProprietaire.getItemCount(); i++) {
            if (cbProprietaire.getItemAt(i).getIdUser() == idProp) {
                cbProprietaire.setSelectedIndex(i);
                return;
            }
        }
    }

    // ----------------------------------------------------------------------
    // Helpers
    // ----------------------------------------------------------------------

    /** Ajoute une ligne « label + composant » à un panneau GridBagLayout. */
    private void ajouterLigne(JPanel panneau, int ligne, String label, java.awt.Component champ) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 4, 4, 4);
        g.gridy = ligne;
        g.gridx = 0;
        g.anchor = GridBagConstraints.WEST;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.NORMAL);
        panneau.add(lbl, g);

        g.gridx = 1;
        g.weightx = 1.0;
        g.fill = GridBagConstraints.HORIZONTAL;
        if (champ instanceof JTextField) {
            ((JTextField) champ).setColumns(16);
        }
        panneau.add(champ, g);
    }

    private int lireInt(String texte, String champ) {
        try {
            return Integer.parseInt(texte.trim());
        } catch (NumberFormatException e) {
            throw new ValidationException("Le champ « " + champ + " » doit être un entier valide.");
        }
    }

    private double lireDouble(String texte, String champ) {
        try {
            return Double.parseDouble(texte.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            throw new ValidationException("Le champ « " + champ + " » doit être un nombre valide.");
        }
    }

    /**
     * @return le bien construit, ou {@code null} si l'utilisateur a annulé
     */
    public Bien getBien() {
        return resultat;
    }

    /**
     * Filtre une liste d'utilisateurs pour n'en garder que les propriétaires.
     */
    public static List<Proprietaire> filtrerProprietaires(List<Utilisateur> utilisateurs) {
        return utilisateurs.stream()
                .filter(u -> u instanceof Proprietaire)
                .map(u -> (Proprietaire) u)
                .collect(Collectors.toList());
    }
}
