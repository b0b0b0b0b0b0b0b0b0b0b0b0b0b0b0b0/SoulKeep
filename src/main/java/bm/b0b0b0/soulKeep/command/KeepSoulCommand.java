package bm.b0b0b0.soulKeep.command;

import bm.b0b0b0.soulKeep.bootstrap.PluginReloadService;
import bm.b0b0b0.soulKeep.message.MessageService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Locale;
import java.util.Map;

public final class KeepSoulCommand implements CommandExecutor, TabCompleter {

    private static final List<String> ADMIN_SUBCOMMANDS = List.of("reload", "debug");

    private final PluginReloadService reloadService;
    private final AdminDebugService adminDebugService;

    public KeepSoulCommand(
            PluginReloadService reloadService,
            AdminDebugService adminDebugService) {
        this.reloadService = reloadService;
        this.adminDebugService = adminDebugService;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            return handleReload(sender);
        }
        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            return handleDebug(sender, args);
        }
        MessageService messages = reloadService.getMessageService();
        if (!(sender instanceof Player player)) {
            messages.send(sender, "command.player-only");
            return true;
        }
        if (args.length > 0) {
            messages.send(sender, "command.unknown-subcommand");
            return true;
        }
        reloadService.getProtectionMenuService().open(player);
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        MessageService messages = reloadService.getMessageService();
        if (!sender.hasPermission("keepsoul.reload")) {
            messages.send(sender, "command.no-permission");
            return true;
        }
        reloadService.reload();
        reloadService.getMessageService().send(sender, "command.reload-done");
        return true;
    }

    private boolean handleDebug(CommandSender sender, String[] args) {
        MessageService messages = reloadService.getMessageService();
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return filterPrefix(ADMIN_SUBCOMMANDS, args[0]);
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("debug")) {
            return filterPrefix(Bukkit.getOnlinePlayers().stream().map(Player::getName).toList(), args[1]);
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
