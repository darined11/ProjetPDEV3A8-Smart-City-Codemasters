package tn.esprit.controllers.ParkingControllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;

import java.io.IOException;


import javafx.stage.Stage;

import java.util.Objects;

public class NavBarController {


    @FXML
    private Pane contentPane;

    @FXML
    void afficherArticle() {
        navigateToPage("Article1.fxml");
    }
    @FXML
    void afficherMedecin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherMedecins.fxml"));
            Parent newPageRoot = loader.load();
            //AfficherMedecinsController afficherMedecinsController = loader.getController();

            // Create a new scene with the newPageRoot
            Scene pageScene = new Scene(newPageRoot);

            // Get the current stage and set the new scene
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(pageScene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @FXML
    void afficherRV() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherListRendezVous.fxml"));
            Parent newPageRoot = loader.load();
            //AfficherListRendezVousController afficherListRendezVousController = loader.getController();

            // Create a new scene with the newPageRoot
            Scene pageScene = new Scene(newPageRoot);

            // Get the current stage and set the new scene
            Stage stage = (Stage) contentPane.getScene().getWindow();
            stage.setScene(pageScene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void afficherAfficherArticle() {
        navigateToPage("AfficherArticle.fxml");
    }

    @FXML
    void afficherModifierArticle() {
        navigateToPage("AfficherArticle.fxml");
    }

    @FXML
    void afficherSupprimerArticle() {
        navigateToPage("SupprimerArticle.fxml");
    }

    private void navigateToPage(String fxmlFileName) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFileName));
            Parent newContentPane = loader.load();
            contentPane.getChildren().clear();
            contentPane.getChildren().add(newContentPane);
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de la page : " + fxmlFileName);
            e.printStackTrace();
        }
    }

    public void afficherParking(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/ParkingResources/AfficherParkingg.fxml")));
            contentPane.getScene().setRoot(root);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
