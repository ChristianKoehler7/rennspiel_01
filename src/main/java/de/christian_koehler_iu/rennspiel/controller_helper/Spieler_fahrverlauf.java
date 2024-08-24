package de.christian_koehler_iu.rennspiel.controller_helper;

import de.christian_koehler_iu.rennspiel.controller.ScenesManager;
import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import de.christian_koehler_iu.rennspiel.data_classes.Punkt;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.interfaces.I_aufgabe_beendet;
import de.christian_koehler_iu.rennspiel.utility.Umrechnung_grid_pixel;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;


import java.util.ArrayList;
import java.util.HashMap;

public class Spieler_fahrverlauf {
    // class attributes


    // object attributes
    private final Spieler spieler;
    private final String spieler_farbe;
    private final HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf;
    private int strecke_breite_grid;
    private int strecke_hoehe_grid;
    private final Umrechnung_grid_pixel umrechnung_grid_pixel;
    private final Rennstrecke rennstrecke;
    private final Group group_strecke;
    private final I_aufgabe_beendet i_aufgabe_beendet;

    private final ArrayList<Fahrlinie> fahrlinien = new ArrayList<>();
    private boolean is_crashed = false;
    private Circle crash_fxNode = null;
    private boolean is_im_ziel = false;
    private int akt_renn_runde = 1;
    private Double gesamt_zeit_beendetes_rennen = null;

    private final ArrayList<Node> fx_nodes_moegliche_zuege =  new ArrayList<>();

    public Spieler_fahrverlauf(
            Spieler spieler,
            String spieler_farbe,
            Punkt startpunkt_grid,
            HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf,
            int strecke_breite_grid,
            int strecke_hoehe_grid,
            Umrechnung_grid_pixel umrechnung_grid_pixel,
            Rennstrecke rennstrecke,
            Group group_strecke,
            I_aufgabe_beendet i_aufgabe_beendet) {
        this.spieler = spieler;
        this.spieler_farbe = spieler_farbe;
        this.strecke_breite_grid = strecke_breite_grid;
        this.strecke_hoehe_grid = strecke_hoehe_grid;
        this.umrechnung_grid_pixel = umrechnung_grid_pixel;
        this.rennstrecke = rennstrecke;
        this.group_strecke = group_strecke;
        this.i_aufgabe_beendet = i_aufgabe_beendet;
        // aus startpunkt die erste fahrlinie mit delta = 0 erzeugen
        fahrlinien.add(
                new Fahrlinie(
                        startpunkt_grid.getX(),
                        startpunkt_grid.getY(),
                        0,
                        0,
                        strecke_breite_grid,
                        strecke_hoehe_grid,
                        umrechnung_grid_pixel,
                        spieler_farbe
                        ));
        this.zuordnung_spieler_spielerFahrverlauf = zuordnung_spieler_spielerFahrverlauf;
    }

    public void start_zug(){
        zeichne_moegliche_neue_positionen();
    }

    private void zeichne_moegliche_neue_positionen(){
        // letzte fahrlinie holen
        Fahrlinie last_fahrlinie = fahrlinien.getLast();

        // fx_nodes mögliche züge von der letzten fahrlinie holen und in arraylist speichern
        fx_nodes_moegliche_zuege.clear();
        fx_nodes_moegliche_zuege.addAll(last_fahrlinie.create_fxNodes_for_moegliche_neue_positionen());

        // fx_nodes mögliche züge der goup übergeben
        group_strecke.getChildren().addAll(fx_nodes_moegliche_zuege);
    }

    public void mouse_clicked(MouseEvent mouseEvent){
        if(mouseEvent.getButton() == MouseButton.PRIMARY){ // TODO && this.sind_moegliche_pos_fx_nodes_angezeigt()){
            System.out.println("Click mit linker Maustaste und mögliche Positionen geladen");
            Integer pos_x_grid_nullable = umrechnung_grid_pixel.posXPixel_to_posXGrid_round(mouseEvent.getX());
            Integer pos_y_grid_nullable = umrechnung_grid_pixel.posYPixel_to_posYGrid_round(mouseEvent.getY());

            if(pos_x_grid_nullable!=null && pos_y_grid_nullable!=null){
                System.out.println("Click auf Kästchenkreuz: x=" + pos_x_grid_nullable + " y=" +pos_y_grid_nullable);
                // click kann einem kästchenkreuz zugeordnet werden -> zur neuen position fahren
                fahre_zur_gewaehlten_position(pos_x_grid_nullable, pos_y_grid_nullable);
            }
        }
    }

    private void fahre_zur_gewaehlten_position(int x_grid, int y_grid){
        // punkt aus clickposition erzeugen
        Punkt punkt_clicked = new Punkt(x_grid, y_grid);

        // letzte fahrlinie holen
        Fahrlinie last_fahrlinie = fahrlinien.getLast();

        // mögliche neue positionen von der letzten fahrlinie holen
        ArrayList<Punkt> moegliche_punkte = last_fahrlinie.calc_moegliche_neue_positionen();
        System.out.println(
                "letzteFahrlinie: x_start=" + last_fahrlinie.get_start_x_grid() +
                " y_start=" + last_fahrlinie.get_start_y_grid() +
                " | delta_x=" + last_fahrlinie.get_delta_x_grid() +
                " delta_y=" + last_fahrlinie.get_delta_y_grid() +
                " | x_end=" + last_fahrlinie.get_end_x_grid() +
                " y_end=" + last_fahrlinie.get_end_y_grid()
                );

        // test ob gewählte position eine erlaubt neue position ist
        boolean is_gew_pos_erlaubt = false;
        for(Punkt moeglicher_punkt : moegliche_punkte){
            System.out.println("moeglicherPunkt: x=" + moeglicher_punkt.getX() + " y=" + moeglicher_punkt.getY());
            if(moeglicher_punkt.equals(punkt_clicked)){
                is_gew_pos_erlaubt = true;
                break;
            }
        }
        if(is_gew_pos_erlaubt){
            // gewählte startposition ist valide
            System.out.println("Click ist erlaubte neue Fahrposition");

            // fx_nodes mögliche züge entfernen
            group_strecke.getChildren().removeAll(fx_nodes_moegliche_zuege);
            fx_nodes_moegliche_zuege.clear();

            // fahrlinie erzeugen
            Fahrlinie neue_fahrlinie = last_fahrlinie.create_next_fahrlinie(x_grid, y_grid);
            // fahrlinie der arraylist hinzufügen
            fahrlinien.add(neue_fahrlinie);
            // fx nodes der fahrlinie der group übergeben
            group_strecke.getChildren().addAll(neue_fahrlinie.get_fx_nodes());

            // spieler hat erfolgreich seine position gewählt

            // TODO auf kollisionen prüfen


            // TODO prüfen ob startlinie überfahren wurde


            // zug beenden
            zug_beendet();
        }else{
            // keine erlaubte position geclickt -> toast ausgeben
            ScenesManager.getInstance().show_toast_neutral("gewählte neue Position ist nicht erlaubt!");
        }
    }

    public boolean get_is_spieler_raus(){
        return is_crashed || is_im_ziel;
    }

    private void zug_beendet(){
        // zug ist beendet -> Rennen_renn_verlauf bescheid geben, dass aufgabe beendet
        i_aufgabe_beendet.aufgabe_beendet();
    }

    private void kollisons_pruefung(){
        // eigene letzte fahrlinie holen
        Fahrlinie eigene_letzte_fahrlinie = get_last_fahrlinie();

        // fahrlinien aller anderen spieler holen
        ArrayList<Fahrlinie> fahrlinien_andere_spieler = new ArrayList<>();

        // test auf kollisionen mit einer randlinie
//        boolean kollision_mit_randlinie = false;
//        for(Linie akt_rand_linie : rennstrecke.getStreckenlinien()){
//            if(eigene_letzte_fahrlinie.){
//
//            }
//        }


    }

    public Fahrlinie get_last_fahrlinie(){
        return fahrlinien.getLast();
    }





}
