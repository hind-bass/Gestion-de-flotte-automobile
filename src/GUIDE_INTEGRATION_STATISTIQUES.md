# Guide d'Intégration des Statistiques Avancées

## 📊 Intégration dans les Contrôleurs Existants

### 1. DashboardController.java

```java
// Ajouter ces imports
import service.StatistiqueService;
import java.util.Map;

// Remplacer la méthode chargerKPIs() existante
private void chargerKPIs() {
    StatistiqueService statService = new StatistiqueService();
    
    // KPIs généraux
    Map<String, Object> kpis = statService.getKPIsGeneraux();
    
    coutTotalLabel.setText(String.format("%.2f DH", (Double) kpis.get("cout_total")));
    nbRavitaillementsLabel.setText(String.valueOf(kpis.get("nombre_ravitaillements")));
    
    // Consommation moyenne réelle
    List<Map<String, Object>> consoReelle = statService.getConsommationReelleParVehicule();
    if (!consoReelle.isEmpty()) {
        double consoMoy = consoReelle.stream()
            .mapToDouble(v -> (Double) v.get("conso_moyenne"))
            .average()
            .orElse(0.0);
        consoMoyenneLabel.setText(String.format("%.2f L/100km", consoMoy));
    }
}
```

### 2. RapportsController.java

```java
// Ajouter ces imports
import service.StatistiqueService;
import service.RapportPDFService;
import java.util.Map;
import java.util.List;

// Remplacer la méthode chargerGraphiques()
private void chargerGraphiques() {
    StatistiqueService statService = new StatistiqueService();
    
    // Statistiques par période selon le choix
    String periode = periodeChoiceBox.getValue();
    int mois = periode.equals("3 derniers mois") ? 3 : 
               periode.equals("6 derniers mois") ? 6 : 12;
    
    List<Map<String, Object>> statsPeriode = statService.getStatistiquesParPeriode(mois);
    
    // Afficher les coûts mensuels
    if (coutsChartContainer != null) {
        String texte = statsPeriode.stream()
            .map(s -> s.get("mois") + ": " + String.format("%.2f DH", (Double) s.get("total_montant")))
            .collect(Collectors.joining(" | "));
        Label ph = new Label("Coûts mensuels: " + texte);
        coutsChartContainer.getChildren().setAll(ph);
    }
    
    // Répartition par véhicule
    List<Map<String, Object>> repartition = statService.getRepartitionParVehicule();
    if (repartitionChartContainer != null) {
        String texte = repartition.stream()
            .map(v -> v.get("matricule") + ": " + String.format("%.2f DH", (Double) v.get("total_montant")))
            .collect(Collectors.joining(" | "));
        Label ph2 = new Label("Répartition: " + texte);
        repartitionChartContainer.getChildren().setAll(ph2);
    }
    
    // Consommation réelle
    List<Map<String, Object>> consoReelle = statService.getConsommationReelleParVehicule();
    if (consoReelleChartContainer != null) {
        String texte = consoReelle.stream()
            .map(v -> v.get("matricule") + ": " + String.format("%.2f L/100km", (Double) v.get("conso_moyenne")))
            .collect(Collectors.joining(" | "));
        Label ph3 = new Label("Consommation (L/100km): " + texte);
        consoReelleChartContainer.getChildren().setAll(ph3);
    }
}

// Remplacer la méthode exporterPDF()
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
        
        String chemin = String.format("rapport_carburant_%s_%s.pdf", 
            periode.replace(" ", "_"), 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        
        pdfService.genererRapportComplet(debut, fin, chemin);
        
        AlertUtil.showAlert("Succès", 
            "Rapport PDF complet généré : " + chemin, 
            Alert.AlertType.INFORMATION);
    } catch (Exception e) {
        AlertUtil.showAlert("Erreur", 
            "Échec de génération : " + e.getMessage(), 
            Alert.AlertType.ERROR);
    }
}
```

### 3. ImpressionController.java

```java
// Ajouter ces imports
import service.StatistiqueService;
import java.util.Map;

// Remplacer la méthode mettreAJourStatistiques()
private void mettreAJourStatistiques(List<Ravitaillement> ravitaillements) {
    if (ravitaillements.isEmpty()) {
        labelStatistiques.setText("Aucun ravitaillement trouvé pour la période sélectionnée");
        btnImprimerRapport.setDisable(true);
        return;
    }

    // Utiliser le service de statistiques pour des calculs plus précis
    StatistiqueService statService = new StatistiqueService();
    LocalDateTime debut = dateDebutPicker.getValue().atStartOfDay();
    LocalDateTime fin = dateFinPicker.getValue().atTime(LocalTime.MAX);
    
    Map<String, Object> stats = statService.getStatistiquesPeriode(debut, fin);

    String statsText = String.format(
        "Période: %s au %s | Total: %d ravitaillements | %.2f litres | %.2f DH | %.2f DH/litre",
        dateDebutPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        dateFinPicker.getValue().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
        (Integer) stats.get("nombre_ravitaillements"),
        (Double) stats.get("total_litres"),
        (Double) stats.get("total_montant"),
        (Double) stats.get("prix_moyen")
    );

    labelStatistiques.setText(statsText);
    btnImprimerRapport.setDisable(false);
}
```

## 🔧 Nouvelles Fonctionnalités Disponibles

### 1. StatistiqueService - Méthodes Principales

```java
// KPIs généraux
Map<String, Object> kpis = statistiqueService.getKPIsGeneraux();

// Statistiques par période (3, 6, 12 mois)
List<Map<String, Object>> stats = statistiqueService.getStatistiquesParPeriode(6);

// Répartition par véhicule
List<Map<String, Object>> repartition = statistiqueService.getRepartitionParVehicule();

// Consommation réelle par véhicule
List<Map<String, Object>> consoReelle = statistiqueService.getConsommationReelleParVehicule();

// Top 5 des stations
List<Map<String, Object>> topStations = statistiqueService.getTopStations();

// Analyse saisonnière
List<Map<String, Object>> saisons = statistiqueService.getAnalyseSaisonniere();

// Évolution des prix
List<Map<String, Object>> evolutionPrix = statistiqueService.getEvolutionPrix();

// Véhicules avec consommation anormale
List<Map<String, Object>> vehiculesAnormaux = statistiqueService.getVehiculesConsommationAnormale();
```

### 2. RapportPDFService - Génération de Rapports

```java
// Rapport PDF complet avec toutes les statistiques
RapportPDFService pdfService = new RapportPDFService();
pdfService.genererRapportComplet(debut, fin, "rapport_complet.pdf");
```

## 📈 Requêtes SQL Optimisées

### Utilisation des Vues Créées

```sql
-- Statistiques rapides
SELECT * FROM v_statistiques_rapides;

-- Ravitaillements avec informations complètes
SELECT * FROM v_ravitaillements_complets 
WHERE date_ravitaillement BETWEEN '2024-09-01' AND '2025-09-01';
```

### Requêtes Spécialisées

```sql
-- Comparaison 3 vs 6 vs 12 mois
SELECT 
    '3 derniers mois' as periode,
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as total_montant
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 3 MONTH)

UNION ALL

SELECT 
    '6 derniers mois' as periode,
    COUNT(*) as nombre_ravitaillements,
    SUM(montant_total) as total_montant
FROM Ravitaillement 
WHERE date_ravitaillement >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH);
```

## 🚀 Améliorations Apportées

### 1. Performance
- ✅ Index sur les colonnes les plus utilisées
- ✅ Vues pour simplifier les requêtes complexes
- ✅ Requêtes optimisées avec JOIN appropriés

### 2. Fonctionnalités
- ✅ Statistiques avancées (saisonnières, évolution prix)
- ✅ Détection de consommation anormale
- ✅ Top stations et conducteurs
- ✅ Rapports PDF complets avec graphiques

### 3. Intégration
- ✅ Compatible avec l'architecture existante
- ✅ Services modulaires et réutilisables
- ✅ Méthodes optimisées pour les contrôleurs

## 📋 Checklist d'Intégration

- [ ] Exécuter le script `requetes_statistiques_optimisees.sql`
- [ ] Ajouter `StatistiqueService.java` au projet
- [ ] Ajouter `RapportPDFService.java` au projet
- [ ] Modifier `DashboardController.java`
- [ ] Modifier `RapportsController.java`
- [ ] Modifier `ImpressionController.java`
- [ ] Tester les nouvelles fonctionnalités
- [ ] Générer un rapport PDF de test

## 🎯 Résultats Attendus

Après intégration, vous aurez :

1. **Dashboard enrichi** avec KPIs précis
2. **Rapports avancés** avec comparaisons temporelles
3. **Export PDF complet** avec toutes les statistiques
4. **Détection automatique** des anomalies de consommation
5. **Analyse saisonnière** des tendances
6. **Performance optimisée** des requêtes

## 🔍 Exemples d'Utilisation

### Génération d'un Rapport Complet
```java
// Dans un contrôleur
@FXML
private void genererRapportComplet() {
    try {
        RapportPDFService pdfService = new RapportPDFService();
        LocalDateTime debut = LocalDateTime.now().minusMonths(6);
        LocalDateTime fin = LocalDateTime.now();
        
        String chemin = "rapport_6_mois_" + 
            LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".pdf";
        
        pdfService.genererRapportComplet(debut, fin, chemin);
        
        // Ouvrir le fichier PDF
        Desktop.getDesktop().open(new File(chemin));
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

### Surveillance des Véhicules
```java
// Vérifier les véhicules avec consommation anormale
StatistiqueService statService = new StatistiqueService();
List<Map<String, Object>> vehiculesAnormaux = statService.getVehiculesConsommationAnormale();

if (!vehiculesAnormaux.isEmpty()) {
    // Afficher une alerte
    String message = vehiculesAnormaux.stream()
        .map(v -> v.get("matricule") + " (+" + v.get("ecart_pourcentage") + "%)")
        .collect(Collectors.joining(", "));
    
    AlertUtil.showAlert("Attention", 
        "Véhicules avec consommation anormale: " + message, 
        Alert.AlertType.WARNING);
}
```

Cette intégration vous permettra d'avoir une application de gestion de carburant complète avec des statistiques avancées et des rapports professionnels ! 🚗⛽📊
