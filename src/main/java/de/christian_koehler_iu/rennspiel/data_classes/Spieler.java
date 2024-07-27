package de.christian_koehler_iu.rennspiel.data_classes;

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

    public void set_name(String name) {
        this.name = name;
    }

    public HashMap<String, Double> get_strecken_bestzeiten() {
        return strecken_bestzeiten;
    }

    public void set_strecken_bestzeiten(HashMap<String, Double> strecken_bestzeiten) {
        this.strecken_bestzeiten.clear();
        this.strecken_bestzeiten.putAll(strecken_bestzeiten);
    }
}
