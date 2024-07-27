package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.interfaces.I_rennstrecke_database;

import java.util.ArrayList;

public class Rennstrecke_database_connection implements I_rennstrecke_database {


    @Override
    public void save_rennstrecke(Rennstrecke rennstrecke) {
        // TODO wenn streckenname schon vorhanden, -> alles von rennstrecke löschen

        // alles von rennstrecke speichern

    }

    @Override
    public Rennstrecke load_rennstrecke(String strecken_name) {
        // TODO rennstrecke laden
        return null;
    }

    @Override
    public ArrayList<String> load_rennstrecke_namen() {
        return null;
    }

    @Override
    public ArrayList<String> load_rennstrecke_standart_namen() {
        return null;
    }

    @Override
    public ArrayList<String> load_rennstrecke_custom_namen() {
        return null;
    }
}
