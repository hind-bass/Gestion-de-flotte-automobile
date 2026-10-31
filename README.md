# 🚗 Application de Gestion des Carburants

Une solution complète et intuitive pour superviser efficacement la flotte de véhicules d'une entreprise.

## ✨ Fonctionnalités Principales

### 🔐 **Authentification Sécurisée**
- Écran de connexion avec gestion des rôles (Admin/Utilisateur)
- Traçabilité des actions par utilisateur
- Protection des données sensibles

### 📊 **Tableau de Bord (Dashboard)**
- **KPIs en temps réel** :
  - Coût total des ravitaillements
  - Nombre d'opérations mensuelles
  - Consommation moyenne de la flotte (L/100km)
- **Graphiques interactifs** :
  - Évolution des coûts mensuels (courbe)
  - Répartition par véhicule (camembert)
  - Consommation réelle par véhicule (histogramme)

### 🚗 **Gestion des Véhicules**
- **CRUD complet** : Ajouter, modifier, supprimer
- **Caractéristiques techniques** :
  - Matricule marocain (format : 1234-AB)
  - Marque et modèle
  - Type de carburant
  - Kilométrage initial et actuel
  - Consommation théorique (L/100km)
- **Recherche rapide** par matricule
- **Historique détaillé** avec graphiques de consommation

### ⛽ **Gestion des Ravitaillements**
- **Saisie simplifiée** :
  - Choix du véhicule
  - Conducteur (chauffeur ou utilisateur)
  - Lieu de ravitaillement
  - Quantité en litres
  - Prix unitaire en Dirhams (DH)
  - Kilométrage relevé
- **Calcul automatique** du montant total
- **Association optionnelle** de carte carburant
- **Traçabilité par conducteur**

### 📈 **Rapports et Analyses**
- **Filtres de période** : 1 mois, 6 mois, 1 an
- **Graphiques avancés** pour l'analyse des tendances
- **Identification des véhicules** les plus gourmands
- **Évaluation des campagnes** d'éco-conduite

### 📄 **Export des Données**
- **Format PDF** pour rapports mensuels
- **Intégration** avec outils de gestion
- **Soumission à la direction**

## 🛠️ Installation et Configuration

### Prérequis
- **Java 11+** (OpenJDK ou Oracle JDK)
- **MySQL 8.0+** ou **MariaDB 10.3+**
- **JavaFX** (inclus dans le projet via lib/)

### 1. **Base de Données**
```sql
-- Créer la base de données
CREATE DATABASE gestion_carburant;
USE gestion_carburant;

-- Exécuter le script SQL
source src/gestionCarburant.sql;
```

### 2. **Configuration de Connexion**
Modifier `src/service/DatabaseConnection.java` :
```java
private final String URL = "jdbc:mysql://localhost:3306/gestion_carburant";
private final String USERNAME = "votre_utilisateur";
private final String PASSWORD = "votre_mot_de_passe";
```

### 3. **Compilation et Exécution**
```bash
# Compiler le projet
ant build

# Exécuter l'application
ant run
```

## 🔑 **Comptes de Test**

### Administrateur
- **Login** : `admin`
- **Mot de passe** : `admin123`
- **Rôle** : Administrateur complet

### Utilisateurs
- **Login** : `chauffeur1` / `chauffeur2`
- **Mot de passe** : `chauffeur123`
- **Rôle** : Utilisateur standard

## 🚀 **Utilisation**

### 1. **Connexion**
- Lancer l'application
- Saisir identifiants
- Accéder au tableau de bord

### 2. **Navigation**
- **Menu latéral** pour changer de section
- **Boutons d'action** pour les opérations CRUD
- **Recherche** par matricule de véhicule

### 3. **Gestion des Véhicules**
- **Ajouter** : Remplir le formulaire et cliquer "Enregistrer"
- **Modifier** : Cliquer sur ✏️ dans la table
- **Supprimer** : Cliquer sur 🗑️ et confirmer
- **Graphiques** : Cliquer sur 📊 pour voir l'historique

### 4. **Saisie des Ravitaillements**
- Sélectionner le véhicule
- Remplir les informations (lieu, conducteur, etc.)
- Le montant se calcule automatiquement
- Cliquer "Enregistrer"

### 5. **Analyse des Rapports**
- Choisir la période d'analyse
- Consulter les graphiques
- Exporter en PDF si nécessaire

## 📊 **Structure de la Base de Données**

### Tables Principales
- **`Role`** : Gestion des rôles utilisateur
- **`Utilisateur`** : Comptes utilisateurs
- **`Vehicule`** : Informations des véhicules
- **`Carburant`** : Types de carburant
- **`Ravitaillement`** : Historique des pleins
- **`CarteCarburant`** : Cartes de paiement
- **`PrixCarburant`** : Historique des prix

### Relations
- Véhicule → Carburant (type de carburant)
- Ravitaillement → Véhicule (véhicule concerné)
- Ravitaillement → Utilisateur (qui a saisi)
- Ravitaillement → CarteCarburant (optionnel)

## 🎨 **Interface Utilisateur**

### Design
- **Interface moderne** avec animations discrètes
- **Navigation fluide** entre les sections
- **Thème clair/sombre** disponible
- **Responsive** et intuitif

### Couleurs
- **Primaire** : #356267 (bleu-vert)
- **Secondaire** : #417377 (vert-bleu)
- **Accent** : #89b4fa (bleu clair)
- **Danger** : #e74c3c (rouge)

## 🔧 **Développement**

### Architecture
- **MVC** (Model-View-Controller)
- **JavaFX** pour l'interface
- **MySQL** pour la persistance
- **Services** pour la logique métier

### Structure des Packages
```
src/
├── main/           # Point d'entrée
├── controller/     # Contrôleurs FXML
├── model/         # Entités métier
├── service/       # Services et DAO
├── util/          # Utilitaires
├── resources/     # FXML, CSS, images
└── components/    # Composants réutilisables
```

## 📝 **Notes Techniques**

### Calculs Automatiques
- **Consommation réelle** : (Quantité × 100) / Kilométrage parcouru
- **Montant total** : Quantité × Prix unitaire
- **Moyennes** : Calculs statistiques en temps réel

### Sécurité
- **Hachage des mots de passe** (à implémenter)
- **Gestion des sessions** utilisateur
- **Validation des données** côté client et serveur

## 🚀 **Fonctionnalités Futures**

- [ ] **Notifications** en temps réel
- [ ] **API REST** pour intégration externe
- [ ] **Mobile** (application Android/iOS)
- [ ] **Géolocalisation** des ravitaillements
- [ ] **Maintenance préventive** des véhicules
- [ ] **Intégration GPS** pour suivi en temps réel

## 📞 **Support**

Pour toute question ou problème :
- **Email** : support@gestioncarburant.ma
- **Documentation** : [Wiki du projet]
- **Issues** : [GitHub Issues]

---

**Version** : 1.0.0  
**Dernière mise à jour** : Janvier 2025  
**Licence** : MIT
