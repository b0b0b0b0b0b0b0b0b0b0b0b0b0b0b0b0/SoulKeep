package bm.b0b0b0.soulKeep.gui;

import bm.b0b0b0.soulKeep.config.GuiSettings;
import bm.b0b0b0.soulKeep.config.PermissionSlotTable;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.service.ChanceCalculationService;
import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public final class ProtectionMenu implements InventoryHolder {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final UUID ownerId;
    private final GuiSettings guiSettings;
    private final PermissionSlotTable permissionSlots;
    private final ChanceCalculationService chanceService;
    private final GuiItemFactory itemFactory;
    private final ProtectionManagementService protectionService;
    private final MessageService messages;
    private final SoulKeepLog log;
    private final Inventory inventory;

    public ProtectionMenu(
            Player owner,
            PlayerProtectionData data,
            GuiSettings guiSettings,
            PermissionSlotTable permissionSlots,
            ChanceCalculationService chanceService,
            GuiItemFactory itemFactory,
            ProtectionManagementService protectionService,
            MessageService messages,
            SoulKeepLog log) {
        this.ownerId = owner.getUniqueId();
        this.guiSettings = guiSettings;
        this.permissionSlots = permissionSlots;
        this.chanceService = chanceService;
        this.itemFactory = itemFactory;
        this.protectionService = protectionService;
        this.messages = messages;
        this.log = log;
        this.inventory = Bukkit.createInventory(
                this,
                guiSettings.getSize(),
                LEGACY.deserialize(guiSettings.getTitle()));
        draw(owner, data);
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public boolean isOwner(Player player) {
        return player.getUniqueId().equals(ownerId);
    }

    public void handleClick(Player player, InventoryClickEvent event) {
        if (!isOwner(player)) {
            return;
        }
        int slot = event.getSlot();
        if (!guiSettings.isProtectionSlot(slot)) {
            return;
        }
        int logicalIndex = guiSettings.logicalIndexFor(slot);
        if (logicalIndex < 0) {
            return;
        }
        int playerMax = permissionSlots.resolveMaxSlots(player);
        if (logicalIndex >= playerMax) {
            messages.send(player, "gui.slot-locked");
            return;
        }
        protectionService.findData(player).ifPresent(data -> {
            int count = data.getProtectedCount();
            if (logicalIndex < count) {
                log.info(player, "gui remove index=" + logicalIndex + " invSlot=" + slot);
                protectionService.tryRemoveAtSlot(player, logicalIndex);
                redraw(player);
                return;
            }
            Material toAdd = resolveMaterialToAdd(player, event);
            log.info(player, "gui add click index=" + logicalIndex + " invSlot=" + slot + " material=" + toAdd.name());
            if (toAdd.isAir()) {
                messages.send(player, "protection.empty-hand");
                return;
            }
            ProtectionManagementService.AddResult result = protectionService.tryAddAtSlot(player, toAdd, logicalIndex);
            log.info(player, "gui add result=" + result.name());
            if (result == ProtectionManagementService.AddResult.ALREADY_PROTECTED) {
                protectionService.sendAlreadyProtected(player, toAdd);
            } else if (result == ProtectionManagementService.AddResult.LIMIT_REACHED) {
                protectionService.sendLimitMessage(player);
            }
            redraw(player);
        });
    }

    private Material resolveMaterialToAdd(Player player, InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (cursor != null && !cursor.getType().isAir()) {
            return cursor.getType();
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!mainHand.getType().isAir()) {
            return mainHand.getType();
        }
        return player.getInventory().getItemInOffHand().getType();
    }

    private void redraw(Player player) {
        protectionService.findData(player).ifPresent(data -> draw(player, data));
    }

    private void draw(Player viewer, PlayerProtectionData data) {
        ItemStack filler = itemFactory.filler(guiSettings.getFillerMaterial());
        for (int index = 0; index < inventory.getSize(); index++) {
            inventory.setItem(index, filler);
        }
        int playerMax = permissionSlots.resolveMaxSlots(viewer);
        List<Material> protectedList = data.getProtectedMaterials();
        for (int logicalIndex = 0; logicalIndex < guiSettings.getDisplaySlotCount(); logicalIndex++) {
            int inventorySlot = guiSettings.getSlotPositions().get(logicalIndex);
            if (logicalIndex >= playerMax) {
                inventory.setItem(inventorySlot, itemFactory.lockedSlot(guiSettings.getLockedSlotMaterial()));
                continue;
            }
            if (logicalIndex < protectedList.size()) {
                Material material = protectedList.get(logicalIndex);
                String chance = chanceService.formatChance(viewer, material);
                inventory.setItem(inventorySlot, itemFactory.protectedSlot(viewer, material, chance));
                continue;
            }
            inventory.setItem(inventorySlot, itemFactory.emptySlot(guiSettings.getEmptySlotMaterial()));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
