package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LinkRennstreckeSpielerBestzeit_db_table {

    protected final String TABELLENNAME = "Link_rennstrecke_spielerBestzeit";
    protected final String SPALTENNAME_ID = "id";
    protected final String SPALTENNAME_FK_RENNSTRECKE = "fk_rennstrecke";
    protected final String SPALTENNAME_FK_SPIELER = "fk_spieler";
    protected final String SPALTENNAME_BESTZEIT = "bestzeit";

    // constructor protected damit nur innerhabl des pakts zugriff möglich ist
    // datenbankzugriffe von außen müssen über Rennstrecke_database_connection oder Spieler_database_connection erfolgen
    protected LinkRennstreckeSpielerBestzeit_db_table() {
    }

    protected String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (\n" +
                SPALTENNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                SPALTENNAME_FK_RENNSTRECKE + " TEXT NOT NULL,\n" +
                SPALTENNAME_FK_SPIELER + " TEXT NOT NULL,\n" +
                SPALTENNAME_BESTZEIT + " REAL NOT NULL,\n" +
                "FOREIGN KEY ("+ SPALTENNAME_FK_RENNSTRECKE +") REFERENCES " + new Rennstrecke_db_table().TABELLENNAME + " ("+new Rennstrecke_db_table().SPALTENNAME_NAME+")\n" +
                "FOREIGN KEY ("+ SPALTENNAME_FK_SPIELER +") REFERENCES " + new Spieler_db_table().TABELLENNAME +" ("+ new Spieler_db_table().SPALTENNAME_NAME +")\n" +
                ");";
    }

    protected void delete_alle_eintraege_der_rennstrecke(String rennstrecke_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // einträge der rennstrecke löschen
        String sql_expression = "DELETE FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, rennstrecke_name);
            pstmt.executeUpdate();
        }
    }

    protected void delete_alle_eintraege_des_spielers(String spieler_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // einträge der rennstrecke löschen
        String sql_expression = "DELETE FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_FK_SPIELER + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, spieler_name);
            pstmt.executeUpdate();
        }
    }

    protected HashMap<String, Double> get_spieler_bestzeiten(String spieler_name) throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // ausgabe hashMap erzeugen
        HashMap<String, Double> strecken_bestzeiten = new HashMap<>();

        String sql_expression = "SELECT * FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_FK_SPIELER + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, spieler_name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String akt_strecken_name = rs.getString(SPALTENNAME_FK_RENNSTRECKE);
                double akt_bestzeit = rs.getDouble(SPALTENNAME_BESTZEIT);
                // strecken_name und bestzeit in hashMap einfügen
                strecken_bestzeiten.put(akt_strecken_name, akt_bestzeit);
            }
        }

        // hashMap ausgeben
        return strecken_bestzeiten;
    }
}



//package de.christian_koehler_iu.rennspiel.database;
//
//import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
//
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.HashMap;
//
//public class LinkRennstreckeSpielerBestzeit_db_table {
//
//    protected final String TABELLENNAME = "Link_rennstrecke_spielerBestzeit";
//    protected final String SPALTENNAME_ID = "id";
//    protected final String SPALTENNAME_FK_RENNSTRECKE = "fk_rennstrecke";
//    protected final String SPALTENNAME_FK_SPIELER = "fk_spieler";
//    protected final String SPALTENNAME_BESTZEIT = "bestzeit";
//
//    // constructor protected damit nur innerhabl des pakts zugriff möglich ist
//    // datenbankzugriffe von außen müssen über Rennstrecke_database_connection oder Spieler_database_connection erfolgen
//    protected LinkRennstreckeSpielerBestzeit_db_table() {
//    }
//
//    protected String get_create_table_string(){
//        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (\n" +
//                SPALTENNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
//                SPALTENNAME_FK_RENNSTRECKE + " TEXT NOT NULL,\n" +
//                SPALTENNAME_FK_SPIELER + " TEXT NOT NULL,\n" +
//                SPALTENNAME_BESTZEIT + " REAL NOT NULL,\n" +
//                "FOREIGN KEY ("+ SPALTENNAME_FK_RENNSTRECKE +") REFERENCES " + new Rennstrecke_db_table().TABELLENNAME + " ("+new Rennstrecke_db_table().SPALTENNAME_NAME+")\n" +
//                "FOREIGN KEY ("+ SPALTENNAME_FK_SPIELER +") REFERENCES " + new Spieler_db_table().TABELLENNAME +" ("+ new Spieler_db_table().SPALTENNAME_NAME +")\n" +
//                ");";
//    }
//
//    protected void delete_alle_eintraege_der_rennstrecke(String rennstrecke_name) throws SQLException {
//        // datenbankverbindung holen
//        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();
//
//        // einträge der rennstrecke löschen
//        String sql_expression = "DELETE FROM " + TABELLENNAME + "\n" +
//                "WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = '" + rennstrecke_name + "'\n" +
//                ";";
//        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
//            pstmt.executeUpdate();
//        }
//    }
//
//    protected HashMap<String, Double> get_spieler_bestzeiten(String spieler_name) throws SQLException{
//        // datenbankverbindung holen
//        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();
//
//        // ausgabe hashMap erzeugen
//        HashMap<String, Double> strecken_bestzeiten = new HashMap<>();
//
//        String sql_expression = "SELECT * FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_FK_SPIELER + " = ?;";
//        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
//            pstmt.setString(1, spieler_name);
//            ResultSet rs = pstmt.executeQuery();
//            while (rs.next()) {
//                String akt_strecken_name = rs.getString(SPALTENNAME_FK_RENNSTRECKE);
//                double akt_bestzeit = rs.getDouble(SPALTENNAME_BESTZEIT);
//                // strecken_name und bestzeit in hashMap einfügen
//                strecken_bestzeiten.put(akt_strecken_name, akt_bestzeit);
//            }
//        }
//
//        // hashMap ausgeben
//        return strecken_bestzeiten;
//    }
//}
