package bm.b0b0b0.soulKeep.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class DatabaseSettings {

    private final String fileName;
    private final int poolSize;

    public DatabaseSettings(SoulKeepSettings settings) {
        this.fileName = settings.storage.database.file;
        this.poolSize = settings.storage.database.poolSize;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public File resolveDatabaseFile(JavaPlugin plugin) {
        return new File(plugin.getDataFolder(), fileName);
    }
}
