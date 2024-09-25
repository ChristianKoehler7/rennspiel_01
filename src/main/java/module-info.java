module rennspiel {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.jetbrains.annotations;
    requires java.sql;
    requires java.desktop;


    // Öffne nur Pakete, die FXML-Controller enthalten
    opens de.christian_koehler_iu.rennspiel to javafx.fxml;
    opens de.christian_koehler_iu.rennspiel.main to javafx.fxml;
    opens de.christian_koehler_iu.rennspiel.controller to javafx.fxml;
    opens de.christian_koehler_iu.rennspiel.controller_helper to javafx.fxml;

    // Exporte nur für Pakete, die von externen Modulen benötigt werden
    exports de.christian_koehler_iu.rennspiel.main;
    exports de.christian_koehler_iu.rennspiel.controller;
    exports de.christian_koehler_iu.rennspiel.data_classes;
    exports de.christian_koehler_iu.rennspiel.utility;
    exports de.christian_koehler_iu.rennspiel.interfaces;
    exports de.christian_koehler_iu.rennspiel.controller_helper;
}


//module de.christian_koehler_iu.rennspiel {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires org.jetbrains.annotations;
//    requires java.sql;
//    requires java.desktop;
//
//
//    opens de.christian_koehler_iu.rennspiel to javafx.fxml;
//    exports de.christian_koehler_iu.rennspiel;
//
//
//    opens de.christian_koehler_iu.rennspiel.controller to javafx.fxml;
//    exports de.christian_koehler_iu.rennspiel.controller;
//
//    exports de.christian_koehler_iu.rennspiel.data_classes;
//
//    exports de.christian_koehler_iu.rennspiel.utility;
//
//    exports de.christian_koehler_iu.rennspiel.interfaces;
//
//    opens de.christian_koehler_iu.rennspiel.controller_helper to javafx.fxml;
//    exports de.christian_koehler_iu.rennspiel.controller_helper;
//
//
//}