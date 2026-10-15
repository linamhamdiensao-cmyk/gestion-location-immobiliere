# 🏠 Application de Gestion des Locations Immobilières

Application de bureau **Java / Swing** permettant de gérer la location de biens
immobiliers (maisons, appartements, studios, chambres) : consultation, ajout,
modification, suppression et recherche d'annonces, gestion des utilisateurs,
réservations et historique.

Le projet est volontairement **autonome** : il ne dépend d'**aucune
bibliothèque externe** et s'importe directement dans **Visual Studio Code**
(avec l'extension *Extension Pack for Java*). Les données sont stockées en
mémoire (`ArrayList`) ; l'architecture est conçue pour brancher une base **SQL**
plus tard **sans réécrire** la logique métier ni l'interface.

---

## 1. Table des matières

1. [Démarrage rapide](#2-démarrage-rapide)
2. [Comptes de démonstration](#3-comptes-de-démonstration)
3. [Architecture (MVC + Services + DAO)](#4-architecture-mvc--services--dao)
4. [Arborescence du projet](#5-arborescence-du-projet)
5. [Explication de chaque classe](#6-explication-de-chaque-classe)
6. [Principes POO respectés](#7-principes-poo-respectés)
7. [Règles de gestion implémentées](#8-règles-de-gestion-implémentées)
8. [Migration future vers SQL](#9-migration-future-vers-sql)
9. [Pistes d'amélioration](#10-pistes-damélioration)

---

## 2. Démarrage rapide

### Option A — Eclipse (recommandé)

Le projet contient déjà les fichiers Eclipse (`.project`, `.classpath`,
`.settings/`) : aucune configuration manuelle n'est nécessaire.

1. Installer le **JDK 17+** (déjà fait si `java -version` répond 17+).
2. Dans Eclipse : **File → Import… → General → Existing Projects into
   Workspace**.
3. **Select root directory** → choisir le dossier `location-immobiliere` →
   cocher le projet → **Finish**.
4. **Lancer l'application** : clic droit sur
   `src/com/location/Main.java` → **Run As → Java Application**.
5. **Lancer les tests** : clic droit sur le dossier `test` (ou une classe
   `*Test.java`) → **Run As → JUnit Test**.

> Si Eclipse affiche une erreur de JRE (« unbound JRE »), faire clic droit sur le
> projet → **Build Path → Configure Build Path → Libraries**, sélectionner le
> *JRE System Library* et **Edit…** pour choisir un JDK 17 installé.

### Option B — Visual Studio Code

1. Installer le **JDK 17+** et l'extension **« Extension Pack for Java »**.
2. Ouvrir le dossier `location-immobiliere` (`Fichier → Ouvrir le dossier…`).
3. Attendre l'initialisation, puis ouvrir `src/com/location/Main.java` et
   cliquer sur **▶ Run** (ou `F5`).

### Option C — Ligne de commande (JDK seul)

```bash
# Depuis le dossier location-immobiliere
run.bat            # Windows : compile puis lance
```

ou manuellement :

```bash
javac -encoding UTF-8 -d bin $(find src -name "*.java")
java -cp bin com.location.Main
```

> ℹ️ L'application démarre sur un **écran de connexion**. Un **jeu de données de
> démonstration** est déjà chargé (4 utilisateurs, 4 biens, 1 réservation en
> attente) — utilisez l'un des comptes ci-dessous.

### Accès selon le rôle

L'interface s'**adapte au rôle** de l'utilisateur connecté :

| Fonction | Propriétaire (admin) | Locataire (client) |
|----------|:---:|:---:|
| Consulter les logements | ✅ | ✅ |
| Recherche multicritère | ✅ | ✅ |
| **Gérer les logements** (ajout/modif/suppr) | ✅ | ❌ |
| **Gérer les utilisateurs** | ✅ | ❌ |
| **Confirmer / Terminer** une réservation | ✅ | ❌ |
| Créer / Annuler ses réservations | ✅ | ✅ (les siennes) |
| Historique | tous les locataires | le sien uniquement |

Un bouton **Déconnexion** (en haut à droite) ramène à l'écran de connexion.

---

## 3. Comptes de démonstration

| Rôle          | Email                        | Mot de passe |
|---------------|------------------------------|--------------|
| Propriétaire  | `karim.proprio@loc.ma`       | `admin123`   |
| Propriétaire  | `salma.proprio@loc.ma`       | `admin123`   |
| Locataire     | `yassine.client@loc.ma`      | `client123`  |
| Locataire     | `imane.client@loc.ma`        | `client123`  |

> Les mots de passe ne sont **jamais stockés en clair** : ils sont hachés
> (SHA-256 + sel) par `PasswordUtil`.

---

## 4. Architecture (MVC + Services + DAO)

Le projet applique une séparation stricte des responsabilités en **5 couches** :

```
┌──────────────┐   appelle   ┌──────────────┐   appelle   ┌──────────────┐
│   VIEW       │ ──────────▶ │ CONTROLLER   │ ──────────▶ │  SERVICE     │
│ (Swing)      │             │ (MVC)        │             │ (métier)     │
└──────────────┘             └──────────────┘             └──────┬───────┘
        ▲                                                        │ utilise
        │ affiche                                                ▼
┌──────────────┐                                          ┌──────────────┐
│   MODEL      │ ◀─────────────────────────────────────── │   DAO        │
│ (entités)    │                  manipule                │ (Repository) │
└──────────────┘                                          └──────┬───────┘
                                                                 │ implémenté par
                                                                 ▼
                                                    ┌──────────────────────┐
                                                    │ InMemory*DAO (today)  │
                                                    │ Jdbc*DAO   (demain)   │
                                                    └──────────────────────┘
```

- **Model** : entités métier pures (héritage, encapsulation, polymorphisme).
- **DAO / Repository** : abstraction de la persistance. **Seule couche à
  remplacer** pour passer à SQL.
- **Service** : règles de gestion, validation, orchestration.
- **Controller** : fait le lien fin entre l'IHM et les services.
- **View** : fenêtres Swing (aucune logique métier).

> Le **point de composition** unique est `AppContext` : il câble
> DAO → Services → Contrôleurs. C'est le seul endroit où l'on choisit
> l'implémentation de persistance.

---

## 5. Arborescence du projet

```
location-immobiliere/
├── README.md                    ← ce fichier
├── run.bat                      ← compile + lance sans IDE
├── .gitignore
├── .vscode/
│   ├── settings.json            ← config projet Java (src, bin)
│   └── launch.json              ← profil de lancement « Main »
├── lib/                         ← (vide) futures librairies .jar (ex. driver JDBC)
├── sql/
│   └── schema.sql               ← schéma SQL prêt pour l'intégration BDD
├── docs/
│   ├── DIAGRAMME_UML.md         ← diagrammes de classes + état (PlantUML/Mermaid)
│   └── ARCHITECTURE.md          ← détail des couches et choix de conception
└── src/
    └── com/location/
        ├── Main.java                         ← point d'entrée
        ├── model/                            ← MODÈLE
        │   ├── Bien.java  (abstrait)
        │   ├── Maison.java
        │   ├── Appartement.java
        │   ├── Studio.java
        │   ├── Chambre.java
        │   ├── Utilisateur.java  (abstrait)
        │   ├── Proprietaire.java
        │   ├── Locataire.java
        │   ├── Reservation.java
        │   └── enums/
        │       ├── Ville.java
        │       ├── TypeBien.java
        │       ├── StatutReservation.java
        │       └── TypeUtilisateur.java
        ├── dao/                              ← PERSISTANCE (Repository)
        │   ├── Repository.java   (interface générique)
        │   ├── BienDAO.java
        │   ├── UtilisateurDAO.java
        │   ├── ReservationDAO.java
        │   └── memory/
        │       ├── InMemoryBienDAO.java
        │       ├── InMemoryUtilisateurDAO.java
        │       └── InMemoryReservationDAO.java
        ├── service/                          ← MÉTIER
        │   ├── BienService.java
        │   ├── UtilisateurService.java
        │   ├── ReservationService.java
        │   └── dto/
        │       └── FiltreRecherche.java
        ├── controller/                       ← CONTRÔLEURS (MVC)
        │   ├── AppContext.java   (composition root)
        │   ├── BienController.java
        │   ├── UtilisateurController.java
        │   └── ReservationController.java
        ├── view/                             ← INTERFACE GRAPHIQUE (Swing)
        │   ├── LoginFrame.java               ← écran de connexion
        │   ├── MainFrame.java                ← fenêtre principale (adaptée au rôle)
        │   ├── UITheme.java
        │   ├── components/
        │   │   └── LogoPanel.java
        │   ├── panels/
        │   │   ├── AccueilPanel.java
        │   │   ├── LogementsPanel.java
        │   │   ├── GestionLogementsPanel.java
        │   │   ├── RecherchePanel.java
        │   │   ├── ReservationPanel.java
        │   │   ├── HistoriquePanel.java
        │   │   └── UtilisateursPanel.java
        │   ├── dialog/
        │   │   ├── BienDialog.java
        │   │   └── UtilisateurDialog.java
        │   └── table/
        │       ├── BienTableModel.java
        │       ├── UtilisateurTableModel.java
        │       └── ReservationTableModel.java
        ├── exception/                        ← EXCEPTIONS MÉTIER
        │   ├── ValidationException.java
        │   ├── EntiteIntrouvableException.java
        │   └── ReservationException.java
        ├── util/                             ← UTILITAIRES
        │   ├── ValidationUtil.java
        │   └── PasswordUtil.java
        └── data/
            └── DonneesTest.java              ← jeu de données de démonstration

test/                                         ← TESTS UNITAIRES (JUnit 5)
└── com/location/
    ├── model/
    │   ├── ReservationTest.java
    │   └── StudioTest.java
    └── service/
        ├── BienServiceTest.java
        ├── UtilisateurServiceTest.java
        └── ReservationServiceTest.java
```

---

## 6. Explication de chaque classe

### Modèle (`model`)
| Classe | Rôle |
|--------|------|
| `Bien` *(abstraite)* | Racine des biens : attributs communs, méthodes abstraites `getTypeBien()` / `getDescriptionSpecifique()` (polymorphisme). |
| `Maison` | Bien avec étages, jardin, garages. |
| `Appartement` | Bien à un étage, options balcon/parking/ascenseur ; parent de `Studio`. |
| `Studio` | Appartement **forcé à 1 pièce** (invariant garanti dans le constructeur et le setter). |
| `Chambre` | Studio avec salle de bain privée et charges incluses. |
| `Utilisateur` *(abstraite)* | Racine des utilisateurs ; hachage du mot de passe, `seConnecter()`. |
| `Proprietaire` | Gère ses biens (rôle administrateur). |
| `Locataire` | Recherche et réserve (rôle client). |
| `Reservation` | Réservation d'un bien : calcule `nbMois` et `prixTotal`, transitions `confirmer/annuler/terminer`. |
| `enums/*` | Valeurs fermées et sûres : `Ville`, `TypeBien`, `StatutReservation`, `TypeUtilisateur`. |

### Persistance (`dao`)
| Classe | Rôle |
|--------|------|
| `Repository<T>` | Contrat générique CRUD. **Clé de l'extensibilité SQL.** |
| `BienDAO`, `UtilisateurDAO`, `ReservationDAO` | Contrats spécialisés (recherches métier). |
| `InMemory*DAO` | Implémentations sur `ArrayList` + `AtomicInteger` (simule l'AUTO_INCREMENT). |

### Métier (`service`)
| Classe | Rôle |
|--------|------|
| `BienService` | Validation de publication, recherche multicritère. |
| `UtilisateurService` | Inscription, unicité de l'e-mail, connexion. |
| `ReservationService` | Cycle de vie d'une réservation, gestion des conflits et de la disponibilité. |
| `FiltreRecherche` | DTO de critères de recherche (patron *Builder*). |

### Contrôleurs (`controller`)
| Classe | Rôle |
|--------|------|
| `AppContext` | **Composition root** : câble toutes les dépendances. |
| `*Controller` | Relais fin entre les vues Swing et les services. |

### Vue (`view`)
| Classe | Rôle |
|--------|------|
| `LoginFrame` | Écran de connexion (email + mot de passe), point d'entrée de l'IHM. |
| `MainFrame` | Fenêtre principale : en-tête (logo + identité + déconnexion) et onglets **filtrés selon le rôle**. |
| `AccueilPanel` | Page d'accueil (logo, présentation). |
| `LogementsPanel` | Consultation des annonces + détail. |
| `GestionLogementsPanel` | Tableau + Ajouter / Modifier / Supprimer. |
| `RecherchePanel` | Filtres ville / prix min / prix max / type. |
| `ReservationPanel` | Création de réservation + actions du cycle de vie. |
| `HistoriquePanel` | Historique des réservations par locataire. |
| `UtilisateursPanel` | Gestion des utilisateurs (CRUD). |
| `BienDialog` / `UtilisateurDialog` | Formulaires modaux dynamiques + validation. |
| `*TableModel` | Modèles de table typés (bonne pratique vs `DefaultTableModel`). |
| `UITheme`, `LogoPanel` | Identité visuelle (couleurs, polices, logo dessiné). |

### Utilitaires & exceptions
| Classe | Rôle |
|--------|------|
| `ValidationUtil` | Contrôles de saisie réutilisables (champs requis, e-mail, dates…). |
| `PasswordUtil` | Hachage et vérification de mot de passe (SHA-256 + sel). |
| `ValidationException`, `EntiteIntrouvableException`, `ReservationException` | Erreurs métier explicites. |

---

## 7. Principes POO respectés

- **Encapsulation** : tous les attributs sont `private`, accès par
  getters/setters ; les invariants sont protégés (ex. `Studio.nbPieces = 1`).
- **Héritage** : `Bien → Maison/Appartement → Studio → Chambre` et
  `Utilisateur → Proprietaire/Locataire`.
- **Polymorphisme** : méthodes abstraites `getTypeBien()` /
  `getDescriptionSpecifique()` redéfinies par chaque sous-classe ; les
  collections de `Bien` sont traitées uniformément.
- **Abstraction** : interfaces `Repository`, `*DAO` ; classes abstraites
  `Bien`, `Utilisateur`.
- **Faible couplage / forte cohésion** : injection de dépendances via les
  constructeurs, chaque couche ignore les détails de la couche inférieure.

---

## 8. Règles de gestion implémentées

| # | Règle | Où |
|---|-------|----|
| 1 | Un bien appartient à un seul propriétaire (`idProprietaire`). | `Bien` |
| 2 | Pas de réservation **confirmée** chevauchant une même période sur un bien. | `ReservationService` |
| 3 | Une réservation est créée par défaut **EN_ATTENTE**. | `Reservation` |
| 4 | Passage en **CONFIRME** seulement sur validation explicite. | `ReservationService.confirmer` |
| 5 | Le bien devient **indisponible** après confirmation. | `ReservationService.confirmer` |
| 6 | Le bien redevient **disponible** à la fin / annulation. | `ReservationService` |
| 7 | Une réservation annulée/terminée n'est plus modifiable. | `Reservation.estModifiable` |
| 8 | Un studio = appartement à **1 pièce**. | `Studio` |
| 9 | Champs obligatoires avant publication (ville, quartier, surface, type, prix). | `BienService.valider` |

---

## 9. Migration future vers SQL

L'architecture rend l'ajout de SQL **non intrusif**. Étapes :

1. Placer le driver JDBC (`mysql-connector-j.jar`) dans `lib/`.
2. Exécuter `sql/schema.sql` sur le SGBD.
3. Créer un package `com.location.dao.jdbc` avec `JdbcBienDAO`,
   `JdbcUtilisateurDAO`, `JdbcReservationDAO` implémentant les **mêmes
   interfaces** DAO (traduction des méthodes en requêtes SQL préparées).
4. Dans **`AppContext`**, remplacer :
   ```java
   this.bienDAO = new InMemoryBienDAO();
   // par
   this.bienDAO = new JdbcBienDAO(connexion);
   ```

➡️ **Aucune** modification dans `model`, `service`, `controller` ou `view`.
C'est tout l'intérêt du patron *Repository*.

Voir `docs/ARCHITECTURE.md` pour un exemple de squelette `JdbcBienDAO`.

---

## 10. Pistes d'amélioration

- Remplacer le hachage SHA-256 par **BCrypt/Argon2** (`PasswordUtil` est isolé).
- Internationalisation (i18n) via `ResourceBundle`.
- Export PDF/Excel des annonces et des réservations.
- Passer à **JavaFX** ou **Maven/Gradle** pour la gestion des dépendances.
- Pagination et tri serveur pour de grands volumes de données.
```
