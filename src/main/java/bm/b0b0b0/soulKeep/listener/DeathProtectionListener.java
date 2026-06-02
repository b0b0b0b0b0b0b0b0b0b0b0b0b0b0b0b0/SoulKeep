package bm.b0b0b0.soulKeep.listener;

import bm.b0b0b0.soulKeep.service.DeathProtectionService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public final class DeathProtectionListener implements Listener {

    private final DeathProtectionService deathProtectionService;

    public DeathProtectionListener(DeathProtectionService deathProtectionService) {
        this.deathProtectionService = deathProtectionService;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        deathProtectionService.handleDeath(event);
    }
}
