DROP DATABASE IF EXISTS fourrage;
CREATE DATABASE fourrage;
USE fourrage;

CREATE TABLE region (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL
);

CREATE TABLE district (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    region_id BIGINT NOT NULL,
    CONSTRAINT fk_district_region
        FOREIGN KEY (region_id) REFERENCES region(id)
);

CREATE TABLE commune (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    district_id BIGINT NOT NULL,
    CONSTRAINT fk_commune_district
        FOREIGN KEY (district_id) REFERENCES district(id)
);

CREATE TABLE client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    contact VARCHAR(255) NOT NULL,
    adresse VARCHAR(255)
);

CREATE TABLE status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL   /* Cree,DEC(devis Etude Cree),DEA(devis Etude Acceptee),DER(devis Etude Refusee),DFC(devis Forage Cree) ,DFA (devis Forage Acceptee) , DFR(devis Forage Refusee),TC(Travaille Commence),TT(Travaille Termine) */
);



CREATE TABLE demande (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    commune_id BIGINT NOT NULL,
    adresse VARCHAR(255),
    client_id BIGINT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_demande_commune
        FOREIGN KEY (commune_id) REFERENCES commune(id),

    CONSTRAINT fk_demande_client
        FOREIGN KEY (client_id) REFERENCES client(id)
);

CREATE TABLE type_devis (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL  /*ex: "Etude", "Forage" */
);

CREATE TABLE devis (    
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    observation VARCHAR(255) NOT NULL,
    demande_id BIGINT NOT NULL,
    date_creation DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    type_id BIGINT NOT NULL,

    CONSTRAINT fk_devis_demande
        FOREIGN KEY (demande_id) REFERENCES demande(id),

    CONSTRAINT fk_devis_type
        FOREIGN KEY (type_id) REFERENCES type_devis(id)
);

CREATE TABLE devisDetail(
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    qte INT NOT NULL,
    prix_unitaire DOUBLE NOT NULL,
    devis_id BIGINT NOT NULL,

    CONSTRAINT fk_devisDetail_devis
        FOREIGN KEY (devis_id) REFERENCES devis(id)
);

CREATE TABLE demande_status (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    demande_id BIGINT NOT NULL,
    status_id BIGINT NOT NULL,
    date_changement DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    observation VARCHAR(255),
    duree_travail INT,
    CONSTRAINT fk_demande_status_demande
        FOREIGN KEY (demande_id) REFERENCES demande(id),
    CONSTRAINT fk_demande_status_status
        FOREIGN KEY (status_id) REFERENCES status(id)
);


CREATE TABLE parametre (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(255) NOT NULL,
    duree_minimum INT NOT NULL,
    couleur VARCHAR(255) NOT NULL,
    status_id_depart BIGINT NOT NULL,
    status_id_arrivee BIGINT NOT NULL,
    CONSTRAINT fk_parametre_status_depart
        FOREIGN KEY (status_id_depart) REFERENCES status(id)
);

-- =========================
-- 1. REGIONS
-- =========================
INSERT INTO region (libelle) VALUES
('Analamanga'),
('Vakinankaratra'),
('Atsinanana');

-- =========================
-- 2. DISTRICTS
-- =========================
INSERT INTO district (libelle, region_id) VALUES
('Antananarivo Atsimondrano', 1),
('Antananarivo Avaradrano', 1),
('Antsirabe I', 2),
('Toamasina I', 3);

-- =========================
-- 3. COMMUNES
-- =========================
INSERT INTO commune (libelle, district_id) VALUES
('Tanjombato', 1),
('Andoharanofotsy', 1),
('Sabotsy Namehana', 2),
('Antsirabe Ville', 3),
('Toamasina Ville', 4);

-- =========================
-- 4. CLIENTS
-- =========================
INSERT INTO client (nom, contact, adresse) VALUES
('Rakoto Jean', '0341234567', 'Tanjombato'),
('Rasoa Marie', '0337654321', 'Andoharanofotsy'),
('Entreprise Mada Forage', '0321112233', 'Antananarivo'),
('Commune Sabotsy Namehana', '0349988776', 'Sabotsy Namehana');

-- =========================
-- 5. STATUS
-- =========================
INSERT INTO status (libelle) VALUES
('Cree'),
('DEC'),
('DEA'),
('DER'),
('DFC'),
('DFA'),
('DFR'),
('TC'),
('TT');

-- =========================
-- 6. TYPE DEVIS
-- =========================
INSERT INTO type_devis (libelle) VALUES
('Etude'),
('Forage');


DELETE FROM parametre;

INSERT INTO parametre (libelle, duree_minimum, couleur, status_id_depart, status_id_arrivee) VALUES
('Cree -> DEC', 2, '#3498DB', 1, 2),
('DEC -> DEA', 5, '#F1C40F', 2, 3),
('DEC -> DER', 5,  '#E74C3C', 2, 4),
('DEA -> DFC',3, '#2ECC71', 3, 5),
('DFC -> DFA', 7, '#9B59B6', 5, 6),
('DFC -> DFR', 7, '#34495E', 5, 7),
('DFA -> TC', 3, '#E67E22', 6, 8),
('TC -> TT',  3, '#1ABC9C', 8, 9);
