package bm.b0b0b0.soulKeep.config;

import java.util.List;

public final class GuiSettings {

    private final String title;
    private final int rows;
    private final List<Integer> slotPositions;
    private final String fillerMaterial;
    private final String emptySlotMaterial;
    private final String lockedSlotMaterial;
    private final int infoSlot;
    private final String infoMaterial;

    public GuiSettings(GuiMainSettings settings) {
        this.title = settings.title;
        this.rows = settings.rows;
        this.slotPositions = List.copyOf(settings.slotPositions);
        this.fillerMaterial = settings.fillerMaterial;
        this.emptySlotMaterial = settings.emptySlotMaterial;
        this.lockedSlotMaterial = settings.lockedSlotMaterial;
        this.infoSlot = settings.infoSlot;
        this.infoMaterial = settings.infoMaterial;
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
}
