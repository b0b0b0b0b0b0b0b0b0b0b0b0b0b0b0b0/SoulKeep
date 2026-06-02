package bm.b0b0b0.soulKeep.config;

import bm.b0b0b0.soulKeep.util.MaterialParser;
import org.bukkit.Material;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

public final class SoulChanceSettings {

    private final double defaultChance;
    private final Map<Material, Double> itemOverrides;

    public SoulChanceSettings(SoulKeepSettings settings) {
        this.defaultChance = settings.settings.defaultChance;
        ItemOverridesSection overrides = settings.itemOverrides;
        this.itemOverrides = overrides.enabled
                ? parseItemOverrides(overrides.entries)
                : Collections.emptyMap();
    }

    public double resolveBaseChance(Material material) {
        return itemOverrides.getOrDefault(material, defaultChance);
    }

    private static Map<Material, Double> parseItemOverrides(Map<String, Double> raw) {
        EnumMap<Material, Double> overrides = new EnumMap<>(Material.class);
        for (Map.Entry<String, Double> entry : raw.entrySet()) {
            MaterialParser.parse(entry.getKey()).ifPresent(material ->
                    overrides.put(material, entry.getValue()));
        }
        return Collections.unmodifiableMap(overrides);
    }
}
