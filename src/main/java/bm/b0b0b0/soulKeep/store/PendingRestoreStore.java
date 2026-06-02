package bm.b0b0b0.soulKeep.store;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PendingRestoreStore {

    private final Map<UUID, List<ItemStack>> pendingByPlayer = new ConcurrentHashMap<>();

    public void put(UUID playerId, List<ItemStack> stacks) {
        if (stacks.isEmpty()) {
            pendingByPlayer.remove(playerId);
            return;
        }
        List<ItemStack> clones = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            clones.add(stack.clone());
        }
        pendingByPlayer.put(playerId, Collections.unmodifiableList(clones));
    }

    public List<ItemStack> take(UUID playerId) {
        List<ItemStack> stacks = pendingByPlayer.remove(playerId);
        if (stacks == null) {
            return List.of();
        }
        List<ItemStack> clones = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            clones.add(stack.clone());
        }
        return clones;
    }

    public boolean hasPending(UUID playerId) {
        return pendingByPlayer.containsKey(playerId);
    }

    public void clear(UUID playerId) {
        pendingByPlayer.remove(playerId);
    }
}
