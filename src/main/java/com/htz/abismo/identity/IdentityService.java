package com.htz.abismo.identity;

import com.htz.abismo.core.DatabaseManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class IdentityService {

    private final DatabaseManager databaseManager;
    private final Map<UUID, String> nickCache = new ConcurrentHashMap<>();
    private final Map<String, UUID> reverseNickCache = new ConcurrentHashMap<>();
    private final Map<UUID, String> skinCache = new ConcurrentHashMap<>();

    public IdentityService(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public String getNickname(UUID uuid) {
        String cached = nickCache.get(uuid);
        if (cached != null) {
            return cached;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT current_nickname FROM player_profiles WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String nickname = rs.getString("current_nickname");
                if (nickname != null) {
                    nickCache.put(uuid, nickname);
                    reverseNickCache.put(nickname, uuid);
                    return nickname;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setNickname(UUID uuid, String nickname) throws SQLException {
        String oldNick = getNickname(uuid);

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE player_profiles SET current_nickname = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ?")) {
            stmt.setString(1, nickname);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO nickname_history (uuid, old_nickname, new_nickname) VALUES (?, ?, ?)")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, oldNick);
            stmt.setString(3, nickname);
            stmt.executeUpdate();
        }

        if (oldNick != null) {
            reverseNickCache.remove(oldNick);
        }
        nickCache.put(uuid, nickname);
        reverseNickCache.put(nickname, uuid);
    }

    public void removeNickname(UUID uuid) throws SQLException {
        String oldNick = getNickname(uuid);

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE player_profiles SET current_nickname = NULL, updated_at = CURRENT_TIMESTAMP WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT INTO nickname_history (uuid, old_nickname, new_nickname) VALUES (?, ?, ?)")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, oldNick);
            stmt.setString(3, "");
            stmt.executeUpdate();
        }

        if (oldNick != null) {
            reverseNickCache.remove(oldNick);
        }
        nickCache.remove(uuid);
    }

    public UUID getRealPlayer(String nickname) {
        UUID cached = reverseNickCache.get(nickname);
        if (cached != null) {
            return cached;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT uuid FROM player_profiles WHERE current_nickname = ?")) {
            stmt.setString(1, nickname);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("uuid"));
                reverseNickCache.put(nickname, uuid);
                return uuid;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean isNicked(UUID uuid) {
        return getNickname(uuid) != null;
    }

    public String getCurrentSkin(UUID uuid) {
        String cached = skinCache.get(uuid);
        if (cached != null) {
            return cached;
        }

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "SELECT current_skin FROM player_profiles WHERE uuid = ?")) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String skin = rs.getString("current_skin");
                if (skin != null) {
                    skinCache.put(uuid, skin);
                    return skin;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public void setSkin(UUID uuid, String skinName) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "UPDATE player_profiles SET current_skin = ?, updated_at = CURRENT_TIMESTAMP WHERE uuid = ?")) {
            stmt.setString(1, skinName);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        }

        skinCache.put(uuid, skinName);
    }

    public void createProfile(UUID uuid, String originalName) throws SQLException {
        try (Connection connection = databaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(
                     "INSERT OR IGNORE INTO player_profiles (uuid, original_name) VALUES (?, ?)")) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, originalName);
            stmt.executeUpdate();
        }
    }

    public void clearCache(UUID uuid) {
        String nickname = nickCache.remove(uuid);
        if (nickname != null) {
            reverseNickCache.remove(nickname);
        }
        skinCache.remove(uuid);
    }

    public void clearAllCaches() {
        nickCache.clear();
        reverseNickCache.clear();
        skinCache.clear();
    }
}