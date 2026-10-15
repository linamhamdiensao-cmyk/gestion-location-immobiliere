# Architecture détaillée & choix de conception

Ce document complète le `README.md` en explicitant les choix d'architecture et
en fournissant un **modèle concret de migration vers SQL**.

---

## 1. Vue d'ensemble en couches

| Couche | Package | Dépend de | Ne connaît PAS |
|--------|---------|-----------|----------------|
| Vue | `view` | `controller` | les DAO, le SQL |
| Contrôleur | `controller` | `service` | Swing, les DAO concrets |
| Service | `service` | `dao` (interfaces) | Swing, l'implémentation SQL/mémoire |
| DAO | `dao` | `model` | le métier, Swing |
| Modèle | `model` | — (rien) | tout le reste |

> **Règle de dépendance** : les flèches vont toujours du haut vers le bas. Une
> couche ne dépend jamais d'une couche supérieure, ce qui garantit que l'on peut
> remplacer la persistance ou l'IHM indépendamment.

---

## 2. Pourquoi le patron Repository / DAO ?

Les services ne manipulent que des **interfaces** (`BienDAO`, `UtilisateurDAO`,
`ReservationDAO`). L'implémentation concrète est choisie **une seule fois** dans
`AppContext`. Conséquence directe :

- Aujourd'hui : `InMemoryBienDAO` (stockage `ArrayList`).
- Demain : `JdbcBienDAO` (stockage SQL).

Le remplacement n'impacte **aucune** autre classe.

---

## 3. Exemple de squelette `JdbcBienDAO`

Voici à quoi ressemblerait l'implémentation SQL, à placer dans
`com.location.dao.jdbc`. Elle implémente la **même interface** `BienDAO`.

```java
package com.location.dao.jdbc;

import com.location.dao.BienDAO;
import com.location.model.Bien;
import com.location.model.Maison;
// ... autres imports

import java.sql.*;
import java.util.*;

public class JdbcBienDAO implements BienDAO {

    private final Connection cx;

    public JdbcBienDAO(Connection cx) {
        this.cx = cx;
    }

    @Override
    public Bien sauvegarder(Bien bien) {
        // INSERT dans `bien` (récupération de l'id généré) puis INSERT dans
        // la table fille correspondante (maison / appartement / studio / chambre).
        // En cas de mise à jour : UPDATE des deux tables.
        // ... PreparedStatement, transaction (cx.setAutoCommit(false))
        return bien;
    }

    @Override
    public Optional<Bien> trouverParId(int id) {
        String sql = "SELECT * FROM bien WHERE id_bien = ?";
        try (PreparedStatement ps = cx.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapper(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur SQL trouverParId", e);
        }
    }

    @Override
    public List<Bien> trouverTout() {
        // SELECT joignant `bien` et ses tables filles, puis mapping.
        return new ArrayList<>();
    }

    @Override public boolean supprimer(int id) { /* DELETE ... */ return false; }
    @Override public boolean existe(int id)   { /* SELECT 1 ... */ return false; }

    @Override
    public List<Bien> trouverParProprietaire(int idProprietaire) {
        // SELECT ... WHERE id_user = ?
        return new ArrayList<>();
    }

    @Override
    public List<Bien> trouverDisponibles() {
        // SELECT ... WHERE disponible = TRUE
        return new ArrayList<>();
    }

    /** Reconstruit l'objet métier concret à partir d'une ligne SQL. */
    private Bien mapper(ResultSet rs) throws SQLException {
        String type = rs.getString("type_bien");
        // switch (type) -> instancier Maison / Appartement / Studio / Chambre
        // en lisant aussi la table fille correspondante.
        return new Maison(/* ... */);
    }
}
```

Puis, dans `AppContext` :

```java
Connection cx = DriverManager.getConnection(
        "jdbc:mysql://localhost:3306/location_immobiliere", "root", "motdepasse");
this.bienDAO = new JdbcBienDAO(cx);
this.utilisateurDAO = new JdbcUtilisateurDAO(cx);
this.reservationDAO = new JdbcReservationDAO(cx);
```

---

## 4. Gestion des erreurs

| Exception | Quand | Traitée par |
|-----------|-------|-------------|
| `ValidationException` | saisie invalide (champ requis, e-mail, prix…) | les vues affichent un `JOptionPane` d'avertissement |
| `EntiteIntrouvableException` | id inexistant | vues / services |
| `ReservationException` | conflit de réservation, dates, bien indisponible | `ReservationPanel` |

Toutes héritent de `RuntimeException` : les vues interceptent un seul type
(`RuntimeException`) et affichent `ex.getMessage()`, ce qui garde le code des
panneaux concis tout en restant robuste.

---

## 5. Thread de l'interface (EDT)

Conformément aux bonnes pratiques Swing, la construction et l'affichage de
l'interface se font sur l'*Event Dispatch Thread* via
`SwingUtilities.invokeLater(...)` dans `Main`. Les traitements métier sont
synchrones et rapides (données en mémoire) ; lors du passage à SQL, on pourra
déporter les requêtes longues dans un `SwingWorker` pour ne pas geler l'IHM.

---

## 6. Synthèse des patrons utilisés

- **MVC** : Model / View / Controller.
- **Repository (DAO)** : abstraction de la persistance.
- **DTO** : `FiltreRecherche` transporte les critères de recherche.
- **Builder** (fluide) : construction lisible de `FiltreRecherche`.
- **Composition Root** : `AppContext` centralise l'injection de dépendances.
- **Template Method / Polymorphisme** : `Bien.getDescriptionSpecifique()`.
