package bm.b0b0b0.soulKeep.gui;

import bm.b0b0b0.soulKeep.config.GuiSettings;
import bm.b0b0b0.soulKeep.config.PermissionSlotTable;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.service.ChanceCalculationService;
import bm.b0b0b0.soulKeep.service.ProtectionManagementService;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
import org.bukkit.entity.Player;

public final class ProtectionMenuFactory {

    private final GuiSettings guiSettings;
    private final PermissionSlotTable permissionSlots;
    private final ChanceCalculationService chanceService;
    private final GuiItemFactory itemFactory;
    private final ProtectionManagementService protectionService;
    private final MessageService messages;
    private final SoulKeepLog log;

    public ProtectionMenuFactory(
            GuiSettings guiSettings,
            PermissionSlotTable permissionSlots,
            ChanceCalculationService chanceService,
            GuiItemFactory itemFactory,
            ProtectionManagementService protectionService,
            MessageService messages,
            SoulKeepLog log) {
        this.guiSettings = guiSettings;
        this.permissionSlots = permissionSlots;
        this.chanceService = chanceService;
        this.itemFactory = itemFactory;
        this.protectionService = protectionService;
        this.messages = messages;
        this.log = log;
    }

    public ProtectionMenu create(Player owner, PlayerProtectionData data) {
        log.info(owner, "gui open, protected=" + data.getProtectedCount());
        return new ProtectionMenu(
                owner,
                data,
                guiSettings,
                permissionSlots,
                chanceService,
                itemFactory,
                protectionService,
                messages,
                log);
    }
}
