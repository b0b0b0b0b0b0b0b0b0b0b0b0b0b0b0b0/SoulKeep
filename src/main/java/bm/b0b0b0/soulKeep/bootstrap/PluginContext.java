package bm.b0b0b0.soulKeep.bootstrap;

import bm.b0b0b0.soulKeep.command.AdminDebugService;
import bm.b0b0b0.soulKeep.command.KeepSoulCommand;
import bm.b0b0b0.soulKeep.config.PluginConfig;
import bm.b0b0b0.soulKeep.gui.GuiItemFactory;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuFactory;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuListener;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuService;
import bm.b0b0b0.soulKeep.database.AsyncDatabaseExecutor;
import bm.b0b0b0.soulKeep.database.DatabaseConnectionProvider;
import bm.b0b0b0.soulKeep.database.PendingRestoreDao;
import bm.b0b0b0.soulKeep.database.PlayerProtectionDao;
import bm.b0b0b0.soulKeep.listener.DeathProtectionListener;
import bm.b0b0b0.soulKeep.listener.PlayerDataLifecycleListener;
import bm.b0b0b0.soulKeep.listener.RespawnRestoreListener;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.persistence.SqlitePendingRestorePersistence;
import bm.b0b0b0.soulKeep.repository.PendingRestoreRepository;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.service.ChanceCalculationService;
import bm.b0b0b0.soulKeep.service.DeathProtectionService;
import bm.b0b0b0.soulKeep.service.InventoryRestoreService;
import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginContext {

    private final PluginLifecycle lifecycle;

    public PluginContext(JavaPlugin plugin) {
        PluginConfig pluginConfig = new PluginConfig(plugin);
        DatabaseConnectionProvider databaseConnectionProvider =
                new DatabaseConnectionProvider(plugin, pluginConfig.getDatabaseSettings());
        AsyncDatabaseExecutor asyncDatabaseExecutor = new AsyncDatabaseExecutor(plugin);
        PlayerProtectionDao playerProtectionDao = new PlayerProtectionDao(databaseConnectionProvider);
        PendingRestoreDao pendingRestoreDao = new PendingRestoreDao(databaseConnectionProvider);
        SqlitePendingRestorePersistence pendingPersistence = new SqlitePendingRestorePersistence(pendingRestoreDao);
        PlayerProtectionRepository playerProtectionRepository =
                new PlayerProtectionRepository(plugin, playerProtectionDao, asyncDatabaseExecutor);
        InventoryRestoreService inventoryRestoreService = new InventoryRestoreService();
        PendingRestoreRepository pendingRestoreRepository = new PendingRestoreRepository(
                plugin,
                pendingPersistence,
                asyncDatabaseExecutor,
                inventoryRestoreService);
        this.lifecycle = new PluginLifecycle(databaseConnectionProvider, playerProtectionRepository);

        MessageService messageService = new MessageService(plugin, pluginConfig.getMessageNotifySettings());
        ChanceCalculationService chanceCalculationService = new ChanceCalculationService(
                pluginConfig.getChanceSettings(),
                pluginConfig.getPermissionBoosts());
        ProtectionManagementService protectionManagementService = new ProtectionManagementService(
                playerProtectionRepository,
                pluginConfig.getPermissionSlots(),
                chanceCalculationService,
                messageService);
        DeathProtectionService deathProtectionService = new DeathProtectionService(
                playerProtectionRepository,
                chanceCalculationService,
                pendingRestoreRepository,
                messageService);
        plugin.getServer().getOnlinePlayers().forEach(player -> {
            playerProtectionRepository.loadAsync(player.getUniqueId());
            deathProtectionService.deliverPending(player);
        });
        GuiItemFactory guiItemFactory = new GuiItemFactory(pluginConfig.getGuiSettings());
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
        AdminDebugService adminDebugService = new AdminDebugService(
                playerProtectionRepository,
                pendingRestoreRepository,
                pendingPersistence,
                chanceCalculationService,
                new SoulKeepLog(plugin));
        KeepSoulCommand keepSoulCommand = new KeepSoulCommand(
                protectionManagementService,
                protectionMenuService,
                adminDebugService,
                messageService);

        registerListeners(plugin, deathProtectionService, playerProtectionRepository);
        registerCommands(plugin, keepSoulCommand);
    }

    public PluginLifecycle getLifecycle() {
        return lifecycle;
    }

    private static void registerListeners(
            JavaPlugin plugin,
            DeathProtectionService deathProtectionService,
            PlayerProtectionRepository playerProtectionRepository) {
        plugin.getServer().getPluginManager().registerEvents(
                new DeathProtectionListener(deathProtectionService), plugin);
        plugin.getServer().getPluginManager().registerEvents(
                new RespawnRestoreListener(plugin, deathProtectionService), plugin);
        plugin.getServer().getPluginManager().registerEvents(
                new PlayerDataLifecycleListener(plugin, playerProtectionRepository, deathProtectionService),
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
