package bm.b0b0b0.soulKeep.service;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public final class InventoryRestoreService {

    public void giveToPlayer(Player player, List<ItemStack> stacks) {
        Map<Integer, ItemStack> overflow = player.getInventory().addItem(stacks.toArray(ItemStack[]::new));
        if (overflow.isEmpty()) {
            return;
        }
        for (ItemStack leftover : overflow.values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), leftover);
        }
    }
}
