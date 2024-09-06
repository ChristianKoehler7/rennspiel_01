package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import org.jetbrains.annotations.Nullable;

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

        // ausgabe hashMap erzeugen strecken_name->bestzeit
        HashMap<String, Double> spieler_bestzeiten = new HashMap<>();

        String sql_expression = "SELECT * FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_FK_SPIELER + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, spieler_name);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String akt_strecken_name = rs.getString(SPALTENNAME_FK_RENNSTRECKE);
                double akt_bestzeit = rs.getDouble(SPALTENNAME_BESTZEIT);
                // strecken_name und bestzeit in hashMap einfügen
                spieler_bestzeiten.put(akt_strecken_name, akt_bestzeit);
            }
        }

        // hashMap ausgeben
        return spieler_bestzeiten;
    }

    @Nullable
    protected Double get_spieler_strecke_bestzeit(String strecken_name, String spieler_name) throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // ausgabe Double erzeugen
        Double spieler_strecke_bestzeit = null;

        String sql_expression = "SELECT * FROM " + TABELLENNAME + " WHERE "
                + SPALTENNAME_FK_RENNSTRECKE + " = ? AND " + SPALTENNAME_FK_SPIELER + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            pstmt.setString(2, spieler_name);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                spieler_strecke_bestzeit = rs.getDouble(SPALTENNAME_BESTZEIT);
                System.out.println("LinkRennstreckeSpielerBestzeit_db_table: get_spieler_strecke_bestzeit: spieler_strecke_bestzeit=" +spieler_strecke_bestzeit);
            }
        }

        // bestzeit ausgeben
        return spieler_strecke_bestzeit;
    }

    @Nullable
    protected void create_new_spieler_strecke_bestzeit(String strecken_name, String spieler_name, double bestzeit) throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        String sql_expression = "INSERT INTO " + TABELLENNAME + "(" + SPALTENNAME_FK_RENNSTRECKE + ", " + SPALTENNAME_FK_SPIELER + ", " + SPALTENNAME_BESTZEIT +")\n" +
                "VALUES ( ?, ?, ?);";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            pstmt.setString(2, spieler_name);
            pstmt.setDouble(3, bestzeit);
            pstmt.executeUpdate();
        }
    }

    @Nullable
    protected void update_spieler_strecke_bestzeit(String strecken_name, String spieler_name, double bestzeit) throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        String sql_expression = "UPDATE " + TABELLENNAME + "\n" +
                "SET " + SPALTENNAME_BESTZEIT + " = ?\n" +
                "WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = ? AND " + SPALTENNAME_FK_SPIELER + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setDouble(1, bestzeit);
            pstmt.setString(2, strecken_name);
            pstmt.setString(3, spieler_name);
            pstmt.executeUpdate();
        }
    }


    @Nullable
    protected Double get_strecken_bestzeit(String strecken_name) throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // ausgabe Double erzeugen
        Double strecken_bestzeit = null;

        String sql_expression = "SELECT * FROM " + TABELLENNAME + "\n" +
                "WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = ?\n" +
                "ORDER BY " + SPALTENNAME_BESTZEIT + " ASC\n" +
                "LIMIT 1";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                strecken_bestzeit = rs.getDouble(SPALTENNAME_BESTZEIT);
            }
        }

        // bestzeit ausgeben
        return strecken_bestzeit;
    }

    @Nullable
    protected String get_strecke_bester_spieler(String strecken_name) throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // ausgabe erzeugen
        String spieler_name = null;

        String sql_expression = "SELECT * FROM " + TABELLENNAME + "\n" +
                "WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = ?\n" +
                "ORDER BY " + SPALTENNAME_BESTZEIT + " ASC\n" +
                "LIMIT 1";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()) {
                spieler_name = rs.getString(SPALTENNAME_FK_SPIELER);
            }
        }

        // spieler ausgeben, der für gegebene strecke die beste zeit gefahren hat
        return spieler_name;
    }



}