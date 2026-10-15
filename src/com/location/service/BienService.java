package com.location.service;

import com.location.dao.BienDAO;
import com.location.exception.EntiteIntrouvableException;
import com.location.exception.ValidationException;
import com.location.model.Bien;
import com.location.service.dto.FiltreRecherche;
import com.location.util.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service métier des biens : validation, règles de gestion et recherche.
 *
 * <p>Le service <strong>ne connaît pas</strong> la manière dont les données sont
 * stockées : il dialogue uniquement avec l'interface {@link BienDAO}. C'est ce
 * découplage qui permettra de brancher SQL plus tard sans rien changer ici.</p>
 */
public class BienService {

    private final BienDAO bienDAO;

    /**
     * Injection de dépendance par le constructeur : le DAO est fourni de
     * l'extérieur, ce qui facilite les tests et le remplacement de
     * l'implémentation (mémoire → SQL).
     */
    public BienService(BienDAO bienDAO) {
        this.bienDAO = bienDAO;
    }

    /**
     * Ajoute (publie) un nouveau bien après validation des champs obligatoires.
     *
     * <p>Règle de gestion : « Chaque bien doit obligatoirement renseigner la
     * ville, le quartier, la surface, le type de bien et le prix mensuel avant
     * d'être publié. »</p>
     *
     * @param bien bien à publier
     * @return le bien enregistré, identifiant renseigné
     */
    public Bien ajouter(Bien bien) {
        valider(bien);
        return bienDAO.sauvegarder(bien);
    }

    /**
     * Met à jour un bien existant.
     *
     * @throws EntiteIntrouvableException si le bien n'existe pas
     */
    public Bien modifier(Bien bien) {
        if (!bienDAO.existe(bien.getIdBien())) {
            throw new EntiteIntrouvableException("Bien", bien.getIdBien());
        }
        valider(bien);
        return bienDAO.sauvegarder(bien);
    }

    /**
     * Supprime un bien par son identifiant.
     *
     * @throws EntiteIntrouvableException si le bien n'existe pas
     */
    public void supprimer(int idBien) {
        if (!bienDAO.supprimer(idBien)) {
            throw new EntiteIntrouvableException("Bien", idBien);
        }
    }

    /**
     * @return tous les biens, triés par identifiant
     */
    public List<Bien> listerTout() {
        return bienDAO.trouverTout();
    }

    /**
     * Récupère un bien ou lève une exception s'il est absent.
     */
    public Bien trouver(int idBien) {
        return bienDAO.trouverParId(idBien)
                .orElseThrow(() -> new EntiteIntrouvableException("Bien", idBien));
    }

    public List<Bien> listerParProprietaire(int idProprietaire) {
        return bienDAO.trouverParProprietaire(idProprietaire);
    }

    /**
     * Recherche multicritère. Les critères {@code null} sont ignorés et les
     * critères renseignés sont combinés par un ET logique.
     *
     * @param filtre critères de recherche (voir {@link FiltreRecherche})
     * @return la liste des biens correspondants
     */
    public List<Bien> rechercher(FiltreRecherche filtre) {
        if (filtre == null) {
            return listerTout();
        }
        // Cohérence des bornes de prix.
        if (filtre.getPrixMin() != null && filtre.getPrixMax() != null
                && filtre.getPrixMin() > filtre.getPrixMax()) {
            throw new ValidationException("Le prix minimum ne peut pas dépasser le prix maximum.");
        }

        return bienDAO.trouverTout().stream()
                .filter(b -> !filtre.isUniquementDisponibles() || b.isDisponible())
                .filter(b -> filtre.getVille() == null || b.getVille() == filtre.getVille())
                .filter(b -> filtre.getTypeBien() == null || b.getTypeBien() == filtre.getTypeBien())
                .filter(b -> filtre.getPrixMin() == null || b.getPrixParMois() >= filtre.getPrixMin())
                .filter(b -> filtre.getPrixMax() == null || b.getPrixParMois() <= filtre.getPrixMax())
                .collect(Collectors.toList());
    }

    /**
     * Valide les champs obligatoires d'un bien (règle de publication).
     *
     * @throws ValidationException si un champ obligatoire est manquant ou invalide
     */
    private void valider(Bien bien) {
        ValidationUtil.exigerNonNull(bien, "bien");
        ValidationUtil.exigerNonNull(bien.getVille(), "ville");
        ValidationUtil.exigerNonVide(bien.getQuartier(), "quartier");
        ValidationUtil.exigerNonNull(bien.getTypeBien(), "type de bien");
        ValidationUtil.exigerPositif(bien.getSurface(), "surface");
        ValidationUtil.exigerPositif(bien.getPrixParMois(), "prix par mois");
        ValidationUtil.exigerNonNegatif(bien.getNbPieces(), "nombre de pièces");
        ValidationUtil.exigerNonNegatif(bien.getNbChambres(), "nombre de chambres");
        ValidationUtil.exigerNonNegatif(bien.getNbSallesDeBain(), "nombre de salles de bain");
    }
}
