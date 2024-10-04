package de.christian_koehler_iu.rennspiel.main;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Start der FX-Application
 */
public class MainApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        // scene manager instance erstellen
        ScenesManager scenesManager = ScenesManager.getInstance(primaryStage);
        // erste scene laden
        scenesManager.switch_to_spieler_wahlen();
//        Admin_functions admin_functions = new Admin_functions();
//        try {
//            admin_functions.export_default_strecken_to_csv();
//        } catch (SQLException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static void main(String[] args) {
        launch();
    }
}