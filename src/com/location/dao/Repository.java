package com.location.dao;

import java.util.List;
import java.util.Optional;

/**
 * Interface générique de persistance (patron <em>Repository / DAO</em>).
 *
 * <p>C'est la <strong>pièce maîtresse de l'extensibilité SQL</strong> : les
 * services ne dépendent que de cette abstraction, jamais d'une implémentation
 * concrète. Aujourd'hui les données vivent en mémoire
 * ({@code com.location.dao.memory}) ; demain, une implémentation JDBC
 * ({@code com.location.dao.jdbc}) pourra s'y substituer <em>sans toucher</em>
 * aux services, contrôleurs ni vues.</p>
 *
 * @param <T> type de l'entité gérée
 */
public interface Repository<T> {

    /**
     * Enregistre une entité. Si elle n'a pas encore d'identifiant, un nouvel
     * identifiant lui est attribué (insertion) ; sinon l'entité existante est
     * mise à jour.
     *
     * @param entite entité à sauvegarder
     * @return l'entité sauvegardée (avec son identifiant renseigné)
     */
    T sauvegarder(T entite);

    /**
     * Recherche une entité par son identifiant.
     *
     * @param id identifiant recherché
     * @return un {@link Optional} contenant l'entité, ou vide si absente
     */
    Optional<T> trouverParId(int id);

    /**
     * @return la liste de toutes les entités (copie, jamais {@code null})
     */
    List<T> trouverTout();

    /**
     * Supprime l'entité portant cet identifiant.
     *
     * @param id identifiant à supprimer
     * @return {@code true} si une entité a effectivement été supprimée
     */
    boolean supprimer(int id);

    /**
     * @param id identifiant à tester
     * @return {@code true} si une entité existe avec cet identifiant
     */
    boolean existe(int id);
}
