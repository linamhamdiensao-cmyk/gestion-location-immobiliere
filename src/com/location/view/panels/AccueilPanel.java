package com.location.view.panels;

import com.location.view.UITheme;
import com.location.view.components.LogoPanel;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.Dimension;

/**
 * Page d'<strong>accueil</strong> : logo, titre de l'application et rappel des
 * fonctionnalités principales.
 */
public class AccueilPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    public AccueilPanel() {
        setBackground(UITheme.FOND);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        LogoPanel logo = new LogoPanel(120);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel titre = new JLabel("Gestion des Locations Immobilières");
        titre.setFont(UITheme.TITRE);
        titre.setForeground(UITheme.PRIMAIRE);
        titre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sousTitre = new JLabel("Maisons · Appartements · Studios · Chambres");
        sousTitre.setFont(UITheme.SOUS_TITRE);
        sousTitre.setForeground(UITheme.ACCENT);
        sousTitre.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel(
                "<html><div style='text-align:center; width:520px;'>"
                + "Application de démonstration en Java (Swing) suivant une architecture "
                + "<b>MVC + Services + DAO</b>. Les données sont actuellement stockées en mémoire ; "
                + "la couche DAO permet de brancher une base de données SQL sans modifier le reste "
                + "de l'application.<br><br>"
                + "Utilisez la barre d'onglets ci-dessus pour naviguer entre la consultation des "
                + "logements, leur gestion, la recherche, les réservations, l'historique et la "
                + "gestion des utilisateurs."
                + "</div></html>");
        description.setFont(UITheme.NORMAL);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        add(Box.createVerticalGlue());
        add(logo);
        add(Box.createRigidArea(new Dimension(0, 20)));
        add(titre);
        add(Box.createRigidArea(new Dimension(0, 8)));
        add(sousTitre);
        add(Box.createRigidArea(new Dimension(0, 24)));
        add(description);
        add(Box.createVerticalGlue());
    }
}
