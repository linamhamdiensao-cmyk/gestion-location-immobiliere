package com.location.view.components;

import com.location.view.UITheme;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Logo de l'application dessiné par programme (aucune ressource image externe
 * nécessaire). Représente une maison stylisée dans un cercle, aux couleurs du
 * thème.
 */
public class LogoPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private final int taille;

    public LogoPanel(int taille) {
        this.taille = taille;
        setOpaque(false);
        setPreferredSize(new Dimension(taille, taille));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int marge = 4;
        int d = taille - 2 * marge;

        // Cercle de fond.
        g2.setColor(UITheme.ACCENT);
        g2.fillOval(marge, marge, d, d);

        // Toit (triangle).
        int cx = taille / 2;
        int toitBase = (int) (taille * 0.58);
        int toitHaut = (int) (taille * 0.30);
        int demiToit = (int) (d * 0.32);
        g2.setColor(Color.WHITE);
        int[] xs = {cx - demiToit, cx + demiToit, cx};
        int[] ys = {toitBase, toitBase, toitHaut};
        g2.fillPolygon(xs, ys, 3);

        // Corps de la maison (rectangle).
        int corpsLarg = (int) (d * 0.40);
        int corpsHaut = (int) (d * 0.26);
        g2.fillRect(cx - corpsLarg / 2, toitBase, corpsLarg, corpsHaut);

        // Porte.
        g2.setColor(UITheme.ACCENT);
        int porteLarg = corpsLarg / 4;
        int porteHaut = corpsHaut / 2;
        g2.fillRect(cx - porteLarg / 2, toitBase + corpsHaut - porteHaut, porteLarg, porteHaut);

        g2.dispose();
    }
}
