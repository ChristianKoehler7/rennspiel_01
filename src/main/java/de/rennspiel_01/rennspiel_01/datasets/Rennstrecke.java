package de.rennspiel_01.rennspiel_01.datasets;

import java.util.ArrayList;

public class Rennstrecke {
    private final String name;
    private final int breite;
    private final int hoehe;
    private ArrayList<Linie> streckenlinien;
    private Linie startlinie;
    private Richtung startrichtung;
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

    public void setStreckenlinien(ArrayList<Linie> streckenlinien) {
        this.streckenlinien = streckenlinien;
    }

    public Linie getStartlinie() {
        return startlinie;
    }

    public void setStartlinie(Linie startlinie) {
        this.startlinie = startlinie;
    }

    public Richtung getStartrichtung() {
        return startrichtung;
    }

    public void setStartrichtung(Richtung startrichtung) {
        this.startrichtung = startrichtung;
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
        // streckenlinien erstellen
        ArrayList<Linie> arrLinien = new ArrayList<Linie>();
        // äußerer rahmen
        arrLinien.add(new Linie(0, 0, 26, 0));
        arrLinien.add(new Linie(0, 0, 0, 20));
        arrLinien.add(new Linie(0, 20, 26, 20));
        arrLinien.add(new Linie(26, 0, 26, 20));

        // waagrechte linien
        arrLinien.add(new Linie(3, 3, 22, 3));
        arrLinien.add(new Linie(0, 10, 5, 10));
        arrLinien.add(new Linie(10, 13, 26, 13));
        arrLinien.add(new Linie(3, 17, 23, 17));

        // senkrechte linien
        arrLinien.add(new Linie(7, 3, 7, 17));
        arrLinien.add(new Linie(10, 5, 10, 13));
        arrLinien.add(new Linie(14, 3, 14, 9));
        arrLinien.add(new Linie(18, 6, 18, 13));
        arrLinien.add(new Linie(22, 3, 22, 7));

        // linien in rennstrecke speichern
        rennstrecke_01.setStreckenlinien(arrLinien);



        return rennstrecke_01;
    }
}


