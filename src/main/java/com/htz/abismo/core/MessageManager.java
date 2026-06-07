package com.htz.abismo.core;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {

    private final JavaPlugin plugin;
    private YamlConfiguration messages;
    private final String prefix;

    public MessageManager(JavaPlugin plugin) {
        this.plugin = plugin;
        loadMessages();
        this.prefix = messages.getString("prefix", "§8[§bAbismo§8]§r");
    }

    private void loadMessages() {
        File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public void reload() {
        loadMessages();
    }

    public String get(String path, Map<String, String> placeholders) {
        String message = messages.getString(path, "");
        if (message.isEmpty()) {
            return "§cMessage not found: " + path;
        }

        message = message.replace("{prefix}", prefix);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                message = message.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        return message;
    }

    public String get(String path) {
        return get(path, new HashMap<>());
    }

    public String getNickSuccess(String nickname) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("nickname", nickname);
        return get("commands.nick.success", placeholders);
    }

    public String getNickRemoved() {
        return get("commands.nick.removed");
    }

    public String getNickAlreadyTaken(String nickname) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("nickname", nickname);
        return get("commands.nick.already-taken", placeholders);
    }

    public String getNickInvalidCharacters() {
        return get("commands.nick.invalid-characters");
    }

    public String getNickTooLong(int maxLength) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("max-length", String.valueOf(maxLength));
        return get("commands.nick.too-long", placeholders);
    }

    public String getNickTooShort(int minLength) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("min-length", String.valueOf(minLength));
        return get("commands.nick.too-short", placeholders);
    }

    public String getNickReserved(String nickname) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("nickname", nickname);
        return get("commands.nick.reserved", placeholders);
    }

    public String getNoNick() {
        return get("commands.nick.no-nick");
    }

    public String getNickError() {
        return get("commands.nick.error");
    }

    public String getSkinSuccess(String skin) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("skin", skin);
        return get("commands.skin.success", placeholders);
    }

    public String getSkinInvalidPlayer(String player) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("player", player);
        return get("commands.skin.invalid-player", placeholders);
    }

    public String getSkinError() {
        return get("commands.skin.error");
    }

    public String getRandomNickSuccess(String nickname) {
        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("nickname", nickname);
        return get("commands.randomnick.success", placeholders);
    }

    public String getRandomNickNoAvailable() {
        return get("commands.randomnick.no-available");
    }

    public String getRandomNickError() {
        return get("commands.randomnick.error");
    }
}