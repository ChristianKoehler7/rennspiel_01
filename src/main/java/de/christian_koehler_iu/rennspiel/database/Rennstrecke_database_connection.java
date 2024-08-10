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
    public void save_rennstrecke_complete(Rennstrecke rennstrecke) {
        // alles von rennstrecke speichern
        try {
            new Rennstrecke_db_table().save_rennstrecke(rennstrecke);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Rennstrecke load_rennstrecke_complete(String strecken_name) {
        try {
            return new Rennstrecke_db_table().load_rennstrecke_complete(strecken_name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<String> load_rennstrecke_namen() {
        try {
            return new Rennstrecke_db_table().get_strecken_namen();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<String> load_rennstrecke_standart_namen() {
        try {
            return new Rennstrecke_db_table().get_strecke_standart_namen();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<String> load_rennstrecke_custom_namen() {
        try {
            return new Rennstrecke_db_table().get_strecke_custom_namen();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete_rennstrecke_complete(String strecken_name) {
        try {
            new Rennstrecke_db_table().delete_rennstrecke_complete(strecken_name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
