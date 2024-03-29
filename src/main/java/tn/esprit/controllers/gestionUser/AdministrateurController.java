package tn.esprit.controllers.gestionUser;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class AdministrateurController {
    @FXML
    private Label label;

    // Vos autres méthodes et gestionnaires d'événements ici...


    public void ToaddPage(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionUserRessources/creerCompte.fxml"));
            Parent root = loader.load();
            Scene pageScene = new Scene(root);

            // Get the current stage and set the new scene
            Stage stage = (Stage) label.getScene().getWindow();
            stage.setScene(pageScene);
            stage.show();
        } catch (
                IOException e) {
            System.err.println("Erreur lors du chargement de la page ");
            e.printStackTrace();
        }
    }

    public void reinitialisermdp(ActionEvent actionEvent) {
    }
}