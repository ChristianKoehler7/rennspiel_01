package de.rennspiel_01.rennspiel_01.datasets;

public class Linie {
    private Punkt p1;
    private Punkt p2;

    public Linie(Punkt p1, Punkt p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Linie(int x1, int y1, int x2, int y2){
        this.p1 = new Punkt(x1,y1);
        this.p2 = new Punkt(x2, y2);
    }

    public Punkt getP1() {
        return p1;
    }

    public void setP1(Punkt p1) {
        this.p1 = p1;
    }

    public Punkt getP2() {
        return p2;
    }

    public void setP2(Punkt p2) {
        this.p2 = p2;
    }
}
