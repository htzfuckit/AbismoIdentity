package com.htz.abismo.core;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {

    private final JavaPlugin plugin;
    private FileConfiguration config;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public void reload() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public String getDatabasePath() {
        return config.getString("database.path", "plugins/AbismoIdentity/data/identity.db");
    }

    public int getMaxNickLength() {
        return config.getInt("nick.max-length", 16);
    }

    public int getMinNickLength() {
        return config.getInt("nick.min-length", 3);
    }

    public java.util.List<String> getReservedWords() {
        return config.getStringList("nick.reserved-words");
    }

    public String getDisallowedCharacters() {
        return config.getString("nick.disallowed-characters", "[]{}()*!@#$%^&+=|;:',<>?/`~");
    }

    public int getSkinCacheDuration() {
        return config.getInt("skin.cache-duration", 3600);
    }

    public int getCacheRefreshInterval() {
        return config.getInt("cache.refresh-interval", 300);
    }

    public boolean isChatPrefixFormatEnabled() {
        return config.getBoolean("chat.enable-adventure", true);
    }

    public boolean isMinimessageEnabled() {
        return config.getBoolean("chat.enable-minimessage", true);
    }

    public boolean isTabUpdateOnNickChange() {
        return config.getBoolean("tab.update-on-nick-change", true);
    }

    public boolean isTabUpdateDisplayName() {
        return config.getBoolean("tab.update-display-name", true);
    }

    public boolean isTabUpdateNametag() {
        return config.getBoolean("tab.update-nametag", true);
    }

    public boolean isPlaceholderAPIEnabled() {
        return config.getBoolean("placeholderapi.enabled", true);
    }

    public int getHikariMaxLifetime() {
        return config.getInt("database.connection-pool.max-lifetime", 1800000);
    }

    public int getHikariConnectionTimeout() {
        return config.getInt("database.connection-pool.connection-timeout", 30000);
    }

    public int getHikariIdleTimeout() {
        return config.getInt("database.connection-pool.idle-timeout", 600000);
    }

    public int getHikariMaxPoolSize() {
        return config.getInt("database.connection-pool.maximum-pool-size", 10);
    }

    public int getHikariMinimumIdle() {
        return config.getInt("database.connection-pool.minimum-idle", 2);
    }
}