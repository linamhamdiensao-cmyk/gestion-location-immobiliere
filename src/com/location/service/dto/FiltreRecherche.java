package com.location.service.dto;

import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;

/**
 * Objet de transfert ({@code DTO}) regroupant les critères de recherche d'un bien.
 *
 * <p>Tous les critères sont <strong>optionnels</strong> : un champ à {@code null}
 * signifie « ne pas filtrer sur ce critère ». Le {@code BienService} combine les
 * critères renseignés avec un ET logique.</p>
 *
 * <p>Le patron <em>Builder</em> (méthodes fluides {@code avecXxx}) rend la
 * construction lisible côté interface graphique :</p>
 * <pre>{@code
 * FiltreRecherche f = new FiltreRecherche()
 *         .avecVille(Ville.RABAT)
 *         .avecPrixMax(5000);
 * }</pre>
 */
public class FiltreRecherche {

    private Ville ville;
    private Double prixMin;
    private Double prixMax;
    private TypeBien typeBien;

    /** Si {@code true}, ne retourne que les biens disponibles (défaut : true). */
    private boolean uniquementDisponibles = true;

    public Ville getVille() {
        return ville;
    }

    public FiltreRecherche avecVille(Ville ville) {
        this.ville = ville;
        return this;
    }

    public Double getPrixMin() {
        return prixMin;
    }

    public FiltreRecherche avecPrixMin(Double prixMin) {
        this.prixMin = prixMin;
        return this;
    }

    public Double getPrixMax() {
        return prixMax;
    }

    public FiltreRecherche avecPrixMax(Double prixMax) {
        this.prixMax = prixMax;
        return this;
    }

    public TypeBien getTypeBien() {
        return typeBien;
    }

    public FiltreRecherche avecTypeBien(TypeBien typeBien) {
        this.typeBien = typeBien;
        return this;
    }

    public boolean isUniquementDisponibles() {
        return uniquementDisponibles;
    }

    public FiltreRecherche avecUniquementDisponibles(boolean uniquementDisponibles) {
        this.uniquementDisponibles = uniquementDisponibles;
        return this;
    }
}
