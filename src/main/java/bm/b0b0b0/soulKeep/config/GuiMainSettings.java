package bm.b0b0b0.soulKeep.config;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.List;

public final class GuiMainSettings extends YamlSerializable {

    @Comment(@CommentValue("Заголовок меню /keepsoul"))
    public String title = "&6SoulKeep &8▪ &dЗащита &5душой";

    @Comment(@CommentValue("Строк в сундуке (1–6)"))
    public int rows = 3;

    @Comment(@CommentValue("Номера слотов защиты в инвентаре меню (0–53)"))
    public List<Integer> slotPositions = List.of(10, 11, 12, 13, 14, 15, 16);

    public String fillerMaterial = "BLACK_STAINED_GLASS_PANE";
    public String emptySlotMaterial = "GRAY_STAINED_GLASS_PANE";
    public String lockedSlotMaterial = "BARRIER";

    public String fillerName = " ";
    public String emptySlotName = "&7Пустой слот";
    public String lockedSlotName = "&cЗакрыто";
    public String lockedSlotLore = "&7Нужен донат-ранг";
    public String protectedSlotName = "&f{material}";
    public String protectedSlotLore = "&7Шанс: &f{chance}%&7, ЛКМ — убрать";

    @Comment(@CommentValue("Слот справки внизу по центру (3 ряда = 22). -1 — выключить"))
    public int infoSlot = 22;

    public String infoMaterial = "KNOWLEDGE_BOOK";
    public String infoName = "&dДля чего это";
    public List<String> infoLore = defaultInfoLore();

    public void load(JavaPlugin plugin) {
        Path file = plugin.getDataFolder().toPath().resolve("gui/main.yml");
        file.getParent().toFile().mkdirs();
        reload(file);
    }

    private static List<String> defaultInfoLore() {
        return List.of(
                "",
                "&7Положи предмет сюда — ты отдаёшь",
                "&7ему &dчасть души&7.",
                "",
                "&7Когда умрёшь, душа запросит",
                "&7удержанные вещи из &5Портала Забвения&7",
                "&7и возродит их вместе с твоей тушкой.",
                "",
                "&8Не все вещи вернутся:",
                "&8часть из них при запросе",
                "&8уходит в небытие."
        );
    }
}
