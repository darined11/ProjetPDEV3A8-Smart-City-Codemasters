package tn.esprit.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.esprit.entities.Medecin;
import tn.esprit.entities.RendezVous;
import tn.esprit.services.ServiceMedecin;
import tn.esprit.services.ServiceRendezVous;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class AfficherListRendezVousController {
    public ListView<RendezVous> listViewRendezVous;
    @FXML
    public Label labelListRV;
    RendezVous currentRendezVousSelected;
    private final ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    public void initialize() {
        ObservableList<RendezVous> listRndezVous = FXCollections.observableArrayList();
        listViewRendezVous.setItems(listRndezVous);
        try {
            List<RendezVous> rendezvousFromService = serviceRendezVous.afficher();
            listRndezVous.addAll(rendezvousFromService);

            // Set a custom CellFactory for the ListView
            listViewRendezVous.setCellFactory(new Callback<ListView<RendezVous>, ListCell<RendezVous>>() {
                @Override
                public ListCell<RendezVous> call(ListView<RendezVous> param) {
                    return new ListCell<RendezVous>() {
                        @Override
                        protected void updateItem(RendezVous rendezVous, boolean empty) {
                            super.updateItem(rendezVous, empty);
                            if (empty || rendezVous == null) {
                                setText(null);
                                setGraphic(null);
                            } else {
                                // Customize the display of the RendezVous object
                                ServiceMedecin sm = new ServiceMedecin();
                                Medecin medecin = sm.getMedecinById(rendezVous.getId_medecin());
                                String doctorName = medecin.getNom_medecin();
                                String doctorSurname = medecin.getPrenom_medecin_medecin();
                                String specialty = medecin.getSpecialite_medecin();
                                int phoneNumber = medecin.getNumero_telephone_medecin();
                                String address = medecin.getAddress_medecin();
                                Timestamp timestamp = rendezVous.getDate_rendez_vous();
//                                "https://stackoverflow.com/questions/56990008/javafx-textfield-changing-padding-to-0-on-focus"
//                                change to display try this link "https://stackoverflow.com/questions/45144853/javafx-combobox-displayed-item-font-size"
                                DateFormat format = new SimpleDateFormat( "dd-MM-YYYY hh:mm " );
                                String timestampAsString = format.format( timestamp );
                                String formattedText = String.format("Dr. %s %s - %s Phone: %s Address: %s",
                                        doctorName, doctorSurname, timestampAsString, phoneNumber, address);
                                setText(formattedText);
                            }
                        }
                    };
                }
            });

            listViewRendezVous.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<RendezVous>() {
                @Override
                public void changed(ObservableValue<? extends RendezVous> observableValue, RendezVous oldRendezVous, RendezVous newRendezVous) {
                    currentRendezVousSelected = newRendezVous;
                    // Display the selected item
                    if (currentRendezVousSelected != null) {
                        // Assuming you have a Label to display the selected RendezVous
                        // selectedRendezVousLabel.setText(currentRendezVousSelected.toString());
                    } else {
                        // Assuming you have a Label to display the selected RendezVous
                        // selectedRendezVousLabel.setText("");
                    }
                }
            });

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void suprrimerRDBT(ActionEvent actionEvent) {
        try {
            serviceRendezVous.supprimer(currentRendezVousSelected.getRef_rendez_vous());
            initialize();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void ajouterRV(ActionEvent actionEvent) {

        try {
//           for load an FXML file and create a scene graph from it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterRendezVous.fxml"));
            Parent addPageRoot = loader.load();
            Scene newPageScene = new Scene(addPageRoot);
            // Get the current stage and set the new scene
            Stage stage = (Stage) labelListRV.getScene().getWindow();
            stage.setScene(newPageScene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public void BT_Modifier(ActionEvent actionEvent) {
        if(currentRendezVousSelected == null){
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Missing Information");
            alert.setHeaderText(null);
            alert.setContentText("Choose one Rv ");
            alert.showAndWait();
            return;
        }
        switchToUpdatePage();
    }
    public void switchToUpdatePage() {
        try {
//           for load an FXML file and create a scene graph from it
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MofidierRv.fxml"));
            Parent newPageRoot = loader.load();
            // Get the controller instance created by the FXMLLoader
            MofidierRvController mofidierRvController = loader.getController();
            mofidierRvController.initializeValues(currentRendezVousSelected.getRef_rendez_vous(),
                    currentRendezVousSelected.getDate_rendez_vous(),
                    currentRendezVousSelected.getId_medecin()
            );

            // Create a new scene with the newPageRoot
            Scene newPageScene = new Scene(newPageRoot);

            // Get the current stage and set the new scene
            Stage stage = (Stage) labelListRV.getScene().getWindow();
            stage.setScene(newPageScene);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
