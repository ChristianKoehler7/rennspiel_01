package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.datasets.Rennstrecke;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Stack;

public class ScenesManager {

    private final Stack<Scene> scene_stack;
    private final Stack<String> scene_names_stack;
    private final Stage STAGE;
    private static ScenesManager scenesManager;

    private ScenesManager(Stage stage) {
        this.STAGE = stage;
        this.scene_stack = new Stack<>();
        this.scene_names_stack = new Stack<>();
        // mainScene laden
        FXMLLoader fxmlLoader = new FXMLLoader(ScenesManager.class.getResource(StreckeGroesseWaehlenController.PATH_TO_FXML));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        StreckeGroesseWaehlenController streckeGroesseWaehlenController = fxmlLoader.getController();

        this.scene_stack.push(scene);
        this.scene_names_stack.push(StreckeGroesseWaehlenController.SCENE_NAME);
        stage.setTitle("Rennspiel");
        stage.setScene(scene);
        stage.show();
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
                throw new RuntimeException("Beim ersten Aufruf von getInstance darf stageOrNull nicht null sein!");
            }
            ScenesManager.scenesManager = new ScenesManager(stage);
        }
        return ScenesManager.scenesManager;
    }

    public void switch_to_streckeErstellen(Rennstrecke rennstrecke){
        URL urlFxml = getClass().getResource(StreckeErstellenController.PATH_TO_FXML);
        Parent root = null;
        if(urlFxml != null){
            System.out.println("urlFxml: " + urlFxml.toString());
            FXMLLoader loader = new FXMLLoader(urlFxml);
            try {
                root = loader.load();
                StreckeErstellenController streckeErstellenController = loader.getController();
                streckeErstellenController.initialize_rennstrecke(rennstrecke);
                Scene scene = new Scene(root);
                this.scene_stack.push(scene);
                this.scene_names_stack.push(StreckeErstellenController.SCENE_NAME);
                this.STAGE.setScene(scene);
                this.STAGE.show();
            } catch (IOException e) {
                System.out.print("Fehler beim laden des FXMLLoaders\n" + e.toString());
            }

        }else{
            System.out.println("FXML file not found.");
        }





    }

    public void goBack(){
        // aktuelle scene entfernen
        this.scene_stack.pop();
        // neue oberste scene (letzte scene) verwenden
        Scene lastScene = this.scene_stack.peek();
        this.STAGE.setScene(lastScene);
        this.scene_names_stack.pop();
        this.STAGE.show();
    }

    public String generate_verlauf_string(){
        StringBuilder verlauf = new StringBuilder();
        for(int i=0 ; i<this.scene_names_stack.size() ; i++){
            verlauf.append(" > ");
            verlauf.append(this.scene_names_stack.elementAt(i));
        }
        return verlauf.toString();
    }




}
