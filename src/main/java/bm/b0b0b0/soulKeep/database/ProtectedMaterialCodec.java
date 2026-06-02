package bm.b0b0b0.soulKeep.database;

import bm.b0b0b0.soulKeep.model.ProtectionEntry;
import bm.b0b0b0.soulKeep.util.MaterialParser;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public final class ProtectedMaterialCodec {

    private static final String SEPARATOR = ",";
    private static final String AMOUNT_SEPARATOR = ":";

    private ProtectedMaterialCodec() {
    }

    public static String encode(List<ProtectionEntry> entries) {
        if (entries.isEmpty()) {
            return "";
        }
        List<String> tokens = new ArrayList<>(entries.size());
        for (ProtectionEntry entry : entries) {
            if (entry.amount() > 1) {
                tokens.add(entry.material().name() + AMOUNT_SEPARATOR + entry.amount());
            } else {
                tokens.add(entry.material().name());
            }
        }
        return String.join(SEPARATOR, tokens);
    }

    public static List<ProtectionEntry> decode(String encoded) {
        List<ProtectionEntry> entries = new ArrayList<>();
        if (encoded == null || encoded.isBlank()) {
            return entries;
        }
        for (String token : encoded.split(SEPARATOR)) {
            if (token.isBlank()) {
                continue;
            }
            parseToken(token.trim()).ifPresent(entries::add);
        }
        return dedupe(entries);
    }

    private static java.util.Optional<ProtectionEntry> parseToken(String token) {
        int separator = token.lastIndexOf(AMOUNT_SEPARATOR);
        if (separator > 0) {
            String materialName = token.substring(0, separator);
            String amountText = token.substring(separator + 1);
            try {
                int amount = Integer.parseInt(amountText);
                return MaterialParser.parse(materialName)
                        .map(material -> ProtectionEntry.of(material, amount));
            } catch (NumberFormatException ignored) {
                return java.util.Optional.empty();
            }
        }
        return MaterialParser.parse(token).map(ProtectionEntry::single);
    }

    private static List<ProtectionEntry> dedupe(List<ProtectionEntry> entries) {
        List<ProtectionEntry> unique = new ArrayList<>(entries.size());
        for (ProtectionEntry entry : entries) {
            Material material = entry.material();
            if (material == null || material.isAir()) {
                continue;
            }
            if (unique.stream().noneMatch(existing -> existing.material() == material)) {
                unique.add(entry);
            }
        }
        return unique;
    }
}
