package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.database.Spieler_database_connection;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
    private final ObservableList<String> observable_vorhandene_spieler_for_listview = FXCollections.observableArrayList();
    // speicher für spieler_name der im listview gewählt ist (null wenn kein spieler gewählt)
    private String spieler_name_selected;

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");

        // listview initialisieren
        init_listview();
    }

    // listview initialisieren
    private void init_listview(){
        // listview und observableList verbinden
        spielerWaehlen_listview_spieler.setItems(observable_vorhandene_spieler_for_listview);
        // ChangeListener wenn sich ausgewähltes item der listview ändert
        spielerWaehlen_listview_spieler.getSelectionModel().selectedItemProperty().addListener( (observable, oldValue, newValue) ->{
            // spieler_name des gewählten listview-elements holen (wenn keins gewählt dann null)
            int index_selected = spielerWaehlen_listview_spieler.getSelectionModel().getSelectedIndex();
            spieler_name_selected = spielerWaehlen_listview_spieler.getSelectionModel().getSelectedItem();
            System.out.println("selected_spieler_name = " + spieler_name_selected);
        });

        // spieler_namen der observableList übergeben
        reload_listview();
    }

    private void reload_listview(){
        // spieler namen aus db holen und in arrayList speichern
        vorhandene_spieler.clear();
        vorhandene_spieler.addAll(
                new Spieler_database_connection().load_spieler_namen()
        );
        // observableList leeren
        observable_vorhandene_spieler_for_listview.clear();
        // spieler_namen der observableList übergeben
        observable_vorhandene_spieler_for_listview.addAll(vorhandene_spieler);
    }

    public void spielerWaehlen_bn_beenden_action(ActionEvent actionEvent) {
        // dialog anzeigen, ob wirklich beendet werden soll
        ScenesManager.getInstance().show_dialog_warning("wirklich beenden?",
                "Ja",
                "Nein",
                new ScenesManager.I_dialog_actions() {
                    @Override
                    public void ok_action() {
                        // spiel beenden
                        Platform.exit();
                    }
                    @Override
                    public void cancel_action() {
                        // nix tun, dialog wird automatisch beendet
                    }
                });
    }

    public void spielerWaehlen_bn_weiter_action(ActionEvent actionEvent) {
        // gewählten spieler laden
        if(spieler_name_selected==null || spieler_name_selected.isEmpty()){
            // kein spieler selected
            ScenesManager.getInstance().show_toast_neutral("kein Spieler gewählt");
        }else{
            // ein spieler ist ausgewählt -> spieler aus db laden
            Spieler spieler = new Spieler_database_connection().load_spieler_complete(spieler_name_selected);
            ScenesManager.getInstance().switch_to_strecke_waehlen(spieler);
        }

    }

    public void spielerWaehlen_bn_neuerSpieler_action(ActionEvent actionEvent) {
        // zur scene spieler_erstellen wechseln
        ScenesManager.getInstance().switch_to_spieler_erstellen();
    }

    public void spielerWaehlen_bn_spielerLoeschen_action(ActionEvent actionEvent) {
        // spieler löschen, der im listview selected
        if(spieler_name_selected==null || spieler_name_selected.isEmpty()){
            // kein spieler selected
            ScenesManager.getInstance().show_toast_neutral("kein Spieler gewählt");
        }else{
            // ein spieler selected
            // dialog anzeigen, ob spieler wirklich gelöscht werden soll
            ScenesManager.getInstance().show_dialog_warning("Spieler \"" + spieler_name_selected + "\" wirklich löschen?",
                    "Ja",
                    "Nein",
                    new ScenesManager.I_dialog_actions() {
                        @Override
                        public void ok_action() {
                            // spieler löschen
                            spieler_selected_loeschen();
                        }
                        @Override
                        public void cancel_action() {
                            // nix tun, dialog wird automatisch beendet
                        }
                    });
        }
    }

    private void spieler_selected_loeschen(){
        // gewählten spieler und streckenbestzeiten aus db löschen
        new Spieler_database_connection().delete_spieler_complete(spieler_name_selected);
        // listview neu laden
        reload_listview();
    }





}
