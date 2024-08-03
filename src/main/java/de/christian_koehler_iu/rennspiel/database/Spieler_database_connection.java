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

    /**
     * legt einen neuen eintrag für den spielernamen an,
     *  es werden jedoch keine bestzeiten gespeichert, nur der name in der tabelle spieler
     *  es wird nicht geprüft, ob der name schon vorhanden
     *  dh. erst testen ob name schon vorhanden mit methode is_spieler_name_vorhanden
     * @param spieler_name
     */
    @Override
    public void save_new_spieler(String spieler_name) {
        try {
            new Spieler_db_table().save_new_spieler(spieler_name);
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

    @Override
    public boolean is_spieler_name_vorhanden(String spieler_name) {
        ArrayList<String> spieler_namen = this.load_spieler_namen();
        return spieler_namen.contains(spieler_name);
    }
}
