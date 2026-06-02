package bm.b0b0b0.soulKeep.message;

import bm.b0b0b0.soulKeep.config.MessageNotifySettings;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class MessageService {

    private static final LegacyComponentSerializer LEGACY = LegacyComponentSerializer.legacyAmpersand();

    private final MessagesSettings messages;
    private final MessageNotifySettings notify;

    public MessageService(JavaPlugin plugin, MessageNotifySettings notify) {
        this.messages = new MessagesSettings();
        this.messages.load(plugin);
        this.notify = notify;
    }

    public void send(CommandSender sender, String path) {
        send(sender, path, Map.of());
    }

    public void send(CommandSender sender, String path, Map<String, String> placeholders) {
        if (!notify.isEnabled(path)) {
            return;
        }
        String raw = resolveRaw(path, placeholders);
        if (raw == null || raw.isBlank()) {
            return;
        }
        sender.sendMessage(LEGACY.deserialize(raw));
    }

    public String resolveRaw(String path, Map<String, String> placeholders) {
        String raw = resolveRaw(path);
        if (raw == null) {
            return "";
        }
        return applyPlaceholders(raw, placeholders);
    }

    public List<String> resolveRawLines(String path, Map<String, String> placeholders) {
        List<String> lines = resolveRawLines(path);
        if (lines.isEmpty()) {
            return List.of();
        }
        List<String> resolved = new ArrayList<>(lines.size());
        for (String line : lines) {
            resolved.add(applyPlaceholders(line, placeholders));
        }
        return resolved;
    }

    private String resolveRaw(String path) {
        return switch (path) {
            case "command.player-only" -> messages.command.playerOnly;
            case "command.unknown-subcommand" -> messages.command.unknownSubcommand;
            case "command.no-permission" -> messages.command.noPermission;
            case "command.player-not-found" -> messages.command.playerNotFound;
            case "command.debug-done" -> messages.command.debugDone;
            case "protection.added" -> messages.protection.added;
            case "protection.removed" -> messages.protection.removed;
            case "protection.not-protected" -> messages.protection.notProtected;
            case "protection.already-protected" -> messages.protection.alreadyProtected;
            case "protection.limit-reached" -> messages.protection.limitReached;
            case "protection.empty-hand" -> messages.protection.emptyHand;
            case "protection.invalid-material" -> messages.protection.invalidMaterial;
            case "protection.data-loading" -> messages.protection.dataLoading;
            case "protection.fill-order" -> messages.protection.fillOrder;
            case "protection.list-empty" -> messages.protection.listEmpty;
            case "protection.list-header" -> messages.protection.listHeader;
            case "protection.list-entry" -> messages.protection.listEntry;
            case "protection.cleared" -> messages.protection.cleared;
            case "death.saved" -> messages.death.saved;
            case "death.nothing-saved" -> messages.death.nothingSaved;
            case "gui.filler-name" -> messages.gui.fillerName;
            case "gui.empty-slot-name" -> messages.gui.emptySlotName;
            case "gui.locked-slot-name" -> messages.gui.lockedSlotName;
            case "gui.locked-slot-lore" -> messages.gui.lockedSlotLore;
            case "gui.protected-slot-name" -> messages.gui.protectedSlotName;
            case "gui.protected-slot-lore" -> messages.gui.protectedSlotLore;
            case "gui.slot-locked" -> messages.gui.slotLocked;
            case "gui.info-name" -> messages.gui.infoName;
            default -> null;
        };
    }

    private List<String> resolveRawLines(String path) {
        if ("gui.info-lore".equals(path)) {
            return messages.gui.infoLore;
        }
        return List.of();
    }

    private String applyPlaceholders(String template, Map<String, String> placeholders) {
        String result = template.replace("{prefix}", messages.prefix);
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
