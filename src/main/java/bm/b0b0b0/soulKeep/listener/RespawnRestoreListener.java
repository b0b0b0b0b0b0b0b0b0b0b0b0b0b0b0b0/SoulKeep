package bm.b0b0b0.soulKeep.listener;

import bm.b0b0b0.soulKeep.service.DeathProtectionService;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

public final class RespawnRestoreListener implements Listener {

    private final JavaPlugin plugin;
    private final DeathProtectionService deathProtectionService;

    public RespawnRestoreListener(JavaPlugin plugin, DeathProtectionService deathProtectionService) {
        this.plugin = plugin;
        this.deathProtectionService = deathProtectionService;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }
                deathProtectionService.deliverPending(player);
            }
        }.runTask(plugin);
    }
}
