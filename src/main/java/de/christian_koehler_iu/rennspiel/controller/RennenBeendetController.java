package de.christian_koehler_iu.rennspiel.controller;

import de.christian_koehler_iu.rennspiel.controller_helper.Spieler_fahrverlauf;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.database.Rennstrecke_database_connection;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * FX-Controller der für die View rennen_beendet_view verantwortlich ist
 * das rennen ist beendet und hier wird die rangliste und streckenrekorde dargestellt
 */
public class RennenBeendetController {

    // class attributes
    public static final String PATH_TO_FXML = "/de/christian_koehler_iu/rennspiel/rennen_beendet_view.fxml";
    public static final String SCENE_NAME = "Rennen";

    // object attributes
    @FXML
    GridPane rennenBeendet_grid_root;
    @FXML
    Button rennenBeendet_bn_weiter;
    @FXML
    Button rennenBeendet_bn_nochmal;
    @FXML
    Button rennenBeendet_bn_beenden;
    @FXML
    GridPane rennenBeendet_grid_spielerPlatzierung;
    @FXML
    GridPane rennenBeendet_grid_streckeBestzeit;


    private final double B_MAX_PIXEL = 860.0;
    private final double H_MAX_PIXEL = 530.0;

    private Spieler aktiver_spieler;
    private Rennstrecke rennstrecke;
    private final ArrayList<Spieler> spieler_alle = new ArrayList<>();
    private final ArrayList<Spieler> mitspieler = new ArrayList<>();
    HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf = new HashMap<>();

    // kostanten
    private final URL url_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css"));
    private final String CSS_STYLE_CLASS_LABEL_SMALL = "label-small";

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
    }


    public void initialize_rennstrecke_spieler_mitspieler_fahrverlaeufe(
            Rennstrecke rennstrecke,
            Spieler aktiver_spieler,
            ArrayList<Spieler> mitspieler,
            HashMap<Spieler, Spieler_fahrverlauf> zuordnung_spieler_spielerFahrverlauf
    ) {
        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // aktiver spieler in attribut speichern
        this.aktiver_spieler = aktiver_spieler;

        // mitspieler in attribut speichern
        this.mitspieler.clear();
        this.mitspieler.addAll(mitspieler);

        // mitspieler in attribut speichern
        this.mitspieler.clear();
        this.mitspieler.addAll(mitspieler);

        // alle spieler in arraylist
        spieler_alle.clear();
        spieler_alle.add(aktiver_spieler);
        spieler_alle.addAll(mitspieler);

        // spieler fahrverlaeufe in attribut speichern
        this.zuordnung_spieler_spielerFahrverlauf.clear();
        this.zuordnung_spieler_spielerFahrverlauf.putAll(zuordnung_spieler_spielerFahrverlauf);

        // spieler arrayList umsortieren nach platzierung
        init_sort_spieler_nach_zeit();

        // gridpane der spieler platzierung mit daten füllen
        init_grid_spieler_paltzierung();

        // gridpane der strecken bestzeit mit daten füllen
        init_grid_strecken_bestzeit();
    }

    /**
     * sortiert die arraylist mit allen spielern um, aufsteigend nach der rennzeit
     */
    private void init_sort_spieler_nach_zeit(){
        System.out.println("spieler vor sortierung: " + spieler_alle);
        for(int akt_platz=0; akt_platz< spieler_alle.size()-1 ; akt_platz++){
            Spieler akt_spieler = spieler_alle.get(akt_platz);
            Spieler_fahrverlauf akt_spieler_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);
            Double akt_spieler_zeit = akt_spieler_fahrverlauf.get_gesamt_renn_zeit();
            int akt_spieler_index_kleinste_zeit = akt_platz;
            Double akt_bestzeit = akt_spieler_zeit;

            for(int index_akt_sortierlauf = akt_platz+1; index_akt_sortierlauf< spieler_alle.size() ; index_akt_sortierlauf++){
                akt_spieler = spieler_alle.get(index_akt_sortierlauf);
                akt_spieler_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);
                akt_spieler_zeit = akt_spieler_fahrverlauf.get_gesamt_renn_zeit();
                if(akt_spieler_zeit!=null && (akt_bestzeit==null || akt_spieler_zeit<akt_bestzeit) ){
                    // für akt platz ist das eine neue bestzeit
                    akt_spieler_index_kleinste_zeit = index_akt_sortierlauf;
                    akt_bestzeit = akt_spieler_zeit;
                }
            }
            // spieler mit bestzeit für den akt_platz aus arraylist löschen
            Spieler spieler_fuer_akt_platz = spieler_alle.get(akt_spieler_index_kleinste_zeit);
            spieler_alle.remove(spieler_fuer_akt_platz);
            // spieler wieder in arraylist einfügen, aber an die richtige stelle
            spieler_alle.add(akt_platz, spieler_fuer_akt_platz);
        }
        System.out.println("spieler nach sortierung: " + spieler_alle);
    }


    private void init_grid_spieler_paltzierung(){
        for(int akt_spieler_index = 0; akt_spieler_index< spieler_alle.size() ; akt_spieler_index++){
            // akt spieler holen
            Spieler akt_spieler = spieler_alle.get(akt_spieler_index);

            // akt fahrverlauf holen
            Spieler_fahrverlauf akt_fahrverlauf = zuordnung_spieler_spielerFahrverlauf.get(akt_spieler);

            // label platzierung erstellen
            Label lb_spieler_platzierung = new Label();
            lb_spieler_platzierung.setText(String.valueOf(akt_spieler_index+1));
            lb_spieler_platzierung.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);


            // label spieler name erstellen
            Label lb_spieler_name = new Label();
            lb_spieler_name.setText(akt_spieler.get_name());
            lb_spieler_name.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);

            // label rennzeit erstellen
            Label lb_spieler_zeit = new Label();
            lb_spieler_zeit.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
            if(akt_fahrverlauf.get_is_im_ziel() && akt_fahrverlauf.get_gesamt_renn_zeit()!=null){
                lb_spieler_zeit.setText( String.format("%.3f",akt_fahrverlauf.get_gesamt_renn_zeit()) );
            }else{
                // spieler hat rennen nicht regulär beendet
                lb_spieler_zeit.setText( "--" );
            }

            // label spieler bestzeit für die strecke erstellen
            Label lb_spieler_bestzeit = new Label();
            lb_spieler_bestzeit.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
            Double bestzeit = akt_spieler.get_bestzeit_von_strecke(rennstrecke.getName());
            if( bestzeit!= null && akt_spieler.get_bestzeit_von_strecke(rennstrecke.getName()) != null ){
                // es gibt für den spieler und für die strecke eine bestzeit
                lb_spieler_bestzeit.setText( String.format("%.3f",bestzeit) );
            }else{
                // es gibt noch keine bestzeit für die strecke
                lb_spieler_bestzeit.setText( "--" );
            }

            // label spieler bemerkungen erstellen
            Label lb_spieler_bemerkungen = new Label();
            lb_spieler_bemerkungen.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);

            Double strecken_bestzeit = new Rennstrecke_database_connection().get_strecken_bestzeit(rennstrecke.getName());
            Double spieler_zeit_nullable = akt_fahrverlauf.get_gesamt_renn_zeit();
            Double spieler_bestzeit_nullable = akt_spieler.get_bestzeit_von_strecke(rennstrecke.getName());

            if(spieler_zeit_nullable == null){
                // es gibt für den spieler und für die strecke eine keine bestzeit
                lb_spieler_bemerkungen.setText( "CRASHED" );
            }else if( spieler_zeit_nullable.equals(strecken_bestzeit) ){
                // zeit ist die strecken_bestzeit
                lb_spieler_bemerkungen.setText( "neuer Streckenrekord" );
            }else if( spieler_zeit_nullable.equals(spieler_bestzeit_nullable) ){
                // zeit ist die persönliche bestzeit
                lb_spieler_bemerkungen.setText( "neue persöhniche Bestzeit" );
            }

            // label der grid übergeben
            rennenBeendet_grid_spielerPlatzierung.add(lb_spieler_platzierung, 0, akt_spieler_index+1);
            rennenBeendet_grid_spielerPlatzierung.add(lb_spieler_name, 1, akt_spieler_index+1);
            rennenBeendet_grid_spielerPlatzierung.add(lb_spieler_zeit, 2, akt_spieler_index+1);
            rennenBeendet_grid_spielerPlatzierung.add(lb_spieler_bestzeit, 3, akt_spieler_index+1);
            rennenBeendet_grid_spielerPlatzierung.add(lb_spieler_bemerkungen, 4, akt_spieler_index+1);
        }
    }

    private void init_grid_strecken_bestzeit(){
        // label strecken name erstellen
        Label lb_strecken_name = new Label();
        lb_strecken_name.setText(rennstrecke.getName());
        lb_strecken_name.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);

        // label strecken bestzeit erstellen
        Label lb_strecken_bestzeit = new Label();
        lb_strecken_bestzeit.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
        Double bestzeit = new Rennstrecke_database_connection().get_strecken_bestzeit(rennstrecke.getName());
        if(bestzeit == null){
            lb_strecken_bestzeit.setText("--");
        }else{
            lb_strecken_bestzeit.setText( String.format("%.3f",bestzeit) );
        }

        // label strecken bester spieler erstellen
        Label lb_strecken_bestspieler = new Label();
        lb_strecken_bestspieler.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
        String bester_spieler = new Rennstrecke_database_connection().get_strecke_bester_spieler(rennstrecke.getName());
        if(bester_spieler == null){
            lb_strecken_bestspieler.setText("--");
        }else{
            lb_strecken_bestspieler.setText( bester_spieler );
        }

        // label der grid übergeben
        rennenBeendet_grid_streckeBestzeit.add(lb_strecken_name, 0, 1);
        rennenBeendet_grid_streckeBestzeit.add(lb_strecken_bestzeit, 1, 1);
        rennenBeendet_grid_streckeBestzeit.add(lb_strecken_bestspieler, 2, 1);
    }


    public void rennenBeendet_bn_weiter_action(ActionEvent actionEvent) {
        // weiter zu strecke wählen scene
        ScenesManager.getInstance().switch_to_strecke_waehlen(aktiver_spieler);
    }


    public void rennenBeendet_bn_nochmal_action(ActionEvent actionEvent) {
        // gleiches rennen nochmal -> wechseln zu rennen
        ScenesManager.getInstance().switch_to_rennen(
                rennstrecke,
                aktiver_spieler,
                mitspieler
        );
    }


    public void rennenBeendet_bn_beenden_action(ActionEvent actionEvent) {
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
}
