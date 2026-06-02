package bm.b0b0b0.soulKeep.config;

import java.util.List;

public final class GuiSettings {

    private final String title;
    private final int rows;
    private final List<Integer> slotPositions;
    private final String fillerMaterial;
    private final String emptySlotMaterial;
    private final String lockedSlotMaterial;

    public GuiSettings(SoulKeepSettings settings) {
        SoulKeepSettings.GuiSection section = settings.gui;
        this.title = section.title;
        this.rows = section.rows;
        this.slotPositions = List.copyOf(section.slotPositions);
        this.fillerMaterial = section.fillerMaterial;
        this.emptySlotMaterial = section.emptySlotMaterial;
        this.lockedSlotMaterial = section.lockedSlotMaterial;
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
}
