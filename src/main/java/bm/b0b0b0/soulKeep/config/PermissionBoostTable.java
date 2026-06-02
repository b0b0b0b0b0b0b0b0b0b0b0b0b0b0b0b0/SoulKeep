package bm.b0b0b0.soulKeep.config;

import org.bukkit.entity.Player;

import java.util.Map;

public final class PermissionBoostTable {

    private final Map<String, Double> boostsByPermission;

    public PermissionBoostTable(SoulKeepSettings settings) {
        this.boostsByPermission = Map.copyOf(settings.permissionBoosts);
    }

    public double resolveMaxBoost(Player player) {
        double maxBoost = 0.0;
        for (Map.Entry<String, Double> entry : boostsByPermission.entrySet()) {
            if (player.hasPermission(entry.getKey())) {
                maxBoost = Math.max(maxBoost, entry.getValue());
            }
        }
        return maxBoost;
    }
}
