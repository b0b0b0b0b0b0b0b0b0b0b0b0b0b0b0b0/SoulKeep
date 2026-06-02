package bm.b0b0b0.soulKeep.gui;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public final class ProtectionDisplayStackResolver {

    private ProtectionDisplayStackResolver() {
    }

    public static ItemStack resolve(Player player, Material material) {
        ItemStack fallback = new ItemStack(material);
        ItemStack best = null;
        int bestDamage = -1;
        for (ItemStack content : player.getInventory().getContents()) {
            if (content == null || content.getType() != material) {
                continue;
            }
            int damage = readDamage(content);
            if (damage > bestDamage) {
                best = content.clone();
                bestDamage = damage;
            }
        }
        if (best != null) {
            best.setAmount(1);
            return best;
        }
        return fallback;
    }

    private static int readDamage(ItemStack stack) {
        if (stack.getItemMeta() instanceof Damageable damageable) {
            return damageable.getDamage();
        }
        return 0;
    }
}
