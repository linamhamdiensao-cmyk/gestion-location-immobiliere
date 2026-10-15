package com.location.util;

import com.location.exception.ValidationException;

import java.time.LocalDate;
import java.util.regex.Pattern;

/**
 * Boîte à outils statique de validation des saisies.
 *
 * <p>Centraliser la validation ici évite la duplication dans les services et
 * garantit des messages d'erreur homogènes. Chaque méthode lève une
 * {@link ValidationException} explicite en cas d'échec.</p>
 *
 * <p>Classe utilitaire : constructeur privé, uniquement des méthodes statiques.</p>
 */
public final class ValidationUtil {

    /** Expression régulière simple mais robuste pour valider un e-mail. */
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[\\w.+-]+@[\\w-]+\\.[\\w.-]+$");

    /** Téléphone : 8 à 15 chiffres, avec un éventuel '+' initial. */
    private static final Pattern TELEPHONE_PATTERN =
            Pattern.compile("^\\+?[0-9]{8,15}$");

    private ValidationUtil() {
        // Empêche l'instanciation d'une classe purement statique.
    }

    /**
     * Vérifie qu'une chaîne n'est ni nulle ni vide (après suppression des espaces).
     *
     * @param valeur valeur à contrôler
     * @param champ  nom du champ pour le message d'erreur
     */
    public static void exigerNonVide(String valeur, String champ) {
        if (valeur == null || valeur.trim().isEmpty()) {
            throw new ValidationException("Le champ « " + champ + " » est obligatoire.");
        }
    }

    /**
     * Vérifie qu'un objet n'est pas nul.
     */
    public static void exigerNonNull(Object valeur, String champ) {
        if (valeur == null) {
            throw new ValidationException("Le champ « " + champ + " » est obligatoire.");
        }
    }

    /**
     * Vérifie qu'un nombre est strictement positif (&gt; 0).
     */
    public static void exigerPositif(double valeur, String champ) {
        if (valeur <= 0) {
            throw new ValidationException("Le champ « " + champ + " » doit être strictement positif.");
        }
    }

    /**
     * Vérifie qu'un nombre est positif ou nul (&gt;= 0).
     */
    public static void exigerNonNegatif(double valeur, String champ) {
        if (valeur < 0) {
            throw new ValidationException("Le champ « " + champ + " » ne peut pas être négatif.");
        }
    }

    /**
     * Valide le format d'une adresse e-mail.
     */
    public static void exigerEmailValide(String email) {
        exigerNonVide(email, "email");
        if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("L'adresse e-mail « " + email + " » est invalide.");
        }
    }

    /**
     * Valide le format d'un numéro de téléphone.
     */
    public static void exigerTelephoneValide(String telephone) {
        exigerNonVide(telephone, "téléphone");
        if (!TELEPHONE_PATTERN.matcher(telephone.trim()).matches()) {
            throw new ValidationException("Le numéro de téléphone « " + telephone + " » est invalide.");
        }
    }

    /**
     * Vérifie la cohérence d'un intervalle de dates : les deux dates sont
     * présentes et la date de fin est strictement postérieure à la date de début.
     */
    public static void exigerIntervalleValide(LocalDate debut, LocalDate fin) {
        exigerNonNull(debut, "date de début");
        exigerNonNull(fin, "date de fin");
        if (!fin.isAfter(debut)) {
            throw new ValidationException("La date de fin doit être postérieure à la date de début.");
        }
    }
}
