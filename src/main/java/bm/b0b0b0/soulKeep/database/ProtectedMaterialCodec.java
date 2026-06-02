package bm.b0b0b0.soulKeep.database;

import bm.b0b0b0.soulKeep.util.MaterialParser;
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
            MaterialParser.parse(name.trim()).ifPresent(materials::add);
        }
        return materials;
    }
}
