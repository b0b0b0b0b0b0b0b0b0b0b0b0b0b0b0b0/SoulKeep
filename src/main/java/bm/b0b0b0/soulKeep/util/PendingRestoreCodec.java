package bm.b0b0b0.soulKeep.util;

import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PendingRestoreCodec {

    private PendingRestoreCodec() {
    }

    public static boolean hasItemMeta(ItemStack stack) {
        ItemStack vanilla = ItemStack.of(stack.getType(), stack.getAmount());
        return !stack.equals(vanilla);
    }

    public static PendingRestoreRecord encode(ItemStack stack) {
        ItemStack clone = stack.clone();
        if (hasItemMeta(clone)) {
            return PendingRestoreRecord.binary(clone.serializeAsBytes());
        }
        return PendingRestoreRecord.material(clone.getType(), clone.getAmount());
    }

    public static ItemStack decode(PendingRestoreRecord record) {
        return switch (record.getStorageType()) {
            case MATERIAL -> new ItemStack(record.getMaterial(), record.getAmount());
            case BINARY -> ItemStack.deserializeBytes(record.getItemData()).clone();
        };
    }

    public static List<PendingRestoreRecord> encodeAll(Collection<ItemStack> stacks) {
        List<PendingRestoreRecord> records = new ArrayList<>(stacks.size());
        for (ItemStack stack : stacks) {
            records.add(encode(stack));
        }
        return records;
    }

    public static List<ItemStack> decodeAll(List<PendingRestoreRecord> records) {
        List<ItemStack> stacks = new ArrayList<>(records.size());
        for (PendingRestoreRecord record : records) {
            stacks.add(decode(record));
        }
        return stacks;
    }
}
