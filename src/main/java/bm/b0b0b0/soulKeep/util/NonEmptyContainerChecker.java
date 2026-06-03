package bm.b0b0b0.soulKeep.util;

import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BundleMeta;
import org.bukkit.inventory.meta.ItemMeta;

public final class NonEmptyContainerChecker {

    private NonEmptyContainerChecker() {
    }

    public static boolean hasContents(ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            return false;
        }
        ItemMeta meta = stack.getItemMeta();
        if (meta == null) {
            return false;
        }
        if (meta instanceof BundleMeta bundle) {
            for (ItemStack item : bundle.getItems()) {
                if (item != null && !item.getType().isAir()) {
                    return true;
                }
            }
        }
        if (meta instanceof BlockStateMeta blockStateMeta) {
            BlockState state = blockStateMeta.getBlockState();
            if (state instanceof InventoryHolder holder) {
                for (ItemStack item : holder.getInventory().getStorageContents()) {
                    if (item != null && !item.getType().isAir()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
