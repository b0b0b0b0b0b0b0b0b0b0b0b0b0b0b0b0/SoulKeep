package bm.b0b0b0.soulKeep.persistence;

import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public interface PendingRestorePersistence {

    void saveAll(UUID playerId, List<PendingRestoreRecord> records) throws SQLException;

    List<PendingRestoreRecord> takeAll(UUID playerId) throws SQLException;

    boolean hasAny(UUID playerId) throws SQLException;
}
