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
import javafx.scene.control.Button;
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
    @FXML
    Button rennen_bn_rennenBeenden;
    @FXML
    Button rennen_bn_weiter;


    private final double B_MAX_PIXEL = 860.0;
    private final double H_MAX_PIXEL = 530.0;

    private Spieler aktiver_spieler;
    private Rennstrecke rennstrecke;
    private ArrayList<Spieler> mitspieler;
    private final ArrayList<Spieler> spieler = new ArrayList<>();
    private final String[] farben = {"blue", "red", "orange", "green"};
    private final HashMap<Spieler, String> zurordnung_spieler_farbe = new HashMap<>();
    private final URL url_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css"));
    private Umrechnung_grid_pixel umrechnung_grid_pixel;
    private final HashMap<Spieler, Rennen_spieler_info_labels> zurordnung_spieler_infoLabels = new HashMap<>(); // für die infobox

    private Rennen_start_positionen_waehlen rennen_start_positionen_waehlen;

    private HashMap<Spieler, Spieler_fahrverlauf> rueckgabe_von_rennverlauf_zuordnung_spieler_spielerFahrverlauf;


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
        this.mitspieler = mitspieler;

        // alle spieler in attribut speichern
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
        new Rennen_strecke_zeichnen(umrechnung_grid_pixel, rennstrecke, rennen_group_strecke);

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
                new Rennen_renn_verlauf.I_rennen_renn_verlauf() {
                    @Override
                    public void rennen_beendet(HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf) {
                        rennen_renn_verlauf_beendet(zuordnung_spieler_spielerFahrverlauf);
                    }
                });
    }

    private void rennen_renn_verlauf_beendet(HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf){
        // button beenden disablen
        rennen_bn_rennenBeenden.setDisable(true);

        // button weiter aktivieren
        rennen_bn_weiter.setDisable(false);

        // spieler strecken bestzeiten sind bereits im spieler-objekt und in der db aktualisiert

        // zuordnung spieler fahrverläufe speichern, um es dann der scene rennen beendet weiterzugeben
        rueckgabe_von_rennverlauf_zuordnung_spieler_spielerFahrverlauf = zuordnung_spieler_spielerFahrverlauf;


    }


    public void rennen_bn_rennenBeenden_action(ActionEvent actionEvent) {
        ScenesManager.getInstance().show_dialog_warning(
                "Rennen wirklich beenden?",
                "ja",
                "nein",
                new ScenesManager.I_dialog_actions() {
                    @Override
                    public void ok_action() {
                        // rennen beenden und zurück zu strecke wählen scene
                        ScenesManager.getInstance().switch_to_strecke_waehlen(aktiver_spieler);
                    }

                    @Override
                    public void cancel_action() {
                        // nix tun
                    }
                });
    }

    public void rennen_bn_weiter_action(ActionEvent actionEvent) {
        // wechseln zu scene rennen beendet
        ScenesManager.getInstance().switch_to_rennen_beendet(
                rennstrecke,
                aktiver_spieler,
                mitspieler,
                rueckgabe_von_rennverlauf_zuordnung_spieler_spielerFahrverlauf
        );
    }
}
