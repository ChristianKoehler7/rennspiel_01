package de.christian_koehler_iu.rennspiel.datasets;

public class Richtung {
    private int x; // -1, 0, 1
    private int y; // -1, 0, 1

    public Richtung(int x, int y) {
        this.setX(x);
        this.setY(y);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        if(x==-1 || x==0 || x==1){
            this.x = x;
        }else {
            // TODO throw exeption
        }
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        if(y==-1 || y==0 || y==1){
            this.y = y;
        }else {
            // TODO throw exeption
        }
    }
}
