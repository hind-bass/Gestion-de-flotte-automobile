package controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.util.Duration;
import model.Ravitaillement;
import model.Vehicule;
import model.CarteCarburant;
import service.RavitaillementService;
import service.VehiculeService;
import service.CarteCarburantService;
import util.NumberUtil;
import util.AlertUtil;

public class GestionCarburantController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ChoiceBox<Vehicule> vehiculeChoiceBox;
    @FXML private TextField conducteurField;
    @FXML private TextField lieuField;
    @FXML private TextField quantiteField;
    @FXML private TextField prixUnitaireField;
    @FXML private Label montantLabel;
    @FXML private TextField kmField;
    @FXML private ChoiceBox<CarteCarburant> carteChoiceBox;
    @FXML private TableView<Ravitaillement> tableRavitaillements;

    private final RavitaillementService ravitaillementService = new RavitaillementService();
    private final VehiculeService vehiculeService = new VehiculeService();
    private final CarteCarburantService carteService = new CarteCarburantService();
    private Ravitaillement ravitaillementSelectionne;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        datePicker.setValue(java.time.LocalDate.now());
        chargerVehicules();
        chargerCartes();
        chargerTable();

        quantiteField.textProperty().addListener((obs, old, newVal) -> recalculerMontant());
        prixUnitaireField.textProperty().addListener((obs, old, newVal) -> recalculerMontant());
        
        // Listener pour la sélection dans le tableau
        tableRavitaillements.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            ravitaillementSelectionne = newSelection;
        });
    }
    

    private void animerApparition() {
        Node rootPane = null;
        FadeTransition ft = new FadeTransition(Duration.millis(600), rootPane);
        ft.setFromValue(0);
        ft.setToValue(1);
        ft.play();
    }

    private void recalculerMontant() {
        double quantite = parseDouble(quantiteField.getText(), 0.0);
        double prix = parseDouble(prixUnitaireField.getText(), 0.0);
        double montant = quantite * prix;
        montantLabel.setText(NumberUtil.formatDH(montant));
    }

    private double parseDouble(String text, double defaultValue) {
        if (text == null || text.trim().isEmpty()) return defaultValue;
        try {
            return Double.parseDouble(text.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private int parseInt(String text, int defaultValue) {
        if (text == null || text.trim().isEmpty()) return defaultValue;
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void chargerVehicules() {
        List<Vehicule> vehicules = vehiculeService.getAll();
        vehiculeChoiceBox.setItems(FXCollections.observableArrayList(vehicules));
    }

    private void chargerCartes() {
        carteChoiceBox.getItems().clear();
        carteChoiceBox.getItems().add(null);
        List<CarteCarburant> cartes = carteService.getAll();
        carteChoiceBox.getItems().addAll(cartes);
        carteChoiceBox.setValue(null);
    }

    private void chargerTable() {
        List<Ravitaillement> list = ravitaillementService.getAll();
        var data = FXCollections.observableArrayList(list);
        tableRavitaillements.setItems(data);

        // Configuration des colonnes
        if (tableRavitaillements.getColumns().size() >= 7) {
            // 1. Date
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(0))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    cd.getValue().getDateRavitaillement() != null
                        ? cd.getValue().getDateRavitaillement().toLocalDate().toString()
                        : ""
                ));

            // 2. Véhicule
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(1))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    cd.getValue().getVehicule() != null ? cd.getValue().getVehicule().getMatricule() : "Inconnu"
                ));

            // 3. Conducteur
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(2))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    cd.getValue().getConducteur() != null ? cd.getValue().getConducteur() : ""
                ));

            // 4. Quantité
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(3))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    NumberUtil.formatNumber(cd.getValue().getQuantite(), 2) + " L"
                ));

            // 5. Prix unitaire
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(4))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    NumberUtil.formatNumber(cd.getValue().getPrixUnitaire(), 3) + " DH"
                ));

            // 6. Montant total
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(5))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    NumberUtil.formatDH(cd.getValue().getMontantTotal())
                ));

            // 7. Lieu
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(6))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    cd.getValue().getLieu() != null ? cd.getValue().getLieu() : ""
                ));

            // 8. Kilométrage
            ((TableColumn<Ravitaillement, String>) tableRavitaillements.getColumns().get(7))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    String.valueOf(cd.getValue().getKilometrageReleve())
                ));
        }
    }

    @FXML
    private void enregistrer() {
        try {
            if (vehiculeChoiceBox.getValue() == null) {
                AlertUtil.showError("Erreur", "Veuillez sélectionner un véhicule.");
                return;
            }

            Ravitaillement r;
            boolean isModification = (ravitaillementSelectionne != null);
            
            if (isModification) {
                // Modification d'un ravitaillement existant
                r = ravitaillementSelectionne;
            } else {
                // Création d'un nouveau ravitaillement
                r = new Ravitaillement();
            }

            r.setDateRavitaillement(LocalDateTime.of(datePicker.getValue(), java.time.LocalTime.now()));
            r.setVehicule(vehiculeChoiceBox.getValue());
            r.setConducteur(conducteurField.getText().trim());
            r.setLieu(lieuField.getText().trim());
            r.setQuantite(parseDouble(quantiteField.getText(), 0.0));
            r.setPrixUnitaire(parseDouble(prixUnitaireField.getText(), 0.0));
            r.setKilometrageReleve(parseInt(kmField.getText(), 0));
            r.setCarteCarburant(carteChoiceBox.getValue());

            if (isModification) {
                ravitaillementService.mettreAJour(r);
                AlertUtil.showInfo("Succès", "Ravitaillement modifié avec succès.");
            } else {
                ravitaillementService.ajouter(r);
                AlertUtil.showInfo("Succès", "Ravitaillement enregistré avec succès.");
            }
            
            reinitialiserFormulaire();
            chargerTable();

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Échec : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annuler() {
        reinitialiserFormulaire();
    }

    private void reinitialiserFormulaire() {
        datePicker.setValue(java.time.LocalDate.now());
        vehiculeChoiceBox.setValue(null);
        conducteurField.clear();
        lieuField.clear();
        quantiteField.clear();
        prixUnitaireField.clear();
        kmField.clear();
        carteChoiceBox.setValue(null);
        montantLabel.setText("0.00 DH");
        ravitaillementSelectionne = null;
        tableRavitaillements.getSelectionModel().clearSelection();
    }

    @FXML
    private void modifierSelection() {
        if (ravitaillementSelectionne == null) {
            AlertUtil.showError("Erreur", "Veuillez sélectionner un ravitaillement à modifier.");
            return;
        }

        try {
            // Remplir le formulaire avec les données du ravitaillement sélectionné
            datePicker.setValue(ravitaillementSelectionne.getDateRavitaillement().toLocalDate());
            vehiculeChoiceBox.setValue(ravitaillementSelectionne.getVehicule());
            conducteurField.setText(ravitaillementSelectionne.getConducteur());
            lieuField.setText(ravitaillementSelectionne.getLieu());
            quantiteField.setText(String.valueOf(ravitaillementSelectionne.getQuantite()));
            prixUnitaireField.setText(String.valueOf(ravitaillementSelectionne.getPrixUnitaire()));
            kmField.setText(String.valueOf(ravitaillementSelectionne.getKilometrageReleve()));
            carteChoiceBox.setValue(ravitaillementSelectionne.getCarteCarburant());
            montantLabel.setText(NumberUtil.formatDH(ravitaillementSelectionne.getMontantTotal()));

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors du chargement des données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void supprimerSelection() {
        if (ravitaillementSelectionne == null) {
            AlertUtil.showError("Erreur", "Veuillez sélectionner un ravitaillement à supprimer.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le ravitaillement");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce ravitaillement ?\n" +
                           "Date: " + ravitaillementSelectionne.getDateRavitaillement().toLocalDate() + "\n" +
                           "Véhicule: " + (ravitaillementSelectionne.getVehicule() != null ? ravitaillementSelectionne.getVehicule().getMatricule() : "N/A") + "\n" +
                           "Montant: " + NumberUtil.formatDH(ravitaillementSelectionne.getMontantTotal()));

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    ravitaillementService.supprimer(ravitaillementSelectionne.getIdRavitaillement());
                    AlertUtil.showInfo("Succès", "Ravitaillement supprimé avec succès.");
                    reinitialiserFormulaire();
                    chargerTable();
                } catch (Exception e) {
                    AlertUtil.showError("Erreur", "Erreur lors de la suppression : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    // Navigation menu left
    @FXML private void versDashboard() { main.MainApp.showMainView(); }
    @FXML private void versVehicules() { main.MainApp.loadVehiculesView(); }
    @FXML private void versCarburant() { main.MainApp.loadCarburantView(); }
    @FXML private void versRapports() { main.MainApp.loadRapportsView(); }
    @FXML private void versImpression() { main.MainApp.loadImpressionView(); }
    @FXML private void deconnecter() { util.AppSession.deconnecter(); main.MainApp.showLoginView(); }

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
