package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Linie;

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
                SPALTENNAME_ID + " TEXT PRIMARY KEY," +
                SPALTENNAME_FK_RENNSTRECKE + " TEXT NOT NULL," +
                SPALTENNAME_X0 + " INTEGER NOT NULL," +
                SPALTENNAME_Y0 + " INTEGER NOT NULL," +
                SPALTENNAME_X1 + " INTEGER NOT NULL," +
                SPALTENNAME_Y1 + " INTEGER NOT NULL," +
                "FOREIGN KEY ("+SPALTENNAME_FK_RENNSTRECKE+") REFERENCES " + new Rennstrecke_db_table().TABELLENNAME + "("+new Rennstrecke_db_table().SPALTENNAME_NAME+")," +
                ");";
    }



    public void saveStartlinie(Linie startlinie, String rennstreckeName) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String insertStartlinieSQL = "INSERT INTO Startlinie (fk_rennstrecke, x0, y0, x1, y1) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertStartlinieSQL)) {
            pstmt.setString(1, rennstreckeName);
            pstmt.setInt(2, startlinie.getP0().getX());
            pstmt.setInt(3, startlinie.getP0().getY());
            pstmt.setInt(4, startlinie.getP1().getX());
            pstmt.setInt(5, startlinie.getP1().getY());
            pstmt.executeUpdate();
        }
    }

    public Linie getStartlinie(String rennstreckeName) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String selectStartlinieSQL = "SELECT * FROM Startlinie WHERE fk_rennstrecke = ?";
        Linie startlinie = null;

        try (PreparedStatement pstmt = connection.prepareStatement(selectStartlinieSQL)) {
            pstmt.setString(1, rennstreckeName);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                int x0 = rs.getInt("x0");
                int y0 = rs.getInt("y0");
                int x1 = rs.getInt("x1");
                int y1 = rs.getInt("y1");

                Punkt p0 = new Punkt();
                p0.setX(x0);
                p0.setY(y0);

                Punkt p1 = new Punkt();
                p1.setX(x1);
                p1.setY(y1);

                startlinie = new Linie(p0, p1);
            }
        }
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
