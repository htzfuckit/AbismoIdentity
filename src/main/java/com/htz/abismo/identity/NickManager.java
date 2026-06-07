package com.htz.abismo.identity;

import com.htz.abismo.core.ConfigManager;
import com.htz.abismo.core.DatabaseManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class NickManager {

    private final DatabaseManager databaseManager;
    private final IdentityService identityService;
    private final ConfigManager configManager;

    public NickManager(DatabaseManager databaseManager, IdentityService identityService) {
        this.databaseManager = databaseManager;
        this.identityService = identityService;
        this.configManager = new ConfigManager(JavaPlugin.getProvidingPlugin(NickManager.class));
    }

    public boolean isValidNickname(String nickname) {
        if (nickname == null || nickname.isEmpty()) {
            return false;
        }

        int minLength = configManager.getMinNickLength();
        int maxLength = configManager.getMaxNickLength();

        if (nickname.length() < minLength || nickname.length() > maxLength) {
            return false;
        }

        String disallowed = configManager.getDisallowedCharacters();
        for (char c : nickname.toCharArray()) {
            if (disallowed.indexOf(c) != -1) {
                return false;
            }
        }

        return true;
    }

    public boolean isReservedNickname(String nickname) {
        java.util.List<String> reserved = configManager.getReservedWords();
        return reserved.contains(nickname.toLowerCase());
    }

    public boolean isNicknameTaken(String nickname) {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT uuid FROM player_profiles WHERE current_nickname = ? LIMIT 1")) {
            stmt.setString(1, nickname);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void setNickname(UUID uuid, String nickname) throws SQLException {
        identityService.setNickname(uuid, nickname);
    }

    public void removeNickname(UUID uuid) throws SQLException {
        identityService.removeNickname(uuid);
    }

    public String getNickname(UUID uuid) {
        return identityService.getNickname(uuid);
    }

    public String getRandomNickname() {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT nickname FROM nick_pool WHERE available = 1 ORDER BY RANDOM() LIMIT 1")) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("nickname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addNicknameToPool(String nickname) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT OR IGNORE INTO nick_pool (nickname, available) VALUES (?, 1)")) {
            stmt.setString(1, nickname);
            stmt.executeUpdate();
        }
    }

    public void markNicknameUsed(String nickname) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE nick_pool SET available = 0 WHERE nickname = ?")) {
            stmt.setString(1, nickname);
            stmt.executeUpdate();
        }
    }

    public void markNicknameAvailable(String nickname) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE nick_pool SET available = 1 WHERE nickname = ?")) {
            stmt.setString(1, nickname);
            stmt.executeUpdate();
        }
    }
}