package bm.b0b0b0.soulKeep.gui;

import bm.b0b0b0.soulKeep.config.GuiSettings;
import bm.b0b0b0.soulKeep.util.MaterialParser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public final class GuiItemFactory {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final GuiSettings guiSettings;

    public GuiItemFactory(GuiSettings guiSettings) {
        this.guiSettings = guiSettings;
    }

    public ItemStack filler(String materialName) {
        return displayItem(materialName, guiSettings.getFillerName(), null);
    }

    public ItemStack emptySlot(String materialName) {
        return displayItem(materialName, guiSettings.getEmptySlotName(), null);
    }

    public ItemStack lockedSlot(String materialName) {
        return displayItem(materialName, guiSettings.getLockedSlotName(), guiSettings.getLockedSlotLore());
    }

    public ItemStack infoButton(String materialName) {
        Material material = MaterialParser.parse(materialName).orElse(Material.KNOWLEDGE_BOOK);
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(LEGACY.deserialize(guiSettings.getInfoName()));
        meta.lore(guiSettings.getInfoLore().stream()
                .map(LEGACY::deserialize)
                .toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack protectedSlot(Player viewer, Material material, String chanceText) {
        ItemStack display = ProtectionDisplayStackResolver.resolve(viewer, material);
        ItemMeta meta = display.getItemMeta();
        Map<String, String> placeholders = Map.of(
                "material", material.name(),
                "chance", chanceText);
        meta.displayName(LEGACY.deserialize(guiSettings.getProtectedSlotName(placeholders)));
        meta.lore(List.of(LEGACY.deserialize(guiSettings.getProtectedSlotLore(placeholders))));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        display.setItemMeta(meta);
        return display;
    }

    private ItemStack displayItem(String materialName, String name, String loreLine) {
        Material material = MaterialParser.parse(materialName).orElse(Material.STONE);
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(LEGACY.deserialize(name));
        if (loreLine != null) {
            meta.lore(List.of(LEGACY.deserialize(loreLine)));
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }
}
