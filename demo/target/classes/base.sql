CREATE DATABASE projet_java; 

USE gestion_evenements;

-- Table des utilisateurs
CREATE TABLE users (
    id_user INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100),
    prenom VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    type ENUM('ETUDIANT', 'PROFESSEUR')
);

-- Table des événements
CREATE TABLE evenemant (
    id_event INT AUTO_INCREMENT PRIMARY KEY,
    nom_event VARCHAR(100),
    date_event DATETIME,
    description TEXT,
    id_user INT,
    FOREIGN KEY (id_user) REFERENCES users(id_user)
);

-- Table des salles
CREATE TABLE salles (
    id_salle INT AUTO_INCREMENT PRIMARY KEY,
    nom_salle VARCHAR(100),
    capacite INT
);

-- Table des terrains
CREATE TABLE terrains (
    id_terrain INT AUTO_INCREMENT PRIMARY KEY,
    nom_terrain VARCHAR(100),
    type VARCHAR(50)
);

-- Table des réservations
CREATE TABLE reservations (
    id_reservation INT AUTO_INCREMENT PRIMARY KEY,
    id_user INT,
    id_event INT,
    id_salle INT,
    id_terrain INT,
    date_reservation DATETIME,
    FOREIGN KEY (id_user) REFERENCES users(id_user),
    FOREIGN KEY (id_event) REFERENCES evenemant(id_event),
    FOREIGN KEY (id_salle) REFERENCES salles(id_salle),
    FOREIGN KEY (id_terrain) REFERENCES terrains(id_terrain)
);