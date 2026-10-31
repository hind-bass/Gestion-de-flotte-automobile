# 🖨️ Fonctionnalité d'Impression des Reçus de Ravitaillement

## 📋 Vue d'ensemble

Cette nouvelle fonctionnalité permet d'imprimer des reçus de ravitaillement détaillés et des rapports de consommation au format texte (.txt). Elle inclut toutes les informations demandées :

- **Prix** : Prix unitaire et montant total
- **Conducteur** : À qui le ravitaillement est destiné
- **Litres** : Quantité de carburant
- **Période de consommation** : Calcul automatique de la durée d'utilisation de la carte

## 🚀 Fonctionnalités

### 1. Impression de Reçus Individuels
- Reçu détaillé pour chaque ravitaillement
- Informations complètes du véhicule et du conducteur
- Détails de la carte carburant (plafond, titulaire)
- Calculs automatiques de consommation et d'autonomie

### 2. Rapports de Consommation
- Filtrage par période (dates de début et fin)
- Tableau récapitulatif de tous les ravitaillements
- Statistiques détaillées (totaux, moyennes, coûts quotidiens)
- Export au format texte structuré

### 3. Gestion des Cartes Carburant
- Calcul automatique de la durée restante de la carte
- Estimation des litres restants basée sur le plafond
- Alertes en cas de dépassement du plafond

### 4. En-tête avec Logo Stylisé
- Logo "🚗 GESTION CARBURANT 🚗" dans un cadre décoratif
- Mise en forme professionnelle avec bordures Unicode
- Titre centré et personnalisable pour chaque type de document

## 🎯 Utilisation

### Accès à la fonctionnalité
1. Se connecter à l'application
2. Aller dans le **Dashboard**
3. Cliquer sur le bouton **🖨️ Impression** dans le menu latéral

### Interface utilisateur
- **Filtrage par date** : Sélectionner une période pour les rapports
- **Tableau des ravitaillements** : Voir tous les ravitaillements disponibles
- **Boutons d'action** :
  - 🖨️ **Imprimer Reçu** : Génère un reçu pour le ravitaillement sélectionné
  - 📊 **Imprimer Rapport** : Génère un rapport pour la période sélectionnée

### Configuration
- **Dossier de sauvegarde** : Choisir où sauvegarder les fichiers
- **Format des fichiers** : Tous les fichiers sont au format .txt
- **Ouverture automatique** : Les fichiers s'ouvrent automatiquement après génération

## 📊 Exemples de Sortie

### Reçu de Ravitaillement avec Logo
```
======================================================================
  ╔══════════════════════════════════════════════════════════════╗
  ║                    🚗 GESTION CARBURANT 🚗                  ║
  ║                                                              ║
  ║                    RECU DE RAVITAILLEMENT                    ║
  ╚══════════════════════════════════════════════════════════════╝
======================================================================

Date et heure: 15/12/2024 14:30
Lieu: Station Total - Casablanca
N° Ravitaillement: 1

Véhicule: ABC-123
Marque/Modèle: Renault Clio

Conducteur: Ahmed Benali

Carte carburant: CARTE-001
Titulaire: Mohammed Basso
Plafond: 2000.00 DH

------------------------------------------------------------

Quantité: 45.50 litres
Prix unitaire: 12.50 DH/litre
Montant total: 568.75 DH
Kilométrage: 125000 km

INFORMATIONS DE CONSOMMATION:
-----------------------------------
Plafond de la carte: 2000.00 DH
Prix moyen du carburant: 12.50 DH/litre
Litres possibles avec le plafond: 160.00 litres
Autonomie estimée: 2000 km
Durée estimée de la carte: 0.8 mois

============================================================
           RECU GENERÉ LE 15/12/2024 14:30:45
============================================================
```

### Rapport de Consommation avec Logo
```
================================================================================
  ╔══════════════════════════════════════════════════════════════════════════╗
  ║                      🚗 GESTION CARBURANT 🚗                            ║
  ║                                                                          ║
  ║                      RAPPORT DE CONSOMMATION                             ║
  ╚══════════════════════════════════════════════════════════════════════════╝
================================================================================

Période: 01/12/2024 au 15/12/2024

ID     Date         Conducteur      Véhicule      Litres   Prix/L      Total
--------------------------------------------------------------------------------
1      15/12/2024   Ahmed Benali    ABC-123      45.50    12.50       568.75
2      08/12/2024   Ahmed Benali    ABC-123      38.00    12.30       467.40
3      01/12/2024   Ahmed Benali    ABC-123      42.00    12.45       522.90
--------------------------------------------------------------------------------
TOTAUX:                             125.50                 1559.05

STATISTIQUES DÉTAILLÉES:
------------------------------
Nombre de ravitaillements: 3
Consommation moyenne par ravitaillement: 41.83 litres
Coût moyen par litre: 12.42 DH
Consommation quotidienne moyenne: 8.37 litres/jour
Coût quotidien moyen: 103.94 DH/jour

======================================================================
```

## 🔧 Architecture Technique

### Classes créées
- **`ImpressionService`** : Service principal pour la génération des reçus
- **`ImpressionController`** : Contrôleur de l'interface utilisateur
- **`RecuRavitaillement`** : Modèle pour les reçus avec historique
- **`Impression.fxml`** : Interface utilisateur

### Intégration
- Ajout du bouton dans le **Dashboard**
- Navigation via **MainApp.loadImpressionView()**
- Utilisation des services existants (RavitaillementService, etc.)

## 📁 Structure des Fichiers

```
src/
├── service/
│   └── ImpressionService.java          # Service d'impression
├── controller/
│   └── ImpressionController.java       # Contrôleur d'impression
├── model/
│   └── RecuRavitaillement.java        # Modèle de reçu
├── resources/
│   └── Impression.fxml                 # Interface utilisateur
└── test/
    └── TestImpression.java             # Tests de la fonctionnalité
```

## 🧪 Tests

Pour tester la fonctionnalité :

1. **Compiler le projet** : `javac -cp "lib/*" src/test/TestImpression.java`
2. **Exécuter les tests** : `java -cp "lib/*:src" test.TestImpression`
3. **Vérifier les fichiers générés** : Les fichiers .txt seront créés dans le répertoire courant

## 🎨 Personnalisation

### Modifier le format des reçus
- Éditer `ImpressionService.java`
- Modifier les méthodes `genererRecuRavitaillement()` et `genererRapportConsommation()`

### Personnaliser le logo et l'en-tête
- Modifier la méthode `genererEnteteLogo()` dans `ImpressionService.java`
- Changer le texte "🚗 GESTION CARBURANT 🚗" par votre propre nom d'entreprise
- Ajuster la largeur et le style des bordures
- Remplacer les emojis par des caractères personnalisés

### Changer le dossier de sauvegarde
- Par défaut : `~/Documents/`
- Modifiable via l'interface utilisateur

### Ajouter de nouveaux champs
- Étendre le modèle `RecuRavitaillement`
- Modifier les services d'impression
- Mettre à jour l'interface utilisateur

## 🚨 Limitations et Améliorations Futures

### Limitations actuelles
- Format de sortie : Texte uniquement (pas de PDF)
- Pas de graphiques dans les rapports
- Calculs basés sur des estimations (consommation moyenne)

### Améliorations possibles
- Export PDF avec mise en forme avancée
- Graphiques et visualisations dans les rapports
- Intégration avec des imprimantes physiques
- Envoi par email des reçus
- Sauvegarde cloud (Google Drive, Dropbox)

## 📞 Support

Pour toute question ou problème avec cette fonctionnalité :
1. Vérifier les logs de l'application
2. Tester avec le fichier `TestImpression.java`
3. Vérifier les permissions d'écriture dans le dossier de sauvegarde

---

**Version** : 1.0  
**Date** : Décembre 2024  
**Auteur** : Mohammed Basso
