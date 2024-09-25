package de.christian_koehler_iu.rennspiel.controller_helper;

import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import de.christian_koehler_iu.rennspiel.data_classes.Punkt;
import de.christian_koehler_iu.rennspiel.utility.Umrechnung_grid_pixel;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Objects;

public class Fahrlinie {
    // class attributes


    // object attributes
    private final int strecke_breite_grid;
    private final int strecke_hoehe_grid;

    private final int start_x_grid;
    private final int start_y_grid;

    // delta nötig, da eine fahrlinie über den rand hinaus gehen kann
    private final int delta_x_grid;
    private final int delta_y_grid;

    private final Umrechnung_grid_pixel umrechnung_grid_pixel;
    private final String farbe_spieler;

    private int end_x_grid;
    private int end_y_grid;

    // eine fahrlinie kann aus mehreren linien bestehen, da fahrlinie über den rand hinaus gehen kann
    private final ArrayList<Linie> grid_linien = new ArrayList<>();
    private final ArrayList<Line> fx_lines = new ArrayList<>();
    private Circle fx_end_node;

    // konstanten
    private final String path_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css")).toExternalForm();
    private final String CSS_STYLE_CLASS_CIRCLE_MOEGLICHE_POS = "circle_moegliche_neue_position";
    private final String CSS_STYLE_CLASS_LINE_MOEGLICHE_POS = "linie_moegliche_neue_fahr_linie";
    private final String CSS_STYLE_CLASS_CIRCLE_FAHR_POS = "circle_fahr_position";
    private final String CSS_STYLE_CLASS_LINE_FAHR_LINIE = "linie_fahr_linie";
    private final String CSS_STYLE_CLASS_LINE_UNSICHTBAR = "linie_unsichtbar";
    public enum Fahrlinien_styles{
        FAHRLINIE,
        MOEGLICHER_ZUG_MIT_LINIE,
        MOEGLICHER_ZUG_OHNE_LINIE
    }

    // constructor
    public Fahrlinie(
            int start_x_grid,
            int start_y_grid,
            int delta_x_grid,
            int delta_y_grid,
            int strecke_breite_grid,
            int strecke_hoehe_grid,
            Umrechnung_grid_pixel umrechnung_grid_pixel,
            String farbe_spieler) {
        this.start_x_grid = start_x_grid;
        this.start_y_grid = start_y_grid;
        this.delta_x_grid = delta_x_grid;
        this.delta_y_grid = delta_y_grid;
        this.strecke_breite_grid = strecke_breite_grid;
        this.strecke_hoehe_grid = strecke_hoehe_grid;
        this.umrechnung_grid_pixel = umrechnung_grid_pixel;
        this.farbe_spieler = farbe_spieler;

        // endpunkte erzeugen
        // endpunkt x berechnen
        end_x_grid = (start_x_grid+delta_x_grid) % strecke_breite_grid;
        // wichtig, da java die bei negativem modulo negativen rest ausgibt, muss es positiv gemacht werden
        if(end_x_grid<0){
            end_x_grid += strecke_breite_grid;
        }else if(end_x_grid==0) {
            // position auf randlinie,
            //  da beide randlinien rechts und links die selbe position darstellen
            //  -> entscheiden ob neue position rechts oder links
            if (delta_x_grid > 0) {
                // fahrt nach rechts -> am rechten rand bleiben
                end_x_grid = strecke_breite_grid;
            } else if (delta_x_grid == 0) {
                // vertikale fahrt auf der linken oder rechten randlinie
                //  -> x nicht ändern
                end_x_grid = start_x_grid;
            }
        }
        // endpunkt y berechnen
        end_y_grid = (start_y_grid+delta_y_grid) % strecke_hoehe_grid;
        // wichtig, da java die bei negativem modulo negativen rest ausgibt, muss es positiv gemacht werden
        if(end_y_grid<0){
            end_y_grid += strecke_hoehe_grid;
        }else if(end_y_grid==0) {
            // position auf randlinie,
            //  da beide randlinien rechts und links die selbe position darstellen
            //  -> entscheiden ob neue position rechts oder links
            if (delta_y_grid > 0) {
                // fahrt nach rechts -> am rechten rand bleiben
                end_y_grid = strecke_hoehe_grid;
            } else if (delta_y_grid == 0) {
                // vertikale fahrt auf der linken oder rechten randlinie
                //  -> x nicht ändern
                end_y_grid = start_y_grid;
            }
        }

        // log erstellen
        System.out.println(
                "neue Fahrlinie erzeugt: x_start=" + this.get_start_x_grid() +
                        " y_start=" + this.get_start_y_grid() +
                        " | delta_x=" + this.get_delta_x_grid() +
                        " delta_y=" + this.get_delta_y_grid() +
                        " | x_end=" + this.get_end_x_grid() +
                        " y_end=" + this.get_end_y_grid()
        );

        // fx_nodes erzeugen
        ArrayList<Node> fx_nodes_for_zug = new ArrayList<>();
        generate_fx_nodes_for_zug(
                fx_nodes_for_zug,
                Fahrlinien_styles.FAHRLINIE,
                start_x_grid,
                start_y_grid,
                delta_x_grid,
                delta_y_grid
        );
        // fx nodes aufteilen in linien und dem endpunkt
        fx_lines.clear();
        for(Node akt_node : fx_nodes_for_zug){
            if(akt_node instanceof Line){
                fx_lines.add((Line)akt_node);
            }else if(akt_node instanceof Circle){
                fx_end_node = (Circle) akt_node;
            }else{
                // fehler node ist weder line noch circle
                throw new RuntimeException("Fehler: Node ist weder Line noch Circle!");
            }
        }
    }

    public ArrayList<Node> create_fxNodes_for_moegliche_neue_positionen(){
        System.out.println("Fahrlinie: create_fxNodes_for_moegliche_neue_positionen() -> start");
        // arraylist erzeugen, in der alle fx_nodes aller möglichen neuen positionen rein kommen
        ArrayList<Node> fx_nodes_moegliche_zuege = new ArrayList<>();

        // es gibt immer 9 mögliche positionen
        for(int delta_x=delta_x_grid-1 ; delta_x<=delta_x_grid+1 ; delta_x++){
            for(int delta_y=delta_y_grid-1 ; delta_y<=delta_y_grid+1 ; delta_y++){
                if(delta_x==delta_x_grid && delta_y==delta_y_grid){
                    // wiederholung der letzen fahrlinie
                    generate_fx_nodes_for_zug(
                            fx_nodes_moegliche_zuege,
                            Fahrlinien_styles.MOEGLICHER_ZUG_MIT_LINIE,
                            end_x_grid,
                            end_y_grid,
                            delta_x,
                            delta_y
                    );
                }else{
                    // keine wiederholung der letzen fahrlinie -> unsichtbare linie
                    generate_fx_nodes_for_zug(
                            fx_nodes_moegliche_zuege,
                            Fahrlinien_styles.MOEGLICHER_ZUG_OHNE_LINIE,
                            end_x_grid,
                            end_y_grid,
                            delta_x,
                            delta_y
                    );
                }
            }
        }

        // fx_nodes ausgeben
        return fx_nodes_moegliche_zuege;
    }

    public ArrayList<Punkt> calc_moegliche_neue_positionen(){
        // arraylist erzeugen, in dem alle möglichen neuen punkte rein kommen
        ArrayList<Punkt> moegliche_punkte = new ArrayList<>();

        // es gibt immer 9 mögliche positionen
        for(int akt_delta_x=delta_x_grid-1 ; akt_delta_x<=delta_x_grid+1 ; akt_delta_x++){
            for(int akt_delta_y=delta_y_grid-1 ; akt_delta_y<=delta_y_grid+1 ; akt_delta_y++){
                // moeglichen x wert berechnen
                int x = (end_x_grid+akt_delta_x) % strecke_breite_grid;
                // wichtig, da java die bei negativem modulo negativen rest ausgibt, muss es positiv gemacht werden
                if(x<0){
                    x += strecke_breite_grid;
                }else if(x==0){
                    // position auf randlinie,
                    //  da beide randlinien rechts und links die selbe position darstellen
                    //  -> entscheiden ob neue position rechts oder links
                    if(akt_delta_x>0){
                        // fahrt nach rechts -> am rechten rand bleiben
                        x = strecke_breite_grid;
                    }else if(akt_delta_x==0){
                        // vertikale fahrt auf der linken oder rechten randlinie
                        //  -> x nicht ändern
                        x = end_x_grid;
                    }
                }

                // moeglichen y wert berechnen
                int y = (end_y_grid+akt_delta_y) % strecke_hoehe_grid;
                // wichtig, da java die bei negativem modulo negativen rest ausgibt, muss es positiv gemacht werden
                if(y<0){
                    y += strecke_hoehe_grid;
                }else if(y==0) {
                    // position auf randlinie, da beide randlinien oben und unten die selbe position darstellen
                    //  -> entscheiden ob neue position oben oder unten
                    if (akt_delta_y > 0) {
                        // fahrt nach unten -> am unteren rand bleiben
                        y = strecke_hoehe_grid;
                    } else if (akt_delta_y == 0) {
                        // horizontale fahrt auf der oberen oder unteren randlinie
                        //  -> y nicht ändern
                        y = end_y_grid;
                    }
                }

                // punkt erzeugen
                Punkt punkt = new Punkt(x, y);

                // punkt der arraylist übergeben
                moegliche_punkte.add(punkt);
            }
        }

        // arraylist ausgeben
        return moegliche_punkte;
    }

    public Fahrlinie create_next_fahrlinie(int x_neu_grid, int y_neu_grid){
        // delta_x und delta_y berechnen
        int delta_x = -999;
        int delta_y = -999;
        // es gibt immer 9 mögliche positionen
        boolean valider_neuer_punkt = false;
        for(int akt_delta_x=delta_x_grid-1 ; akt_delta_x<=delta_x_grid+1 ; akt_delta_x++){
            for(int akt_delta_y=delta_y_grid-1 ; akt_delta_y<=delta_y_grid+1 ; akt_delta_y++){
                // moeglichen x wert berechnen
                int x = (end_x_grid + akt_delta_x) % strecke_breite_grid;
                // wichtig, da java die bei negativem modulo negativen rest ausgibt, muss es positiv gemacht werden
                if(x<0){
                    x += strecke_breite_grid;
                }else if(x==0){
                    // position auf randlinie, da beide randlinien rechts und links die selbe position darstellen
                    //  -> entscheiden ob neue position rechts oder links
                    if(akt_delta_x>0) {
                        // fahrt nach rechts -> am rechten rand bleiben
                        x = strecke_breite_grid;
                    }else if(akt_delta_x==0){
                        // vertikale fahrt auf der linken oder rechten randlinie
                        //  -> x nicht ändern
                        x = end_x_grid;
                    }
                }

                // moeglichen y wert berechnen
                int y = (end_y_grid + akt_delta_y) % strecke_hoehe_grid;
                // wichtig, da java die bei negativem modulo negativen rest ausgibt, muss es positiv gemacht werden
                if(y<0){
                    y += strecke_hoehe_grid;
                }else if(y==0){
                    // position auf randlinie, da beide randlinien oben und unten die selbe position darstellen
                    //  -> entscheiden ob neue position oben oder unten
                    if(akt_delta_y>0) {
                        // fahrt nach unten -> am unteren rand bleiben
                        y = strecke_hoehe_grid;
                    }else if(akt_delta_y==0){
                        // horizontale fahrt auf der oberen oder unteren randlinie
                        //  -> y nicht ändern
                        y = end_y_grid;
                    }
                }

                // test ob x und y mit eingabewerten übereinstimmen
                if(x==x_neu_grid && y==y_neu_grid){
                    delta_x = akt_delta_x;
                    delta_y = akt_delta_y;
                    valider_neuer_punkt = true;
                    break;
                }
            }
        }

        if(valider_neuer_punkt) {
            // neue fahrlinie erzeugen
            return new Fahrlinie(
                    end_x_grid,
                    end_y_grid,
                    delta_x,
                    delta_y,
                    strecke_breite_grid,
                    strecke_hoehe_grid,
                    umrechnung_grid_pixel,
                    farbe_spieler
            );
        }else{
            // kein valider neuer punkt -> fehler ausgeben
            throw new RuntimeException("übergebener Punkt ist keine erlaubte neue Fahrposition!");
        }
    }

    /**
     * der methode werden eine startposition übergeben, die im spielfeld liegt und delta_x und delta_y
     *  der methoden aufruf kümmert sich nur um die linie die sich noch im spielfeld befindet,
     *  sobald ein randduchgang stattfindet, ruft sich die methode mit angepassten werten erneut auf
     *  -> rekursive methode
     * @param fx_nodes_for_zug
     * @param fahrlinien_style
     * @param start_x_grid
     * @param start_y_grid
     * @param delta_x_grid
     * @param delta_y_grid
     */
    private void generate_fx_nodes_for_zug(
            ArrayList<Node> fx_nodes_for_zug,
            Fahrlinien_styles fahrlinien_style,
            double start_x_grid,
            double start_y_grid,
            double delta_x_grid,
            double delta_y_grid){
        System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> start");

        double end_x_grid = start_x_grid + delta_x_grid;
        double end_y_grid = start_y_grid + delta_y_grid;

        if( end_x_grid >= 0 && end_x_grid <= strecke_breite_grid &&
            end_y_grid >= 0 && end_y_grid <= strecke_hoehe_grid) {
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> keinen randdurchgang");
            // es gibt keine randdurchgänge -> abbruch bedingung für rekursion
            // fahrlinie erzeugen
            Line line = create_fx_line(
                            start_x_grid,
                            start_y_grid,
                            end_x_grid,
                            end_y_grid,
                            fahrlinien_style
            );
            // prüfen ob line null ist, das ist je nach da fahrlinien_style der fall
            if(line != null){
                // line der arraylist hinzufügen
                fx_nodes_for_zug.add(line);
            }
            // endpunkt erzeugen
            Circle endpunkt = create_fx_circle(end_x_grid, end_y_grid,fahrlinien_style);
            fx_nodes_for_zug.add(endpunkt);
            // rekursion beenden
            return;
        }

        // es gibt mindestens einen randduchgang
        System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> min 1 randdurchgang");
        if(delta_x_grid == 0){
            // vertikale linie
            if(delta_y_grid>0){
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> vertikale linie nach unten");
                // vertikale linie nach unten
                // randdurchgang unten
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        end_x_grid,
                        strecke_hoehe_grid,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = start_x_grid;
                double delta_x_grid_neu = delta_x_grid; // ==0
                double start_y_grid_neu = 0;
                double delta_y_grid_neu = delta_y_grid - (strecke_hoehe_grid - start_y_grid);
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            }else if(delta_y_grid<0){
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> vertikale linie nach oben");
                // vertikale linie nach oben
                // randdurchgang oben bei null linie
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        end_x_grid,
                        0,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = start_x_grid;
                double delta_x_grid_neu = delta_x_grid; // ==0
                double start_y_grid_neu = strecke_hoehe_grid;
                double delta_y_grid_neu = delta_y_grid + start_y_grid;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            }else{
                // unvorhergesehener zustand
                throw new RuntimeException("unvorhergesehener zustand im else!");
            }
        }else if(delta_x_grid>0 && delta_y_grid==0){
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> line nur nach rechts");
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang rechts");
            // randduchgang rechts
            // fahrlinie erzeugen
            Line line = create_fx_line(
                    start_x_grid,
                    start_y_grid,
                    strecke_breite_grid,
                    start_y_grid,
                    fahrlinien_style
            );
            // prüfen ob line null ist
            if(line != null){
                // line der arraylist hinzufügen
                fx_nodes_for_zug.add(line);
            }
            // rekursiver aufruf
            double start_x_grid_neu = 0;
            double delta_x_grid_neu = start_x_grid + delta_x_grid -strecke_breite_grid;
            double start_y_grid_neu = start_y_grid;
            double delta_y_grid_neu = 0;
            generate_fx_nodes_for_zug(
                    fx_nodes_for_zug,
                    fahrlinien_style,
                    start_x_grid_neu,
                    start_y_grid_neu,
                    delta_x_grid_neu,
                    delta_y_grid_neu
            );
        }else if(delta_x_grid>0 && delta_y_grid>0){
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> line nach rechts unten");
            // line nach rechts unten
            // y wert berechnen für den rechten randdurchgang => y = f(x=b)
            // formel y = f(x) = y_s + delta_y / delta_x * (x- x_s)
            double y_rechter_rand = start_y_grid + delta_y_grid/delta_x_grid * (strecke_breite_grid - start_x_grid);
            // x wert berechnen für den unteren randduchgang => x = f(y=h)
            // formel x = f(y) = (y - y_s) * delta_x / delta_y + x_s
            double x_unterer_rand = (strecke_hoehe_grid - start_y_grid) * delta_x_grid/delta_y_grid + start_x_grid;
            // randdurchgänge testen, indem geschaut wird ob berechneten die werte noch im spielfeld liegen
            if(y_rechter_rand<=strecke_hoehe_grid && x_unterer_rand<=strecke_breite_grid){
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang am eckpunkt (unten-rechts)");
                // randduchgang am eckpunkt (unten-rechts) des spielfelds
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        strecke_breite_grid,
                        strecke_hoehe_grid,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = 0;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - strecke_breite_grid;
                double start_y_grid_neu = 0;
                double delta_y_grid_neu = delta_y_grid - (strecke_hoehe_grid - start_y_grid);
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            }else if(y_rechter_rand <= strecke_hoehe_grid){
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang nur rechts");
                // randduchgang rechts
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        strecke_breite_grid,
                        y_rechter_rand,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = 0;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - strecke_breite_grid;
                double start_y_grid_neu = y_rechter_rand;
                double delta_y_grid_neu = delta_y_grid + start_y_grid - y_rechter_rand;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            }else if (x_unterer_rand <= strecke_breite_grid){
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang nur unten");
                // randduchgang unten
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        x_unterer_rand,
                        strecke_hoehe_grid,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = x_unterer_rand;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - x_unterer_rand;
                double start_y_grid_neu = 0;
                double delta_y_grid_neu = delta_y_grid - (strecke_hoehe_grid - start_y_grid);
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            }else{
                // unverhergesehener zustand
                throw new RuntimeException("unverhergesehener zustand im else!");
            }
        }else if(delta_x_grid>0 && delta_y_grid<0) {
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> line nach rechts oben");
            // line nach rechts oben
            // y wert berechnen für den rechten randdurchgang => y = f(x=b)
            // formel y = f(x) = y_s + delta_y / delta_x * (x- x_s)
            double y_rechter_rand = start_y_grid + delta_y_grid / delta_x_grid * (strecke_breite_grid - start_x_grid);
            // x wert berechnen für den unteren randduchgang => x = f(y=0)
            // formel x = f(y) = (y - y_s) * delta_x / delta_y + x_s
            double x_oberer_rand = (0 - start_y_grid) * delta_x_grid / delta_y_grid + start_x_grid;
            // randdurchgänge testen, indem geschaut wird ob die berechneten werte noch im spielfeld liegen
            if (y_rechter_rand >= 0 && x_oberer_rand <= strecke_breite_grid) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang am eckpunkt (oben-rechts)");
                // randduchgang am eckpunkt (oben-rechts) des spielfelds
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        strecke_breite_grid,
                        0,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = 0;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - strecke_breite_grid;
                double start_y_grid_neu = strecke_hoehe_grid;
                double delta_y_grid_neu = delta_y_grid + start_y_grid;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else if (y_rechter_rand >= 0) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang rechts");
                // randduchgang rechts
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        strecke_breite_grid,
                        y_rechter_rand,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = 0;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - strecke_breite_grid;
                double start_y_grid_neu = y_rechter_rand;
                double delta_y_grid_neu = delta_y_grid + (start_y_grid - y_rechter_rand);
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else if (x_oberer_rand <= strecke_breite_grid) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang oben");
                // randduchgang oben
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        x_oberer_rand,
                        0,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = x_oberer_rand;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - x_oberer_rand;
                double start_y_grid_neu = strecke_hoehe_grid;
                double delta_y_grid_neu = delta_y_grid + start_y_grid;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else {
                // unverhergesehener zustand
                throw new RuntimeException("unverhergesehener zustand im else!");
            }
        }else if(delta_x_grid<0 && delta_y_grid==0){
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> line nur nach links");
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang links");
            // randduchgang links
            // fahrlinie erzeugen
            Line line = create_fx_line(
                    start_x_grid,
                    start_y_grid,
                    0,
                    start_y_grid,
                    fahrlinien_style
            );
            // prüfen ob line null ist
            if(line != null){
                // line der arraylist hinzufügen
                fx_nodes_for_zug.add(line);
            }
            // rekursiver aufruf
            double start_x_grid_neu = strecke_breite_grid;
            double delta_x_grid_neu = delta_x_grid + start_x_grid;
            double start_y_grid_neu = start_y_grid;
            double delta_y_grid_neu = 0;
            generate_fx_nodes_for_zug(
                    fx_nodes_for_zug,
                    fahrlinien_style,
                    start_x_grid_neu,
                    start_y_grid_neu,
                    delta_x_grid_neu,
                    delta_y_grid_neu
            );
        }else if(delta_x_grid<0 && delta_y_grid<0) {
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> line nach links oben");
            // line nach links oben
            // y wert berechnen für den linken randdurchgang => y = f(x=0)
            // formel y = f(x) = y_s + delta_y / delta_x * (x - x_s)
            double y_linker_rand = start_y_grid + delta_y_grid / delta_x_grid * (0 - start_x_grid);
            // x wert berechnen für den oberen randduchgang => x = f(y=0)
            // formel x = f(y) = (y - y_s) * delta_x / delta_y + x_s
            double x_oberer_rand = (0 - start_y_grid) * delta_x_grid / delta_y_grid + start_x_grid;
            // randdurchgänge testen, indem geschaut wird ob die berechneten werte noch im spielfeld liegen
            if (y_linker_rand >= 0 && x_oberer_rand >= 0) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang am eckpunkt (oben-links)");
                // randduchgang am eckpunkt (oben-links) des spielfelds
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        0,
                        0,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = strecke_breite_grid;
                double delta_x_grid_neu = delta_x_grid + start_x_grid;
                double start_y_grid_neu = strecke_hoehe_grid;
                double delta_y_grid_neu = delta_y_grid + start_y_grid;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else if (y_linker_rand >= 0) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang links");
                // randduchgang links
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        0,
                        y_linker_rand,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = strecke_breite_grid;
                double delta_x_grid_neu = delta_x_grid + start_x_grid;
                double start_y_grid_neu = y_linker_rand;
                double delta_y_grid_neu = delta_y_grid + (start_y_grid - y_linker_rand);
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else if (x_oberer_rand >= 0) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang oben");
                // randduchgang oben
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        x_oberer_rand,
                        0,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = x_oberer_rand;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - x_oberer_rand;
                double start_y_grid_neu = strecke_hoehe_grid;
                double delta_y_grid_neu = delta_y_grid + start_y_grid;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else {
                // unverhergesehener zustand
                throw new RuntimeException("unverhergesehener zustand im else!");
            }
        }else if(delta_x_grid<0 && delta_y_grid>0) {
            System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> line nach links unten");
            // line nach links unten
            // y wert berechnen für den linken randdurchgang => y = f(x=0)
            // formel y = f(x) = y_s + delta_y / delta_x * (x - x_s)
            double y_linker_rand = start_y_grid + delta_y_grid / delta_x_grid * (0 - start_x_grid);
            // x wert berechnen für den unteren randduchgang => x = f(y=0)
            // formel x = f(y) = (y - y_s) * delta_x / delta_y + x_s
            double x_unterer_rand = (strecke_hoehe_grid - start_y_grid) * delta_x_grid / delta_y_grid + start_x_grid;
            // randdurchgänge testen, indem geschaut wird ob die berechneten werte noch im spielfeld liegen
            if (y_linker_rand <=strecke_hoehe_grid && x_unterer_rand >= 0) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang am eckpunkt (unten-links)");
                // randduchgang am eckpunkt (unten-links) des spielfelds
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        0,
                        strecke_hoehe_grid,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = strecke_breite_grid;
                double delta_x_grid_neu = delta_x_grid + start_x_grid;
                double start_y_grid_neu = 0;
                double delta_y_grid_neu = delta_y_grid + start_y_grid - strecke_hoehe_grid;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else if (y_linker_rand <= strecke_hoehe_grid) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang links");
                // randduchgang links
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        0,
                        y_linker_rand,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = strecke_breite_grid;
                double delta_x_grid_neu = delta_x_grid + start_x_grid;
                double start_y_grid_neu = y_linker_rand;
                double delta_y_grid_neu = delta_y_grid + start_y_grid - y_linker_rand;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else if (x_unterer_rand >= 0) {
                System.out.println("Fahrlinie: generate_fx_nodes_for_zug() -> randduchgang unten");
                // randduchgang unten
                // fahrlinie erzeugen
                Line line = create_fx_line(
                        start_x_grid,
                        start_y_grid,
                        x_unterer_rand,
                        strecke_hoehe_grid,
                        fahrlinien_style
                );
                // prüfen ob line null ist
                if(line != null){
                    // line der arraylist hinzufügen
                    fx_nodes_for_zug.add(line);
                }
                // rekursiver aufruf
                double start_x_grid_neu = x_unterer_rand;
                double delta_x_grid_neu = delta_x_grid + start_x_grid - x_unterer_rand;
                double start_y_grid_neu = 0;
                double delta_y_grid_neu = delta_y_grid + start_y_grid - strecke_hoehe_grid;
                generate_fx_nodes_for_zug(
                        fx_nodes_for_zug,
                        fahrlinien_style,
                        start_x_grid_neu,
                        start_y_grid_neu,
                        delta_x_grid_neu,
                        delta_y_grid_neu
                );
            } else {
                // unverhergesehener zustand
                throw new RuntimeException("unverhergesehener zustand im else!");
            }
        }
    }

    @Nullable
    private Line create_fx_line(
            double start_x_grid,
            double start_y_grid,
            double end_x_grid,
            double end_y_grid,
            Fahrlinien_styles fahrlinien_styles
    ){
        // fahrlinie erzeugen
        Line line = new Line();
        line.setStartX(umrechnung_grid_pixel.posXGrid_to_posXPixel(start_x_grid));
        line.setStartY(umrechnung_grid_pixel.posYGrid_to_posYPixel(start_y_grid));
        line.setEndX(umrechnung_grid_pixel.posXGrid_to_posXPixel(end_x_grid));
        line.setEndY(umrechnung_grid_pixel.posYGrid_to_posYPixel(end_y_grid));
        if(fahrlinien_styles == Fahrlinien_styles.FAHRLINIE){
            line.getStyleClass().add(CSS_STYLE_CLASS_LINE_FAHR_LINIE);
            line.setStyle("-fx-stroke: " + farbe_spieler + ";");
            // bei tatsächlichen fahlinien werden zusätzlich noch die Linien (grid) gespeichert
            Linie grid_linie = new Linie(start_x_grid, start_y_grid, end_x_grid, end_y_grid);
            grid_linien.add(grid_linie);
        }else if(fahrlinien_styles == Fahrlinien_styles.MOEGLICHER_ZUG_MIT_LINIE){
            line.getStyleClass().add(CSS_STYLE_CLASS_LINE_MOEGLICHE_POS);
            line.setStyle("-fx-stroke: " + farbe_spieler + ";");
        }else if(fahrlinien_styles == Fahrlinien_styles.MOEGLICHER_ZUG_OHNE_LINIE){
            // es soll keine linie gezeichnet werden
            line = null;
        }else{
            throw new RuntimeException("unerwarteter Wert für Fahrlinien_styles");
        }

        // line oder null ausgeben
        return line;
    }

    private Circle create_fx_circle(
            double x_grid,
            double y_grid,
            Fahrlinien_styles fahrlinien_style
    ){
        // endpunkt erzeugen
        Circle endpunkt = new Circle();
        endpunkt.setCenterX(umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid));
        endpunkt.setCenterY(umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid));

        if(fahrlinien_style == Fahrlinien_styles.FAHRLINIE){
            endpunkt.setRadius(umrechnung_grid_pixel.B_1GRID_IN_PIXEL * 0.2);
            endpunkt.getStyleClass().add(CSS_STYLE_CLASS_CIRCLE_FAHR_POS);
            endpunkt.setStyle("-fx-fill: " + farbe_spieler + ";");
        }else if(fahrlinien_style == Fahrlinien_styles.MOEGLICHER_ZUG_MIT_LINIE ||
                 fahrlinien_style == Fahrlinien_styles.MOEGLICHER_ZUG_OHNE_LINIE){
            endpunkt.setRadius(umrechnung_grid_pixel.B_1GRID_IN_PIXEL * 0.1);
            endpunkt.getStyleClass().add(CSS_STYLE_CLASS_CIRCLE_MOEGLICHE_POS);
            endpunkt.setStyle("-fx-stroke: " + farbe_spieler + ";");
        }else{
            throw new RuntimeException("unerwarteter Wert für Fahrlinien_styles");
        }

        // line ausgeben
        return endpunkt;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // getter
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public int get_start_x_grid() {
        return start_x_grid;
    }

    public int get_start_y_grid() {
        return start_y_grid;
    }

    public int get_delta_x_grid() {
        return delta_x_grid;
    }

    public int get_delta_y_grid() {
        return delta_y_grid;
    }

    public int get_end_x_grid() {
        return end_x_grid;
    }

    public int get_end_y_grid() {
        return end_y_grid;
    }

    public ArrayList<Node> get_fx_nodes(){
        // alle fx_modes der aktuellen fahrlinie in arraylist
        ArrayList<Node> fx_nodes = new ArrayList<>();
        fx_nodes.addAll(fx_lines);
        fx_nodes.add(fx_end_node);

        // arraylist ausgeben
        return fx_nodes;
    }

    public ArrayList<Linie> get_grid_linien() {
        return grid_linien;
    }
}
