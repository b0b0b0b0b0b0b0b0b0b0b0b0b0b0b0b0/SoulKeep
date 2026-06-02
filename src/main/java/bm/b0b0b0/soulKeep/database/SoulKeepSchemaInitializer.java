package bm.b0b0b0.soulKeep.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SoulKeepSchemaInitializer {

    private static final String PROTECTION_TABLE = """
            CREATE TABLE IF NOT EXISTS soul_protection (
                player_uuid TEXT PRIMARY KEY NOT NULL,
                materials TEXT NOT NULL
            )
            """;
    private static final String PENDING_TABLE = """
            CREATE TABLE IF NOT EXISTS soul_pending_restore (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_uuid TEXT NOT NULL,
                storage_type TEXT NOT NULL,
                material TEXT,
                amount INTEGER,
                item_data BLOB,
                created_at INTEGER NOT NULL
            )
            """;
    private static final String PENDING_INDEX = """
            CREATE INDEX IF NOT EXISTS idx_soul_pending_player
            ON soul_pending_restore (player_uuid)
            """;

    private final DatabaseConnectionProvider connectionProvider;

    public SoulKeepSchemaInitializer(DatabaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void initialize() {
        try (Connection connection = connectionProvider.openConnection();
             Statement statement = connection.createStatement()) {
            statement.execute(PROTECTION_TABLE);
            statement.execute(PENDING_TABLE);
            statement.execute(PENDING_INDEX);
            statement.execute("PRAGMA journal_mode=WAL");
            migrateLegacyPendingTable(connection);
        } catch (SQLException exception) {
            throw new IllegalStateException("Failed to initialize database schema", exception);
        }
    }

    private void migrateLegacyPendingTable(Connection connection) throws SQLException {
        if (!tableExists(connection, "soul_pending_restore")) {
            return;
        }
        if (columnExists(connection, "soul_pending_restore", "storage_type")) {
            return;
        }
        try (Statement statement = connection.createStatement()) {
            statement.execute("ALTER TABLE soul_pending_restore ADD COLUMN storage_type TEXT NOT NULL DEFAULT 'BINARY'");
            statement.execute("ALTER TABLE soul_pending_restore ADD COLUMN material TEXT");
            statement.execute("ALTER TABLE soul_pending_restore ADD COLUMN amount INTEGER");
        }
    }

    private boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(null, null, tableName, null)) {
            return resultSet.next();
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getColumns(null, null, tableName, columnName)) {
            return resultSet.next();
        }
    }
}
