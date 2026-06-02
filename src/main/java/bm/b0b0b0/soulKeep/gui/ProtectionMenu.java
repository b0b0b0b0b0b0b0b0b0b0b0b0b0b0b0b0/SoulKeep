package bm.b0b0b0.soulKeep.gui;

import bm.b0b0b0.soulKeep.config.GuiSettings;
import bm.b0b0b0.soulKeep.config.LockedSlotLabel;
import bm.b0b0b0.soulKeep.config.PermissionSlotTable;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.service.ChanceCalculationService;
import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import bm.b0b0b0.soulKeep.util.TextParser;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public final class ProtectionMenu implements InventoryHolder {

    private final UUID ownerId;
    private final GuiSettings guiSettings;
    private final PermissionSlotTable permissionSlots;
    private final ChanceCalculationService chanceService;
    private final GuiItemFactory itemFactory;
    private final ProtectionManagementService protectionService;
    private final MessageService messages;
    private final Inventory inventory;

    public ProtectionMenu(
            Player owner,
            PlayerProtectionData data,
            GuiSettings guiSettings,
            PermissionSlotTable permissionSlots,
            ChanceCalculationService chanceService,
            GuiItemFactory itemFactory,
            ProtectionManagementService protectionService,
            MessageService messages) {
        this.ownerId = owner.getUniqueId();
        this.guiSettings = guiSettings;
        this.permissionSlots = permissionSlots;
        this.chanceService = chanceService;
        this.itemFactory = itemFactory;
        this.protectionService = protectionService;
        this.messages = messages;
        this.inventory = Bukkit.createInventory(
                this,
                guiSettings.getSize(),
                TextParser.parse(guiSettings.getTitle()));
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
                protectionService.tryRemoveAtSlot(player, logicalIndex);
                redraw(player);
                return;
            }
            ItemStack toAdd = resolveStackToAdd(player, event);
            if (toAdd == null || toAdd.getType().isAir()) {
                messages.send(player, "protection.empty-hand");
                return;
            }
            ProtectionManagementService.AddResult result = protectionService.tryAddAtSlot(player, toAdd, logicalIndex);
            if (result == ProtectionManagementService.AddResult.ALREADY_PROTECTED) {
                protectionService.sendAlreadyProtected(player, toAdd.getType());
            } else if (result == ProtectionManagementService.AddResult.LIMIT_REACHED) {
                protectionService.sendLimitMessage(player);
            }
            redraw(player);
        });
    }

    private ItemStack resolveStackToAdd(Player player, InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (cursor != null && !cursor.getType().isAir()) {
            return cursor.clone();
        }
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        if (!mainHand.getType().isAir()) {
            return mainHand.clone();
        }
        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (!offHand.getType().isAir()) {
            return offHand.clone();
        }
        return null;
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
        for (int logicalIndex = 0; logicalIndex < guiSettings.getDisplaySlotCount(); logicalIndex++) {
            int inventorySlot = guiSettings.getSlotPositions().get(logicalIndex);
            if (logicalIndex >= playerMax) {
                LockedSlotLabel locked = guiSettings.resolveLockedSlot(logicalIndex + 1);
                inventory.setItem(inventorySlot, itemFactory.lockedSlot(guiSettings.getLockedSlotMaterial(), locked));
                continue;
            }
            if (logicalIndex < data.getProtectedCount()) {
                var entry = data.getEntryAt(logicalIndex);
                String chance = chanceService.formatChance(viewer, entry.material());
                inventory.setItem(
                        inventorySlot,
                        itemFactory.protectedSlot(viewer, entry.material(), entry.amount(), chance));
                continue;
            }
            inventory.setItem(inventorySlot, itemFactory.emptySlot(guiSettings.getEmptySlotMaterial()));
        }
        if (guiSettings.isInfoEnabled()) {
            inventory.setItem(
                    guiSettings.getInfoSlot(),
                    itemFactory.infoButton(guiSettings.getInfoMaterial()));
        }
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}
