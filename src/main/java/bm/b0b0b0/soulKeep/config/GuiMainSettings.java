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

    @Comment(@CommentValue("Слот справки внизу по центру (3 ряда = 22). -1 — выключить"))
    public int infoSlot = 22;

    public String infoMaterial = "KNOWLEDGE_BOOK";

    public void load(JavaPlugin plugin) {
        Path file = plugin.getDataFolder().toPath().resolve("gui/main.yml");
        file.getParent().toFile().mkdirs();
        reload(file);
    }
}
