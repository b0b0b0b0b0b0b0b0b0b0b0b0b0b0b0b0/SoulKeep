package bm.b0b0b0.soulKeep.config;

import net.elytrium.serializer.SerializerConfig;
import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.annotations.NewLine;
import net.elytrium.serializer.annotations.Serializer;
import net.elytrium.serializer.language.object.YamlSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class SoulKeepSettings extends YamlSerializable {

    private static final SerializerConfig CONFIG = new SerializerConfig.Builder()
            .registerSerializer(new ItemOverridesSerializer())
            .build();

    public SoulKeepSettings() {
        super(CONFIG);
    }

    @Comment(@CommentValue("Базовые настройки шанса сохранения при смерти"))
    public SettingsSection settings = new SettingsSection();

    @NewLine
    @Comment({
            @CommentValue("Отдельный шанс (%) для типов предметов (ключ — имя Material)."),
            @CommentValue("enabled: false — все типы используют settings.default-chance")
    })
    @Serializer(ItemOverridesSerializer.class)
    public ItemOverridesSection itemOverrides = new ItemOverridesSection();

    @NewLine
    @Comment(@CommentValue("Бонус к шансу (%) по permission (берётся максимальный из имеющихся)"))
    public Map<String, Double> permissionBoosts = defaultPermissionBoosts();

    @NewLine
    @Comment(@CommentValue("Доп. слоты защиты по permission (берётся максимальный из имеющихся)"))
    public Map<String, Integer> permissionSlots = defaultPermissionSlots();

    @NewLine
    @Comment(@CommentValue("SQLite и пул соединений"))
    public StorageSection storage = new StorageSection();

    @NewLine
    @Comment(@CommentValue("Меню /keepsoul — слоты, материалы рамки"))
    public GuiSection gui = new GuiSection();

    @NewLine
    @Comment({
            @CommentValue("Включение сообщений в чат (true — показывать, false — тихо)."),
            @CommentValue("Тексты сообщений — в lang/messages.yml")
    })
    public NotifySection notify = new NotifySection();

    public void load(JavaPlugin plugin) {
        plugin.getDataFolder().mkdirs();
        reload(plugin.getDataFolder().toPath().resolve("config.yml"));
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
        @Comment(@CommentValue("Базовый шанс (%) удержать защищённый предмет при смерти"))
        public double defaultChance = 20.0;
    }

    public static final class StorageSection {
        public DatabaseSection database = new DatabaseSection();
    }

    public static final class DatabaseSection {
        @Comment(@CommentValue("Имя файла БД в папке плагина"))
        public String file = "soulkeep.db";
        @Comment(@CommentValue("Размер пула HikariCP (для async-запросов)"))
        public int poolSize = 4;
    }

    public static final class GuiSection {
        public String title = "&6SoulKeep &8▪ &dЗащита &5душой";
        public int rows = 3;
        @Comment(@CommentValue("Номера слотов в инвентаре меню (0–53)"))
        public List<Integer> slotPositions = List.of(10, 11, 12, 13, 14, 15, 16);
        public String fillerMaterial = "BLACK_STAINED_GLASS_PANE";
        public String emptySlotMaterial = "GRAY_STAINED_GLASS_PANE";
        public String lockedSlotMaterial = "BARRIER";
    }

    public static final class NotifySection {
        @Comment(@CommentValue("Сообщения при добавлении/удалении типов и ошибках"))
        public ProtectionNotifySection protection = new ProtectionNotifySection();
        @Comment(@CommentValue("Сообщения после смерти (сохранено / ничего не удержано)"))
        public DeathNotifySection death = new DeathNotifySection();
        @Comment(@CommentValue("Сообщения при клике по закрытому слоту в GUI"))
        public GuiNotifySection gui = new GuiNotifySection();
        @Comment(@CommentValue("Сообщения команд /keepsoul"))
        public CommandNotifySection command = new CommandNotifySection();
    }

    public static final class ProtectionNotifySection {
        @Comment(@CommentValue("Тип добавлен в защиту"))
        public boolean added = true;
        @Comment(@CommentValue("Тип убран из защиты"))
        public boolean removed = true;
        @Comment(@CommentValue("Тип уже в списке"))
        public boolean alreadyProtected = true;
        @Comment(@CommentValue("Тип не в списке"))
        public boolean notProtected = true;
        @Comment(@CommentValue("Достигнут лимит слотов"))
        public boolean limitReached = true;
        @Comment(@CommentValue("Пустая рука / не указан тип"))
        public boolean emptyHand = true;
        @Comment(@CommentValue("Неизвестный Material"))
        public boolean invalidMaterial = true;
        @Comment(@CommentValue("Данные игрока ещё грузятся"))
        public boolean dataLoading = true;
        @Comment(@CommentValue("Нужно заполнить слоты по порядку"))
        public boolean fillOrder = true;
        @Comment(@CommentValue("/keepsoul list — заголовок и строки списка"))
        public boolean list = true;
        @Comment(@CommentValue("Список защиты очищен"))
        public boolean cleared = true;
    }

    public static final class DeathNotifySection {
        @Comment(@CommentValue("Предметы удержаны душой"))
        public boolean saved = true;
        @Comment(@CommentValue("Ничего не сохранилось на этот раз"))
        public boolean nothingSaved = true;
    }

    public static final class GuiNotifySection {
        @Comment(@CommentValue("Клик по слоту без права"))
        public boolean slotLocked = true;
    }

    public static final class CommandNotifySection {
        public boolean playerOnly = true;
        public boolean unknownSubcommand = true;
        public boolean noPermission = true;
        public boolean playerNotFound = true;
        public boolean debugDone = true;
    }
}
