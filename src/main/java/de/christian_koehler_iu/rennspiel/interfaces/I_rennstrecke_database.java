package de.christian_koehler_iu.rennspiel.interfaces;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;

import java.util.ArrayList;

/**
 * schnittstelle, die methoden für die datenbank zugriffe definiert, was die rennstrecke angeht
 */
public interface I_rennstrecke_database {
    void save_rennstrecke_complete(Rennstrecke rennstrecke);
    Rennstrecke load_rennstrecke_complete(String strecken_name);
    ArrayList<String> load_rennstrecke_namen();
    ArrayList<String> load_rennstrecke_standart_namen();
    ArrayList<String> load_rennstrecke_custom_namen();
    void delete_rennstrecke_complete(String strecken_name);
    //void update_bestzeit(String strecken_name, double neue_bestzeit);
    Double get_strecken_bestzeit(String strecken_name);
    String get_strecke_bester_spieler(String strecken_name);
}
