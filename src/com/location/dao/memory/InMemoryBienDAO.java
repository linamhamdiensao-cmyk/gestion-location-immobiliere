package com.location.dao.memory;

import com.location.dao.BienDAO;
import com.location.model.Bien;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implémentation <strong>en mémoire</strong> de {@link BienDAO}, fondée sur une
 * {@link ArrayList}.
 *
 * <p>Conforme à la contrainte « pas de base de données pour l'instant ». Les
 * identifiants sont générés par un {@link AtomicInteger} (équivalent d'une
 * colonne AUTO_INCREMENT). Pour passer à SQL, il suffira de créer une classe
 * {@code JdbcBienDAO} implémentant la même interface.</p>
 */
public class InMemoryBienDAO implements BienDAO {

    /** Stockage temporaire des biens en mémoire vive. */
    private final List<Bien> biens = new ArrayList<>();

    /** Séquence d'identifiants (simule l'AUTO_INCREMENT SQL). */
    private final AtomicInteger sequence = new AtomicInteger(0);

    @Override
    public Bien sauvegarder(Bien bien) {
        if (bien.getIdBien() == 0) {
            // Insertion : nouvel identifiant.
            bien.setIdBien(sequence.incrementAndGet());
            biens.add(bien);
        } else {
            // Mise à jour : on remplace l'élément existant.
            supprimer(bien.getIdBien());
            biens.add(bien);
            // On conserve la séquence cohérente si l'id vient d'un jeu de test.
            sequence.updateAndGet(courant -> Math.max(courant, bien.getIdBien()));
        }
        return bien;
    }

    @Override
    public Optional<Bien> trouverParId(int id) {
        return biens.stream().filter(b -> b.getIdBien() == id).findFirst();
    }

    @Override
    public List<Bien> trouverTout() {
        return new ArrayList<>(biens);
    }

    @Override
    public boolean supprimer(int id) {
        return biens.removeIf(b -> b.getIdBien() == id);
    }

    @Override
    public boolean existe(int id) {
        return biens.stream().anyMatch(b -> b.getIdBien() == id);
    }

    @Override
    public List<Bien> trouverParProprietaire(int idProprietaire) {
        return biens.stream()
                .filter(b -> b.getIdProprietaire() == idProprietaire)
                .collect(Collectors.toList());
    }

    @Override
    public List<Bien> trouverDisponibles() {
        return biens.stream()
                .filter(Bien::isDisponible)
                .collect(Collectors.toList());
    }
}
