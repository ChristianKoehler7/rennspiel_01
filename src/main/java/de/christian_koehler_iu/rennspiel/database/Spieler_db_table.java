package de.christian_koehler_iu.rennspiel.database;

import de.christian_koehler_iu.rennspiel.data_classes.Spieler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * diese klasse ist für die sqlite tabelle Spieler zuständig
 * - die sichtbarkeit ist protected, damit diese klasse nur innerhalb des package database erreichbar ist
 * - enthält den string, um die tabelle zu erstellen
 * - enthält verschiedene methoden, um auf die db-tabelle zuzugreifen
 */
public class Spieler_db_table {

    protected final String TABELLENNAME = "Spieler";
    protected final String SPALTENNAME_NAME = "name";

    // constructor protected damit nur innerhabl des pakts zugriff möglich ist
    // datenbankzugriffe von außen müssen über Rennstrecke_database_connection oder Spieler_database_connection erfolgen
    protected Spieler_db_table() {
    }

    protected String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (" +
                SPALTENNAME_NAME + " TEXT PRIMARY KEY" +
                ");";
    }

    /**
     * speicher neuen spieler in datenbank, aber nur den spielernamen und nicht die bestzeiten
     * @param spieler_name
     * @throws SQLException
     */
    protected void save_new_spieler(String spieler_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // test ob spieler schon vorhanden
        String sql_expression = "SELECT " + SPALTENNAME_NAME + " FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_NAME + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, spieler_name);
            ResultSet rs = pstmt.executeQuery();
            if(rs.next()){
                // spielername schon vorhanden
                throw new SQLException("Fehler beim Speichern des neuen Spielers: Spieler mit dem Namen " + spieler_name + " schon vorhanden!" );
            }
        }
        // spieler noch nicht vorhanden

        // spieler in db anlegen
        sql_expression = "INSERT INTO " + TABELLENNAME + " (" + SPALTENNAME_NAME + ") VALUES (?);";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, spieler_name);
            pstmt.executeUpdate();
        }
    }

    // fertig
    @Nullable
    protected Spieler get_spieler_complete(String spieler_name) throws SQLException {
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
    protected ArrayList<String> get_spieler_namen() throws SQLException {
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

    /**
     * Löscht einen Spieler aus der DB zusammen mit allen Streckenbestzeiten.
     * @param spieler_name Name des zu löschenden Spielers
     * @throws SQLException Bei SQL-Fehlern
     */
    protected void delete_spieler_complete(String spieler_name) throws SQLException {
        // Datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();
        Connection connection = sqLiteDbConnection.getConnection();

        // Beginne eine Transaktion, falls es beim löschen der Streckenrekorde des Spielers zu fehlern kommt
        //  wird alles rückgängig gemacht um inkonsistenzen der db zu vermeiden
        try (PreparedStatement beginStmt = connection.prepareStatement("BEGIN TRANSACTION")) {
            beginStmt.execute();

            // Spieler löschen
            String sqlExpression = "DELETE FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_NAME + " = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(sqlExpression)) {
                pstmt.setString(1, spieler_name);
                pstmt.executeUpdate();
            }

            // Dazugehörige Einträge in LinkRennstreckeSpielerBestzeit löschen
            new LinkRennstreckeSpielerBestzeit_db_table().delete_alle_eintraege_des_spielers(spieler_name);

            // Commit der Transaktion
            try (PreparedStatement commitStmt = connection.prepareStatement("COMMIT")) {
                commitStmt.execute();
            }
        } catch (SQLException e) {
            // Rollback der Transaktion bei Fehler
            try (PreparedStatement rollbackStmt = connection.prepareStatement("ROLLBACK")) {
                rollbackStmt.execute();
            }
            throw e;
        }
    }


}
