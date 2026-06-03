package bm.b0b0b0.soulKeep.config;

public final class MessageNotifySettings {

    private final SoulKeepSettings.NotifySection notify;

    public MessageNotifySettings(SoulKeepSettings settings) {
        this.notify = settings.notify;
    }

    public boolean isEnabled(String path) {
        return switch (path) {
            case "command.player-only" -> notify.command.playerOnly;
            case "command.unknown-subcommand" -> notify.command.unknownSubcommand;
            case "command.no-permission" -> notify.command.noPermission;
            case "command.player-not-found" -> notify.command.playerNotFound;
            case "command.debug-done" -> notify.command.debugDone;
            case "command.reload-done" -> notify.command.reloadDone;
            case "protection.added" -> notify.protection.added;
            case "protection.removed" -> notify.protection.removed;
            case "protection.not-protected" -> notify.protection.notProtected;
            case "protection.already-protected" -> notify.protection.alreadyProtected;
            case "protection.limit-reached" -> notify.protection.limitReached;
            case "protection.empty-hand" -> notify.protection.emptyHand;
            case "protection.invalid-material" -> notify.protection.invalidMaterial;
            case "protection.data-loading" -> notify.protection.dataLoading;
            case "protection.fill-order" -> notify.protection.fillOrder;
            case "protection.non-empty-container" -> notify.protection.nonEmptyContainer;
            case "protection.list-empty" -> notify.protection.list;
            case "protection.list-header", "protection.list-entry" -> notify.protection.list;
            case "protection.cleared" -> notify.protection.cleared;
            case "death.saved" -> notify.death.saved;
            case "death.nothing-saved" -> notify.death.nothingSaved;
            case "gui.slot-locked" -> notify.gui.slotLocked;
            default -> true;
        };
    }
}
