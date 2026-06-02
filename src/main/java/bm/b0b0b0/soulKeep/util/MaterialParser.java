package bm.b0b0b0.soulKeep.util;

import org.bukkit.Material;

import java.util.Optional;

public final class MaterialParser {

    private MaterialParser() {
    }

    public static Optional<Material> parse(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        Material material = Material.matchMaterial(input.trim());
        if (material == null || !material.isItem()) {
            return Optional.empty();
        }
        return Optional.of(material);
    }
}
