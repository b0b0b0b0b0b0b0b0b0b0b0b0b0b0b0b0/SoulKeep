package bm.b0b0b0.soulKeep.database;

import bm.b0b0b0.soulKeep.util.MaterialParser;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class ProtectedMaterialCodec {

    private static final String SEPARATOR = ",";

    private ProtectedMaterialCodec() {
    }

    public static String encode(List<Material> materials) {
        if (materials.isEmpty()) {
            return "";
        }
        List<String> names = new ArrayList<>(materials.size());
        for (Material material : materials) {
            names.add(material.name());
        }
        return String.join(SEPARATOR, names);
    }

    public static List<Material> decode(String encoded) {
        List<Material> materials = new ArrayList<>();
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
