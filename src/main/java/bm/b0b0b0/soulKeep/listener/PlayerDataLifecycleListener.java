package bm.b0b0b0.soulKeep.listener;

import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.service.DeathProtectionService;
import bm.b0b0b0.soulKeep.store.PendingRestoreStore;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class PlayerDataLifecycleListener implements Listener {

    private final JavaPlugin plugin;
    private final PlayerProtectionRepository repository;
    private final DeathProtectionService deathProtectionService;
    private final PendingRestoreStore pendingRestoreStore;

    public PlayerDataLifecycleListener(
            JavaPlugin plugin,
            PlayerProtectionRepository repository,
            DeathProtectionService deathProtectionService,
            PendingRestoreStore pendingRestoreStore) {
        this.plugin = plugin;
        this.repository = repository;
        this.deathProtectionService = deathProtectionService;
        this.pendingRestoreStore = pendingRestoreStore;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        repository.loadAsync(player.getUniqueId());
        if (!pendingRestoreStore.hasPending(player.getUniqueId())) {
            return;
        }
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!player.isOnline()) {
                    return;
                }
                deathProtectionService.handleRespawn(player);
            }
        }.runTask(plugin);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        repository.unloadAsync(event.getPlayer().getUniqueId());
    }
}
