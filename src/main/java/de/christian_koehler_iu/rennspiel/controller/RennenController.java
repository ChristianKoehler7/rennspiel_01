package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.controller_helper.*;
import de.christian_koehler_iu.rennspiel.data_classes.Punkt;
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
import javafx.scene.shape.Circle;


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
    Label rennen_lb_anzStreckenRunden;
    @FXML
    Group rennen_group_strecke;
    @FXML
    GridPane rennen_grid_spieler_info;

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
    private final HashMap<Spieler, Rennen_spieler_info_labels> zurordnung_spieler_infoLabels = new HashMap<>();

    private Rennen_start_positionen_waehlen rennen_start_positionen_waehlen;


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

        // spieler info labels erstellen und der gridpane übergeben
        for(int akt_index=0 ; akt_index<spieler.size() ; akt_index++){
            int akt_grid_zeile = akt_index+1; // +1 weil die erste zeile für die überschrift ist

            // akt spieler holen
            Spieler akt_spieler = spieler.get(akt_index);

            // label erstellen mit helperclass Rennen_spieler_info_labels
            Rennen_spieler_info_labels rennen_spieler_info_labels =  new Rennen_spieler_info_labels(
                    akt_spieler.get_name(),
                    zurordnung_spieler_farbe.get(akt_spieler),
                    rennstrecke.get_anz_runden()
            );

            // Rennen_spieler_info_labels für den spieler in hashMap speichern
            zurordnung_spieler_infoLabels.put(akt_spieler, rennen_spieler_info_labels);

            // labels der grid übergeben
            rennen_grid_spieler_info.add(rennen_spieler_info_labels.get_lb_spieler_name(), 0, akt_grid_zeile);
            rennen_grid_spieler_info.add(rennen_spieler_info_labels.get_lb_spieler_runde(), 1, akt_grid_zeile);
            rennen_grid_spieler_info.add(rennen_spieler_info_labels.get_lb_spieler_zeit(), 2, akt_grid_zeile);
            rennen_grid_spieler_info.add(rennen_spieler_info_labels.get_lb_spieler_status(), 3, akt_grid_zeile);
        }

        // renn info label leeren
        rennen_lb_rennInfos.setText("");

        // streckenname setzen
        rennen_lb_streckenName.setText(rennstrecke.getName());

        // anz strecken runden anzeigen
        rennen_lb_anzStreckenRunden.setText(String.valueOf(rennstrecke.get_anz_runden()));

        // umrechnung_grid_pixel initialisieren
        this.umrechnung_grid_pixel = new Umrechnung_grid_pixel(B_MAX_PIXEL, H_MAX_PIXEL, rennstrecke.getBreite(), rennstrecke.getHoehe());

        // rennstrecke zeichnen
        Rennen_strecke_zeichnen rennen_strecke_zeichnen = new Rennen_strecke_zeichnen(umrechnung_grid_pixel, rennstrecke, rennen_group_strecke);

        // startpositionen wählen
        start_positionen_waehlen();
    }

    private void start_positionen_waehlen(){
        rennen_start_positionen_waehlen = new Rennen_start_positionen_waehlen(
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
        // wichtige variablen aus rennen_start_positionen_waehlen holen
        HashMap<Spieler, Circle> zuordnung_spieler_fxStartNode = rennen_start_positionen_waehlen.getZuordnung_spieler_fxStartNode();
        HashMap<Spieler, Punkt> zuordnung_spieler_startpunkt = rennen_start_positionen_waehlen.getZuordnung_spieler_startpunkt();
        // rennen_start_positionen_waehlen löschen
        rennen_start_positionen_waehlen = null;
        // rennen starten
        Rennen_renn_verlauf rennen_renn_verlauf = new Rennen_renn_verlauf(
                zuordnung_spieler_fxStartNode,
                zuordnung_spieler_startpunkt,
                zurordnung_spieler_infoLabels,
                umrechnung_grid_pixel,
                rennstrecke,
                rennen_group_strecke,
                spieler,
                rennen_lb_rennInfos,
                zurordnung_spieler_farbe,
                new I_aufgabe_beendet() {
                    @Override
                    public void aufgabe_beendet() {
                        // rennen beendet
                        // TODO weiter zu rennen_beendet
                    }
                });
    }


















    public void rennen_group_strecke_mDragged(MouseEvent mouseEvent) {
    }


    public void rennen_group_strecke_mPressed(MouseEvent mouseEvent) {
    }


    public void rennen_group_mReleased(MouseEvent mouseEvent) {
    }


    public void rennen_bn_rennenBeenden_action(ActionEvent actionEvent) {
        // TODO
        ScenesManager.getInstance().show_toast_neutral("beenden");
    }

    public void rennen_bn_weiter_action(ActionEvent actionEvent) {
        // TODO
        ScenesManager.getInstance().show_toast_neutral("weiter");
    }
}
