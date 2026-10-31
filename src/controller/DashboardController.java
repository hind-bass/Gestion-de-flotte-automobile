package controller;

import java.net.URL;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.animation.FadeTransition;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import model.Ravitaillement;
import service.RavitaillementService;
import util.AlertUtil;
import util.AppSession;
import main.MainApp;
import service.VehiculeService;
import model.Vehicule;

public class DashboardController implements Initializable {

    @FXML private BorderPane rootPane;
    @FXML private Label dateLabel;
    @FXML private Label userLabel;
    @FXML private Label coutTotalLabel;
    @FXML private Label consoMoyenneLabel;
    @FXML private Label nbRavitaillementsLabel;
    // Conteneurs pour graphiques (créés en code)
    @FXML private javafx.scene.layout.VBox coutsChartContainer;
    @FXML private javafx.scene.layout.VBox repartitionChartContainer;
    @FXML private javafx.scene.layout.VBox consoReelleChartContainer; 

    private final RavitaillementService ravitaillementService = new RavitaillementService();
    private final VehiculeService vehiculeService = new VehiculeService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        dateLabel.setText("Date : " + LocalDate.now());
        if (AppSession.getUtilisateur() != null) {
            userLabel.setText("Connecté : " + AppSession.getUtilisateur().getNom());
        } else {
            userLabel.setText("Connecté : Invité");
        }

        chargerKPIs();
        chargerGraphiques();
        animerApparition();
    }

    private void animerApparition() {
    FadeTransition ft = new FadeTransition(Duration.millis(600), rootPane);
    ft.setFromValue(0);
    ft.setToValue(1);
    ft.play();
}

    private void chargerKPIs() {
        var ravits = ravitaillementService.getAll();
        double total = ravits.stream().mapToDouble(Ravitaillement::getMontantTotal).sum();
        int nb = ravits.size();
        
        // Calcul de la consommation moyenne réelle (L/100km)
        double consoMoy = 0.0;
        if (ravits.size() >= 2) {
            // Trier par véhicule puis par date - CORRIGÉ pour gérer les véhicules null
            Map<Vehicule, List<Ravitaillement>> ravitsParVehicule = ravits.stream()
                .filter(r -> r.getVehicule() != null) // Filtrer les véhicules null
                .sorted(Comparator.comparing(Ravitaillement::getDateRavitaillement))
                .collect(Collectors.groupingBy(Ravitaillement::getVehicule));
            
            double totalConso = 0;
            int nbIntervalles = 0;
            
            for (List<Ravitaillement> liste : ravitsParVehicule.values()) {
                if (liste.size() < 2) continue;
                
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
            }
            
            if (nbIntervalles > 0) {
                consoMoy = totalConso / nbIntervalles;
            }
        }

        coutTotalLabel.setText(String.format("%.2f DH", total));
        nbRavitaillementsLabel.setText(String.valueOf(nb));
        consoMoyenneLabel.setText(String.format("%.2f L/100km", consoMoy));
    }

    private void chargerGraphiques() {
        try {
            List<Ravitaillement> ravits = ravitaillementService.getAll();
            
            // Évolution des coûts par mois (3 derniers mois)
            if (coutsChartContainer != null) {
                try {
                    LocalDate troisMoisAgo = LocalDate.now().minusMonths(3);
                    Map<String, Double> coutsParMois = ravits.stream()
                        .filter(r -> r.getDateRavitaillement() != null && r.getDateRavitaillement().toLocalDate().isAfter(troisMoisAgo)) // Filtrer les 3 derniers mois
                        .collect(Collectors.groupingBy(
                            r -> r.getDateRavitaillement().getMonth().toString().substring(0, 3),
                            Collectors.summingDouble(Ravitaillement::getMontantTotal)
                        ));
                    
                    CategoryAxis x = new CategoryAxis();
                    x.setLabel("Mois");
                    NumberAxis y = new NumberAxis();
                    y.setLabel("Coût (DH)");
                    LineChart<String, Number> chart = new LineChart<>(x, y);
                    chart.setLegendVisible(false);
                    chart.setTitle("Évolution des Coûts");
                    
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Coûts mensuels");
                    
                    // Trier par mois et ajouter les données
                    coutsParMois.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));
                    
                    chart.getData().add(series);
                    coutsChartContainer.getChildren().setAll(chart);
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement du graphique des coûts: " + e.getMessage());
                    coutsChartContainer.getChildren().setAll(new Label("Erreur de chargement du graphique"));
                }
            }

            // Répartition par véhicule (données réelles) - CORRIGÉ pour gérer les véhicules null
            if (repartitionChartContainer != null) {
                try {
                    Map<String, Double> repartitionParVehicule = ravits.stream()
                        .filter(r -> r.getVehicule() != null && r.getVehicule().getMatricule() != null) // Double vérification
                        .collect(Collectors.groupingBy(
                            r -> r.getVehicule().getMatricule(),
                            Collectors.summingDouble(Ravitaillement::getMontantTotal)
                        ));
                    
                    if (!repartitionParVehicule.isEmpty()) {
                        PieChart pie = new PieChart();
                        pie.setLegendVisible(true);
                        pie.setTitle("Répartition par Véhicule");
                        
                        repartitionParVehicule.forEach((matricule, montant) -> 
                            pie.getData().add(new PieChart.Data(matricule, montant))
                        );
                        
                        repartitionChartContainer.getChildren().setAll(pie);
                    } else {
                        repartitionChartContainer.getChildren().setAll(new Label("Aucune donnée disponible"));
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement du graphique de répartition: " + e.getMessage());
                    repartitionChartContainer.getChildren().setAll(new Label("Erreur de chargement du graphique"));
                }
            }
            
            // Consommation réelle par véhicule (données réelles) - CORRIGÉ pour gérer les véhicules null
            if (consoReelleChartContainer != null) {
                try {
                    CategoryAxis x2 = new CategoryAxis();
                    x2.setLabel("Véhicule");
                    NumberAxis y2 = new NumberAxis();
                    y2.setLabel("Consommation (L/100km)");
                    BarChart<String, Number> bar = new BarChart<>(x2, y2);
                    bar.setLegendVisible(false);
                    bar.setTitle("Consommation Réelle par Véhicule");
                    
                    XYChart.Series<String, Number> series = new XYChart.Series<>();
                    series.setName("Consommation (L/100km)");

                    Map<Vehicule, List<Ravitaillement>> parVehicule = ravits.stream()
                        .filter(r -> r.getVehicule() != null && r.getVehicule().getMatricule() != null) // Double vérification
                        .sorted(Comparator.comparing(Ravitaillement::getDateRavitaillement))
                        .collect(Collectors.groupingBy(Ravitaillement::getVehicule));

                    for (Map.Entry<Vehicule, List<Ravitaillement>> entry : parVehicule.entrySet()) {
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
                            series.getData().add(new XYChart.Data<>(v.getMatricule(), Math.round(consoMoyenne * 100.0) / 100.0));
                        }
                    }

                    if (!series.getData().isEmpty()) {
                        bar.getData().add(series);
                        consoReelleChartContainer.getChildren().setAll(bar);
                    } else {
                        consoReelleChartContainer.getChildren().setAll(new Label("Aucune donnée de consommation disponible"));
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du chargement du graphique de consommation: " + e.getMessage());
                    consoReelleChartContainer.getChildren().setAll(new Label("Erreur de chargement du graphique"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur générale lors du chargement des graphiques: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void versDashboard() {
        MainApp.showMainView();
    }

    @FXML
    private void versVehicules() {
        main.MainApp.loadVehiculesView();
    }

    @FXML
    private void versCarburant() {
        main.MainApp.loadCarburantView();
    }

    @FXML
    private void versRapports() {
        main.MainApp.loadRapportsView();
    }
    
    @FXML
    private void versImpression() {
        main.MainApp.loadImpressionView();
    }

    @FXML
    private void deconnecter() {
        AppSession.deconnecter();
        MainApp.showLoginView();
    }

    @FXML
    private void toggleTheme() {
        MainApp.toggleTheme();
    }

    private Vehicule trouverVehiculeParMatricule(String matricule) {
    return vehiculeService.getAll().stream()
        .filter(v -> v.getMatricule().equals(matricule))
        .findFirst()
        .orElse(null);
}

private void afficherDetailsVehicule(Vehicule v, double consoReelle) {
    Alert alert = new Alert(Alert.AlertType.INFORMATION);
    alert.setTitle("Détails du véhicule");
    alert.setHeaderText("Informations détaillées : " + v.getMatricule());

    String content = String.format(
        "Marque : %s\nModèle : %s\nType de carburant : %s\nKilométrage actuel : %d km\nConsommation théorique : %.2f L/100km\nConsommation réelle moyenne : %.2f L/100km",
        v.getMarque(),
        v.getModele(),
        v.getCarburant().getNomCarburant(),
        v.getKilometrageActuel(),
        v.getConsoTheorique(),
        consoReelle
    );

    alert.setContentText(content);
    alert.getDialogPane().setPrefWidth(400);
    alert.showAndWait();
}

    /**
* Charge le graphique de consommation réelle par véhicule
*/
private void chargerGraphiqueConsommation(List<Ravitaillement> ravits) {
    // Trier par véhicule puis par date
    Map<Vehicule, List<Ravitaillement>> ravitsParVehicule = ravits.stream()
        .sorted(Comparator.comparing(Ravitaillement::getDateRavitaillement))
        .collect(Collectors.groupingBy(Ravitaillement::getVehicule));

    XYChart.Series<String, Number> series = new XYChart.Series<>();
    series.setName("Consommation (L/100km)");

    for (Map.Entry<Vehicule, List<Ravitaillement>> entry : ravitsParVehicule.entrySet()) {
        Vehicule v = entry.getKey();
        List<Ravitaillement> liste = entry.getValue();

        if (liste.size() < 2) continue; // Besoin de 2 ravitaillements

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
            series.getData().add(new XYChart.Data<>(v.getMatricule(), Math.round(consoMoyenne * 100.0) / 100.0));
        }
    }

    if (consoReelleChartContainer != null) {
        Label ph = new Label("Consommation (L/100km) calculée pour " + ravits.size() + " enregistrements");
        consoReelleChartContainer.getChildren().setAll(ph);
    }
}

    @FXML
    private void exporterPDF() {
        try {
            javafx.stage.FileChooser chooser = new javafx.stage.FileChooser();
            chooser.setTitle("Enregistrer le rapport PDF");
            chooser.getExtensionFilters().add(new javafx.stage.FileChooser.ExtensionFilter("PDF", "*.pdf"));
            java.io.File file = chooser.showSaveDialog(main.MainApp.getPrimaryStage());
            if (file == null) return;

            java.util.List<Ravitaillement> ravits = ravitaillementService.getAll();
            new service.RapportService().genererRapportPDF(ravits, file.getAbsolutePath());
            AlertUtil.showAlert("Export", "PDF généré: " + file.getAbsolutePath(), Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            AlertUtil.showAlert("Erreur", "Export PDF échoué: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}