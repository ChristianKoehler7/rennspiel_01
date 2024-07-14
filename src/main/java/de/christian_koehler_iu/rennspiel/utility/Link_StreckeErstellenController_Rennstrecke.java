package de.christian_koehler_iu.rennspiel.utility;

import de.christian_koehler_iu.rennspiel.controller.StreckeErstellenController;
import de.christian_koehler_iu.rennspiel.datasets.Linie;
import de.christian_koehler_iu.rennspiel.datasets.Rennstrecke;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.util.*;

/*
    klasse um StreckeErstellenController und Rennstrecke zu koppeln
 */
public class Link_StreckeErstellenController_Rennstrecke {

    //------------------------------------------------------------------------------------------------------------------
    // css style attribute
    private final String css_style_hintergrund_rechteck = "-fx-fill: white;"  // Hintergrundfarbe
                                                        + "-fx-stroke: lightgray;" // Randfarbe
                                                        + "-fx-stroke-width: 1px;"; // Randbreite

    private final String css_style_streckenlinie_notSelected = "-fx-stroke: blue;"
                                                              +"-fx-stroke-width: 2px;";

    private final String css_style_streckenlinie_selected = "-fx-stroke: green;"
                                                           +"-fx-stroke-width: 2px;";

    private final String css_style_startline = "-fx-stroke: red;"
                                              +"-fx-stroke-width: 2px;";

    private final String css_style_startrichtungslinien = "-fx-stroke: violet;"
                                                         +"-fx-stroke-width: 4px;";

    private final String css_style_gitterlinie = "-fx-stroke: lightgray;"
                                                +"-fx-stroke-width: 1px;";

    //------------------------------------------------------------------------------------------------------------------
    // sonstige attribute

    private final double b_max_pixel;
    private final double h_max_pixel;
    private final Umrechnung_grid_pixel umrechnung_grid_pixel;
    // akt linie die gerade mit der maus gezeichnet wird
    private Line aktMouseLine;
    I_Link_StrErstController_Rennstrecke i_linkStrErstControllerRennstrecke; // interface um zeichnungszustand aus controller zu holen

        // speicher für streckenlinien, die im listview angezeigt werden
    private final ObservableList<String> observable_streckenlinien_for_listview = FXCollections.observableArrayList();
    // hashMap, die streckenlinien mit den fxLinies verbindet
    private final HashMap<Linie, Line> streckenLinien_fxLines_map = new HashMap<>();
    private Line startFxLine = null;
    private Line selceted_streckenFxLine = null;
    private final ArrayList<Line> startrichtung_fxLines = new ArrayList<>();

    //------------------------------------------------------------------------------------------------------------------
    // constructor

    public Link_StreckeErstellenController_Rennstrecke(double b_max_pixel, double h_max_pixel, I_Link_StrErstController_Rennstrecke i_linkStrErstControllerRennstrecke) {
        this.b_max_pixel = b_max_pixel;
        this.h_max_pixel = h_max_pixel;
        this.umrechnung_grid_pixel = new Umrechnung_grid_pixel(
                b_max_pixel,
                h_max_pixel,
                i_linkStrErstControllerRennstrecke.get_rennstrecke().getBreite(),
                i_linkStrErstControllerRennstrecke.get_rennstrecke().getHoehe());
        this.i_linkStrErstControllerRennstrecke = i_linkStrErstControllerRennstrecke;
        this.init_gitter_und_hintergrund();
    }


    //------------------------------------------------------------------------------------------------------------------
    // methoden

    private void init_gitter_und_hintergrund(){
        // streckenfläche zeichen
        // Erstelle ein Rechteck
        Rectangle rechteck_hintergrund = new Rectangle();
        rechteck_hintergrund.setX(0.0); // X-Position des Rechtecks
        rechteck_hintergrund.setY(0.0); // Y-Position des Rechtecks
        rechteck_hintergrund.setWidth(umrechnung_grid_pixel.get_b_strecke_pixel_mit_rand()); // Breite des Rechtecks
        rechteck_hintergrund.setHeight(umrechnung_grid_pixel.get_h_strecke_pixel_mit_rand()); // Höhe des Rechtecks
        rechteck_hintergrund.setStyle(this.css_style_hintergrund_rechteck);
        // rechteck dem group-element hinzufügen
        this.i_linkStrErstControllerRennstrecke.get_group().getChildren().add(rechteck_hintergrund);

        // gitter zeichen
        int strecken_breite = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getBreite();
        int strecken_hoehe = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getHoehe();
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
            aktLine.setStroke(Color.LIGHTGRAY);
            aktLine.setStrokeWidth(0.5);
            // linie dem group-element hinzufügen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().add(aktLine);
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
            aktLine.setStroke(Color.LIGHTGRAY);
            aktLine.setStrokeWidth(0.5);
            // linie dem group-element hinzufügen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().add(aktLine);
        }

    }

    private void init_streckenlinien(){

    }

    private void init_startlinie(){

    }

    private void init_startrichtung(){

    }

    public ObservableList<String> get_observable_streckenlinien_for_listview() {
        return observable_streckenlinien_for_listview;
    }

    public void delete_last_streckenlinie(){
        // index der letzten streckenlinie holen
        int anzahl_streckenlinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStreckenlinien().size();
        if(anzahl_streckenlinie > 0){
            // streckenline löschen
            this.delete_streckenlinie(anzahl_streckenlinie-1);
        }
    }

    public void delete_streckenlinie(int index){
        // index der letzten streckenlinie holen
        int anzahl_streckenlinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStreckenlinien().size();

        if (index < anzahl_streckenlinie && index >= 0) {
            Linie akt_streckenLine = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStreckenlinien().get(index);

            Line akt_streckenFxLine = this.streckenLinien_fxLines_map.get(akt_streckenLine);

            // streckenlinie aus rennstrecke löschen
            this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStreckenlinien().remove(index);

            // streckenlinie aus group löschen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().remove(akt_streckenFxLine);

            // streckenlinie map eintrag löschen
            this.streckenLinien_fxLines_map.remove(akt_streckenLine);

            // streckenline aus listview-datencontainer löschen
            this.observable_streckenlinien_for_listview.remove(index);
        }
    }

    public void listView_selected_changed(int index_selected/*wenn keins gewählt dann -1*/){
        // alte selected Line zurücksetzen
        if(this.selceted_streckenFxLine != null){
            this.selceted_streckenFxLine.setStyle(this.css_style_streckenlinie_notSelected);
            this.selceted_streckenFxLine = null;
        }
        if(index_selected >= 0){ // wenn eine line selected wurde
            // gew streckenlinie holen
            Linie selceted_streckenLinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStreckenlinien().get(index_selected);
            // gew fxLine holen
            this.selceted_streckenFxLine = this.streckenLinien_fxLines_map.get(selceted_streckenLinie);
            // fxLine umfärben
            this.selceted_streckenFxLine.setStyle(this.css_style_streckenlinie_selected);
        }
    }

    public void add_streckenlinie(Line fxLine){
        Integer startXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(fxLine.getStartX());
        Integer startYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(fxLine.getStartY());
        Integer endXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(fxLine.getEndX());
        Integer endYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(fxLine.getEndY());

        // wenn grip positionen alle ungleich null sind
        if(startXGrid!=null && startYGrid!=null && endXGrid!=null && endYGrid!=null) {
            // streckenlinie erzeugen
            Linie streckenlinie = new Linie(startXGrid, startYGrid, endXGrid, endYGrid);

            // neue fxLine erzeugen, da eingabe fxLine als endpunkt nicht auf dem kästchenkreuz liegt
            Line newFxLine = new Line(
                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(startXGrid),
                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(startYGrid),
                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(endXGrid),
                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(endYGrid)
            );
            newFxLine.setStyle(this.css_style_streckenlinie_notSelected);

            // testen ob fxLine valide ist
            boolean is_start_ungleich_end = !(endXGrid.equals(startXGrid) && endYGrid.equals(startYGrid));
            boolean is_streckenlinie_noch_nicht_vorhanden = true;
            for (Linie aktStreckenLinie : this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStreckenlinien()) {
                if (aktStreckenLinie.equals(streckenlinie)) {
                    is_streckenlinie_noch_nicht_vorhanden = false;
                    break;
                }
            }

            if (is_start_ungleich_end && is_streckenlinie_noch_nicht_vorhanden) {
                // streckenlinie der rennstrecke übergeben
                this.i_linkStrErstControllerRennstrecke.get_rennstrecke().addStreckenlinie(streckenlinie);

                // streckenline und fxLine in map verknüfen
                this.streckenLinien_fxLines_map.put(streckenlinie, newFxLine);

                // streckenline in listview-datencontainer hinzufügen
                this.observable_streckenlinien_for_listview.add(this.linie_to_string(streckenlinie));

                // fxLine dem group hinzufügen
                this.i_linkStrErstControllerRennstrecke.get_group().getChildren().add(newFxLine);
            }
        }else{
            System.out.println("Mindestens eine Grid-Position ist null!");
        }
    }

    private String linie_to_string(Linie linie){
        return ("X0=" +linie.getP1().getX()
                + " Y0=" +linie.getP1().getY()
                + " X1=" +linie.getP2().getX()
                + " Y1=" +linie.getP2().getY());
    }

    public void add_or_change_startlinie(Line fxLine){
        Integer startXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(fxLine.getStartX());
        Integer startYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(fxLine.getStartY());
        Integer endXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(fxLine.getEndX());
        Integer endYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(fxLine.getEndY());

        // wenn grip positionen alle ungleich null sind
        if(startXGrid!=null && startYGrid!=null && endXGrid!=null && endYGrid!=null) {
            // startlinie erzeugen
            Linie startlinie = new Linie(startXGrid, startYGrid, endXGrid, endYGrid);

            // neue fxLine erzeugen, da eingabe fxLine als endpunkt nicht auf dem kästchenkreuz liegt
            Line newFxLine = new Line(
                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(startXGrid),
                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(startYGrid),
                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(endXGrid),
                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(endYGrid)
            );
            newFxLine.setStyle(this.css_style_startline);

            // testen ob fxLine valide ist
            boolean is_start_ungleich_end = !(endXGrid.equals(startXGrid) && endYGrid.equals(startYGrid));
            boolean is_startline_horizontal_oder_vertikal = (startXGrid.equals(endXGrid)) || (startYGrid.equals(endYGrid));

            if (is_start_ungleich_end && is_startline_horizontal_oder_vertikal) {
                if(this.startFxLine != null){
                    // es ist bereits eine startlinie vorhanden
                    // alte startlinie aus group entfernen
                    this.i_linkStrErstControllerRennstrecke.get_group().getChildren().remove(this.startFxLine);
                }
                // startlinie der rennstrecke übergeben
                this.i_linkStrErstControllerRennstrecke.get_rennstrecke().setStartlinie(startlinie);

                // fxLine dem group hinzufügen
                this.i_linkStrErstControllerRennstrecke.get_group().getChildren().add(newFxLine);

                // fxLine in startFxLine speichern
                this.startFxLine = newFxLine;
            }
        }else{
            System.out.println("Mindestens eine Grid-Position ist null!");
        }

        // startrichtung zeichnen
        this.draw_start_richtung();
    }

    private void draw_start_richtung(){
        // alte startrichtungslinien löschen
        // groupLines löschen
        for(Line aktLine : this.startrichtung_fxLines){
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().remove(aktLine);
        }
        // startrichtun_fxLines leeren
        this.startrichtung_fxLines.clear();

        // neue startrichtungs linien erstellen
        if(this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStartlinie() != null){
            // startpunkt ermitteln an dem der pfeil beginnt
            int x0_grid_startLinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStartlinie().getP1().getX();
            int y0_grid_startLinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStartlinie().getP1().getY();
            int x1_grid_startLinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStartlinie().getP2().getX();
            int y1_grid_startLinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStartlinie().getP2().getY();

            // startposition des richtungspfeils ermitteln
            int x_grid_pfeil_start = x0_grid_startLinie<x1_grid_startLinie ? x0_grid_startLinie : x1_grid_startLinie;
            int y_grid_pfeil_start = y0_grid_startLinie<y1_grid_startLinie ? y0_grid_startLinie : y1_grid_startLinie;

            // startlinie horizontal oder vertikal
            boolean is_startlinie_horizontal;
            if(y0_grid_startLinie == y1_grid_startLinie){
                is_startlinie_horizontal = true;
            }else if(x0_grid_startLinie == x1_grid_startLinie){
                is_startlinie_horizontal = false;
            }else{
                throw new RuntimeException("Unerwarteter Fehler beim Zeichnen der Startrichtung!");
            }

            if (is_startlinie_horizontal) {
                if(this.i_linkStrErstControllerRennstrecke.get_rennstrecke().get_is_startrichtung_nach_unten_oder_rechts()){
                    // startlinie -> horizontal
                    // startrichtung -> unten
                    this.startrichtung_fxLines.add( new Line(
                            this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                            this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                            this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                            this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start +2)
                            )
                    );
                    this.startrichtung_fxLines.add( new Line(
                            this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                            this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start +2),
                            this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start -1),
                            this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start +1)
                            )
                    );
                    this.startrichtung_fxLines.add( new Line(
                            this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                            this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start +2),
                            this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start +1),
                            this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start +1)
                            )
                    );
                }else{
                    // startlinie -> horizontal
                    // startrichtung -> oben
                    this.startrichtung_fxLines.add( new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start -2)
                            )
                    );
                    this.startrichtung_fxLines.add( new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start -2),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start -1),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start -1)
                            )
                    );
                    this.startrichtung_fxLines.add( new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start -2),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start +1),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start -1)
                            )
                    );
                }
            }else {
                if (this.i_linkStrErstControllerRennstrecke.get_rennstrecke().get_is_startrichtung_nach_unten_oder_rechts()) {
                    // startlinie -> vertikal
                    // startrichtung -> rechts
                    this.startrichtung_fxLines.add(new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start +2),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start)
                            )
                    );
                    this.startrichtung_fxLines.add(new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start +2),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start +1),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start -1)
                            )
                    );
                    this.startrichtung_fxLines.add(new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start +2),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start +1),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start +1)
                            )
                    );
                } else {
                    // startlinie -> vertikal
                    // startrichtung -> links
                    this.startrichtung_fxLines.add(new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start -2),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start)
                            )
                    );
                    this.startrichtung_fxLines.add(new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start -2),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start -1),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start -1)
                            )
                    );
                    this.startrichtung_fxLines.add(new Line(
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start -2),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start),
                                    this.umrechnung_grid_pixel.posXGrid_to_posXPixel(x_grid_pfeil_start -1),
                                    this.umrechnung_grid_pixel.posYGrid_to_posYPixel(y_grid_pfeil_start +1)
                            )
                    );
                }
            }
            // style der lines setzen
            for(Line aktline : this.startrichtung_fxLines){
                aktline.setStyle(this.css_style_startrichtungslinien);
            }
            // lines der group hinzufügen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().addAll(this.startrichtung_fxLines);
        }
    }


    public void start_richtung_aendern(){
        // akt startrichtung holen
        boolean aktStartrichtung = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().get_is_startrichtung_nach_unten_oder_rechts();
        // umgekehrte startrichtung setzen
        this.i_linkStrErstControllerRennstrecke.get_rennstrecke().set_is_startrichtung_nach_unten_oder_rechts(!aktStartrichtung);
        // startrichtung zeichnen
        this.draw_start_richtung();
    }

    public void group_mPressed(MouseEvent mouseEvent) {
        // wenn rechte maustaste gerdückt, dann linienerstellung beenden
        if (mouseEvent.isSecondaryButtonDown()) {
            // linie aus group entfernen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().remove(this.aktMouseLine);
            // temp mouselinie auf null setzen
            this.aktMouseLine = null;
            return;
        }
        Integer aktMouseLine_posPressdGridX_round = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(mouseEvent.getX());
        Integer aktMouseLine_posPressdGridY_round = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(mouseEvent.getY());
        if(aktMouseLine_posPressdGridX_round !=null && aktMouseLine_posPressdGridY_round !=null && this.i_linkStrErstControllerRennstrecke.get_zeichnungszustand()!= StreckeErstellenController.Zeichnungszustand.KEINS_GEWAEHLT){ // wenn clickposition validen wert hat
            // positioen vom grid in pixel umrechnen (pos im kästchenkreuz != mouseposition)
            double posXpixel = this.umrechnung_grid_pixel.posXGrid_to_posXPixel(aktMouseLine_posPressdGridX_round);
            double posYpixel = this.umrechnung_grid_pixel.posYGrid_to_posYPixel(aktMouseLine_posPressdGridY_round);
            // linie erstellen
            this.aktMouseLine = new Line();
            // linienfarbe wählen je nach linienart
            if(this.i_linkStrErstControllerRennstrecke.get_zeichnungszustand() == StreckeErstellenController.Zeichnungszustand.STRECKENLINIE_ZEICHNEN){
                this.aktMouseLine.setStyle(this.css_style_streckenlinie_notSelected);
            }else if(this.i_linkStrErstControllerRennstrecke.get_zeichnungszustand()== StreckeErstellenController.Zeichnungszustand.STARTLINIE_ZEICHNEN){
                this.aktMouseLine.setStyle(this.css_style_startline);
            }else{
                throw new RuntimeException("unvorhergesehener Zeichnungszustand");
            }
            this.aktMouseLine.setStartX(posXpixel);
            this.aktMouseLine.setStartY(posYpixel);
            this.aktMouseLine.setEndX(posXpixel);
            this.aktMouseLine.setEndY(posYpixel);
            // linie zeichenen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().add(this.aktMouseLine);
        }else {
            this.aktMouseLine = null;
        }
    }

    public void group_mDraged(MouseEvent mouseEvent) {
        // wenn rechte maustaste gerdückt, dann linienerstellung beenden
        if (mouseEvent.isSecondaryButtonDown()) {
            // linie aus group entfernen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().remove(this.aktMouseLine);
            // temp mouselinie auf null setzen
            this.aktMouseLine = null;
            return;
        }
        if(this.aktMouseLine != null){
            double endX = mouseEvent.getX();
            double endY = mouseEvent.getY();
            this.aktMouseLine.setEndX(endX);
            this.aktMouseLine.setEndY(endY);
        }
    }

    public void group_mReleased(MouseEvent mouseEvent){
        // wenn rechte maustaste gerdückt, dann linienerstellung beenden
        if (mouseEvent.isSecondaryButtonDown()) {
            // linie aus group entfernen
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().remove(this.aktMouseLine);
            // temp mouselinie auf null setzen
            this.aktMouseLine = null;
            return;
        }
        if(this.aktMouseLine != null) {
            // aktMouseLine aus group löschen, falls aktMouseLine valide werte hat, wird die linie in den add_ methoden dem group hinzugefügt
            this.i_linkStrErstControllerRennstrecke.get_group().getChildren().remove(this.aktMouseLine);

            if(this.i_linkStrErstControllerRennstrecke.get_zeichnungszustand() == StreckeErstellenController.Zeichnungszustand.STRECKENLINIE_ZEICHNEN){
                this.add_streckenlinie(this.aktMouseLine);
            }else if(this.i_linkStrErstControllerRennstrecke.get_zeichnungszustand() == StreckeErstellenController.Zeichnungszustand.STARTLINIE_ZEICHNEN){
                this.add_or_change_startlinie(this.aktMouseLine);
            }

            // aktMouseLine auf null setzen
            this.aktMouseLine = null;
        }


//        if(this.aktMouseLine != null) {
//            double endXPixel = mouseEvent.getX();
//            double endYPixel = mouseEvent.getY();
//
//            Integer startXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(this.aktMouseLine.getStartX());
//            Integer startYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(this.aktMouseLine.getStartY());
//            if(startXGrid==null || startYGrid==null){
//                throw new RuntimeException("Startpunkt ist null, das kann eigentlich hier nicht sein!");
//            }
//
//            Integer endXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(endXPixel);
//            Integer endYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(endYPixel);
//
//            // wenn endpunkt auch validen wert hat und endpunkt != startpunkt
//            if (endXGrid!=null && endYGrid!=null
//                    && !(endXGrid.equals(startXGrid) && endYGrid.equals(startYGrid)) ) {
//                // endpunkt der linie setzen
//                this.aktMouseLine.setEndX(this.umrechnung_grid_pixel.posXGrid_to_posXPixel(endXGrid));
//                this.aktMouseLine.setEndY(this.umrechnung_grid_pixel.posYGrid_to_posYPixel(endYGrid));
//
//                // linie in der rennstrecke speichern (wichtig Line != Linie)
//                // abfrage welche art von line
//                if(this.i_linkStrErstControllerRennstrecke.get_zeichnungszustand() == StreckeErstellenController.Zeichnungszustand.STRECKENLINIE_ZEICHNEN){
//                    // streckenlinie in rennstrecke hinzufügen
//                    this.add_streckenlinie(this.aktMouseLine);
//                    // listview aktualisieren
//                    this.update_streckenLinien_listView();
//                }else if(this.i_linkStrErstControllerRennstrecke.get_zeichnungszustand() == StreckeErstellenController.Zeichnungszustand.STARTLINIE_ZEICHNEN){
//                    // startlinie darf nur horizontal oder vertikal sein
//                    if( (startXGrid.equals(endXGrid)) || (startYGrid.equals(endYGrid)) ){
//                        // alte linie in group falls vorhanden löschen
//                        Linie alteRennstreckeStartlinie = this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStartlinie();
//                        System.out.println("alteRennstreckeStartlinie = " + (alteRennstreckeStartlinie != null ? alteRennstreckeStartlinie.toString() : null));
//                        if(alteRennstreckeStartlinie != null){
//                            if(alteGroupStartLine != null){
//                                this.streckeErstellen_group_strecke.getChildren().remove(alteGroupStartLine);
//                            }else{
//                                throw new RuntimeException("Fehler: alte Startlinie im Group-Element nicht gefunden!");
//                            }
//                        }
//                        // startlinie in rennstrecke setzen
//                        this.rennstrecke.setStartlinie(startXGrid, startYGrid, endXGrid, endYGrid);
//                    }else{
//                        // akt line löschen
//                        this.streckeErstellen_group_strecke.getChildren().remove(this.aktMouseLine);
//                    }
//                }else{
//                    throw new RuntimeException("unvorhergesehener Zeichnungszustand");
//                }
//            } else { // nicht valider endpunkt
//                // linie aus group entfernen
//                this.streckeErstellen_group_strecke.getChildren().remove(this.aktMouseLine);
//            }
//            // temp mouselinie auf null setzen
//            this.aktMouseLine = null;
//        }
    }

    private void update_streckenLinien_listView(){
        // listviewdaten löschen
        this.observable_streckenlinien_for_listview.clear();

        // listview neu initialisieren
        // rennstrecken.streckenlinien in ObservableList<String> einfügen
        for (Linie linie : this.i_linkStrErstControllerRennstrecke.get_rennstrecke().getStreckenlinien()) {
            this.observable_streckenlinien_for_listview.add("x0=" + linie.getP1().getX() + ", y0=" + linie.getP1().getY() +
                    ", x1=" + linie.getP2().getX() + ", y1=" + linie.getP2().getY());
        }
        // observable_streckenlinien_for_listview mit listview verbinden
        // this.streckeErstellen_listview_streckenlinien.setItems(this.observable_streckenlinien_for_listview);
    }



    public interface I_Link_StrErstController_Rennstrecke {
        public StreckeErstellenController.Zeichnungszustand get_zeichnungszustand();
        public Group get_group();
        public Rennstrecke get_rennstrecke();
    }

}

