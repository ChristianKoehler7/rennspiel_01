package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;


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
    VBox rennen_vbox_info;
    @FXML
    Label rennen_lb_streckenName;

    private final double B_MAX_PIXEL = 860.0;
    private final double H_MAX_PIXEL = 530.0;

    private Spieler aktiver_spieler;
    private Rennstrecke rennstrecke;
    private final ArrayList<Spieler> spieler = new ArrayList<>();
    private Link_StreckeErstellenController_Rennstrecke link_StreckeErstellenController_Rennstrecke;
//    private final Paint[] farben = {Color.BLUE,Color.RED, Color.ORANGE, Color.GREEN};
    private final String[] farben = {"blue", "red", "orange", "green"};
    private final HashMap<Spieler, String> zurordnung_spieler_farbe = new HashMap<>();
    private final URL url_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css"));









    public void initialize_spieler_rennstrecke_mitspieler(Spieler aktiver_spieler, Rennstrecke rennstrecke, ArrayList<Spieler> mitspieler) {
        // aktiver spieler in attribut speichern
        this.aktiver_spieler = aktiver_spieler;

        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // mitspieler in attribut speichern
        this.spieler.add(aktiver_spieler);
        this.spieler.addAll(mitspieler);

        // den spielern farben zuordnen
        if(spieler.size()>4){
            throw new RuntimeException("Es dürfen maximal 4 Spieler am Rennen teilnehmen!");
        }
        for(int akt_index=0 ; akt_index<spieler.size() ; akt_index++){
            zurordnung_spieler_farbe.put(spieler.get(akt_index), farben[akt_index]);
        }

        // infobox füllen
        rennen_lb_streckenName.setText(rennstrecke.getName());
        for(Spieler akt_spieler : spieler){
            Label akt_label = new Label();
            akt_label.getStylesheets().add(url_to_stylesheet.toExternalForm());
            akt_label.getStyleClass().add("label-large");
            akt_label.setText(akt_spieler.get_name());
            // Textfarbe über CSS-Stil überschreiben
            akt_label.setStyle("-fx-text-fill: " + zurordnung_spieler_farbe.get(akt_spieler));
            rennen_vbox_info.getChildren().add(akt_label);
        }




    }


    public void rennen_group_strecke_mDragged(MouseEvent mouseEvent) {
    }


    public void rennen_group_strecke_mPressed(MouseEvent mouseEvent) {
    }


    public void rennen_group_mReleased(MouseEvent mouseEvent) {
    }


    public void rennen_bn_rennenBeenden_action(ActionEvent actionEvent) {
    }
}
