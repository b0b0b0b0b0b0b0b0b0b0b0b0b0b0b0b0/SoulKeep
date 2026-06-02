package bm.b0b0b0.soulKeep.model;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PlayerProtectionData {

    private final UUID playerId;
    private final List<ProtectionEntry> entries;

    public PlayerProtectionData(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId);
        this.entries = new ArrayList<>();
    }

    public PlayerProtectionData(UUID playerId, List<ProtectionEntry> entries) {
        this.playerId = Objects.requireNonNull(playerId);
        this.entries = new ArrayList<>(dedupeByMaterial(entries));
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public List<ProtectionEntry> getEntries() {
        return List.copyOf(entries);
    }

    public List<Material> getProtectedMaterials() {
        return entries.stream().map(ProtectionEntry::material).toList();
    }

    public int getProtectedCount() {
        return entries.size();
    }

    public boolean isProtected(Material material) {
        for (ProtectionEntry entry : entries) {
            if (entry.material() == material) {
                return true;
            }
        }
        return false;
    }

    public ProtectionEntry getEntryAt(int index) {
        return entries.get(index);
    }

    public Material getMaterialAt(int index) {
        return entries.get(index).material();
    }

    public int getAmountAt(int index) {
        return entries.get(index).amount();
    }

    public boolean add(ProtectionEntry entry) {
        if (isProtected(entry.material())) {
            return false;
        }
        entries.add(entry);
        return true;
    }

    public void removeAt(int index) {
        entries.remove(index);
    }

    public void clear() {
        entries.clear();
    }

    private static List<ProtectionEntry> dedupeByMaterial(List<ProtectionEntry> source) {
        List<ProtectionEntry> unique = new ArrayList<>(source.size());
        for (ProtectionEntry entry : source) {
            if (entry == null || entry.material() == null || entry.material().isAir()) {
                continue;
            }
            if (unique.stream().noneMatch(existing -> existing.material() == entry.material())) {
                unique.add(entry);
            }
        }
        return unique;
    }
}
