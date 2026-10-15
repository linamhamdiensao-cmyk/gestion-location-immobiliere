package com.location.model;

import com.location.model.enums.TypeBien;
import com.location.model.enums.Ville;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests unitaires des invariants d'héritage (encapsulation).
 */
@DisplayName("Studio / Chambre - invariant nb_pieces = 1")
class StudioTest {

    @Test
    @DisplayName("Un studio a toujours 1 pièce, même si on tente de la changer")
    void studioToujoursUnePiece() {
        Studio studio = new Studio(35, 1, 1, Ville.RABAT, "Centre", 2020,
                true, 3000, 1, 2, false, false, true);

        assertEquals(1, studio.getNbPieces());

        // Tentative de modification : doit rester à 1 (invariant protégé).
        studio.setNbPieces(5);
        assertEquals(1, studio.getNbPieces(), "Le studio doit rester à 1 pièce");
    }

    @Test
    @DisplayName("Le type d'un studio est bien STUDIO (polymorphisme)")
    void typeStudio() {
        Studio studio = new Studio(35, 1, 1, Ville.RABAT, "Centre", 2020,
                true, 3000, 1, 2, false, false, true);
        assertEquals(TypeBien.STUDIO, studio.getTypeBien());
    }

    @Test
    @DisplayName("Une chambre hérite de l'invariant 1 pièce et de son type")
    void chambreUnePieceEtType() {
        Chambre chambre = new Chambre(18, 1, Ville.NADOR, "Oliviers", 2021,
                true, 1500, 2, 0, false, false, false, true, true);

        assertEquals(1, chambre.getNbPieces());
        assertEquals(TypeBien.CHAMBRE, chambre.getTypeBien());
    }
}
