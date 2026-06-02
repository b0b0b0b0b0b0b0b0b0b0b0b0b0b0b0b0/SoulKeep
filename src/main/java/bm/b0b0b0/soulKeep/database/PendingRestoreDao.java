package bm.b0b0b0.soulKeep.database;

import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;
import org.bukkit.Material;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class PendingRestoreDao {

    private static final String INSERT_SQL = """
            INSERT INTO soul_pending_restore (player_uuid, storage_type, material, amount, item_data, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
            """;
    private static final String SELECT_SQL = """
            SELECT storage_type, material, amount, item_data
            FROM soul_pending_restore
            WHERE player_uuid = ?
            ORDER BY id ASC
            """;
    private static final String DELETE_SQL =
            "DELETE FROM soul_pending_restore WHERE player_uuid = ?";
    private static final String EXISTS_SQL =
            "SELECT 1 FROM soul_pending_restore WHERE player_uuid = ? LIMIT 1";

    private final DatabaseConnectionProvider connectionProvider;

    public PendingRestoreDao(DatabaseConnectionProvider connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    public void insertAll(UUID playerId, List<PendingRestoreRecord> records) throws SQLException {
        if (records.isEmpty()) {
            return;
        }
        long createdAt = System.currentTimeMillis();
        try (Connection connection = connectionProvider.openConnection();
             PreparedStatement statement = connection.prepareStatement(INSERT_SQL)) {
            connection.setAutoCommit(false);
            try {
                for (PendingRestoreRecord record : records) {
                    statement.setString(1, playerId.toString());
                    statement.setString(2, record.getStorageType().name());
                    if (record.getStorageType() == PendingRestoreRecord.StorageType.MATERIAL) {
                        statement.setString(3, record.getMaterial().name());
                        statement.setInt(4, record.getAmount());
                        statement.setBytes(5, null);
                    } else {
                        statement.setString(3, null);
                        statement.setInt(4, 0);
                        statement.setBytes(5, record.getItemData());
                    }
                    statement.setLong(6, createdAt);
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<PendingRestoreRecord> takeAll(UUID playerId) throws SQLException {
        try (Connection connection = connectionProvider.openConnection()) {
            connection.setAutoCommit(false);
            try {
                List<PendingRestoreRecord> records = readAll(connection, playerId);
                if (records.isEmpty()) {
                    connection.commit();
                    return records;
                }
                deleteAll(connection, playerId);
                connection.commit();
                return records;
            } catch (SQLException exception) {
                connection.rollback();
                throw exception;
            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public List<PendingRestoreRecord> peekAll(UUID playerId) throws SQLException {
        try (Connection connection = connectionProvider.openConnection()) {
            return readAll(connection, playerId);
        }
    }

    public boolean exists(UUID playerId) throws SQLException {
        try (Connection connection = connectionProvider.openConnection();
             PreparedStatement statement = connection.prepareStatement(EXISTS_SQL)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private List<PendingRestoreRecord> readAll(Connection connection, UUID playerId) throws SQLException {
        List<PendingRestoreRecord> records = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement(SELECT_SQL)) {
            statement.setString(1, playerId.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    records.add(readRecord(resultSet));
                }
            }
        }
        return records;
    }

    private PendingRestoreRecord readRecord(ResultSet resultSet) throws SQLException {
        String storageType = resultSet.getString("storage_type");
        if (PendingRestoreRecord.StorageType.MATERIAL.name().equals(storageType)) {
            Material material = Material.valueOf(resultSet.getString("material"));
            int amount = resultSet.getInt("amount");
            return PendingRestoreRecord.material(material, amount);
        }
        return PendingRestoreRecord.binary(resultSet.getBytes("item_data"));
    }

    private void deleteAll(Connection connection, UUID playerId) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setString(1, playerId.toString());
            statement.executeUpdate();
        }
    }
}
