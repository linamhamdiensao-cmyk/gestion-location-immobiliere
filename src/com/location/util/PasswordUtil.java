package com.location.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utilitaire de hachage de mot de passe.
 *
 * <p>L'architecture cible mentionne BCrypt. Afin de garder le projet
 * <strong>autonome (aucune dépendance externe)</strong> et directement
 * importable dans VS Code sans Maven ni accès Internet, on fournit ici une
 * implémentation pédagogique à base de <em>SHA-256 + sel aléatoire</em>,
 * fournie nativement par la JDK ({@code java.security}).</p>
 *
 * <p>Le mot de passe stocké a la forme {@code sel:hash} (tous deux encodés en
 * Base64). Le sel garantit que deux utilisateurs ayant le même mot de passe
 * obtiennent des empreintes différentes.</p>
 *
 * <p><strong>Évolution recommandée :</strong> remplacer le corps de
 * {@link #hacher(String)} et {@link #verifier(String, String)} par l'appel à la
 * librairie {@code jBCrypt} (ou {@code Argon2}) sans changer la signature, donc
 * sans impacter le reste de l'application.</p>
 */
public final class PasswordUtil {

    private static final String ALGORITHME = "SHA-256";
    private static final int TAILLE_SEL = 16; // 128 bits
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
    }

    /**
     * Hache un mot de passe en clair avec un sel aléatoire.
     *
     * @param motDePasseClair mot de passe saisi par l'utilisateur
     * @return chaîne stockable de la forme {@code selBase64:hashBase64}
     */
    public static String hacher(String motDePasseClair) {
        byte[] sel = new byte[TAILLE_SEL];
        RANDOM.nextBytes(sel);
        byte[] hash = calculer(motDePasseClair, sel);
        return encode(sel) + ":" + encode(hash);
    }

    /**
     * Vérifie qu'un mot de passe en clair correspond à une empreinte stockée.
     *
     * @param motDePasseClair mot de passe saisi à la connexion
     * @param empreinteStockee valeur précédemment produite par {@link #hacher(String)}
     * @return {@code true} si le mot de passe est correct
     */
    public static boolean verifier(String motDePasseClair, String empreinteStockee) {
        if (empreinteStockee == null || !empreinteStockee.contains(":")) {
            return false;
        }
        String[] parties = empreinteStockee.split(":", 2);
        byte[] sel = decode(parties[0]);
        byte[] hashAttendu = decode(parties[1]);
        byte[] hashCalcule = calculer(motDePasseClair, sel);
        // Comparaison à temps constant pour éviter les attaques temporelles.
        return MessageDigest.isEqual(hashAttendu, hashCalcule);
    }

    private static byte[] calculer(String motDePasse, byte[] sel) {
        try {
            MessageDigest digest = MessageDigest.getInstance(ALGORITHME);
            digest.update(sel);
            return digest.digest(motDePasse.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            // SHA-256 est garanti présent sur toute JVM : ne devrait jamais arriver.
            throw new IllegalStateException("Algorithme de hachage indisponible : " + ALGORITHME, e);
        }
    }

    private static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    private static byte[] decode(String data) {
        return Base64.getDecoder().decode(data);
    }
}
