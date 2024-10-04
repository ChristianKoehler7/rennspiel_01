package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Linie;

import java.sql.*;
import java.util.ArrayList;

/**
 * diese klasse ist für die sqlite tabelle Streckenlinie zuständig
 * - die sichtbarkeit ist protected, damit diese klasse nur innerhalb des package database erreichbar ist
 * - enthält den string, um die tabelle zu erstellen
 * - enthält verschiedene methoden, um auf die db-tabelle zuzugreifen
 */
public class Streckenlinie_db_table {

    public final String DEFAULT_STRECKEN_STRECKENLINIEN_CSV_PATH = "/de/christian_koehler_iu/rennspiel/default_strecken_streckenlinien.csv";

    protected final String TABELLENNAME = "Streckenlinie";
    protected final String SPALTENNAME_ID = "id";
    protected final String SPALTENNAME_FK_RENNSTRECKE = "fk_rennstrecke";
    protected final String SPALTENNAME_X0 = "x0";
    protected final String SPALTENNAME_Y0 = "y0";
    protected final String SPALTENNAME_X1 = "x1";
    protected final String SPALTENNAME_Y1 = "y1";

    // constructor protected damit nur innerhabl des pakts zugriff möglich ist
    // datenbankzugriffe von außen müssen über Rennstrecke_database_connection oder Spieler_database_connection erfolgen
    protected Streckenlinie_db_table() {
    }

    protected String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (" +
                SPALTENNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                SPALTENNAME_FK_RENNSTRECKE + " TEXT NOT NULL," +
                SPALTENNAME_X0 + " INTEGER NOT NULL," +
                SPALTENNAME_Y0 + " INTEGER NOT NULL," +
                SPALTENNAME_X1 + " INTEGER NOT NULL," +
                SPALTENNAME_Y1 + " INTEGER NOT NULL," +
                "FOREIGN KEY ("+SPALTENNAME_FK_RENNSTRECKE+") REFERENCES " + new Rennstrecke_db_table().TABELLENNAME + "("+new Rennstrecke_db_table().SPALTENNAME_NAME+")" +
                ");";
    }

    protected void saveStreckenlinie(Linie linie, String rennstreckeName) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // testen ob linie nur ganzzahlige koordinaten hat
        double x0 = linie.getP0().getX();
        double y0 = linie.getP0().getY();
        double x1 = linie.getP1().getX();
        double y1 = linie.getP1().getY();
        if( !linie.is_jede_koordinate_ganzzahl() ){
            // mindestens eine koordinate ist keine ganzzahl -> fehler ausgeben
            throw new RuntimeException("beim Speichern einer Streckenlinie ist ein Fehler aufgetreten. Mindestens eine Koordinate ist keine Ganzzahl!");
        }
        // alle koordinaten können als ganzzahlen dargestellt werden

        // streckenlinie in db speichern
        String sql_expression = "INSERT INTO " + TABELLENNAME + " ("+SPALTENNAME_FK_RENNSTRECKE+", "+SPALTENNAME_X0+", "+SPALTENNAME_Y0+", "+SPALTENNAME_X1+", "+SPALTENNAME_Y1+")\n" +
                "VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, rennstreckeName);
            pstmt.setInt(2, (int)linie.getP0().getX());
            pstmt.setInt(3, (int)linie.getP0().getY());
            pstmt.setInt(4, (int)linie.getP1().getX());
            pstmt.setInt(5, (int)linie.getP1().getY());
            pstmt.executeUpdate();
        }
    }

    protected ArrayList<Linie> getStreckenlinien(String rennstrecke_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        String sql_expression = "SELECT * FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = ?;";
        ArrayList<Linie> streckenlinien = new ArrayList<>();

        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, rennstrecke_name);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int x0 = rs.getInt(SPALTENNAME_X0);
                int y0 = rs.getInt(SPALTENNAME_Y0);
                int x1 = rs.getInt(SPALTENNAME_X1);
                int y1 = rs.getInt(SPALTENNAME_Y1);

                // linie erzeugen und der arraylist hinzufügen
                streckenlinien.add(new Linie(x0, y0, x1, y1));
            }
        }
        // streckenlinen ausgeben
        return streckenlinien;
    }

    protected void delete_streckenlinien_der_rennstrecke(String rennstrecke_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // streckenlinien löschen
        String sql_expression = "DELETE FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, rennstrecke_name);
            pstmt.executeUpdate();
        }
    }
}