package de.christian_koehler_iu.rennspiel.controller_helper;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import de.christian_koehler_iu.rennspiel.data_classes.Punkt;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.interfaces.I_aufgabe_beendet;
import de.christian_koehler_iu.rennspiel.utility.Umrechnung_grid_pixel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Controller-Hilfsklasse für den FX-Controller RennenController
 * diese klasse kümmert sich um den rennverlauf
 * der rennverlauf beginnt nachdem alle spieler ihre startpositionen gewählt haben
 * und endet wenn alle spieler raus sind (unfall oder regulär beendet)
 * diese hilfklasse koordiniert den rennverlauf und hat als weitere hilfklassen instanzen der klasse Spieler_fahrverlauf,
 *  in dem die fahrverläufe der jeweiligen spieler gespeichert und berechnet werden
 */
public class Rennen_renn_verlauf {

    // class attributes

    // object attributes
    // constructor eingaben
    private final Umrechnung_grid_pixel umrechnung_grid_pixel;
    private final Rennstrecke rennstrecke;
    private final Group group_strecke;
    private final ArrayList<Spieler> spieler_arrayList;
    private final Label label_renn_infos;
    private final HashMap<Spieler, String> zurordnung_spieler_farbe;
    private final I_rennen_renn_verlauf i_rennen_renn_verlauf;
    // konstanten
    private final String path_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css")).toExternalForm();
    private final String CSS_STYLE_CLASS_RECHTECK_HINTERGRUND = "rechteck_rennen_hintergrund";
    private final String CSS_STYLE_CLASS_LINIE_GITTER = "linie_rennen_gitter";
    private final String CSS_STYLE_CLASS_LINIE_STRECKE = "linie_rennen_strecke";
    private final String CSS_STYLE_CLASS_LINIE_START = "linie_rennen_start";
    private final String CSS_STYLE_CLASS_POLYGON_STARTRICHTUNG = "polygon_rennen_startrichting";
    private final String CSS_STYLE_CLASS_POLYGON_RANDDURCHGANG = "polygon_rennen_randdurchgang";
    private final String CSS_STYLE_CLASS_CIRCLE_MOEGLICHE_POS = "circle_moegliche_neue_position";
    private final String CSS_STYLE_CLASS_CIRCLE_FAHR_POS = "circle_fahr_position";

    // sonstige attribute
    EventHandler<MouseEvent> mouseClickHandler_group; // click event handler für group
    private int akt_spieler_index = 0; // index des spielers der am zug ist
    private final HashMap<Spieler, Rennen_spieler_info_labels> zurordnung_spieler_infoLabels = new HashMap<>();
    private final HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf = new HashMap<>();

    // constructor
    public Rennen_renn_verlauf(
            HashMap<Spieler, Circle> zuordnung_spieler_fxStartNode,
            HashMap<Spieler, Punkt> zuordnung_spieler_startpunkt,
            HashMap<Spieler, Rennen_spieler_info_labels> zurordnung_spieler_infoLabels,
            Umrechnung_grid_pixel umrechnung_grid_pixel,
            Rennstrecke rennstrecke,
            Group group_strecke,
            ArrayList<Spieler> spieler_arrayList,
            Label label_renn_infos,
            HashMap<Spieler, String> zurordnung_spieler_farbe,
            I_rennen_renn_verlauf i_rennen_renn_verlauf) {

        this.umrechnung_grid_pixel = umrechnung_grid_pixel;
        this.rennstrecke = rennstrecke;
        this.group_strecke = group_strecke;
        this.spieler_arrayList = spieler_arrayList;
        this.label_renn_infos = label_renn_infos;
        this.zurordnung_spieler_farbe = zurordnung_spieler_farbe;
        this.i_rennen_renn_verlauf = i_rennen_renn_verlauf;

        // initialisierung zuordnung_spieler_spielerFahrverlauf
        for(Spieler akt_spieler : spieler_arrayList){
            // fahrverlauf erstellen
            Spieler_fahrverlauf akt_spieler_fahrverlauf = new Spieler_fahrverlauf(
                    akt_spieler,
                    zurordnung_spieler_farbe.get(akt_spieler),
                    zuordnung_spieler_startpunkt.get(akt_spieler),
                    zurordnung_spieler_infoLabels.get(akt_spieler),
                    zuordnung_spieler_spielerFahrverlauf,
                    umrechnung_grid_pixel,
                    rennstrecke,
                    group_strecke,
                    new I_aufgabe_beendet() {
                        @Override
                        public void aufgabe_beendet() {
                            // akt zug von spieler_fahrverlauf ist beendet
                            zug_beendet();
                        }
                    });
            zuordnung_spieler_spielerFahrverlauf.put(akt_spieler, akt_spieler_fahrverlauf);
        }
        // mouseClickHandler erstellen
        mouseClickHandler_group = mouseEvent -> {
            // aktuellen spieler holen
            Spieler akt_spieler = spieler_arrayList.get(akt_spieler_index);

            // fahrverlauf holen
            Spieler_fahrverlauf spieler_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);

            // mouse click event an akt fahrverlauf weitergeben
            spieler_fahrverlauf.mouse_clicked(mouseEvent);
        };

        // mouseClickHandler der group übergeben
        group_strecke.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickHandler_group);

        // zug starten
        start_next_zug();
    }

    private void start_next_zug(){
        // abfragen, ob es alle spieler raus sind
        boolean alle_spieler_raus = true;
        for(Spieler_fahrverlauf akt_spieler_fahrverlauf : zuordnung_spieler_spielerFahrverlauf.values()){
            if(!akt_spieler_fahrverlauf.get_is_spieler_raus()){
                // ein spieler ist noch nicht raus
                alle_spieler_raus = false;
            }
        }
        if(alle_spieler_raus){
            // wenn alle raus sind ist das rennen beendet
            rennen_beendet();
            return;
        }
        // mindestens ein spieler ist noch im rennen

        // aktuellen spieler holen
        Spieler akt_spieler = spieler_arrayList.get(akt_spieler_index);

        // fahrverlauf holen
        Spieler_fahrverlauf spieler_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);

        // abfragen, ob aktueller spieler raus ist
        if(spieler_fahrverlauf.get_is_spieler_raus()){
            // rennen des spielers ist beendet -> ende des zugs
            zug_beendet();
        }else{
            // rennen des spielers ist nicht beendet
            // infolabel aktualisieren
            // textfarbe vom spieler im info label setzen
            label_renn_infos.setStyle("-fx-text-fill: " + zurordnung_spieler_farbe.get(spieler_arrayList.get(akt_spieler_index)) + ";");
            // info label text setzen
            label_renn_infos.setText(spieler_arrayList.get(akt_spieler_index).get_name() + ": neue Position wählen");
            // spieler_fahrverlauf starten
            spieler_fahrverlauf.start_zug();
        }
    }

    private void zug_beendet(){
        // aktuellen spieler holen
        Spieler akt_spieler = spieler_arrayList.get(akt_spieler_index);

        // fahrverlauf holen
        Spieler_fahrverlauf spieler_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);

        // info label leeren
        label_renn_infos.setText("");

        // index akt spieler hochsetzen
        akt_spieler_index++;

        // test ob akt_runde beendet
        if(akt_spieler_index < spieler_arrayList.size()){
            // runde nicht beendet -> nächster zug
            start_next_zug();
        }else{
            // runde beendet

            // index des spielers der am zug ist zurücksetzten
            akt_spieler_index = 0;
            // nächster zug
            start_next_zug();
        }
    }

    private void rennen_beendet(){
        // toast anzeigen, dass rennen beendet
        ScenesManager.getInstance().show_toast_neutral("Rennen beendet!");
        // infobox setzen
        label_renn_infos.setText("Rennen beendet");
        label_renn_infos.setStyle("-fx-text-fill: white;");
        // click listener entfernen
        group_strecke.removeEventHandler(MouseEvent.MOUSE_CLICKED, mouseClickHandler_group);

        // spieler bestzeiten speichern (in spieler-objekt und db)
        Double akt_bestzeit = null; // berechnet die bestzeit des aktuellen rennens
        String strecken_name = rennstrecke.getName();
        for(Spieler akt_spieler : spieler_arrayList){
            // spieler fahrverlauf holen
            Spieler_fahrverlauf akt_spieler_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);
            // rennzeit holen (wenn nicht im ziel dann null)
            Double renn_zeit_nullable = akt_spieler_fahrverlauf.get_gesamt_renn_zeit();
            if(renn_zeit_nullable != null && akt_spieler_fahrverlauf.get_is_im_ziel()){
                // akt_spieler hat das rennen regulär beendet
                akt_spieler.process_new_strecken_zeit(strecken_name, renn_zeit_nullable);
                if(akt_bestzeit==null || renn_zeit_nullable<akt_bestzeit){
                    akt_bestzeit = renn_zeit_nullable;
                }
            }
        }

//        // strecken bestzeit aktualisieren
//        if(akt_bestzeit!=null && akt_bestzeit < rennstrecke.getStrecken_bestzeit()){
//            // es wurde eine neue strecken bestzeit gefahren
//            // neue bestzeit im rennstrecken-objekt speichern
//            rennstrecke.setStrecken_bestzeit(akt_bestzeit);
//            // neue bestzeit i db speichern
//            new Rennstrecke_database_connection().update_bestzeit(rennstrecke.getName(), akt_bestzeit);
//        }

        // zurück zum rennenController
        i_rennen_renn_verlauf.rennen_beendet(zuordnung_spieler_spielerFahrverlauf);
    }


    public interface I_rennen_renn_verlauf{
        void rennen_beendet(HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf);
    }

}
