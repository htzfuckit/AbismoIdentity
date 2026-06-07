package com.htz.abismo.commands;

import com.htz.abismo.AbismoIdentity;
import com.htz.abismo.core.MessageManager;
import com.htz.abismo.identity.SkinManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SkinCommand implements CommandExecutor {

    private final AbismoIdentity plugin;
    private final SkinManager skinManager;
    private final MessageManager messageManager;

    public SkinCommand(AbismoIdentity plugin, SkinManager skinManager) {
        this.plugin = plugin;
        this.skinManager = skinManager;
        this.messageManager = plugin.getMessageManager();
        plugin.getCommand("skin").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /skin <player_name>");
            return true;
        }

        String targetPlayerName = args[0];

        try {
            skinManager.setSkin(player.getUniqueId(), targetPlayerName);
            player.sendMessage(messageManager.getSkinSuccess(targetPlayerName));
        } catch (IllegalArgumentException e) {
            player.sendMessage(messageManager.getSkinInvalidPlayer(targetPlayerName));
        } catch (Exception e) {
            player.sendMessage(messageManager.getSkinError());
            e.printStackTrace();
        }

        return true;
    }
}