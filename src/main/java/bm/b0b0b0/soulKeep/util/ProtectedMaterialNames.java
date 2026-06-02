package bm.b0b0b0.soulKeep.util;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class ProtectedMaterialNames {

    private ProtectedMaterialNames() {
    }

    public static List<Material> dedupeOrdered(List<Material> materials) {
        List<Material> unique = new ArrayList<>(materials.size());
        for (Material material : materials) {
            if (material == null || material.isAir()) {
                continue;
            }
            if (!unique.contains(material)) {
                unique.add(material);
            }
        } 
        return unique;
    }
}
