module de.rennspiel_01.rennspiel_01 {
    requires javafx.controls;
    requires javafx.fxml;


    opens de.rennspiel_01.rennspiel_01 to javafx.fxml;
    exports de.rennspiel_01.rennspiel_01;
}