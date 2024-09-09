package de.christian_koehler_iu.rennspiel.database;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

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

    public void set_all_rennstrecken_as_default() throws SQLException {
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        Rennstrecke_db_table rennstrecke_db_table = new Rennstrecke_db_table();

        // rennstrecke db-eintrag machen
        String sql_expression = "UPDATE " + rennstrecke_db_table.TABELLENNAME + "\n" +
                "SET " + rennstrecke_db_table.SPALTENNAME_IS_STANDARTSTRECKE +" = ? \n";
        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression)) {
            pstmt.setBoolean(1, true);
            pstmt.executeUpdate();
        }
    }

    public void export_default_strecken_to_csv() throws SQLException{
        // alle rennstrecken einträge aus rennstrecke_db in csv schreiben
        rennstrecken_to_csv();

        // alle startlinien einträge aus startlinie_db in csv schreiben
        startlinien_to_csv();

        // alle streckenlinien einträge aus streckenlinie_db in csv schreiben
        streckenlinien_to_csv();
    }

    private void rennstrecken_to_csv() throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // rennstrecke_db_table objekt erstellen
        Rennstrecke_db_table rennstrecke_db_table = new Rennstrecke_db_table();

        // sql ausdruck erstellen
        String sql_expression = "SELECT " +
                rennstrecke_db_table.SPALTENNAME_NAME + ", " +
                rennstrecke_db_table.SPALTENNAME_IS_STANDARTSTRECKE + ", " +
                rennstrecke_db_table.SPALTENNAME_BREITE + ", " +
                rennstrecke_db_table.SPALTENNAME_HOEHE + ", " +
                rennstrecke_db_table.SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS + ", " +
                rennstrecke_db_table.SPALTENNAME_ANZ_RUNDEN + " FROM " + rennstrecke_db_table.TABELLENNAME;

        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression);
             ResultSet rs = pstmt.executeQuery();
             FileWriter csvWriter = new FileWriter(rennstrecke_db_table.DEFAULT_STRECKEN_RENNSTRECKEN_CSV_PATH)) {

            // Schreibe die Kopfzeile in die CSV-Datei
            csvWriter.append(
                    rennstrecke_db_table.SPALTENNAME_NAME + "," +
                            rennstrecke_db_table.SPALTENNAME_IS_STANDARTSTRECKE + "," +
                            rennstrecke_db_table.SPALTENNAME_BREITE + "," +
                            rennstrecke_db_table.SPALTENNAME_HOEHE + "," +
                            rennstrecke_db_table.SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS + "," +
                            rennstrecke_db_table.SPALTENNAME_ANZ_RUNDEN + "\n"
            );

            // durchlaufe alle Einträge in der Datenbank und schreibe sie in die CSV
            while (rs.next()) {
                csvWriter.append(rs.getString(rennstrecke_db_table.SPALTENNAME_NAME)).append(",");
                csvWriter.append(String.valueOf(rs.getBoolean(rennstrecke_db_table.SPALTENNAME_IS_STANDARTSTRECKE))).append(",");
                csvWriter.append(String.valueOf(rs.getInt(rennstrecke_db_table.SPALTENNAME_BREITE))).append(",");
                csvWriter.append(String.valueOf(rs.getInt(rennstrecke_db_table.SPALTENNAME_HOEHE))).append(",");
                csvWriter.append(String.valueOf(rs.getBoolean(rennstrecke_db_table.SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS))).append(",");
                csvWriter.append(String.valueOf(rs.getInt(rennstrecke_db_table.SPALTENNAME_ANZ_RUNDEN))).append("\n");
            }

            System.out.println("Rennstrecken erfolgreich in CSV exportiert.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startlinien_to_csv() throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // startlinie_db_table objekt erstellen
        Startlinie_db_table startlinie_db_table = new Startlinie_db_table();

        // sql ausdruck erstellen
        String sql_expression = "SELECT " +
                startlinie_db_table.SPALTENNAME_FK_RENNSTRECKE + ", " +
                startlinie_db_table.SPALTENNAME_X0 + ", " +
                startlinie_db_table.SPALTENNAME_Y0 + ", " +
                startlinie_db_table.SPALTENNAME_X1 + ", " +
                startlinie_db_table.SPALTENNAME_Y1 + " FROM " + startlinie_db_table.TABELLENNAME;

        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression);
             ResultSet rs = pstmt.executeQuery();
             FileWriter csvWriter = new FileWriter(startlinie_db_table.DEFAULT_STRECKEN_STARTLINIEN_CSV_PATH)) {

            // Schreibe die Kopfzeile in die CSV-Datei
            csvWriter.append(
                    startlinie_db_table.SPALTENNAME_FK_RENNSTRECKE + "," +
                            startlinie_db_table.SPALTENNAME_X0 + "," +
                            startlinie_db_table.SPALTENNAME_Y0 + "," +
                            startlinie_db_table.SPALTENNAME_X1 + "," +
                            startlinie_db_table.SPALTENNAME_Y1 + "\n"
            );

            // durchlaufe alle Einträge in der Datenbank und schreibe sie in die CSV
            while (rs.next()) {
                csvWriter.append(rs.getString(startlinie_db_table.SPALTENNAME_FK_RENNSTRECKE)).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(startlinie_db_table.SPALTENNAME_X0))).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(startlinie_db_table.SPALTENNAME_Y0))).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(startlinie_db_table.SPALTENNAME_X1))).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(startlinie_db_table.SPALTENNAME_Y1))).append("\n");
            }

            System.out.println("Startlinien erfolgreich in CSV exportiert.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void streckenlinien_to_csv() throws SQLException{
        // datenbankverbindung holen
        SQLite_db_connection sqLiteDbConnection = SQLite_db_connection.getInstance();

        // streckenlinie_db_table objekt erstellen
        Streckenlinie_db_table streckenlinie_db_table = new Streckenlinie_db_table();

        // sql ausdruck erstellen
        String sql_expression = "SELECT " +
                streckenlinie_db_table.SPALTENNAME_FK_RENNSTRECKE + ", " +
                streckenlinie_db_table.SPALTENNAME_X0 + ", " +
                streckenlinie_db_table.SPALTENNAME_Y0 + ", " +
                streckenlinie_db_table.SPALTENNAME_X1 + ", " +
                streckenlinie_db_table.SPALTENNAME_Y1 + " FROM " + streckenlinie_db_table.TABELLENNAME;

        try (PreparedStatement pstmt = sqLiteDbConnection.getConnection().prepareStatement(sql_expression);
             ResultSet rs = pstmt.executeQuery();
             FileWriter csvWriter = new FileWriter(streckenlinie_db_table.DEFAULT_STRECKEN_STRECKENLINIEN_CSV_PATH)) {

            // Schreibe die Kopfzeile in die CSV-Datei
            csvWriter.append(
                    streckenlinie_db_table.SPALTENNAME_FK_RENNSTRECKE + "," +
                    streckenlinie_db_table.SPALTENNAME_X0 + "," +
                    streckenlinie_db_table.SPALTENNAME_Y0 + "," +
                    streckenlinie_db_table.SPALTENNAME_X1 + "," +
                    streckenlinie_db_table.SPALTENNAME_Y1 + "\n"
            );

            // durchlaufe alle Einträge in der Datenbank und schreibe sie in die CSV
            while (rs.next()) {
                csvWriter.append(rs.getString(streckenlinie_db_table.SPALTENNAME_FK_RENNSTRECKE)).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(streckenlinie_db_table.SPALTENNAME_X0))).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(streckenlinie_db_table.SPALTENNAME_Y0))).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(streckenlinie_db_table.SPALTENNAME_X1))).append(",");
                csvWriter.append(String.valueOf(rs.getDouble(streckenlinie_db_table.SPALTENNAME_Y1))).append("\n");
            }

            System.out.println("Streckenlinien erfolgreich in CSV exportiert.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
