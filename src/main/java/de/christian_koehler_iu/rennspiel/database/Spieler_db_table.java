package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Spieler_db_table {

    public final String TABELLENNAME = "Spieler";
    public final String SPALTENNAME_NAME = "name";

    public String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (" +
                SPALTENNAME_NAME + " TEXT PRIMARY KEY" +
                ");";
    }

    /**
     * speicher neuen spieler in datenbank, aber nur den spielernamen und nicht die bestzeiten
     * @param spieler
     * @throws SQLException
     */
    public void save_new_spieler(Spieler spieler) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // test ob spieler schon vorhanden
        String sql_expression = "SELECT " + SPALTENNAME_NAME + " FROM " + TABELLENNAME + "\n" +
                "WHERE " + SPALTENNAME_NAME + " = " + spieler.get_name() + ";";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                // spielername schon vorhanden
                throw new SQLException("Fehler beim Speichern des neuen Spielers: Spieler mit dem Namen " + spieler.get_name() + " schon vorhanden!" );
            }
        }
        // spieler noch nicht vorhanden

        // spieler in db anlegen
        sql_expression = "INSERT INTO " + TABELLENNAME + "(" + SPALTENNAME_NAME + ")\n" +
                "VALUES ('" + spieler.get_name() + "')\n" +
                ";";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.executeUpdate();
        }
    }

    // fertig
    @Nullable
    public Spieler get_spieler(String spieler_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // spieler variable erzeugen
        Spieler spieler = null;

        // spieler aus db holen
        String sql_expression = "SELECT * FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_NAME + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, spieler_name);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // spieler ist vorhanden
                // spieler objekt erstellen
                spieler = new Spieler(spieler_name);
                // bestzeiten laden und dem spieler übergeben
                HashMap<String, Double> strecken_bestzeiten = new LinkRennstreckeSpielerBestzeit_db_table().get_spieler_bestzeiten(spieler_name);
                spieler.set_strecken_bestzeiten(strecken_bestzeiten);
            }
        }
        // spieler ausgeben, wenn spieler nicht in db vorhanden, dann wird null ausgegeben
        return spieler;
    }

    @NotNull
    public ArrayList<String> get_spieler_namen() throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // spieler_namen variable erzeugen
        ArrayList<String> spieler_namen = new ArrayList<>();

        // spieler_namen aus db holen
        String sql_expression = "SELECT "+SPALTENNAME_NAME+" FROM " + TABELLENNAME + ";";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                // akt_spieler_name in arraylist einfügen
                String akt_spieler_name = rs.getString(SPALTENNAME_NAME);
                spieler_namen.add(akt_spieler_name);
            }
        }
        // spieler_namen ausgeben
        return spieler_namen;
    }
}
