package de.rennspiel_01.rennspiel_01.controller;

import de.rennspiel_01.rennspiel_01.datasets.Linie;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.GridPane;
import de.rennspiel_01.rennspiel_01.datasets.Rennstrecke;

public class StreckeErstellenController {


    public static final String PATH_TO_FXML = "/de/rennspiel_01/rennspiel_01/strecke_erstellen_view.fxml";
    public static final String SCENE_NAME = "StreckeErstellen";

    public final double RAND_X_GRID = 1.0; // anzahl kästchen rand links und rechts
    public final double RAND_Y_GRID = 1.0; // anzahl kästchen rand oben und unten

    public final double CLICK_TOLERANZ_GRID = 0.25; // abweichung click zu kästchenkreuz, sodass der click noch zählt

    private Scene lastScene;
    private Rennstrecke rennstrecke;

    private final int MAX_BREITE_GROUP = 896;
    private final int MAX_HOEHE_GROUP = 560;


//    private Line aktMouseLine;
//    private int posPressdGridX_round = -1;
//    private int posPressdGridY_round = -1;
//    private int posReleasedGridX_round = -1;
//    private int posReleasedGridY_round = -1;
//
//    private final ArrayList<Line> gitterLininen = new ArrayList<>();
//    private final ArrayList<Line> streckenlinien = new ArrayList<>();
//    private final ArrayList<Line> fahrLinien = new ArrayList<>();


    @FXML
    private GridPane streckeErstellen_grid_root;
    @FXML
    private GridPane streckeErstellen_grid_rechts;
    @FXML
    private Group streckeErstellen_group_strecke;
    @FXML
    private ListView<Linie> streckeErstellen_listview_streckenlinien;
    @FXML
    private Label streckeErstellen_lb_streckengroesse;

    @FXML
    public void initialize() {
        System.out.println("#############################");
        System.out.println("initialize()");
        System.out.println("this.streckeErstellen_group_strecke.getLayoutBounds().getWidth()) = " + this.streckeErstellen_group_strecke.getLayoutBounds().getWidth());
    }

    
    
    public void initialize_rennstrecke(Rennstrecke rennstrecke){
        // rennstrecke in attribut speichern
        this.rennstrecke = rennstrecke;

        // labeltext setzen
        this.streckeErstellen_lb_streckengroesse.setText("Streckenname: " + rennstrecke.getName() + "\nBreite: " + rennstrecke.getBreite() + "\nHöhe: " + rennstrecke.getHoehe());

        // gitter zeichnen
        System.out.println("this.streckeErstellen_group_strecke.getLayoutBounds().getWidth()) = " + this.streckeErstellen_group_strecke.getLayoutBounds().getWidth());
        System.out.println("this.streckeErstellen_group_strecke.getLayoutBounds() = " +this.streckeErstellen_group_strecke.getLayoutBounds());
        System.out.println("this.streckeErstellen_group_strecke.getBoundsInParent() = " +this.streckeErstellen_group_strecke.getBoundsInParent());
        System.out.println("this.streckeErstellen_group_strecke.getParent().getLayoutX() = " +this.streckeErstellen_group_strecke.getParent().getLayoutX());
        System.out.println("this.streckeErstellen_group_strecke.getLayoutBounds().getWidth()"+this.streckeErstellen_group_strecke.getLayoutBounds().getWidth());

        // Erzwingt das Layout nach dem Initialisieren der Szene
        Platform.runLater(() -> {
            // layout() erzwingen, um die Größen zu berechnen
            streckeErstellen_grid_root.layout();

            // Jetzt können wir die Größen der Zelle abfragen
            double width = streckeErstellen_group_strecke.getLayoutBounds().getWidth();
            double height = streckeErstellen_group_strecke.getLayoutBounds().getHeight();

            System.out.println("Width: " + width + ", Height: " + height);
        });
    }

    public void streckeErstellen_bn_zurueck(ActionEvent actionEvent) {
        ScenesManager.getInstance().goBack();
    }


//    private double posGridX_to_posCanvasX(int posGridX){
//        double pixel_breite = this.streckeErstellen_group_strecke.getParent().;
//        int strecken_breite = this.rennstrecke.getBreite();
//
//        return this.RAND_X + posGridX / ((double)strecken_breite) * (pixel_breite - 2*this.RAND_X);
//    }
//
//    private double posGridY_to_posCanvasY(int posGridY){
//        double pixel_hoehe = this.nopixel_pane_strecke.getHeight();
//        int strecken_hoehe = this.rennstrecke.getHoehe();
//
//        return this.RAND_Y + posGridY / ((double)strecken_hoehe) * (pixel_hoehe - 2*this.RAND_Y);
//    }
//
//    private double posPixelX_to_posGridX(double posPixelX){
//        double pixel_breite = this.nopixel_pane_strecke.getWidth();
//        int grid_breite = this.rennstrecke.getBreite();
//
//        return (posPixelX - this.RAND_X) * grid_breite / (pixel_breite - 2*this.RAND_X);
//    }
//
//    private double posPixelY_to_posGridY(double posPixelY){
//        double pixel_hoehe = this.nopixel_pane_strecke.getHeight();
//        int grid_hoehe = this.rennstrecke.getHoehe();
//
//        return (posPixelY - this.RAND_Y) * grid_hoehe / (pixel_hoehe - 2*this.RAND_Y);
//    }
//
//    private int posPixelX_to_posGridX_round(double posPixelX){
//        double posGridX_double = this.posPixelX_to_posGridX(posPixelX);
//
//        int posGridX_int;
//        if(posGridX_double <= 0.2 && posGridX_double >= -0.2){
//            posGridX_int = 0;
//        }else if(posGridX_double-(double) ((int)posGridX_double) <= 0.2){
//            posGridX_int = (int)posGridX_double;
//        }else if(posGridX_double-(double) ((int)posGridX_double) >= 0.8) {
//            posGridX_int = ((int) posGridX_double) + 1;
//        }else {
//            posGridX_int = -1;
//        }
//
//        return posGridX_int;
//    }
//
//    private int posPixelY_to_posGridY_round(double posPixelY){
//        double pixel_breite = this.nopixel_pane_strecke.getWidth();
//        int grid_breite = this.rennstrecke.getBreite();
//
//        double posGridY_double = this.posPixelY_to_posGridY(posPixelY);
//
//        int posGridY_int;
//        if(posGridY_double <= 0.2 && posGridY_double >= -0.2){
//            posGridY_int = 0;
//        }else if(posGridY_double-(double) ((int)posGridY_double) <= 0.2){
//            posGridY_int = (int)posGridY_double;
//        }else if(posGridY_double-(double) ((int)posGridY_double) >= 0.8) {
//            posGridY_int = ((int) posGridY_double) + 1;
//        }else {
//            posGridY_int = -1;
//        }
//
//        return posGridY_int;
//    }


}
/**
    public void nocanvas_bn_delLastLine_action(ActionEvent actionEvent) {
        this.nocanvas_pane_strecke.getChildren().removeLast();
        this.nocanvas_pane_strecke.getChildren().removeLast();
        this.nocanvas_pane_strecke.getChildren().removeLast();
    }

    public void nocanvas_canv_rennstreck_mousePressed(MouseEvent mouseEvent) {
        this.nocanvas_lb_clickPosCanvasX.setText(Double.toString(mouseEvent.getX()));
        this.nocanvas_lb_clickPosCanvasY.setText(Double.toString(mouseEvent.getY()));
        this.posPressdGridX_round = this.posCanvasX_to_posGridX_round(mouseEvent.getX());
        this.posPressdGridY_round = this.posCanvasY_to_posGridY_round(mouseEvent.getY());
        this.nocanvas_lb_clickPosGridX.setText(Integer.toString( this.posPressdGridX_round ));
        this.nocanvas_lb_clickPosGridY.setText(Integer.toString( this.posPressdGridY_round ));
        if(this.posPressdGridX_round>=0
                && this.posPressdGridY_round>=0){
            this.aktMouseLine = new Line();
            this.aktMouseLine.setStroke(Color.BLUE);
            this.aktMouseLine.setStrokeWidth(2.0);
            this.aktMouseLine.setStartX(this.posGridX_to_posCanvasX(this.posPressdGridX_round));
            this.aktMouseLine.setStartY(this.posGridY_to_posCanvasY(this.posPressdGridY_round));
            this.aktMouseLine.setEndX(this.posGridX_to_posCanvasX(this.posPressdGridX_round));
            this.aktMouseLine.setEndY(this.posGridY_to_posCanvasY(this.posPressdGridY_round));
            this.nocanvas_pane_strecke.getChildren().add(this.aktMouseLine);
        }else {
            this.aktMouseLine = null;
        }
    }

    public void nocanvas_canv_rennstreck_mouseDragged(MouseEvent mouseEvent) {
        if(this.aktMouseLine != null){
            double endX = mouseEvent.getX();
            double endY = mouseEvent.getY();
            this.aktMouseLine.setEndX(endX);
            this.aktMouseLine.setEndY(endY);
        }
    }

    public void nocanvas_canv_rennstreck_mouseReleased(MouseEvent mouseEvent) {
        if(this.aktMouseLine != null) {
            double endXCanvas = mouseEvent.getX();
            double endYCanvas = mouseEvent.getY();

            int endXGrid = this.posCanvasX_to_posGridX_round(endXCanvas);
            int endYGrid = this.posCanvasY_to_posGridY_round(endYCanvas);

            if (endXGrid >= 0
                    && endYGrid >= 0
                    && !(endXGrid == this.posCanvasX_to_posGridX(this.aktMouseLine.getStartX())
                    && endYGrid == this.posCanvasY_to_posGridY(this.aktMouseLine.getStartY()))) {
                this.aktMouseLine.setEndX(this.posGridX_to_posCanvasX(endXGrid));
                this.aktMouseLine.setEndY(this.posGridY_to_posCanvasY(endYGrid));
                draw_points_to_Line(this.aktMouseLine);
                this.aktMouseLine = null;
            } else {
                this.nocanvas_pane_strecke.getChildren().remove(this.aktMouseLine);
                this.aktMouseLine = null;
            }
        }
    }

    private void draw_points_to_Line(Line line){
        Circle circleStart = new Circle();
        circleStart.setFill(Color.BLUE);
        circleStart.setRadius(5.0);
        circleStart.setCenterX(line.getStartX());
        circleStart.setCenterY(line.getStartY());

        Circle circleEnd = new Circle();
        circleEnd.setFill(Color.BLUE);
        circleEnd.setRadius(5.0);
        circleEnd.setCenterX(line.getEndX());
        circleEnd.setCenterY(line.getEndY());
        nocanvas_pane_strecke.getChildren().addAll(circleStart, circleEnd);
    }

//    private void drawMouseLine(){
//        if(    this.posPressdGridX_round >=0
//            && this.posPressdGridY_round >=0
//            && this.posReleasedGridX_round >=0
//            && this.posReleasedGridY_round >=0){
//            GraphicsContext gc = this.nocanvas_canv_rennstrecke.getGraphicsContext2D();
//            gc.setStroke(Color.BLUE);
//            gc.setLineWidth(2.0);
//            gc.strokeLine(this.posGridX_to_posCanvasX(this.posPressdGridX_round),
//                    this.posGridY_to_posCanvasY(this.posPressdGridY_round),
//                    this.posGridX_to_posCanvasX(this.posReleasedGridX_round),
//                    this.posGridY_to_posCanvasY(this.posReleasedGridY_round)
//            );
//            this.drawCircle(this.posGridX_to_posCanvasX(this.posPressdGridX_round),
//                    this.posGridY_to_posCanvasY(this.posPressdGridY_round),
//                    4.0
//            );
//            this.drawCircle(this.posGridX_to_posCanvasX(this.posReleasedGridX_round),
//                    this.posGridY_to_posCanvasY(this.posReleasedGridY_round),
//                    4.0
//            );
//        }
//    }



    public Rennstrecke getRennstrecke() {
        return rennstrecke;
    }

    private void initalisierung_is_fertig(){
        System.out.println("#############################");
        System.out.println("initalisierung_is_fertig()");
        double pane_breite = this.nocanvas_pane_strecke.getWidth();
        double pane_hoehe = this.nocanvas_pane_strecke.getHeight();
        System.out.println("pane_breite = " + pane_breite);
        System.out.println("pane_hoehe = " + pane_hoehe);


        // gitter zeichen
        int strecken_breite = this.rennstrecke.getBreite();
        int strecken_hoehe = this.rennstrecke.getHoehe();
        // vertikale gitterlinien zeichen
        for(int i=0 ; i<strecken_breite+1 ; i++){
            int x0 = i;
            int y0 = 0;
            int x1 = i;
            int y1 = strecken_hoehe;
            Line aktLine = new Line(this.posGridX_to_posCanvasX(x0),
                    this.posGridY_to_posCanvasY(y0),
                    this.posGridX_to_posCanvasX(x1),
                    this.posGridY_to_posCanvasY(y1));
            aktLine.setStroke(Color.LIGHTGRAY);
            aktLine.setStrokeWidth(0.5);
            this.gitterLininen.add(aktLine);
            this.nocanvas_pane_strecke.getChildren().add(aktLine);
        }
        // horizontale gitterlinien zeichen
        for(int i=0 ; i<strecken_hoehe+1 ; i++){
            int x0 = 0;
            int y0 = i;
            int x1 = strecken_breite;
            int y1 = i;
            Line aktLine = new Line(this.posGridX_to_posCanvasX(x0),
                    this.posGridY_to_posCanvasY(y0),
                    this.posGridX_to_posCanvasX(x1),
                    this.posGridY_to_posCanvasY(y1));
            aktLine.setStroke(Color.LIGHTGRAY);
            aktLine.setStrokeWidth(0.5);
            this.gitterLininen.add(aktLine);
            this.nocanvas_pane_strecke.getChildren().add(aktLine);
        }

        // rennstrecke zeichnen
        for(int i=0 ; i<rennstrecke.getStreckenlinien().size() ; i++){
            int x0 = rennstrecke.getStreckenlinien().get(i).getP1().getX();
            int y0 = rennstrecke.getStreckenlinien().get(i).getP1().getY();
            int x1 = rennstrecke.getStreckenlinien().get(i).getP2().getX();
            int y1 = rennstrecke.getStreckenlinien().get(i).getP2().getY();
            Line aktLine = new Line(this.posGridX_to_posCanvasX(x0),
                    this.posGridY_to_posCanvasY(y0),
                    this.posGridX_to_posCanvasX(x1),
                    this.posGridY_to_posCanvasY(y1));
            aktLine.setStroke(Color.BLACK);
            aktLine.setStrokeWidth(2.0);
            this.streckenlinien.add(aktLine);
            this.nocanvas_pane_strecke.getChildren().add(aktLine);
        }
    }

    public void initialize_rennstrecke(Rennstrecke rennstrecke) {
        this.rennstrecke = rennstrecke;

        final int[] integer = new int[1];
        integer[0] = 0;

        // Listener für die Breite hinzufügen
        nocanvas_pane_strecke.widthProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0) {
                // Listener entfernen, nachdem die Breite nicht mehr 0 ist
                if(integer[0] == 0){
                    integer[0] = 1;
                }else{
                    this.initalisierung_is_fertig();
                }
                //nocanvas_pane_strecke.widthProperty().removeListener((ChangeListener<? super Number>) this);
            }
        });

        // Listener für die Höhe hinzufügen
        nocanvas_pane_strecke.heightProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.doubleValue() > 0) {
                // Listener entfernen, nachdem die Höhe nicht mehr 0 ist
                if(integer[0] == 0){
                    integer[0] = 1;
                }else{
                    this.initalisierung_is_fertig();
                }
                //nocanvas_pane_strecke.heightProperty().removeListener((ChangeListener<? super Number>) this);
            }
        });
    }

    private double posGridX_to_posCanvasX(int posGridX){
        double canvas_breite = this.nocanvas_pane_strecke.getWidth();
        int strecken_breite = this.rennstrecke.getBreite();

        return this.RAND_X + posGridX / ((double)strecken_breite) * (canvas_breite - 2*this.RAND_X);
    }

    private double posGridY_to_posCanvasY(int posGridY){
        double canvas_hoehe = this.nocanvas_pane_strecke.getHeight();
        int strecken_hoehe = this.rennstrecke.getHoehe();

        return this.RAND_Y + posGridY / ((double)strecken_hoehe) * (canvas_hoehe - 2*this.RAND_Y);
    }

    private double posCanvasX_to_posGridX(double posCanvasX){
        double canvas_breite = this.nocanvas_pane_strecke.getWidth();
        int grid_breite = this.rennstrecke.getBreite();

        return (posCanvasX - this.RAND_X) * grid_breite / (canvas_breite - 2*this.RAND_X);
    }

    private double posCanvasY_to_posGridY(double posCanvasY){
        double canvas_hoehe = this.nocanvas_pane_strecke.getHeight();
        int grid_hoehe = this.rennstrecke.getHoehe();

        return (posCanvasY - this.RAND_Y) * grid_hoehe / (canvas_hoehe - 2*this.RAND_Y);
    }

    private int posCanvasX_to_posGridX_round(double posCanvasX){
        double posGridX_double = this.posCanvasX_to_posGridX(posCanvasX);

        int posGridX_int;
        if(posGridX_double <= 0.2 && posGridX_double >= -0.2){
            posGridX_int = 0;
        }else if(posGridX_double-(double) ((int)posGridX_double) <= 0.2){
            posGridX_int = (int)posGridX_double;
        }else if(posGridX_double-(double) ((int)posGridX_double) >= 0.8) {
            posGridX_int = ((int) posGridX_double) + 1;
        }else {
            posGridX_int = -1;
        }

        return posGridX_int;
    }



    private int posCanvasY_to_posGridY_round(double posCanvasY){
        double canvas_breite = this.nocanvas_pane_strecke.getWidth();
        int grid_breite = this.rennstrecke.getBreite();

        double posGridY_double = this.posCanvasY_to_posGridY(posCanvasY);

        int posGridY_int;
        if(posGridY_double <= 0.2 && posGridY_double >= -0.2){
            posGridY_int = 0;
        }else if(posGridY_double-(double) ((int)posGridY_double) <= 0.2){
            posGridY_int = (int)posGridY_double;
        }else if(posGridY_double-(double) ((int)posGridY_double) >= 0.8) {
            posGridY_int = ((int) posGridY_double) + 1;
        }else {
            posGridY_int = -1;
        }

        return posGridY_int;
    }




}
**/
