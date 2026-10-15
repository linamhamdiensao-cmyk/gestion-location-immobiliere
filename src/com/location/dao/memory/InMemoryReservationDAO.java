package com.location.dao.memory;

import com.location.dao.ReservationDAO;
import com.location.model.Reservation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Implémentation en mémoire de {@link ReservationDAO}.
 */
public class InMemoryReservationDAO implements ReservationDAO {

    private final List<Reservation> reservations = new ArrayList<>();
    private final AtomicInteger sequence = new AtomicInteger(0);

    @Override
    public Reservation sauvegarder(Reservation reservation) {
        if (reservation.getIdReservation() == 0) {
            reservation.setIdReservation(sequence.incrementAndGet());
            reservations.add(reservation);
        } else {
            supprimer(reservation.getIdReservation());
            reservations.add(reservation);
            sequence.updateAndGet(courant -> Math.max(courant, reservation.getIdReservation()));
        }
        return reservation;
    }

    @Override
    public Optional<Reservation> trouverParId(int id) {
        return reservations.stream().filter(r -> r.getIdReservation() == id).findFirst();
    }

    @Override
    public List<Reservation> trouverTout() {
        return new ArrayList<>(reservations);
    }

    @Override
    public boolean supprimer(int id) {
        return reservations.removeIf(r -> r.getIdReservation() == id);
    }

    @Override
    public boolean existe(int id) {
        return reservations.stream().anyMatch(r -> r.getIdReservation() == id);
    }

    @Override
    public List<Reservation> trouverParLocataire(int idLocataire) {
        return reservations.stream()
                .filter(r -> r.getIdLocataire() == idLocataire)
                .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> trouverParBien(int idBien) {
        return reservations.stream()
                .filter(r -> r.getIdBien() == idBien)
                .collect(Collectors.toList());
    }
}
