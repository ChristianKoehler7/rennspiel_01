package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.datasets.Linie;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import de.christian_koehler_iu.rennspiel.datasets.Rennstrecke;
import de.christian_koehler_iu.rennspiel.utility.Umrechnung_grid_pixel;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.collections.FXCollections;
import org.jetbrains.annotations.Nullable;

public class StreckeErstellenController {


    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/strecke_erstellen_view.fxml";
    public static final String SCENE_NAME = "StreckeErstellen";

    private final String css_style_button_streckenLinie_pressed = "-fx-background-color: #ADD8E6;" + // Hellblauer Hintergrund
            "-fx-border-color: #0000FF;" +    // Blaue Umrandung
            "-fx-border-width: 2px;" +        // Dickere Umrandung
            "-fx-text-fill: #FFFFFF;";        // Weiße Schrift

    private final String css_style_button_startLinie_pressed = "-fx-background-color: #FF6347;" + // Tomatenroter Hintergrund
            "-fx-border-color: #FF0000;" +    // Rote Umrandung
            "-fx-border-width: 2px;" +        // Dickere Umrandung
            "-fx-text-fill: #FFFFFF;";        // Weiße Schrift

    private Rennstrecke rennstrecke;

    private final double B_MAX_PIXEL = 896.0-20.0;
    private final double H_MAX_PIXEL = 560.0-20.0;

    private Umrechnung_grid_pixel umrechnung_grid_pixel;

    private enum Zeichnungszustand {
        STRECKENLINIE_ZEICHNEN,
        STARTLINIE_ZEICHNEN,
        KEINS_GEWAEHLT;
    }
    private Zeichnungszustand zeichnungszustand = Zeichnungszustand.KEINS_GEWAEHLT;

    // speicher für streckenlinien, die im listview angezeigt werden
    private final ObservableList<String> observable_streckenlinien_for_listview = FXCollections.observableArrayList();;

    // akt linie die gerade mit der maus gezeichnet wird
    private Line aktMouseLine;
    private Integer posPressdGridX_round = null;
    private Integer posPressdGridY_round = null;

    @FXML
    private GridPane streckeErstellen_grid_root;
    @FXML
    private GridPane streckeErstellen_grid_rechts;
    @FXML
    private Group streckeErstellen_group_strecke;
    @FXML
    private ListView<String> streckeErstellen_listview_streckenlinien;
    @FXML
    private Label streckeErstellen_lb_streckengroesse;
    @FXML
    private Button streckeErstellen_bn_streckenLinieZeichnen;
    @FXML
    private Button streckeErstellen_bn_startLinieZeichnen;
    @FXML
    private Button streckeErstellen_bn_letzteStrLinieLoeschen;

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }

    
    
    public void initialize_rennstrecke(Rennstrecke rennstrecke){
        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // labeltext setzen
        this.streckeErstellen_lb_streckengroesse.setText("Streckenname: " + rennstrecke.getName() + "\nBreite: " + rennstrecke.getBreite() + "\nHöhe: " + rennstrecke.getHoehe());

        // umrechnungs_grid_pixel objekt erstellen
        this.umrechnung_grid_pixel = new Umrechnung_grid_pixel(this.B_MAX_PIXEL, this.H_MAX_PIXEL, this.rennstrecke.getBreite(), this.rennstrecke.getHoehe());

        // streckenfläche zeichen
        // Erstelle ein Rechteck
        Rectangle rechteck_hintergrund = new Rectangle();
        rechteck_hintergrund.setX(0.0); // X-Position des Rechtecks
        rechteck_hintergrund.setY(0.0); // Y-Position des Rechtecks
        rechteck_hintergrund.setWidth(umrechnung_grid_pixel.get_b_strecke_pixel_mit_rand()); // Breite des Rechtecks
        rechteck_hintergrund.setHeight(umrechnung_grid_pixel.get_h_strecke_pixel_mit_rand()); // Höhe des Rechtecks
        // Setze die Füllfarbe auf Weiß
        rechteck_hintergrund.setFill(Color.WHITE);
        // Setze die Randfarbe auf Schwarz
        rechteck_hintergrund.setStroke(Color.DARKGRAY);
        // rechteck dem group-element hinzufügen
        streckeErstellen_group_strecke.getChildren().add(rechteck_hintergrund);

        // gitter zeichen
        int strecken_breite = this.rennstrecke.getBreite();
        int strecken_hoehe = this.rennstrecke.getHoehe();
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
            streckeErstellen_group_strecke.getChildren().add(aktLine);
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
            streckeErstellen_group_strecke.getChildren().add(aktLine);
        }

        // speicher für listview initialisieren
        //this.streckeErstellen_listview_streckenlinien.setItems(this.observable_streckenlinien_for_listview);
        this.update_streckenLinien_listView();

        // ChangeListener wenn sich ausgewähltes item der listview ändert erstellen
        this.init_create_listView_selected_changeListener();
    }

    private void init_create_listView_selected_changeListener(){
        // ChangeListener wenn sich ausgewähltes item der listview ändert
        this.streckeErstellen_listview_streckenlinien.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) ->{
            // evtl ist bereits eine groupLine eingefärbt, dh alle einfärbungen rückgängig machen
            this.grouplines_farben_wiederherstellen();

            // index der gewählten streckenlinie holen (wenn keins gewählt dann -1)
            int index_selected = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();
            System.out.println("index_selected = " + index_selected);

            if(index_selected >= 0){
                // gewählte linie grün färben
                // gew rennstrecke.streckenlinie holen
                Linie gewStreckenlinie = this.rennstrecke.getStreckenlinien().get(index_selected);
                // groupline holen
                Line gewGroupLine = this.get_groupLine_from_streckenLinie(gewStreckenlinie);
                System.out.println("gewGroupLine = " + gewGroupLine);
                if (gewGroupLine != null) {
                    gewGroupLine.setStroke(Color.GREEN);
                }
            }
        });
    }

    public void streckeErstellen_bn_zurueck_action(ActionEvent actionEvent) {
        ScenesManager.getInstance().goBack();
    }

    public void streckeErstellen_bn_gewStrLinieLoeschen_action(ActionEvent actionEvent) {
        // löscht die line, die im listview selected ist

        // index der gewählten linie holen (wenn nix selected dann -1)
        int index_selected_item = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();

        if(index_selected_item >=0){
            // rennstrecke.streckenlinie holen
            Linie gewStreckenLinie = this.rennstrecke.getStreckenlinien().get(index_selected_item);
            // grouplinie holen
            Line gewGroupLine = this.get_groupLine_from_streckenLinie(gewStreckenLinie);
            // groupline aus gui löschen
            this.streckeErstellen_group_strecke.getChildren().remove(gewGroupLine);
            // rennstrecke.streckenlinie holen löschen
            this.rennstrecke.getStreckenlinien().remove(gewStreckenLinie);
            // listview aktualisieren
            this.update_streckenLinien_listView();
        }
    }

    private void grouplines_farben_wiederherstellen(){
        for(Node aktNode : this.streckeErstellen_group_strecke.getChildren()){
            if(aktNode instanceof Line){
                // linienart herausfinden
                // pixel positionen der aktNode holen
                double x0_pixel_groupLine = ((Line) aktNode).getStartX();
                double y0_pixel_groupLine = ((Line) aktNode).getStartY();
                double x1_pixel_groupLine = ((Line) aktNode).getEndX();
                double y1_pixel_groupLine = ((Line) aktNode).getEndY();

                // temp linien objekt erzeugen um auf gleichheit zu prüfen
                Integer x0_grid_groupLine = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(x0_pixel_groupLine);
                Integer y0_grid_groupLine = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(y0_pixel_groupLine);
                Integer x1_grid_groupLine = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(x1_pixel_groupLine);
                Integer y1_grid_groupLine = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(y1_pixel_groupLine);
                if(x0_grid_groupLine==null || y0_grid_groupLine==null || x1_grid_groupLine==null || y1_grid_groupLine==null){
                    throw new RuntimeException("Gridposition ist null, das kann hier eigentlich nicht sein!");
                }
                Linie tmpLinie = new Linie(x0_grid_groupLine, y0_grid_groupLine, x1_grid_groupLine, y1_grid_groupLine);

                // prüfen ob aktNode eine streckenlinie ist
                for(Linie aktStreckenLinie : this.rennstrecke.getStreckenlinien()){
                    if(aktStreckenLinie.equals(tmpLinie)){
                        // aktNode ist eine streckenlinie => blau färben
                        ((Line) aktNode).setStroke(Color.BLUE);
                    }
                }

                if(this.rennstrecke.getStartlinie() !=null
                        && this.rennstrecke.getStartlinie().equals(tmpLinie)){
                    // aktNode ist eine startlinie => rot färben
                    ((Line) aktNode).setStroke(Color.RED);
                }
            }
        }
    }


    public void streckeErstellen_bn_streckenLinieZeichnen_action(ActionEvent actionEvent) {
        if(this.zeichnungszustand != Zeichnungszustand.STRECKENLINIE_ZEICHNEN){
            // bis jetzt war button nicht gedrückt
            this.zeichnungszustand = Zeichnungszustand.STRECKENLINIE_ZEICHNEN;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle(this.css_style_button_streckenLinie_pressed);
            this.streckeErstellen_bn_startLinieZeichnen.setStyle("");
        }else{ // button war gedrückt
            this.zeichnungszustand = Zeichnungszustand.KEINS_GEWAEHLT;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle("");
            this.streckeErstellen_bn_startLinieZeichnen.setStyle("");
        }
    }

    public void streckeErstellen_bn_startLinieZeichnen_action(ActionEvent actionEvent) {
        if(this.zeichnungszustand != Zeichnungszustand.STARTLINIE_ZEICHNEN){
            // bis jetzt war button nicht gedrückt
            this.zeichnungszustand = Zeichnungszustand.STARTLINIE_ZEICHNEN;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle("");
            this.streckeErstellen_bn_startLinieZeichnen.setStyle(this.css_style_button_startLinie_pressed);
        }else{ // button war gedrückt
            this.zeichnungszustand = Zeichnungszustand.KEINS_GEWAEHLT;
            this.streckeErstellen_bn_streckenLinieZeichnen.setStyle("");
            this.streckeErstellen_bn_startLinieZeichnen.setStyle("");
        }
    }

    public void streckeErstellen_bn_letzteStrLinieLoeschen_action(ActionEvent actionEvent) {
        // letzte streckenlinie der rennstrecke holen
        if(!this.rennstrecke.getStreckenlinien().isEmpty()){
            Linie lastStreckenLine = this.rennstrecke.getStreckenlinien().getLast();
            // groupLine holen
            Line groupLine = this.get_groupLine_from_streckenLinie(lastStreckenLine);
            // groupLine aus gui entfernen
            this.streckeErstellen_group_strecke.getChildren().remove(groupLine);
            // streckenlinie aus rennstrecke löschen
            this.rennstrecke.getStreckenlinien().removeLast();
            // listview aktualisieren
            this.update_streckenLinien_listView();
        }
    }

    private void update_streckenLinien_listView(){
        // listviewdaten löschen
        this.observable_streckenlinien_for_listview.clear();

        // listview neu initialisieren
        // rennstrecken.streckenlinien in ObservableList<String> einfügen
        for (Linie linie : this.rennstrecke.getStreckenlinien()) {
            this.observable_streckenlinien_for_listview.add("x0=" + linie.getP1().getX() + ", y0=" + linie.getP1().getY() +
                    ", x1=" + linie.getP2().getX() + ", y1=" + linie.getP2().getY());
        }
        // observable_streckenlinien_for_listview mit listview verbinden
        this.streckeErstellen_listview_streckenlinien.setItems(this.observable_streckenlinien_for_listview);
    }

    public void streckeErstellen_group_strecke_mPressed(MouseEvent mouseEvent) {
        // wenn rechte maustaste gerdückt, dann linienerstellung beenden
        if (mouseEvent.isSecondaryButtonDown()) {
            // linie aus group entfernen
            this.streckeErstellen_group_strecke.getChildren().remove(this.aktMouseLine);
            // temp mouselinie auf null setzen
            this.aktMouseLine = null;
            return;
        }
        this.posPressdGridX_round = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(mouseEvent.getX());
        this.posPressdGridY_round = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(mouseEvent.getY());
        if(this.posPressdGridX_round!=null && this.posPressdGridY_round!=null && this.zeichnungszustand!=Zeichnungszustand.KEINS_GEWAEHLT){ // wenn clickposition validen wert hat
            // positioen vom grid in pixel umrechnen (pos im kästchenkreuz != mouseposition)
            double posXpixel = this.umrechnung_grid_pixel.posXGrid_to_posXPixel(this.posPressdGridX_round);
            double posYpixel = this.umrechnung_grid_pixel.posYGrid_to_posYPixel(this.posPressdGridY_round);
            // linie erstellen
            this.aktMouseLine = new Line();
            // linienfarbe wählen je nach linienart
            if(this.zeichnungszustand==Zeichnungszustand.STRECKENLINIE_ZEICHNEN){
                this.aktMouseLine.setStroke(Color.BLUE);
            }else if(this.zeichnungszustand==Zeichnungszustand.STARTLINIE_ZEICHNEN){
                this.aktMouseLine.setStroke(Color.RED);
            }else{
                throw new RuntimeException("unvorhergesehener Zeichnungszustand");
            }
            this.aktMouseLine.setStrokeWidth(2.0);
            this.aktMouseLine.setStartX(posXpixel);
            this.aktMouseLine.setStartY(posYpixel);
            this.aktMouseLine.setEndX(posXpixel);
            this.aktMouseLine.setEndY(posYpixel);
            // linie zeichenen
            this.streckeErstellen_group_strecke.getChildren().add(this.aktMouseLine);
        }else {
            this.aktMouseLine = null;
        }
    }

    public void streckeErstellen_group_strecke_mDragged(MouseEvent mouseEvent){
        // wenn rechte maustaste gerdückt, dann linienerstellung beenden
        if (mouseEvent.isSecondaryButtonDown()) {
            // linie aus group entfernen
            this.streckeErstellen_group_strecke.getChildren().remove(this.aktMouseLine);
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

    public void streckeErstellen_group_mReleased(MouseEvent mouseEvent){
        // wenn rechte maustaste gerdückt, dann linienerstellung beenden
        if (mouseEvent.isSecondaryButtonDown()) {
            // linie aus group entfernen
            this.streckeErstellen_group_strecke.getChildren().remove(this.aktMouseLine);
            // temp mouselinie auf null setzen
            this.aktMouseLine = null;
            return;
        }
        if(this.aktMouseLine != null) {
            double endXPixel = mouseEvent.getX();
            double endYPixel = mouseEvent.getY();

            Integer startXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(this.aktMouseLine.getStartX());
            Integer startYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(this.aktMouseLine.getStartY());
            if(startXGrid==null || startYGrid==null){
                throw new RuntimeException("Startpunkt ist null, das kann eigentlich hier nicht sein!");
            }

            Integer endXGrid = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(endXPixel);
            Integer endYGrid = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(endYPixel);

//            System.out.println("startXGrid = " +startXGrid);
//            System.out.println("startYGrid = " +startYGrid);
//            System.out.println("endXGrid = " +endXGrid);
//            System.out.println("endYGrid = " +endYGrid);
//            System.out.println("endXGrid!=null && endYGrid!=null && !(endXGrid.equals(startXGrid)) && endYGrid.equals(startYGrid) = " +(endXGrid!=null && endYGrid!=null && !(endXGrid.equals(startXGrid) && endYGrid.equals(startYGrid))) );

            // wenn endpunkt auch validen wert hat und endpunkt != startpunkt
            if (endXGrid!=null && endYGrid!=null
                    && !(endXGrid.equals(startXGrid) && endYGrid.equals(startYGrid)) ) {
                // endpunkt der linie setzen
                this.aktMouseLine.setEndX(this.umrechnung_grid_pixel.posXGrid_to_posXPixel(endXGrid));
                this.aktMouseLine.setEndY(this.umrechnung_grid_pixel.posYGrid_to_posYPixel(endYGrid));

                // linie in der rennstrecke speichern (wichtig Line != Linie)
                // abfrage welche art von line
                if(this.zeichnungszustand==Zeichnungszustand.STRECKENLINIE_ZEICHNEN){
                    // streckenlinie in rennstrecke hinzufügen
                    this.rennstrecke.addStreckenlinie(startXGrid, startYGrid, endXGrid, endYGrid);
                    // listview aktualisieren
                    this.update_streckenLinien_listView();
                }else if(this.zeichnungszustand==Zeichnungszustand.STARTLINIE_ZEICHNEN){
                    // startlinie darf nur horizontal oder vertikal sein
                    if( (startXGrid.equals(endXGrid)) || (startYGrid.equals(endYGrid)) ){
                        // alte linie in group falls vorhanden löschen
                        Linie alteRennstreckeStartlinie = this.rennstrecke.getStartlinie();
                        System.out.println("alteRennstreckeStartlinie = " + (alteRennstreckeStartlinie != null ? alteRennstreckeStartlinie.toString() : null));
                        if(alteRennstreckeStartlinie != null){
                            Line alteGroupStartLine = this.get_groupLine_from_streckenLinie(alteRennstreckeStartlinie);
                            System.out.println("alteGroupStartLine = " + alteGroupStartLine);
                            if(alteGroupStartLine != null){
                                this.streckeErstellen_group_strecke.getChildren().remove(alteGroupStartLine);
                            }else{
                                throw new RuntimeException("Fehler: alte Startlinie im Group-Element nicht gefunden!");
                            }
                        }
                        // startlinie in rennstrecke setzen
                        this.rennstrecke.setStartlinie(startXGrid, startYGrid, endXGrid, endYGrid);
                    }else{
                        // akt line löschen
                        this.streckeErstellen_group_strecke.getChildren().remove(this.aktMouseLine);
                    }
                }else{
                    throw new RuntimeException("unvorhergesehener Zeichnungszustand");
                }
            } else { // nicht valider endpunkt
                // linie aus group entfernen
                this.streckeErstellen_group_strecke.getChildren().remove(this.aktMouseLine);
            }
            // temp mouselinie auf null setzen
            this.aktMouseLine = null;
        }
    }

    @Nullable
    private Line get_groupLine_from_streckenLinie(Linie linie){
        // pixelpositionen der eingabeLinie holen
        double x0_pixel_streckenLinie = this.umrechnung_grid_pixel.posXGrid_to_posXPixel(linie.getP1().getX());
        double y0_pixel_streckenLinie = this.umrechnung_grid_pixel.posYGrid_to_posYPixel(linie.getP1().getY());
        double x1_pixel_streckenLinie = this.umrechnung_grid_pixel.posXGrid_to_posXPixel(linie.getP2().getX());
        double y1_pixel_streckenLinie = this.umrechnung_grid_pixel.posYGrid_to_posYPixel(linie.getP2().getY());

//        System.out.println("private Line get_groupLine_from_streckenLinie(Linie linie){");
//        System.out.println("x0_pixel_streckenLinie = " +x0_pixel_streckenLinie);
//        System.out.println("y0_pixel_streckenLinie = " +y0_pixel_streckenLinie);
//        System.out.println("x1_pixel_streckenLinie = " +x1_pixel_streckenLinie);
//        System.out.println("y1_pixel_streckenLinie = " +y1_pixel_streckenLinie);

        int aktIndex = 0;
        while( aktIndex < this.streckeErstellen_group_strecke.getChildren().size() ){
            if(this.streckeErstellen_group_strecke.getChildren().get(aktIndex) instanceof Line){
                double x0_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getStartX();
                double y0_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getStartY();
                double x1_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getEndX();
                double y1_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getEndY();
                if(   x0_pixel_streckenLinie==x0_groupLine
                   && y0_pixel_streckenLinie==y0_groupLine
                   && x1_pixel_streckenLinie==x1_groupLine
                   && y1_pixel_streckenLinie==y1_groupLine ){
                    // linie gefunden
                    return ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex));
                }
            }
            aktIndex++;
        }
        // keine line gefunden
        return null;
    }
}
/**
    public void nocanvas_bn_delLastLine_action(ActionEvent actionEvent) {
        this.nocanvas_pane_strecke.getChildren().removeLast();
        this.nocanvas_pane_strecke.getChildren().removeLast();
        this.nocanvas_pane_strecke.getChildren().removeLast();
    }

    public void nocanvas_canv_rennstreck_mousePressed(MouseEvent mouseEvent) {
        this.nocanvas_lb_clickPosCanvasX.setText(Double.toString(mouseEvent.getX()));
        this.nocanvas_lb_clickPosCanvasY.setText(Double.toString(mouseEvent.getY()));
        this.posPressdGridX_round = this.posCanvasX_to_posGridX_round(mouseEvent.getX());
        this.posPressdGridY_round = this.posCanvasY_to_posGridY_round(mouseEvent.getY());
        this.nocanvas_lb_clickPosGridX.setText(Integer.toString( this.posPressdGridX_round ));
        this.nocanvas_lb_clickPosGridY.setText(Integer.toString( this.posPressdGridY_round ));
        if(this.posPressdGridX_round>=0
                && this.posPressdGridY_round>=0){
            this.aktMouseLine = new Line();
            this.aktMouseLine.setStroke(Color.BLUE);
            this.aktMouseLine.setStrokeWidth(2.0);
            this.aktMouseLine.setStartX(this.posGridX_to_posCanvasX(this.posPressdGridX_round));
            this.aktMouseLine.setStartY(this.posGridY_to_posCanvasY(this.posPressdGridY_round));
            this.aktMouseLine.setEndX(this.posGridX_to_posCanvasX(this.posPressdGridX_round));
            this.aktMouseLine.setEndY(this.posGridY_to_posCanvasY(this.posPressdGridY_round));
            this.nocanvas_pane_strecke.getChildren().add(this.aktMouseLine);
        }else {
            this.aktMouseLine = null;
        }
    }

    public void nocanvas_canv_rennstreck_mouseDragged(MouseEvent mouseEvent) {
        if(this.aktMouseLine != null){
            double endX = mouseEvent.getX();
            double endY = mouseEvent.getY();
            this.aktMouseLine.setEndX(endX);
            this.aktMouseLine.setEndY(endY);
        }
    }

    public void nocanvas_canv_rennstreck_mouseReleased(MouseEvent mouseEvent) {
        if(this.aktMouseLine != null) {
            double endXCanvas = mouseEvent.getX();
            double endYCanvas = mouseEvent.getY();

            int endXGrid = this.posCanvasX_to_posGridX_round(endXCanvas);
            int endYGrid = this.posCanvasY_to_posGridY_round(endYCanvas);

            if (endXGrid >= 0
                    && endYGrid >= 0
                    && !(endXGrid == this.posCanvasX_to_posGridX(this.aktMouseLine.getStartX())
                    && endYGrid == this.posCanvasY_to_posGridY(this.aktMouseLine.getStartY()))) {
                this.aktMouseLine.setEndX(this.posGridX_to_posCanvasX(endXGrid));
                this.aktMouseLine.setEndY(this.posGridY_to_posCanvasY(endYGrid));
                draw_points_to_Line(this.aktMouseLine);
                this.aktMouseLine = null;
            } else {
                this.nocanvas_pane_strecke.getChildren().remove(this.aktMouseLine);
                this.aktMouseLine = null;
            }
        }
    }

    private void draw_points_to_Line(Line line){
        Circle circleStart = new Circle();
        circleStart.setFill(Color.BLUE);
        circleStart.setRadius(5.0);
        circleStart.setCenterX(line.getStartX());
        circleStart.setCenterY(line.getStartY());

        Circle circleEnd = new Circle();
        circleEnd.setFill(Color.BLUE);
        circleEnd.setRadius(5.0);
        circleEnd.setCenterX(line.getEndX());
        circleEnd.setCenterY(line.getEndY());
        nocanvas_pane_strecke.getChildren().addAll(circleStart, circleEnd);
    }

//    private void drawMouseLine(){
//        if(    this.posPressdGridX_round >=0
//            && this.posPressdGridY_round >=0
//            && this.posReleasedGridX_round >=0
//            && this.posReleasedGridY_round >=0){
//            GraphicsContext gc = this.nocanvas_canv_rennstrecke.getGraphicsContext2D();
//            gc.setStroke(Color.BLUE);
//            gc.setLineWidth(2.0);
//            gc.strokeLine(this.posGridX_to_posCanvasX(this.posPressdGridX_round),
//                    this.posGridY_to_posCanvasY(this.posPressdGridY_round),
//                    this.posGridX_to_posCanvasX(this.posReleasedGridX_round),
//                    this.posGridY_to_posCanvasY(this.posReleasedGridY_round)
//            );
//            this.drawCircle(this.posGridX_to_posCanvasX(this.posPressdGridX_round),
//                    this.posGridY_to_posCanvasY(this.posPressdGridY_round),
//                    4.0
//            );
//            this.drawCircle(this.posGridX_to_posCanvasX(this.posReleasedGridX_round),
//                    this.posGridY_to_posCanvasY(this.posReleasedGridY_round),
//                    4.0
//            );
//        }
//    }



    public Rennstrecke getRennstrecke() {
        return rennstrecke;
    }

    private void initalisierung_is_fertig(){
        System.out.println("#############################");
        System.out.println("initalisierung_is_fertig()");
        double pane_breite = this.nocanvas_pane_strecke.getWidth();
        double pane_hoehe = this.nocanvas_pane_strecke.getHeight();
        System.out.println("pane_breite = " + pane_breite);
        System.out.println("pane_hoehe = " + pane_hoehe);


        // gitter zeichen
        int strecken_breite = this.rennstrecke.getBreite();
        int strecken_hoehe = this.rennstrecke.getHoehe();
        // vertikale gitterlinien zeichen
        for(int i=0 ; i<strecken_breite+1 ; i++){
            int x0 = i;
            int y0 = 0;
            int x1 = i;
            int y1 = strecken_hoehe;
            Line aktLine = new Line(this.posGridX_to_posCanvasX(x0),
                    this.posGridY_to_posCanvasY(y0),
                    this.posGridX_to_posCanvasX(x1),
                    this.posGridY_to_posCanvasY(y1));
            aktLine.setStroke(Color.LIGHTGRAY);
            aktLine.setStrokeWidth(0.5);
            this.gitterLininen.add(aktLine);
            this.nocanvas_pane_strecke.getChildren().add(aktLine);
        }
        // horizontale gitterlinien zeichen
        for(int i=0 ; i<strecken_hoehe+1 ; i++){
            int x0 = 0;
            int y0 = i;
            int x1 = strecken_breite;
            int y1 = i;
            Line aktLine = new Line(this.posGridX_to_posCanvasX(x0),
                    this.posGridY_to_posCanvasY(y0),
                    this.posGridX_to_posCanvasX(x1),
                    this.posGridY_to_posCanvasY(y1));
            aktLine.setStroke(Color.LIGHTGRAY);
            aktLine.setStrokeWidth(0.5);
            this.gitterLininen.add(aktLine);
            this.nocanvas_pane_strecke.getChildren().add(aktLine);
        }

        // rennstrecke zeichnen
        for(int i=0 ; i<rennstrecke.getStreckenlinien().size() ; i++){
            int x0 = rennstrecke.getStreckenlinien().get(i).getP1().getX();
            int y0 = rennstrecke.getStreckenlinien().get(i).getP1().getY();
            int x1 = rennstrecke.getStreckenlinien().get(i).getP2().getX();
            int y1 = rennstrecke.getStreckenlinien().get(i).getP2().getY();
            Line aktLine = new Line(this.posGridX_to_posCanvasX(x0),
                    this.posGridY_to_posCanvasY(y0),
                    this.posGridX_to_posCanvasX(x1),
                    this.posGridY_to_posCanvasY(y1));
            aktLine.setStroke(Color.BLACK);
            aktLine.setStrokeWidth(2.0);
            this.streckenlinien.add(aktLine);
            this.nocanvas_pane_strecke.getChildren().add(aktLine);
        }
    }

    public void initialize_rennstrecke(Rennstrecke rennstrecke) {
        this.rennstrecke = rennstrecke;

        final int[] integer = new int[1];
        integer[0] = 0;

        // Listener für die Breite hinzufügen
        nocanvas_pane_strecke.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0) {
                // Listener entfernen, nachdem die Breite nicht mehr 0 ist
                if(integer[0] == 0){
                    integer[0] = 1;
                }else{
                    this.initalisierung_is_fertig();
                }
                //nocanvas_pane_strecke.widthProperty().removeListener((ChangeListener<? super Number>) this);
            }
        });

        // Listener für die Höhe hinzufügen
        nocanvas_pane_strecke.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0) {
                // Listener entfernen, nachdem die Höhe nicht mehr 0 ist
                if(integer[0] == 0){
                    integer[0] = 1;
                }else{
                    this.initalisierung_is_fertig();
                }
                //nocanvas_pane_strecke.heightProperty().removeListener((ChangeListener<? super Number>) this);
            }
        });
    }

    private double posGridX_to_posCanvasX(int posGridX){
        double canvas_breite = this.nocanvas_pane_strecke.getWidth();
        int strecken_breite = this.rennstrecke.getBreite();

        return this.RAND_X + posGridX / ((double)strecken_breite) * (canvas_breite - 2*this.RAND_X);
    }

    private double posGridY_to_posCanvasY(int posGridY){
        double canvas_hoehe = this.nocanvas_pane_strecke.getHeight();
        int strecken_hoehe = this.rennstrecke.getHoehe();

        return this.RAND_Y + posGridY / ((double)strecken_hoehe) * (canvas_hoehe - 2*this.RAND_Y);
    }

    private double posCanvasX_to_posGridX(double posCanvasX){
        double canvas_breite = this.nocanvas_pane_strecke.getWidth();
        int grid_breite = this.rennstrecke.getBreite();

        return (posCanvasX - this.RAND_X) * grid_breite / (canvas_breite - 2*this.RAND_X);
    }

    private double posCanvasY_to_posGridY(double posCanvasY){
        double canvas_hoehe = this.nocanvas_pane_strecke.getHeight();
        int grid_hoehe = this.rennstrecke.getHoehe();

        return (posCanvasY - this.RAND_Y) * grid_hoehe / (canvas_hoehe - 2*this.RAND_Y);
    }

    private int posCanvasX_to_posGridX_round(double posCanvasX){
        double posGridX_double = this.posCanvasX_to_posGridX(posCanvasX);

        int posGridX_int;
        if(posGridX_double <= 0.2 && posGridX_double >= -0.2){
            posGridX_int = 0;
        }else if(posGridX_double-(double) ((int)posGridX_double) <= 0.2){
            posGridX_int = (int)posGridX_double;
        }else if(posGridX_double-(double) ((int)posGridX_double) >= 0.8) {
            posGridX_int = ((int) posGridX_double) + 1;
        }else {
            posGridX_int = -1;
        }

        return posGridX_int;
    }



    private int posCanvasY_to_posGridY_round(double posCanvasY){
        double canvas_breite = this.nocanvas_pane_strecke.getWidth();
        int grid_breite = this.rennstrecke.getBreite();

        double posGridY_double = this.posCanvasY_to_posGridY(posCanvasY);

        int posGridY_int;
        if(posGridY_double <= 0.2 && posGridY_double >= -0.2){
            posGridY_int = 0;
        }else if(posGridY_double-(double) ((int)posGridY_double) <= 0.2){
            posGridY_int = (int)posGridY_double;
        }else if(posGridY_double-(double) ((int)posGridY_double) >= 0.8) {
            posGridY_int = ((int) posGridY_double) + 1;
        }else {
            posGridY_int = -1;
        }

        return posGridY_int;
    }




}
**/
