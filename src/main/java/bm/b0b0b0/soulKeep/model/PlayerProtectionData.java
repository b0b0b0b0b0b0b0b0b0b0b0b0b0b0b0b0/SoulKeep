package bm.b0b0b0.soulKeep.model;

import bm.b0b0b0.soulKeep.util.ProtectedMaterialNames;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public final class PlayerProtectionData {

    private final UUID playerId;
    private final List<Material> protectedMaterials;

    public PlayerProtectionData(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId);
        this.protectedMaterials = new ArrayList<>();
    }

    public PlayerProtectionData(UUID playerId, List<Material> materials) {
        this.playerId = Objects.requireNonNull(playerId);
        this.protectedMaterials = new ArrayList<>(ProtectedMaterialNames.dedupeOrdered(materials));
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public List<Material> getProtectedMaterials() {
        return List.copyOf(protectedMaterials);
    }

    public int getProtectedCount() {
        return protectedMaterials.size();
    }

    public boolean isProtected(Material material) {
        return protectedMaterials.contains(material);
    }

    public Material getAt(int index) {
        return protectedMaterials.get(index);
    }

    public boolean add(Material material) {
        if (protectedMaterials.contains(material)) {
            return false;
        }
        protectedMaterials.add(material);
        return true;
    }

    public void addAt(int index, Material material) {
        protectedMaterials.add(index, material);
    }

    public void removeAt(int index) {
        protectedMaterials.remove(index);
    }

    public void clear() {
        protectedMaterials.clear();
    }
}
