package tn.pidev.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/firstpage.fxml"));
        primaryStage.setTitle("E-city");
        primaryStage.setScene(new Scene(root,1300,770));
        primaryStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }
}
