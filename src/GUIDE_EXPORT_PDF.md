# Guide d'Export PDF - Véhicules et Statistiques

## 📊 Services PDF Disponibles

### 1. **RapportService.java** (Amélioré)
- ✅ **Export des ravitaillements** avec statistiques
- ✅ **Résumé des statistiques** (totaux, moyennes)
- ✅ **Statistiques par véhicule**
- ✅ **Format professionnel** avec en-têtes

### 2. **ExportVehiculesPDFService.java** (Nouveau)
- ✅ **Informations complètes des véhicules**
- ✅ **Statistiques détaillées par véhicule**
- ✅ **Comparaison consommation réelle vs théorique**
- ✅ **Historique des ravitaillements**
- ✅ **Détection d'anomalies de consommation**

### 3. **RapportPDFService.java** (Corrigé)
- ✅ **Rapport complet avec 8 sections**
- ✅ **Statistiques avancées** (saisonnières, évolution prix)
- ✅ **Top stations et conducteurs**
- ✅ **Recommandations automatiques**

## 🔧 Intégration dans les Contrôleurs

### **DashboardController.java** - Bouton Export Statistiques

```java
// Ajouter ces imports
import service.ExportVehiculesPDFService;
import service.RapportPDFService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.io.File;
import java.awt.Desktop;

// Ajouter cette méthode pour le bouton d'export
@FXML
private void exporterStatistiques() {
    try {
        // Créer un dialogue pour choisir le type d'export
        javafx.scene.control.ChoiceDialog<String> dialog = new javafx.scene.control.ChoiceDialog<>(
            "Véhicules et Informations", 
            "Véhicules et Informations", 
            "Statistiques Complètes", 
            "Ravitaillements"
        );
        dialog.setTitle("Export PDF");
        dialog.setHeaderText("Choisir le type de rapport à exporter");
        dialog.setContentText("Type de rapport:");
        
        java.util.Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String typeRapport = result.get();
            String chemin = "";
            
            switch (typeRapport) {
                case "Véhicules et Informations":
                    ExportVehiculesPDFService exportVehicules = new ExportVehiculesPDFService();
                    chemin = "rapport_vehicules_" + 
                        java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
                    exportVehicules.genererRapportVehiculesPDF(chemin);
                    break;
                    
                case "Statistiques Complètes":
                    RapportPDFService rapportComplet = new RapportPDFService();
                    LocalDateTime debut = LocalDateTime.now().minusMonths(6);
                    LocalDateTime fin = LocalDateTime.now();
                    chemin = "rapport_statistiques_complet_" + 
                        java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
                    rapportComplet.genererRapportComplet(debut, fin, chemin);
                    break;
                    
                case "Ravitaillements":
                    RapportService rapportRavitaillements = new RapportService();
                    List<Ravitaillement> ravits = ravitaillementService.getAll();
                    chemin = "rapport_ravitaillements_" + 
                        java.time.LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
                    rapportRavitaillements.genererRapportPDF(ravits, chemin);
                    break;
            }
            
            // Afficher le message de succès
            util.AlertUtil.showAlert("Succès", 
                "Rapport PDF généré avec succès : " + chemin, 
                javafx.scene.control.Alert.AlertType.INFORMATION);
            
            // Ouvrir le fichier PDF
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(new File(chemin));
            }
        }
    } catch (Exception e) {
        util.AlertUtil.showAlert("Erreur", 
            "Échec de génération du rapport : " + e.getMessage(), 
            javafx.scene.control.Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}
```

### **GestionVehiculesController.java** - Export des Véhicules

```java
// Ajouter ces imports
import service.ExportVehiculesPDFService;
import java.io.File;
import java.awt.Desktop;

// Ajouter cette méthode pour l'export des véhicules
@FXML
private void exporterVehiculesPDF() {
    try {
        ExportVehiculesPDFService exportService = new ExportVehiculesPDFService();
        String chemin = "rapport_vehicules_" + 
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        
        exportService.genererRapportVehiculesPDF(chemin);
        
        util.AlertUtil.showAlert("Succès", 
            "Rapport des véhicules généré : " + chemin, 
            javafx.scene.control.Alert.AlertType.INFORMATION);
        
        // Ouvrir le fichier PDF
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(chemin));
        }
    } catch (Exception e) {
        util.AlertUtil.showAlert("Erreur", 
            "Échec de génération : " + e.getMessage(), 
            javafx.scene.control.Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}
```

### **RapportsController.java** - Export des Statistiques

```java
// Remplacer la méthode exporterPDF() existante
@FXML
private void exporterPDF() {
    try {
        RapportPDFService pdfService = new RapportPDFService();
        
        // Définir la période selon le choix
        String periode = periodeChoiceBox.getValue();
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime debut = fin.minusMonths(
            periode.equals("3 derniers mois") ? 3 : 
            periode.equals("6 derniers mois") ? 6 : 12
        );
        
        String chemin = String.format("rapport_statistiques_%s_%s.pdf", 
            periode.replace(" ", "_"), 
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        pdfService.genererRapportComplet(debut, fin, chemin);
        
        util.AlertUtil.showAlert("Succès", 
            "Rapport PDF complet généré : " + chemin, 
            javafx.scene.control.Alert.AlertType.INFORMATION);
        
        // Ouvrir le fichier PDF
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(new java.io.File(chemin));
        }
    } catch (Exception e) {
        util.AlertUtil.showAlert("Erreur", 
            "Échec de génération : " + e.getMessage(), 
            javafx.scene.control.Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}
```

### **ImpressionController.java** - Export des Ravitaillements

```java
// Remplacer la méthode imprimerRapport() existante
@FXML
private void imprimerRapport() {
    try {
        List<Ravitaillement> ravits = ravitaillementService.getAll();
        
        // Filtrer par période si sélectionnée
        if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null) {
            LocalDateTime debut = dateDebutPicker.getValue().atStartOfDay();
            LocalDateTime fin = dateFinPicker.getValue().atTime(LocalTime.MAX);
            
            ravits = ravits.stream()
                .filter(r -> !r.getDateRavitaillement().isBefore(debut) && 
                            !r.getDateRavitaillement().isAfter(fin))
                .collect(java.util.stream.Collectors.toList());
        }
        
        RapportService rapportService = new RapportService();
        String chemin = "rapport_ravitaillements_" + 
            java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        
        rapportService.genererRapportPDF(ravits, chemin);
        
        util.AlertUtil.showAlert("Succès", 
            "Rapport des ravitaillements généré : " + chemin, 
            javafx.scene.control.Alert.AlertType.INFORMATION);
        
        // Ouvrir le fichier PDF
        if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop.getDesktop().open(new java.io.File(chemin));
        }
    } catch (Exception e) {
        util.AlertUtil.showAlert("Erreur", 
            "Échec de génération : " + e.getMessage(), 
            javafx.scene.control.Alert.AlertType.ERROR);
        e.printStackTrace();
    }
}
```

## 📋 Types de Rapports Disponibles

### 1. **Rapport Véhicules** (`ExportVehiculesPDFService`)
- 📊 **Informations complètes** des véhicules
- 📈 **Statistiques détaillées** par véhicule
- 🔍 **Comparaison consommation** réelle vs théorique
- 📝 **Historique des ravitaillements**
- ⚠️ **Détection d'anomalies** de consommation

### 2. **Rapport Statistiques** (`RapportPDFService`)
- 📊 **Résumé exécutif** avec KPIs
- 📈 **Statistiques par période** (3, 6, 12 mois)
- 🚗 **Répartition par véhicule**
- 🏪 **Top 5 des stations**
- 🌍 **Analyse saisonnière**
- 💰 **Évolution des prix**
- ⚠️ **Véhicules avec consommation anormale**
- 📝 **Détail des ravitaillements**

### 3. **Rapport Ravitaillements** (`RapportService`)
- 📊 **Résumé des statistiques**
- 📝 **Détail des ravitaillements**
- 🚗 **Statistiques par véhicule**
- 📅 **Filtrage par période**

## 🎯 Utilisation Recommandée

### **Dashboard** → Bouton "Export Statistiques"
- **Véhicules et Informations** : Pour un rapport complet de la flotte
- **Statistiques Complètes** : Pour une analyse approfondie
- **Ravitaillements** : Pour un rapport simple des ravitaillements

### **Gestion Véhicules** → Bouton "Export PDF"
- **Rapport Véhicules** : Informations détaillées de chaque véhicule

### **Rapports** → Bouton "Exporter PDF"
- **Rapport Statistiques** : Selon la période sélectionnée (3, 6, 12 mois)

### **Impression** → Bouton "Imprimer Rapport"
- **Rapport Ravitaillements** : Selon la période filtrée

## 🔧 Configuration Requise

### **Dépendances**
- ✅ `openpdf-1.3.30.jar` (déjà présent)
- ✅ `mysql-connector-j-9.3.0.jar` (déjà présent)

### **Imports Nécessaires**
```java
import service.ExportVehiculesPDFService;
import service.RapportPDFService;
import service.RapportService;
import java.awt.Desktop;
import java.io.File;
```

## 📁 Fichiers Générés

Les rapports PDF sont générés dans le répertoire racine du projet avec les noms :
- `rapport_vehicules_YYYY-MM-DD.pdf`
- `rapport_statistiques_complet_YYYY-MM-DD.pdf`
- `rapport_ravitaillements_YYYY-MM-DD.pdf`
- `rapport_statistiques_3_derniers_mois_YYYY-MM-DD.pdf`

## 🚀 Fonctionnalités Avancées

### **Ouverture Automatique**
- Les PDF sont automatiquement ouverts après génération
- Compatible avec tous les lecteurs PDF

### **Gestion d'Erreurs**
- Messages d'erreur détaillés
- Gestion des exceptions SQL et PDF

### **Format Professionnel**
- En-têtes avec logo et informations
- Tableaux formatés avec couleurs
- Statistiques visuelles
- Recommandations automatiques

Votre application dispose maintenant d'un système complet d'export PDF pour tous les types de rapports ! 📊🚗⛽
