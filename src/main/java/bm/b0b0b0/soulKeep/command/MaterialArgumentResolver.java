package bm.b0b0b0.soulKeep.command;

import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.util.MaterialResolver;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Optional;

public final class MaterialArgumentResolver {

    private final MessageService messages;

    public MaterialArgumentResolver(MessageService messages) {
        this.messages = messages;
    }

    public Optional<Material> resolve(Player player, String[] args) {
        String materialArg = args.length > 1 ? args[1] : null;
        Optional<Material> material = MaterialResolver.resolve(player, materialArg);
        if (material.isPresent()) {
            return material;
        }
        if (materialArg != null) {
            messages.send(player, "protection.invalid-material", Map.of("input", materialArg));
        } else {
            messages.send(player, "protection.empty-hand");
        }
        return Optional.empty();
    }
}
