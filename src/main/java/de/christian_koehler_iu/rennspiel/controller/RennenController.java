package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.controller_helper.Link_StreckeErstellenController_Rennstrecke;
import de.christian_koehler_iu.rennspiel.controller_helper.Rennen_renn_verlauf;
import de.christian_koehler_iu.rennspiel.controller_helper.Rennen_start_positionen_waehlen;
import de.christian_koehler_iu.rennspiel.controller_helper.Rennen_strecke_zeichnen;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.interfaces.I_aufgabe_beendet;
import de.christian_koehler_iu.rennspiel.utility.Umrechnung_grid_pixel;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;


import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class RennenController {
    // class attributes
    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/rennen_view.fxml";
    public static final String SCENE_NAME = "Rennen";

    // object attributes
    @FXML
    GridPane rennen_grid_root;
    @FXML
    Label rennen_lb_streckenName;
    @FXML
    VBox rennen_vbox_info;
    @FXML
    Label rennen_lb_rennInfos;
    @FXML
    Group rennen_group_strecke;


    private final double B_MAX_PIXEL = 860.0;
    private final double H_MAX_PIXEL = 530.0;

    private Spieler aktiver_spieler;
    private Rennstrecke rennstrecke;
    private final ArrayList<Spieler> spieler = new ArrayList<>();
    private Link_StreckeErstellenController_Rennstrecke link_StreckeErstellenController_Rennstrecke;
//    private final Paint[] farben = {Color.BLUE,Color.RED, Color.ORANGE, Color.GREEN};
    private final String[] farben = {"blue", "red", "orange", "green"};
    private final HashMap<Spieler, String> zurordnung_spieler_farbe = new HashMap<>();
    private final URL url_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css"));
    private Umrechnung_grid_pixel umrechnung_grid_pixel;


    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }


    public void initialize_spieler_rennstrecke_mitspieler(Spieler aktiver_spieler, Rennstrecke rennstrecke, ArrayList<Spieler> mitspieler) {
        // aktiver spieler in attribut speichern
        this.aktiver_spieler = aktiver_spieler;

        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // mitspieler in attribut speichern
        this.spieler.add(aktiver_spieler);
        this.spieler.addAll(mitspieler);

        // den spielern farben zuordnen
        if(spieler.size()>4){
            throw new RuntimeException("Es dürfen maximal 4 Spieler am Rennen teilnehmen!");
        }
        for(int akt_index=0 ; akt_index<spieler.size() ; akt_index++){
            zurordnung_spieler_farbe.put(spieler.get(akt_index), farben[akt_index]);
        }

        // infobox füllen
        rennen_lb_streckenName.setText(rennstrecke.getName());
        for(Spieler akt_spieler : spieler){
            Label akt_label = new Label();
            akt_label.getStylesheets().add(url_to_stylesheet.toExternalForm());
            akt_label.getStyleClass().add("label-large");
            akt_label.setText(akt_spieler.get_name());
            // Textfarbe über CSS-Stil überschreiben
            akt_label.setStyle("-fx-text-fill: " + zurordnung_spieler_farbe.get(akt_spieler));
            rennen_vbox_info.getChildren().add(akt_label);
        }

        // renn info label leeren
        rennen_lb_rennInfos.setText("");

        // umrechnung_grid_pixel initialisieren
        this.umrechnung_grid_pixel = new Umrechnung_grid_pixel(B_MAX_PIXEL, H_MAX_PIXEL, rennstrecke.getBreite(), rennstrecke.getHoehe());

        // rennstrecke zeichnen
        Rennen_strecke_zeichnen rennen_strecke_zeichnen = new Rennen_strecke_zeichnen(umrechnung_grid_pixel, rennstrecke, rennen_group_strecke);

        // startpositionen wählen
        start_positionen_waehlen();


        // rennen


        // rennnen beendet


    }

    private void start_positionen_waehlen(){
        Rennen_start_positionen_waehlen rennen_start_positionen_waehlen = new Rennen_start_positionen_waehlen(
                umrechnung_grid_pixel,
                rennstrecke,
                rennen_group_strecke,
                spieler,
                rennen_lb_rennInfos,
                zurordnung_spieler_farbe,
                new I_aufgabe_beendet() {
                    @Override
                    public void aufgabe_beendet() {
                        // startpositionen sind alle erfolgreich gewählt
                        // weiter mit rennen starten
                        rennen_starten();
                    }
                });
    }

    private void rennen_starten(){
        Rennen_renn_verlauf rennen_renn_verlauf = new Rennen_renn_verlauf();
    }


















    public void rennen_group_strecke_mDragged(MouseEvent mouseEvent) {
    }


    public void rennen_group_strecke_mPressed(MouseEvent mouseEvent) {
    }


    public void rennen_group_mReleased(MouseEvent mouseEvent) {
    }


    public void rennen_bn_rennenBeenden_action(ActionEvent actionEvent) {
    }
}
