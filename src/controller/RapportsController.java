package controller;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.ChoiceBox;
import javafx.stage.FileChooser;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Ravitaillement;
import model.Vehicule;
import service.RavitaillementService;
import service.RapportService;
import util.AlertUtil;
import main.MainApp;

public class RapportsController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private ChoiceBox<String> periodeChoiceBox;
    // Charts remplacés par conteneurs
    @FXML private javafx.scene.layout.VBox coutsChartContainer;
    @FXML private javafx.scene.layout.VBox repartitionChartContainer;
    @FXML private javafx.scene.layout.VBox consoReelleChartContainer; // 🔥 Nouveau graphique

    private final RavitaillementService ravitaillementService = new RavitaillementService();
    private final RapportService rapportService = new RapportService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        periodeChoiceBox.setItems(FXCollections.observableArrayList("3 derniers mois", "6 derniers mois", "Année"));
        periodeChoiceBox.setValue("3 derniers mois");
        
        // Listener pour le changement de période
        periodeChoiceBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                chargerGraphiques();
            }
        });

        chargerGraphiques();
        animerApparition();
    }

    private void animerApparition() {
        FadeTransition ft = new FadeTransition(Duration.millis(600), rootPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void chargerGraphiques() {
        List<Ravitaillement> ravits = filtrerParPeriode(ravitaillementService.getAll());
        
        // Créer le graphique des coûts mensuels
        creerGraphiqueCouts(ravits);
        
        // Créer le graphique de répartition par véhicule
        creerGraphiqueRepartition(ravits);
        
        // Créer le graphique de consommation
        creerGraphiqueConsommation(ravits);
    }
    
    private List<Ravitaillement> filtrerParPeriode(List<Ravitaillement> ravits) {
        String periode = periodeChoiceBox.getValue();
        if (periode == null) return ravits;
        
        java.time.LocalDate maintenant = java.time.LocalDate.now();
        java.time.LocalDate dateLimite;
        
        switch (periode) {
            case "3 derniers mois":
                dateLimite = maintenant.minusMonths(3);
                break;
            case "6 derniers mois":
                dateLimite = maintenant.minusMonths(6);
                break;
            case "Année":
                dateLimite = maintenant.minusYears(1);
                break;
            default:
                return ravits;
        }
        
        return ravits.stream()
            .filter(r -> r.getDateRavitaillement() != null)
            .filter(r -> r.getDateRavitaillement().toLocalDate().isAfter(dateLimite))
            .collect(Collectors.toList());
    }
    
    private void creerGraphiqueCouts(List<Ravitaillement> ravits) {
        if (coutsChartContainer == null) return;

        Map<String, Double> coutsParMois = ravits.stream()
            .filter(r -> r.getDateRavitaillement() != null)
            .collect(Collectors.groupingBy(
                r -> r.getDateRavitaillement().getMonth().toString().substring(0, 3),
                Collectors.summingDouble(Ravitaillement::getMontantTotal)
            ));
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Coût (DH)");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Évolution des Coûts Mensuels");
        barChart.setLegendVisible(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Coûts");
        
        coutsParMois.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
        
        barChart.getData().add(series);
        barChart.setPrefSize(1000, 300);
        
        coutsChartContainer.getChildren().setAll(barChart);
    }
    
    private void creerGraphiqueRepartition(List<Ravitaillement> ravits) {
        if (repartitionChartContainer == null) return;
        
        Map<String, Double> repartition = ravits.stream()
            .filter(r -> r.getVehicule() != null)
            .collect(Collectors.groupingBy(
                r -> r.getVehicule().getMatricule(),
                Collectors.summingDouble(Ravitaillement::getMontantTotal)
            ));
        
        PieChart pieChart = new PieChart();
        pieChart.setTitle("Répartition par Véhicule");
        
        repartition.entrySet().stream()
            .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
            .forEach(entry -> {
                PieChart.Data slice = new PieChart.Data(entry.getKey(), entry.getValue());
                pieChart.getData().add(slice);
            });
        
        pieChart.setPrefSize(400, 300);
        pieChart.setLabelsVisible(true);
        
        repartitionChartContainer.getChildren().setAll(pieChart);
    }
    
    private void creerGraphiqueConsommation(List<Ravitaillement> ravits) {
        if (consoReelleChartContainer == null) return;
        
        Map<Vehicule, List<Ravitaillement>> ravitsParVehicule = ravits.stream()
            .filter(r -> r.getVehicule() != null)
            .sorted(Comparator.comparing(Ravitaillement::getDateRavitaillement))
            .collect(Collectors.groupingBy(Ravitaillement::getVehicule));
        
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Consommation (L/100km)");
        
        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        barChart.setTitle("Consommation Réelle par Véhicule");
        barChart.setLegendVisible(false);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Consommation");
        
        for (Map.Entry<Vehicule, List<Ravitaillement>> entry : ravitsParVehicule.entrySet()) {
            Vehicule v = entry.getKey();
            List<Ravitaillement> liste = entry.getValue();

            if (liste.size() < 2) continue;

            double totalConso = 0;
            int nbIntervalles = 0;

            for (int i = 1; i < liste.size(); i++) {
                Ravitaillement r1 = liste.get(i - 1);
                Ravitaillement r2 = liste.get(i);

                int kmParcourus = r2.getKilometrageReleve() - r1.getKilometrageReleve();
                if (kmParcourus > 0) {
                    double conso = (r2.getQuantite() * 100.0) / kmParcourus;
                    totalConso += conso;
                    nbIntervalles++;
                }
            }

            if (nbIntervalles > 0) {
                double consoMoyenne = totalConso / nbIntervalles;
                series.getData().add(new XYChart.Data<>(v.getMatricule(), consoMoyenne));
            }
        }
        
        barChart.getData().add(series);
        barChart.setPrefSize(500, 300);
        
        consoReelleChartContainer.getChildren().setAll(barChart);
    }


    @FXML
    private void exporterPDF() {
        try {
            // Filtrer les données selon la période sélectionnée
            List<Ravitaillement> ravits = filtrerParPeriode(ravitaillementService.getAll());
            String periode = periodeChoiceBox.getValue();
            
            // Créer un dialogue pour choisir l'emplacement de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder le rapport PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );
            
            // Nom de fichier par défaut avec la période
            String nomFichier = "rapport_carburant_" + periode.replace(" ", "_") + "_" + java.time.LocalDate.now() + ".pdf";
            fileChooser.setInitialFileName(nomFichier);
            
            // Obtenir la fenêtre principale de manière plus robuste
            Stage stage = null;
            
            // Méthode 1: Essayer d'obtenir la fenêtre via rootPane
            try {
                if (rootPane != null && rootPane.getScene() != null && rootPane.getScene().getWindow() instanceof Stage) {
                    stage = (Stage) rootPane.getScene().getWindow();
                }
            } catch (Exception e) {
                System.out.println("Erreur lors de l'obtention de la fenêtre via rootPane: " + e.getMessage());
            }
            
            // Méthode 2: Si la première méthode échoue, utiliser la fenêtre principale
            if (stage == null) {
                try {
                    stage = javafx.stage.Stage.getWindows().stream()
                        .filter(w -> w instanceof Stage && w.isShowing())
                        .map(w -> (Stage) w)
                        .findFirst()
                        .orElse(null);
                } catch (Exception e) {
                    System.out.println("Erreur lors de l'obtention de la fenêtre principale: " + e.getMessage());
                }
            }
            
            // Méthode 3: Créer une nouvelle fenêtre si nécessaire
            if (stage == null) {
                stage = new Stage();
                stage.setTitle("Export PDF");
            }
            
            java.io.File fichier = fileChooser.showSaveDialog(stage);
            
            if (fichier != null) {
                String chemin = fichier.getAbsolutePath();
            rapportService.genererRapportPDF(ravits, chemin);
                util.AlertUtil.showAlert("Succès", 
                    "Rapport PDF généré avec succès !\n" +
                    "Période : " + periode + "\n" +
                    "Fichier : " + fichier.getName(), 
                    javafx.scene.control.Alert.AlertType.INFORMATION);
            }
        } catch (Exception e) {
            util.AlertUtil.showAlert("Erreur", "Échec lors de la génération du PDF : " + e.getMessage(), javafx.scene.control.Alert.AlertType.ERROR);
        }
    }

    // Navigation menu left
    @FXML private void versDashboard() { main.MainApp.showMainView(); }
    @FXML private void versVehicules() { main.MainApp.loadVehiculesView(); }
    @FXML private void versCarburant() { main.MainApp.loadCarburantView(); }
    @FXML private void versRapports() { main.MainApp.loadRapportsView(); }
    @FXML private void versImpression() { main.MainApp.loadImpressionView(); }
    @FXML private void deconnecter() { util.AppSession.deconnecter(); main.MainApp.showLoginView(); }
}