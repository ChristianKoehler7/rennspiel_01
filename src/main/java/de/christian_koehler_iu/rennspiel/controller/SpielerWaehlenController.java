package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.database.Spieler_database_connection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

public class SpielerWaehlenController {

    @FXML
    private GridPane spielerWaehlen_grid_root;
    @FXML
    private ListView<String> spielerWaehlen_listview_spieler;
    @FXML
    private Button spielerWaehlen_bn_beenden;
    @FXML
    private Button spielerWaehlen_bn_weiter;
    @FXML
    private Button spielerWaehlen_bn_neuerSpieler;
    @FXML
    private Button spielerWaehlen_bn_spielerLoeschen;

    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/spieler_waehlen_view.fxml";
    public static final String SCENE_NAME = "Spieler waehlen";

    // speicher für die vorhandnen spieler_namen
    private final ArrayList<String> vorhandene_spieler = new ArrayList<>();
    // speicher für spieler_namen, die im listview angezeigt werden
    private final ObservableList<String> observable_streckenlinien_for_listview = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");

        // spieler namen aus db holen und in arrayList speichern
        vorhandene_spieler.clear();
        vorhandene_spieler.addAll(
        new Spieler_database_connection().load_spieler_namen()
        );

        // spieler_namen dem listView speicher übergeben
        observable_streckenlinien_for_listview.addAll(vorhandene_spieler);
    }

    public void spielerWaehlen_bn_beenden_action(ActionEvent actionEvent) {
        // TODO
    }

    public void spielerWaehlen_bn_weiter_action(ActionEvent actionEvent) {
        // TODO
    }

    public void spielerWaehlen_bn_neuerSpieler_action(ActionEvent actionEvent) {
        // TODO
    }

    public void spielerWaehlen_bn_spielerLoeschen_action(ActionEvent actionEvent) {
        // TODO
    }





}
