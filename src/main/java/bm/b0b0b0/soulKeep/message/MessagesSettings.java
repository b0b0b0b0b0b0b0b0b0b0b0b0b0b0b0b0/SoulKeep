package bm.b0b0b0.soulKeep.message;

import net.elytrium.serializer.annotations.RegisterPlaceholders;
import net.elytrium.serializer.language.object.YamlSerializable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@RegisterPlaceholders({"prefix"})
public final class MessagesSettings extends YamlSerializable {

    public String prefix = "&8[&6SoulKeep&8] &r";

    public CommandSection command = new CommandSection();
    public ProtectionSection protection = new ProtectionSection();
    public DeathSection death = new DeathSection();
    public GuiSection gui = new GuiSection();

    public void load(JavaPlugin plugin) {
        Path dataFolder = plugin.getDataFolder().toPath();
        Path messagesFile = dataFolder.resolve("lang/messages.yml");
        Path legacyFile = dataFolder.resolve("messages.yml");
        try {
            Files.createDirectories(messagesFile.getParent());
            if (Files.notExists(messagesFile) && Files.isRegularFile(legacyFile)) {
                Files.move(legacyFile, messagesFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to prepare lang/messages.yml", exception);
        }
        reload(messagesFile);
    }

    public static final class CommandSection {
        public String playerOnly = "{prefix}&cТолько для игроков.";
        public String unknownSubcommand = "{prefix}&cНеизвестная подкоманда.";
        public String noPermission = "{prefix}&cНет прав.";
        public String playerNotFound = "{prefix}&cИгрок не найден.";
        public String debugDone = "{prefix}&7Debug записан в консоль сервера (&f{player}&7).";
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
        public String fillOrder = "{prefix}&cСначала заполните предыдущий пустой слот.";
        public String listEmpty = "{prefix}&7Список защиты пуст.";
        public String listHeader = "{prefix}&6Защищённые типы (&f{current}&7/&f{max}&6):";
        public String listEntry = "&7- &f{material} &8(&7{chance}%&8)";
        public String cleared = "{prefix}&eСписок защиты очищен.";
    }

    public static final class DeathSection {
        public String saved = "{prefix}&aСохранено предметов: &f{count}";
        public String nothingSaved = "{prefix}&7На этот раз ничего не удержано душой.";
    }

    public static final class GuiSection {
        public String fillerName = " ";
        public String emptySlotName = "&7Пустой слот";
        public String lockedSlotName = "&cЗакрыто";
        public String lockedSlotLore = "&7Нужен донат-ранг";
        public String protectedSlotName = "&f{material}";
        public String protectedSlotLore = "&7Шанс: &f{chance}%&7, ЛКМ — убрать";
        public String slotLocked = "{prefix}&cЭтот слот недоступен. Повысьте ранг.";
    }
}
