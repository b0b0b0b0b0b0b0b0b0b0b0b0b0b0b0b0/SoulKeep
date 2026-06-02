package bm.b0b0b0.soulKeep.command;

import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.persistence.PendingRestorePersistence;
import bm.b0b0b0.soulKeep.repository.PendingRestoreRepository;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.service.ChanceCalculationService;
import bm.b0b0b0.soulKeep.util.PendingRestoreCodec;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public final class AdminDebugService {

    private final PlayerProtectionRepository protectionRepository;
    private final PendingRestoreRepository pendingRestoreRepository;
    private final PendingRestorePersistence pendingPersistence;
    private final ChanceCalculationService chanceService;
    private final SoulKeepLog log;

    public AdminDebugService(
            PlayerProtectionRepository protectionRepository,
            PendingRestoreRepository pendingRestoreRepository,
            PendingRestorePersistence pendingPersistence,
            ChanceCalculationService chanceService,
            SoulKeepLog log) {
        this.protectionRepository = protectionRepository;
        this.pendingRestoreRepository = pendingRestoreRepository;
        this.pendingPersistence = pendingPersistence;
        this.chanceService = chanceService;
        this.log = log;
    }

    public void dumpConsole(Player target) {
        log.info("===== SoulKeep debug (console) for " + target.getName() + " =====");
        dumpTarget(target);
        log.info("===== SoulKeep debug end =====");
    }

    public void dump(Player admin, Player target) {
        log.info("===== SoulKeep debug by " + admin.getName() + " for " + target.getName() + " =====");
        dumpTarget(target);
        log.info("===== SoulKeep debug end =====");
    }

    private void dumpTarget(Player target) {
        UUID targetId = target.getUniqueId();
        log.info(target, "uuid=" + targetId);
        log.info(target, "data ready=" + protectionRepository.isReady(targetId));
        Optional<PlayerProtectionData> data = protectionRepository.findCached(targetId);
        if (data.isEmpty()) {
            log.warn(target, "protection cache empty");
        } else {
            PlayerProtectionData protection = data.get();
            log.info(target, "protected count=" + protection.getProtectedCount());
            int index = 0;
            for (Material material : protection.getProtectedMaterials()) {
                log.info(target, "slot #" + index + " type=" + material.name()
                        + " chance=" + chanceService.formatChance(target, material) + "%");
                index++;
            }
        }
        dumpPending(target);
    }

    private void dumpPending(Player target) {
        UUID targetId = target.getUniqueId();
        List<ItemStack> memory = pendingRestoreRepository.peekMemory(targetId);
        if (!memory.isEmpty()) {
            log.info(target, "pending memory: " + memory.size() + " stack(s)");
            log.items("pending memory", memory);
        } else {
            log.info(target, "pending memory: empty");
        }
        try {
            List<PendingRestoreRecord> records = pendingPersistence.peekAll(targetId);
            if (records.isEmpty()) {
                log.info(target, "pending database: empty");
                return;
            }
            log.info(target, "pending database: " + records.size() + " row(s)");
            List<ItemStack> decoded = PendingRestoreCodec.decodeAll(records);
            log.items("pending database", decoded);
            for (int index = 0; index < records.size(); index++) {
                PendingRestoreRecord record = records.get(index);
                log.info(target, "db row #" + (index + 1) + " storage=" + record.getStorageType().name());
            }
        } catch (Exception exception) {
            log.warn(target, "pending database read failed: " + exception.getMessage());
        }
    }
}
