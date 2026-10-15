package com.location.exception;

/**
 * Levée lorsqu'une entité (bien, utilisateur, réservation) recherchée par son
 * identifiant n'existe pas dans la source de données.
 *
 * <p>Permet aux services de signaler proprement une référence invalide sans
 * retourner {@code null}, ce qui rend le code appelant plus robuste.</p>
 */
public class EntiteIntrouvableException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntiteIntrouvableException(String message) {
        super(message);
    }

    /**
     * Fabrique un message standardisé du type
     * « Bien introuvable (id = 12) ».
     *
     * @param typeEntite libellé de l'entité (ex. "Bien", "Utilisateur")
     * @param id         identifiant recherché
     */
    public EntiteIntrouvableException(String typeEntite, int id) {
        super(typeEntite + " introuvable (id = " + id + ")");
    }
}
