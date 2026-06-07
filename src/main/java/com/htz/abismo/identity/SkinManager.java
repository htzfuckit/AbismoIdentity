package com.htz.abismo.identity;

import com.htz.abismo.core.DatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class SkinManager {

    private final DatabaseManager databaseManager;
    private final IdentityService identityService;

    public SkinManager(DatabaseManager databaseManager, IdentityService identityService) {
        this.databaseManager = databaseManager;
        this.identityService = identityService;
    }

    public void setSkin(UUID uuid, String playerName) throws SQLException {
        Player targetPlayer = Bukkit.getPlayer(playerName);
        if (targetPlayer == null) {
            throw new IllegalArgumentException("Player not found: " + playerName);
        }

        identityService.setSkin(uuid, playerName);
    }

    public String getCurrentSkin(UUID uuid) {
        return identityService.getCurrentSkin(uuid);
    }

    public void addSkinToPool(String skinName) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT OR IGNORE INTO skin_pool (skin_name, available) VALUES (?, 1)")) {
            stmt.setString(1, skinName);
            stmt.executeUpdate();
        }
    }

    public String getRandomSkin() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT skin_name FROM skin_pool WHERE available = 1 ORDER BY RANDOM() LIMIT 1")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("skin_name");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void markSkinUsed(String skinName) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE skin_pool SET available = 0 WHERE skin_name = ?")) {
            stmt.setString(1, skinName);
            stmt.executeUpdate();
        }
    }

    public void markSkinAvailable(String skinName) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE skin_pool SET available = 1 WHERE skin_name = ?")) {
            stmt.setString(1, skinName);
            stmt.executeUpdate();
        }
    }
}