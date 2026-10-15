package com.location.model;

import com.location.model.enums.TypeUtilisateur;
import com.location.util.PasswordUtil;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Classe <strong>abstraite</strong> représentant un utilisateur de l'application.
 *
 * <p>Deux spécialisations existent :</p>
 * <ul>
 *   <li>{@link Proprietaire} : gère des biens (rôle administrateur).</li>
 *   <li>{@link Locataire} : recherche et réserve des biens (rôle client).</li>
 * </ul>
 *
 * <p>Le mot de passe n'est jamais stocké en clair : il est haché via
 * {@link PasswordUtil}. Le setter {@link #setMotDePasse(String)} attend un mot
 * de passe en clair et le hache automatiquement.</p>
 */
public abstract class Utilisateur {

    private int idUser;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;

    /** Empreinte du mot de passe (jamais le mot de passe en clair). */
    private String motDePasseHache;

    private LocalDate dateInscription;

    protected Utilisateur() {
        this.dateInscription = LocalDate.now();
    }

    /**
     * Constructeur commun. Le mot de passe fourni en clair est immédiatement haché.
     */
    protected Utilisateur(String nom, String prenom, String email, String telephone,
                          String motDePasseClair) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.motDePasseHache = PasswordUtil.hacher(motDePasseClair);
        this.dateInscription = LocalDate.now();
    }

    // ----------------------------------------------------------------------
    // Comportement métier
    // ----------------------------------------------------------------------

    /**
     * Rôle de l'utilisateur. Implémenté par chaque sous-classe (polymorphisme).
     */
    public abstract TypeUtilisateur getTypeUtilisateur();

    /**
     * Tente d'authentifier l'utilisateur.
     *
     * @param email         e-mail saisi
     * @param motDePasse    mot de passe en clair saisi
     * @return {@code true} si les identifiants correspondent
     */
    public boolean seConnecter(String email, String motDePasse) {
        return this.email != null
                && this.email.equalsIgnoreCase(email)
                && PasswordUtil.verifier(motDePasse, this.motDePasseHache);
    }

    /**
     * Met à jour les informations de profil modifiables.
     */
    public void modifierProfil(String nom, String prenom, String telephone) {
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    /** Nom complet « Prénom NOM » pour l'affichage. */
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    // ----------------------------------------------------------------------
    // Getters / Setters
    // ----------------------------------------------------------------------

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getMotDePasseHache() {
        return motDePasseHache;
    }

    /**
     * Définit le mot de passe à partir d'une valeur <strong>en clair</strong>,
     * qui est immédiatement hachée avant stockage.
     */
    public void setMotDePasse(String motDePasseClair) {
        this.motDePasseHache = PasswordUtil.hacher(motDePasseClair);
    }

    /**
     * Permet d'injecter directement une empreinte déjà hachée (utile lors d'un
     * futur chargement depuis la base de données).
     */
    public void setMotDePasseHache(String motDePasseHache) {
        this.motDePasseHache = motDePasseHache;
    }

    public LocalDate getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(LocalDate dateInscription) {
        this.dateInscription = dateInscription;
    }

    // ----------------------------------------------------------------------
    // Méthodes utilitaires
    // ----------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Utilisateur)) {
            return false;
        }
        return idUser == ((Utilisateur) o).idUser;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUser);
    }

    @Override
    public String toString() {
        return String.format("[#%d] %s - %s (%s)",
                idUser, getNomComplet(), email, getTypeUtilisateur().getLibelle());
    }
}
