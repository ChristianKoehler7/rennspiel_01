package de.christian_koehler_iu.rennspiel;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import de.christian_koehler_iu.rennspiel.controller.SpielerWaehlenController;
import de.christian_koehler_iu.rennspiel.controller.StreckeGroesseWaehlenController;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.database.Admin_functions;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Stack;

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