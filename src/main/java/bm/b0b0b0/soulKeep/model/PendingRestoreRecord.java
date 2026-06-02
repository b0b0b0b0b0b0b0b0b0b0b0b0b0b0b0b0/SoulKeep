package bm.b0b0b0.soulKeep.model;

import org.bukkit.Material;

public final class PendingRestoreRecord {

    public enum StorageType {
        MATERIAL,
        BINARY
    }

    private final StorageType storageType;
    private final Material material;
    private final int amount;
    private final byte[] itemData;

    private PendingRestoreRecord(StorageType storageType, Material material, int amount, byte[] itemData) {
        this.storageType = storageType;
        this.material = material;
        this.amount = amount;
        this.itemData = itemData;
    }

    public static PendingRestoreRecord material(Material material, int amount) {
        return new PendingRestoreRecord(StorageType.MATERIAL, material, amount, null);
    }

    public static PendingRestoreRecord binary(byte[] itemData) {
        return new PendingRestoreRecord(StorageType.BINARY, null, 0, itemData);
    }

    public StorageType getStorageType() {
        return storageType;
    }

    public Material getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }

    public byte[] getItemData() {
        return itemData;
    }
}
