package de.christian_koehler_iu.rennspiel;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import javafx.application.Application;
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