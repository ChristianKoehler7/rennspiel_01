package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.database.Rennstrecke_database_connection;
import de.christian_koehler_iu.rennspiel.database.Spieler_database_connection;
import de.christian_koehler_iu.rennspiel.interfaces.I_rennstrecke_database;
import de.christian_koehler_iu.rennspiel.interfaces.I_spieler_database;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Callback;


import java.lang.reflect.Array;
import java.util.ArrayList;


public class MitspielerWaehlenController {
    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/mitspieler_waehlen_view.fxml";
    public static final String SCENE_NAME = "Mitspieler wählen";

    // object attribute
    @FXML
    GridPane mitspielerWaehlen_grid_root;
    @FXML
    Label mitspielerWaehlen_lb_spieler;
    @FXML
    Label mitspielerWaehlen_lb_rennstrecke;
    @FXML
    Label mitspielerWaehlen_lb_maxAnzMitspieler;
    @FXML
    ListView<String> mitspielerWaehlen_listview_mitspieler;

    private Spieler spieler;
    private Rennstrecke rennstrecke;

    // speicher für die vorhandnen mitspieler
    private final ArrayList<String> mitspieler = new ArrayList<>();
    // speicher für die vorhandnen mitspieler, die im listview angezeigt werden
    private final ObservableList<String> observable_mitspieler_for_listview = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }

    public void initialize_spieler_und_Rennstrecke(Spieler spieler, Rennstrecke rennstrecke){
        this.spieler = spieler;
        this.rennstrecke = rennstrecke;

        // label initialisieren
        mitspielerWaehlen_lb_spieler.setText(spieler.get_name());
        mitspielerWaehlen_lb_rennstrecke.setText(rennstrecke.getName());
        mitspielerWaehlen_lb_maxAnzMitspieler.setText(String.valueOf(rennstrecke.get_max_anz_spielern()-1));

        // mitspieler laden und in arraylist mitspieler speichern
        init_load_mitspieler();

        // listview initialisieren
        init_listview();

    }

    private void init_load_mitspieler(){
        // spieler db verbindung holen
        I_spieler_database i_rennstrecke_database = new Spieler_database_connection();

        // spielernamen laden und in arraylist
        mitspieler.clear(); // arraylist leeren
        mitspieler.addAll(i_rennstrecke_database.load_spieler_namen()); // alle spieler der arraylist hinzufügen
        mitspieler.remove(spieler.get_name()); // hauptspieler aus arraylist entfernen
    }

    private void init_listview(){
        // observable mit werten aus arraylist füllen
        observable_mitspieler_for_listview.clear();
        observable_mitspieler_for_listview.addAll(mitspieler);

        // observable dem listview übergeben
        mitspielerWaehlen_listview_mitspieler.setItems(observable_mitspieler_for_listview);

        // Mehrfachauswahl aktivieren
        mitspielerWaehlen_listview_mitspieler.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        mitspielerWaehlen_listview_mitspieler.addEventFilter(MouseEvent.MOUSE_PRESSED, evt -> {
            Node node = evt.getPickResult().getIntersectedNode();

            // Gehe vom angeklickten Knoten nach oben, bis eine Listenzelle gefunden wird
            // oder klar ist, dass es keine Zelle war, die angeklickt wurde
            while (node != null && node != mitspielerWaehlen_listview_mitspieler && !(node instanceof ListCell)) {
                node = node.getParent();
            }

            // Wenn es Teil einer Zelle ist oder die Zelle selbst,
            // das Ereignis selbst handhaben, anstatt die Standardbehandlung zu verwenden
            if (node instanceof ListCell) {
                // Weitere Verarbeitung verhindern
                evt.consume();

                ListCell cell = (ListCell) node;
                ListView lv = cell.getListView();

                // Den Fokus auf das ListView setzen
                lv.requestFocus();

                if (!cell.isEmpty()) {
                    // Auswahl für nicht-leere Zellen handhaben
                    int index = cell.getIndex();
                    if (cell.isSelected()) {
                        lv.getSelectionModel().clearSelection(index);
                    } else {
                        lv.getSelectionModel().select(index);
                    }
                }
            }
        });

    }


    public void mitspielerWaehlen_bn_zurueck_action(ActionEvent actionEvent) {
        // zurück zu strecke auswählen
        ScenesManager.getInstance().switch_to_strecke_waehlen(spieler);
    }

    public void mitspielerWaehlen_bn_weiter_action(ActionEvent actionEvent) {
        // weiter zu rennstrecke

        // prüfen ob die anzahl gewählter mitspieler, die anzahl an mitspielern für die rennstrecke überschreitet

        // gewählte mitspieler holen
        ObservableList<String> selected_mitspieler = mitspielerWaehlen_listview_mitspieler.getSelectionModel().getSelectedItems();
        // testen ob anzahl gewählter spieler in ordnung ist
        if(selected_mitspieler.size() <= (rennstrecke.get_max_anz_spielern()-1)){
            // anzahl gewählter mitspieler ist ok

            // arraylist mit spieler-objekten für die gewählten mitspieler erzeugen
            ArrayList<Spieler> mitspieler = new ArrayList<>();
            for(String akt_name : selected_mitspieler){
                Spieler akt_spieler = new Spieler_database_connection().load_spieler_complete(akt_name);
                mitspieler.add(akt_spieler);
            }

            // wechseln zur scene rennen
            ScenesManager.getInstance().switch_to_rennen(rennstrecke, spieler, mitspieler);
        }else{
            // zu viele mitspieler für die rennstrecke gewählt
            ScenesManager.getInstance().show_toast_warning("Es dürfen maximal " + (rennstrecke.get_max_anz_spielern()-1) + " Mitspieler gewählt werden!");
        }
    }

    public void mitspielerWaehlen_bn_neuerSpieler_action(ActionEvent actionEvent) {
        // wechseln zu spieler erstellen
        ScenesManager.getInstance().switch_to_spieler_erstellen();
    }
}
