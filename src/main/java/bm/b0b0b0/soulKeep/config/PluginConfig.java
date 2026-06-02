package bm.b0b0b0.soulKeep.config;

import org.bukkit.plugin.java.JavaPlugin;

public final class PluginConfig {

    private final SoulChanceSettings chanceSettings;
    private final PermissionBoostTable permissionBoosts;
    private final PermissionSlotTable permissionSlots;
    private final DatabaseSettings databaseSettings;

    public PluginConfig(JavaPlugin plugin) {
        SoulKeepSettings settings = new SoulKeepSettings();
        settings.load(plugin);
        this.chanceSettings = new SoulChanceSettings(settings);
        this.permissionBoosts = new PermissionBoostTable(settings);
        this.permissionSlots = new PermissionSlotTable(settings);
        this.databaseSettings = new DatabaseSettings(settings);
    }

    public SoulChanceSettings getChanceSettings() {
        return chanceSettings;
    }

    public PermissionBoostTable getPermissionBoosts() {
        return permissionBoosts;
    }

    public PermissionSlotTable getPermissionSlots() {
        return permissionSlots;
    }

    public DatabaseSettings getDatabaseSettings() {
        return databaseSettings;
    }
}
