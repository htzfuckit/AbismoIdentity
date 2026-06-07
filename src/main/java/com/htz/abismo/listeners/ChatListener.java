package com.htz.abismo.listeners;

import com.htz.abismo.identity.IdentityService;
import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import java.util.UUID;

public class ChatListener implements Listener {

    private final IdentityService identityService;

    public ChatListener(IdentityService identityService) {
        this.identityService = identityService;
    }

    @EventHandler
    public void onChat(AsyncChatEvent event) {
        UUID playerUuid = event.getPlayer().getUniqueId();
        String nickname = identityService.getNickname(playerUuid);

        if (nickname != null) {
            Component originalMessage = event.message();
            Component newMessage = Component.text(nickname + ": ").append(originalMessage);
            event.message(newMessage);
        }
    }
}