-- ============================================================================
--  Schéma SQL de l'application de Location Immobilière
--  Traduction du MLD fourni. À utiliser lors de l'intégration future de la BDD.
--  SGBD cible : MySQL / MariaDB (adaptable à PostgreSQL).
--
--  Stratégie d'héritage retenue : "Class Table Inheritance" (une table par
--  classe), fidèle au MLD : une table parente `bien` / `utilisateur` et des
--  tables filles reliées par la clé primaire partagée.
-- ============================================================================

CREATE DATABASE IF NOT EXISTS location_immobiliere
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE location_immobiliere;

-- ----------------------------------------------------------------------------
-- Utilisateurs
-- ----------------------------------------------------------------------------
CREATE TABLE utilisateur (
    id_user           INT AUTO_INCREMENT PRIMARY KEY,
    nom               VARCHAR(80)  NOT NULL,
    prenom            VARCHAR(80)  NOT NULL,
    email             VARCHAR(150) NOT NULL UNIQUE,
    telephone         VARCHAR(20)  NOT NULL,
    mot_de_passe      VARCHAR(255) NOT NULL,           -- empreinte (hash), jamais en clair
    date_inscription  DATE         NOT NULL,
    type_user         ENUM('PROPRIETAIRE','LOCATAIRE') NOT NULL
);

CREATE TABLE proprietaire (
    id_user INT PRIMARY KEY,
    CONSTRAINT fk_proprietaire_user FOREIGN KEY (id_user)
        REFERENCES utilisateur(id_user) ON DELETE CASCADE
);

CREATE TABLE locataire (
    id_user INT PRIMARY KEY,
    CONSTRAINT fk_locataire_user FOREIGN KEY (id_user)
        REFERENCES utilisateur(id_user) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Biens (table parente) + tables filles
-- ----------------------------------------------------------------------------
CREATE TABLE bien (
    id_bien            INT AUTO_INCREMENT PRIMARY KEY,
    id_user            INT NOT NULL,                    -- propriétaire
    surface            DOUBLE NOT NULL,
    nb_pieces          INT NOT NULL,
    nb_chambres        INT NOT NULL,
    nb_salles_de_bain  INT NOT NULL,
    ville              ENUM('CASABLANCA','RABAT','NADOR','OUJDA') NOT NULL,
    quartier           VARCHAR(120) NOT NULL,
    annee_construction INT NOT NULL,
    meuble             BOOLEAN NOT NULL DEFAULT FALSE,
    prix_par_mois      DOUBLE NOT NULL,
    disponible         BOOLEAN NOT NULL DEFAULT TRUE,
    type_bien          ENUM('MAISON','APPARTEMENT','STUDIO','CHAMBRE') NOT NULL,
    CONSTRAINT fk_bien_proprietaire FOREIGN KEY (id_user)
        REFERENCES proprietaire(id_user)
);

CREATE TABLE maison (
    id_bien        INT PRIMARY KEY,
    nb_etages      INT NOT NULL,
    surface_jardin DOUBLE NOT NULL DEFAULT 0,
    nb_garages     INT NOT NULL DEFAULT 0,
    CONSTRAINT fk_maison_bien FOREIGN KEY (id_bien)
        REFERENCES bien(id_bien) ON DELETE CASCADE
);

CREATE TABLE appartement (
    id_bien        INT PRIMARY KEY,
    etage          INT NOT NULL,
    avec_balcon    BOOLEAN NOT NULL DEFAULT FALSE,
    avec_parking   BOOLEAN NOT NULL DEFAULT FALSE,
    avec_ascenseur BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_appartement_bien FOREIGN KEY (id_bien)
        REFERENCES bien(id_bien) ON DELETE CASCADE
);

-- Studio : spécialisation d'appartement (nb_pieces = 1). Pas d'attribut propre.
CREATE TABLE studio (
    id_bien INT PRIMARY KEY,
    CONSTRAINT fk_studio_appartement FOREIGN KEY (id_bien)
        REFERENCES appartement(id_bien) ON DELETE CASCADE
);

CREATE TABLE chambre (
    id_bien             INT PRIMARY KEY,
    salle_de_bain_privee BOOLEAN NOT NULL DEFAULT FALSE,
    charges_incluses     BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_chambre_studio FOREIGN KEY (id_bien)
        REFERENCES studio(id_bien) ON DELETE CASCADE
);

-- ----------------------------------------------------------------------------
-- Réservations
-- ----------------------------------------------------------------------------
CREATE TABLE reservation (
    id_res           INT AUTO_INCREMENT PRIMARY KEY,
    id_user          INT NOT NULL,                      -- locataire
    id_bien          INT NOT NULL,
    date_debut       DATE NOT NULL,
    date_fin         DATE NOT NULL,
    date_reservation DATE NOT NULL,
    statut           ENUM('EN_ATTENTE','CONFIRME','ANNULE','TERMINE')
                         NOT NULL DEFAULT 'EN_ATTENTE',
    CONSTRAINT fk_reservation_locataire FOREIGN KEY (id_user)
        REFERENCES locataire(id_user),
    CONSTRAINT fk_reservation_bien FOREIGN KEY (id_bien)
        REFERENCES bien(id_bien),
    CONSTRAINT chk_dates CHECK (date_fin > date_debut)
);

-- Index utiles aux recherches fréquentes.
CREATE INDEX idx_bien_ville  ON bien(ville);
CREATE INDEX idx_bien_prix   ON bien(prix_par_mois);
CREATE INDEX idx_bien_type   ON bien(type_bien);
CREATE INDEX idx_res_bien    ON reservation(id_bien);
CREATE INDEX idx_res_user    ON reservation(id_user);
