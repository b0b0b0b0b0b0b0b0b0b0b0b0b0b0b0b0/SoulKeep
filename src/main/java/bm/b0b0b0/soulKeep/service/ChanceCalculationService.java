package bm.b0b0b0.soulKeep.service;

import bm.b0b0b0.soulKeep.config.PermissionBoostTable;
import bm.b0b0b0.soulKeep.config.SoulChanceSettings;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public final class ChanceCalculationService {

    private static final double MAX_CHANCE = 100.0;

    private final SoulChanceSettings chanceSettings;
    private final PermissionBoostTable permissionBoosts;

    public ChanceCalculationService(SoulChanceSettings chanceSettings, PermissionBoostTable permissionBoosts) {
        this.chanceSettings = chanceSettings;
        this.permissionBoosts = permissionBoosts;
    }

    public double resolveFinalChance(Player player, Material material) {
        double base = chanceSettings.resolveBaseChance(material);
        double boost = permissionBoosts.resolveMaxBoost(player);
        return Math.min(MAX_CHANCE, base + boost);
    }

    public boolean rollSuccess(Player player, Material material) {
        double chance = resolveFinalChance(player, material);
        return ThreadLocalRandom.current().nextDouble(100.0) < chance;
    }

    public String formatChance(Player player, Material material) {
        double chance = resolveFinalChance(player, material);
        if (chance == Math.rint(chance)) {
            return String.valueOf((int) chance);
        }
        return String.valueOf(chance);
    }
}
