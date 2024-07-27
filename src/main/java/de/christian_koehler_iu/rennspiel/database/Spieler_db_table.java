package de.christian_koehler_iu.rennspiel.database;

import java.sql.*;
import java.util.HashMap;

public class Spieler_db_table {

    public final String TABELLENNAME = "Spieler";
    public final String SPALTENNAME_NAME = "name";

    public String get_create_table_string(){
        return "CREATE TABLE IF NOT EXISTS " + TABELLENNAME + " (" +
                SPALTENNAME_NAME + " TEXT PRIMARY KEY" +
                ");";
    }


    public void savePlayer(Player player) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String insertPlayerSQL = "INSERT INTO Spieler (name) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertPlayerSQL)) {
            pstmt.setString(1, player.getName());
            pstmt.executeUpdate();
        }

        // Save Bestzeiten
        for (String rennstreckeName : player.getStreckenBestzeiten().keySet()) {
            double bestzeit = player.getStreckenBestzeiten().get(rennstreckeName);
            saveBestzeit(player.getName(), rennstreckeName, bestzeit);
        }
    }

    public void saveBestzeit(String playerName, String rennstreckeName, double bestzeit) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String insertBestzeitSQL = "INSERT INTO Link_rennstrecke_spielerBestzeit (fk_spieler, fk_rennstrecke, bestzeit) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(insertBestzeitSQL)) {
            pstmt.setString(1, playerName);
            pstmt.setString(2, rennstreckeName);
            pstmt.setDouble(3, bestzeit);
            pstmt.executeUpdate();
        }
    }

    public Player getPlayer(String name) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String selectPlayerSQL = "SELECT * FROM Spieler WHERE name = ?";
        Player player = null;

        try (PreparedStatement pstmt = connection.prepareStatement(selectPlayerSQL)) {
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                player = new Player(name);
                player.setStreckenBestzeiten(getBestzeiten(name));
            }
        }
        return player;
    }

    public HashMap<String, Double> getBestzeiten(String playerName) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        String selectBestzeitenSQL = "SELECT * FROM Link_rennstrecke_spielerBestzeit WHERE fk_spieler = ?";
        HashMap<String, Double> bestzeiten = new HashMap<>();

        try (PreparedStatement pstmt = connection.prepareStatement(selectBestzeitenSQL)) {
            pstmt.setString(1, playerName);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String rennstreckeName = rs.getString("fk_rennstrecke");
                double bestzeit = rs.getDouble("bestzeit");
                bestzeiten.put(rennstreckeName, bestzeit);
            }
        }
        return bestzeiten;
    }

    // Weitere CRUD-Operationen wie Update und Delete können hier hinzugefügt werden
}
