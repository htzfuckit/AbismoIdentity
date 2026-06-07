package com.htz.abismo;

import com.htz.abismo.core.ConfigManager;
import com.htz.abismo.core.DatabaseManager;
import com.htz.abismo.core.MessageManager;
import com.htz.abismo.core.ServiceRegistry;
import com.htz.abismo.identity.IdentityService;
import com.htz.abismo.identity.NickManager;
import com.htz.abismo.identity.SkinManager;
import com.htz.abismo.commands.NickCommand;
import com.htz.abismo.commands.UnnickCommand;
import com.htz.abismo.commands.RandomNickCommand;
import com.htz.abismo.commands.SkinCommand;
import com.htz.abismo.listeners.ChatListener;
import com.htz.abismo.listeners.PlayerJoinListener;
import com.htz.abismo.listeners.PlayerQuitListener;
import org.bukkit.plugin.java.JavaPlugin;

public class AbismoIdentity extends JavaPlugin {

    private static AbismoIdentity instance;
    private ServiceRegistry serviceRegistry;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private DatabaseManager databaseManager;
    private IdentityService identityService;

    @Override
    public void onLoad() {
        instance = this;
    }

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();

        // Load configurations
        saveDefaultConfig();
        saveResource("messages.yml", false);

        configManager = new ConfigManager(this);
        messageManager = new MessageManager(this);
        databaseManager = new DatabaseManager(this);

        // Initialize database
        if (!databaseManager.initialize()) {
            getLogger().severe("Failed to initialize database!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize service registry
        serviceRegistry = new ServiceRegistry();
        identityService = new IdentityService(databaseManager);
        NickManager nickManager = new NickManager(databaseManager, identityService);
        SkinManager skinManager = new SkinManager(databaseManager, identityService);

        serviceRegistry.register(IdentityService.class, identityService);
        serviceRegistry.register(NickManager.class, nickManager);
        serviceRegistry.register(SkinManager.class, skinManager);
        serviceRegistry.register(ConfigManager.class, configManager);
        serviceRegistry.register(MessageManager.class, messageManager);
        serviceRegistry.register(DatabaseManager.class, databaseManager);

        // Register commands
        new NickCommand(this, nickManager);
        new UnnickCommand(this, nickManager);
        new RandomNickCommand(this, nickManager);
        new SkinCommand(this, skinManager);

        // Register listeners
        getServer().getPluginManager().registerEvents(new ChatListener(identityService), this);
        getServer().getPluginManager().registerEvents(new PlayerJoinListener(identityService), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(identityService), this);

        // Try to register PlaceholderAPI expansion (optional)
        tryRegisterPlaceholders();

        long duration = System.currentTimeMillis() - startTime;
        getLogger().info("AbismoIdentity enabled in " + duration + "ms!");
    }

    private void tryRegisterPlaceholders() {
        try {
            if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
                Class<?> placeholderClass = Class.forName("com.htz.abismo.placeholders.AbismoPlaceholder");
                Object placeholder = placeholderClass.getConstructor(IdentityService.class).newInstance(identityService);
                placeholderClass.getMethod("register").invoke(placeholder);
                getLogger().info("PlaceholderAPI expansion registered!");
            }
        } catch (Exception e) {
            getLogger().fine("PlaceholderAPI not available or failed to register expansion");
        }
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.shutdown();
        }
        getLogger().info("AbismoIdentity disabled!");
    }

    public static AbismoIdentity getInstance() {
        return instance;
    }

    public ServiceRegistry getServiceRegistry() {
        return serviceRegistry;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public IdentityService getIdentityService() {
        return identityService;
    }
}
