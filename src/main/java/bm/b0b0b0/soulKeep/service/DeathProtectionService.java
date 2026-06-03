package bm.b0b0b0.soulKeep.service;

import bm.b0b0b0.soulKeep.config.ProtectionSettings;
import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.model.ProtectionEntry;
import bm.b0b0b0.soulKeep.repository.PendingRestoreRepository;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.util.NonEmptyContainerChecker;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class DeathProtectionService {

    private final PlayerProtectionRepository repository;
    private ProtectionSettings protectionSettings;
    private ChanceCalculationService chanceService;
    private final PendingRestoreRepository pendingRestoreRepository;
    private MessageService messages;

    public DeathProtectionService(
            PlayerProtectionRepository repository,
            ProtectionSettings protectionSettings,
            ChanceCalculationService chanceService,
            PendingRestoreRepository pendingRestoreRepository,
            MessageService messages) {
        this.repository = repository;
        this.protectionSettings = protectionSettings;
        this.chanceService = chanceService;
        this.pendingRestoreRepository = pendingRestoreRepository;
        this.messages = messages;
    }

    public void rebind(
            ProtectionSettings protectionSettings,
            ChanceCalculationService chanceService,
            MessageService messages) {
        this.protectionSettings = protectionSettings;
        this.chanceService = chanceService;
        this.messages = messages;
    }

    public void handleDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        Optional<PlayerProtectionData> dataOptional = repository.findCached(player.getUniqueId());
        if (dataOptional.isEmpty()) {
            return;
        }
        PlayerProtectionData data = dataOptional.get();
        if (data.getProtectedCount() == 0) {
            return;
        }
        List<ItemStack> saved = new ArrayList<>();
        Map<Material, Boolean> rollResults = new HashMap<>();
        for (ProtectionEntry entry : data.getEntries()) {
            Material material = entry.material();
            Boolean success = rollResults.get(material);
            if (success == null) {
                success = chanceService.rollSuccess(player, material);
                rollResults.put(material, success);
            }
            if (!success) {
                continue;
            }
            int limit = protectionSettings.isAllowStacks() ? entry.amount() : 1;
            collectDrops(event.getDrops(), material, limit, saved);
        }
        if (saved.isEmpty()) {
            messages.send(player, "death.nothing-saved");
            return;
        }
        pendingRestoreRepository.stage(
                player.getUniqueId(),
                saved,
                () -> messages.send(player, "death.saved", Map.of("count", String.valueOf(saved.size()))));
    }

    private static void collectDrops(List<ItemStack> drops, Material material, int limit, List<ItemStack> saved) {
        int collected = 0;
        Iterator<ItemStack> iterator = drops.iterator();
        while (iterator.hasNext() && collected < limit) {
            ItemStack drop = iterator.next();
            if (drop == null || drop.getType() != material) {
                continue;
            }
            if (NonEmptyContainerChecker.hasContents(drop)) {
                continue;
            }
            int take = Math.min(drop.getAmount(), limit - collected);
            ItemStack part = drop.clone();
            part.setAmount(take);
            saved.add(part);
            collected += take;
            if (drop.getAmount() > take) {
                drop.setAmount(drop.getAmount() - take);
            } else {
                iterator.remove();
            }
        }
    }

    public void deliverPending(Player player) {
        pendingRestoreRepository.deliverIfPresent(player);
    }
}
