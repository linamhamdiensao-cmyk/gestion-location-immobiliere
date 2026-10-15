package com.location.dao.memory;

import com.location.dao.UtilisateurDAO;
import com.location.model.Utilisateur;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implémentation en mémoire de {@link UtilisateurDAO}.
 */
public class InMemoryUtilisateurDAO implements UtilisateurDAO {

    private final List<Utilisateur> utilisateurs = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    @Override
    public Utilisateur sauvegarder(Utilisateur utilisateur) {
        if (utilisateur.getIdUser() == 0) {
            utilisateur.setIdUser(sequence.incrementAndGet());
            utilisateurs.add(utilisateur);
        } else {
            supprimer(utilisateur.getIdUser());
            utilisateurs.add(utilisateur);
            sequence.updateAndGet(courant -> Math.max(courant, utilisateur.getIdUser()));
        }
        return utilisateur;
    }

    @Override
    public Optional<Utilisateur> trouverParId(int id) {
        return utilisateurs.stream().filter(u -> u.getIdUser() == id).findFirst();
    }

    @Override
    public List<Utilisateur> trouverTout() {
        return new ArrayList<>(utilisateurs);
    }

    @Override
    public boolean supprimer(int id) {
        return utilisateurs.removeIf(u -> u.getIdUser() == id);
    }

    @Override
    public boolean existe(int id) {
        return utilisateurs.stream().anyMatch(u -> u.getIdUser() == id);
    }

    @Override
    public Optional<Utilisateur> trouverParEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return utilisateurs.stream()
                .filter(u -> email.equalsIgnoreCase(u.getEmail()))
                .findFirst();
    }
}
