package com.htz.abismo.commands;

import com.htz.abismo.AbismoIdentity;
import com.htz.abismo.core.MessageManager;
import com.htz.abismo.identity.NickManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NickCommand implements CommandExecutor {

    private final AbismoIdentity plugin;
    private final NickManager nickManager;
    private final MessageManager messageManager;

    public NickCommand(AbismoIdentity plugin, NickManager nickManager) {
        this.plugin = plugin;
        this.nickManager = nickManager;
        this.messageManager = plugin.getMessageManager();
        plugin.getCommand("nick").setExecutor(this);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("§cOnly players can use this command!");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage("§cUsage: /nick <name>");
            return true;
        }

        String nickname = args[0];

        if (!nickManager.isValidNickname(nickname)) {
            int maxLength = plugin.getConfigManager().getMaxNickLength();
            player.sendMessage(messageManager.getNickTooLong(maxLength));
            return true;
        }

        if (nickManager.isReservedNickname(nickname)) {
            player.sendMessage(messageManager.getNickReserved(nickname));
            return true;
        }

        if (nickManager.isNicknameTaken(nickname)) {
            player.sendMessage(messageManager.getNickAlreadyTaken(nickname));
            return true;
        }

        try {
            nickManager.setNickname(player.getUniqueId(), nickname);
            player.sendMessage(messageManager.getNickSuccess(nickname));
            updatePlayerDisplay(player, nickname);
        } catch (Exception e) {
            player.sendMessage(messageManager.getNickError());
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