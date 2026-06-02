package bm.b0b0b0.soulKeep.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class GuiSettings {

    private final String title;
    private final int rows;
    private final List<Integer> slotPositions;
    private final String fillerMaterial;
    private final String emptySlotMaterial;
    private final String lockedSlotMaterial;
    private final int infoSlot;
    private final String infoMaterial;
    private final String fillerName;
    private final String emptySlotName;
    private final String lockedSlotName;
    private final String lockedSlotLore;
    private final Map<Integer, LockedSlotLabel> lockedSlotTiers;
    private final String protectedSlotName;
    private final String protectedSlotLore;
    private final String infoName;
    private final List<String> infoLore;

    public GuiSettings(GuiMainSettings settings) {
        this.title = settings.title;
        this.rows = settings.rows;
        this.slotPositions = List.copyOf(settings.slotPositions);
        this.fillerMaterial = settings.fillerMaterial;
        this.emptySlotMaterial = settings.emptySlotMaterial;
        this.lockedSlotMaterial = settings.lockedSlotMaterial;
        this.infoSlot = settings.infoSlot;
        this.infoMaterial = settings.infoMaterial;
        this.fillerName = settings.fillerName;
        this.emptySlotName = settings.emptySlotName;
        this.lockedSlotName = settings.lockedSlotName;
        this.lockedSlotLore = settings.lockedSlotLore;
        this.lockedSlotTiers = parseLockedTiers(settings.lockedSlotTiers);
        this.protectedSlotName = settings.protectedSlotName;
        this.protectedSlotLore = settings.protectedSlotLore;
        this.infoName = settings.infoName;
        this.infoLore = List.copyOf(settings.infoLore);
    }

    public String getTitle() {
        return title;
    }

    public int getRows() {
        return rows;
    }

    public int getSize() {
        return rows * 9;
    }

    public List<Integer> getSlotPositions() {
        return slotPositions;
    }

    public String getFillerMaterial() {
        return fillerMaterial;
    }

    public String getEmptySlotMaterial() {
        return emptySlotMaterial;
    }

    public String getLockedSlotMaterial() {
        return lockedSlotMaterial;
    }

    public int getDisplaySlotCount() {
        return slotPositions.size();
    }

    public boolean isProtectionSlot(int inventorySlot) {
        return slotPositions.contains(inventorySlot);
    }

    public int logicalIndexFor(int inventorySlot) {
        return slotPositions.indexOf(inventorySlot);
    }

    public boolean isInfoEnabled() {
        return infoSlot >= 0 && infoSlot < getSize();
    }

    public int getInfoSlot() {
        return infoSlot;
    }

    public String getInfoMaterial() {
        return infoMaterial;
    }

    public String format(String template, Map<String, String> placeholders) {
        String result = template;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }

    public String getFillerName() {
        return fillerName;
    }

    public String getEmptySlotName() {
        return emptySlotName;
    }

    public LockedSlotLabel resolveLockedSlot(int requiredSlots) {
        LockedSlotLabel label = lockedSlotTiers.get(requiredSlots);
        if (label != null) {
            return label;
        }
        return new LockedSlotLabel(lockedSlotName, lockedSlotLore);
    }

    public String getProtectedSlotName(Map<String, String> placeholders) {
        return format(protectedSlotName, placeholders);
    }

    public String getProtectedSlotLore(Map<String, String> placeholders) {
        return format(protectedSlotLore, placeholders);
    }

    public String getInfoName() {
        return infoName;
    }

    public List<String> getInfoLore() {
        return infoLore;
    }

    private static Map<Integer, LockedSlotLabel> parseLockedTiers(
            Map<String, GuiMainSettings.LockedSlotTierSection> raw) {
        Map<Integer, LockedSlotLabel> tiers = new HashMap<>();
        if (raw == null) {
            return tiers;
        }
        for (Map.Entry<String, GuiMainSettings.LockedSlotTierSection> entry : raw.entrySet()) {
            try {
                int slotCount = Integer.parseInt(entry.getKey().trim());
                GuiMainSettings.LockedSlotTierSection section = entry.getValue();
                if (section == null) {
                    continue;
                }
                tiers.put(slotCount, new LockedSlotLabel(section.name, section.lore));
            } catch (NumberFormatException ignored) {
            }
        }
        return tiers;
    }
}
