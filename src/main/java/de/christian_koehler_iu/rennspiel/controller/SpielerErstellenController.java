package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.database.Spieler_database_connection;
import de.christian_koehler_iu.rennspiel.interfaces.I_spieler_database;
import de.christian_koehler_iu.rennspiel.utility.User_eingaben_pruefen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

/**
 * FX-Controller der für die View spieler_erstellen_view verantwortlich ist
 * hier hat der anwender die möglichkeit einen neuen spieler anzulegen
 */
public class SpielerErstellenController {

    @FXML
    private GridPane spielerErstellen_grid_root;
    @FXML
    private Label spielerErstellen_lb_fehler;
    @FXML
    private Button spielerErstellen_bn_zurueck;
    @FXML
    private Button spielerErstellen_bn_weiter;
    @FXML
    private TextField spielerErstellen_tf_spielerName;

    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/spieler_erstellen_view.fxml";
    public static final String SCENE_NAME = "Spieler erstellen";

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }

    public void spielerErstellen_bn_zurueck_action(ActionEvent actionEvent) {
        // zurück zur scene spieler_waehlen wechseln
        FXMLLoader loader = new FXMLLoader(getClass().getResource(SpielerWaehlenController.PATH_TO_FXML));
        try {
            Parent root = loader.load();
//        SpielerErstellenController spielerErstellenController = loader.getController();
//        spielerErstellenController.initialize_something();
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void spielerErstellen_bn_weiter_action(ActionEvent actionEvent) {
        // eingegebenen spielernamen überprüfen

        // eingabe aus textField holen
        final String benutzer_eingabe = this.spielerErstellen_tf_spielerName.getText();

        User_eingaben_pruefen userEingabenPruefen = new User_eingaben_pruefen(benutzer_eingabe);

        List<String> fehlermeldungen = userEingabenPruefen.get_fehlermeldungen_string_eingabe();

        // spieler-db-verbindung holen
        I_spieler_database i_spieler_database = new Spieler_database_connection();

        // testen, ob spieler_name schon existiert
        boolean is_spieler_schon_vorhanden = i_spieler_database.is_spieler_name_vorhanden(benutzer_eingabe);

        if(!is_spieler_schon_vorhanden && fehlermeldungen.isEmpty()){
            // kein fehler vorhanden -> spieler anlegen
            i_spieler_database.save_new_spieler(benutzer_eingabe);
            // wechseln zur scene spieler_waehlen
            ScenesManager.getInstance().switch_to_spieler_wahlen();
        }else{
            // fehler vorhanden
            String fehler_text;
            if(is_spieler_schon_vorhanden){
                // spieler_name schon vorhanden
                fehler_text = "Spieler mit dem Namen " + benutzer_eingabe + " schon vorhanden";
            }else{
                // sonstiger fehler bei der eingabe
                fehler_text = fehlermeldungen.getFirst();
            }
            // fehler_text in fehler_label einfügen
            spielerErstellen_lb_fehler.setText(fehler_text);
        }

    }
}

