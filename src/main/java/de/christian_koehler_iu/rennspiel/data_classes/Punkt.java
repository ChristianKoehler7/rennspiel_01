package de.christian_koehler_iu.rennspiel.data_classes;

/**
 * die klasse Punkt repräsentiert einen punkt im 2d koordinatensystem
 *  zwei punkte werden verwendet um eine linie zu definieren
 */
public class Punkt{
    private double x;
    private double y;

    public Punkt(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    @Override
    public boolean equals(Object obj) {
        boolean is_equal = false;
        if(obj instanceof Punkt){
            if( ((Punkt)obj).getX() == this.getX() && ((Punkt)obj).getY() == this.getY()){
                is_equal = true;
            }
        }
        return is_equal;
    }

    public boolean is_jede_koordinate_ganzzahl(){
        return ((x % 1)==0) && ((y % 1)==0);
    }
}
