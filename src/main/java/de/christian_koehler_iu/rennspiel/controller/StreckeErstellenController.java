package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.utility.Link_StreckeErstellenController_Rennstrecke;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import de.christian_koehler_iu.rennspiel.datasets.Rennstrecke;

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
    private Button streckeErstellen_bn_streckenLinieZeichnen;
    @FXML
    private Button streckeErstellen_bn_startLinieZeichnen;
    @FXML
    private Button streckeErstellen_bn_letzteStrLinieLoeschen;
    @FXML
    private Button streckeErstellen_bn_startRichtungAendern;

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

    private final double B_MAX_PIXEL = 896.0-20.0;
    private final double H_MAX_PIXEL = 560.0-20.0;

    private Rennstrecke rennstrecke;


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

    
    
    public void initialize_rennstrecke(Rennstrecke rennstrecke){
        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // Link_StreckeErstellenController_Rennstrecke objekt erstellen
        this.link_StreckeErstellenController_Rennstrecke = new Link_StreckeErstellenController_Rennstrecke(
                this.B_MAX_PIXEL,
                this.H_MAX_PIXEL,
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
        //this.update_streckenLinien_listView();

        // ChangeListener wenn sich ausgewähltes item der listview ändert erstellen
        this.init_create_listView_selected_changeListener();
    }

    private void init_create_listView_selected_changeListener(){
        // ChangeListener wenn sich ausgewähltes item der listview ändert
        this.streckeErstellen_listview_streckenlinien.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) ->{
            // index der gewählten streckenlinie holen (wenn keins gewählt dann -1)
            int index_selected = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();

            // weitergebenn an link_StreckeErstellenController_Rennstrecke
            this.link_StreckeErstellenController_Rennstrecke.listView_selected_changed(index_selected);
        });
    }

    public void streckeErstellen_bn_zurueck_action(ActionEvent actionEvent) {
        ScenesManager.getInstance().goBack();
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