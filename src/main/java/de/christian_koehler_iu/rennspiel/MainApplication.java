package de.christian_koehler_iu.rennspiel;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import de.christian_koehler_iu.rennspiel.controller.SpielerWaehlenController;
import de.christian_koehler_iu.rennspiel.controller.StreckeGroesseWaehlenController;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // scene manager instance erstellen
        ScenesManager scenesManager = ScenesManager.getInstance(primaryStage);
        scenesManager.switch_to_strecke_erstellen(new Rennstrecke("Test", 30, 20));

//        // mainScene laden
//        FXMLLoader fxmlLoader = new FXMLLoader(ScenesManager.class.getResource(SpielerWaehlenController.PATH_TO_FXML));
//        Scene scene = null;
//        try {
//            scene = new Scene(fxmlLoader.load());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        //StreckeGroesseWaehlenController streckeGroesseWaehlenController = fxmlLoader.getController();
//        primaryStage.setTitle("Rennspiel");
//        primaryStage.setWidth(1280); // Breite des Fensters
//        primaryStage.setHeight(800); // Höhe des Fensters
//        // Fenstergröße fixieren
//        primaryStage.setResizable(false);
//        primaryStage.setScene(scene);
//        primaryStage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}