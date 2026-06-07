package com.htz.abismo.core;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private final JavaPlugin plugin;
    private final ConfigManager configManager;
    private HikariDataSource dataSource;

    public DatabaseManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.configManager = new ConfigManager(plugin);
    }

    public boolean initialize() {
        try {
            String dbPath = configManager.getDatabasePath();
            File dbFile = new File(dbPath);
            File parentDir = dbFile.getParentFile();

            if (parentDir != null && !parentDir.exists()) {
                if (!parentDir.mkdirs()) {
                    plugin.getLogger().severe("Failed to create database directory!");
                    return false;
                }
            }

            HikariConfig config = new HikariConfig();
            config.setJdbcUrl("jdbc:sqlite:" + dbPath);
            config.setMaxLifetime(configManager.getHikariMaxLifetime());
            config.setConnectionTimeout(configManager.getHikariConnectionTimeout());
            config.setIdleTimeout(configManager.getHikariIdleTimeout());
            config.setMaximumPoolSize(configManager.getHikariMaxPoolSize());
            config.setMinimumIdle(configManager.getHikariMinimumIdle());

            dataSource = new HikariDataSource(config);

            createTables();
            plugin.getLogger().info("Database initialized successfully!");
            return true;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void createTables() throws SQLException {
        try (Connection connection = dataSource.getConnection(); Statement statement = connection.createStatement()) {
            // player_profiles table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS player_profiles (" +
                            "uuid TEXT PRIMARY KEY," +
                            "original_name TEXT NOT NULL," +
                            "current_nickname TEXT," +
                            "current_skin TEXT," +
                            "created_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                            "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP" +
                            ")"
            );

            // nickname_history table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS nickname_history (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "uuid TEXT NOT NULL," +
                            "old_nickname TEXT," +
                            "new_nickname TEXT NOT NULL," +
                            "changed_at DATETIME DEFAULT CURRENT_TIMESTAMP," +
                            "FOREIGN KEY (uuid) REFERENCES player_profiles(uuid)" +
                            ")"
            );

            // nick_pool table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS nick_pool (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "nickname TEXT UNIQUE NOT NULL," +
                            "available BOOLEAN DEFAULT 1" +
                            ")"
            );

            // skin_pool table
            statement.execute(
                    "CREATE TABLE IF NOT EXISTS skin_pool (" +
                            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                            "skin_name TEXT UNIQUE NOT NULL," +
                            "available BOOLEAN DEFAULT 1" +
                            ")"
            );

            // Create indexes
            statement.execute("CREATE INDEX IF NOT EXISTS idx_uuid ON player_profiles(uuid)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_nickname ON player_profiles(current_nickname)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_available_nicks ON nick_pool(available)");
            statement.execute("CREATE INDEX IF NOT EXISTS idx_available_skins ON skin_pool(available)");

            plugin.getLogger().info("Database tables created/verified successfully!");
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource is not initialized or closed");
        }
        return dataSource.getConnection();
    }

    public void shutdown() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
            plugin.getLogger().info("Database connection pool closed!");
        }
    }

    public boolean isConnected() {
        return dataSource != null && !dataSource.isClosed();
    }
}