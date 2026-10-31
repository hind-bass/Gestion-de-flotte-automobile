package util;

import javafx.scene.control.Alert;

public class AlertUtil {
    
    public static void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    public static void showInfo(String title, String message) {
        showAlert(title, message, Alert.AlertType.INFORMATION);
    }
    
    public static void showError(String title, String message) {
        showAlert(title, message, Alert.AlertType.ERROR);
    }
    
    public static void showWarning(String title, String message) {
        showAlert(title, message, Alert.AlertType.WARNING);
    }
    
    public static void showConfirmation(String title, String message) {
        showAlert(title, message, Alert.AlertType.CONFIRMATION);
    }
}