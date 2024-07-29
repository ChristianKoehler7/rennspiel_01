package de.christian_koehler_iu.rennspiel;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import de.christian_koehler_iu.rennspiel.controller.SpielerWaehlenController;
import de.christian_koehler_iu.rennspiel.controller.StreckeGroesseWaehlenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Stack;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // mainScene laden
        FXMLLoader fxmlLoader = new FXMLLoader(ScenesManager.class.getResource(SpielerWaehlenController.PATH_TO_FXML));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //StreckeGroesseWaehlenController streckeGroesseWaehlenController = fxmlLoader.getController();
        primaryStage.setTitle("Rennspiel");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}