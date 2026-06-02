package bm.b0b0b0.soulKeep.config;

import org.bukkit.entity.Player;

public final class PermissionBoostTable {

    private final String prefix;
    private final int min;
    private final int max;

    public PermissionBoostTable(SoulKeepSettings settings) {
        SoulKeepSettings.BoostPermissionSection section = settings.permissions.boost;
        this.prefix = section.prefix;
        this.min = section.min;
        this.max = section.max;
    }

    public double resolveMaxBoost(Player player) {
        double maxBoost = 0.0;
        int from = Math.min(min, max);
        int to = Math.max(min, max);
        for (int i = from; i <= to; i++) {
            if (player.hasPermission(prefix + i)) {
                maxBoost = Math.max(maxBoost, i);
            }
        }
        return maxBoost;
    }
}
