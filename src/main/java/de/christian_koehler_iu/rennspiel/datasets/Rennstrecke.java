package de.christian_koehler_iu.rennspiel.datasets;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class Rennstrecke {
    private final String name;
    private final int breite;
    private final int hoehe;
    private final ArrayList<Linie> streckenlinien = new ArrayList<>();
    private Linie startlinie = null;
    private boolean is_startrichtung_nach_unten_oder_rechts = true;
    private int anz_runden;
    private float strecken_bestzeit;

    public Rennstrecke(String streckenname, int streckenbreite, int streckenhoehe) {
        this.name = streckenname;
        this.breite = streckenbreite;
        this.hoehe = streckenhoehe;
    }

    public String getName() {
        return name;
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
        this.streckenlinien.removeLast();    }

    @Nullable
    public Linie getStartlinie() {
        return startlinie;
    }

    public void setStartlinie(Linie startlinie) {
        this.startlinie = startlinie;
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

    public int getAnz_runden() {
        return anz_runden;
    }

    public void setAnz_runden(int anz_runden) {
        this.anz_runden = anz_runden;
    }

    public float getStrecken_bestzeit() {
        return strecken_bestzeit;
    }

    public void setStrecken_bestzeit(float strecken_bestzeit) {
        this.strecken_bestzeit = strecken_bestzeit;
    }

    public static Rennstrecke generate_strecke_01(){
        Rennstrecke rennstrecke_01 = new Rennstrecke("Rennstrecke 01", 26, 20);
        rennstrecke_01.setAnz_runden(1);

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


