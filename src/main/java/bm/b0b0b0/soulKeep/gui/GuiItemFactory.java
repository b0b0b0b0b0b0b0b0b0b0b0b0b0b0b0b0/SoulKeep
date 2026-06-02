package bm.b0b0b0.soulKeep.gui;

import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.util.MaterialParser;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

public final class GuiItemFactory {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final MessageService messages;

    public GuiItemFactory(MessageService messages) {
        this.messages = messages;
    }

    public ItemStack filler(String materialName) {
        return displayItem(materialName, "gui.filler-name", Map.of());
    }

    public ItemStack emptySlot(String materialName) {
        return displayItem(materialName, "gui.empty-slot-name", Map.of());
    }

    public ItemStack lockedSlot(String materialName) {
        return displayItem(materialName, "gui.locked-slot-name", "gui.locked-slot-lore", Map.of());
    }

    public ItemStack protectedType(Material material, String chanceText) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(LEGACY.deserialize(messages.resolveRaw("gui.protected-slot-name", Map.of(
                "material", material.name()))));
        meta.lore(List.of(LEGACY.deserialize(messages.resolveRaw("gui.protected-slot-lore", Map.of(
                "chance", chanceText)))));
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }

    private ItemStack displayItem(String materialName, String namePath, Map<String, String> placeholders) {
        return displayItem(materialName, namePath, null, placeholders);
    }

    private ItemStack displayItem(
            String materialName,
            String namePath,
            String lorePath,
            Map<String, String> placeholders) {
        Material material = MaterialParser.parse(materialName).orElse(Material.STONE);
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.displayName(LEGACY.deserialize(messages.resolveRaw(namePath, placeholders)));
        if (lorePath != null) {
            meta.lore(List.of(LEGACY.deserialize(messages.resolveRaw(lorePath, placeholders))));
        }
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        stack.setItemMeta(meta);
        return stack;
    }
}
