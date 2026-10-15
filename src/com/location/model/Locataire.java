package com.location.model;

import com.location.model.enums.TypeUtilisateur;

import java.util.ArrayList;
import java.util.List;

/**
 * Locataire : utilisateur qui recherche et réserve des biens (rôle client).
 *
 * <p>Conserve l'historique de ses réservations sous forme d'identifiants, pour
 * les mêmes raisons de cohérence relationnelle que {@link Proprietaire}.</p>
 */
public class Locataire extends Utilisateur {

    /** Identifiants des réservations effectuées par ce locataire. */
    private final List<Integer> idsReservations = new ArrayList<>();

    public Locataire() {
        super();
    }

    public Locataire(String nom, String prenom, String email, String telephone,
                     String motDePasseClair) {
        super(nom, prenom, email, telephone, motDePasseClair);
    }

    @Override
    public TypeUtilisateur getTypeUtilisateur() {
        return TypeUtilisateur.LOCATAIRE;
    }

    /**
     * Ajoute une réservation à l'historique du locataire.
     */
    public void ajouterReservation(int idReservation) {
        if (!idsReservations.contains(idReservation)) {
            idsReservations.add(idReservation);
        }
    }

    /**
     * @return copie défensive de l'historique des identifiants de réservations
     */
    public List<Integer> getIdsReservations() {
        return new ArrayList<>(idsReservations);
    }
}
