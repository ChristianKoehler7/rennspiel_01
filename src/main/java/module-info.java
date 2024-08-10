module de.christian_koehler_iu.rennspiel {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.sql;
    requires java.desktop;


    opens de.christian_koehler_iu.rennspiel to javafx.fxml;
    exports de.christian_koehler_iu.rennspiel;


    opens de.christian_koehler_iu.rennspiel.controller to javafx.fxml;
    exports de.christian_koehler_iu.rennspiel.controller;

    exports de.christian_koehler_iu.rennspiel.data_classes;


}