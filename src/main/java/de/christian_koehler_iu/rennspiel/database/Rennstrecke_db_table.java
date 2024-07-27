package de.christian_koehler_iu.rennspiel.database;
import de.christian_koehler_iu.rennspiel.data_classes.Rennstrecke;
import de.christian_koehler_iu.rennspiel.data_classes.Linie;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.ArrayList;

public class Rennstrecke_db_table {

    public final String TABELLENNAME = "Streckenlinie";
    public final String SPALTENNAME_NAME = "name";
    public final String SPALTENNAME_IS_STANDARTSTRECKE = "is_standartstrecke";
    public final String SPALTENNAME_BREITE = "breite";
    public final String SPALTENNAME_HOEHE = "hoehe";
    public final String SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS = "is_startrichtung_nach_unten_oder_rechts";
    public final String SPALTENNAME_ANZ_RUNDEN = "anz_runden";
    public final String SPALTENNAME_STRECKEN_BESTZEIT = "strecken_bestzeit";

    public String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (" +
                SPALTENNAME_NAME + " TEXT PRIMARY KEY," +
                SPALTENNAME_IS_STANDARTSTRECKE + " BOOLEAN NOT NULL," +
                SPALTENNAME_BREITE + " INTEGER NOT NULL," +
                SPALTENNAME_HOEHE + " INTEGER NOT NULL," +
                SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS + " BOOLEAN NOT NULL," +
                SPALTENNAME_ANZ_RUNDEN + " INTEGER NOT NULL," +
                SPALTENNAME_STRECKEN_BESTZEIT + " REAL NOT NULL" +
                ");";
    }

    // fertig
    public void saveRennstrecke(Rennstrecke rennstrecke) throws SQLException {
        // test ob alle nötigen daten vorhanden
        if(rennstrecke.getStartlinie() == null){
            throw new SQLException("Fehler beim speichern der Rennstrecke " + rennstrecke.getName() + ": Rennstrecke muss eine Startlinie besitzen!");
        }

        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // rennstrecke db-eintrag machen
        String insertRennstreckeSQL = "INSERT INTO " + TABELLENNAME + " ("+SPALTENNAME_NAME+", " + SPALTENNAME_IS_STANDARTSTRECKE + ", " + SPALTENNAME_BREITE + ", " + SPALTENNAME_HOEHE + ", " + SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS + ") VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(insertRennstreckeSQL)) {
            pstmt.setString(1, rennstrecke.getName());
            pstmt.setBoolean(2, rennstrecke.get_is_standartstrecke());
            pstmt.setInt(3, rennstrecke.getBreite());
            pstmt.setInt(4, rennstrecke.getHoehe());
            pstmt.setBoolean(5, rennstrecke.get_is_startrichtung_nach_unten_oder_rechts());
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
    public Rennstrecke getRennstrecke(String name) throws SQLException {
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();
        String selectRennstreckeSQL = "SELECT * FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_NAME + " = ?";
        Rennstrecke rennstrecke = null;

        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(selectRennstreckeSQL)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                boolean is_standartstrecke = rs.getBoolean(SPALTENNAME_IS_STANDARTSTRECKE);
                int breite = rs.getInt(SPALTENNAME_BREITE);
                int hoehe = rs.getInt(SPALTENNAME_HOEHE);
                boolean isStartrichtungNachUntenOderRechts = rs.getBoolean(SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS);

                Streckenlinie_db_table streckenlinie_db_table = new Streckenlinie_db_table();
                ArrayList<Linie> streckenlinien = streckenlinie_db_table.getStreckenlinien(name);

                Startlinie_db_table startlinieDAO = new Startlinie_db_table();
                Linie startlinie = startlinieDAO.getStartlinie(name);

                rennstrecke = new Rennstrecke(name, is_standartstrecke, breite, hoehe, startlinie, streckenlinien, isStartrichtungNachUntenOderRechts);
            }
        }
        return rennstrecke;
    }

    // fertig
    public void delete_rennstrecke(String strecken_name, boolean auch_streckenrekorde_loeschen) throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // testen ob es eine rennstrecke gibt, mit dem streckennamen
        String selectRennstreckeNameSQL = "SELECT "+SPALTENNAME_NAME+" FROM " + TABELLENNAME + " WHERE " + SPALTENNAME_NAME + " = ?";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(selectRennstreckeNameSQL)) {
            pstmt.setString(1, strecken_name);
            ResultSet rs = pstmt.executeQuery();

            if (!rs.next()) {
                // name nicht vorhanden
                throw new SQLException("Fehler beim löschen der Rennstrecke: Rennstrecke mit dem Namen " + strecken_name + " nicht vorhanden!" );
            }
        }
        // rennstrecke mit dem streckennamen aus eingabe vorhanden

        // startlinie löschen
        new Startlinie_db_table().delete_startlinie(strecken_name);

        // streckenlinien löschen
        new Streckenlinie_db_table().delete_streckenlinien(strecken_name);

        // streckenrekorde aller spieler für diese rennstrecke löschen
        if(auch_streckenrekorde_loeschen){
            //TODO
        }
    }
}
