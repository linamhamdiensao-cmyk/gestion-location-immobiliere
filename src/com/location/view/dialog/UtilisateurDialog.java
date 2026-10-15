package com.location.view.dialog;

import com.location.exception.ValidationException;
import com.location.model.Locataire;
import com.location.model.Proprietaire;
import com.location.model.Utilisateur;
import com.location.model.enums.TypeUtilisateur;
import com.location.view.UITheme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

/**
 * Boîte de dialogue modale d'ajout / modification d'un {@link Utilisateur}.
 *
 * <p>En mode édition, le mot de passe laissé vide conserve l'ancien. Le résultat
 * est récupérable via {@link #getUtilisateur()} ({@code null} si annulation).</p>
 */
public class UtilisateurDialog extends JDialog {

    private static final long serialVersionUID = 1L;

    private final JComboBox<TypeUtilisateur> cbType = new JComboBox<>(TypeUtilisateur.values());
    private final JTextField tfNom = new JTextField();
    private final JTextField tfPrenom = new JTextField();
    private final JTextField tfEmail = new JTextField();
    private final JTextField tfTelephone = new JTextField();
    private final JPasswordField pfMotDePasse = new JPasswordField();

    private final Utilisateur utilisateurExistant;
    private Utilisateur resultat;

    public UtilisateurDialog(Frame parent, Utilisateur aEditer) {
        super(parent, aEditer == null ? "Ajouter un utilisateur" : "Modifier l'utilisateur", true);
        this.utilisateurExistant = aEditer;

        construireInterface();
        if (aEditer != null) {
            preremplir(aEditer);
        }

        pack();
        setMinimumSize(new Dimension(400, getHeight()));
        setLocationRelativeTo(parent);
    }

    private void construireInterface() {
        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        principal.setBackground(UITheme.BLANC);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BLANC);
        int y = 0;
        ajouterLigne(form, y++, "Type :", cbType);
        ajouterLigne(form, y++, "Nom :", tfNom);
        ajouterLigne(form, y++, "Prénom :", tfPrenom);
        ajouterLigne(form, y++, "Email :", tfEmail);
        ajouterLigne(form, y++, "Téléphone :", tfTelephone);
        ajouterLigne(form, y++, "Mot de passe :", pfMotDePasse);

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

        principal.add(form, BorderLayout.CENTER);
        principal.add(boutons, BorderLayout.SOUTH);
        setContentPane(principal);
    }

    private void valider() {
        try {
            TypeUtilisateur type = (TypeUtilisateur) cbType.getSelectedItem();
            String nom = tfNom.getText().trim();
            String prenom = tfPrenom.getText().trim();
            String email = tfEmail.getText().trim();
            String tel = tfTelephone.getText().trim();
            String mdp = new String(pfMotDePasse.getPassword());

            if (nom.isEmpty() || prenom.isEmpty()) {
                throw new ValidationException("Le nom et le prénom sont obligatoires.");
            }
            if (utilisateurExistant == null && mdp.isEmpty()) {
                throw new ValidationException("Le mot de passe est obligatoire pour un nouvel utilisateur.");
            }

            // Construction de l'objet concret selon le type choisi.
            Utilisateur u;
            if (type == TypeUtilisateur.PROPRIETAIRE) {
                u = new Proprietaire();
            } else {
                u = new Locataire();
            }
            u.setNom(nom);
            u.setPrenom(prenom);
            u.setEmail(email);
            u.setTelephone(tel);

            if (utilisateurExistant != null) {
                u.setIdUser(utilisateurExistant.getIdUser());
                u.setDateInscription(utilisateurExistant.getDateInscription());
                if (mdp.isEmpty()) {
                    // Conserve l'ancien mot de passe haché.
                    u.setMotDePasseHache(utilisateurExistant.getMotDePasseHache());
                } else {
                    u.setMotDePasse(mdp);
                }
            } else {
                u.setMotDePasse(mdp);
            }

            this.resultat = u;
            dispose();
        } catch (ValidationException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(),
                    "Saisie invalide", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void preremplir(Utilisateur u) {
        cbType.setSelectedItem(u.getTypeUtilisateur());
        cbType.setEnabled(false); // le type ne se change pas après création
        tfNom.setText(u.getNom());
        tfPrenom.setText(u.getPrenom());
        tfEmail.setText(u.getEmail());
        tfTelephone.setText(u.getTelephone());
        pfMotDePasse.setToolTipText("Laisser vide pour conserver le mot de passe actuel.");
    }

    private void ajouterLigne(JPanel panneau, int ligne, String label, java.awt.Component champ) {
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 4, 5, 4);
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
            ((JTextField) champ).setColumns(18);
        }
        panneau.add(champ, g);
    }

    public Utilisateur getUtilisateur() {
        return resultat;
    }
}
