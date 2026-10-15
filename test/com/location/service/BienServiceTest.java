package com.location.service;

import com.location.dao.BienDAO;
import com.location.dao.memory.InMemoryBienDAO;
import com.location.exception.EntiteIntrouvableException;
import com.location.exception.ValidationException;
import com.location.model.Appartement;
import com.location.model.Bien;
import com.location.model.Maison;
import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;
import com.location.service.dto.FiltreRecherche;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests unitaires de {@link BienService} : validation à la publication et
 * recherche multicritère.
 */
@DisplayName("BienService - validation et recherche")
class BienServiceTest {

    private BienService service;

    @BeforeEach
    void initialiser() {
        // Un DAO en mémoire neuf avant chaque test (isolation des tests).
        BienDAO dao = new InMemoryBienDAO();
        service = new BienService(dao);
    }

    private Maison maisonValide() {
        return new Maison(120, 5, 3, 2, Ville.RABAT, "Agdal", 2015,
                true, 8000, 1, 2, 100, 1);
    }

    @Test
    @DisplayName("Ajouter un bien valide lui attribue un identifiant")
    void ajoutBienValide() {
        Bien enregistre = service.ajouter(maisonValide());
        assertTrue(enregistre.getIdBien() > 0, "Un id doit être généré");
        assertEquals(1, service.listerTout().size());
    }

    @Test
    @DisplayName("Un bien sans quartier est refusé (champ obligatoire)")
    void refusBienSansQuartier() {
        Maison m = maisonValide();
        m.setQuartier("   "); // vide après trim
        assertThrows(ValidationException.class, () -> service.ajouter(m));
    }

    @Test
    @DisplayName("Un bien avec un prix négatif est refusé")
    void refusPrixNegatif() {
        Maison m = maisonValide();
        m.setPrixParMois(-100);
        assertThrows(ValidationException.class, () -> service.ajouter(m));
    }

    @Test
    @DisplayName("Modifier un bien inexistant lève EntiteIntrouvableException")
    void modifierBienInexistant() {
        Maison m = maisonValide();
        m.setIdBien(999);
        assertThrows(EntiteIntrouvableException.class, () -> service.modifier(m));
    }

    @Test
    @DisplayName("La recherche combine ville, type et prix maximum")
    void rechercheMulticritere() {
        // 2 biens à Rabat, 1 à Casablanca.
        service.ajouter(maisonValide()); // Rabat, 8000, MAISON
        service.ajouter(new Appartement(80, 3, 2, 1, Ville.RABAT, "Hassan", 2018,
                true, 4000, 1, 2, true, false, true)); // Rabat, 4000, APPARTEMENT
        service.ajouter(new Appartement(90, 3, 2, 1, Ville.CASABLANCA, "Maârif", 2019,
                true, 6000, 1, 4, true, true, true)); // Casa

        FiltreRecherche filtre = new FiltreRecherche()
                .avecVille(Ville.RABAT)
                .avecTypeBien(TypeBien.APPARTEMENT)
                .avecPrixMax(5000.0);

        List<Bien> resultats = service.rechercher(filtre);
        assertEquals(1, resultats.size());
        assertEquals(Ville.RABAT, resultats.get(0).getVille());
        assertEquals(TypeBien.APPARTEMENT, resultats.get(0).getTypeBien());
    }

    @Test
    @DisplayName("Un prix min supérieur au prix max est rejeté")
    void bornesPrixIncoherentes() {
        FiltreRecherche filtre = new FiltreRecherche()
                .avecPrixMin(9000.0)
                .avecPrixMax(1000.0);
        assertThrows(ValidationException.class, () -> service.rechercher(filtre));
    }
}
