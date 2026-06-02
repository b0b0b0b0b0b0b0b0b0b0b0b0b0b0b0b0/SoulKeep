package bm.b0b0b0.soulKeep.database;

import bm.b0b0b0.soulKeep.model.PlayerProtectionData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public final class PlayerProtectionDao {

    private static final String SELECT_SQL =
            "SELECT materials FROM soul_protection WHERE player_uuid = ?";
    private static final String UPSERT_SQL = """
            INSERT INTO soul_protection (player_uuid, materials)
            VALUES (?, ?)
            ON CONFLICT(player_uuid) DO UPDATE SET materials = excluded.materials
            """;

    private final DatabaseConnectionProvider connectionProvider;

    public PlayerProtectionDao(DatabaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public PlayerProtectionData load(UUID playerId) throws SQLException {
        try (Connection connection = connectionProvider.openConnection();
             PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return new PlayerProtectionData(playerId);
                }
                String encoded = resultSet.getString("materials");
                return new PlayerProtectionData(playerId, ProtectedMaterialCodec.decode(encoded));
            }
        }
    }

    public void save(PlayerProtectionData data) throws SQLException {
        String encoded = ProtectedMaterialCodec.encode(data.getEntries());
        try (Connection connection = connectionProvider.openConnection();
             PreparedStatement statement = connection.prepareStatement(UPSERT_SQL)) {
            statement.setString(1, data.getPlayerId().toString());
            statement.setString(2, encoded);
            statement.executeUpdate();
        }
    }
}
