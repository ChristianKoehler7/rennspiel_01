package de.christian_koehler_iu.rennspiel.interfaces;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;

import java.util.ArrayList;

public interface I_spieler_database {
    void save_new_spieler(String spieler_name);
    Spieler load_spieler_complete(String spieler_name);
    ArrayList<String> load_spieler_namen();
    boolean is_spieler_name_vorhanden(String spieler_name);
    void delete_spieler_complete(String spieler_name);
    void save_spieler_rennstrecke_bestzeit(String spieler_name, String strecken_name, Double bestzeit);
}
