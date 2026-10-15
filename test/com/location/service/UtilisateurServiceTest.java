package com.location.service;

import com.location.dao.UtilisateurDAO;
import com.location.dao.memory.InMemoryUtilisateurDAO;
import com.location.exception.ValidationException;
import com.location.model.Locataire;
import com.location.model.Proprietaire;
import com.location.model.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests unitaires de {@link UtilisateurService} : unicité de l'e-mail,
 * validation et connexion.
 */
@DisplayName("UtilisateurService - inscription et connexion")
class UtilisateurServiceTest {

    private UtilisateurService service;

    @BeforeEach
    void initialiser() {
        UtilisateurDAO dao = new InMemoryUtilisateurDAO();
        service = new UtilisateurService(dao);
    }

    @Test
    @DisplayName("Inscription d'un utilisateur valide")
    void inscriptionValide() {
        Utilisateur u = service.ajouter(
                new Locataire("Tazi", "Yassine", "y@loc.ma", "+212600000000", "pw12345"));
        assertEquals(1, u.getIdUser());
    }

    @Test
    @DisplayName("Deux comptes ne peuvent pas partager le même e-mail")
    void emailUnique() {
        service.ajouter(new Locataire("Tazi", "Yassine", "y@loc.ma", "+212600000000", "pw"));
        assertThrows(ValidationException.class, () ->
                service.ajouter(new Proprietaire("Autre", "Nom", "y@loc.ma", "+212611111111", "pw")));
    }

    @Test
    @DisplayName("Un e-mail mal formé est refusé")
    void emailInvalide() {
        assertThrows(ValidationException.class, () ->
                service.ajouter(new Locataire("Tazi", "Yassine", "pas-un-email", "+212600000000", "pw")));
    }

    @Test
    @DisplayName("Connexion réussie avec les bons identifiants")
    void connexionReussie() {
        service.ajouter(new Proprietaire("El Amrani", "Karim", "k@loc.ma", "+212600000001", "admin123"));
        Utilisateur connecte = service.seConnecter("k@loc.ma", "admin123");
        assertEquals("Karim El Amrani", connecte.getNomComplet());
    }

    @Test
    @DisplayName("Connexion refusée avec un mauvais mot de passe")
    void connexionMauvaisMotDePasse() {
        service.ajouter(new Proprietaire("El Amrani", "Karim", "k@loc.ma", "+212600000001", "admin123"));
        assertThrows(ValidationException.class, () -> service.seConnecter("k@loc.ma", "mauvais"));
    }
}
