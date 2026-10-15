package com.location.controller;

import com.location.model.Bien;
import com.location.service.BienService;
import com.location.service.dto.FiltreRecherche;

import java.util.List;

/**
 * Contrôleur des biens (au sens du patron MVC).
 *
 * <p>Le contrôleur fait l'<strong>intermédiaire entre la vue (Swing) et le
 * service métier</strong>. Il ne contient pas de logique métier : il se contente
 * de relayer les demandes de l'interface vers le service approprié. Cette couche
 * fine permet de faire évoluer indépendamment l'IHM et la logique métier.</p>
 */
public class BienController {

    private final BienService bienService;

    public BienController(BienService bienService) {
        this.bienService = bienService;
    }

    public Bien ajouter(Bien bien) {
        return bienService.ajouter(bien);
    }

    public Bien modifier(Bien bien) {
        return bienService.modifier(bien);
    }

    public void supprimer(int idBien) {
        bienService.supprimer(idBien);
    }

    public List<Bien> listerTout() {
        return bienService.listerTout();
    }

    public Bien trouver(int idBien) {
        return bienService.trouver(idBien);
    }

    public List<Bien> rechercher(FiltreRecherche filtre) {
        return bienService.rechercher(filtre);
    }
}
