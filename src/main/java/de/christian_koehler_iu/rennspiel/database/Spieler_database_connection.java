package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import de.christian_koehler_iu.rennspiel.interfaces.I_rennstrecke_database;
import de.christian_koehler_iu.rennspiel.interfaces.I_spieler_database;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * klasse die die Verbindung zwischen den java-klassen und der datenbank herstellt, was die Spieler angeht.
 */
public class Spieler_database_connection implements I_spieler_database {

    @Override
    public void save_spieler(Spieler spieler) {
        try {
            new Spieler_db_table().save_new_spieler(spieler);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Spieler load_spieler(String spieler_name) {
        try {
            return new Spieler_db_table().get_spieler(spieler_name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ArrayList<String> load_spieler_namen() {
        try {
            return new Spieler_db_table().get_spieler_namen();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
