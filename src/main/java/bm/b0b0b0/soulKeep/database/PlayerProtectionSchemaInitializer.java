package bm.b0b0b0.soulKeep.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public final class PlayerProtectionSchemaInitializer {

    private static final String CREATE_TABLE = """
            CREATE TABLE IF NOT EXISTS soul_protection (
                player_uuid TEXT PRIMARY KEY NOT NULL,
                materials TEXT NOT NULL
            )
            """;

    private final DatabaseConnectionProvider connectionProvider;

    public PlayerProtectionSchemaInitializer(DatabaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void initialize() {
        try (Connection connection = connectionProvider.openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(CREATE_TABLE);
            statement.execute("PRAGMA journal_mode=WAL");
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to initialize database schema", exception);
        }
    }
}
