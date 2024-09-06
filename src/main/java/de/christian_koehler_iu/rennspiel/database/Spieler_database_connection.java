package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
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
    public Spieler load_spieler_complete(String spieler_name) {
        try {
            return new Spieler_db_table().get_spieler_complete(spieler_name);
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

    @Override
    public void delete_spieler_complete(String spieler_name) {
        try {
            new Spieler_db_table().delete_spieler_complete(spieler_name);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void save_spieler_rennstrecke_bestzeit(String spieler_name, String strecken_name, Double bestzeit) {
        try {
            // alte spieler_strecke_bestzeit holen
            Double bestzeit_alt = null;
            LinkRennstreckeSpielerBestzeit_db_table linkRennstreckeSpielerBestzeit_db_table = new LinkRennstreckeSpielerBestzeit_db_table();
            System.out.println("Spieler_database_connection: save_spieler_rennstrecke_bestzeit(): 0");
            bestzeit_alt = linkRennstreckeSpielerBestzeit_db_table.get_spieler_strecke_bestzeit(strecken_name, spieler_name);

            System.out.println("Spieler_database_connection: save_spieler_rennstrecke_bestzeit(): 1: bestzeit_alt=" + bestzeit_alt);
            if(bestzeit_alt == null){
                // es gibt für den spieler und die strecke noch keine bestzeit,
                //  -> neuen db eintrag erstellen
                linkRennstreckeSpielerBestzeit_db_table.create_new_spieler_strecke_bestzeit(strecken_name, spieler_name, bestzeit);
                System.out.println("Spieler_database_connection: save_spieler_rennstrecke_bestzeit(): 2");

            }else if(bestzeit < bestzeit_alt){
                // die neue bestzeit ist schneller als die alte
                //  alte bestzeit updaten
                linkRennstreckeSpielerBestzeit_db_table.update_spieler_strecke_bestzeit(strecken_name, spieler_name, bestzeit);
            }else{
                // neue bestzeit ist langsamer als die bisherige
                //  -> fehler ausgeben
                throw new RuntimeException("Spieler_database_connection:save_spieler_rennstrecke_bestzeit(...) neue Zeit ist langssamer als bisherige!");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
