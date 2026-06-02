package bm.b0b0b0.soulKeep.repository;

import bm.b0b0b0.soulKeep.database.AsyncDatabaseExecutor;
import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;
import bm.b0b0b0.soulKeep.persistence.PendingRestorePersistence;
import bm.b0b0b0.soulKeep.service.InventoryRestoreService;
import bm.b0b0b0.soulKeep.util.PendingRestoreCodec;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class PendingRestoreRepository {

    private final JavaPlugin plugin;
    private final PendingRestorePersistence persistence;
    private final AsyncDatabaseExecutor asyncExecutor;
    private final InventoryRestoreService inventoryRestoreService;
    private final Map<UUID, List<ItemStack>> memoryBuffer = new ConcurrentHashMap<>();

    public PendingRestoreRepository(
            JavaPlugin plugin,
            PendingRestorePersistence persistence,
            AsyncDatabaseExecutor asyncExecutor,
            InventoryRestoreService inventoryRestoreService) {
        this.plugin = plugin;
        this.persistence = persistence;
        this.asyncExecutor = asyncExecutor;
        this.inventoryRestoreService = inventoryRestoreService;
    }

    public void stage(UUID playerId, List<ItemStack> items, Runnable onStored) {
        if (items.isEmpty()) {
            return;
        }
        List<ItemStack> clones = new ArrayList<>(items.size());
        for (ItemStack stack : items) {
            clones.add(stack.clone());
        }
        memoryBuffer.put(playerId, clones);
        List<PendingRestoreRecord> records = PendingRestoreCodec.encodeAll(clones);
        asyncExecutor.run(() -> {
            try {
                persistence.saveAll(playerId, records);
                plugin.getServer().getScheduler().runTask(plugin, onStored);
            } catch (Exception exception) {
                plugin.getLogger().log(Level.SEVERE, "Failed to stage pending restore for " + playerId, exception);
            }
        });
    }

    public void deliverIfPresent(Player player) {
        UUID playerId = player.getUniqueId();
        List<ItemStack> cached = takeMemory(playerId);
        if (!cached.isEmpty()) {
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (!player.isOnline()) {
                    return;
                }
                inventoryRestoreService.giveToPlayer(player, cached);
            });
            asyncExecutor.run(() -> discardDatabase(playerId));
            return;
        }
        asyncExecutor.supply(() -> loadFromDatabase(playerId))
                .thenAccept(stacks -> plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline() || stacks.isEmpty()) {
                        return;
                    }
                    inventoryRestoreService.giveToPlayer(player, stacks);
                }));
    }

    private List<ItemStack> takeMemory(UUID playerId) {
        List<ItemStack> cached = memoryBuffer.remove(playerId);
        if (cached == null || cached.isEmpty()) {
            return List.of();
        }
        List<ItemStack> clones = new ArrayList<>(cached.size());
        for (ItemStack stack : cached) {
            clones.add(stack.clone());
        }
        return clones;
    }

    private void discardDatabase(UUID playerId) {
        try {
            persistence.takeAll(playerId);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to clear duplicate pending rows for " + playerId, exception);
        }
    }

    private List<ItemStack> loadFromDatabase(UUID playerId) {
        try {
            List<PendingRestoreRecord> records = persistence.takeAll(playerId);
            if (records.isEmpty()) {
                return List.of();
            }
            return PendingRestoreCodec.decodeAll(records);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to load pending restore for " + playerId, exception);
            return List.of();
        }
    }
}
