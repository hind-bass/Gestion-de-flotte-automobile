package main;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import service.DatabaseConnection;
import util.AppSession;

public class MainApp extends Application {
    private static Stage primaryStage;
    public static String currentTheme = "/styles/style.css"; // Thème par défaut (clair)

    public static void showDashboardView() {
        // Le Dashboard correspond à la vue principale
        showMainView();
    }

    // navigation helpers (defined later)

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;
            primaryStage.setTitle("Gestion des Carburants");

            // Charger l'icône
            try (InputStream iconStream = getClass().getResourceAsStream("/images/logo.png")) {
                if (iconStream != null) {
                    primaryStage.getIcons().add(new Image(iconStream));
                } else {
                    System.err.println("Logo non trouvé : /images/logo.png");
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement icône : " + e.getMessage());
            }

            // Afficher le SplashScreen (LoginView comme splash)
            showSplashScreen();

        } catch (Exception e) {
            showError("Erreur au démarrage", e);
        }
    }

    /**
     * Affiche un écran de démarrage (splash) pendant 2 secondes
     */
    private void showSplashScreen() {
        try {
            URL splashUrl = getClass().getResource("/view/SplashScreen.fxml");
            if (splashUrl == null) {
                System.err.println("SplashScreen (LoginView.fxml) introuvable !");
                showLoginView();
                return;
            }

            Parent splashRoot = FXMLLoader.load(splashUrl);
            Scene splashScene = new Scene(splashRoot);
            Stage splashStage = new Stage();
            splashStage.initStyle(StageStyle.UNDECORATED);
            splashStage.setScene(splashScene);
            splashStage.centerOnScreen();
            splashStage.show();

            // Après 3 secondes, fermer le splash et ouvrir le vrai login
            new Thread(() -> {
                try {
                    Thread.sleep(3000);
                    Platform.runLater(() -> {
                        splashStage.close();
                        showLoginView();
                    });
                } catch (InterruptedException e) {
                    Platform.runLater(() -> showLoginView());
                }
            }).start();

        } catch (IOException e) {
            System.err.println("Échec du SplashScreen : " + e.getMessage());
            showLoginView();
        }
    }
    // moved below
    // ————————————————————————
    // Méthodes de navigation
    // ————————————————————————

    public static void loadLoginView() {
        showLoginView();
    }

    public static void loadMainView() {
        showMainView();
    }

    public static void loadDashboardView() {
        showMainView(); // Dashboard = MainView
    }

    public static void showLoginView() {
        loadView("/view/LoginView.fxml", "/styles/style.css");
    }

    public static void showMainView() {
        loadView("/view/DashboardView.fxml", currentTheme);
    }

    // Centralized navigation
    public static void loadVehiculesView() {
        loadView("/view/GestionVehiculesView.fxml", currentTheme);
    }

    public static void loadCarburantView() {
        loadView("/view/GestionCarburantView.fxml", currentTheme);
    }

    public static void loadRapportsView() {
        loadView("/view/RapportsView.fxml", currentTheme);
    }
    
    public static void loadImpressionView() {
        loadView("/view/Impression.fxml", currentTheme);
    }

    // ————————————————————————
    // Gestion des thèmes
    // ————————————————————————

    public static void toggleTheme() {
        currentTheme = currentTheme.equals("/styles/style.css") 
            ? "/styles/dark.css" 
            : "/styles/style.css";

        if (primaryStage.getScene() != null) {
            applyTheme(primaryStage.getScene());
        }
    }

    /**
     * Charge une vue FXML et applique le style
     */
    private static void loadView(String fxmlPath, String cssPath) {
        try {
            URL fxmlUrl = MainApp.class.getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("FXML introuvable : " + fxmlPath);
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent root = loader.load();

            Scene scene = primaryStage.getScene();
            if (scene == null) {
                scene = new Scene(root);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }

            // Appliquer le CSS spécifique
            scene.getStylesheets().clear();
            URL cssUrl = MainApp.class.getResource(cssPath);
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                System.err.println("CSS non trouvé : " + cssPath);
            }

            // Appliquer le thème actuel
            applyTheme(scene);

            primaryStage.centerOnScreen();
            primaryStage.show();

        } catch (Exception e) {
            showError("Erreur chargement vue : " + fxmlPath, e);
        }
    }

    /**
     * Applique le thème actuel à la scène
     */
    public static void applyTheme(Scene scene) {
        scene.getStylesheets().removeIf(s -> s.contains("style.css") || s.contains("dark.css"));
        URL themeUrl = MainApp.class.getResource(currentTheme);
        if (themeUrl != null) {
            scene.getStylesheets().add(themeUrl.toExternalForm());
        }
    }

    // ————————————————————————
    // Utilitaires
    // ————————————————————————

    private static void showError(String message, Exception e) {
        System.err.println(message + " : " + e.getMessage());
        e.printStackTrace();
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    // ————————————————————————
    // Démarrage de l'application
    // ————————————————————————

    public static void main(String[] args) {
        // Initialiser la base de données avant JavaFX
        DatabaseConnection.getInstance();
        // Réinitialiser la session utilisateur
        AppSession.deconnecter();
        // Lancer l'application
        launch(args);
    }
}
