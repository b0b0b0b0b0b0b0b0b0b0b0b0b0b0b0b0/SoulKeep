package bm.b0b0b0.soulKeep.bootstrap;

import bm.b0b0b0.soulKeep.command.KeepSoulCommand;
import bm.b0b0b0.soulKeep.config.PluginConfig;
import bm.b0b0b0.soulKeep.gui.GuiItemFactory;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuFactory;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuListener;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuService;
import bm.b0b0b0.soulKeep.database.AsyncDatabaseExecutor;
import bm.b0b0b0.soulKeep.database.DatabaseConnectionProvider;
import bm.b0b0b0.soulKeep.database.PlayerProtectionDao;
import bm.b0b0b0.soulKeep.listener.DeathProtectionListener;
import bm.b0b0b0.soulKeep.listener.PlayerDataLifecycleListener;
import bm.b0b0b0.soulKeep.listener.RespawnRestoreListener;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.service.ChanceCalculationService;
import bm.b0b0b0.soulKeep.service.DeathProtectionService;
import bm.b0b0b0.soulKeep.service.InventoryRestoreService;
import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import bm.b0b0b0.soulKeep.store.PendingRestoreStore;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginContext {

    private final PluginLifecycle lifecycle;

    public PluginContext(JavaPlugin plugin) {
        PluginConfig pluginConfig = new PluginConfig(plugin);
        DatabaseConnectionProvider databaseConnectionProvider =
                new DatabaseConnectionProvider(plugin, pluginConfig.getDatabaseSettings());
        AsyncDatabaseExecutor asyncDatabaseExecutor = new AsyncDatabaseExecutor(plugin);
        PlayerProtectionDao playerProtectionDao = new PlayerProtectionDao(databaseConnectionProvider);
        PlayerProtectionRepository playerProtectionRepository =
                new PlayerProtectionRepository(plugin, playerProtectionDao, asyncDatabaseExecutor);
        this.lifecycle = new PluginLifecycle(databaseConnectionProvider, playerProtectionRepository);
        plugin.getServer().getOnlinePlayers().forEach(player ->
                playerProtectionRepository.loadAsync(player.getUniqueId()));

        MessageService messageService = new MessageService(plugin);
        PendingRestoreStore pendingRestoreStore = new PendingRestoreStore();
        ChanceCalculationService chanceCalculationService = new ChanceCalculationService(
                pluginConfig.getChanceSettings(),
                pluginConfig.getPermissionBoosts());
        InventoryRestoreService inventoryRestoreService = new InventoryRestoreService();
        ProtectionManagementService protectionManagementService = new ProtectionManagementService(
                playerProtectionRepository,
                pluginConfig.getPermissionSlots(),
                chanceCalculationService,
                messageService);
        DeathProtectionService deathProtectionService = new DeathProtectionService(
                playerProtectionRepository,
                chanceCalculationService,
                pendingRestoreStore,
                messageService,
                inventoryRestoreService);
        GuiItemFactory guiItemFactory = new GuiItemFactory(messageService);
        ProtectionMenuFactory protectionMenuFactory = new ProtectionMenuFactory(
                pluginConfig.getGuiSettings(),
                pluginConfig.getPermissionSlots(),
                chanceCalculationService,
                guiItemFactory,
                protectionManagementService,
                messageService);
        ProtectionMenuService protectionMenuService = new ProtectionMenuService(
                protectionManagementService,
                protectionMenuFactory);
        KeepSoulCommand keepSoulCommand = new KeepSoulCommand(
                protectionManagementService,
                protectionMenuService,
                messageService);

        registerListeners(plugin, deathProtectionService, playerProtectionRepository, pendingRestoreStore);
        registerCommands(plugin, keepSoulCommand);
    }

    public PluginLifecycle getLifecycle() {
        return lifecycle;
    }

    private static void registerListeners(
            JavaPlugin plugin,
            DeathProtectionService deathProtectionService,
            PlayerProtectionRepository playerProtectionRepository,
            PendingRestoreStore pendingRestoreStore) {
        plugin.getServer().getPluginManager().registerEvents(
                new DeathProtectionListener(deathProtectionService), plugin);
        plugin.getServer().getPluginManager().registerEvents(
                new RespawnRestoreListener(plugin, deathProtectionService), plugin);
        plugin.getServer().getPluginManager().registerEvents(
                new PlayerDataLifecycleListener(
                        plugin,
                        playerProtectionRepository,
                        deathProtectionService,
                        pendingRestoreStore),
                plugin);
        plugin.getServer().getPluginManager().registerEvents(new ProtectionMenuListener(), plugin);
    }

    private static void registerCommands(JavaPlugin plugin, KeepSoulCommand keepSoulCommand) {
        var command = plugin.getCommand("keepsoul");
        if (command == null) {
            plugin.getLogger().severe("Command keepsoul is missing from plugin.yml");
            return;
        }
        command.setExecutor(keepSoulCommand);
        command.setTabCompleter(keepSoulCommand);
    }
}
