package com.htz.abismo.placeholders;

import com.htz.abismo.identity.IdentityService;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class AbismoPlaceholder extends PlaceholderExpansion {

    private final IdentityService identityService;

    public AbismoPlaceholder(IdentityService identityService) {
        this.identityService = identityService;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "abismo";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Htz";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) {
            return "";
        }

        return switch (params) {
            case "nick" -> {
                String nickname = identityService.getNickname(player.getUniqueId());
                yield nickname != null ? nickname : player.getName();
            }
            case "real_name" -> player.getName();
            case "skin" -> {
                String skin = identityService.getCurrentSkin(player.getUniqueId());
                yield skin != null ? skin : "Default";
            }
            case "is_nicked" -> identityService.isNicked(player.getUniqueId()) ? "Yes" : "No";
            default -> null;
        };
    }
}