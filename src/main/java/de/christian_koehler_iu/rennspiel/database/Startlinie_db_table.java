package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import org.jetbrains.annotations.Nullable;

import java.sql.*;

public class Startlinie_db_table {

    public final String TABELLENNAME = "Startlinie";
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



    public void saveStartlinie(Linie startlinie, String strecken_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // statlinie in db speichern
        String sql_expression = "INSERT INTO " + TABELLENNAME + " ("+SPALTENNAME_FK_RENNSTRECKE+", "+SPALTENNAME_X0+", "+SPALTENNAME_Y0+", "+SPALTENNAME_X1+", "+SPALTENNAME_Y1+") VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            pstmt.setInt(2, startlinie.getP0().getX());
            pstmt.setInt(3, startlinie.getP0().getY());
            pstmt.setInt(4, startlinie.getP1().getX());
            pstmt.setInt(5, startlinie.getP1().getY());
            pstmt.executeUpdate();
        }
    }

    @Nullable
    public Linie get_startlinie(String strecken_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // linien variable erzeugen
        Linie startlinie = null;

        // startlinie aus db laden
        String sql_expression = "SELECT * FROM "+TABELLENNAME+" WHERE "+SPALTENNAME_FK_RENNSTRECKE+" = ?";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int x0 = rs.getInt(SPALTENNAME_X0);
                int y0 = rs.getInt(SPALTENNAME_Y0);
                int x1 = rs.getInt(SPALTENNAME_X1);
                int y1 = rs.getInt(SPALTENNAME_Y1);
                startlinie = new Linie(x0, y0, x1, y1);
            }
        }

        // startlinie ausgeben, wenn keine gefunden wurde, dann wird null ausgegeben
        return startlinie;
    }

    public void delete_startlinie(String rennstrecke_name) throws SQLException {
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();
        String sql_delete_startlinie_der_rennstrecke = "DELETE FROM " + TABELLENNAME + "\n" +
                "WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = '" + rennstrecke_name + "'\n" +
                ";";

        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_delete_startlinie_der_rennstrecke)) {
            pstmt.executeUpdate();
        }
    }
}
