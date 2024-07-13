package de.christian_koehler_iu.rennspiel.datasets;

public class Punkt{
    private int x;
    private int y;

    public Punkt(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
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
}
