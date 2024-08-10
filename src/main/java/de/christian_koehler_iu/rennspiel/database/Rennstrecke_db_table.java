package de.christian_koehler_iu.rennspiel.database;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;

public class Rennstrecke_db_table {

    public final String TABELLENNAME = "Rennstrecke";
    public final String SPALTENNAME_NAME = "name";
    public final String SPALTENNAME_IS_STANDARTSTRECKE = "is_standartstrecke";
    public final String SPALTENNAME_BREITE = "breite";
    public final String SPALTENNAME_HOEHE = "hoehe";
    public final String SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS = "is_startrichtung_nach_unten_oder_rechts";
    public final String SPALTENNAME_ANZ_RUNDEN = "anz_runden";
    public final String SPALTENNAME_STRECKEN_BESTZEIT = "strecken_bestzeit";

    protected String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (" +
                SPALTENNAME_NAME + " TEXT PRIMARY KEY," +
                SPALTENNAME_IS_STANDARTSTRECKE + " BOOLEAN NOT NULL," +
                SPALTENNAME_BREITE + " INTEGER NOT NULL," +
                SPALTENNAME_HOEHE + " INTEGER NOT NULL," +
                SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS + " BOOLEAN NOT NULL," +
                SPALTENNAME_ANZ_RUNDEN + " INTEGER NOT NULL," +
                SPALTENNAME_STRECKEN_BESTZEIT + " REAL DEFAULT -1 NOT NULL" +
                ");";
    }

    // constructor protected damit nur innerhabl des pakts zugriff möglich ist
    // datenbankzugriffe von außen müssen über Rennstrecke_database_connection oder Spieler_database_connection erfolgen
    protected Rennstrecke_db_table() {
    }

    /**
     * speichert ein rennstrecken objekt mit allen daten und linien in die datenbank
     *  falls die rennstrecke schon in der datenbank vorhanden ist, werden erst die rennstrecken-einträgen und alle linien-einträge gelöscht
     * @param rennstrecke
     * @throws SQLException
     */
    protected void save_rennstrecke(Rennstrecke rennstrecke) throws SQLException {
        // test ob alle nötigen daten vorhanden
        if(rennstrecke.getStartlinie() == null){
            throw new SQLException("Fehler beim speichern der Rennstrecke " + rennstrecke.getName() + ": Rennstrecke muss eine Startlinie besitzen!");
        }

        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // rennstrecke db-eintrag machen
        String sql_expression = "INSERT INTO " + TABELLENNAME +
                " ("+SPALTENNAME_NAME+", " +
                    SPALTENNAME_IS_STANDARTSTRECKE + ", " +
                    SPALTENNAME_BREITE + ", " +
                    SPALTENNAME_HOEHE + ", " +
                    SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS + ", " +
                    SPALTENNAME_ANZ_RUNDEN + ") VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, rennstrecke.getName());
            pstmt.setBoolean(2, rennstrecke.get_is_standartstrecke());
            pstmt.setInt(3, rennstrecke.getBreite());
            pstmt.setInt(4, rennstrecke.getHoehe());
            pstmt.setBoolean(5, rennstrecke.get_is_startrichtung_nach_unten_oder_rechts());
            pstmt.setInt(6, rennstrecke.get_anz_runden());
            pstmt.executeUpdate();
        }

        // streckenlinien db-einträge machen
        Streckenlinie_db_table streckenlinie_db_table = new Streckenlinie_db_table();
        for (Linie linie : rennstrecke.getStreckenlinien()) {
            streckenlinie_db_table.saveStreckenlinie(linie, rennstrecke.getName());
        }

        // startlinie db-eintrag machen
        Startlinie_db_table startlinie_db_table = new Startlinie_db_table();
        startlinie_db_table.saveStartlinie(rennstrecke.getStartlinie(), rennstrecke.getName());
    }

    // fertig
    @Nullable
    protected Rennstrecke load_rennstrecke_complete(String strecken_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // rennstrecke aus db laden
        String sql_expression = "SELECT * FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_NAME + " = ?";
        Rennstrecke rennstrecke = null;
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean is_standartstrecke = rs.getBoolean(SPALTENNAME_IS_STANDARTSTRECKE);
                int breite = rs.getInt(SPALTENNAME_BREITE);
                int hoehe = rs.getInt(SPALTENNAME_HOEHE);
                boolean isStartrichtungNachUntenOderRechts = rs.getBoolean(SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS);
                int anz_runden = rs.getInt(SPALTENNAME_ANZ_RUNDEN);

                Streckenlinie_db_table streckenlinie_db_table = new Streckenlinie_db_table();
                ArrayList<Linie> streckenlinien = streckenlinie_db_table.getStreckenlinien(strecken_name);

                Startlinie_db_table startlinie_db_table = new Startlinie_db_table();
                Linie startlinie = startlinie_db_table.get_startlinie(strecken_name);

                rennstrecke = new Rennstrecke(strecken_name,
                        is_standartstrecke,
                        breite,
                        hoehe,
                        startlinie,
                        streckenlinien,
                        isStartrichtungNachUntenOderRechts,
                        anz_runden);
            }
        }
        return rennstrecke;
    }

    // fertig
    protected void delete_rennstrecke_complete(String strecken_name) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // testen ob es eine rennstrecke gibt, mit dem streckennamen
        String sql_expression = "SELECT "+SPALTENNAME_NAME+" FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_NAME + " = ?";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setString(1, strecken_name);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                // name nicht vorhanden
                throw new SQLException("Fehler beim Löschen der Rennstrecke: Rennstrecke mit dem Namen " + strecken_name + " nicht vorhanden!" );
            }
        }
        // rennstrecke mit dem streckennamen aus eingabe vorhanden

        // beginne eine Transaktion, falls es zwischen drin zu fehlern kommt,
        //  wird alles rückgängig gemacht um inkonsistenzen der db zu vermeiden
        try (PreparedStatement beginStmt = sqLiteDbConnection.getConnection().prepareStatement("BEGIN TRANSACTION")) {
            beginStmt.execute();

            // startlinie löschen
            new Startlinie_db_table().delete_startlinie(strecken_name);

            // streckenlinien löschen
            new Streckenlinie_db_table().delete_streckenlinien_der_rennstrecke(strecken_name);

            // streckenrekorde aller spieler für diese rennstrecke löschen
            new LinkRennstreckeSpielerBestzeit_db_table().delete_alle_eintraege_der_rennstrecke(strecken_name);

            // rennstrecke löschen
            sql_expression = "DELETE FROM " + TABELLENNAME + "\n" +
                                    "WHERE " + SPALTENNAME_NAME + " = ?;";
            try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
                pstmt.setString(1, strecken_name);
                pstmt.execute();
            }

            // Commit der Transaktion
            try (PreparedStatement commitStmt = sqLiteDbConnection.getConnection().prepareStatement("COMMIT")) {
                commitStmt.execute();
            }
        } catch (SQLException e) {
            // Rollback der Transaktion bei Fehler
            try (PreparedStatement rollbackStmt = sqLiteDbConnection.getConnection().prepareStatement("ROLLBACK")) {
                rollbackStmt.execute();
            }
            throw e;
        }
    }

    protected ArrayList<String> get_strecken_namen() throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // ausgabe arraylist erstellen
        ArrayList<String> strecken_namen = new ArrayList<>();

        // streckennamen aus db holen
        String sql_expression = "SELECT " + SPALTENNAME_NAME +  " FROM " + TABELLENNAME + ";";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String akt_strecken_name = rs.getString(SPALTENNAME_NAME);
                // streckennamen in ausgabe-arraylist hinzufügen
                strecken_namen.add(akt_strecken_name);
            }
        }

        // streckennamen ausgeben
        return strecken_namen;
    }

    /**
     * läd alle Streckennamen der Standartstrecken aus db
     * @return ArrayList<String> mit allen Streckennamen der Standartstrecken
     * @throws SQLException
     */
    protected ArrayList<String> get_strecke_standart_namen() throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // ausgabe arraylist erstellen
        ArrayList<String> strecken_namen = new ArrayList<>();

        // streckennamen aus db holen
        String sql_expression = "SELECT " + SPALTENNAME_NAME + " FROM " + TABELLENNAME + "\n"+
                "WHERE " + SPALTENNAME_IS_STANDARTSTRECKE + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setBoolean(1, true);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String akt_strecken_name = rs.getString(SPALTENNAME_NAME);
                // streckennamen in ausgabe-arraylist hinzufügen
                strecken_namen.add(akt_strecken_name);
            }
        }

        // streckennamen ausgeben
        return strecken_namen;
    }

    /**
     * läd alle Streckennamen der selbst erstellten Strecken aus db
     * @return ArrayList<String> mit allen Streckennamen der selbst erstellten Strecken
     * @throws SQLException
     */
    protected ArrayList<String> get_strecke_custom_namen() throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // ausgabe arraylist erstellen
        ArrayList<String> strecken_namen = new ArrayList<>();

        // streckennamen aus db holen
        String sql_expression = "SELECT " + SPALTENNAME_NAME +  " FROM " + TABELLENNAME + "\n" +
                "WHERE " + SPALTENNAME_IS_STANDARTSTRECKE + " = ?;";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setBoolean(1, false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String akt_strecken_name = rs.getString(SPALTENNAME_NAME);
                // streckennamen in ausgabe-arraylist hinzufügen
                strecken_namen.add(akt_strecken_name);
            }
        }

        // streckennamen ausgeben
        return strecken_namen;
    }
}