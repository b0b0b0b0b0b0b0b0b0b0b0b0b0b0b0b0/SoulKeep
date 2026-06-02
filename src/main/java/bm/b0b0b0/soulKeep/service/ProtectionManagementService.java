package bm.b0b0b0.soulKeep.service;

import bm.b0b0b0.soulKeep.config.PermissionSlotTable;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public final class ProtectionManagementService {

    private final PlayerProtectionRepository repository;
    private final PermissionSlotTable permissionSlots;
    private final ChanceCalculationService chanceService;
    private final MessageService messages;
    private final SoulKeepLog log;

    public ProtectionManagementService(
            PlayerProtectionRepository repository,
            PermissionSlotTable permissionSlots,
            ChanceCalculationService chanceService,
            MessageService messages,
            SoulKeepLog log) {
        this.repository = repository;
        this.permissionSlots = permissionSlots;
        this.chanceService = chanceService;
        this.messages = messages;
        this.log = log;
    }

    public AddResult tryAdd(Player player, Material material) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            log.warn(player, "add " + material.name() + ": data not ready");
            return AddResult.DATA_NOT_READY;
        }
        PlayerProtectionData data = dataOptional.get();
        int max = permissionSlots.resolveMaxSlots(player);
        if (data.isProtected(material)) {
            log.info(player, "add " + material.name() + ": already protected");
            return AddResult.ALREADY_PROTECTED;
        }
        if (data.getProtectedCount() >= max) {
            log.info(player, "add " + material.name() + ": limit reached (" + max + ")");
            return AddResult.LIMIT_REACHED;
        }
        data.add(material);
        repository.saveAsync(data);
        log.info(player, "add " + material.name() + ": ok (" + data.getProtectedCount() + "/" + max + ") order=" + formatOrder(data));
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
        int max = permissionSlots.resolveMaxSlots(player);
        if (slotIndex > data.getProtectedCount()) {
            log.warn(player, "add " + material.name() + " at slot " + slotIndex + " skipped (count=" + data.getProtectedCount() + ")");
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
        log.info(player, "remove slot " + slotIndex + " " + material.name() + ", order=" + formatOrder(data));
        messages.send(player, "protection.removed", Map.of("material", formatMaterial(material)));
        return RemoveResult.SUCCESS;
    }

    public RemoveResult tryRemove(Player player, Material material) {
        Optional<PlayerProtectionData> dataOptional = requireData(player);
        if (dataOptional.isEmpty()) {
            log.warn(player, "remove " + material.name() + ": data not ready");
            return RemoveResult.DATA_NOT_READY;
        }
        PlayerProtectionData data = dataOptional.get();
        if (!data.isProtected(material)) {
            log.info(player, "remove " + material.name() + ": not protected");
            return RemoveResult.NOT_PROTECTED;
        }
        data.remove(material);
        repository.saveAsync(data);
        log.info(player, "remove " + material.name() + ": ok");
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
        log.info(player, "clear protection list");
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

    private static String formatOrder(PlayerProtectionData data) {
        return data.getProtectedMaterials().toString();
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
