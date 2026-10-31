# Guide d'Intégration du Logo dans les PDF

## 🎨 Logo Intégré

Le logo **Al Omrane - Région de l'Oriental Oujda** est maintenant intégré dans tous les services PDF :

### **Services Modifiés**
- ✅ **RapportService.java** - Rapports des ravitaillements
- ✅ **ExportVehiculesPDFService.java** - Rapports des véhicules
- ✅ **RapportPDFService.java** - Rapports statistiques complets
- ✅ **ImpressionPDFService.java** - Nouveau service pour reçus et rapports

## 📊 Structure des PDF avec Logo

### **En-tête Standard**
```
┌─────────────────────────────────────┐
│           [LOGO AL OMRANE]          │
│                                     │
│        TITRE DU RAPPORT             │
│     Sous-titre - Région Oujda       │
│                                     │
│    Rapport généré le DD/MM/YYYY     │
│─────────────────────────────────────│
```

## 🔧 Utilisation des Services

### **1. RapportService.java** (Rapports des Ravitaillements)

```java
// Dans ImpressionController.java
@FXML
private void imprimerRapport() {
    try {
        List<Ravitaillement> ravits = ravitaillementService.getAll();
        
        // Filtrer par période si nécessaire
        if (dateDebutPicker.getValue() != null && dateFinPicker.getValue() != null) {
            LocalDateTime debut = dateDebutPicker.getValue().atStartOfDay();
            LocalDateTime fin = dateFinPicker.getValue().atTime(LocalTime.MAX);
            
            ravits = ravits.stream()
                .filter(r -> !r.getDateRavitaillement().isBefore(debut) && 
                            !r.getDateRavitaillement().isAfter(fin))
                .collect(Collectors.toList());
        }
        
        RapportService rapportService = new RapportService();
        String chemin = "rapport_ravitaillements_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        
        rapportService.genererRapportPDF(ravits, chemin);
        
        AlertUtil.showAlert("Succès", 
            "Rapport des ravitaillements généré avec logo : " + chemin, 
            Alert.AlertType.INFORMATION);
        
        // Ouvrir le fichier PDF
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(chemin));
        }
    } catch (Exception e) {
        AlertUtil.showAlert("Erreur", 
            "Échec de génération : " + e.getMessage(), 
            Alert.AlertType.ERROR);
    }
}
```

### **2. ExportVehiculesPDFService.java** (Rapports des Véhicules)

```java
// Dans GestionVehiculesController.java
@FXML
private void exporterVehiculesPDF() {
    try {
        ExportVehiculesPDFService exportService = new ExportVehiculesPDFService();
        String chemin = "rapport_vehicules_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        
        exportService.genererRapportVehiculesPDF(chemin);
        
        AlertUtil.showAlert("Succès", 
            "Rapport des véhicules généré avec logo : " + chemin, 
            Alert.AlertType.INFORMATION);
        
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(chemin));
        }
    } catch (Exception e) {
        AlertUtil.showAlert("Erreur", 
            "Échec de génération : " + e.getMessage(), 
            Alert.AlertType.ERROR);
    }
}
```

### **3. RapportPDFService.java** (Statistiques Complètes)

```java
// Dans RapportsController.java
@FXML
private void exporterPDF() {
    try {
        RapportPDFService pdfService = new RapportPDFService();
        
        String periode = periodeChoiceBox.getValue();
        LocalDateTime fin = LocalDateTime.now();
        LocalDateTime debut = fin.minusMonths(
            periode.equals("3 derniers mois") ? 3 : 
            periode.equals("6 derniers mois") ? 6 : 12
        );
        
        String chemin = String.format("rapport_statistiques_%s_%s.pdf", 
            periode.replace(" ", "_"), 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        pdfService.genererRapportComplet(debut, fin, chemin);
        
        AlertUtil.showAlert("Succès", 
            "Rapport statistiques complet généré avec logo : " + chemin, 
            Alert.AlertType.INFORMATION);
        
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(chemin));
        }
    } catch (Exception e) {
        AlertUtil.showAlert("Erreur", 
            "Échec de génération : " + e.getMessage(), 
            Alert.AlertType.ERROR);
    }
}
```

### **4. ImpressionPDFService.java** (Nouveau - Reçus et Rapports)

```java
// Pour générer un reçu PDF
@FXML
private void genererRecuPDF(Ravitaillement ravitaillement) {
    try {
        ImpressionPDFService impressionService = new ImpressionPDFService();
        String chemin = "recu_ravitaillement_" + 
            ravitaillement.getIdRavitaillement() + "_" +
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        
        impressionService.genererRecuPDF(ravitaillement, chemin);
        
        AlertUtil.showAlert("Succès", 
            "Reçu PDF généré avec logo : " + chemin, 
            Alert.AlertType.INFORMATION);
        
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(chemin));
        }
    } catch (Exception e) {
        AlertUtil.showAlert("Erreur", 
            "Échec de génération du reçu : " + e.getMessage(), 
            Alert.AlertType.ERROR);
    }
}

// Pour générer un rapport PDF
@FXML
private void genererRapportPDF(List<Ravitaillement> ravitaillements) {
    try {
        ImpressionPDFService impressionService = new ImpressionPDFService();
        String chemin = "rapport_impression_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        
        impressionService.genererRapportPDF(ravitaillements, chemin);
        
        AlertUtil.showAlert("Succès", 
            "Rapport PDF généré avec logo : " + chemin, 
            Alert.AlertType.INFORMATION);
        
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().open(new File(chemin));
        }
    } catch (Exception e) {
        AlertUtil.showAlert("Erreur", 
            "Échec de génération du rapport : " + e.getMessage(), 
            Alert.AlertType.ERROR);
    }
}
```

## 🎯 Caractéristiques du Logo

### **Taille et Position**
- **Rapports** : 200x80 pixels, centré
- **Reçus** : 150x60 pixels, centré
- **Position** : En haut de chaque document

### **Gestion d'Erreurs**
- ✅ **Logo non trouvé** : Le PDF est généré sans logo
- ✅ **Message d'information** : "Logo non trouvé, génération du rapport sans logo"
- ✅ **Continuation** : Le processus ne s'arrête pas

### **Chemin du Logo**
```
/src/resources/images/entete_logo.png
```

## 📋 Types de Documents avec Logo

### **1. Reçus de Ravitaillement**
- Logo Al Omrane en en-tête
- Informations du ravitaillement
- Détails complets (véhicule, conducteur, station, etc.)
- Signature pour validation

### **2. Rapports des Ravitaillements**
- Logo Al Omrane en en-tête
- Résumé des statistiques
- Tableau détaillé des ravitaillements
- Statistiques par véhicule et conducteur

### **3. Rapports des Véhicules**
- Logo Al Omrane en en-tête
- Informations complètes des véhicules
- Statistiques par véhicule
- Comparaison consommation réelle vs théorique
- Historique des ravitaillements

### **4. Rapports Statistiques Complets**
- Logo Al Omrane en en-tête
- Résumé exécutif
- Statistiques par période (3, 6, 12 mois)
- Top stations et analyse saisonnière
- Évolution des prix
- Véhicules avec consommation anormale

## 🚀 Avantages de l'Intégration du Logo

### **1. Professionnalisme**
- ✅ **Identité visuelle** de la région
- ✅ **Cohérence** avec l'organisation
- ✅ **Crédibilité** des documents

### **2. Contexte Local**
- ✅ **Adaptation** au contexte marocain
- ✅ **Région de l'Oriental** bien identifiée
- ✅ **Oujda** comme centre de référence

### **3. Qualité des Documents**
- ✅ **Format PDF** professionnel
- ✅ **Logo haute qualité** intégré
- ✅ **Mise en page** soignée

## 📁 Fichiers Générés avec Logo

### **Noms des Fichiers**
- `rapport_ravitaillements_YYYY-MM-DD.pdf`
- `rapport_vehicules_YYYY-MM-DD.pdf`
- `rapport_statistiques_3_derniers_mois_YYYY-MM-DD.pdf`
- `recu_ravitaillement_123_YYYY-MM-DD.pdf`
- `rapport_impression_YYYY-MM-DD.pdf`

### **Localisation**
- Tous les PDF sont générés dans le répertoire racine du projet
- Ouverture automatique après génération
- Compatible avec tous les lecteurs PDF

## 🔧 Configuration Requise

### **Dépendances**
- ✅ `openpdf-1.3.30.jar` (déjà présent)
- ✅ Logo `entete_logo.png` dans `/src/resources/images/`

### **Imports Nécessaires**
```java
import service.RapportService;
import service.ExportVehiculesPDFService;
import service.RapportPDFService;
import service.ImpressionPDFService;
import java.awt.Desktop;
import java.io.File;
```

Votre application génère maintenant des documents PDF professionnels avec le logo officiel d'Al Omrane - Région de l'Oriental Oujda ! 🎨📊🚗
