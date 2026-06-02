package bm.b0b0b0.soulKeep.model;

import org.bukkit.Material;

import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public final class PlayerProtectionData {

    private final UUID playerId;
    private final Set<Material> protectedMaterials;

    public PlayerProtectionData(UUID playerId) {
        this.playerId = Objects.requireNonNull(playerId);
        this.protectedMaterials = new LinkedHashSet<>();
    }

    public PlayerProtectionData(UUID playerId, Set<Material> materials) {
        this.playerId = Objects.requireNonNull(playerId);
        this.protectedMaterials = new LinkedHashSet<>(materials);
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public Set<Material> getProtectedMaterials() {
        return Set.copyOf(protectedMaterials);
    }

    public int getProtectedCount() {
        return protectedMaterials.size();
    }

    public boolean isProtected(Material material) {
        return protectedMaterials.contains(material);
    }

    public void add(Material material) {
        protectedMaterials.add(material);
    }

    public void remove(Material material) {
        protectedMaterials.remove(material);
    }

    public void clear() {
        protectedMaterials.clear();
    }
}
