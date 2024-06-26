package tn.esprit.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;


import java.io.IOException;

public class MainFX extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
//    override the start method, which is the entry point for all JavaFX applications
    public void start(Stage primaryStage) {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gestionUserRessources/firstpage.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root);
        primaryStage.setTitle("E-City");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gestionUserRessources/icone_fenetre.png")));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
