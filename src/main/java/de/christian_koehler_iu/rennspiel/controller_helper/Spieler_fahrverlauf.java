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
import org.jetbrains.annotations.Nullable;


import java.util.ArrayList;
import java.util.HashMap;

public class Spieler_fahrverlauf {
    // class attributes


    // object attributes
    private final Spieler spieler;
    private final String spieler_farbe;
    private final HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf;
    private final Umrechnung_grid_pixel umrechnung_grid_pixel;
    private final Rennstrecke rennstrecke;
    private final Group group_strecke;
    private final I_aufgabe_beendet i_aufgabe_beendet;
    private final Rennen_spieler_info_labels rennen_spieler_info_labels;

    private final ArrayList<Fahrlinie> fahrlinien = new ArrayList<>();
    private boolean is_crashed = false;
    private Circle crash_fxNode = null;
    private boolean is_im_ziel = false;
    private int akt_renn_runde = 0;
    private double akt_zeit = 0.0;
//    private Double gesamt_zeit_beendetes_rennen = null;
    private boolean startline_nach_start_verlassen = false; // wird benötigt für die prüfung eine startlinien-durchfahrung

    private final ArrayList<Node> fx_nodes_moegliche_zuege =  new ArrayList<>();

    public Spieler_fahrverlauf(
            Spieler spieler,
            String spieler_farbe,
            Punkt startpunkt_grid,
            Rennen_spieler_info_labels rennen_spieler_info_labels,
            HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf,
            Umrechnung_grid_pixel umrechnung_grid_pixel,
            Rennstrecke rennstrecke,
            Group group_strecke,
            I_aufgabe_beendet i_aufgabe_beendet) {
        this.spieler = spieler;
        this.spieler_farbe = spieler_farbe;
        this.rennen_spieler_info_labels = rennen_spieler_info_labels;
        this.umrechnung_grid_pixel = umrechnung_grid_pixel;
        this.rennstrecke = rennstrecke;
        this.group_strecke = group_strecke;
        this.i_aufgabe_beendet = i_aufgabe_beendet;
        // aus startpunkt die erste fahrlinie mit delta = 0 erzeugen
        // startpunkte müssen ganzzahl sein
        if( !startpunkt_grid.is_jede_koordinate_ganzzahl() ){
            // mindestens eine koordinate ist keine ganzzahl -> fehler ausgeben
            throw new RuntimeException("Mindestens eine Startpunkt-Koordinate ist keine Ganzzahl !");
        }
        // startpunkt koordinaten sind beide ganzzahlen
        fahrlinien.add(
                new Fahrlinie(
                        (int)startpunkt_grid.getX(),
                        (int)startpunkt_grid.getY(),
                        0,
                        0,
                        rennstrecke.getBreite(),
                        rennstrecke.getHoehe(),
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

            // akt_zeit erhöhen
            akt_zeit++;

            // auf kollisionen prüfen
            kollisons_pruefung();

            // prüfen ob startlinie überfahren wurde
            startlinie_durchfahren_pruefung();

            // info labels aktualisieren
            update_info_labels();

            // zug beenden
            zug_beendet();
        }else{
            // keine erlaubte position geclickt -> toast ausgeben
            ScenesManager.getInstance().show_toast_neutral("gewählte neue Position ist nicht erlaubt!");
        }
    }

    public boolean get_is_im_ziel(){
        return is_im_ziel;
    }

    public boolean get_is_spieler_raus(){
        return is_crashed || is_im_ziel;
    }

    /**
     * gibt die renn zeit aus, wenn der spieler das rennen noch nicht regulär beendet hat wird null ausgegeben
     * @return Double gesamt renn zeit
     */
    @Nullable
    public Double get_gesamt_renn_zeit(){
        if(is_im_ziel && !is_crashed){
            return akt_zeit;
        }else{
            // rennen nicht regulär beendet
            return null;
        }
    }

    private void zug_beendet(){
        // zug ist beendet -> Rennen_renn_verlauf bescheid geben, dass aufgabe beendet
        i_aufgabe_beendet.aufgabe_beendet();
    }

    private void update_info_labels(){
        rennen_spieler_info_labels.set_spieler_runde(akt_renn_runde+1); // +1 da attribut bei null beginnt
        rennen_spieler_info_labels.set_spieler_zeit(akt_zeit);
        if(is_crashed){
            rennen_spieler_info_labels.set_spieler_status("CRASHED");
        }else if(is_im_ziel){
            rennen_spieler_info_labels.set_spieler_status("im Ziel");
        }

    }

    private void kollisons_pruefung(){
        // eigene letzte fahrlinie holen
        Fahrlinie eigene_letzte_fahrlinie = get_last_fahrlinie();

        // aktuelle fahrpositionen aller anderen spieler holen, die noch nicht raus sind
        ArrayList<Punkt> fahrpositionen_andere_spieler = new ArrayList<>();
        for(Spieler akt_spieler : zuordnung_spieler_spielerFahrverlauf.keySet()){
            if(akt_spieler != this.spieler){
                // akt_spieler ist ein anderer spieler
                Spieler_fahrverlauf akt_spieler_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);
                if( !akt_spieler_fahrverlauf.get_is_spieler_raus()){
                    // nur wenn spieler noch nicht raus ist, kann man mit ihm kollidieren
                    int pos_x_grid_anderer_spieler = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler).get_last_fahrlinie().get_end_x_grid();
                    int pos_y_grid_anderer_spieler = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler).get_last_fahrlinie().get_end_y_grid();
                    // punkt erzeugen
                    fahrpositionen_andere_spieler.add(
                            new Punkt(pos_x_grid_anderer_spieler, pos_y_grid_anderer_spieler)
                    );
                }
            }
        }

        // test auf kollisionen mit einer randlinie
        boolean kollision_mit_randlinie = false;
        for(Linie akt_rand_linie : rennstrecke.getStreckenlinien()){
            System.out.println("akt_rand_linie: " + akt_rand_linie);
            for(Linie akt_fahrlinien_line : eigene_letzte_fahrlinie.get_grid_linien()){
                System.out.println("akt_fahrlinien_line: " + akt_fahrlinien_line);
                if(akt_rand_linie.hat_schnittpunkt_mit_linie(akt_fahrlinien_line)){
                    kollision_mit_randlinie = true;
                    break;
                }
            }
        }
        System.out.println("kollision_mit_randlinie = " +kollision_mit_randlinie);
        if(kollision_mit_randlinie){
            // toast anzeigen
            ScenesManager.getInstance().show_toast_warning("CRASH mit Streckenlinie!");
            // spieler crashed speichern
            is_crashed = true;
            // TODO crash einzeichnen + links anzeigen, dass spieler raus ist
        }

        // test auf kollisionen mit anderen spielerpositionen
        boolean kollision_mit_anderen_spielern = false;
        for(Punkt akt_pos_anderer_spieler : fahrpositionen_andere_spieler){
            for(Linie akt_fahrlinien_line : eigene_letzte_fahrlinie.get_grid_linien()){
                if(akt_fahrlinien_line.liegt_punkt_auf_linie(akt_pos_anderer_spieler)){
                    // fahrline geht über eine fahrposition eines anderen spielers
                    kollision_mit_anderen_spielern = true;
                    break;
                }
            }
        }
        System.out.println("kollision_mit_anderen_spielern = " +kollision_mit_anderen_spielern);
        if(kollision_mit_anderen_spielern){
            // toast anzeigen
            ScenesManager.getInstance().show_toast_warning("CRASH mit anderem Spieler!");
            // spieler crashed speichern
            is_crashed = true;
            // TODO crash einzeichnen+ links anzeigen, dass spieler raus ist
        }
    }

    private void startlinie_durchfahren_pruefung(){
        System.out.println("Spieler_fahrverlauf: startlinie_durchfahren_pruefung()");

        // eigene letzte fahrlinie holen
        Fahrlinie eigene_letzte_fahrlinie = get_last_fahrlinie();

        // startlinie holen
        Linie startlinie = rennstrecke.getStartlinie();
        if(startlinie == null){
            throw new RuntimeException("Startlinie fehlt!");
        }

        // prüfung findet erst statt, sobald das erste mal die startlinie verlassen wurde
        if(!startline_nach_start_verlassen){
            // startlinie wurde bis jetzt noch nicht verlassen
            // prüfen, ob jetzt verlassen

            // endpunkt der aktuellen fahrlinie holen
            Punkt end_punkt = new Punkt(
                    eigene_letzte_fahrlinie.get_end_x_grid(),
                    eigene_letzte_fahrlinie.get_end_y_grid()
            );
            if( !startlinie.liegt_punkt_auf_linie(end_punkt) ){
                // endpunkt der akt fahrlinie liegt nicht auf startlinie -> startlinie verlassen
                startline_nach_start_verlassen = true;
            }
            // damit man nicht rückwärts durch die startlinie fahren kann und dann durch ziel
            //  muss geprüft werden, ob rückwärts gefahren wurde
            boolean richtige_richtung_gestartet = false;
            if(
                    !startlinie.is_horizontal() && // startlinie vertikal
                    !rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung links
                    (eigene_letzte_fahrlinie.get_delta_x_grid() < 0) // fahrt nach links
            ){
                richtige_richtung_gestartet = true;
            }
            if(
                    !startlinie.is_horizontal() && // startlinie vertikal
                    rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung rechts
                    (eigene_letzte_fahrlinie.get_delta_x_grid() > 0) // fahrt nach rechts
            ){
                richtige_richtung_gestartet = true;
            }
            if(
                    startlinie.is_horizontal() && // startlinie horizontal
                    !rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung oben
                    (eigene_letzte_fahrlinie.get_delta_y_grid() < 0) // fahrt nach oben
            ){
                richtige_richtung_gestartet = true;
            }
            if(
                    startlinie.is_horizontal() && // startlinie horizontal
                    rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung unten
                    (eigene_letzte_fahrlinie.get_delta_y_grid() > 0) // fahrt nach unten
            ){
                richtige_richtung_gestartet = true;
            }
            if(!richtige_richtung_gestartet){
                // wenn in die falsche richtung gestartet wurde, dann akt_renn_runde verringern
                akt_renn_runde--;
            }

            // prüfung beenden
            return;
        }

        // pruefen, ob eine der fahrlinien_linien die startlinie berührt hat (berührt oder durchfahren)
        int anz_linien_die_die_startlinie_beruehren = 0;
        for(Linie akt_linie : eigene_letzte_fahrlinie.get_grid_linien()){
            if(akt_linie.hat_schnittpunkt_mit_linie(startlinie)){
                anz_linien_die_die_startlinie_beruehren++;
            }
        }
        if(anz_linien_die_die_startlinie_beruehren==0){
            // keine berührung mit startlinie -> methode returnen
            return;
        }
        // es gibt mindestens eine berührung mit der startlinie
        System.out.println("es gibt mindestens eine berührung mit der startlinie");

        // prüfung, ob startlinie durchfahren wurde
        // endpunkt der fahrlinie erzeugen
        Punkt endpunkt = new Punkt(
                eigene_letzte_fahrlinie.get_end_x_grid(),
                eigene_letzte_fahrlinie.get_end_y_grid()
                );
        if(startlinie.liegt_punkt_auf_linie(endpunkt) && anz_linien_die_die_startlinie_beruehren==1){
            // wenn endpunkt der letzten fahlinie auf der startlinie liegt und keine vorherigen linien die
            //  startlinie berühren, dann wurde die startlinie nicht durchfahren -> methode returnen
            return;
        }
        // starlinie wurde durchfahren
        System.out.println("starlinie wurde durchfahren");

        // prüfung ob startlinie in richtiger richtung durchfahren wurde
        boolean richtige_richtung_durchfahren = false;
        if(
            !startlinie.is_horizontal() && // startlinie vertikal
            !rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung links
            (eigene_letzte_fahrlinie.get_delta_x_grid() < 0) // fahrt nach links
        ){
            richtige_richtung_durchfahren = true;
        }
        if(
            !startlinie.is_horizontal() && // startlinie vertikal
            rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung rechts
            (eigene_letzte_fahrlinie.get_delta_x_grid() > 0) // fahrt nach rechts
        ){
            richtige_richtung_durchfahren = true;
        }
        if(
            startlinie.is_horizontal() && // startlinie horizontal
            !rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung oben
            (eigene_letzte_fahrlinie.get_delta_y_grid() < 0) // fahrt nach oben
        ){
            richtige_richtung_durchfahren = true;
        }
        if(
            startlinie.is_horizontal() && // startlinie horizontal
            rennstrecke.get_is_startrichtung_nach_unten_oder_rechts() && // startrichtung unten
            (eigene_letzte_fahrlinie.get_delta_y_grid() > 0) // fahrt nach unten
        ){
            richtige_richtung_durchfahren = true;
        }

        if(richtige_richtung_durchfahren){
            // akt_renn_runde erhöhen
            akt_renn_runde++;

            // toast anzeigen
            ScenesManager.getInstance().show_toast_neutral("Zielline durchfahren");

            // test ob rennen beendet
            if(akt_renn_runde >= rennstrecke.get_anz_runden()){
                // spieler hat alle runden beendet
                is_im_ziel = true;
                // gesamtzeit berechnen
                akt_zeit = calc_ziel_zeit();
            }
        }else{ // startline in falscher richtung durchfahren
            // akt_renn_runde verringern
            akt_renn_runde--;

            // toast anzeigen
            ScenesManager.getInstance().show_toast_neutral("Zielline in falscher Richtung durchfahren");
        }
    }

    public Fahrlinie get_last_fahrlinie(){
        return fahrlinien.getLast();
    }

    private double calc_ziel_zeit(){
        // eigene letzte fahrlinie holen
        Fahrlinie eigene_letzte_fahrlinie = get_last_fahrlinie();

        // startlinie holen
        Linie startlinie = rennstrecke.getStartlinie();
        if(startlinie == null){
            throw new RuntimeException("Startlinie fehlt!");
        }

        // anzahl ganze fahrlinien berechnen
        double anz_ganze_fahrlinien = fahrlinien.size() - 2; //-2 da letzte und erste wegfallen (erste ist nur die startposition)

        // linie holen, die die startlinie als erstes durchfahren hat
        int index_linie_erste_durchfahrt = -1;
        for(int akt_index=0 ; akt_index<eigene_letzte_fahrlinie.get_grid_linien().size() ; akt_index++){
            Linie akt_linie = eigene_letzte_fahrlinie.get_grid_linien().get(akt_index);
            if(akt_linie.hat_schnittpunkt_mit_linie(startlinie)){
                index_linie_erste_durchfahrt = akt_index;
                break;
            }
        }
        if(index_linie_erste_durchfahrt < 0){
            throw new RuntimeException("Aufruf der Methode Spieler_fahrverlauf:calc_rennzeit() ohne, dass die Startlinie überfahren wurde");
        }

        // berechnen welcher anteil der linie bis zur durchquerung der startlinie erfolgt ist
        double anteil_bis_ziel;
        Linie erste_durchfahrts_linie = eigene_letzte_fahrlinie.get_grid_linien().get(index_linie_erste_durchfahrt);
        if(startlinie.is_horizontal()){
            // delta y entscheidend
            // delta_y bis zur startlinie berechnen
            double delta_y_bis_startlinie = startlinie.getP0().getY() - erste_durchfahrts_linie.getP0().getY();
            // die delta_y der vorherigen linien hinzufügen
            for(int akt_index=0 ; akt_index<index_linie_erste_durchfahrt ; akt_index++){
                Linie akt_linie = eigene_letzte_fahrlinie.get_grid_linien().get(akt_index);
                delta_y_bis_startlinie = delta_y_bis_startlinie + (akt_linie.getP1().getY() - akt_linie.getP0().getY());
            }

            // anteil bis ziel berechnen
            anteil_bis_ziel = delta_y_bis_startlinie / eigene_letzte_fahrlinie.get_delta_y_grid();
        }else{ // startlinie ist vertikal
            // delta x entscheidend
            // delta_x bis zur startlinie berechnen
            double delta_x_bis_startlinie = startlinie.getP0().getX() - erste_durchfahrts_linie.getP0().getX();
            // die delta_x der vorherigen linien hinzufügen
            for(int akt_index=0 ; akt_index<index_linie_erste_durchfahrt ; akt_index++){
                Linie akt_linie = eigene_letzte_fahrlinie.get_grid_linien().get(akt_index);
                delta_x_bis_startlinie = delta_x_bis_startlinie + (akt_linie.getP1().getX() - akt_linie.getP0().getX());
            }

            // anteil bis ziel berechnen
            anteil_bis_ziel = delta_x_bis_startlinie / eigene_letzte_fahrlinie.get_delta_x_grid();
        }

        // gesamtzeit berechnen und ausgeben
        return anz_ganze_fahrlinien + anteil_bis_ziel;
    }
}
