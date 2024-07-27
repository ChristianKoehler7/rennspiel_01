package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.interfaces.I_rennstrecke_database;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * klasse die die Verbindung zwischen den java-klassen und der datenbank herstellt, was die Rennstrecke darstellt.
 */
public class Rennstrecke_database_connection implements I_rennstrecke_database {

    @Override
    public void save_rennstrecke(Rennstrecke rennstrecke) {
        // alles von rennstrecke speichern
        try {
            new Rennstrecke_db_table().save_rennstrecke(rennstrecke);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
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
