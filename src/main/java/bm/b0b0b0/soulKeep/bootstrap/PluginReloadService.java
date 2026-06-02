package bm.b0b0b0.soulKeep.bootstrap;

import bm.b0b0b0.soulKeep.config.PluginConfig;
import bm.b0b0b0.soulKeep.gui.GuiItemFactory;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuFactory;
import bm.b0b0b0.soulKeep.gui.ProtectionMenuService;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.service.ChanceCalculationService;
import bm.b0b0b0.soulKeep.service.DeathProtectionService;
import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginReloadService {

    private final JavaPlugin plugin;
    private final PlayerProtectionRepository protectionRepository;
    private final DeathProtectionService deathProtectionService;

    private MessageService messageService;
    private ChanceCalculationService chanceCalculationService;
    private ProtectionManagementService protectionManagementService;
    private ProtectionMenuService protectionMenuService;

    public PluginReloadService(
            JavaPlugin plugin,
            PlayerProtectionRepository protectionRepository,
            DeathProtectionService deathProtectionService) {
        this.plugin = plugin;
        this.protectionRepository = protectionRepository;
        this.deathProtectionService = deathProtectionService;
        reload();
    }

    public void reload() {
        PluginConfig pluginConfig = new PluginConfig(plugin);
        messageService = new MessageService(plugin, pluginConfig.getMessageNotifySettings());
        chanceCalculationService = new ChanceCalculationService(
                pluginConfig.getChanceSettings(),
                pluginConfig.getPermissionBoosts());
        protectionManagementService = new ProtectionManagementService(
                protectionRepository,
                pluginConfig.getPermissionSlots(),
                pluginConfig.getProtectionSettings(),
                messageService);
        GuiItemFactory guiItemFactory = new GuiItemFactory(pluginConfig.getGuiSettings());
        ProtectionMenuFactory protectionMenuFactory = new ProtectionMenuFactory(
                pluginConfig.getGuiSettings(),
                pluginConfig.getPermissionSlots(),
                chanceCalculationService,
                guiItemFactory,
                protectionManagementService,
                messageService);
        protectionMenuService = new ProtectionMenuService(
                protectionManagementService,
                protectionMenuFactory);
        deathProtectionService.rebind(
                pluginConfig.getProtectionSettings(),
                chanceCalculationService,
                messageService);
    }

    public MessageService getMessageService() {
        return messageService;
    }

    public ProtectionManagementService getProtectionManagementService() {
        return protectionManagementService;
    }

    public ProtectionMenuService getProtectionMenuService() {
        return protectionMenuService;
    }
}
