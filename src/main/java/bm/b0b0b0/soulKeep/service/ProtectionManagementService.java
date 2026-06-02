package bm.b0b0b0.soulKeep.service;

import bm.b0b0b0.soulKeep.config.PermissionSlotTable;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public final class ProtectionManagementService {

    private final PlayerProtectionRepository repository;
    private final PermissionSlotTable permissionSlots;
    private final ChanceCalculationService chanceService;
    private final MessageService messages;

    public ProtectionManagementService(
            PlayerProtectionRepository repository,
            PermissionSlotTable permissionSlots,
            ChanceCalculationService chanceService,
            MessageService messages) {
        this.repository = repository;
        this.permissionSlots = permissionSlots;
        this.chanceService = chanceService;
        this.messages = messages;
    }

    public AddResult tryAdd(Player player, Material material) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            return AddResult.DATA_NOT_READY;
        }
        PlayerProtectionData data = dataOptional.get();
        int max = permissionSlots.resolveMaxSlots(player);
        if (data.isProtected(material)) {
            return AddResult.ALREADY_PROTECTED;
        }
        if (data.getProtectedCount() >= max) {
            return AddResult.LIMIT_REACHED;
        }
        if (!data.add(material)) {
            return AddResult.ALREADY_PROTECTED;
        }
        repository.saveAsync(data);
        messages.send(player, "protection.added", Map.of(
                "material", formatMaterial(material),
                "current", String.valueOf(data.getProtectedCount()),
                "max", String.valueOf(max)));
        return AddResult.SUCCESS;
    }

    public AddResult tryAddAtSlot(Player player, Material material, int slotIndex) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            return AddResult.DATA_NOT_READY;
        }
        PlayerProtectionData data = dataOptional.get();
        if (slotIndex > data.getProtectedCount()) {
            messages.send(player, "protection.fill-order");
            return AddResult.SLOT_OUT_OF_ORDER;
        }
        if (slotIndex < data.getProtectedCount()) {
            return AddResult.SLOT_OCCUPIED;
        }
        return tryAdd(player, material);
    }

    public RemoveResult tryRemoveAtSlot(Player player, int slotIndex) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            return RemoveResult.DATA_NOT_READY;
        }
        PlayerProtectionData data = dataOptional.get();
        if (slotIndex < 0 || slotIndex >= data.getProtectedCount()) {
            return RemoveResult.NOT_PROTECTED;
        }
        Material material = data.getAt(slotIndex);
        data.removeAt(slotIndex);
        repository.saveAsync(data);
        messages.send(player, "protection.removed", Map.of("material", formatMaterial(material)));
        return RemoveResult.SUCCESS;
    }

    public RemoveResult tryRemove(Player player, Material material) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            return RemoveResult.DATA_NOT_READY;
        }
        PlayerProtectionData data = dataOptional.get();
        if (!data.isProtected(material)) {
            return RemoveResult.NOT_PROTECTED;
        }
        data.remove(material);
        repository.saveAsync(data);
        messages.send(player, "protection.removed", Map.of("material", formatMaterial(material)));
        return RemoveResult.SUCCESS;
    }

    public void list(Player player) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            return;
        }
        PlayerProtectionData data = dataOptional.get();
        int max = permissionSlots.resolveMaxSlots(player);
        if (data.getProtectedCount() == 0) {
            messages.send(player, "protection.list-empty");
            return;
        }
        messages.send(player, "protection.list-header", Map.of(
                "current", String.valueOf(data.getProtectedCount()),
                "max", String.valueOf(max)));
        for (Material material : data.getProtectedMaterials()) {
            messages.send(player, "protection.list-entry", Map.of(
                    "material", formatMaterial(material),
                    "chance", chanceService.formatChance(player, material)));
        }
    }

    public void clear(Player player) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            return;
        }
        PlayerProtectionData data = dataOptional.get();
        data.clear();
        repository.saveAsync(data);
        messages.send(player, "protection.cleared");
    }

    public void sendLimitMessage(Player player) {
        int max = permissionSlots.resolveMaxSlots(player);
        messages.send(player, "protection.limit-reached", Map.of("max", String.valueOf(max)));
    }

    public void sendAlreadyProtected(Player player, Material material) {
        messages.send(player, "protection.already-protected", Map.of("material", formatMaterial(material)));
    }

    public void sendNotProtected(Player player, Material material) {
        messages.send(player, "protection.not-protected", Map.of("material", formatMaterial(material)));
    }

    public Optional<PlayerProtectionData> findData(Player player) {
        return requireData(player);
    }

    private Optional<PlayerProtectionData> requireData(Player player) {
        if (!repository.isReady(player.getUniqueId())) {
            messages.send(player, "protection.data-loading");
            return Optional.empty();
        }
        return repository.findCached(player.getUniqueId());
    }

    private static String formatMaterial(Material material) {
        return material.name();
    }

    public enum AddResult {
        SUCCESS,
        ALREADY_PROTECTED,
        LIMIT_REACHED,
        DATA_NOT_READY,
        SLOT_OUT_OF_ORDER,
        SLOT_OCCUPIED
    }

    public enum RemoveResult {
        SUCCESS,
        NOT_PROTECTED,
        DATA_NOT_READY
    }
}
