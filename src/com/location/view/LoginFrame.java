package com.location.view;

import com.location.controller.AppContext;
import com.location.model.Utilisateur;
import com.location.view.components.LogoPanel;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.function.Consumer;

/**
 * Fenêtre de <strong>connexion</strong>. Première fenêtre affichée au lancement.
 *
 * <p>Authentifie l'utilisateur via le service ; en cas de succès, elle invoque
 * le callback {@code onConnexion} avec l'utilisateur connecté (ce qui déclenche
 * l'ouverture de la fenêtre principale adaptée à son rôle), puis se ferme.</p>
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    private final transient AppContext ctx;
    private final transient Consumer<Utilisateur> onConnexion;

    private final JTextField tfEmail = new JTextField(20);
    private final JPasswordField pfMotDePasse = new JPasswordField(20);
    private final JLabel lblErreur = new JLabel(" ", SwingConstants.CENTER);

    /**
     * @param ctx         contexte applicatif
     * @param onConnexion action exécutée avec l'utilisateur une fois connecté
     */
    public LoginFrame(AppContext ctx, Consumer<Utilisateur> onConnexion) {
        super("Connexion - Location Immobilière");
        this.ctx = ctx;
        this.onConnexion = onConnexion;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        construireInterface();
        pack();
        setMinimumSize(new Dimension(420, getHeight()));
        setLocationRelativeTo(null);
    }

    private void construireInterface() {
        JPanel racine = new JPanel(new BorderLayout(0, 10));
        racine.setBackground(UITheme.BLANC);
        racine.setBorder(BorderFactory.createEmptyBorder(24, 30, 24, 30));

        racine.add(construireEntete(), BorderLayout.NORTH);
        racine.add(construireFormulaire(), BorderLayout.CENTER);
        racine.add(construirePiedDePage(), BorderLayout.SOUTH);

        setContentPane(racine);
        getRootPane().setDefaultButton(boutonConnexion);
    }

    private JPanel construireEntete() {
        JPanel entete = new JPanel();
        entete.setLayout(new javax.swing.BoxLayout(entete, javax.swing.BoxLayout.Y_AXIS));
        entete.setBackground(UITheme.BLANC);

        LogoPanel logo = new LogoPanel(80);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titre = new JLabel("Location Immobilière");
        titre.setFont(UITheme.TITRE);
        titre.setForeground(UITheme.PRIMAIRE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sousTitre = new JLabel("Veuillez vous connecter");
        sousTitre.setFont(UITheme.NORMAL);
        sousTitre.setForeground(UITheme.ACCENT);
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        entete.add(logo);
        entete.add(javax.swing.Box.createVerticalStrut(10));
        entete.add(titre);
        entete.add(javax.swing.Box.createVerticalStrut(4));
        entete.add(sousTitre);
        return entete;
    }

    private final JButton boutonConnexion = new JButton("Se connecter");

    private JPanel construireFormulaire() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BLANC);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(6, 6, 6, 6);
        g.anchor = GridBagConstraints.WEST;
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx = 0; g.gridy = 0; form.add(new JLabel("Email :"), g);
        g.gridx = 1; g.weightx = 1; form.add(tfEmail, g);

        g.gridx = 0; g.gridy = 1; g.weightx = 0; form.add(new JLabel("Mot de passe :"), g);
        g.gridx = 1; g.weightx = 1; form.add(pfMotDePasse, g);

        boutonConnexion.setBackground(UITheme.ACCENT);
        boutonConnexion.setForeground(UITheme.BLANC);
        boutonConnexion.setFont(UITheme.GRAS);
        boutonConnexion.setFocusPainted(false);
        boutonConnexion.addActionListener(e -> tenterConnexion());

        g.gridx = 0; g.gridy = 2; g.gridwidth = 2; g.fill = GridBagConstraints.HORIZONTAL;
        form.add(boutonConnexion, g);

        lblErreur.setForeground(UITheme.DANGER);
        lblErreur.setFont(UITheme.NORMAL);
        g.gridy = 3;
        form.add(lblErreur, g);

        return form;
    }

    private JPanel construirePiedDePage() {
        JPanel pied = new JPanel(new BorderLayout());
        pied.setBackground(new Color(248, 249, 250));
        pied.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(222, 226, 230)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));

        JLabel aide = new JLabel("<html><b>Comptes de démonstration</b><br>"
                + "Propriétaire : karim.proprio@loc.ma / admin123<br>"
                + "Locataire : yassine.client@loc.ma / client123</html>");
        aide.setFont(new java.awt.Font("Segoe UI", java.awt.Font.PLAIN, 11));
        aide.setForeground(new Color(90, 98, 104));
        pied.add(aide, BorderLayout.CENTER);
        return pied;
    }

    private void tenterConnexion() {
        String email = tfEmail.getText().trim();
        String motDePasse = new String(pfMotDePasse.getPassword());
        try {
            Utilisateur utilisateur = ctx.getUtilisateurController().seConnecter(email, motDePasse);
            // Connexion réussie : on ferme l'écran de login et on poursuit.
            dispose();
            onConnexion.accept(utilisateur);
        } catch (RuntimeException ex) {
            lblErreur.setText(ex.getMessage());
            pfMotDePasse.setText("");
        }
    }
}
