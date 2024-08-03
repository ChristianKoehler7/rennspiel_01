package de.christian_koehler_iu.rennspiel.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
        }
    }
}