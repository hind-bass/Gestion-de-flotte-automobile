CREATE DATABASE IF NOT EXISTS gestion_carburant;
USE gestion_carburant;

-- Table des Rôles (Admin, Utilisateur)
CREATE TABLE Role (
    id_role INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL UNIQUE
);

-- Table des Utilisateurs
CREATE TABLE Utilisateur (
    id_utilisateur INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    mot_de_passe VARCHAR(255) NOT NULL, -- Stocker un hash, pas le mot de passe en clair !
    id_role INT,
    FOREIGN KEY (id_role) REFERENCES Role(id_role)
);

-- Table des Types de Carburant
CREATE TABLE Carburant (
    id_carburant INT PRIMARY KEY AUTO_INCREMENT,
    nom_carburant VARCHAR(50) NOT NULL UNIQUE
);

-- Table des Véhicules
CREATE TABLE Vehicule (
    matricule VARCHAR(20) PRIMARY KEY,
    marque VARCHAR(50) NOT NULL,
    modele VARCHAR(50) NOT NULL,
    kilometrage_initial INT DEFAULT 0,
    kilometrage_actuel INT NOT NULL,
    date_mise_service DATE,
    conso_theorique DECIMAL(5, 2) COMMENT 'Consommation en L/100km',
    id_carburant INT,
    FOREIGN KEY (id_carburant) REFERENCES Carburant(id_carburant)
);

-- Table des Cartes de Carburant
CREATE TABLE CarteCarburant (
    id_carte INT PRIMARY KEY AUTO_INCREMENT,
    numero_carte VARCHAR(50) NOT NULL UNIQUE,
    titulaire VARCHAR(100),
    plafond DECIMAL(10, 2),
    statut ENUM('active', 'inactive') NOT NULL DEFAULT 'active'
);

-- Table des Ravitaillements
CREATE TABLE Ravitaillement (
    id_ravitaillement INT PRIMARY KEY AUTO_INCREMENT,
    date_ravitaillement DATETIME NOT NULL,
    lieu VARCHAR(255),
    quantite DECIMAL(8, 2) NOT NULL,
    prix_unitaire DECIMAL(6, 3) NOT NULL,
    montant_total DECIMAL(10, 2) NOT NULL,
    kilometrage_releve INT NOT NULL,
    vehicule_matricule VARCHAR(20),
    id_utilisateur INT,
    id_carte INT NULL, -- Une carte n'est pas obligatoire
    conducteur VARCHAR(100),
    FOREIGN KEY (vehicule_matricule) REFERENCES Vehicule(matricule),
    FOREIGN KEY (id_utilisateur) REFERENCES Utilisateur(id_utilisateur),
    FOREIGN KEY (id_carte) REFERENCES CarteCarburant(id_carte)
);

-- Table pour l'historique des prix du carburant
CREATE TABLE PrixCarburant (
    id_prix INT PRIMARY KEY AUTO_INCREMENT,
    date_prix DATE NOT NULL,
    prix DECIMAL(6, 3) NOT NULL,
    id_carburant INT,
    FOREIGN KEY (id_carburant) REFERENCES Carburant(id_carburant)
);

-- Insertion de données initiales
INSERT INTO Role (nom) VALUES ('ADMIN');

use gestion_carburant;
 -- Ajout d'utilisateurs de test
INSERT INTO Utilisateur (nom, login, mot_de_passe, id_role) VALUES 
('Admin Principal', 'admin', 'admin123', 1);

-- Ajout de cartes carburant
INSERT INTO CarteCarburant (numero_carte, titulaire, plafond, statut) VALUES 
('CARTE001', 'Flotte Entreprise', 5000.00, 'active'),
('CARTE002', 'Service Commercial', 3000.00, 'active'),
('CARTE003', 'Service Technique', 2000.00, 'active');



INSERT INTO PrixCarburant (date_prix, prix, id_carburant) VALUES 
('2025-01-01', 12.50, 1),
('2025-02-15', 12.60, 1),
('2025-03-01', 12.70, 1),
('2025-04-06', 12.80, 2),
('2025-05-15', 12.90, 2),
('2025-06-01', 13.00, 2);

