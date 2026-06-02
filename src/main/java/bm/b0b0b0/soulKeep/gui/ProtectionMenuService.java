package bm.b0b0b0.soulKeep.gui;

import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import org.bukkit.entity.Player;

public final class ProtectionMenuService {

    private final ProtectionManagementService protectionService;
    private final ProtectionMenuFactory menuFactory;

    public ProtectionMenuService(
            ProtectionManagementService protectionService,
            ProtectionMenuFactory menuFactory) {
        this.protectionService = protectionService;
        this.menuFactory = menuFactory;
    }

    public void open(Player player) {
        protectionService.findData(player).ifPresent(data -> {
            ProtectionMenu menu = menuFactory.create(player, data);
            player.openInventory(menu.getInventory());
        });
    }
}
