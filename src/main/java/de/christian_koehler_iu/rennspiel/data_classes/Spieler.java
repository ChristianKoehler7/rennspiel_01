package de.christian_koehler_iu.rennspiel.data_classes;

import de.christian_koehler_iu.rennspiel.database.Spieler_database_connection;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Spieler {
    private String name;
    private final HashMap<String, Double> strecken_bestzeiten = new HashMap<>();

    public Spieler(String name) {
        this.name = name;
    }

    public String get_name() {
        return name;
    }

    public HashMap<String, Double> get_strecken_bestzeiten() {
        return strecken_bestzeiten;
    }

    public void set_strecken_bestzeiten(HashMap<String, Double> strecken_bestzeiten) {
        this.strecken_bestzeiten.clear();
        this.strecken_bestzeiten.putAll(strecken_bestzeiten);
    }

    /**
     * nach jedem rennen kann hier die streckenzeit übergeben werden,
     *  die methode kümmert sich dann daraum zu prüfen, ob das eine neue persönliche strecken bestzeit ist
     *  und wenn ja wird sie im objekt und in der datenbank gespeichert
     * @param strecken_name
     * @param strecken_zeit_nullable
     */
    public void process_new_strecken_zeit(String strecken_name, Double strecken_zeit_nullable){
        // db verbindung holen
        Spieler_database_connection spieler_database_connection = new Spieler_database_connection();

        // prüfen, ob strecken_zeit eine neue strecken_bestzeit ist
        // alte bestzeit holen
        Double bestzeit_alt_nullable = strecken_bestzeiten.get(strecken_name);
        if(strecken_zeit_nullable!=null){
            // aktuelle streckenzeit hat einen wert
            if( bestzeit_alt_nullable==null || strecken_zeit_nullable<bestzeit_alt_nullable){
                // akt_streckenzeit ist schneller als die bisherige
                // spieler objekt neue bestzeit speichern
                strecken_bestzeiten.put(strecken_name, strecken_zeit_nullable);
                // neue bestzeit in db speichern
                spieler_database_connection.save_spieler_rennstrecke_bestzeit(name, strecken_name, strecken_zeit_nullable);
            }
        }
    }

    /**
     * gibt die bestzeit des spielers für die angegebene strecke zurück,
     *  wenn es für die strecke keinen wert gibt, dann wir null zurück gegeben
     * @param @Nullable strecken_name
     * @return
     */
    @Nullable
    public Double get_bestzeit_von_strecke(String strecken_name){
        return strecken_bestzeiten.get(strecken_name);
    }
}
