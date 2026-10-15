package com.location.model;

import com.location.model.enums.TypeUtilisateur;

import java.util.ArrayList;
import java.util.List;

/**
 * Propriétaire : utilisateur qui publie et gère ses biens (rôle administrateur).
 *
 * <p>Conserve la liste des identifiants de ses biens. On stocke des
 * <strong>identifiants</strong> plutôt que les objets {@link Bien} eux-mêmes
 * pour rester cohérent avec le futur modèle relationnel (clé étrangère) et
 * éviter les références croisées difficiles à persister.</p>
 */
public class Proprietaire extends Utilisateur {

    /** Identifiants des biens possédés par ce propriétaire. */
    private final List<Integer> idsBiens = new ArrayList<>();

    public Proprietaire() {
        super();
    }

    public Proprietaire(String nom, String prenom, String email, String telephone,
                        String motDePasseClair) {
        super(nom, prenom, email, telephone, motDePasseClair);
    }

    @Override
    public TypeUtilisateur getTypeUtilisateur() {
        return TypeUtilisateur.PROPRIETAIRE;
    }

    /**
     * Associe un bien à ce propriétaire (sans doublon).
     */
    public void ajouterBien(int idBien) {
        if (!idsBiens.contains(idBien)) {
            idsBiens.add(idBien);
        }
    }

    /**
     * Dissocie un bien de ce propriétaire.
     */
    public void retirerBien(int idBien) {
        idsBiens.remove(Integer.valueOf(idBien));
    }

    /**
     * @return copie défensive de la liste des identifiants de biens
     */
    public List<Integer> getIdsBiens() {
        return new ArrayList<>(idsBiens);
    }
}
