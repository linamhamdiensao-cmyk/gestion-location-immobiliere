package com.location.view;

import java.awt.Color;
import java.awt.Font;

/**
 * Constantes graphiques partagées (couleurs et polices) afin d'assurer une
 * identité visuelle homogène et professionnelle sur toutes les fenêtres.
 */
public final class UITheme {

    private UITheme() {
    }

    // --- Palette de couleurs ---
    /** Bleu nuit principal (barre de navigation, en-tête). */
    public static final Color PRIMAIRE = new Color(33, 47, 61);
    /** Bleu accent (boutons d'action, sélection). */
    public static final Color ACCENT = new Color(41, 128, 185);
    /** Vert succès. */
    public static final Color SUCCES = new Color(39, 174, 96);
    /** Rouge danger (suppression). */
    public static final Color DANGER = new Color(192, 57, 43);
    /** Fond clair des panneaux. */
    public static final Color FOND = new Color(245, 247, 250);
    /** Blanc des cartes / tableaux. */
    public static final Color BLANC = Color.WHITE;
    /** Texte clair sur fond sombre. */
    public static final Color TEXTE_CLAIR = new Color(236, 240, 241);

    // --- Polices ---
    public static final Font TITRE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font SOUS_TITRE = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font GRAS = new Font("Segoe UI", Font.BOLD, 13);
}
