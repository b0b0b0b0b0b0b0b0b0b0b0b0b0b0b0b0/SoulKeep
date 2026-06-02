package bm.b0b0b0.soulKeep.config;

import net.elytrium.serializer.language.object.YamlSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SoulKeepSettings extends YamlSerializable {

    public SettingsSection settings = new SettingsSection();
    public Map<String, Double> itemOverrides = defaultItemOverrides();
    public Map<String, Double> permissionBoosts = defaultPermissionBoosts();
    public Map<String, Integer> permissionSlots = defaultPermissionSlots();
    public StorageSection storage = new StorageSection();
    public GuiSection gui = new GuiSection();

    public void load(JavaPlugin plugin) {
        plugin.getDataFolder().mkdirs();
        reload(plugin.getDataFolder().toPath().resolve("config.yml"));
    }

    private static Map<String, Double> defaultItemOverrides() {
        Map<String, Double> values = new LinkedHashMap<>();
        values.put("TOTEM_OF_UNDYING", 5.0);
        return values;
    }

    private static Map<String, Double> defaultPermissionBoosts() {
        Map<String, Double> values = new LinkedHashMap<>();
        values.put("keepsouls.boost.10", 10.0);
        values.put("keepsouls.boost.25", 25.0);
        return values;
    }

    private static Map<String, Integer> defaultPermissionSlots() {
        Map<String, Integer> values = new LinkedHashMap<>();
        values.put("keepsouls.slots.1", 1);
        values.put("keepsouls.slots.2", 2);
        values.put("keepsouls.slots.3", 3);
        values.put("keepsouls.slots.4", 4);
        return values;
    }

    public static final class SettingsSection {
        public double defaultChance = 20.0;
    }

    public static final class StorageSection {
        public DatabaseSection database = new DatabaseSection();
    }

    public static final class DatabaseSection {
        public String file = "soulkeep.db";
        public int poolSize = 4;
    }

    public static final class GuiSection {
        public String title = "&6SoulKeep &8| &7защита душой";
        public int rows = 3;
        public List<Integer> slotPositions = List.of(10, 11, 12, 13, 14, 15, 16);
        public String fillerMaterial = "BLACK_STAINED_GLASS_PANE";
        public String emptySlotMaterial = "GRAY_STAINED_GLASS_PANE";
        public String lockedSlotMaterial = "BARRIER";
    }
}
