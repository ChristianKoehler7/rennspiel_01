package de.christian_koehler_iu.rennspiel.data_classes;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

/**
 * die klasse Rennstrecke repräsentiert einen strecke im spiel
 *  sowohl die standart als auch die custom strecken
 *  gespeichert werden alle nötigen daten um eine rennstrecke zu definieren
 */
public class Rennstrecke {
    private final String name;
    private boolean is_standartstrecke = false;
    private final int breite;
    private final int hoehe;
    private final ArrayList<Linie> streckenlinien = new ArrayList<>();
    private Linie startlinie = null;
    private boolean is_startrichtung_nach_unten_oder_rechts = true;
    private int anz_runden;
    //private double strecken_bestzeit = -1; // -1 bedeutet, dass es noch keine bestzeit gibt

    public Rennstrecke(String streckenname, int streckenbreite, int streckenhoehe) {
        this.name = streckenname;
        this.breite = streckenbreite;
        this.hoehe = streckenhoehe;
    }

    public Rennstrecke(String streckenname,
                       boolean is_standartstrecke,
                       int streckenbreite,
                       int streckenhoehe,
                       Linie startlinie,
                       ArrayList<Linie> streckenlinien,
                       boolean is_startrichtung_nach_unten_oder_rechts,
                       int anz_runden) {
        this.name = streckenname;
        this.is_standartstrecke = is_standartstrecke;
        this.breite = streckenbreite;
        this.hoehe = streckenhoehe;
        this.startlinie = startlinie;
        this.set_streckenlinien(streckenlinien);
        this.is_startrichtung_nach_unten_oder_rechts = is_startrichtung_nach_unten_oder_rechts;
        this.anz_runden = anz_runden;
    }

    public String getName() {
        return name;
    }

    public boolean get_is_standartstrecke() {
        return is_standartstrecke;
    }

    public void set_is_standartstrecke(boolean is_standartstrecke) {
        this.is_standartstrecke = is_standartstrecke;
    }

    public int getBreite() {
        return breite;
    }


    public int getHoehe() {
        return hoehe;
    }

    public ArrayList<Linie> getStreckenlinien() {
        return streckenlinien;
    }

    public void set_streckenlinien(ArrayList<Linie> streckenlininen){
        // streckenlinien-arraylist leeren
        this.streckenlinien.clear();
        // streckenlinien aus einagbe in attribut streckenlinien einfügen
        for(Linie streckenlinie : streckenlininen){
            // test ob streckenlinie nur ganzzahl koordinaten hat
            if(streckenlinie.is_jede_koordinate_ganzzahl()){
                this.streckenlinien.add(streckenlinie);
            }else{
                // mindestens eine koordinate ist keine ganzzahl -> fehler ausgeben
                throw new RuntimeException("Mindestens eine Streckenlinien-Koordinate ist keine Ganzzahl !");
            }

        }
    }

    public void addStreckenlinie(Linie linie) {
        this.streckenlinien.add(linie);
    }

    public void addStreckenlinie(int x0, int y0, int x1, int y1) {
        Linie linie = new Linie(x0, y0, x1, y1);
        this.streckenlinien.add(linie);
    }

    public void removeStreckenlinie(Linie linie) {
        this.streckenlinien.remove(linie);
    }

    public void removeLastStreckenlinie() {
        this.streckenlinien.remove(this.streckenlinien.size()-1);
    }

    @Nullable
    public Linie getStartlinie() {
        return startlinie;
    }

    public void setStartlinie(Linie startlinie) {
        // startlinien koordinaten müssen alle ganzzahlen sein
        if(startlinie.is_jede_koordinate_ganzzahl()){
            this.startlinie = startlinie;
        }else{
            // mindestens eine koordinate ist keine ganzzahl -> fehler ausgeben
            throw new RuntimeException("Mindestens eine Startlinien-Koordinate ist keine Ganzzahl !");
        }
    }

    public void setStartlinie(int x0, int y0, int x1, int y1) {
        this.startlinie = new Linie(x0, y0, x1, y1);
    }

    public boolean get_is_startrichtung_nach_unten_oder_rechts() {
        return is_startrichtung_nach_unten_oder_rechts;
    }

    public void set_is_startrichtung_nach_unten_oder_rechts(boolean is_startrichtung_nach_unten_oder_rechts) {
        this.is_startrichtung_nach_unten_oder_rechts = is_startrichtung_nach_unten_oder_rechts;
    }

    public int get_anz_runden() {
        return anz_runden;
    }

    public void set_anz_runden(int anz_runden) {
        this.anz_runden = anz_runden;
    }

//    public double getStrecken_bestzeit() {
//        return strecken_bestzeit;
//    }

//    public void setStrecken_bestzeit(double strecken_bestzeit) {
//        this.strecken_bestzeit = strecken_bestzeit;
//    }

    public int get_max_anz_spielern(){
        // max spieler die auf startlinie passen zurück geben, aber maximal 4
        double max_anz_spieler_double = (startlinie.get_max_breite_oder_hoehe()-2)<=4 ? (startlinie.get_max_breite_oder_hoehe()-2) : 4;
        if( (max_anz_spieler_double%1)==0 ){
            // max_anz_spieler_double ist eine ganzzahl
            return (int) max_anz_spieler_double;
        }else{
            throw new RuntimeException("Mindestens eine Koordinate der Startlinie ist keine Ganzzahl!");
        }
    }

    public static Rennstrecke generate_strecke_01(){
        Rennstrecke rennstrecke_01 = new Rennstrecke("Rennstrecke 01", 26, 20);
        rennstrecke_01.set_anz_runden(1);

        // äußerer rahmen
        rennstrecke_01.addStreckenlinie(0, 0, 26, 0);
        rennstrecke_01.addStreckenlinie(0, 0, 0, 20);
        rennstrecke_01.addStreckenlinie(0, 20, 26, 20);
        rennstrecke_01.addStreckenlinie(26, 0, 26, 20);

        // waagrechte linien
        rennstrecke_01.addStreckenlinie(3, 3, 22, 3);
        rennstrecke_01.addStreckenlinie(0, 10, 5, 10);
        rennstrecke_01.addStreckenlinie(10, 13, 26, 13);
        rennstrecke_01.addStreckenlinie(3, 17, 23, 17);

        // senkrechte linien
        rennstrecke_01.addStreckenlinie(7, 3, 7, 17);
        rennstrecke_01.addStreckenlinie(10, 5, 10, 13);
        rennstrecke_01.addStreckenlinie(14, 3, 14, 9);
        rennstrecke_01.addStreckenlinie(18, 6, 18, 13);
        rennstrecke_01.addStreckenlinie(22, 3, 22, 7);

        return rennstrecke_01;
    }
}


