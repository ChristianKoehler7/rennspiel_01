package de.christian_koehler_iu.rennspiel.datasets;

public class Linie {
    private Punkt p1;
    private Punkt p2;

    public Linie(Punkt p1, Punkt p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

    public Linie(int x0, int y0, int x1, int y1){
        this.p1 = new Punkt(x0,y0);
        this.p2 = new Punkt(x1, y1);
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

    @Override
    public String toString() {
        return "Linie x0=" +getP1().getX() + " y0=" + getP1().getY() + " x1=" + getP2().getX() + " y1=" + getP2().getY();
    }

    @Override
    public boolean equals(Object obj) {
        // es wird geprüft, ob die eingabeLinie die gleichen punkte hat, wobei die punkte auch vertauscht sein dürfen
        boolean is_equal = false;
        if(obj instanceof Linie){
            if( ((Linie)obj).getP1().equals(this.p1) &&  ((Linie)obj).getP2().equals(this.p2)
                || ((Linie)obj).getP1().equals(this.p2) &&  ((Linie)obj).getP2().equals(this.p1)){
                is_equal = true;
            }
        }
        return is_equal;
    }
}
