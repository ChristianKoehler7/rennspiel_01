package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import de.christian_koehler_iu.rennspiel.data_classes.Punkt;

import java.sql.*;
import java.util.ArrayList;

public class Streckenlinie_db_table {

    public final String TABELLENNAME = "Streckenlinie";
    public final String SPALTENNAME_ID = "id";
    public final String SPALTENNAME_FK_RENNSTRECKE = "fk_rennstrecke";
    public final String SPALTENNAME_X0 = "x0";
    public final String SPALTENNAME_Y0 = "y0";
    public final String SPALTENNAME_X1 = "x1";
    public final String SPALTENNAME_Y1 = "y1";

    public String get_create_table_string(){
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

    public void saveStreckenlinie(Linie linie, String rennstreckeName) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // streckenlinie in db speichern
        String sql_expression = "INSERT INTO " + TABELLENNAME + " ("+SPALTENNAME_FK_RENNSTRECKE+", "+SPALTENNAME_X0+", "+SPALTENNAME_Y0+", "+SPALTENNAME_X1+", "+SPALTENNAME_Y1+")\n" +
                "VALUES (?, ?, ?, ?, ?);";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, rennstreckeName);
            pstmt.setInt(2, linie.getP0().getX());
            pstmt.setInt(3, linie.getP0().getY());
            pstmt.setInt(4, linie.getP1().getX());
            pstmt.setInt(5, linie.getP1().getY());
            pstmt.executeUpdate();
        }
    }

    public ArrayList<Linie> getStreckenlinien(String rennstrecke_name) throws SQLException {
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

    public void delete_streckenlinien_der_rennstrecke(String rennstrecke_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // streckenlinien löschen
        String sql_expression = "DELETE FROM " + TABELLENNAME + "\n" +
                "WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = '" + rennstrecke_name + "'\n" +
                ";";

        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.executeUpdate();
        }
    }
}
