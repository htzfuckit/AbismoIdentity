package com.htz.abismo.commands;

import com.htz.abismo.AbismoIdentity;
import com.htz.abismo.core.MessageManager;
import com.htz.abismo.identity.NickManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class RandomNickCommand implements CommandExecutor {

    private final AbismoIdentity plugin;
    private final NickManager nickManager;
    private final MessageManager messageManager;

    public RandomNickCommand(AbismoIdentity plugin, NickManager nickManager) {
        this.plugin = plugin;
        this.nickManager = nickManager;
        this.messageManager = plugin.getMessageManager();
        plugin.getCommand("randomnick").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        String randomNick = nickManager.getRandomNickname();
        if (randomNick == null) {
            player.sendMessage(messageManager.getRandomNickNoAvailable());
            return true;
        }

        try {
            nickManager.setNickname(player.getUniqueId(), randomNick);
            player.sendMessage(messageManager.getRandomNickSuccess(randomNick));
            updatePlayerDisplay(player, randomNick);
        } catch (Exception e) {
            player.sendMessage(messageManager.getRandomNickError());
            e.printStackTrace();
        }

        return true;
    }

    private void updatePlayerDisplay(Player player, String nickname) {
        player.setDisplayName(nickname);

        if (plugin.getConfigManager().isTabUpdateDisplayName()) {
            player.setPlayerListName(nickname);
        }
    }
}