package de.christian_koehler_iu.rennspiel.controller_helper;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import de.christian_koehler_iu.rennspiel.data_classes.Punkt;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.interfaces.I_aufgabe_beendet;
import de.christian_koehler_iu.rennspiel.utility.Umrechnung_grid_pixel;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * FX-Controller-Hilfsklasse für den Controller RennenController
 * diese klasse ist dafür zuständig, dass alle spieler ihr startpositionen auf der startlinie wählen
 *  dabei wird geprüft, ob bereits ein anderer spieler auf der gewählten position liegt und dass sich kein spieler auf eine streckenline setzt
 */
public class Rennen_start_positionen_waehlen {
    // class attributes

    // object attributes
    // constructor eingaben
    private final Umrechnung_grid_pixel umrechnung_grid_pixel;
    private final Rennstrecke rennstrecke;
    private final Group group_strecke;
    private final ArrayList<Spieler> spieler;
    private final Label label_renn_infos;
    private final HashMap<Spieler, String> zurordnung_spieler_farbe;
    private final I_aufgabe_beendet i_aufgabe_beendet;
    // konstanten
    private final String path_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css")).toExternalForm();
    private final String CSS_STYLE_CLASS_CIRCLE_MOEGLICHE_POS = "circle_moegliche_neue_position";
    private final String CSS_STYLE_CLASS_CIRCLE_FAHR_POS = "circle_fahr_position";

    private final HashMap<Spieler, Punkt> zuordnung_spieler_startpunkt = new HashMap<>();
    private final HashMap<Spieler, Circle> zuordnung_spieler_fxStartNode = new HashMap<>();
    private int akt_index_spieler = 0; // index des spielers der am zug ist
    private final ArrayList<Circle> fx_nodes_moegliche_startpunkte = new ArrayList<>();

    // constructor
    public Rennen_start_positionen_waehlen(
           Umrechnung_grid_pixel umrechnung_grid_pixel,
           Rennstrecke rennstrecke,
           Group group_strecke,
           ArrayList<Spieler> spieler,
           Label label_renn_infos,
           HashMap<Spieler, String> zuordnung_spieler_farbe,
           I_aufgabe_beendet i_aufgabe_beendet) {
        // object attribute initialisieren
        this.umrechnung_grid_pixel = umrechnung_grid_pixel;
        this.rennstrecke = rennstrecke;
        this.group_strecke = group_strecke;
        this.spieler = spieler;
        this.label_renn_infos = label_renn_infos;
        this.zurordnung_spieler_farbe = zuordnung_spieler_farbe;
        this.i_aufgabe_beendet = i_aufgabe_beendet;

        // positionswahl beginnen
        start_choice_next_spieler();

        // maus click listener erstellen
        group_strecke.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                if(mouseEvent.getButton() == MouseButton.PRIMARY && !fx_nodes_moegliche_startpunkte.isEmpty()){
                    System.out.println("Click mit linker Maustaste und mögliche Positionen geladen");
                    Integer pos_x_grid_nullable = umrechnung_grid_pixel.posXPixel_to_posXGrid_round(mouseEvent.getX());
                    Integer pos_y_grid_nullable = umrechnung_grid_pixel.posYPixel_to_posYGrid_round(mouseEvent.getY());

                    if(pos_x_grid_nullable!=null && pos_y_grid_nullable!=null){
                        System.out.println("Click auf Kästchenkreuz");
                        // click kann einem kästchenkreuz zugeordnet werden
                        // test ob das kästchenkreuz eine mögliche startposition ist
                        ArrayList<Punkt> moegliche_startpositionen = get_moegliche_startpunkte();
                        boolean is_clicked_valider_startpunkt = false;
                        for(Punkt moeglicher_startpunkt : moegliche_startpositionen){
                            if(moeglicher_startpunkt.getX()==pos_x_grid_nullable && moeglicher_startpunkt.getY()== pos_y_grid_nullable){
                                is_clicked_valider_startpunkt = true;
                                break;
                            }
                        }
                        if(is_clicked_valider_startpunkt){
                            System.out.println("Click ist valider Startpunkt");
                            // gewählte startposition ist valide
                            // moegliche startpunkt nodes entfernen
                            group_strecke.getChildren().removeAll(fx_nodes_moegliche_startpunkte);
                            fx_nodes_moegliche_startpunkte.clear();
                            // startpunkt dem spieler zuordnen
                            zuordnung_spieler_startpunkt.put(spieler.get(akt_index_spieler), new Punkt(pos_x_grid_nullable, pos_y_grid_nullable));
                            // startpunkt einzeichnen
                            Circle startpunkt = new Circle();
                            startpunkt.getStyleClass().add(CSS_STYLE_CLASS_CIRCLE_FAHR_POS);
                            startpunkt.setStyle("-fx-fill: " + zurordnung_spieler_farbe.get(spieler.get(akt_index_spieler)) + ";");
                            startpunkt.setCenterX(umrechnung_grid_pixel.posXGrid_to_posXPixel(pos_x_grid_nullable));
                            startpunkt.setCenterY(umrechnung_grid_pixel.posYGrid_to_posYPixel(pos_y_grid_nullable));
                            startpunkt.setRadius(umrechnung_grid_pixel.B_1GRID_IN_PIXEL * 0.2);
                            // gewählten fx-startpunkt-node der group übergeben
                            group_strecke.getChildren().add(startpunkt);
                            // gewählten fx-startpunkt-node dem spieler zuordnen
                            zuordnung_spieler_fxStartNode.put(spieler.get(akt_index_spieler), startpunkt);

                            // aktueller spielr hat erfolgreich seine position gewählt
                            // index des aktuellen spielers erhöhen
                            akt_index_spieler++;
                            // nächsten spieler wählen lassen
                            start_choice_next_spieler();
                        }
                    }
                }
            }
        });
    }

    private void start_choice_next_spieler(){
        if(akt_index_spieler < spieler.size()){
            // aktueller spieler vorhanden -> position wählen lassen
            // infolabel aktualisieren
            // textfarbe vom spieler im info label setzen
            label_renn_infos.setStyle("-fx-text-fill: " + zurordnung_spieler_farbe.get(spieler.get(akt_index_spieler)));
            // info label text setzen
            label_renn_infos.setText(spieler.get(akt_index_spieler).get_name() + ": Startposition wählen");

            // mögliche startpositionen zeichnen
            zeichne_moegliche_startpositionen();
        }else{
            // alle spieler haben die startposition gewählt
            // alles auf der group löschen, außer die fahrpunkte
            group_strecke.getChildren().removeAll(fx_nodes_moegliche_startpunkte);
            // info label leeren
            label_renn_infos.setText("");
            // toast ausgeben, dass startpositionen erflogreich gewählt
            ScenesManager.getInstance().show_toast_neutral("Alle Startpositionen wurden erfolgreich gewählt!");
            // rennen-controller mitteilen, dass startpositionen wählen beendet ist
            i_aufgabe_beendet.aufgabe_beendet();
        }

    }

    private void zeichne_moegliche_startpositionen(){
        // alte nodes aus group löschen
        group_strecke.getChildren().removeAll(fx_nodes_moegliche_startpunkte);
        // arraylist leeren
        fx_nodes_moegliche_startpunkte.clear();

        ArrayList<Punkt> moegleiche_startpunkte = get_moegliche_startpunkte();

        for(Punkt akt_punkt : moegleiche_startpunkte){
            // circle node erzeugen
            Circle akt_circle = new Circle();
            akt_circle.getStyleClass().add(CSS_STYLE_CLASS_CIRCLE_MOEGLICHE_POS);
            akt_circle.setCenterX( umrechnung_grid_pixel.posXGrid_to_posXPixel(akt_punkt.getX()) );
            akt_circle.setCenterY( umrechnung_grid_pixel.posYGrid_to_posYPixel(akt_punkt.getY()) );
            akt_circle.setRadius(umrechnung_grid_pixel.B_1GRID_IN_PIXEL * 0.3);
            akt_circle.setStyle("-fx-stroke: " + zurordnung_spieler_farbe.get(spieler.get(akt_index_spieler)) );
            // circle der arraylist übergeben
            fx_nodes_moegliche_startpunkte.add(akt_circle);
            // circle der group übergeben
            group_strecke.getChildren().add(akt_circle);
        }
    }

    private ArrayList<Punkt> get_moegliche_startpunkte(){
        // mögliche startpositionen ermitteln

        // ausgabe arraylist erstellen
        ArrayList<Punkt> moegleiche_startpunkte = new ArrayList<>();

        Linie start_linie = rennstrecke.getStartlinie();
        if(start_linie == null){
            throw new RuntimeException("Startlinie fehlt!");
        }

        // startpunkt ermitteln an dem startlinie beginnt
        int x0_grid_startLinie = (int)rennstrecke.getStartlinie().getP0().getX();
        int y0_grid_startLinie = (int)rennstrecke.getStartlinie().getP0().getY();
        int x1_grid_startLinie = (int)rennstrecke.getStartlinie().getP1().getX();
        int y1_grid_startLinie = (int)rennstrecke.getStartlinie().getP1().getY();

        // startlinie horizontal oder vertikal
        boolean is_startlinie_horizontal;
        if(y0_grid_startLinie == y1_grid_startLinie){
            is_startlinie_horizontal = true;
        }else if(x0_grid_startLinie == x1_grid_startLinie){
            is_startlinie_horizontal = false;
        }else{
            throw new RuntimeException("Startlinie ist weder horizontal noch vertikal!");
        }

        // linker oberer punkt der startlinie ist der startpunkt
        int x_min_grid_startLinie = x0_grid_startLinie<x1_grid_startLinie ? x0_grid_startLinie : x1_grid_startLinie;
        int y_min_grid_startLinie = y0_grid_startLinie<y1_grid_startLinie ? y0_grid_startLinie : y1_grid_startLinie;
        int x_max_grid_startLinie = x0_grid_startLinie>x1_grid_startLinie ? x0_grid_startLinie : x1_grid_startLinie;
        int y_max_grid_startLinie = y0_grid_startLinie>y1_grid_startLinie ? y0_grid_startLinie : y1_grid_startLinie;

        // gridpunkte der startlinie durchgehen
        if(is_startlinie_horizontal){
            for(int akt_x_grid=x_min_grid_startLinie ; akt_x_grid<x_max_grid_startLinie ; akt_x_grid++){
                // alle streckenlinien durchgehen und testen, ob akt punkt aud einer streckenlinie liegt
                boolean is_punkt_moegliche_startposition = true;
                for(Linie akt_strecken_linie : rennstrecke.getStreckenlinien()){
                    if( akt_strecken_linie.liegt_punkt_auf_linie(akt_x_grid, y_min_grid_startLinie) ){
                        is_punkt_moegliche_startposition = false;
                        break;
                    }
                }
                if(is_punkt_moegliche_startposition){
                    // punkt liegt auf keiner streckenlinie
                    moegleiche_startpunkte.add(new Punkt(akt_x_grid, y_min_grid_startLinie));
                }
            }
        }else { // starlinie ist vertikal
            for(int akt_y_grid=y_min_grid_startLinie ; akt_y_grid<y_max_grid_startLinie ; akt_y_grid++){
                // alle streckenlinien durchgehen und testen, ob akt punkt aud einer streckenlinie liegt
                boolean is_punkt_moegliche_startposition = true;
                for(Linie akt_strecken_linie : rennstrecke.getStreckenlinien()){
                    if( akt_strecken_linie.liegt_punkt_auf_linie(x_min_grid_startLinie, akt_y_grid) ){
                        is_punkt_moegliche_startposition = false;
                        break;
                    }
                }
                if(is_punkt_moegliche_startposition){
                    // punkt liegt auf keiner streckenlinie
                    moegleiche_startpunkte.add(new Punkt(x_min_grid_startLinie, akt_y_grid));
                }
            }
        }

        // von den generell möglichen startpositionen, die abziehen, wo bereits ein spieler drauf ist
        zuordnung_spieler_startpunkt.forEach((spieler, punkt) -> moegleiche_startpunkte.remove(punkt));

        // möglich startpositionen ausgeben
        return moegleiche_startpunkte;
    }


    public HashMap<Spieler, Circle> getZuordnung_spieler_fxStartNode() {
        return zuordnung_spieler_fxStartNode;
    }

    public HashMap<Spieler, Punkt> getZuordnung_spieler_startpunkt() {
        return zuordnung_spieler_startpunkt;
    }
}
