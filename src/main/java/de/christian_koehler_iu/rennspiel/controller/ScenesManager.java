package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.controller_helper.Spieler_fahrverlauf;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * singelton klasse, die sich um scene-wechsel kümmert und methoden zum anzeigen von dialogen und toasts enthält
 */
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

    public void switch_to_strecke_waehlen(Spieler spieler){
        StreckeWaehlenController streckeWaehlenController = switch_scene(StreckeWaehlenController.PATH_TO_FXML);
        streckeWaehlenController.initialize_spieler(spieler);
    }

    public void switch_to_mitspieler_waehlen(@NotNull Spieler spieler, @NotNull Rennstrecke rennstrecke){
        MitspielerWaehlenController mitspielerWaehlenController = switch_scene(MitspielerWaehlenController.PATH_TO_FXML);
        mitspielerWaehlenController.initialize_spieler_und_Rennstrecke(spieler, rennstrecke);
    }

    public void switch_to_strecke_groesse_wahlen(@NotNull Spieler spieler){
        StreckeGroesseWaehlenController streckeGroesseWaehlenController = switch_scene(StreckeGroesseWaehlenController.PATH_TO_FXML);
        streckeGroesseWaehlenController.initialize_spieler(spieler);
    }

    public void switch_to_strecke_erstellen(@NotNull Spieler spieler, @NotNull Rennstrecke rennstrecke){
        StreckeErstellenController streckeErstellenController = switch_scene(StreckeErstellenController.PATH_TO_FXML);
        streckeErstellenController.initialize_spieler_rennstrecke(spieler, rennstrecke);

    }

    public void switch_to_rennen(Rennstrecke rennstrecke, Spieler aktiver_spieler, ArrayList<Spieler> mitspieler){
        RennenController rennenController = switch_scene(RennenController.PATH_TO_FXML);
        rennenController.initialize_spieler_rennstrecke_mitspieler(aktiver_spieler, rennstrecke, mitspieler);
    }

    public void switch_to_rennen_beendet(
            Rennstrecke rennstrecke,
            Spieler aktiver_spieler,
            ArrayList<Spieler> mitspieler,
            HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf
            ){
        RennenBeendetController rennenBeendetController = switch_scene(RennenBeendetController.PATH_TO_FXML);
        rennenBeendetController.initialize_rennstrecke_spieler_mitspieler_fahrverlaeufe(
                rennstrecke,
                aktiver_spieler,
                mitspieler,
                zuordnung_spieler_spielerFahrverlauf
                );
    }

    /**
     * wechselt in die szene, die hinter dem pfad der eingabe steckt
     * @param path_to_fxml
     * @return controller-instanz der view zu der gewechselt wird
     * @param <T> typ der controller klasse
     */
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

    public void show_toast_neutral(String message) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        int toastDelay = 2500; // anzeige dauer
        int fadeInDelay = 500; // einblende dauer
        int fadeOutDelay = 500; // ausblende dauer

        Label label = new Label(message);
        label.setStyle("-fx-background-color: grey; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 50; -fx-border-radius: 50; -fx-border-width: 0; -fx-border-color: black;");
        label.setFont(new Font("Arial", 14));

        StackPane pane = new StackPane(label);
        pane.setStyle("-fx-background-radius: 50; -fx-border-radius: 50; ");
        pane.setOpacity(0);

        popup.getContent().add(pane);

        popup.setOnShown(e -> {
            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(fadeInDelay), pane);
            fadeInTransition.setFromValue(0);
            fadeInTransition.setToValue(1);
            fadeInTransition.play();

            new Thread(() -> {
                try {
                    Thread.sleep(toastDelay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(() -> {
                    FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(fadeOutDelay), pane);
                    fadeOutTransition.setFromValue(1);
                    fadeOutTransition.setToValue(0);
                    fadeOutTransition.setOnFinished(evt -> popup.hide());
                    fadeOutTransition.play();
                });
            }).start();
        });

        popup.show(STAGE);
    }

    public void show_toast_warning(String message) {
        Popup popup = new Popup();
        popup.setAutoFix(true);
        popup.setAutoHide(true);
        popup.setHideOnEscape(true);

        int toastDelay = 2500; // anzeige dauer
        int fadeInDelay = 500; // einblende dauer
        int fadeOutDelay = 500; // ausblende dauer

        Label label = new Label(message);
        label.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-padding: 10px; -fx-background-radius: 50; -fx-border-radius: 50; -fx-border-width: 0; -fx-border-color: red;");
        label.setFont(new Font("Arial", 14));

        StackPane pane = new StackPane(label);
        pane.setStyle("-fx-background-radius: 50; -fx-border-radius: 50; ");
        pane.setOpacity(0);

        popup.getContent().add(pane);

        popup.setOnShown(e -> {
            FadeTransition fadeInTransition = new FadeTransition(Duration.millis(fadeInDelay), pane);
            fadeInTransition.setFromValue(0);
            fadeInTransition.setToValue(1);
            fadeInTransition.play();

            new Thread(() -> {
                try {
                    Thread.sleep(toastDelay);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                Platform.runLater(() -> {
                    FadeTransition fadeOutTransition = new FadeTransition(Duration.millis(fadeOutDelay), pane);
                    fadeOutTransition.setFromValue(1);
                    fadeOutTransition.setToValue(0);
                    fadeOutTransition.setOnFinished(evt -> popup.hide());
                    fadeOutTransition.play();
                });
            }).start();
        });

        popup.show(STAGE);
    }

    public void show_dialog_neutral(String message_text, String ok_text, String cancel_text, I_dialog_actions i_dialog_actions){
        // wenn keine button texte mitgegeben wurden, dann auf standartwerte setzen
        if(ok_text.isEmpty()){
            ok_text = "OK";
        }
        if(cancel_text.isEmpty()){
            cancel_text = "Cancel";
        }

        // Erstellen des benutzerdefinierten Dialogs
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(STAGE);
        dialogStage.initStyle(StageStyle.TRANSPARENT);  // Entfernt die Titelleiste und ermöglicht Transparenz

        // Erstellen der Buttons
        Button okButton = new Button("button-weiter-ok");
        Button cancelButton = new Button("button-zurueck-abbrechen");

        // Anwenden der CSS-Stile auf die Buttons
        okButton.getStyleClass().add("button-vorsicht-beenden-loeschen"); // button style aus css-datei wählen
        cancelButton.getStyleClass().add("button-zurueck-abbrechen"); // button style aus css-datei wählen

        // Hinzufügen der Buttons zum Dialog
        HBox buttonBox = new HBox(10, cancelButton, okButton);
        buttonBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Label mit eingabe_text erstellen
        Label label = new Label(message_text);
        label.getStyleClass().add("label-large"); // button style aus css-datei wählen

        // Stil des gesamten Dialogs
        VBox dialogVBox = new VBox(10, label, buttonBox);
        String dialogVBox_style = "-fx-background-color: #FFFFFFF0; " + // Hintergrundfarbe weiß FFFFFF mit deckungskraft F0
                "-fx-padding: 20; " +               // Innenabstand
                "-fx-border-radius: 50; " +         // Abgerundete Ecken
                "-fx-background-radius: 50; " +     // Hintergrund-Eckenradius
                "-fx-border-width: 5; " +           // Rahmenbreite
                "-fx-border-color: black;" +      // Rahmenfarbe
                "-fx-alignment: center;";           // kindelemente sollen mittig sein
        dialogVBox.setStyle(dialogVBox_style);

        // Hinzufügen der Button-Aktionen
        okButton.setOnAction(e -> {
            i_dialog_actions.ok_action();
            dialogStage.close();
        });
        cancelButton.setOnAction(e -> {
            i_dialog_actions.cancel_action();
            dialogStage.close();
        });

        // Dialog anzeigen
        Scene dialogScene = new Scene(dialogVBox);
        // CSS-Datei laden
        dialogScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css")).toExternalForm());
        dialogScene.setFill(Color.TRANSPARENT);  // Transparenter Hintergrund für den Dialog
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    public void show_dialog_warning(String message_text, String ok_text, String cancel_text, I_dialog_actions i_dialog_actions){
        // wenn keine button texte mitgegeben wurden, dann auf standartwerte setzen
        if(ok_text.isEmpty()){
            ok_text = "OK";
        }
        if(cancel_text.isEmpty()){
            cancel_text = "Cancel";
        }

        // Erstellen des benutzerdefinierten Dialogs
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(STAGE);
        dialogStage.initStyle(StageStyle.TRANSPARENT);  // Entfernt die Titelleiste und ermöglicht Transparenz

        // Erstellen der Buttons
        Button okButton = new Button(ok_text);
        Button cancelButton = new Button(cancel_text);

        // Anwenden der CSS-Stile auf die Buttons
        okButton.getStyleClass().add("button-vorsicht-beenden-loeschen"); // button style aus css-datei wählen
        cancelButton.getStyleClass().add("button-zurueck-abbrechen"); // button style aus css-datei wählen

        // Hinzufügen der Buttons zum Dialog
        HBox buttonBox = new HBox(10, cancelButton, okButton);
        buttonBox.setStyle("-fx-padding: 20; -fx-alignment: center;");

        // Label mit eingabe_text erstellen
        Label label = new Label(message_text);
        label.getStyleClass().add("label-error-large"); // button style aus css-datei wählen

        // Stil des gesamten Dialogs
        VBox dialogVBox = new VBox(10, label, buttonBox);
        String dialogVBox_style = "-fx-background-color: #FFFFFFF0; " + // Hintergrundfarbe weiß FFFFFF mit deckungskraft F0
                "-fx-padding: 20; " +               // Innenabstand
                "-fx-border-radius: 50; " +         // Abgerundete Ecken
                "-fx-background-radius: 50; " +     // Hintergrund-Eckenradius
                "-fx-border-width: 5; " +           // Rahmenbreite
                "-fx-border-color: red;" +      // Rahmenfarbe
                "-fx-alignment: center;";           // kindelemente sollen mittig sein
        dialogVBox.setStyle(dialogVBox_style);

        // Hinzufügen der Button-Aktionen
        okButton.setOnAction(e -> {
            i_dialog_actions.ok_action();
            dialogStage.close();
        });
        cancelButton.setOnAction(e -> {
            i_dialog_actions.cancel_action();
            dialogStage.close();
        });

        // Dialog anzeigen
        Scene dialogScene = new Scene(dialogVBox);
        // CSS-Datei laden
        dialogScene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css")).toExternalForm());
        dialogScene.setFill(Color.TRANSPARENT);  // Transparenter Hintergrund für den Dialog
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
    }

    //--------------------------------------------------------------------------------------
    // interfaces
    //--------------------------------------------------------------------------------------
    public interface I_dialog_actions{
        void ok_action();
        void cancel_action();
    }
}
