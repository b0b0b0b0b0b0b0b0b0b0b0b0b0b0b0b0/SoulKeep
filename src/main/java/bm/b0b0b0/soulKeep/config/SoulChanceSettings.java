package bm.b0b0b0.soulKeep.config;

import org.bukkit.Material;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class SoulChanceSettings {

    private final double defaultChance;
    private final Map<Material, Double> itemOverrides;

    public SoulChanceSettings(SoulKeepSettings settings) {
        this.defaultChance = settings.settings.defaultChance;
        this.itemOverrides = parseItemOverrides(settings.itemOverrides);
    }

    public double resolveBaseChance(Material material) {
        return itemOverrides.getOrDefault(material, defaultChance);
    }

    private static Map<Material, Double> parseItemOverrides(Map<String, Double> raw) {
        EnumMap<Material, Double> overrides = new EnumMap<>(Material.class);
        for (Map.Entry<String, Double> entry : raw.entrySet()) {
            Material material = Material.matchMaterial(entry.getKey());
            if (material == null || !material.isItem()) {
                continue;
            }
            overrides.put(material, entry.getValue());
        }
        return Collections.unmodifiableMap(overrides);
    }
}
