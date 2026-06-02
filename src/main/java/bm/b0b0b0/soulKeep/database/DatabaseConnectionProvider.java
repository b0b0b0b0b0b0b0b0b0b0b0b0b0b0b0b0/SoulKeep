package bm.b0b0b0.soulKeep.database;

import bm.b0b0b0.soulKeep.config.DatabaseSettings;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnectionProvider {

    private final HikariDataSource dataSource;

    public DatabaseConnectionProvider(JavaPlugin plugin, DatabaseSettings settings) {
        File databaseFile = settings.resolveDatabaseFile(plugin);
        File parent = databaseFile.getParentFile();
        if (parent != null) {
            parent.mkdirs();
        }
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + databaseFile.getAbsolutePath());
        config.setMaximumPoolSize(settings.getPoolSize());
        config.setPoolName("SoulKeep-SQLite");
        config.setDriverClassName("org.sqlite.JDBC");
        this.dataSource = new HikariDataSource(config);
        new SoulKeepSchemaInitializer(this).initialize();
    }

    public Connection openConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public void shutdown() {
        dataSource.close();
    }
}
