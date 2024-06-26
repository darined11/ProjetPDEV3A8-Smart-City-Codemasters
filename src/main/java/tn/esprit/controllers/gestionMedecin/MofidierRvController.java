package tn.esprit.controllers.gestionMedecin;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import tn.esprit.entities.gestionMedecin.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Stage;
import tn.esprit.entities.gestionUserEntities.User;
import tn.esprit.services.gestionMedecin.ServiceMedecin;
import tn.esprit.services.gestionMedecin.ServiceRendezVous;
import tn.esprit.services.gestionUserServices.ServiceUser;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MofidierRvController implements Initializable {
    public ComboBox<String> specialiteR;
    public ComboBox<Medecin> medecinR;
    public Label adresse_TextField;
    public Label n_TelTextField;
    public DatePicker dateR;
    public ComboBox<Integer> hourComboBox;
    public ComboBox<Integer> minuteComboBox;
    int ref_rendez_vous;
    int id_oldMedecin ;
    Timestamp oldDateRv ;
    ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    public void initializeValues(int ref_rendez_vous, Timestamp date_rendez_vous, int id_medecin) {
        id_oldMedecin = id_medecin;
        oldDateRv = date_rendez_vous;
        this.ref_rendez_vous = ref_rendez_vous;
        dateR.setValue(date_rendez_vous.toLocalDateTime().toLocalDate());
        hourComboBox.setValue(date_rendez_vous.getHours());
        minuteComboBox.setValue(date_rendez_vous.getMinutes());
        ServiceMedecin serviceMedecin = new ServiceMedecin();
        Medecin medecin = serviceMedecin.getMedecinById(id_medecin);
        n_TelTextField.setText(Integer.toString(medecin.getNumero_telephone_medecin()));
        adresse_TextField.setText(medecin.getAddress_medecin());
        specialiteR.setValue(medecin.getSpecialite_medecin());
        medecinR.setValue(medecin);

    }

    public void modiferRvBT(ActionEvent actionEvent) throws SQLException {
        // Check if the medecinR, specialiteR, hourComboBox, and minuteComboBox fields are filled
        if (medecinR.getValue() == null || specialiteR.getValue() == null || hourComboBox.getValue() == null || minuteComboBox.getValue() == null
                || dateR.getValue() == null) {
            // Show an alert if any field is empty
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Information manquante");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez remplir tous les champs.");
            alert.showAndWait();
        } else {
            LocalDate date = dateR.getValue(); // Get the date from the DatePicker
            int hour = hourComboBox.getValue(); // Get the hour from the ComboBox
            int minute = minuteComboBox.getValue(); // Get the minute from the ComboBox

            // Combine the date, hour, and minute into a LocalDateTime
            LocalDateTime dateTime = LocalDateTime.of(date, LocalTime.of(hour, minute));
            ServiceRendezVous serviceRendezVous = new ServiceRendezVous();
            List<LocalDateTime> listRVbyidMedecin = serviceRendezVous.getAllDateRendezVousByidMedeicn(medecinR.getValue().getId_medecin());
            if (listRVbyidMedecin.contains(dateTime)) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Le médecin a un rendez-vous à cette date.");
                alert.setHeaderText(null);
                alert.setContentText("Choisissez une autre date ou heure. ");
                alert.showAndWait();
                return;
            }
            if (dateTime.isBefore(LocalDateTime.now())) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Sélectionnez une date future ");
                alert.setHeaderText(null);
                alert.setContentText("Sélectionnez une date future");
                alert.showAndWait();

            } else {
                try {
                    Timestamp timestamp = Timestamp.valueOf(dateTime);
                    serviceRendezVous.modifier(ref_rendez_vous, timestamp, medecinR.getValue().id_medecin);
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");
                    ServiceMedecin serviceMedecin = new ServiceMedecin();
                    Medecin newMedecin = serviceMedecin.getMedecinById(medecinR.getValue().getId_medecin());
                    Medecin oldMedecin = serviceMedecin.getMedecinById(id_oldMedecin);
                    Medecin medecin = serviceMedecin.getMedecinById(medecinR.getValue().getId_medecin());;
                    if (id_oldMedecin != medecinR.getValue().id_medecin) {

                        TwilioSendSms twilioSendSms = new TwilioSendSms();
                        // for old doctor
                        String msgForOldDoctor = "Bonjour Dr. " + oldMedecin.getNom_medecin() + " Votre rendez-vous le " + oldDateRv.toLocalDateTime().format(myFormatObj) + " sera annulé";
//                        Message.creator(new PhoneNumber("+4915510686794"), new PhoneNumber(twilioSendSms.getFromNumberMyTwillioNumber()), msgForOldDoctor).create();
                        // for new doctor
                        String msgForNewDoctor = "Bonjour Dr. " + newMedecin.getNom_medecin() + " vous avez un rendez-vous le " + dateTime.format(myFormatObj);
//                        Message.creator(new PhoneNumber("+4915510686794"), new PhoneNumber(twilioSendSms.getFromNumberMyTwillioNumber()), msgForNewDoctor).create();
                    } else if (id_oldMedecin == medecinR.getValue().getId_medecin() && !oldDateRv.toLocalDateTime().equals(dateTime)) {
                        medecin = serviceMedecin.getMedecinById(medecinR.getValue().getId_medecin());
                        TwilioSendSms twilioSendSms = new TwilioSendSms();

                        // for old doctor
                        String msgForUpdatedDateToDoctor = "Bonjour Dr. " + medecin.getNom_medecin() + " Votre rendez-vous le " + oldDateRv.toLocalDateTime().format(myFormatObj) + " sera changé au " + dateTime.format(myFormatObj);
//                        Message.creator(new PhoneNumber("+4915510686794"), new PhoneNumber(twilioSendSms.getFromNumberMyTwillioNumber()), msgForUpdatedDateToDoctor).create();

                    }
                    // For email to client
                    ServiceUser serviceUser = new ServiceUser();
                    User client = serviceUser.getOneById(serviceRendezVous.getRendezVousByRefRv(ref_rendez_vous).getId_personne());
                    // 7ot email l client
                    String receiverAdresse = "tavef44143@aersm.com";
                    String subject = "Modifier Rendez-Vous";
                    String body = "Bonjour Mr." + client.getNom_personne() + " votre rendezVous sera le " + dateTime.format(myFormatObj) + "avec Dr." + medecin.getNom_medecin();
                    new SendEmail(receiverAdresse, subject, body);

                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Boîte de dialogue d'information");
                    alert.setContentText("Rendez-vous modifié avec succès !");
//          block the execution until the user closes the alert dialog.
                    alert.showAndWait();
                    switchToDisplayAllRV();

                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }


    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // For Calender
        dateFocus = ZonedDateTime.now();
        today = ZonedDateTime.now();
        try {
            drawCalendar();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Initialize ComboBox specialiteCombobox
        ServiceMedecin serviceMedecin = new ServiceMedecin();
        ObservableList<String> specialiteList = FXCollections.observableArrayList();
        specialiteR.setItems(specialiteList);
        try {
            specialiteList.addAll(serviceMedecin.getAllSpecialié());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        // Listen for changes in specialiteR selection
        specialiteR.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                // Call method to initialize medecinR based on the selected speciality
                initialiserComboboxMedecin();
                n_TelTextField.setText("N°Tel");
                adresse_TextField.setText("Adresse");
            }
        });
        // Initialize the hour ComboBox with values from  0 to  23
        ObservableList<Integer> hours = FXCollections.observableArrayList();
        for (int i = 8; i < 17; i++) {
            hours.add(i);
        }
        hourComboBox.setItems(hours);

        // Initialize the minute ComboBox with values from  0 to  59
        ObservableList<Integer> minutes = FXCollections.observableArrayList();

        for (int i = 0; i < 60; i += 30) {
            minutes.add(i);
        }
        minuteComboBox.setItems(minutes);
    }

    @FXML
    public void initialiserComboboxMedecin() {
        ServiceMedecin serviceMedecin = new ServiceMedecin();
        ObservableList<Medecin> medecinListBySpecialite = FXCollections.observableArrayList();
        medecinR.setItems(medecinListBySpecialite);
        medecinListBySpecialite.addAll(serviceMedecin.getMedecinBySpecialite(specialiteR.getValue()));

        // Display the medecinR ComboBox items using toStringWithSpeciality() method
        medecinR.setCellFactory(listView -> new ListCell<Medecin>() {
            @Override
            protected void updateItem(Medecin item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    n_TelTextField.setText("N°Tel");
                    adresse_TextField.setText("Adresse");
                } else {
                    setText(item.toStringNomPrenom());
                    n_TelTextField.setText(Integer.toString(item.getNumero_telephone_medecin()));
                    adresse_TextField.setText(item.getAddress_medecin());
                }
            }
        });

    }

    public void switchToDisplayAllRV() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionMedecin/AfficherListRendezVous.fxml"));
            Parent newPageRoot = loader.load();
            AfficherListRendezVousController afficherListRendezVousController = loader.getController();

            // Create a new scene with the newPageRoot
            Scene pageScene = new Scene(newPageRoot);

            // Get the current stage and set the new scene
            Stage stage = (Stage) adresse_TextField.getScene().getWindow();
            stage.setScene(pageScene);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    //    CalenderController ------------------------------------------------------------

    ZonedDateTime dateFocus;
    ZonedDateTime today;
    // year in above calendar
    @FXML
    private Text year;
    // Month in above calendar
    @FXML
    private Text month;

    @FXML
    private FlowPane calendar;


    @FXML
    void backOneMonth(ActionEvent event) {
        dateFocus = dateFocus.minusMonths(1);
        calendar.getChildren().clear();
        try {
            drawCalendar();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    void forwardOneMonth(ActionEvent event) {
        dateFocus = dateFocus.plusMonths(1);
        calendar.getChildren().clear();
        try {
            drawCalendar();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void drawCalendar() throws SQLException {
        // To fill the year and month textfield (above calendar) with the current year and month
        year.setText(String.valueOf(dateFocus.getYear()));
        month.setText(String.valueOf(dateFocus.getMonth()));

        double calendarWidth = calendar.getPrefWidth();
        double calendarHeight = calendar.getPrefHeight();
        double strokeWidth = 1;
        double spacingH = calendar.getHgap();
        double spacingV = calendar.getVgap();

        //List of activities for a given month
        Map<Integer, List<CalendarActivity>> calendarActivityMap = getCalendarActivitiesMonth(dateFocus);

        int monthMaxDate = dateFocus.getMonth().maxLength();
        //Check for leap year
        if(dateFocus.getYear() % 4 != 0 && monthMaxDate == 29){
            monthMaxDate = 28;
        }
        int dateOffset = ZonedDateTime.of(dateFocus.getYear(), dateFocus.getMonthValue(), 1,0,0,0,0,dateFocus.getZone()).getDayOfWeek().getValue();

        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                StackPane stackPane = new StackPane();
                Rectangle rectangle = new Rectangle();
                rectangle.setFill(Color.TRANSPARENT);
                rectangle.setStroke(Color.BLACK);
                rectangle.setStrokeWidth(strokeWidth);
                double rectangleWidth =(calendarWidth/7) - strokeWidth - spacingH;
                rectangle.setWidth(rectangleWidth);
                double rectangleHeight = (calendarHeight/6) - strokeWidth - spacingV;
                rectangle.setHeight(rectangleHeight);
                stackPane.getChildren().add(rectangle);

                int calculatedDate = (j+1)+(7*i);
                if(calculatedDate > dateOffset){
                    int currentDate = calculatedDate - dateOffset;
                    if(currentDate <= monthMaxDate){
                        Text date = new Text(String.valueOf(currentDate));
                        double textTranslationY = - (rectangleHeight / 2) * 0.75;
                        date.setTranslateY(textTranslationY);
                        stackPane.getChildren().add(date);

                        List<CalendarActivity> calendarActivities = calendarActivityMap.get(currentDate);
                        if(calendarActivities != null){
                            createCalendarActivity(calendarActivities, rectangleHeight, rectangleWidth, stackPane);
                        }
                    }
                    if(today.getYear() == dateFocus.getYear() && today.getMonth() == dateFocus.getMonth() && today.getDayOfMonth() == currentDate){
                        rectangle.setStroke(Color.BLUE);
                    }
                }
                calendar.getChildren().add(stackPane);
            }
        }
    }

    private void createCalendarActivity(List<CalendarActivity> calendarActivities, double rectangleHeight, double rectangleWidth, StackPane stackPane) {
        VBox calendarActivityBox = new VBox();
        for (int k = 0; k < calendarActivities.size(); k++) {
            if(k >= 2) {
                Text moreActivities = new Text("...");
                calendarActivityBox.getChildren().add(moreActivities);
                moreActivities.setOnMouseClicked(mouseEvent -> {
                    Popup popup = new Popup();
                    popup.setAutoHide(true); // The popup will automatically hide when clicked outside

                    // Create a ListView to display the activities
                    ListView<String> listView = new ListView<>();
                    for (CalendarActivity activity : calendarActivities) {
                        listView.getItems().add(activity.getClientName() + ", " + activity.getDate().toLocalTime());
                    }
                    listView.setCellFactory(param -> new ListCell<String>() {
                        @Override
                        protected void updateItem(String item, boolean empty) {
                            super.updateItem(item, empty);
                            if (item == null || empty) {
                                setText(null);
                            } else {
                                setText(item);
                            }
                        }
                    });

                    VBox popupVbox = new VBox(listView);
                    popup.getContent().add(popupVbox);

                    // Position the popup near the source of the click event
                    popup.show(moreActivities.getScene().getWindow(),
                            600,
                            300);
                });
                break;
            }
//            Text text = new Text(calendarActivities.get(k).getClientName() + ", " + calendarActivities.get(k).getDate().toLocalTime());
            Label text = new Label(calendarActivities.get(k).getClientName() + ", " + calendarActivities.get(k).getDate().toLocalTime());
            text.setPrefWidth(rectangleWidth); // Set the preferred width to match rectangleWidth
            text.setStyle("-fx-font-size:  9px; -fx-border-width: 1px ; -fx-border-color: blue; -fx-border-style: dashed"); // Set the font size




//            calendarActivityBox.getChildren().add(text);
            calendarActivityBox.getChildren().add(text);
            text.setOnMouseClicked(mouseEvent -> {
                //On Text clicked
                System.out.println(text.getText());
            });
        }
        calendarActivityBox.setTranslateY((rectangleHeight / 2) * 0.20);
        calendarActivityBox.setMaxWidth(rectangleWidth * 0.8);
        calendarActivityBox.setMaxHeight(rectangleHeight * 0.65);
        calendarActivityBox.setStyle("-fx-background-color:#F5F5DC");
        stackPane.getChildren().add(calendarActivityBox);
    }

    private Map<Integer, List<CalendarActivity>> createCalendarMap(List<CalendarActivity> calendarActivities) {
        Map<Integer, List<CalendarActivity>> calendarActivityMap = new HashMap<>();

        for (CalendarActivity activity: calendarActivities) {
            int activityDate = activity.getDate().getDayOfMonth();
            if(!calendarActivityMap.containsKey(activityDate)){
                calendarActivityMap.put(activityDate, List.of(activity));
            } else {
                List<CalendarActivity> OldListByDate = calendarActivityMap.get(activityDate);

                List<CalendarActivity> newList = new ArrayList<>(OldListByDate);
                newList.add(activity);
                calendarActivityMap.put(activityDate, newList);
            }
        }
        return  calendarActivityMap;
    }

    private Map<Integer, List<CalendarActivity>> getCalendarActivitiesMonth(ZonedDateTime dateFocus) throws SQLException {
        List<CalendarActivity> calendarActivities = new ArrayList<>();
        ServiceRendezVous serviceRendezVous = new ServiceRendezVous();
        ServiceMedecin serviceMedecin = new ServiceMedecin();
        List<RendezVous> listRendezvous = serviceRendezVous.afficher();
        int year = dateFocus.getYear();
        int month = dateFocus.getMonth().getValue();
        for(RendezVous rendezVous: listRendezvous){
            int yearRendezVous = rendezVous.getDate_rendez_vous().toLocalDateTime().getYear();
            int monthNumberRendezvous = rendezVous.getDate_rendez_vous().toLocalDateTime().getMonth().getValue();
            if(( yearRendezVous== year)&&( monthNumberRendezvous==month)){
                int dayOftheMonth = rendezVous.getDate_rendez_vous().toLocalDateTime().getDayOfMonth();
                int heureRv = rendezVous.getDate_rendez_vous().toLocalDateTime().getHour();
                int minuteRv = rendezVous.getDate_rendez_vous().toLocalDateTime().getMinute();
                Medecin medecin = serviceMedecin.getMedecinById(rendezVous.getId_medecin());
                String nomMedecin = "Dr."+medecin.getNom_medecin();

                ZonedDateTime time = ZonedDateTime.of(year, month, dayOftheMonth, heureRv,minuteRv,0,0,dateFocus.getZone());
                calendarActivities.add(new CalendarActivity(time, nomMedecin, 111111));
            }
        }
        return createCalendarMap(calendarActivities);
    }

    public void returnDisplayRv(ActionEvent actionEvent) {
        switchToDisplayAllRV();
    }
}

