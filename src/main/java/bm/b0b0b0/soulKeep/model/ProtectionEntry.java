package bm.b0b0b0.soulKeep.model;

import org.bukkit.Material;

public record ProtectionEntry(Material material, int amount) {

    public ProtectionEntry {
        if (amount < 1) {
            amount = 1;
        }
    }

    public static ProtectionEntry of(Material material, int amount) {
        return new ProtectionEntry(material, amount);
    }

    public static ProtectionEntry single(Material material) {
        return new ProtectionEntry(material, 1);
    }
}
