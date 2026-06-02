package bm.b0b0b0.soulKeep.config;

import net.elytrium.serializer.annotations.Comment;
import net.elytrium.serializer.annotations.CommentValue;
import net.elytrium.serializer.language.object.YamlSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class GuiMainSettings extends YamlSerializable {

    @Comment(@CommentValue("Заголовок меню /keepsoul"))
    public String title = "        &#C084FC✦ &#721ddbЗащита душой &#C084FC✦";

    @Comment(@CommentValue("Строк в сундуке (1–6)"))
    public int rows = 3;

    @Comment(@CommentValue("Номера слотов защиты в инвентаре меню (0–53)"))
    public List<Integer> slotPositions = List.of(10, 11, 12, 13, 14, 15, 16);

    public String fillerMaterial = "PURPLE_STAINED_GLASS_PANE";
    public String emptySlotMaterial = "GRAY_STAINED_GLASS_PANE";
    public String lockedSlotMaterial = "BARRIER";

    public String fillerName = " ";
    public String emptySlotName = "&#8B8B8B◦ &#AAAAAAПустой слот";
    public String lockedSlotName = "&#FF5555✖ &#FFAAAAЗакрыто";
    public String lockedSlotLore = "&#757575Нужен донат-ранг";

    @Comment({
            @CommentValue("Текст барьера по номеру слота (ключ = сколько слотов нужно всего)."),
            @CommentValue("2 — второй слот (keepsouls.slots.2), 3 — третий и т.д.")
    })
    public Map<String, LockedSlotTierSection> lockedSlotTiers = defaultLockedSlotTiers();
    public String protectedSlotName = "&#F3E8FF{material}";
    public String protectedSlotLore = "&#AAAAAAШанс: &#E9D5FF{chance}% &#757575· ЛКМ убрать";

    @Comment(@CommentValue("Слот справки внизу по центру (3 ряда = 22). -1 — выключить"))
    public int infoSlot = 22;

    public String infoMaterial = "KNOWLEDGE_BOOK";
    public String infoName = "&#C084FC✦ &#C084FCСправка";
    public List<String> infoLore = defaultInfoLore();

    public void load(JavaPlugin plugin) {
        Path file = plugin.getDataFolder().toPath().resolve("gui/main.yml");
        file.getParent().toFile().mkdirs();
        reload(file);
    }

    public static final class LockedSlotTierSection {
        public String name = "&cЗакрыто";
        public String lore = "&7Нужен донат-ранг";
    }

    private static Map<String, LockedSlotTierSection> defaultLockedSlotTiers() {
        Map<String, LockedSlotTierSection> tiers = new LinkedHashMap<>();
        tiers.put("2", tier("&#FFD700✦ VIP", "&#AAAAAAНужен &#FFD700VIP"));
        tiers.put("3", tier("&#4FC3F7✦ Premium", "&#AAAAAAНужен &#4FC3F7Premium"));
        tiers.put("4", tier("&#B388FF✦ Elite", "&#AAAAAAНужен &#B388FFElite"));
        tiers.put("5", tier("&#FF80AB✦ Legend", "&#AAAAAAНужен &#FF80ABLegend"));
        tiers.put("6", tier("&#FF5555✦ Master", "&#AAAAAAНужен &#FF5555Master"));
        tiers.put("7", tier("&#FFE066✦ Ultimate", "&#AAAAAAНужен &#FFE066Ultimate"));
        return tiers;
    }

    private static LockedSlotTierSection tier(String name, String lore) {
        LockedSlotTierSection section = new LockedSlotTierSection();
        section.name = name;
        section.lore = lore;
        return section;
    }

    private static List<String> defaultInfoLore() {
        return List.of(
                "",
                "&#AAAAAAПоложи предмет в свободный слот —",
                "&#AAAAAAты отдаёшь ему &#C084FCчасть души&#AAAAAA.",
                "",
                "&#AAAAAAКогда умрёшь, душа запросит",
                "&#AAAAAAвещи из &#9D4EDDПортала Забвения",
                "&#AAAAAAи вернёт их с твоей тушкой.",
                "",
                "&#757575Не все вещи вернутся:",
                "&#757575часть уходит в небытие."
        );
    }
}
