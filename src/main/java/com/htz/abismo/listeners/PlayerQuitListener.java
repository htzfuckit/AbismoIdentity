package com.htz.abismo.listeners;

import com.htz.abismo.identity.IdentityService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final IdentityService identityService;

    public PlayerQuitListener(IdentityService identityService) {
        this.identityService = identityService;
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        identityService.clearCache(uuid);
    }
}