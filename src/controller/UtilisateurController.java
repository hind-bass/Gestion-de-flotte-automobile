package controller;

import components.LogoImageView;
import main.MainApp;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox; // Assuming a VBox as root in login_view.fxml

import javafx.event.ActionEvent;

public class UtilisateurController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private LogoImageView appLogo; // fx:id for the LogoImageView
    @FXML private VBox loginContainer; // Assuming this is the root or a container for theming

    

    @FXML
    public void initialize() {
        // Charge le logo de manière programmatique, car 'imagePath' n'est pas une propriété FXML directe
        if (appLogo != null) {
            appLogo.setImage(new Image("/images/logo.png"));
        }
    }
    

     @FXML
    protected void handleLogin(ActionEvent event) {
        try {
            // Validation des identifiants
            if (authentificationReussie()) {
                MainApp.showMainView(); // Appel corrigé
            } else {
                showErrorAlert("Identifiants incorrects");
            }
        } catch (Exception e) {
            System.err.println("Erreur de connexion: " + e.getMessage());
            e.printStackTrace();
        }
    }
     @FXML
    private void handleLogout() {
        // Déconnexion
        MainApp.showLoginView();
    }
     @FXML
    private void handleDashboard() {
        MainApp.showDashboardView();
    }
    

    private boolean authentificationReussie() {
        // Implémentez votre logique d'authentification
        return true; // Temporaire
    }

    private void showErrorAlert(String identifiants_incorrects) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle("Erreur de connexion");
        alert.setHeaderText(null);
        alert.setContentText(identifiants_incorrects);
        alert.showAndWait();
    }
}