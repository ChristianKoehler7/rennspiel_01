package de.christian_koehler_iu.rennspiel.controller_helper;

import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.utility.Umrechnung_grid_pixel;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Rennen_strecke_zeichnen {
    // class attributes

    // object attributes
    private final Umrechnung_grid_pixel umrechnung_grid_pixel;
    private final Rennstrecke rennstrecke;
    private final Group group_strecke;

    private final String path_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css")).toExternalForm();
    private final String CSS_STYLE_CLASS_RECHTECK_HINTERGRUND = "rechteck_rennen_hintergrund";
    private final String CSS_STYLE_CLASS_LINIE_GITTER = "linie_rennen_gitter";
    private final String CSS_STYLE_CLASS_LINIE_STRECKE = "linie_rennen_strecke";
    private final String CSS_STYLE_CLASS_LINIE_START = "linie_rennen_start";
    private final String CSS_STYLE_CLASS_POLYGON_STARTRICHTUNG = "polygon_rennen_startrichting";
    private final String CSS_STYLE_CLASS_POLYGON_RANDDURCHGANG = "polygon_rennen_randdurchgang";

    // speicher für fx-elemente
    private final Rectangle rechteck_hintergrund = new Rectangle();
    private final ArrayList<Line> linien_gitter = new ArrayList<>();
    private final HashMap<Linie, Line> map_streckenlinie_fxline = new HashMap<>();
    private final Line line_start = new Line();
    private final ArrayList<Polygon> polygone_start_richtung = new ArrayList<>();
    private final ArrayList<Polygon> polygone_rand_durchgaenge = new ArrayList<>();

    // constructor
    public Rennen_strecke_zeichnen(Umrechnung_grid_pixel umrechnung_grid_pixel, Rennstrecke rennstrecke, Group group_strecke) {
        this.umrechnung_grid_pixel = umrechnung_grid_pixel;
        this.rennstrecke = rennstrecke;
        this.group_strecke = group_strecke;

        // hintergrung und gitterlinien zeichen
        init_gitter_und_hintergrund();

        // streckenlinien zeichnen
        init_strecken_linien_zeichnen();

        // startlinie zeichnen
        init_start_linie_zeichnen();

        // startrichtung zeichnen
        init_start_richtung_zeichnen();

        // rand durchgänge zeichnen
        init_rand_durchgaenge_zeichnen();
    }

    private void init_gitter_und_hintergrund(){
        // streckenfläche zeichen
        // Erstelle ein Rechteck
        rechteck_hintergrund.setX(0.0); // X-Position des Rechtecks
        rechteck_hintergrund.setY(0.0); // Y-Position des Rechtecks
        rechteck_hintergrund.setWidth(umrechnung_grid_pixel.get_b_strecke_pixel_mit_rand()); // Breite des Rechtecks
        rechteck_hintergrund.setHeight(umrechnung_grid_pixel.get_h_strecke_pixel_mit_rand()); // Höhe des Rechtecks
        rechteck_hintergrund.getStyleClass().add(CSS_STYLE_CLASS_RECHTECK_HINTERGRUND);
        // rechteck dem group-element hinzufügen
        group_strecke.getChildren().add(rechteck_hintergrund);

        // gitter zeichen
        int strecken_breite = rennstrecke.getBreite();
        int strecken_hoehe = rennstrecke.getHoehe();
        // vertikale gitterlinien zeichen
        for(int i=0 ; i<strecken_breite+1 ; i++){
            int x0 = i;
            int y0 = 0;
            int x1 = i;
            int y1 = strecken_hoehe;
            Line aktLine = new Line(umrechnung_grid_pixel.posXGrid_to_posXPixel(x0),
                    umrechnung_grid_pixel.posYGrid_to_posYPixel(y0),
                    umrechnung_grid_pixel.posXGrid_to_posXPixel(x1),
                    umrechnung_grid_pixel.posYGrid_to_posYPixel(y1));
            aktLine.getStyleClass().add(CSS_STYLE_CLASS_LINIE_GITTER);
            // linie dem group-element hinzufügen
            group_strecke.getChildren().add(aktLine);
            // linie der arraylist für gitterlinien hinzufügen
            linien_gitter.add(aktLine);
        }
        // horizontale gitterlinien zeichen
        for(int i=0 ; i<strecken_hoehe+1 ; i++){
            int x0 = 0;
            int y0 = i;
            int x1 = strecken_breite;
            int y1 = i;
            Line aktLine = new Line(umrechnung_grid_pixel.posXGrid_to_posXPixel(x0),
                    umrechnung_grid_pixel.posYGrid_to_posYPixel(y0),
                    umrechnung_grid_pixel.posXGrid_to_posXPixel(x1),
                    umrechnung_grid_pixel.posYGrid_to_posYPixel(y1));
            aktLine.getStyleClass().add(CSS_STYLE_CLASS_LINIE_GITTER);
            // linie dem group-element hinzufügen
            group_strecke.getChildren().add(aktLine);
            // linie der arraylist für gitterlinien hinzufügen
            linien_gitter.add(aktLine);
        }
    }

    private void init_strecken_linien_zeichnen(){
        // strecken lines zeichen
        for(Linie akt_strecken_linie : rennstrecke.getStreckenlinien()){
            Line akt_line = new Line();
            akt_line.getStyleClass().add(CSS_STYLE_CLASS_LINIE_STRECKE);
            akt_line.setStartX(umrechnung_grid_pixel.posXGrid_to_posXPixel(akt_strecken_linie.getP0().getX()));
            akt_line.setStartY(umrechnung_grid_pixel.posYGrid_to_posYPixel(akt_strecken_linie.getP0().getY()));
            akt_line.setEndX(umrechnung_grid_pixel.posXGrid_to_posXPixel(akt_strecken_linie.getP1().getX()));
            akt_line.setEndY(umrechnung_grid_pixel.posYGrid_to_posYPixel(akt_strecken_linie.getP1().getY()));
            // linie dem group-element hinzufügen
            group_strecke.getChildren().add(akt_line);
            // line der arraylist hinzufügen
            map_streckenlinie_fxline.put(akt_strecken_linie, akt_line);
        }
    }

    private void init_start_linie_zeichnen(){
        if(rennstrecke.getStartlinie() == null){
            throw new RuntimeException("Startlinie fehlt!");
        }
        // start line zeichen
        line_start.getStyleClass().add(CSS_STYLE_CLASS_LINIE_START);
        line_start.setStartX(umrechnung_grid_pixel.posXGrid_to_posXPixel(rennstrecke.getStartlinie().getP0().getX()));
        line_start.setStartY(umrechnung_grid_pixel.posYGrid_to_posYPixel(rennstrecke.getStartlinie().getP0().getY()));
        line_start.setEndX(umrechnung_grid_pixel.posXGrid_to_posXPixel(rennstrecke.getStartlinie().getP1().getX()));
        line_start.setEndY(umrechnung_grid_pixel.posYGrid_to_posYPixel(rennstrecke.getStartlinie().getP1().getY()));
        // linie dem group-element hinzufügen
        group_strecke.getChildren().add(line_start);
    }


    private void init_start_richtung_zeichnen(){
        if(rennstrecke.getStartlinie() == null) {
            throw new RuntimeException("Startlinie fehlt!");
        }

        // neue startrichtungs linien erstellen

        // startpunkt ermitteln an dem startlinie beginnt
        int x0_grid_startLinie = rennstrecke.getStartlinie().getP0().getX();
        int y0_grid_startLinie = rennstrecke.getStartlinie().getP0().getY();
        int x1_grid_startLinie = rennstrecke.getStartlinie().getP1().getX();
        int y1_grid_startLinie = rennstrecke.getStartlinie().getP1().getY();

        // startlinie horizontal oder vertikal
        boolean is_startlinie_horizontal;
        if(y0_grid_startLinie == y1_grid_startLinie){
            is_startlinie_horizontal = true;
        }else if(x0_grid_startLinie == x1_grid_startLinie){
            is_startlinie_horizontal = false;
        }else{
            throw new RuntimeException("Unerwarteter Fehler beim Zeichnen der Startrichtung!");
        }

        // linker oberer punkt der startlinie ist der startpunkt
        int x_min_grid_startLinie = x0_grid_startLinie<x1_grid_startLinie ? x0_grid_startLinie : x1_grid_startLinie;
        int y_min_grid_startLinie = y0_grid_startLinie<y1_grid_startLinie ? y0_grid_startLinie : y1_grid_startLinie;
        int x_max_grid_startLinie = x0_grid_startLinie>x1_grid_startLinie ? x0_grid_startLinie : x1_grid_startLinie;
        int y_max_grid_startLinie = y0_grid_startLinie>y1_grid_startLinie ? y0_grid_startLinie : y1_grid_startLinie;

        if(is_startlinie_horizontal){
            for(int akt_x_grid=x_min_grid_startLinie ; akt_x_grid<x_max_grid_startLinie ; akt_x_grid++){
                polygone_start_richtung.add(
                        this.create_dreieck(akt_x_grid,
                                y_min_grid_startLinie,
                                is_startlinie_horizontal,
                                rennstrecke.get_is_startrichtung_nach_unten_oder_rechts(),
                                CSS_STYLE_CLASS_POLYGON_STARTRICHTUNG)
                );
            }
        }else { // starlinie ist vertikal
            for(int akt_y_grid=y_min_grid_startLinie ; akt_y_grid<y_max_grid_startLinie ; akt_y_grid++){
                polygone_start_richtung.add(
                        this.create_dreieck(x_min_grid_startLinie,
                                akt_y_grid,
                                is_startlinie_horizontal,
                                rennstrecke.get_is_startrichtung_nach_unten_oder_rechts(),
                                CSS_STYLE_CLASS_POLYGON_STARTRICHTUNG)
                );
            }
        }

        // lines der group hinzufügen
        group_strecke.getChildren().addAll(polygone_start_richtung);
    }

    private void init_rand_durchgaenge_zeichnen(){
        int strecken_breite = rennstrecke.getBreite();
        int strecken_hoehe = rennstrecke.getHoehe();

        // vertikale ränder durchlaufen
        for(int akt_y_grid=0 ; akt_y_grid<strecken_hoehe ; akt_y_grid++){
            // links und rechts werden punkte erzeugt, die in der y-mitte der kästchen liegen
            double x_linker_randpunkt = 0;
            double y_linker_randpunkt = akt_y_grid+0.5;
            double x_rechter_randpunkt = (double)strecken_breite;
            double y_rechter_randpunkt = akt_y_grid+0.5;

            // flag, ob durchgang vorhanden
            boolean is_rand_durchgang_vorhanden = true;

            // alle streckenlinien durchgehen und testen, ob eine der linien auf einem der akt randpunkte liegt
            for(Linie akt_strecken_linie : rennstrecke.getStreckenlinien()){
                if(akt_strecken_linie.liegt_punkt_auf_linie(x_linker_randpunkt, y_linker_randpunkt)
                        || akt_strecken_linie.liegt_punkt_auf_linie(x_rechter_randpunkt, y_rechter_randpunkt)){
                    is_rand_durchgang_vorhanden = false;
                    break;
                }
            }

            if(is_rand_durchgang_vorhanden){
                // durchgangselemente zeichnen
                Polygon dreieck_links = this.create_dreieck(0, akt_y_grid,
                        false, false,
                        CSS_STYLE_CLASS_POLYGON_RANDDURCHGANG);
                Polygon dreieck_rechts = this.create_dreieck(strecken_breite, akt_y_grid,
                        false, true,
                        CSS_STYLE_CLASS_POLYGON_RANDDURCHGANG);

                // dreiecke dem group-element geben
                group_strecke.getChildren().addAll(dreieck_links, dreieck_rechts);

                // dreiecke in arraylist speichern
                polygone_rand_durchgaenge.add(dreieck_links);
                polygone_rand_durchgaenge.add(dreieck_rechts);
            }
        }

        // horizontale ränder durchlaufen
        for(int akt_x_grid=0 ; akt_x_grid<strecken_breite ; akt_x_grid++){
            // oben und unten werden punkte erzeugt, die in der x-mitte der kästchen liegen
            double x_oberer_randpunkt = akt_x_grid+0.5;
            double y_oberer_randpunkt = 0;
            double x_unterer_randpunkt = akt_x_grid+0.5;
            double y_unterer_randpunkt = strecken_hoehe;

            // flag, ob durchgang vorhanden
            boolean is_rand_durchgang_vorhanden = true;

            // alle streckenlinien durchgehen und testen, ob eine der linien auf einem der akt randpunkte liegt
            for(Linie akt_strecken_linie : rennstrecke.getStreckenlinien()){
                if(akt_strecken_linie.liegt_punkt_auf_linie(x_oberer_randpunkt, y_oberer_randpunkt)
                        || akt_strecken_linie.liegt_punkt_auf_linie(x_unterer_randpunkt, y_unterer_randpunkt)){
                    is_rand_durchgang_vorhanden = false;
                    break;
                }
            }

            if(is_rand_durchgang_vorhanden){
                // durchgangselemente zeichnen
                Polygon dreieck_oben = this.create_dreieck(akt_x_grid, 0,
                        true, false,
                        CSS_STYLE_CLASS_POLYGON_RANDDURCHGANG);
                Polygon dreieck_unten = this.create_dreieck(akt_x_grid, strecken_hoehe,
                        true, true,
                        CSS_STYLE_CLASS_POLYGON_RANDDURCHGANG);

                // dreiecke dem group-element geben
                group_strecke.getChildren().addAll(dreieck_oben, dreieck_unten);

                // dreiecke in arraylist speichern
                polygone_rand_durchgaenge.add(dreieck_oben);
                polygone_rand_durchgaenge.add(dreieck_unten);
            }
        }
    }

    public Rectangle getRechteck_hintergrund() {
        return rechteck_hintergrund;
    }

    public ArrayList<Line> getLinien_gitter() {
        return linien_gitter;
    }

    public HashMap<Linie, Line> getMap_streckenlinie_fxline() {
        return map_streckenlinie_fxline;
    }

    public Line getLine_start() {
        return line_start;
    }

    public ArrayList<Polygon> getPolygone_start_richtung() {
        return polygone_start_richtung;
    }

    public ArrayList<Polygon> getPolygone_rand_durchgaenge() {
        return polygone_rand_durchgaenge;
    }

    private Polygon create_dreieck(int x_grid_baseline_left, int y_grid_baseline_up,
                                   boolean is_baseline_horizontal, boolean is_richtung_unten_oder_rechts,
                                   String css_style_class){
        // erster punkt der baseline in parameter

        // zweiten punkt der baseline berechnen
        int x_grid_baseline_right;
        int y_grid_baseline_down;
        if(is_baseline_horizontal){
            // baseline horizontal
            x_grid_baseline_right = x_grid_baseline_left + 1;
            y_grid_baseline_down = y_grid_baseline_up;
        }else{ // baseline vertikal
            x_grid_baseline_right = x_grid_baseline_left;
            y_grid_baseline_down = y_grid_baseline_up + 1;
        }

        // punkt der dreieckspitze berechnen
        double x_grid_spitze;
        double y_grid_spitze;

        if(is_baseline_horizontal && is_richtung_unten_oder_rechts){
            // baseline horizontal
            // richtung unten
            x_grid_spitze = (double) x_grid_baseline_left + 0.5;
            y_grid_spitze = (double) y_grid_baseline_up + 0.5;

        }else if(is_baseline_horizontal && !is_richtung_unten_oder_rechts){
            // baseline horizontal
            // richtung oben
            x_grid_spitze = (double) x_grid_baseline_left + 0.5;
            y_grid_spitze = (double) y_grid_baseline_up - 0.5;
        }else if(!is_baseline_horizontal && is_richtung_unten_oder_rechts){
            // baseline vertikal
            // richtung rechts
            x_grid_spitze = (double) x_grid_baseline_left + 0.5;
            y_grid_spitze = (double) y_grid_baseline_up + 0.5;
        }else{
            // baseline vertikal
            // richtung links
            x_grid_spitze = (double) x_grid_baseline_left - 0.5;
            y_grid_spitze = (double) y_grid_baseline_up + 0.5;
        }

        // dreieck erstellen
        Polygon dreieck = new Polygon(
                this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_baseline_left), // x0
                this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_baseline_up), // y0
                this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_baseline_right), // x1
                this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_baseline_down), // y1
                this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_spitze), // x2
                this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_spitze) // y2
        );

        // style class setzen
        dreieck.getStyleClass().add(css_style_class);

        // dreicke ausgeben
        return dreieck;
    }
}

