package de.christian_koehler_iu.rennspiel.utility;

import org.jetbrains.annotations.Nullable;

public class Umrechnung_grid_pixel {

    private final double B_RAND_GRID = 1.5; // breite des randes (in kästchen) im group
    private final double H_RAND_GRID = 1.5; // breite des randes (in kästchen) im group
    public final double CLICK_TOLERANZ_GRID = 0.4; // abweichung click (in kästchen) zu kästchenkreuz, sodass der click noch zählt

    private final double B_MAX_PIXEL; // breite des elements in dem group steckt (in pixel)
    private final double H_MAX_PIXEL; // höhe des elements in dem group steckt
    private final double B_STRECKE_PIXEL_MIT_RAND; // breite der strecke (in pixel) mit rand
    private final double H_STRECKE_PIXEL_MIT_RAND;  // höhe der strecke (in pixel) mit rand

    private final int B_STRECKE_GRID_OHNE_RAND; // breite der strecke ohne rand (in kästchen)
    private final int H_STRECKE_GRID_OHNE_RAND; // höhe der strecke ohne rand (in kästchen)
    private final double B_STRECKE_GRID_MIT_RAND; // breite der strecke mit rand (in kästchen)
    private final double H_STRECKE_GRID_MIT_RAND; // höhe der strecke mit rand (in kästchen)

    public final double B_1GRID_IN_PIXEL; // breite eines kästchens (in pixel)
    public final double H_1GRID_IN_PIXEL; // höhe eines kästchens (in pixel)


    public Umrechnung_grid_pixel(double b_max_pixel, double h_max_pixel, int b_strecke_grid_ohne_rand, int h_strecke_grid_ohne_rand) {
        B_MAX_PIXEL = b_max_pixel;
        H_MAX_PIXEL = h_max_pixel;
        B_STRECKE_GRID_OHNE_RAND = b_strecke_grid_ohne_rand;
        H_STRECKE_GRID_OHNE_RAND = h_strecke_grid_ohne_rand;
        B_STRECKE_GRID_MIT_RAND = b_strecke_grid_ohne_rand + 2 * B_RAND_GRID;
        H_STRECKE_GRID_MIT_RAND = h_strecke_grid_ohne_rand + 2 * H_RAND_GRID;
        B_STRECKE_PIXEL_MIT_RAND = this.init_calc_b_strecke_pixel_mit_rand(b_max_pixel, h_max_pixel, B_STRECKE_GRID_MIT_RAND, H_STRECKE_GRID_MIT_RAND);
        H_STRECKE_PIXEL_MIT_RAND = this.init_calc_h_strecke_pixel_mit_rand(b_max_pixel, h_max_pixel, B_STRECKE_GRID_MIT_RAND, H_STRECKE_GRID_MIT_RAND);
        B_1GRID_IN_PIXEL = B_STRECKE_PIXEL_MIT_RAND / B_STRECKE_GRID_MIT_RAND;
        H_1GRID_IN_PIXEL = H_STRECKE_PIXEL_MIT_RAND / H_STRECKE_GRID_MIT_RAND;

    }

    private double init_calc_b_strecke_pixel_mit_rand(double b_max_pixel, double h_max_pixel, double b_strecke_grid_mit_rand, double h_strecke_grid_mit_rand){
        double b_strecke_pixel_mit_rand;
        if(b_max_pixel/h_max_pixel > b_strecke_grid_mit_rand/h_strecke_grid_mit_rand){
            b_strecke_pixel_mit_rand = h_max_pixel * b_strecke_grid_mit_rand / h_strecke_grid_mit_rand;
        }else{
            b_strecke_pixel_mit_rand = b_max_pixel;
        }
        return b_strecke_pixel_mit_rand;
    }

    private double init_calc_h_strecke_pixel_mit_rand(double b_max_pixel, double h_max_pixel, double b_strecke_grid_mit_rand, double h_strecke_grid_mit_rand){
        double h_strecke_pixel_mit_rand;
        if(b_max_pixel/h_max_pixel < b_strecke_grid_mit_rand/h_strecke_grid_mit_rand){
            h_strecke_pixel_mit_rand = b_max_pixel * h_strecke_grid_mit_rand / b_strecke_grid_mit_rand;
        }else{
            h_strecke_pixel_mit_rand = h_max_pixel;
        }
        return h_strecke_pixel_mit_rand;
    }

//    public double posXGrid_to_posXPixel(int posXGrid){
//        return this.B_1GRID_IN_PIXEL * (this.B_RAND_GRID + (double) posXGrid);
//    }

    public double posXGrid_to_posXPixel(double posXGrid){
        return this.B_1GRID_IN_PIXEL * (this.B_RAND_GRID + posXGrid);
    }

//    public double posYGrid_to_posYPixel(int posYGrid){
//        return this.H_1GRID_IN_PIXEL * (this.H_RAND_GRID + (double) posYGrid);
//    }

    public double posYGrid_to_posYPixel(double posYGrid){
        return this.H_1GRID_IN_PIXEL * (this.H_RAND_GRID + posYGrid);
    }

    private double posXPixel_to_posXGrid(double posXPixel){
        return (posXPixel / this.B_1GRID_IN_PIXEL) - this.B_RAND_GRID;
    }

    private double posYPixel_to_posYGrid(double posYPixel){
        return (posYPixel / this.H_1GRID_IN_PIXEL) - this.H_RAND_GRID;
    }

    /*
    gibt die x-grid position in abhängigkeit der x-pixel position aus
    wenn die x-pixel position die clicktoleranz nicht einhält wir null ausgegeben
     */
    @Nullable
    public Integer posXPixel_to_posXGrid_round(double posXPixel){
        double posXGrid = this.posXPixel_to_posXGrid(posXPixel);
        // testen ob es eine gridposition gibt, die mit der clicktoleranz übereinstimmt
        Integer posGridX_Integer;
        if(posXGrid < -this.CLICK_TOLERANZ_GRID){
            posGridX_Integer = null;
        }
        else if(posXGrid >= -this.CLICK_TOLERANZ_GRID && posXGrid <= this.CLICK_TOLERANZ_GRID) {
            posGridX_Integer = 0;
        }else if(posXGrid > this.B_STRECKE_GRID_OHNE_RAND+this.CLICK_TOLERANZ_GRID){
            // posXGrid geht über den rechten rand der strecke hinaus
            posGridX_Integer = null;
        }else if(posXGrid - (double)((int)posXGrid)/*vorkommastellen abgeschnitten*/ <= this.CLICK_TOLERANZ_GRID){
            posGridX_Integer = (int)posXGrid;
        }else if(posXGrid - (double)((int)posXGrid)/*vorkommastellen abgeschnitten*/ >= 1-this.CLICK_TOLERANZ_GRID) {
            posGridX_Integer = ((int) posXGrid) + 1;
        }else {
            posGridX_Integer = null;
        }

        return posGridX_Integer;
    }

    /*
    gibt die y-grid position in abhängigkeit der y-pixel position aus
    wenn die y-pixel position die clicktoleranz nicht einhält wir null ausgegeben
     */
    @Nullable
    public Integer posYPixel_to_posYGrid_round(double posYPixel){
        double posYGrid = this.posYPixel_to_posYGrid(posYPixel);
        // testen ob es eine gridposition gibt, die mit der clicktoleranz übereinstimmt
        Integer posGridY_Integer;
        if(posYGrid < -this.CLICK_TOLERANZ_GRID){
            posGridY_Integer = null;
        }
        else if(posYGrid >= -this.CLICK_TOLERANZ_GRID && posYGrid <= this.CLICK_TOLERANZ_GRID) {
            posGridY_Integer = 0;
        }else if(posYGrid > this.H_STRECKE_GRID_OHNE_RAND+this.CLICK_TOLERANZ_GRID){
            // posYGrid geht über den rechten rand der strecke hinaus
            posGridY_Integer = null;
        }else if(posYGrid - (double)((int)posYGrid)/*vorkommastellen abgeschnitten*/ <= this.CLICK_TOLERANZ_GRID){
            posGridY_Integer = (int)posYGrid;
        }else if(posYGrid - (double)((int)posYGrid)/*vorkommastellen abgeschnitten*/ >= 1-this.CLICK_TOLERANZ_GRID) {
            posGridY_Integer = ((int) posYGrid) + 1;
        }else {
            posGridY_Integer = null;
        }

        return posGridY_Integer;
    }

    public double get_b_strecke_pixel_mit_rand() {
        return B_STRECKE_PIXEL_MIT_RAND;
    }

    public double get_h_strecke_pixel_mit_rand() {
        return H_STRECKE_PIXEL_MIT_RAND;
    }
}
