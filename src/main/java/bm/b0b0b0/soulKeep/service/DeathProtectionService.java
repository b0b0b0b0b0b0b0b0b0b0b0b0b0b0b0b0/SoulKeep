package bm.b0b0b0.soulKeep.service;

import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.repository.PendingRestoreRepository;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.util.SoulKeepLog;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DeathProtectionService {

    private final PlayerProtectionRepository repository;
    private final ChanceCalculationService chanceService;
    private final PendingRestoreRepository pendingRestoreRepository;
    private final MessageService messages;
    private final SoulKeepLog log;

    public DeathProtectionService(
            PlayerProtectionRepository repository,
            ChanceCalculationService chanceService,
            PendingRestoreRepository pendingRestoreRepository,
            MessageService messages,
            SoulKeepLog log) {
        this.repository = repository;
        this.chanceService = chanceService;
        this.pendingRestoreRepository = pendingRestoreRepository;
        this.messages = messages;
        this.log = log;
    }

    public void handleDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Optional<PlayerProtectionData> dataOptional = repository.findCached(player.getUniqueId());
        if (dataOptional.isEmpty()) {
            log.info(player, "death: no cached protection data");
            return;
        }
        PlayerProtectionData data = dataOptional.get();
        if (data.getProtectedCount() == 0) {
            log.info(player, "death: protection list empty");
            return;
        }
        log.info(player, "death: processing drops, protected types=" + data.getProtectedCount());
        List<ItemStack> saved = new ArrayList<>();
        Iterator<ItemStack> iterator = event.getDrops().iterator();
        while (iterator.hasNext()) {
            ItemStack drop = iterator.next();
            if (drop == null || drop.getType().isAir()) {
                continue;
            }
            Material material = drop.getType();
            if (!data.isProtected(material)) {
                continue;
            }
            if (!chanceService.rollSuccess(player, material)) {
                log.info(player, "death: roll failed for " + material.name());
                log.item("death roll failed", drop);
                continue;
            }
            log.info(player, "death: roll ok, saving " + material.name());
            log.item("death saved", drop);
            saved.add(drop.clone());
            iterator.remove();
        }
        if (saved.isEmpty()) {
            log.info(player, "death: nothing saved");
            messages.send(player, "death.nothing-saved");
            return;
        }
        log.info(player, "death: staging " + saved.size() + " stack(s)");
        pendingRestoreRepository.stage(
                player.getUniqueId(),
                saved,
                () -> messages.send(player, "death.saved", Map.of("count", String.valueOf(saved.size()))));
    }

    public void deliverPending(Player player) {
        log.info(player, "deliver: requested");
        pendingRestoreRepository.deliverIfPresent(player);
    }
}
