package de.christian_koehler_iu.rennspiel.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Admin_functions {


    public Admin_functions() {
    }


    public void set_rennstrecke_as_default(String strecken_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        Rennstrecke_db_table rennstrecke_db_table = new Rennstrecke_db_table();

        // rennstrecke db-eintrag machen
        String sql_expression = "UPDATE " + rennstrecke_db_table.TABELLENNAME + "\n" +
                "SET " + rennstrecke_db_table.SPALTENNAME_IS_STANDARTSTRECKE +" = ? \n" +
                "WHERE " + rennstrecke_db_table.SPALTENNAME_NAME +" = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setBoolean(1, true);
            pstmt.setString(2, strecken_name);
            pstmt.executeUpdate();
        }
    }
}
