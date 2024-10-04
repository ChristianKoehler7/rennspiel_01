package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.database.Rennstrecke_database_connection;
import de.christian_koehler_iu.rennspiel.utility.User_eingaben_pruefen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

/**
 * FX-Controller der für die View strecke_groesse_waehlen_view verantwortlich ist
 * diese scene wird vor dem streckeneditor angezeigt, der anwender hat hier die möglichkeit folgendes zu wählen
 * - streckenname
 * - strecken_breite
 * - strecken_hoehe
 */
public class StreckeGroesseWaehlenController {
    // klassen attribute
    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/strecke_groesse_waehlen_view.fxml";
    public static final String SCENE_NAME = "StreckeGroesseWaehlen";

    // object attribute
    public final double RAND_X_GRID = 1.0; // anzahl kästchen rand links und rechts
    public final double RAND_Y_GRID = 1.0; // anzahl kästchen rand oben und unten
    public final double CLICK_TOLERANZ_GRID = 0.25; // abweichung click zu kästchenkreuz, sodass der click noch zählt
    private Spieler spieler;

    @FXML
    private GridPane streckeGroesse_grid_root;
    @FXML
    private Button streckeGroesse_bn_zurueck;
    @FXML
    private Button streckeGroesse_bn_weiter;
    @FXML
    private Label streckeGroesse_lb_fehler;
    @FXML
    private TextField streckeGroesse_tf_breite;
    @FXML
    private TextField streckeGroesse_tf_streckenname;
    @FXML
    private TextField streckeGroesse_tf_hoehe;

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }

    public void initialize_spieler(Spieler spieler){
        this.spieler = spieler;
    }


    public void streckeGroesse_bn_zurueck_action(ActionEvent actionEvent) {
        ScenesManager.getInstance().switch_to_strecke_waehlen(spieler);
    }

    public void streckeGroesse_bn_weiter_action(ActionEvent actionEvent) {
        // eingaben überprüfen
        String breite_eingabe = streckeGroesse_tf_breite.getText();
        String hoehe_eingabe = streckeGroesse_tf_hoehe.getText();
        String streckenname = streckeGroesse_tf_streckenname.getText();

        // streckenname prüfen
        // test ob erlaubte eingabe
        User_eingaben_pruefen user_eingaben_pruefen = new User_eingaben_pruefen(streckenname);
        List<String> fehlermeldungen = user_eingaben_pruefen.get_fehlermeldungen_string_eingabe();
        if(!fehlermeldungen.isEmpty()){
            streckeGroesse_lb_fehler.setText(fehlermeldungen.getFirst());
            return;
        }
        // test ob streckenname schon existiert
        Rennstrecke_database_connection rennstrecke_database_connection = new Rennstrecke_database_connection();
        ArrayList<String> vorhandene_strecken_namen = rennstrecke_database_connection.load_rennstrecke_namen();
        if(vorhandene_strecken_namen.contains(streckenname)){
            streckeGroesse_lb_fehler.setText("Streckenname ist schon vorhanden!");
            return;
        }

        // strings in int umwandeln
        int breite_int = 0;
        int hoehe_int = 0;
        try {
            breite_int = Integer.parseInt(breite_eingabe);
            hoehe_int = Integer.parseInt(hoehe_eingabe);
        } catch (NumberFormatException e) {
            streckeGroesse_lb_fehler.setText("Bitte nur positive Ganzzahlen eingeben!");
            return;
        }

        if(breite_int<=0 || hoehe_int<=0){
            streckeGroesse_lb_fehler.setText("Bitte nur positive Ganzzahlen (größer Null) eingeben!");
            return;
        }else if(streckenname==null || streckenname.isEmpty()) {
            streckeGroesse_lb_fehler.setText("Bitte einen Streckennamen eingeben!");
            return;
        }

        // bis jetzt nicht returned => kein fehler
        // rennstrecken-objekt erzeugen mit name und größe
        Rennstrecke rennstrecke = new Rennstrecke(streckenname, breite_int, hoehe_int);
        // zur scene strecke_erstellen wechseln
        ScenesManager.getInstance().switch_to_strecke_erstellen(spieler, rennstrecke);
    }
}