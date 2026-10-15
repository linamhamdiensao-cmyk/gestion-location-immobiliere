package com.location.exception;

/**
 * Levée lorsqu'une donnée saisie ne respecte pas les règles de validation
 * (champ obligatoire manquant, valeur négative, e-mail mal formé, etc.).
 *
 * <p>C'est une exception <em>non vérifiée</em> ({@link RuntimeException}) :
 * elle signale une erreur de saisie utilisateur, généralement interceptée au
 * niveau du contrôleur pour afficher un message clair dans l'interface.</p>
 */
public class ValidationException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
