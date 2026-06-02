package bm.b0b0b0.soulKeep.util;

import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

public final class SoulKeepLog {

    private final Logger logger;

    public SoulKeepLog(JavaPlugin plugin) {
        this.logger = plugin.getLogger();
    }

    public void info(String message) {
        logger.info(message);
    }

    public void info(Player player, String message) {
        logger.info(player.getName() + " (" + player.getUniqueId() + "): " + message);
    }

    public void info(UUID playerId, String message) {
        logger.info(playerId + ": " + message);
    }

    public void warn(String message) {
        logger.warning(message);
    }

    public void warn(Player player, String message) {
        logger.warning(player.getName() + " (" + player.getUniqueId() + "): " + message);
    }

    public void item(String context, ItemStack stack) {
        if (stack == null || stack.getType().isAir()) {
            logger.info(context + ": <air>");
            return;
        }
        PendingRestoreRecord record = PendingRestoreCodec.encode(stack.clone());
        StringBuilder line = new StringBuilder();
        line.append(context).append(": ").append(stack.getType().name())
                .append(" x").append(stack.getAmount())
                .append(" storage=").append(record.getStorageType().name());
        if (stack.getItemMeta() instanceof Damageable damageable) {
            int max = stack.getType().getMaxDurability();
            line.append(" damage=").append(damageable.getDamage()).append("/").append(max);
        }
        if (record.getStorageType() == PendingRestoreRecord.StorageType.BINARY) {
            line.append(" bytes=").append(record.getItemData().length);
        }
        logger.info(line.toString());
    }

    public void items(String context, List<ItemStack> stacks) {
        logger.info(context + ": " + stacks.size() + " stack(s)");
        for (int index = 0; index < stacks.size(); index++) {
            item(context + " #" + (index + 1), stacks.get(index));
        }
    }
}
