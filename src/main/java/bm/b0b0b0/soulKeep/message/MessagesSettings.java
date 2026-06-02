package bm.b0b0b0.soulKeep.message;

import net.elytrium.serializer.annotations.RegisterPlaceholders;
import net.elytrium.serializer.language.object.YamlSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;

@RegisterPlaceholders({"prefix"})
public final class MessagesSettings extends YamlSerializable {

    public String prefix = "&8[&6SoulKeep&8] &r";

    public CommandSection command = new CommandSection();
    public ProtectionSection protection = new ProtectionSection();
    public DeathSection death = new DeathSection();

    public void load(JavaPlugin plugin) {
        plugin.getDataFolder().mkdirs();
        reload(plugin.getDataFolder().toPath().resolve("messages.yml"));
    }

    public static final class CommandSection {
        public String playerOnly = "{prefix}&cТолько для игроков.";
        public String unknownSubcommand = "{prefix}&cНеизвестная подкоманда.";
    }

    public static final class ProtectionSection {
        public String added = "{prefix}&aТип &f{material} &aдобавлен в защиту (&f{current}&7/&f{max}&a).";
        public String removed = "{prefix}&eТип &f{material} &eубран из защиты.";
        public String notProtected = "{prefix}&cТип &f{material} &cне в списке защиты.";
        public String alreadyProtected = "{prefix}&cТип &f{material} &cуже защищён.";
        public String limitReached = "{prefix}&cЛимит слотов: &f{max}&c. Уберите тип или повысьте ранг.";
        public String emptyHand = "{prefix}&cВозьмите предмет в руку или укажите тип.";
        public String invalidMaterial = "{prefix}&cНеизвестный тип предмета: &f{input}";
        public String dataLoading = "{prefix}&7Данные ещё загружаются, подождите.";
        public String listEmpty = "{prefix}&7Список защиты пуст.";
        public String listHeader = "{prefix}&6Защищённые типы (&f{current}&7/&f{max}&6):";
        public String listEntry = "&7- &f{material} &8(&7{chance}%&8)";
        public String cleared = "{prefix}&eСписок защиты очищен.";
    }

    public static final class DeathSection {
        public String saved = "{prefix}&aСохранено предметов: &f{count}";
        public String nothingSaved = "{prefix}&7На этот раз ничего не удержано душой.";
    }
}
