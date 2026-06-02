package bm.b0b0b0.soulKeep.service;

import bm.b0b0b0.soulKeep.config.PermissionBoostTable;
import bm.b0b0b0.soulKeep.config.SoulChanceSettings;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.ThreadLocalRandom;

public final class ChanceCalculationService {

    private static final double MAX_CHANCE = 100.0;

    private final SoulChanceSettings chanceSettings;
    private final PermissionBoostTable permissionBoosts;
    private final SoulKeepLog log;

    public ChanceCalculationService(
            SoulChanceSettings chanceSettings,
            PermissionBoostTable permissionBoosts,
            SoulKeepLog log) {
        this.chanceSettings = chanceSettings;
        this.permissionBoosts = permissionBoosts;
        this.log = log;
    }

    public double resolveFinalChance(Player player, Material material) {
        double base = chanceSettings.resolveBaseChance(material);
        double boost = permissionBoosts.resolveMaxBoost(player);
        return Math.min(MAX_CHANCE, base + boost);
    }

    public boolean rollSuccess(Player player, Material material) {
        double chance = resolveFinalChance(player, material);
        double roll = ThreadLocalRandom.current().nextDouble(100.0);
        boolean success = roll < chance;
        log.info(player, "roll " + material.name() + ": rolled=" + roll + " need<" + chance + " success=" + success);
        return success;
    }

    public String formatChance(Player player, Material material) {
        double chance = resolveFinalChance(player, material);
        if (chance == Math.rint(chance)) {
            return String.valueOf((int) chance);
        }
        return String.valueOf(chance);
    }
}
