package de.christian_koehler_iu.rennspiel.datasets;

public class Linie{
    private Punkt p0;
    private Punkt p1;

    public Linie(Punkt p0, Punkt p1) {
        this.p0 = p0;
        this.p1 = p1;
    }

    public Linie(int x0, int y0, int x1, int y1){
        this.p0 = new Punkt(x0,y0);
        this.p1 = new Punkt(x1, y1);
    }

    public Punkt getP0() {
        return p0;
    }

    public void setP0(Punkt p0) {
        this.p0 = p0;
    }

    public Punkt getP1() {
        return p1;
    }

    public void setP1(Punkt p1) {
        this.p1 = p1;
    }

    public boolean is_horizontal(){
        boolean is_horizontal = false;
        if(this.p0.getY() == this.p1.getY()){
            is_horizontal = true;
        }
        return is_horizontal;
    }
    public boolean is_vertikal(){
        boolean is_vertikal = false;
        if(this.p0.getX() == this.p1.getX()){
            is_vertikal = true;
        }
        return is_vertikal;
    }

    public boolean hat_schnittpunkt_mit_linie(Linie otherLinie){
        Punkt A = this.getP0();
        Punkt B = this.getP1();
        Punkt C = otherLinie.getP0();
        Punkt D = otherLinie.getP1();

        // Berechnung der Orientierungspunkte
        double o1 = schnittpunktberechnung_orientierung(A, B, C);
        double o2 = schnittpunktberechnung_orientierung(A, B, D);
        double o3 = schnittpunktberechnung_orientierung(C, D, A);
        double o4 = schnittpunktberechnung_orientierung(C, D, B);

        // Allgemeiner Fall
        if (o1 != o2 && o3 != o4) {
            return true;
        }

        // Spezielle Fälle
        // A, B und C sind kollinear und C liegt auf AB
        if (o1 == 0 && schnittpunktberechnung_liegtAufLinie(A, B, C)) return true;

        // A, B und D sind kollinear und D liegt auf AB
        if (o2 == 0 && schnittpunktberechnung_liegtAufLinie(A, B, D)) return true;

        // C, D und A sind kollinear und A liegt auf CD
        if (o3 == 0 && schnittpunktberechnung_liegtAufLinie(C, D, A)) return true;

        // C, D und B sind kollinear und B liegt auf CD
        if (o4 == 0 && schnittpunktberechnung_liegtAufLinie(C, D, B)) return true;

        return false; // Keine der Bedingungen ist erfüllt
    }

    // Funktion zur Berechnung der Orientierung
    private double schnittpunktberechnung_orientierung(Punkt A, Punkt B, Punkt C) {
        double val = (B.getY() - A.getY()) * (C.getX() - B.getX()) - (B.getX() - A.getX()) * (C.getY() - B.getY());
        if (val == 0) return 0;  // kollinear
        return (val > 0) ? 1 : 2; // 1: im Uhrzeigersinn, 2: gegen den Uhrzeigersinn
    }

    // Funktion zur Berechnung der Orientierung
    private double schnittpunktberechnung_orientierung(double a_x, double a_y,
                                                       double b_x, double b_y,
                                                       double c_x, double c_y) {
        double val = (b_y - a_y) * (c_x - b_x) - (b_x - a_x) * (c_y - b_y);
        if (val == 0) return 0;  // kollinear
        return (val > 0) ? 1 : 2; // 1: im Uhrzeigersinn, 2: gegen den Uhrzeigersinn
    }

    // Funktion zur Überprüfung, ob Punkt C auf Linie AB liegt
    private boolean schnittpunktberechnung_liegtAufLinie(Punkt A, Punkt B, Punkt C) {
        return C.getX() <= Math.max(A.getX(), B.getX()) && C.getX() >= Math.min(A.getX(), B.getX()) &&
                C.getY() <= Math.max(A.getY(), B.getY()) && C.getY() >= Math.min(A.getY(), B.getY());
    }

    // Funktion zur Überprüfung, ob Punkt C auf Linie AB liegt
    private boolean schnittpunktberechnung_liegtAufLinie(double a_x, double a_y,
                                                         double b_x, double b_y,
                                                         double c_x, double c_y) {
        return c_x <= Math.max(a_x, b_x) && c_x >= Math.min(a_x, b_x) &&
                c_y <= Math.max(a_y, b_y) && c_y >= Math.min(a_y, b_y);
    }

    // Funktion zur Überprüfung, ob ein Punkt auf einer Linie liegt
    public boolean liegt_punkt_auf_linie(Punkt punkt) {
        return schnittpunktberechnung_orientierung(this.getP0(), this.getP1(), punkt) == 0
                && schnittpunktberechnung_liegtAufLinie(this.getP0(), this.getP1(), punkt);
    }

    // Funktion zur Überprüfung, ob ein Punkt auf einer Linie liegt
    public boolean liegt_punkt_auf_linie(double x_punkt, double y_punkt){
        return schnittpunktberechnung_orientierung(this.getP0().getX(), this.getP0().getY(),
                this.getP1().getX(), this.getP1().getY(),
                x_punkt, y_punkt) == 0
                && schnittpunktberechnung_liegtAufLinie(this.getP0().getX(), this.getP0().getY(),
                this.getP1().getX(), this.getP1().getY(),
                x_punkt, y_punkt);
    }

    @Override
    public String toString() {
        return "Linie x0=" + getP0().getX() + " y0=" + getP0().getY() + " x1=" + getP1().getX() + " y1=" + getP1().getY();
    }

    @Override
    public boolean equals(Object obj) {
        // es wird geprüft, ob die eingabeLinie die gleichen punkte hat, wobei die punkte auch vertauscht sein dürfen
        boolean is_equal = false;
        if(obj instanceof Linie){
            if( ((Linie)obj).getP0().equals(this.p0) &&  ((Linie)obj).getP1().equals(this.p1)
                || ((Linie)obj).getP0().equals(this.p1) &&  ((Linie)obj).getP1().equals(this.p0)){
                is_equal = true;
            }
        }
        return is_equal;
    }


}
