package bm.b0b0b0.soulKeep.repository;

import bm.b0b0b0.soulKeep.database.AsyncDatabaseExecutor;
import bm.b0b0b0.soulKeep.database.PlayerProtectionDao;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

public final class PlayerProtectionRepository {

    private final JavaPlugin plugin;
    private final PlayerProtectionDao dao;
    private final AsyncDatabaseExecutor asyncExecutor;
    private final Map<UUID, PlayerProtectionData> cache = new ConcurrentHashMap<>();
    private final Set<UUID> loading = ConcurrentHashMap.newKeySet();

    public PlayerProtectionRepository(
            JavaPlugin plugin,
            PlayerProtectionDao dao,
            AsyncDatabaseExecutor asyncExecutor) {
        this.plugin = plugin;
        this.dao = dao;
        this.asyncExecutor = asyncExecutor;
    }

    public void loadAsync(UUID playerId) {
        if (cache.containsKey(playerId) || !loading.add(playerId)) {
            return;
        }
        asyncExecutor.supply(() -> dao.load(playerId))
                .whenComplete((data, error) -> {
                    loading.remove(playerId);
                    if (error != null) {
                        plugin.getLogger().log(Level.SEVERE, "Failed to load player " + playerId, error);
                        cache.putIfAbsent(playerId, new PlayerProtectionData(playerId));
                        return;
                    }
                    cache.put(playerId, data);
                });
    }

    public void unloadAsync(UUID playerId) {
        PlayerProtectionData data = cache.remove(playerId);
        loading.remove(playerId);
        if (data == null) {
            return;
        }
        asyncExecutor.run(() -> {
            try {
                dao.save(data);
            } catch (Exception exception) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save player on quit " + playerId, exception);
            }
        });
    }

    public Optional<PlayerProtectionData> findCached(UUID playerId) {
        return Optional.ofNullable(cache.get(playerId));
    }

    public boolean isReady(UUID playerId) {
        return cache.containsKey(playerId);
    }

    public void saveAsync(PlayerProtectionData data) {
        cache.put(data.getPlayerId(), data);
        asyncExecutor.run(() -> {
            try {
                dao.save(data);
            } catch (Exception exception) {
                plugin.getLogger().log(Level.SEVERE, "Failed to save player " + data.getPlayerId(), exception);
            }
        });
    }

    public void flushAllSync() {
        for (PlayerProtectionData data : cache.values()) {
            try {
                dao.save(data);
            } catch (Exception exception) {
                plugin.getLogger().log(Level.SEVERE, "Failed to flush player " + data.getPlayerId(), exception);
            }
        }
        cache.clear();
        loading.clear();
    }
}
