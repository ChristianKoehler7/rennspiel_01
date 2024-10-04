package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.database.Rennstrecke_database_connection;
import de.christian_koehler_iu.rennspiel.interfaces.I_rennstrecke_database;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;

/**
 * FX-Controller der für die View strecke_waehlen_view verantwortlich ist
 * hier hat der anwender die möglichkeit eine vorhande strecke (standart oder custom) zu wählen
 * außerdem können hier eigene strecken gelöscht werden
 */
public class StreckeWaehlenController {
    // klassen attribute
    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/strecke_waehlen_view.fxml";
    public static final String SCENE_NAME = "Strecke wählen";

    @FXML
    GridPane streckeWaehlen_grid_root;
    @FXML
    ListView<String> streckeWaehlen_listview_standartStrecken;
    @FXML
    ListView<String> streckeWaehlen_listview_eigeneStrecken;

    // object attribute
    private Spieler spieler;

    // speicher für die vorhandnen default strecken
    private final ArrayList<String> strecken_default = new ArrayList<>();
    // speicher für vorhandnen default strecken, die im listview angezeigt werden
    private final ObservableList<String> observable_strecken_default_for_listview = FXCollections.observableArrayList();

    // speicher für die vorhandnen custom strecken
    private final ArrayList<String> strecken_custom = new ArrayList<>();
    // speicher für vorhandnen standart strecken, die im listview angezeigt werden
    private final ObservableList<String> observable_strecken_custom_for_listview = FXCollections.observableArrayList();

    // speicher für strecken_name der im listview gewählt ist (null wenn kein spieler gewählt)
    private String strecken_name_selected = null;

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }

    public void initialize_spieler(Spieler spieler){
        this.spieler = spieler;

        // strecken namen laden
        init_load_strecken();

        // listview standart strecken initialisieren
        init_listview_strecken_default();

        // listview custom strecken initialisieren
        init_listview_strecken_custom();
    }

    private void init_load_strecken(){
        // rennstrecke db verbindung holen
        I_rennstrecke_database i_rennstrecke_database = new Rennstrecke_database_connection();

        // standart strecken namen laden und in arraylist
        strecken_default.clear();
        strecken_default.addAll(i_rennstrecke_database.load_rennstrecke_standart_namen());

        // custom strecken namen laden und in arraylist
        strecken_custom.clear();
        strecken_custom.addAll(i_rennstrecke_database.load_rennstrecke_custom_namen());
    }

    private void init_listview_strecken_default(){
        // observable mit werten aus arraylist füllen
        observable_strecken_default_for_listview.clear();
        observable_strecken_default_for_listview.addAll(strecken_default);

        // observable dem listview übergeben
        streckeWaehlen_listview_standartStrecken.setItems(observable_strecken_default_for_listview);

        // selected listener hinzufügen
        streckeWaehlen_listview_standartStrecken.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(t1 != null) {
                    System.out.println("observableValue = " + observableValue);
                    System.out.println("s = " + s); // s ist der vorherige wert
                    System.out.println("t1 = " + t1);  // t1 ist der neue wert
                    // strecken_name des gewählten listview-elements holen (wenn keins gewählt dann null)
                    strecken_name_selected = streckeWaehlen_listview_standartStrecken.getSelectionModel().getSelectedItem();
                    // andere listview unselecten, da nur eine strecke der beiden listviews selected sein darf
                    streckeWaehlen_listview_eigeneStrecken.getSelectionModel().clearSelection();
                }
            }
        });
    }

    private void init_listview_strecken_custom(){
        // observable mit werten aus arraylist füllen
        observable_strecken_custom_for_listview.clear();
        observable_strecken_custom_for_listview.addAll(strecken_custom);

        // observable dem listview übergeben
        streckeWaehlen_listview_eigeneStrecken.setItems(observable_strecken_custom_for_listview);

        // selected listener hinzufügen
        streckeWaehlen_listview_eigeneStrecken.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1) {
                if(t1 != null){
                    System.out.println("observableValue = " + observableValue);
                    System.out.println("s = " + s);
                    System.out.println("t1 = " + t1);
                    // strecken_name des gewählten listview-elements holen (wenn keins gewählt dann null)
                    strecken_name_selected = streckeWaehlen_listview_eigeneStrecken.getSelectionModel().getSelectedItem();
                    // andere listview unselecten, da nur eine strecke der beiden listviews selected sein darf
                    streckeWaehlen_listview_standartStrecken.getSelectionModel().clearSelection();
                }
            }
        });
    }



    public void streckeWaehlen_bn_zurueck_action(ActionEvent actionEvent) {
        // zurück zur spieler wählen
        ScenesManager.getInstance().switch_to_spieler_wahlen();
    }

    public void streckeWaehlen_bn_weiter_action(ActionEvent actionEvent) {
        if(strecken_name_selected != null){
            // gewählte rennstrecke aus db laden
            I_rennstrecke_database i_rennstrecke_database = new Rennstrecke_database_connection();
            Rennstrecke rennstrecke = i_rennstrecke_database.load_rennstrecke_complete(strecken_name_selected);
            System.out.println(rennstrecke);
            System.out.println(rennstrecke.getName());

            // weiter zu mitspieler wählen
            ScenesManager.getInstance().switch_to_mitspieler_waehlen(spieler, rennstrecke);
        }else{
            ScenesManager.getInstance().show_toast_neutral("Keine Rennstrecke gewählt!");
        }
    }

    public void streckeWaehlen_bn_neuerStrecke_action(ActionEvent actionEvent) {
        // wechseln zu strecke größe wählen
        ScenesManager.getInstance().switch_to_strecke_groesse_wahlen(spieler);
    }

    public void streckeWaehlen_bn_streckeLoeschen_action(ActionEvent actionEvent) {
        if(strecken_name_selected == null){
            // keine strecke gewählt
            // toast ausgeben
            ScenesManager.getInstance().show_toast_neutral("keine Strecke gewählt");
        }else if(strecken_default.contains(strecken_name_selected)){
            // standartstrecke ist gewählt -> darf nicht gelöscht werden
            // toast ausgeben
            ScenesManager.getInstance().show_toast_warning("Standartstrecken dürfen nicht gelöscht werden");

        }else if(strecken_custom.contains(strecken_name_selected)){
            // gewählte wird gelöscht, aber zuvor nochmal fragen
            ScenesManager.getInstance().show_dialog_warning(
                    ("Soll die Strecke \"" + strecken_name_selected + "\" wirklich gelöscht werden?"),
                    "ja",
                    "nein",
                    new ScenesManager.I_dialog_actions() {
                        @Override
                        public void ok_action() {
                            // gewählte rennstrecke löschen mit allen linien und spieler_bestzeiten
                            I_rennstrecke_database i_rennstrecke_database = new Rennstrecke_database_connection();
                            i_rennstrecke_database.delete_rennstrecke_complete(strecken_name_selected);

                            // strecken namen laden
                            init_load_strecken();

                            // listview standart strecken initialisieren
                            init_listview_strecken_default();

                            // listview custom strecken initialisieren
                            init_listview_strecken_custom();
                        }

                        @Override
                        public void cancel_action() {
                            // nix tun
                        }
                    }
            );
        }else{
            // unvorhergesehener zustand -> fehler ausgeben
            throw new RuntimeException("StreckeWaehlenController: streckeWaehlen_bn_streckeLoeschen_action(ActionEvent actionEvent): unvorhergesener Zustand");
        }
    }
}
