package bm.b0b0b0.soulKeep.config;

import org.bukkit.entity.Player;

public final class PermissionSlotTable {

    private final String prefix;
    private final int min;
    private final int max;
    private final int defaultSlots;

    public PermissionSlotTable(SoulKeepSettings settings) {
        SoulKeepSettings.SlotsPermissionSection section = settings.permissions.slots;
        this.prefix = section.prefix;
        this.min = section.min;
        this.max = section.max;
        this.defaultSlots = section.defaultSlots;
    }

    public int resolveMaxSlots(Player player) {
        int maxSlots = defaultSlots;
        int from = Math.min(min, max);
        int to = Math.max(min, max);
        for (int i = from; i <= to; i++) {
            if (player.hasPermission(prefix + i)) {
                maxSlots = Math.max(maxSlots, i);
            }
        }
        return maxSlots;
    }
}
