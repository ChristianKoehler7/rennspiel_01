package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class LinkRennstreckeSpielerBestzeit_db_table {

    public final String TABELLENNAME = "Link_rennstrecke_spielerBestzeit";
    public final String SPALTENNAME_ID = "id";
    public final String SPALTENNAME_FK_RENNSTRECKE = "fk_rennstrecke";
    public final String SPALTENNAME_FK_SPIELER = "fk_spieler";
    public final String SPALTENNAME_BESTZEIT = "bestzeit";

    public String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (\n" +
                SPALTENNAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,\n" +
                SPALTENNAME_FK_RENNSTRECKE + " TEXT NOT NULL,\n" +
                SPALTENNAME_FK_SPIELER + " TEXT NOT NULL,\n" +
                SPALTENNAME_BESTZEIT + " REAL NOT NULL,\n" +
                "FOREIGN KEY ("+ SPALTENNAME_FK_RENNSTRECKE +") REFERENCES " + new Rennstrecke_db_table().TABELLENNAME + " ("+new Rennstrecke_db_table().SPALTENNAME_NAME+")\n" +
                "FOREIGN KEY ("+ SPALTENNAME_FK_SPIELER +") REFERENCES " + new Spieler_db_table().TABELLENNAME +" ("+ new Spieler_db_table().SPALTENNAME_NAME +")\n" +
                ");";
    }

    public void delete_alle_eintraege_der_rennstrecke(String rennstrecke_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // einträge der rennstrecke löschen
        String sql_expression = "DELETE FROM " + TABELLENNAME + "\n" +
                "WHERE " + SPALTENNAME_FK_RENNSTRECKE + " = '" + rennstrecke_name + "'\n" +
                ";";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.executeUpdate();
        }
    }

    public HashMap<String, Double> get_spieler_bestzeiten(String spieler_name) throws SQLException{
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
