package de.rennspiel_01.rennspiel_01;

import de.rennspiel_01.rennspiel_01.controller.ScenesManager;
import de.rennspiel_01.rennspiel_01.controller.StreckeGroesseWaehlenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        ScenesManager.getInstance(primaryStage);
    }

    public static void main(String[] args) {
        launch();
    }
}