package de.christian_koehler_iu.rennspiel.interfaces;

import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;

import java.util.ArrayList;

public interface I_rennstrecke_database {
    void save_rennstrecke_complete(Rennstrecke rennstrecke);
    Rennstrecke load_rennstrecke_complete(String strecken_name);
    ArrayList<String> load_rennstrecke_namen();
    ArrayList<String> load_rennstrecke_standart_namen();
    ArrayList<String> load_rennstrecke_custom_namen();
    void delete_rennstrecke_complete(String strecken_name);
}
