package bm.b0b0b0.soulKeep.database;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class ProtectedMaterialCodec {

    private static final String SEPARATOR = ",";

    private ProtectedMaterialCodec() {
    }

    public static String encode(Set<Material> materials) {
        if (materials.isEmpty()) {
            return "";
        }
        List<String> names = new ArrayList<>(materials.size());
        for (Material material : materials) {
            names.add(material.name());
        }
        return String.join(SEPARATOR, names);
    }

    public static Set<Material> decode(String encoded) {
        Set<Material> materials = new LinkedHashSet<>();
        if (encoded == null || encoded.isBlank()) {
            return materials;
        }
        for (String name : encoded.split(SEPARATOR)) {
            if (name.isBlank()) {
                continue;
            }
            Material material = Material.matchMaterial(name.trim());
            if (material != null && material.isItem()) {
                materials.add(material);
            }
        }
        return materials;
    }
}
