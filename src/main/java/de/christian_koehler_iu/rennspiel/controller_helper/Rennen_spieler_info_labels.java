package de.christian_koehler_iu.rennspiel.controller_helper;

import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * beim rennen gibt es links eine infobox
 * in der infobox befindet sich eine gridpane, in der für alle spieler infos angezeigt werden
 *  - spieler_name
 *  - akt runde
 *  - akt zeit
 *  - spieler_status
 *  ein objekt dieser klasse stellt eine zeile des gridpanes dar
 *  es werden hier die labels erstellt. diese müssen dann von der vernwendenden klasse in das gridpane eingefügt werden
 */
public class Rennen_spieler_info_labels {
    private final Label lb_spieler_name;
    private final Label lb_spieler_runde;
    private final Label lb_spieler_zeit;
    private final Label lb_spieler_status;
    private final String spieler_farbe;
    private final int anz_runden_der_strecke;

    // konstanten
    private final String path_to_stylesheet = Objects.requireNonNull(getClass().getResource("/de/christian_koehler_iu/rennspiel/styles_04.css")).toExternalForm();
    private final String CSS_STYLE_CLASS_LABEL_SMALL = "label-small";



    public Rennen_spieler_info_labels(String spieler_name,
                                      String spieler_farbe,
                                      int anz_runden_der_strecke) {

        this.spieler_farbe = spieler_farbe;
        this.anz_runden_der_strecke = anz_runden_der_strecke;

        // label erstellen
        lb_spieler_name = new Label();
        lb_spieler_name.setText(spieler_name);
        lb_spieler_name.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
        lb_spieler_name.setStyle("-fx-text-fill: " + spieler_farbe + ";");

        lb_spieler_runde = new Label();
        lb_spieler_runde.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
        lb_spieler_runde.setStyle("-fx-text-fill: " + spieler_farbe + ";");
        set_spieler_runde(1);

        lb_spieler_zeit = new Label();
        lb_spieler_zeit.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
        lb_spieler_zeit.setStyle("-fx-text-fill: " + spieler_farbe + ";");
        set_spieler_zeit(0.0);

        lb_spieler_status = new Label();
        lb_spieler_status.getStyleClass().add(CSS_STYLE_CLASS_LABEL_SMALL);
        lb_spieler_status.setStyle("-fx-text-fill: " + spieler_farbe + ";");
        set_spieler_status("aktiv");

    }

    public void set_spieler_runde(int akt_runde){
        lb_spieler_runde.setText(akt_runde + "/" + anz_runden_der_strecke);
    }

    public void set_spieler_zeit(Double akt_zeit){
        lb_spieler_zeit.setText(String.valueOf(akt_zeit));
    }

    public void set_spieler_status(String text){
        lb_spieler_status.setText(text);
    }

    // getter

    @NotNull
    public Label get_lb_spieler_name() {
        return lb_spieler_name;
    }

    @NotNull
    public Label get_lb_spieler_runde() {
        return lb_spieler_runde;
    }

    @NotNull
    public Label get_lb_spieler_zeit() {
        return lb_spieler_zeit;
    }

    @NotNull
    public Label get_lb_spieler_status() {
        return lb_spieler_status;
    }
}

