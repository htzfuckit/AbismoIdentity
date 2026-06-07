package com.htz.abismo.commands;

import com.htz.abismo.AbismoIdentity;
import com.htz.abismo.core.MessageManager;
import com.htz.abismo.identity.NickManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class UnnickCommand implements CommandExecutor {

    private final AbismoIdentity plugin;
    private final NickManager nickManager;
    private final MessageManager messageManager;

    public UnnickCommand(AbismoIdentity plugin, NickManager nickManager) {
        this.plugin = plugin;
        this.nickManager = nickManager;
        this.messageManager = plugin.getMessageManager();
        plugin.getCommand("unnick").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        String currentNick = nickManager.getNickname(player.getUniqueId());
        if (currentNick == null) {
            player.sendMessage(messageManager.getNoNick());
            return true;
        }

        try {
            nickManager.removeNickname(player.getUniqueId());
            player.sendMessage(messageManager.getNickRemoved());
            resetPlayerDisplay(player);
        } catch (Exception e) {
            player.sendMessage(messageManager.getNickError());
            e.printStackTrace();
        }

        return true;
    }

    private void resetPlayerDisplay(Player player) {
        player.setDisplayName(player.getName());
        player.setPlayerListName(player.getName());
    }
}