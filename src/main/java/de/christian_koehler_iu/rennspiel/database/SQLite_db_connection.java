package de.christian_koehler_iu.rennspiel.database;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.Objects;

/*
SQLite_db_connection ist eine Singleton-Klasse, die sich um die Verbindung zur SQLite-Datenbank kümmert und
die Tabellen erstellt, falls sie noch nicht vorhanden sind
 */
public class SQLite_db_connection {

    protected static SQLite_db_connection instance;
    protected static Connection connection;
    protected static final String URL = "jdbc:sqlite:rennspiel.db";

    // privater constructor
    private SQLite_db_connection() throws SQLException {
        try {
            connection = DriverManager.getConnection(URL);
            createTables();
        } catch (SQLException e) {
            throw new SQLException("Failed to create the database connection.", e);
        }
    }

    protected static SQLite_db_connection getInstance() throws SQLException {
        if (instance == null) { // Erster Check (ohne Synchronisierung)
            /*
            Double-Checked Locking Pattern
            in einem Multi-Threading-Umfeld können mehrere Threads gleichzeitig versuchen, die Singleton-Instanz zu erstellen
            dies könnte dazu führen, dass mehrere Instanzen der Klasse erstellt werden, was das Singleton-Designmuster verletzt
            Synchronisierung hilft, dieses Problem zu vermeiden
             */
            synchronized (SQLite_db_connection.class) { // Synchronisierter Block nur ein Thread kann diesen Block gleichzeitig ausführen
                if (instance == null) { // Zweiter Check (innerhalb des synchronisierten Blocks)
                    instance = new SQLite_db_connection();
                }
            }
        }
        return instance;
    }

    protected Connection getConnection() {
        return connection;
    }

    private void createTables() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // alle tabellen erstellen, falls sie noch nicht existieren
            System.out.println("Rennstrecke initialisieren");
            stmt.execute(new Rennstrecke_db_table().get_create_table_string());
            System.out.println("Streckenlinie initialisieren");
            stmt.execute(new Streckenlinie_db_table().get_create_table_string());
            System.out.println("Startlinie initialisieren");
            stmt.execute(new Startlinie_db_table().get_create_table_string());
            System.out.println("Spieler initialisieren");
            stmt.execute(new Spieler_db_table().get_create_table_string());
            System.out.println("LinkRennstreckeSpielerBestzeit initialisieren");
            stmt.execute(new LinkRennstreckeSpielerBestzeit_db_table().get_create_table_string());
            System.out.println("Tabellen fertig initialisiert");

            // default-strecken laden, falls sie noch nicht in db enthalten sind
            import_default_strecken_from_csv();
        }
    }

    private void import_default_strecken_from_csv() throws SQLException{
        // in klasse SQLite_db_connection werden bei initialisierung die datenbank und alle tabellen erstellt, falls noch nicht vorhanden

        // abfragen, ob default-strecken schon in db vorhanden sind
        ArrayList<String> default_strecken_namen = new Rennstrecke_db_table().get_strecke_standart_namen(connection);

        if(default_strecken_namen.isEmpty()){
            // default strecken sind noch nicht in db geladen -> aus csv dateien alle default strecken laden

            // alle rennstrecken einträge aus rennstrecke_db in csv schreiben
            rennstrecken_from_csv(connection);

            // alle startlinien einträge aus startlinie_db in csv schreiben
            startlinien_from_csv(connection);

            // alle streckenlinien einträge aus streckenlinie_db in csv schreiben
            streckenlinien_from_csv(connection);
        }
    }

    private void rennstrecken_from_csv(Connection connection) throws SQLException{
        // rennstrecke_db_table objekt erstellen
        Rennstrecke_db_table rennstrecke_db_table = new Rennstrecke_db_table();

        // sql ausdruck erstellen
        String sql_expression = "INSERT INTO " + rennstrecke_db_table.TABELLENNAME + " (" +
                rennstrecke_db_table.SPALTENNAME_NAME + ", " +
                rennstrecke_db_table.SPALTENNAME_IS_STANDARTSTRECKE + ", " +
                rennstrecke_db_table.SPALTENNAME_BREITE + ", " +
                rennstrecke_db_table.SPALTENNAME_HOEHE + ", " +
                rennstrecke_db_table.SPALTENNAME_IS_STARTRICHTUNG_NACH_UNTEN_ODER_RECHTS + ", " +
                rennstrecke_db_table.SPALTENNAME_ANZ_RUNDEN +
                ") VALUES (?, ?, ?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(rennstrecke_db_table.DEFAULT_STRECKEN_RENNSTRECKEN_CSV_PATH)), StandardCharsets.UTF_8))) {

            String line;
            br.readLine(); // Kopfzeile überspringen

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                try (PreparedStatement pstmt = connection.prepareStatement(sql_expression)) {
                    pstmt.setString(1, values[0]);  // name
                    pstmt.setBoolean(2, Boolean.parseBoolean(values[1]));  // isStandardStrecke
                    pstmt.setInt(3, Integer.parseInt(values[2]));  // breite
                    pstmt.setInt(4, Integer.parseInt(values[3]));  // hoehe
                    pstmt.setBoolean(5, Boolean.parseBoolean(values[4]));  // isStartRichtungNachUntenOderRechts
                    pstmt.setInt(6, Integer.parseInt(values[5]));  // anzRunden
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Rennstrecken erfolgreich aus CSV importiert.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startlinien_from_csv(Connection connection) throws SQLException{
        // rennstrecke_db_table objekt erstellen
        Startlinie_db_table startlinie_db_table = new Startlinie_db_table();

        // sql ausdruck erstellen
        String sql_expression = "INSERT INTO " + startlinie_db_table.TABELLENNAME + " (" +
                startlinie_db_table.SPALTENNAME_FK_RENNSTRECKE + ", " +
                startlinie_db_table.SPALTENNAME_X0 + ", " +
                startlinie_db_table.SPALTENNAME_Y0 + ", " +
                startlinie_db_table.SPALTENNAME_X1 + ", " +
                startlinie_db_table.SPALTENNAME_Y1 +
                ") VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(startlinie_db_table.DEFAULT_STRECKEN_STARTLINIEN_CSV_PATH)), StandardCharsets.UTF_8))) {

            String line;
            br.readLine(); // Kopfzeile überspringen

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                try (PreparedStatement pstmt = connection.prepareStatement(sql_expression)) {
                    pstmt.setString(1, values[0]);  // fk_rennstrecke
                    pstmt.setDouble(2, Double.parseDouble(values[1])); // x0
                    pstmt.setDouble(3, Double.parseDouble(values[2])); // y0
                    pstmt.setDouble(4, Double.parseDouble(values[3])); // x1
                    pstmt.setDouble(5, Double.parseDouble(values[4])); // y1
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Startlinien erfolgreich aus CSV importiert.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void streckenlinien_from_csv(Connection connection) throws SQLException{
        // rennstrecke_db_table objekt erstellen
        Streckenlinie_db_table streckenlinie_db_table = new Streckenlinie_db_table();

        // sql ausdruck erstellen
        String sql_expression = "INSERT INTO " + streckenlinie_db_table.TABELLENNAME + " (" +
                streckenlinie_db_table.SPALTENNAME_FK_RENNSTRECKE + ", " +
                streckenlinie_db_table.SPALTENNAME_X0 + ", " +
                streckenlinie_db_table.SPALTENNAME_Y0 + ", " +
                streckenlinie_db_table.SPALTENNAME_X1 + ", " +
                streckenlinie_db_table.SPALTENNAME_Y1 +
                ") VALUES (?, ?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream(streckenlinie_db_table.DEFAULT_STRECKEN_STRECKENLINIEN_CSV_PATH)), StandardCharsets.UTF_8))) {

            String line;
            br.readLine(); // Kopfzeile überspringen

            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");

                try (PreparedStatement pstmt = connection.prepareStatement(sql_expression)) {
                    pstmt.setString(1, values[0]);  // fk_rennstrecke
                    pstmt.setDouble(2, Double.parseDouble(values[1])); // x0
                    pstmt.setDouble(3, Double.parseDouble(values[2])); // y0
                    pstmt.setDouble(4, Double.parseDouble(values[3])); // x1
                    pstmt.setDouble(5, Double.parseDouble(values[4])); // y1
                    pstmt.executeUpdate();
                }
            }

            System.out.println("Startlinien erfolgreich aus CSV importiert.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}