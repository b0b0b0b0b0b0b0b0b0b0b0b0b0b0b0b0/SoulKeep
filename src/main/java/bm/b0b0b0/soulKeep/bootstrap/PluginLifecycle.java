package bm.b0b0b0.soulKeep.bootstrap;

import bm.b0b0b0.soulKeep.database.DatabaseConnectionProvider;
import bm.b0b0b0.soulKeep.repository.PlayerProtectionRepository;

public final class PluginLifecycle {

    private final DatabaseConnectionProvider databaseConnectionProvider;
    private final PlayerProtectionRepository playerProtectionRepository;

    public PluginLifecycle(
            DatabaseConnectionProvider databaseConnectionProvider,
            PlayerProtectionRepository playerProtectionRepository) {
        this.databaseConnectionProvider = databaseConnectionProvider;
        this.playerProtectionRepository = playerProtectionRepository;
    }

    public void shutdown() {
        playerProtectionRepository.flushAllSync();
        databaseConnectionProvider.shutdown();
    }
}
