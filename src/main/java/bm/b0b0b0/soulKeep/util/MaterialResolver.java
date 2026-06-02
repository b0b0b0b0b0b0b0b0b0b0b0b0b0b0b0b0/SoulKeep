package bm.b0b0b0.soulKeep.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Locale;
import java.util.Optional;

public final class MaterialResolver {

    private MaterialResolver() {
    }

    public static Optional<Material> fromHand(Player player) {
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType().isAir()) {
            return Optional.empty();
        }
        return Optional.of(hand.getType());
    }

    public static Optional<Material> fromArgument(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }
        Material material = Material.matchMaterial(input.trim().toUpperCase(Locale.ROOT));
        if (material == null || !material.isItem()) {
            return Optional.empty();
        }
        return Optional.of(material);
    }

    public static Optional<Material> resolve(Player player, String argument) {
        if (argument != null && !argument.isBlank()) {
            return fromArgument(argument);
        }
        return fromHand(player);
    }
}
