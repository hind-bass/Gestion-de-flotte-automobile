package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Ravitaillement;
import service.ImpressionService;
import service.RavitaillementService;
import service.ImpressionPDFService;
import util.AlertUtil;
import java.io.File;

public class ImpressionController implements Initializable {

    @FXML private TableView<Ravitaillement> tableRavitaillements;
    @FXML private DatePicker dateDebutPicker;
    @FXML private DatePicker dateFinPicker;
    @FXML private Button btnImprimerRecu;
    @FXML private Button btnImprimerRapport;
    @FXML private Button btnSelectionnerFichier;
    @FXML private TextField cheminFichierField;
    @FXML private Label labelStatistiques;
    
    private final ImpressionService impressionService = new ImpressionService();
    private final ImpressionPDFService impressionPDFService = new ImpressionPDFService();
    private final RavitaillementService ravitaillementService = new RavitaillementService();
    private Ravitaillement ravitaillementSelectionne;
    private String cheminSauvegarde = System.getProperty("user.home") + "/Documents/";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        initialiserInterface();
        chargerRavitaillements();
        configurerTableau();
        configurerDatePickers();
    }

    private void initialiserInterface() {
        // Initialiser les dates
        dateDebutPicker.setValue(LocalDate.now().minusMonths(1));
        dateFinPicker.setValue(LocalDate.now());
        
        // Configurer le champ de chemin
        cheminFichierField.setText(cheminSauvegarde);
        cheminFichierField.setEditable(false);
        
        // Désactiver les boutons initialement
        btnImprimerRecu.setDisable(true);
        btnImprimerRapport.setDisable(true);
        
        // Charger tous les ravitaillements au démarrage
        chargerTousRavitaillements();
    }

    private void configurerTableau() {
        // Colonnes du tableau
        TableColumn<Ravitaillement, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("idRavitaillement"));
        colId.setPrefWidth(50);

        TableColumn<Ravitaillement, String> colDate = new TableColumn<>("Date");
        colDate.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getDateRavitaillement();
            return new javafx.beans.property.SimpleStringProperty(
                date.format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
            );
        });
        colDate.setPrefWidth(120);

        TableColumn<Ravitaillement, String> colConducteur = new TableColumn<>("Conducteur");
        colConducteur.setCellValueFactory(new PropertyValueFactory<>("conducteur"));
        colConducteur.setPrefWidth(120);

        TableColumn<Ravitaillement, String> colVehicule = new TableColumn<>("Véhicule");
        colVehicule.setCellValueFactory(cellData -> {
            String matricule = cellData.getValue().getVehicule() != null ? 
                cellData.getValue().getVehicule().getMatricule() : "N/A";
            return new javafx.beans.property.SimpleStringProperty(matricule);
        });
        colVehicule.setPrefWidth(100);

        TableColumn<Ravitaillement, Double> colLitres = new TableColumn<>("Litres");
        colLitres.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colLitres.setPrefWidth(80);

        TableColumn<Ravitaillement, Double> colPrix = new TableColumn<>("Prix/L");
        colPrix.setCellValueFactory(new PropertyValueFactory<>("prixUnitaire"));
        colPrix.setPrefWidth(80);

        TableColumn<Ravitaillement, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("montantTotal"));
        colTotal.setPrefWidth(100);

        // Ajouter les colonnes
        tableRavitaillements.getColumns().addAll(
            colId, colDate, colConducteur, colVehicule, colLitres, colPrix, colTotal
        );

        // Sélection d'une ligne
        tableRavitaillements.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                ravitaillementSelectionne = newSelection;
                btnImprimerRecu.setDisable(newSelection == null);
            }
        );
    }

    private void configurerDatePickers() {
        // Mettre à jour la liste quand les dates changent
        dateDebutPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateFinPicker.getValue() != null) {
                filtrerRavitaillements();
            }
        });

        dateFinPicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && dateDebutPicker.getValue() != null) {
                filtrerRavitaillements();
            }
        });
    }

    private void chargerRavitaillements() {
        try {
            List<Ravitaillement> ravitaillements = ravitaillementService.getAll();
            System.out.println("Nombre de ravitaillements chargés: " + ravitaillements.size());
            
            if (ravitaillements.isEmpty()) {
                System.out.println("Aucun ravitaillement trouvé dans la base de données");
                AlertUtil.showInfo("Information", "Aucun ravitaillement trouvé dans la base de données. Veuillez d'abord ajouter des ravitaillements.");
            }
            
            tableRavitaillements.setItems(FXCollections.observableArrayList(ravitaillements));
            filtrerRavitaillements();
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des ravitaillements: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Erreur lors du chargement des ravitaillements: " + e.getMessage());
        }
    }
    
    private void chargerTousRavitaillements() {
        try {
            List<Ravitaillement> ravitaillements = ravitaillementService.getAll();
            System.out.println("Chargement de tous les ravitaillements: " + ravitaillements.size());
            
            if (ravitaillements.isEmpty()) {
                System.out.println("Aucun ravitaillement trouvé dans la base de données");
                AlertUtil.showInfo("Information", "Aucun ravitaillement trouvé dans la base de données. Veuillez d'abord ajouter des ravitaillements dans la section 'Ravitaillements'.");
            } else {
                tableRavitaillements.setItems(FXCollections.observableArrayList(ravitaillements));
                mettreAJourStatistiques(ravitaillements);
                btnImprimerRapport.setDisable(false);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de tous les ravitaillements: " + e.getMessage());
            e.printStackTrace();
            AlertUtil.showError("Erreur", "Erreur lors du chargement des ravitaillements: " + e.getMessage());
        }
    }

    private void filtrerRavitaillements() {
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            System.out.println("Dates non sélectionnées, affichage de tous les ravitaillements");
            return;
        }

        LocalDateTime debut = dateDebutPicker.getValue().atStartOfDay();
        LocalDateTime fin = dateFinPicker.getValue().atTime(LocalTime.MAX);
        
        System.out.println("Filtrage des ravitaillements du " + debut + " au " + fin);

        List<Ravitaillement> tous = ravitaillementService.getAll();
        System.out.println("Total de ravitaillements avant filtrage: " + tous.size());
        
        List<Ravitaillement> filtres = tous.stream()
            .filter(r -> !r.getDateRavitaillement().isBefore(debut) && 
                        !r.getDateRavitaillement().isAfter(fin))
            .collect(java.util.stream.Collectors.toList());
            
        System.out.println("Nombre de ravitaillements après filtrage: " + filtres.size());

        tableRavitaillements.setItems(FXCollections.observableArrayList(filtres));
        mettreAJourStatistiques(filtres);
    }

    private void mettreAJourStatistiques(List<Ravitaillement> ravitaillements) {
        if (ravitaillements.isEmpty()) {
            labelStatistiques.setText("Aucun ravitaillement trouvé pour la période sélectionnée");
            btnImprimerRapport.setDisable(true);
            return;
        }

        double totalLitres = ravitaillements.stream().mapToDouble(Ravitaillement::getQuantite).sum();
        double totalMontant = ravitaillements.stream().mapToDouble(Ravitaillement::getMontantTotal).sum();
        double prixMoyen = totalMontant / totalLitres;

        String stats = String.format(
            "Période: %s au %s | Total: %d ravitaillements | %.2f litres | %.2f DH | %.2f DH/litre",
            dateDebutPicker.getValue().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            dateFinPicker.getValue().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            ravitaillements.size(),
            totalLitres,
            totalMontant,
            prixMoyen
        );

        labelStatistiques.setText(stats);
        btnImprimerRapport.setDisable(false);
    }

    @FXML
    private void imprimerRecu() {
        if (ravitaillementSelectionne == null) {
            AlertUtil.showError("Erreur", "Veuillez sélectionner un ravitaillement");
            return;
        }

        try {
            // Créer un dialogue pour choisir l'emplacement de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder le reçu PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );
            
            // Nom de fichier par défaut
            String nomFichier = "recu_ravitaillement_" + ravitaillementSelectionne.getIdRavitaillement() + "_" + 
                java.time.LocalDate.now() + ".pdf";
            fileChooser.setInitialFileName(nomFichier);
            fileChooser.setInitialDirectory(new File(cheminSauvegarde));
            
            // Obtenir la fenêtre principale
            Stage stage = (Stage) tableRavitaillements.getScene().getWindow();
            File fichier = fileChooser.showSaveDialog(stage);
            
            if (fichier != null) {
                String chemin = fichier.getAbsolutePath();
                impressionPDFService.genererRecuPDF(ravitaillementSelectionne, chemin);
                AlertUtil.showInfo("Succès", 
                    "Reçu PDF généré avec succès!\nFichier: " + fichier.getName() + "\nChemin: " + chemin);
                
                // Ouvrir le fichier
                ouvrirFichier(chemin);
            }
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors de la génération du reçu PDF: " + e.getMessage());
        }
    }

    @FXML
    private void imprimerRapport() {
        if (dateDebutPicker.getValue() == null || dateFinPicker.getValue() == null) {
            AlertUtil.showError("Erreur", "Veuillez sélectionner une période");
            return;
        }

        try {
            LocalDateTime debut = dateDebutPicker.getValue().atStartOfDay();
            LocalDateTime fin = dateFinPicker.getValue().atTime(LocalTime.MAX);

            List<Ravitaillement> ravitaillements = tableRavitaillements.getItems();
            
            if (ravitaillements.isEmpty()) {
                AlertUtil.showError("Erreur", "Aucun ravitaillement trouvé pour la période sélectionnée");
                return;
            }

            // Créer un dialogue pour choisir l'emplacement de sauvegarde
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder le rapport PDF");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers PDF", "*.pdf")
            );
            
            // Nom de fichier par défaut
            String nomFichier = String.format("Rapport_Consommation_%s_%s.pdf",
                dateDebutPicker.getValue().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")),
                dateFinPicker.getValue().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd")));
            fileChooser.setInitialFileName(nomFichier);
            fileChooser.setInitialDirectory(new File(cheminSauvegarde));
            
            // Obtenir la fenêtre principale
            Stage stage = (Stage) tableRavitaillements.getScene().getWindow();
            File fichier = fileChooser.showSaveDialog(stage);
            
            if (fichier != null) {
                String chemin = fichier.getAbsolutePath();
                impressionPDFService.genererRapportPDF(ravitaillements, chemin);
                AlertUtil.showInfo("Succès", 
                    "Rapport PDF généré avec succès!\nFichier: " + fichier.getName() + "\nChemin: " + chemin);
                
                // Ouvrir le fichier
                ouvrirFichier(chemin);
            }
        } catch (Exception e) {
            AlertUtil.showError("Erreur", "Erreur lors de la génération du rapport PDF: " + e.getMessage());
        }
    }

    @FXML
    private void selectionnerDossier() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner le dossier de sauvegarde");
        fileChooser.setInitialDirectory(new File(cheminSauvegarde));
        
        // Sélectionner un dossier en sélectionnant un fichier dans le dossier
        File fichier = fileChooser.showOpenDialog(new Stage());
        if (fichier != null) {
            cheminSauvegarde = fichier.getParent() + "/";
            cheminFichierField.setText(cheminSauvegarde);
        }
    }

    private void ouvrirFichier(String cheminFichier) {
        try {
            if (System.getProperty("os.name").toLowerCase().contains("win")) {
                // Windows
                Runtime.getRuntime().exec("cmd /c start " + cheminFichier);
            } else if (System.getProperty("os.name").toLowerCase().contains("mac")) {
                // macOS
                Runtime.getRuntime().exec("open " + cheminFichier);
            } else {
                // Linux
                Runtime.getRuntime().exec("xdg-open " + cheminFichier);
            }
        } catch (Exception e) {
            // Si l'ouverture automatique échoue, on affiche juste le message de succès
        }
    }


    // Navigation menu left
    @FXML private void versDashboard() { main.MainApp.showMainView(); }
    @FXML private void versVehicules() { main.MainApp.loadVehiculesView(); }
    @FXML private void versCarburant() { main.MainApp.loadCarburantView(); }
    @FXML private void versRapports() { main.MainApp.loadRapportsView(); }
    @FXML private void versImpression() { main.MainApp.loadImpressionView(); }
    @FXML private void deconnecter() { util.AppSession.deconnecter(); main.MainApp.showLoginView(); }
    @FXML private void exporterPDF() { 
        AlertUtil.showAlert("Information", "Fonctionnalité d'export PDF en cours de développement.", Alert.AlertType.INFORMATION);
    }
}
