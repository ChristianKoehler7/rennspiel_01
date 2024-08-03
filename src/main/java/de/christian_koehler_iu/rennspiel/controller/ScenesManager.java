package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

public class ScenesManager {

    // klassen attribute
    private static ScenesManager scenesManager;
    private static final int FENSTER_BREITE = 1280;
    private static final int FENSTER_HOEHE = 800;

    // objekt attribute
    private final Stage STAGE;

    // privater constructor
    private ScenesManager(Stage stage) {
        this.STAGE = stage;
        stage.setTitle("Rennspiel");
        stage.setWidth(ScenesManager.FENSTER_BREITE); // Breite des Fensters
        stage.setHeight(ScenesManager.FENSTER_HOEHE); // Höhe des Fensters
        stage.setResizable(false); // Fenstergröße fixieren
    }

    public static ScenesManager getInstance() {
        if(ScenesManager.scenesManager == null){
            throw new RuntimeException("Beim ersten Aufruf von ScenesManager.getInstance() muss eine Stage übergeben werden!");
        }
        return scenesManager;
    }

    public static ScenesManager getInstance(Stage stage) {
        if(ScenesManager.scenesManager == null){
            if(stage == null){
                throw new RuntimeException("Beim ersten Aufruf von getInstance darf stage nicht null sein!");
            }
            ScenesManager.scenesManager = new ScenesManager(stage);
        }
        return ScenesManager.scenesManager;
    }

    public void switch_to_spieler_wahlen(){
        switch_scene(SpielerWaehlenController.PATH_TO_FXML);
    }

    public void switch_to_spieler_erstellen(){
        switch_scene(SpielerErstellenController.PATH_TO_FXML);
    }

    public void switch_to_strecke_waehlen(){
        switch_scene(StreckeWaehlenController.PATH_TO_FXML);
    }

    public void switch_to_mitspieler_waehlen(){
        switch_scene(MitspielerWaehlenController.PATH_TO_FXML);
    }

    public void switch_to_strecke_groesse_wahlen(){
        switch_scene(StreckeGroesseWaehlenController.PATH_TO_FXML);
    }

    public void switch_to_strecke_erstellen(Rennstrecke rennstrecke){
        StreckeErstellenController streckeErstellenController = switch_scene(StreckeErstellenController.PATH_TO_FXML);
        streckeErstellenController.initialize_rennstrecke(rennstrecke);

    }

    public void switch_to_rennen(){
        switch_scene(RennenController.PATH_TO_FXML);
    }

    public void switch_to_rennen_beendet(Rennstrecke rennstrecke){
        switch_scene(StreckeErstellenController.PATH_TO_FXML);
    }

    private <T> T switch_scene(String path_to_fxml){
        URL urlFxml = getClass().getResource(path_to_fxml);
        Parent root = null;
        T controller = null;
        if(urlFxml != null){
            System.out.println("urlFxml: " + urlFxml.toString());
            FXMLLoader loader = new FXMLLoader(urlFxml);
            try {
                root = loader.load();
                controller = loader.getController();
                Scene scene = new Scene(root);
                this.STAGE.setScene(scene);
                this.STAGE.show();
            } catch (IOException e) {
                System.out.print("Fehler beim laden des FXMLLoaders\n" + e.toString());
            }

        }else{
            System.out.println("FXML file not found.");
        }

        return controller;
    }
}
