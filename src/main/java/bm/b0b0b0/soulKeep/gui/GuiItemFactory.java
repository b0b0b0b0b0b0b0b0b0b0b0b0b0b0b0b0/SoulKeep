package bm.b0b0b0.soulKeep.gui;

import bm.b0b0b0.soulKeep.config.GuiSettings;
import bm.b0b0b0.soulKeep.config.LockedSlotLabel;
import bm.b0b0b0.soulKeep.util.MaterialParser;
import bm.b0b0b0.soulKeep.util.TextParser;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public final class GuiItemFactory {

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

    public ItemStack lockedSlot(String materialName, LockedSlotLabel label) {
        return displayItem(materialName, label.name(), label.lore());
    }

    public ItemStack infoButton(String materialName) {
        Material material = MaterialParser.parse(materialName).orElse(Material.KNOWLEDGE_BOOK);
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextParser.parse(guiSettings.getInfoName()));
        meta.lore(guiSettings.getInfoLore().stream()
                .map(TextParser::parse)
                .toList());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    public ItemStack protectedSlot(Player viewer, Material material, int amount, String chanceText) {
        ItemStack display = ProtectionDisplayStackResolver.resolve(viewer, material, amount);
        ItemMeta meta = display.getItemMeta();
        Map<String, String> placeholders = Map.of(
                "material", material.name(),
                "chance", chanceText);
        meta.displayName(TextParser.parse(guiSettings.getProtectedSlotName(placeholders)));
        meta.lore(List.of(TextParser.parse(guiSettings.getProtectedSlotLore(placeholders))));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        display.setItemMeta(meta);
        return display;
    }

    private ItemStack displayItem(String materialName, String name, String loreLine) {
        Material material = MaterialParser.parse(materialName).orElse(Material.STONE);
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(TextParser.parse(name));
        if (loreLine != null) {
            meta.lore(List.of(TextParser.parse(loreLine)));
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }
}
