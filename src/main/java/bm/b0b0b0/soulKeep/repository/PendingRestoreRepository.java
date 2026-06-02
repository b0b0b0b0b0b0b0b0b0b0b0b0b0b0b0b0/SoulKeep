package bm.b0b0b0.soulKeep.repository;

import bm.b0b0b0.soulKeep.database.AsyncDatabaseExecutor;
import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;
import bm.b0b0b0.soulKeep.persistence.PendingRestorePersistence;
import bm.b0b0b0.soulKeep.service.InventoryRestoreService;
import bm.b0b0b0.soulKeep.util.PendingRestoreCodec;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
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
    private final SoulKeepLog log;
    private final Map<UUID, List<ItemStack>> memoryBuffer = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> deliverInProgress = new ConcurrentHashMap<>();

    public PendingRestoreRepository(
            JavaPlugin plugin,
            PendingRestorePersistence persistence,
            AsyncDatabaseExecutor asyncExecutor,
            InventoryRestoreService inventoryRestoreService,
            SoulKeepLog log) {
        this.plugin = plugin;
        this.persistence = persistence;
        this.asyncExecutor = asyncExecutor;
        this.inventoryRestoreService = inventoryRestoreService;
        this.log = log;
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
        log.info(playerId, "stage: memory buffer set");
        log.items("stage", clones);
        List<PendingRestoreRecord> records = PendingRestoreCodec.encodeAll(clones);
        asyncExecutor.run(() -> {
            try {
                persistence.saveAll(playerId, records);
                log.info(playerId, "stage: saved " + records.size() + " row(s) to database");
                for (int index = 0; index < records.size(); index++) {
                    log.info(playerId, "stage db row #" + (index + 1) + " type=" + records.get(index).getStorageType().name());
                }
                plugin.getServer().getScheduler().runTask(plugin, onStored);
            } catch (Exception exception) {
                plugin.getLogger().log(Level.SEVERE, "Failed to stage pending restore for " + playerId, exception);
            }
        });
    }

    public void deliverIfPresent(Player player) {
        UUID playerId = player.getUniqueId();
        if (deliverInProgress.putIfAbsent(playerId, Boolean.TRUE) != null) {
            log.info(player, "deliver: skipped, already running");
            return;
        }
        asyncExecutor.run(() -> {
            try {
                List<ItemStack> stacks = pullPending(playerId);
                if (stacks.isEmpty()) {
                    log.info(playerId, "deliver: nothing pending");
                    return;
                }
                log.info(playerId, "deliver: giving " + stacks.size() + " stack(s)");
                log.items("deliver", stacks);
                plugin.getServer().getScheduler().runTask(plugin, () -> {
                    if (!player.isOnline()) {
                        log.info(player, "deliver: aborted, player offline");
                        return;
                    }
                    inventoryRestoreService.giveToPlayer(player, stacks);
                    log.info(player, "deliver: done");
                });
            } finally {
                deliverInProgress.remove(playerId);
            }
        });
    }

    private List<ItemStack> pullPending(UUID playerId) {
        List<ItemStack> fromMemory = takeMemory(playerId);
        try {
            if (!fromMemory.isEmpty()) {
                List<PendingRestoreRecord> cleared = persistence.takeAll(playerId);
                if (!cleared.isEmpty()) {
                    log.info(playerId, "deliver: dropped " + cleared.size() + " duplicate db row(s)");
                }
                return fromMemory;
            }
            return loadFromDatabase(playerId);
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Failed to pull pending restore for " + playerId, exception);
            return List.of();
        }
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

    private List<ItemStack> loadFromDatabase(UUID playerId) throws Exception {
        List<PendingRestoreRecord> records = persistence.takeAll(playerId);
        if (records.isEmpty()) {
            return List.of();
        }
        log.info(playerId, "deliver: loaded " + records.size() + " row(s) from database");
        return PendingRestoreCodec.decodeAll(records);
    }
}
