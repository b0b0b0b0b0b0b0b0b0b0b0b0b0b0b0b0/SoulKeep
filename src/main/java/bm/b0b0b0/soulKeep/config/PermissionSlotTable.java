package bm.b0b0b0.soulKeep.config;

import org.bukkit.entity.Player;

import java.util.Map;

public final class PermissionSlotTable {

    private static final int FALLBACK_SLOTS = 1;

    private final Map<String, Integer> slotsByPermission;

    public PermissionSlotTable(SoulKeepSettings settings) {
        this.slotsByPermission = Map.copyOf(settings.permissionSlots);
    }

    public int resolveMaxSlots(Player player) {
        int maxSlots = FALLBACK_SLOTS;
        for (Map.Entry<String, Integer> entry : slotsByPermission.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                maxSlots = Math.max(maxSlots, entry.getValue());
            }
        }
        return maxSlots;
    }
}
