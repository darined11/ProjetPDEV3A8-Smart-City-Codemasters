package tn.esprit.controllers;
import javafx.scene.control.Alert;
import javafx.util.Callback;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import tn.esprit.entities.Medecin;
import tn.esprit.services.ServiceMedecin;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javafx.stage.Stage;

public class AfficherMedecinsController {

    @FXML
    public Label selectedDoctor;
    @FXML
    private ListView<Medecin> listViewMedecins;
    private final ServiceMedecin serviceMedecin = new ServiceMedecin();
    Medecin currentDoctorSelected;


    //    To can populate our list view with data
    /*public void initialize() {
        ObservableList<Medecin> medecinList = FXCollections.observableArrayList();
        listViewMedecins.setItems(medecinList);
        try {
            List<Medecin> medecinsFromService = serviceMedecin.afficher();
            medecinList.addAll(medecinsFromService);
            listViewMedecins.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldIndex, Number newIndex) {
                    currentDoctorSelected = listViewMedecins.getSelectionModel().getSelectedItem();
//                    display selected item
                    *//*if (currentDoctorSelected != null) {
                        selectedDoctor.setText(currentDoctorSelected.toString());
                    } else {
                        selectedDoctor.setText("");
                    }*//*
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }*/
    public void initialize() {
        ObservableList<Medecin> medecinList = FXCollections.observableArrayList();
        listViewMedecins.setItems(medecinList);
        try {
            List<Medecin> medecinsFromService = serviceMedecin.afficher();
            medecinList.addAll(medecinsFromService);

            // Set a custom CellFactory for the ListView
            listViewMedecins.setCellFactory(new Callback<ListView<Medecin>, ListCell<Medecin>>() {
                @Override
                public ListCell<Medecin> call(ListView<Medecin> param) {
                    return new ListCell<Medecin>() {
                        @Override
                        protected void updateItem(Medecin medecin, boolean empty) {
                            super.updateItem(medecin, empty);
                            if (empty || medecin == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                // Customize the display of the Medecin object
                                String doctorName = medecin.getNom_medecin();
                                String doctorSurname = medecin.getPrenom_medecin_medecin();
                                String specialty = medecin.getSpecialite_medecin();
                                int phoneNumber = medecin.getNumero_telephone_medecin();
                                String address = medecin.getAddress_medecin();
                                String formattedText = String.format("Dr. %s %s - %s Phone: %s Address: %s",
                                        doctorName, doctorSurname, specialty, phoneNumber, address);
                                setText(formattedText);
                            }
                        }
                    };
                }
            });

            listViewMedecins.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> observableValue, Number oldIndex, Number newIndex) {
                    currentDoctorSelected = listViewMedecins.getSelectionModel().getSelectedItem();
                    // Display the selected item
                    if (currentDoctorSelected != null) {
                        selectedDoctor.setText(currentDoctorSelected.getNom_medecin() + " - " + currentDoctorSelected.getSpecialite_medecin());
                    } else {
                        selectedDoctor.setText("");
                    }
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void BT_supprimer(ActionEvent actionEvent) {
        try {
            serviceMedecin.supprimer(currentDoctorSelected.getId_medecin());
            initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void switchToUpdatePage() {
        try {
//           for load an FXML file and create a scene graph from it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierMedecin.fxml"));
            Parent newPageRoot = loader.load();
            // Get the controller instance created by the FXMLLoader
            ModifierMedecinController modifierMedecinController = loader.getController();
            modifierMedecinController.initializeValues(currentDoctorSelected.getId_medecin(),
                    currentDoctorSelected.getNom_medecin(),
                    currentDoctorSelected.getPrenom_medecin_medecin(), currentDoctorSelected.getNumero_telephone_medecin(),
                    currentDoctorSelected.getAddress_medecin(), currentDoctorSelected.getSpecialite_medecin()
            );

            // Create a new scene with the newPageRoot
            Scene newPageScene = new Scene(newPageRoot);

            // Get the current stage and set the new scene
            Stage stage = (Stage) selectedDoctor.getScene().getWindow();
            stage.setScene(newPageScene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void switchToAddPage() {
        try {
//           for load an FXML file and create a scene graph from it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterMedecin.fxml"));
            Parent addPageRoot = loader.load();
            Scene newPageScene = new Scene(addPageRoot);
            newPageScene.getStylesheets().add(getClass().getResource("/stylesForGM.css").toExternalForm());

            // Get the current stage and set the new scene
            Stage stage = (Stage) selectedDoctor.getScene().getWindow();
            stage.setScene(newPageScene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void BT_Modifier(ActionEvent actionEvent) {
        if(currentDoctorSelected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Choose one doctor ");
            alert.showAndWait();
            return;
        }
        switchToUpdatePage();
    }


    public void SwitchToAdd(ActionEvent actionEvent) {
        switchToAddPage();
    }
}
