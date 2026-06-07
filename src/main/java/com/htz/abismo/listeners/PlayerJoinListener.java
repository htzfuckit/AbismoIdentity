package com.htz.abismo.listeners;

import com.htz.abismo.identity.IdentityService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final IdentityService identityService;

    public PlayerJoinListener(IdentityService identityService) {
        this.identityService = identityService;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        String playerName = event.getPlayer().getName();

        try {
            identityService.createProfile(uuid, playerName);

            String nickname = identityService.getNickname(uuid);
            if (nickname != null) {
                event.getPlayer().setDisplayName(nickname);
                event.getPlayer().setPlayerListName(nickname);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}