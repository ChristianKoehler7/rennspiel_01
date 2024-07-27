package de.christian_koehler_iu.rennspiel.interfaces;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;

import java.util.ArrayList;

public interface I_spieler_database {
    void save_spieler(Spieler spieler);
    Spieler load_spieler(String spieler_name);
    ArrayList<String> load_spieler_namen();
}
