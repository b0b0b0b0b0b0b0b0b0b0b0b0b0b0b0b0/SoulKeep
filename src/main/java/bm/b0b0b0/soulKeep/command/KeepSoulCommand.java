package bm.b0b0b0.soulKeep.command;

import bm.b0b0b0.soulKeep.gui.ProtectionMenuService;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class KeepSoulCommand implements CommandExecutor, TabCompleter {

    private static final List<String> SUBCOMMANDS = List.of("add", "remove", "list", "clear", "debug");

    private final ProtectionManagementService protectionService;
    private final ProtectionMenuService menuService;
    private final AdminDebugService adminDebugService;
    private final MessageService messages;
    private final MaterialArgumentResolver materialArgumentResolver;

    public KeepSoulCommand(
            ProtectionManagementService protectionService,
            ProtectionMenuService menuService,
            AdminDebugService adminDebugService,
            MessageService messages) {
        this.protectionService = protectionService;
        this.menuService = menuService;
        this.adminDebugService = adminDebugService;
        this.messages = messages;
        this.materialArgumentResolver = new MaterialArgumentResolver(messages);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            return handleDebug(sender, args);
        }
        if (!(sender instanceof Player player)) {
            messages.send(sender, "command.player-only");
            return true;
        }
        if (args.length == 0) {
            menuService.open(player);
            return true;
        }
        String sub = args[0].toLowerCase(Locale.ROOT);
        switch (sub) {
            case "add" -> handleAdd(player, args);
            case "remove" -> handleRemove(player, args);
            case "list" -> protectionService.list(player);
            case "clear" -> handleClear(player);
            default -> messages.send(sender, "command.unknown-subcommand");
        }
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        if (!sender.hasPermission("keepsoul.debug")) {
            messages.send(sender, "command.no-permission");
            return true;
        }
        Player target;
        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                messages.send(sender, "command.player-not-found");
                return true;
            }
        } else if (sender instanceof Player player) {
            target = player;
        } else {
            messages.send(sender, "command.player-only");
            return true;
        }
        if (sender instanceof Player admin) {
            adminDebugService.dump(admin, target);
        } else {
            adminDebugService.dumpConsole(target);
        }
        messages.send(sender, "command.debug-done", Map.of("player", target.getName()));
        return true;
    }

    private void handleAdd(Player player, String[] args) {
        materialArgumentResolver.resolve(player, args).ifPresent(material -> {
            ProtectionManagementService.AddResult result = protectionService.tryAdd(player, material);
            if (result == ProtectionManagementService.AddResult.ALREADY_PROTECTED) {
                protectionService.sendAlreadyProtected(player, material);
            } else if (result == ProtectionManagementService.AddResult.LIMIT_REACHED) {
                protectionService.sendLimitMessage(player);
            }
        });
    }

    private void handleRemove(Player player, String[] args) {
        materialArgumentResolver.resolve(player, args).ifPresent(material -> {
            ProtectionManagementService.RemoveResult result = protectionService.tryRemove(player, material);
            if (result == ProtectionManagementService.RemoveResult.NOT_PROTECTED) {
                protectionService.sendNotProtected(player, material);
            }
        });
    }

    private void handleClear(Player player) {
        protectionService.clear(player);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterPrefix(SUBCOMMANDS, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return filterPrefix(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[1]);
        }
        if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> materials = new ArrayList<>();
            for (Material material : Material.values()) {
                if (material.isItem()) {
                    materials.add(material.name().toLowerCase(Locale.ROOT));
                }
            }
            return filterPrefix(materials, args[1]);
        }
        return List.of();
    }

    private static List<String> filterPrefix(List<String> options, String prefix) {
        String lower = prefix.toLowerCase(Locale.ROOT);
        return options.stream()
                .filter(option -> option.toLowerCase(Locale.ROOT).startsWith(lower))
                .toList();
    }
}
