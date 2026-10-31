package controller;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import components.LogoImageView;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import model.Utilisateur;
import service.UtilisateurService;
import util.AlertUtil;
import util.AppSession;
import main.MainApp;
import javafx.application.Platform;

public class LoginController {

    @FXML private ImageView logoImageView;
    @FXML private ImageView loginImageView;
    @FXML private TextField loginField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorMessage;

    private final UtilisateurService utilisateurService = new UtilisateurService();

    @FXML
    private void initialize() {
        // Configurer l'image de connexion (station-service)
        Platform.runLater(() -> {
            try {
                // Charger l'image de la station-service
                if (loginImageView != null) {
                    Image stationImage = new Image("/images/login.png");
                    loginImageView.setImage(stationImage);
                    System.out.println("Image de station-service chargée avec succès !");
                }
            } catch (Exception e) {
                System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
                e.printStackTrace();
            }
            
            // Focus sur le champ de connexion
            loginField.requestFocus();
        });
    }

    @FXML
    private void handleLogin() {
        String login = loginField.getText().trim();
        String password = passwordField.getText();

        Utilisateur user = utilisateurService.authentifier(login, password);

        if (user != null) {
            AppSession.setUtilisateur(user); // Session démarrée
            MainApp.showMainView();
        } else {
            showError("Identifiants incorrects.");
        }
    }

    private void showError(String message) {
        errorMessage.setText(message);
        passwordField.clear();
    }

    private void closeLoginWindow() {
        Stage loginStage = (Stage) loginField.getScene().getWindow();
        loginStage.close();
    }

    private void openDashboard(Utilisateur user) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/view/DashboardView.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1000, 700);
            scene.getStylesheets().add(
                getClass().getResource("/styles/style.css").toExternalForm()
            );

            Stage stage = new Stage();
            stage.setTitle("Dashboard - Gestion Carburants");
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();

            // Optionnel : afficher un message de bienvenue
            System.out.println("Utilisateur connecté : " + user.getLogin() + " (Rôle : " + user.getRole() + ")");

        } catch (IOException e) {
            AlertUtil.showAlert("Erreur", "Impossible de charger l'interface principale.", Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }
}
