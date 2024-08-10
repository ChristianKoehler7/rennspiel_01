package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.database.Rennstrecke_database_connection;
import de.christian_koehler_iu.rennspiel.interfaces.I_rennstrecke_database;
import de.christian_koehler_iu.rennspiel.utility.User_eingaben_pruefen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;

public class StreckeErstellenController {
    @FXML
    private GridPane streckeErstellen_grid_root;
    @FXML
    private GridPane streckeErstellen_grid_rechts;
    @FXML
    private Group streckeErstellen_group_strecke;
    @FXML
    private ListView<String> streckeErstellen_listview_streckenlinien;
    @FXML
    private Label streckeErstellen_lb_streckengroesse;
    @FXML
    private Label streckeErstellen_lb_fehler;
    @FXML
    private Button streckeErstellen_bn_streckenLinieZeichnen;
    @FXML
    private Button streckeErstellen_bn_startLinieZeichnen;
    @FXML
    private Button streckeErstellen_bn_letzteStrLinieLoeschen;
    @FXML
    private Button streckeErstellen_bn_startRichtungAendern;
    @FXML
    private Button streckeErstellen_bn_streckeSpeichern;
    @FXML
    private TextField streckeErstellen_tf_anzRunden;


    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/strecke_erstellen_view.fxml";
    public static final String SCENE_NAME = "StreckeErstellen";

    private final String css_style_button_streckenLinie_pressed = "-fx-background-color: #ADD8E6;" + // Hellblauer Hintergrund
            "-fx-border-color: #0000FF;" +    // Blaue Umrandung
            "-fx-border-width: 2px;" +        // Dickere Umrandung
            "-fx-text-fill: #FFFFFF;";        // Weiße Schrift

    private final String css_style_button_startLinie_pressed = "-fx-background-color: #FF6347;" + // Tomatenroter Hintergrund
            "-fx-border-color: #FF0000;" +    // Rote Umrandung
            "-fx-border-width: 2px;" +        // Dickere Umrandung
            "-fx-text-fill: #FFFFFF;";        // Weiße Schrift

    private Link_StreckeErstellenController_Rennstrecke link_StreckeErstellenController_Rennstrecke;

    private final double B_MAX_PIXEL = 860.0;
    private final double H_MAX_PIXEL = 530.0;

    private Rennstrecke rennstrecke;

    private Spieler spieler;

    public enum Zeichnungszustand {
        STRECKENLINIE_ZEICHNEN,
        STARTLINIE_ZEICHNEN,
        KEINS_GEWAEHLT;
    }
    private Zeichnungszustand zeichnungszustand = Zeichnungszustand.KEINS_GEWAEHLT;

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }
    
    public void initialize_spieler_rennstrecke(Spieler spieler, Rennstrecke rennstrecke){
        // spieler in attribut speichern
        this.spieler = spieler;

        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // Link_StreckeErstellenController_Rennstrecke objekt erstellen
        this.link_StreckeErstellenController_Rennstrecke = new Link_StreckeErstellenController_Rennstrecke(
                B_MAX_PIXEL,
                H_MAX_PIXEL,
                new Link_StreckeErstellenController_Rennstrecke.I_Link_StrErstController_Rennstrecke() {
                    @Override
                    public Zeichnungszustand get_zeichnungszustand() {
                        return zeichnungszustand;
                    }

                    @Override
                    public Group get_group() {
                        return streckeErstellen_group_strecke;
                    }

                    @Override
                    public Rennstrecke get_rennstrecke() {
                        return rennstrecke;
                    }
                }
        );

        // labeltext setzen
        this.streckeErstellen_lb_streckengroesse.setText("Streckenname: " + rennstrecke.getName() + "\nBreite: " + rennstrecke.getBreite() + "\nHöhe: " + rennstrecke.getHoehe());

        // speicher für listview initialisieren
        this.streckeErstellen_listview_streckenlinien.setItems(this.link_StreckeErstellenController_Rennstrecke.get_observable_streckenlinien_for_listview());

        // ChangeListener wenn sich ausgewähltes item der listview ändert erstellen
        this.init_create_listView_selected_changeListener();
    }

    private void init_create_listView_selected_changeListener(){
        // ChangeListener wenn sich ausgewähltes item der listview ändert
        this.streckeErstellen_listview_streckenlinien.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) ->{
            // index der gewählten streckenlinie holen (wenn keins gewählt dann -1)
            int index_selected = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();

            // weitergeben an link_StreckeErstellenController_Rennstrecke
            this.link_StreckeErstellenController_Rennstrecke.listView_selected_changed(index_selected);
        });
    }

    public void streckeErstellen_bn_streckeSpeichern_action(ActionEvent actionEvent) {
        // test ob alle rennstreckenwerte vorhanden

        String eingabe_anz_runden = streckeErstellen_tf_anzRunden.getText();
        User_eingaben_pruefen user_eingaben_pruefen = new User_eingaben_pruefen(eingabe_anz_runden);

        if(rennstrecke.getStartlinie() == null){
            // startlinie fehlt
            ScenesManager.getInstance().show_toast_warning("Es muss eine Startlinie geben!");
        }else if(rennstrecke.getStartlinie().get_max_breite_oder_hoehe() < 3){
            // startlinie nicht lang genug
            ScenesManager.getInstance().show_toast_warning("Die Startlinie muss mindestens 3 Kästchen lang sein!");
        }else if(user_eingaben_pruefen.get_parsed_integer() == null){
            // falsche eingabe beim anzahl runden textfield
            ScenesManager.getInstance().show_toast_warning("Bitte im Feld \"Anzahl Runden\" eine positive Ganzzahl zwichen 1 und 5 eingeben!");
        }else if(user_eingaben_pruefen.get_parsed_integer() > 5){
            // anz runden darf nicht größer als 5 sein, das es größer 0 ist wurde schon in User_eingaben_pruefen gerprüft
            ScenesManager.getInstance().show_toast_warning("Anzahl Runden darf maximal 5 sein!");
        }else{ // keine fehler aufgetreten
            // anzahl runden der rennstrecke übergeben
            rennstrecke.set_anz_runden(user_eingaben_pruefen.get_parsed_integer());
            // rennstrecke in db speichern
            I_rennstrecke_database i_rennstrecke_database = new Rennstrecke_database_connection();
            i_rennstrecke_database.save_rennstrecke_complete(rennstrecke);
            // wechseln zu strecke_waehlen
            ScenesManager.getInstance().switch_to_strecke_waehlen(spieler);
            ScenesManager.getInstance().show_toast_neutral("Strecke erfolgreich erstellt!");
        }
    }

    public void streckeErstellen_bn_zurueck_action(ActionEvent actionEvent) {
        ScenesManager.getInstance().switch_to_strecke_groesse_wahlen(spieler);
    }

    public void streckeErstellen_bn_gewStrLinieLoeschen_action(ActionEvent actionEvent) {
        // löscht die line, die im listview selected ist

        // index der gewählten linie holen (wenn nix selected dann -1)
        int index_selected_item = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();

        // streckenline löschen
        link_StreckeErstellenController_Rennstrecke.delete_streckenlinie(index_selected_item);
    }

    public void streckeErstellen_bn_streckenLinieZeichnen_action(ActionEvent actionEvent) {
        if(this.zeichnungszustand != Zeichnungszustand.STRECKENLINIE_ZEICHNEN){
            // bis jetzt war button nicht gedrückt
            this.zeichnungszustand = Zeichnungszustand.STRECKENLINIE_ZEICHNEN;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle(this.css_style_button_streckenLinie_pressed);
            this.streckeErstellen_bn_startLinieZeichnen.setStyle("");
        }else{ // button war gedrückt
            this.zeichnungszustand = Zeichnungszustand.KEINS_GEWAEHLT;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle("");
            this.streckeErstellen_bn_startLinieZeichnen.setStyle("");
        }
    }

    public void streckeErstellen_bn_startLinieZeichnen_action(ActionEvent actionEvent) {
        if(this.zeichnungszustand != Zeichnungszustand.STARTLINIE_ZEICHNEN){
            // bis jetzt war button nicht gedrückt
            this.zeichnungszustand = Zeichnungszustand.STARTLINIE_ZEICHNEN;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle("");
            this.streckeErstellen_bn_startLinieZeichnen.setStyle(this.css_style_button_startLinie_pressed);
        }else{ // button war gedrückt
            this.zeichnungszustand = Zeichnungszustand.KEINS_GEWAEHLT;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle("");
            this.streckeErstellen_bn_startLinieZeichnen.setStyle("");
        }
    }

    public void streckeErstellen_bn_letzteStrLinieLoeschen_action(ActionEvent actionEvent) {
        this.link_StreckeErstellenController_Rennstrecke.delete_last_streckenlinie();
    }

    public void streckeErstellen_bn_startRichtungAendern_action(ActionEvent actionEvent) {
        this.link_StreckeErstellenController_Rennstrecke.start_richtung_aendern();
    }

    public void streckeErstellen_group_strecke_mPressed(MouseEvent mouseEvent) {
        this.link_StreckeErstellenController_Rennstrecke.group_mPressed(mouseEvent);
    }

    public void streckeErstellen_group_strecke_mDragged(MouseEvent mouseEvent){
        this.link_StreckeErstellenController_Rennstrecke.group_mDraged(mouseEvent);
    }

    public void streckeErstellen_group_mReleased(MouseEvent mouseEvent){
        this.link_StreckeErstellenController_Rennstrecke.group_mReleased(mouseEvent);
    }
}