package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class StreckeGroesseWaehlenController {


    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/strecke_groesse_waehlen_view.fxml";
    public static final String SCENE_NAME = "StreckeGroesseWaehlen";

    public final double RAND_X_GRID = 1.0; // anzahl kästchen rand links und rechts
    public final double RAND_Y_GRID = 1.0; // anzahl kästchen rand oben und unten

    public final double CLICK_TOLERANZ_GRID = 0.25; // abweichung click zu kästchenkreuz, sodass der click noch zählt

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


    public void streckeGroesse_bn_zurueck_action(ActionEvent actionEvent) {
        ScenesManager.getInstance().switch_to_strecke_waehlen();;
    }

    public void streckeGroesse_bn_weiter_action(ActionEvent actionEvent) {
        // eingaben überprüfen
        String breite_eingabe = streckeGroesse_tf_breite.getText();
        String hoehe_eingabe = streckeGroesse_tf_hoehe.getText();
        String streckenname = streckeGroesse_tf_streckenname.getText();

        int breite_int = 0;
        int hoehe_int = 0;

        // Strings in int umwandeln
        try {
            breite_int = Integer.parseInt(breite_eingabe);
            hoehe_int = Integer.parseInt(hoehe_eingabe);
        } catch (NumberFormatException e) {
            streckeGroesse_lb_fehler.setText("Bitte nur positive Ganzzahlen eingeben!");
        }

        if(breite_int<=0 || hoehe_int<=0){
            streckeGroesse_lb_fehler.setText("Bitte nur positive Ganzzahlen (größer Null) eingeben!");
        }else if(streckenname==null || streckenname.isEmpty()) {
            streckeGroesse_lb_fehler.setText("Bitte einen Streckennamen eingeben!");
        }else{
            // rennstrecken-objekt erzeugen mit name und größe
            Rennstrecke rennstrecke = new Rennstrecke(streckenname, breite_int, hoehe_int);
            // zur scene strecke_erstellen wechseln
            ScenesManager.getInstance().switch_to_strecke_erstellen(rennstrecke);
        }
    }
}