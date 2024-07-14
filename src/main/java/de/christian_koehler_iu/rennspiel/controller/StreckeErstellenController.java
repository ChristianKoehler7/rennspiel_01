package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.utility.Link_StreckeErstellenController_Rennstrecke;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import de.christian_koehler_iu.rennspiel.datasets.Rennstrecke;

public class StreckeErstellenController {
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
    private Button streckeErstellen_bn_startRichtungAendern;

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

    private Link_StreckeErstellenController_Rennstrecke link_StreckeErstellenController_Rennstrecke;

    private final double B_MAX_PIXEL = 896.0-20.0;
    private final double H_MAX_PIXEL = 560.0-20.0;

    private Rennstrecke rennstrecke;


    public enum Zeichnungszustand {
        STRECKENLINIE_ZEICHNEN,
        STARTLINIE_ZEICHNEN,
        KEINS_GEWAEHLT;
    }
    private Zeichnungszustand zeichnungszustand = Zeichnungszustand.KEINS_GEWAEHLT;


    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }

    
    
    public void initialize_rennstrecke(Rennstrecke rennstrecke){
        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // Link_StreckeErstellenController_Rennstrecke objekt erstellen
        this.link_StreckeErstellenController_Rennstrecke = new Link_StreckeErstellenController_Rennstrecke(
                this.B_MAX_PIXEL,
                this.H_MAX_PIXEL,
                new Link_StreckeErstellenController_Rennstrecke.I_Link_StrErstController_Rennstrecke() {
                    @Override
                    public Zeichnungszustand get_zeichnungszustand() {
                        return zeichnungszustand;
                    }

                    @Override
                    public Group get_group() {
                        return streckeErstellen_group_strecke;
                    }

                    @Override
                    public Rennstrecke get_rennstrecke() {
                        return rennstrecke;
                    }
                }
        );

        // labeltext setzen
        this.streckeErstellen_lb_streckengroesse.setText("Streckenname: " + rennstrecke.getName() + "\nBreite: " + rennstrecke.getBreite() + "\nHöhe: " + rennstrecke.getHoehe());

        // speicher für listview initialisieren
        this.streckeErstellen_listview_streckenlinien.setItems(this.link_StreckeErstellenController_Rennstrecke.get_observable_streckenlinien_for_listview());
        //this.update_streckenLinien_listView();

        // ChangeListener wenn sich ausgewähltes item der listview ändert erstellen
        this.init_create_listView_selected_changeListener();
    }

    private void init_create_listView_selected_changeListener(){
        // ChangeListener wenn sich ausgewähltes item der listview ändert
        this.streckeErstellen_listview_streckenlinien.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) ->{
            // index der gewählten streckenlinie holen (wenn keins gewählt dann -1)
            int index_selected = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();

            // weitergebenn an link_StreckeErstellenController_Rennstrecke
            this.link_StreckeErstellenController_Rennstrecke.listView_selected_changed(index_selected);

//            // evtl ist bereits eine groupLine eingefärbt, dh alle einfärbungen rückgängig machen
//            this.grouplines_farben_wiederherstellen();
//
//            // index der gewählten streckenlinie holen (wenn keins gewählt dann -1)
//            int index_selected = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();
//            System.out.println("index_selected = " + index_selected);
//
//            if(index_selected >= 0){
//                // gewählte linie grün färben
//                // gew rennstrecke.streckenlinie holen
//                Linie gewStreckenlinie = this.rennstrecke.getStreckenlinien().get(index_selected);
//                // groupline holen
//                Line gewGroupLine = this.get_groupLine_from_streckenLinie(gewStreckenlinie);
//                System.out.println("gewGroupLine = " + gewGroupLine);
//                if (gewGroupLine != null) {
//                    gewGroupLine.setStroke(Color.GREEN);
//                }
//            }
        });
    }

    public void streckeErstellen_bn_zurueck_action(ActionEvent actionEvent) {
        ScenesManager.getInstance().goBack();
    }

    public void streckeErstellen_bn_gewStrLinieLoeschen_action(ActionEvent actionEvent) {
        // löscht die line, die im listview selected ist

        // index der gewählten linie holen (wenn nix selected dann -1)
        int index_selected_item = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();

        // streckenline löschen
        link_StreckeErstellenController_Rennstrecke.delete_streckenlinie(index_selected_item);

//        // index der gewählten linie holen (wenn nix selected dann -1)
//        int index_selected_item = this.streckeErstellen_listview_streckenlinien.getSelectionModel().getSelectedIndex();
//
//        if(index_selected_item >=0){
//            // rennstrecke.streckenlinie holen
//            Linie gewStreckenLinie = this.rennstrecke.getStreckenlinien().get(index_selected_item);
//            // grouplinie holen
//            Line gewGroupLine = this.get_groupLine_from_streckenLinie(gewStreckenLinie);
//            // groupline aus gui löschen
//            this.streckeErstellen_group_strecke.getChildren().remove(gewGroupLine);
//            // rennstrecke.streckenlinie holen löschen
//            this.rennstrecke.getStreckenlinien().remove(gewStreckenLinie);
//            // listview aktualisieren
//            this.update_streckenLinien_listView();
//        }
    }

//    private void grouplines_farben_wiederherstellen(){
//        for(Node aktNode : this.streckeErstellen_group_strecke.getChildren()){
//            if(aktNode instanceof Line){
//                // linienart herausfinden
//                // pixel positionen der aktNode holen
//                double x0_pixel_groupLine = ((Line) aktNode).getStartX();
//                double y0_pixel_groupLine = ((Line) aktNode).getStartY();
//                double x1_pixel_groupLine = ((Line) aktNode).getEndX();
//                double y1_pixel_groupLine = ((Line) aktNode).getEndY();
//
//                // temp linien objekt erzeugen um auf gleichheit zu prüfen
//                Integer x0_grid_groupLine = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(x0_pixel_groupLine);
//                Integer y0_grid_groupLine = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(y0_pixel_groupLine);
//                Integer x1_grid_groupLine = this.umrechnung_grid_pixel.posXPixel_to_posXGrid_round(x1_pixel_groupLine);
//                Integer y1_grid_groupLine = this.umrechnung_grid_pixel.posYPixel_to_posYGrid_round(y1_pixel_groupLine);
//                if(x0_grid_groupLine==null || y0_grid_groupLine==null || x1_grid_groupLine==null || y1_grid_groupLine==null){
//                    throw new RuntimeException("Gridposition ist null, das kann hier eigentlich nicht sein!");
//                }
//                Linie tmpLinie = new Linie(x0_grid_groupLine, y0_grid_groupLine, x1_grid_groupLine, y1_grid_groupLine);
//
//                // prüfen ob aktNode eine streckenlinie ist
//                for(Linie aktStreckenLinie : this.rennstrecke.getStreckenlinien()){
//                    if(aktStreckenLinie.equals(tmpLinie)){
//                        // aktNode ist eine streckenlinie => blau färben
//                        ((Line) aktNode).setStroke(Color.BLUE);
//                    }
//                }
//
//                if(this.rennstrecke.getStartlinie() !=null
//                        && this.rennstrecke.getStartlinie().equals(tmpLinie)){
//                    // aktNode ist eine startlinie => rot färben
//                    ((Line) aktNode).setStroke(Color.RED);
//                }
//            }
//        }
//    }


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
        this.link_StreckeErstellenController_Rennstrecke.delete_last_streckenlinie();
//        // letzte streckenlinie der rennstrecke holen
//        if(!this.rennstrecke.getStreckenlinien().isEmpty()){
//            Linie lastStreckenLine = this.rennstrecke.getStreckenlinien().getLast();
//            // groupLine holen
//            Line groupLine = this.get_groupLine_from_streckenLinie(lastStreckenLine);
//            // groupLine aus gui entfernen
//            this.streckeErstellen_group_strecke.getChildren().remove(groupLine);
//            // streckenlinie aus rennstrecke löschen
//            this.rennstrecke.getStreckenlinien().removeLast();
//            // listview aktualisieren
//            this.update_streckenLinien_listView();
//        }
    }

    public void streckeErstellen_bn_startRichtungAendern_action(ActionEvent actionEvent) {
        this.link_StreckeErstellenController_Rennstrecke.start_richtung_aendern();
    }

    public void streckeErstellen_group_strecke_mPressed(MouseEvent mouseEvent) {
        this.link_StreckeErstellenController_Rennstrecke.group_mPressed(mouseEvent);
    }

    public void streckeErstellen_group_strecke_mDragged(MouseEvent mouseEvent){
        this.link_StreckeErstellenController_Rennstrecke.group_mDraged(mouseEvent);
    }

    public void streckeErstellen_group_mReleased(MouseEvent mouseEvent){
        this.link_StreckeErstellenController_Rennstrecke.group_mReleased(mouseEvent);
    }

//    @Nullable
//    private Line get_groupLine_from_streckenLinie(Linie linie){
//        // pixelpositionen der eingabeLinie holen
//        double x0_pixel_streckenLinie = this.umrechnung_grid_pixel.posXGrid_to_posXPixel(linie.getP1().getX());
//        double y0_pixel_streckenLinie = this.umrechnung_grid_pixel.posYGrid_to_posYPixel(linie.getP1().getY());
//        double x1_pixel_streckenLinie = this.umrechnung_grid_pixel.posXGrid_to_posXPixel(linie.getP2().getX());
//        double y1_pixel_streckenLinie = this.umrechnung_grid_pixel.posYGrid_to_posYPixel(linie.getP2().getY());
//
////        System.out.println("private Line get_groupLine_from_streckenLinie(Linie linie){");
////        System.out.println("x0_pixel_streckenLinie = " +x0_pixel_streckenLinie);
////        System.out.println("y0_pixel_streckenLinie = " +y0_pixel_streckenLinie);
////        System.out.println("x1_pixel_streckenLinie = " +x1_pixel_streckenLinie);
////        System.out.println("y1_pixel_streckenLinie = " +y1_pixel_streckenLinie);
//
//        int aktIndex = 0;
//        while( aktIndex < this.streckeErstellen_group_strecke.getChildren().size() ){
//            if(this.streckeErstellen_group_strecke.getChildren().get(aktIndex) instanceof Line){
//                double x0_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getStartX();
//                double y0_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getStartY();
//                double x1_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getEndX();
//                double y1_groupLine = ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex)).getEndY();
//                if(   x0_pixel_streckenLinie==x0_groupLine
//                   && y0_pixel_streckenLinie==y0_groupLine
//                   && x1_pixel_streckenLinie==x1_groupLine
//                   && y1_pixel_streckenLinie==y1_groupLine ){
//                    // linie gefunden
//                    return ((Line) this.streckeErstellen_group_strecke.getChildren().get(aktIndex));
//                }
//            }
//            aktIndex++;
//        }
//        // keine line gefunden
//        return null;
//    }
}