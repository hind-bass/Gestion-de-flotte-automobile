package controller;

import java.net.URL;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import model.Vehicule;
import model.Carburant;
import model.Ravitaillement;
import service.VehiculeService;
import service.CarburantService;
import service.RavitaillementService;
import util.AlertUtil;

public class GestionVehiculesController implements Initializable {

    @FXML private TableView<Vehicule> tableVehicules;
    @FXML private TextField matriculeField;
    @FXML private TextField marqueField;
    @FXML private TextField modeleField;
    @FXML private ChoiceBox<Carburant> carburantChoiceBox;
    @FXML private TextField kmField;
    @FXML private TextField consoField;
    @FXML private Label totalVehiculesLabel;
    @FXML private TextField rechercheField;
    @FXML private VBox graphiqueBox;
    @FXML private VBox ravitaillementChartContainer;
    @FXML private javafx.scene.control.ScrollPane ravitaillementScrollContainer;

    private final VehiculeService vehiculeService = new VehiculeService();
    private final CarburantService carburantService = new CarburantService();
    private final RavitaillementService ravitaillementService = new RavitaillementService();
    private Vehicule vehiculeEnCours;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        chargerCarburants();
        chargerTable();
        reinitialiserFormulaire();
        rechercheField.textProperty().addListener((obs, old, newVal) -> rechercher());
    }

    private void chargerCarburants() {
        List<Carburant> carburants = carburantService.getAll();
        carburantChoiceBox.setItems(FXCollections.observableArrayList(carburants));
    }

    private void chargerTable() {
        List<Vehicule> vehicules = vehiculeService.getAll();
        tableVehicules.setItems(FXCollections.observableArrayList(vehicules));
        
        // Configuration des colonnes
        if (tableVehicules.getColumns().size() >= 7) {
            // 1. Matricule
            ((TableColumn<Vehicule, String>) tableVehicules.getColumns().get(0))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getMatricule()));

            // 2. Marque
            ((TableColumn<Vehicule, String>) tableVehicules.getColumns().get(1))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getMarque()));

            // 3. Modèle
            ((TableColumn<Vehicule, String>) tableVehicules.getColumns().get(2))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getModele()));

            // 4. Carburant
            ((TableColumn<Vehicule, String>) tableVehicules.getColumns().get(3))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    cd.getValue().getCarburant() != null ? cd.getValue().getCarburant().getNomCarburant() : "N/A"
                ));

            // 5. Kilométrage
            ((TableColumn<Vehicule, String>) tableVehicules.getColumns().get(4))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    String.format("%,d km", cd.getValue().getKilometrageActuel())
                ));

            // 6. Consommation
            ((TableColumn<Vehicule, String>) tableVehicules.getColumns().get(5))
                .setCellValueFactory(cd -> new ReadOnlyStringWrapper(
                    String.format("%.2f L/100km", cd.getValue().getConsoTheorique())
                ));

            // 7. Actions
            ((TableColumn<Vehicule, Void>) tableVehicules.getColumns().get(6))
                .setCellFactory(column -> new TableCell<Vehicule, Void>() {
                    private final Button editButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");
                    private final Button chartButton = new Button("Graphique");

                    {
                        // Style du bouton Modifier (jaune)
                        editButton.setStyle("-fx-background-color: #ffc107; -fx-text-fill: #000; -fx-padding: 5 10; -fx-border-radius: 3; -fx-font-weight: bold;");
                        
                        // Style du bouton Supprimer (rouge)
                        deleteButton.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 3; -fx-font-weight: bold;");
                        
                        // Style du bouton Graphique (bleu)
                        chartButton.setStyle("-fx-background-color: #007bff; -fx-text-fill: white; -fx-padding: 5 10; -fx-border-radius: 3; -fx-font-weight: bold;");
                        
                        editButton.setOnAction(e -> {
                            Vehicule vehicule = getTableView().getItems().get(getIndex());
                            editerVehicule(vehicule);
                        });
                        
                        deleteButton.setOnAction(e -> {
                            Vehicule vehicule = getTableView().getItems().get(getIndex());
                            supprimerVehicule(vehicule);
                        });
                        
                        chartButton.setOnAction(e -> {
                            Vehicule vehicule = getTableView().getItems().get(getIndex());
                            afficherGraphiqueConsommation(vehicule);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(new HBox(5, editButton, deleteButton, chartButton));
                        }
                    }
                });
        }
        
        // Mettre à jour le compteur
        totalVehiculesLabel.setText("(" + vehicules.size() + " véhicules)");
    }

    @FXML
    private void enregistrer() {
        try {
            if (matriculeField.getText().trim().isEmpty()) {
                AlertUtil.showError("Erreur", "Le matricule est obligatoire.");
                return;
            }

            Vehicule vehicule;
            boolean isModification = (vehiculeEnCours != null);
            
            if (isModification) {
                // Modification d'un véhicule existant
                vehicule = vehiculeEnCours;
            } else {
                // Création d'un nouveau véhicule
                vehicule = new Vehicule();
            }

            vehicule.setMatricule(matriculeField.getText().trim().toUpperCase());
            vehicule.setMarque(marqueField.getText().trim());
            vehicule.setModele(modeleField.getText().trim());
            vehicule.setCarburant(carburantChoiceBox.getValue());
            vehicule.setKilometrageActuel(parseInt(kmField.getText(), 0));
            vehicule.setConsoTheorique(parseDouble(consoField.getText(), 0.0));

            if (isModification) {
                vehiculeService.mettreAJour(vehicule);
                AlertUtil.showInfo("Succès", "Véhicule modifié avec succès.");
            } else {
                vehiculeService.ajouter(vehicule);
                AlertUtil.showInfo("Succès", "Véhicule ajouté avec succès.");
            }
            
            reinitialiserFormulaire();
            chargerTable();

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors de l'enregistrement : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void ajouter() {
        reinitialiserFormulaire();
        // Focus sur le premier champ pour faciliter la saisie
        matriculeField.requestFocus();
    }

    @FXML
    private void modifier() {
        if (vehiculeEnCours == null) {
            AlertUtil.showError("Erreur", "Aucun véhicule sélectionné.");
            return;
        }

        try {
            vehiculeEnCours.setMatricule(matriculeField.getText().trim().toUpperCase());
            vehiculeEnCours.setMarque(marqueField.getText().trim());
            vehiculeEnCours.setModele(modeleField.getText().trim());
            vehiculeEnCours.setCarburant(carburantChoiceBox.getValue());
            vehiculeEnCours.setKilometrageActuel(parseInt(kmField.getText(), 0));
            vehiculeEnCours.setConsoTheorique(parseDouble(consoField.getText(), 0.0));

            vehiculeService.mettreAJour(vehiculeEnCours);
            AlertUtil.showInfo("Succès", "Véhicule modifié avec succès.");
            reinitialiserFormulaire();
            chargerTable();

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors de la modification : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void annuler() {
        reinitialiserFormulaire();
    }

    private void reinitialiserFormulaire() {
        matriculeField.clear();
        marqueField.clear();
        modeleField.clear();
        carburantChoiceBox.setValue(null);
        kmField.clear();
        consoField.clear();
        vehiculeEnCours = null;
    }

    private void editerVehicule(Vehicule vehicule) {
        vehiculeEnCours = vehicule;
        matriculeField.setText(vehicule.getMatricule());
        marqueField.setText(vehicule.getMarque());
        modeleField.setText(vehicule.getModele());
        carburantChoiceBox.setValue(vehicule.getCarburant());
        kmField.setText(String.valueOf(vehicule.getKilometrageActuel()));
        consoField.setText(String.valueOf(vehicule.getConsoTheorique()));
    }

    private void supprimerVehicule(Vehicule vehicule) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer le véhicule");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer le véhicule " + vehicule.getMatricule() + " ?");

        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    vehiculeService.supprimer(vehicule.getMatricule());
                    AlertUtil.showInfo("Succès", "Véhicule supprimé avec succès.");
                    chargerTable();
                } catch (Exception e) {
                    AlertUtil.showError("Erreur", "Erreur lors de la suppression : " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void afficherGraphiqueConsommation(Vehicule vehicule) {
        try {
            List<Ravitaillement> ravitaillements = ravitaillementService.getAll().stream()
                .filter(r -> r.getVehicule() != null && r.getVehicule().getMatricule().equals(vehicule.getMatricule()))
                .collect(java.util.stream.Collectors.toList());
            
            if (ravitaillements.isEmpty()) {
                AlertUtil.showInfo("Information", "Aucun ravitaillement trouvé pour ce véhicule.");
                return;
            }

            // Afficher la zone de défilement
            graphiqueBox.setVisible(true);
            graphiqueBox.setManaged(true);
            
            // Nettoyer le conteneur et créer un nouveau contenu
            ravitaillementChartContainer.getChildren().clear();
            
            // Créer un VBox pour le contenu
            VBox contentBox = new VBox(10);
            contentBox.setStyle("-fx-padding: 15; -fx-spacing: 10;");
            
            // Titre
            Label titreLabel = new Label("📊 HISTORIQUE DES RAVITAILLEMENTS - " + vehicule.getMatricule());
            titreLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #356267; -fx-padding: 10 0;");
            
            // Informations générales
            Label infoLabel = new Label("📈 Nombre de ravitaillements: " + ravitaillements.size());
            infoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #666; -fx-font-weight: bold; -fx-padding: 5 0;");
            
            // Calculer la consommation moyenne
            double consommationMoyenne = 0.0;
            if (ravitaillements.size() > 1) {
                consommationMoyenne = calculerConsommationMoyenne(ravitaillements);
            }
            
            // Afficher la consommation moyenne
            Label consoLabel = new Label("⛽ Consommation moyenne: " + String.format("%.2f L/100km", consommationMoyenne));
            consoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #28a745; -fx-font-weight: bold; -fx-padding: 5 0;");
            
            // Créer un tableau simple pour l'historique
            javafx.scene.control.TableView<Ravitaillement> historiqueTable = new javafx.scene.control.TableView<>();
            historiqueTable.setPrefHeight(800);
            historiqueTable.setMinHeight(700);
            historiqueTable.setMaxHeight(900);
            historiqueTable.setStyle("-fx-background-color: white; -fx-border-color: #dee2e6; -fx-border-radius: 5; -fx-font-size: 14px;");
            
            // Colonnes du tableau
            javafx.scene.control.TableColumn<Ravitaillement, String> dateCol = new javafx.scene.control.TableColumn<>("📅 Date");
            dateCol.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                cd.getValue().getDateRavitaillement() != null ? 
                    cd.getValue().getDateRavitaillement().toLocalDate().toString() : "N/A"
            ));
            dateCol.setPrefWidth(180);
            dateCol.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-alignment: CENTER;");
            
            javafx.scene.control.TableColumn<Ravitaillement, String> quantiteCol = new javafx.scene.control.TableColumn<>("⛽ Quantité (L)");
            quantiteCol.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                String.format("%.2f", cd.getValue().getQuantite())
            ));
            quantiteCol.setPrefWidth(170);
            quantiteCol.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER; -fx-font-weight: bold;");
            
            javafx.scene.control.TableColumn<Ravitaillement, String> prixCol = new javafx.scene.control.TableColumn<>("💰 Prix (DH)");
            prixCol.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                String.format("%.3f", cd.getValue().getPrixUnitaire())
            ));
            prixCol.setPrefWidth(160);
            prixCol.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER; -fx-font-weight: bold;");
            
            javafx.scene.control.TableColumn<Ravitaillement, String> montantCol = new javafx.scene.control.TableColumn<>("💵 Montant (DH)");
            montantCol.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                String.format("%.2f", cd.getValue().getMontantTotal())
            ));
            montantCol.setPrefWidth(170);
            montantCol.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER; -fx-font-weight: bold; -fx-text-fill: #dc3545;");
            
            javafx.scene.control.TableColumn<Ravitaillement, String> kmCol = new javafx.scene.control.TableColumn<>("🛣️ Kilométrage");
            kmCol.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                String.format("%,d km", cd.getValue().getKilometrageReleve())
            ));
            kmCol.setPrefWidth(170);
            kmCol.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER; -fx-font-weight: bold;");
            
            javafx.scene.control.TableColumn<Ravitaillement, String> consoCol = new javafx.scene.control.TableColumn<>("📊 Consommation");
            consoCol.setCellValueFactory(cd -> {
                // Calculer la consommation si possible
                String consoText = "N/A";
                try {
                    int index = ravitaillements.indexOf(cd.getValue());
                    if (index > 0) {
                        Ravitaillement prev = ravitaillements.get(index - 1);
                        int kmParcourus = cd.getValue().getKilometrageReleve() - prev.getKilometrageReleve();
                        if (kmParcourus > 0) {
                            double conso = (cd.getValue().getQuantite() / kmParcourus) * 100;
                            consoText = String.format("%.2f L/100km", conso);
                        }
                    }
                } catch (Exception e) {
                    // Ignorer les erreurs de calcul
                }
                return new javafx.beans.property.ReadOnlyStringWrapper(consoText);
            });
            consoCol.setPrefWidth(200);
            consoCol.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER; -fx-text-fill: #28a745; -fx-font-weight: bold;");
            
            javafx.scene.control.TableColumn<Ravitaillement, String> lieuCol = new javafx.scene.control.TableColumn<>("🏪 Lieu");
            lieuCol.setCellValueFactory(cd -> new javafx.beans.property.ReadOnlyStringWrapper(
                cd.getValue().getLieu() != null ? cd.getValue().getLieu() : ""
            ));
            lieuCol.setPrefWidth(220);
            lieuCol.setStyle("-fx-font-size: 15px; -fx-alignment: CENTER; -fx-font-weight: bold;");
            
            historiqueTable.getColumns().addAll(dateCol, quantiteCol, prixCol, montantCol, kmCol, consoCol, lieuCol);
            
            // Trier les ravitaillements par date
            ravitaillements.sort((r1, r2) -> r1.getDateRavitaillement().compareTo(r2.getDateRavitaillement()));
            historiqueTable.setItems(javafx.collections.FXCollections.observableArrayList(ravitaillements));
            
            contentBox.getChildren().addAll(titreLabel, infoLabel, consoLabel, historiqueTable);
            
            // Configurer la ScrollPane avec le contenu
            ravitaillementScrollContainer.setContent(contentBox);
            ravitaillementScrollContainer.setFitToWidth(true);
            ravitaillementScrollContainer.setFitToHeight(true);

        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors du chargement de l'historique : " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private double calculerConsommationMoyenne(List<Ravitaillement> ravitaillements) {
        if (ravitaillements.size() < 2) return 0.0;
        
        try {
            // Trier par date
            ravitaillements.sort((r1, r2) -> r1.getDateRavitaillement().compareTo(r2.getDateRavitaillement()));
            
            double totalConso = 0.0;
            int nbCalculs = 0;
            
            for (int i = 1; i < ravitaillements.size(); i++) {
                Ravitaillement prev = ravitaillements.get(i-1);
                Ravitaillement curr = ravitaillements.get(i);
                
                // Utiliser le kilométrage relevé du ravitaillement, pas du véhicule
                int kmParcourus = curr.getKilometrageReleve() - prev.getKilometrageReleve();
                if (kmParcourus > 0) {
                    double conso = (curr.getQuantite() / kmParcourus) * 100;
                    totalConso += conso;
                    nbCalculs++;
                }
            }
            
            return nbCalculs > 0 ? totalConso / nbCalculs : 0.0;
        } catch (Exception e) {
            System.err.println("Erreur calcul consommation: " + e.getMessage());
            return 0.0;
        }
    }

    @FXML
    private void fermerGraphique() {
        graphiqueBox.setVisible(false);
        graphiqueBox.setManaged(false);
    }

    @FXML
    private void rechercher() {
        String recherche = rechercheField.getText().trim().toLowerCase();
        
        if (recherche.isEmpty()) {
            chargerTable();
            return;
        }

        List<Vehicule> vehicules = vehiculeService.getAll();
        List<Vehicule> vehiculesFiltres = vehicules.stream()
            .filter(v -> v.getMatricule().toLowerCase().contains(recherche) ||
                        v.getMarque().toLowerCase().contains(recherche) ||
                        v.getModele().toLowerCase().contains(recherche))
            .collect(java.util.stream.Collectors.toList());

        tableVehicules.setItems(FXCollections.observableArrayList(vehiculesFiltres));
        totalVehiculesLabel.setText("(" + vehiculesFiltres.size() + " véhicules trouvés)");
    }

    @FXML
    private void effacerRecherche() {
        rechercheField.clear();
        chargerTable();
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
