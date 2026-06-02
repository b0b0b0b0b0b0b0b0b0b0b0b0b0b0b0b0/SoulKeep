package bm.b0b0b0.soulKeep.message;

import net.elytrium.serializer.placeholders.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;

public final class MessageService {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final MessagesSettings messages;

    public MessageService(JavaPlugin plugin) {
        this.messages = new MessagesSettings();
        this.messages.load(plugin);
    }

    public void send(CommandSender sender, String path) {
        send(sender, path, Map.of());
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        String raw = resolveRaw(path);
        if (raw == null) {
            return;
        }
        sender.sendMessage(LEGACY.deserialize(applyPlaceholders(raw, placeholders)));
    }

    private String resolveRaw(String path) {
        return switch (path) {
            case "command.player-only" -> messages.command.playerOnly;
            case "command.unknown-subcommand" -> messages.command.unknownSubcommand;
            case "protection.added" -> messages.protection.added;
            case "protection.removed" -> messages.protection.removed;
            case "protection.not-protected" -> messages.protection.notProtected;
            case "protection.already-protected" -> messages.protection.alreadyProtected;
            case "protection.limit-reached" -> messages.protection.limitReached;
            case "protection.empty-hand" -> messages.protection.emptyHand;
            case "protection.invalid-material" -> messages.protection.invalidMaterial;
            case "protection.data-loading" -> messages.protection.dataLoading;
            case "protection.list-empty" -> messages.protection.listEmpty;
            case "protection.list-header" -> messages.protection.listHeader;
            case "protection.list-entry" -> messages.protection.listEntry;
            case "protection.cleared" -> messages.protection.cleared;
            case "death.saved" -> messages.death.saved;
            case "death.nothing-saved" -> messages.death.nothingSaved;
            default -> null;
        };
    }

    private String applyPlaceholders(String template, Map<String, String> placeholders) {
        String withPrefix = Placeholders.replaceFor(messages, template, messages.prefix);
        String result = withPrefix;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
