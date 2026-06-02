package bm.b0b0b0.soulKeep.service;

import bm.b0b0b0.soulKeep.message.MessageService;
import bm.b0b0b0.soulKeep.model.PlayerProtectionData;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;
import bm.b0b0b0.soulKeep.store.PendingRestoreStore;
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
    private final PendingRestoreStore pendingStore;
    private final MessageService messages;
    private final InventoryRestoreService inventoryRestoreService;

    public DeathProtectionService(
            PlayerProtectionRepository repository,
            ChanceCalculationService chanceService,
            PendingRestoreStore pendingStore,
            MessageService messages,
            InventoryRestoreService inventoryRestoreService) {
        this.repository = repository;
        this.chanceService = chanceService;
        this.pendingStore = pendingStore;
        this.messages = messages;
        this.inventoryRestoreService = inventoryRestoreService;
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
                continue;
            }
            saved.add(drop.clone());
            iterator.remove();
        }
        if (saved.isEmpty()) {
            messages.send(player, "death.nothing-saved");
            return;
        }
        pendingStore.put(player.getUniqueId(), saved);
        messages.send(player, "death.saved", Map.of("count", String.valueOf(saved.size())));
    }

    public void handleRespawn(Player player) {
        if (!pendingStore.hasPending(player.getUniqueId())) {
            return;
        }
        List<ItemStack> stacks = pendingStore.take(player.getUniqueId());
        inventoryRestoreService.giveToPlayer(player, stacks);
    }
}
