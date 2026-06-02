package bm.b0b0b0.soulKeep.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;

import java.util.Locale;
import java.util.Optional;

public final class MaterialParser {

    private MaterialParser() {
    }

    public static Optional<Material> parse(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        String trimmed = input.trim();
        Material fromRegistry = fromRegistry(trimmed);
        if (fromRegistry != null) {
            return Optional.of(fromRegistry);
        }
        Material legacy = Material.matchMaterial(trimmed.toUpperCase(Locale.ROOT));
        if (legacy != null && legacy.isItem()) {
            return Optional.of(legacy);
        }
        legacy = Material.matchMaterial(trimmed);
        if (legacy != null && legacy.isItem()) {
            return Optional.of(legacy);
        }
        return Optional.empty();
    }

    private static Material fromRegistry(String input) {
        NamespacedKey key = input.contains(":")
                ? NamespacedKey.fromString(input.toLowerCase(Locale.ROOT))
                : NamespacedKey.minecraft(input.toLowerCase(Locale.ROOT));
        if (key == null) {
            return null;
        }
        Material material = Registry.MATERIAL.get(key);
        if (material == null || !material.isItem()) {
            return null;
        }
        return material;
    }
}
