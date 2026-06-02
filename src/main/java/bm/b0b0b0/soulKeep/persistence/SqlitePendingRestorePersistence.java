package bm.b0b0b0.soulKeep.persistence;

import bm.b0b0b0.soulKeep.database.PendingRestoreDao;
import bm.b0b0b0.soulKeep.model.PendingRestoreRecord;

import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

public final class SqlitePendingRestorePersistence implements PendingRestorePersistence {

    private final PendingRestoreDao dao;

    public SqlitePendingRestorePersistence(PendingRestoreDao dao) {
        this.dao = dao;
    }

    @Override
    public void saveAll(UUID playerId, List<PendingRestoreRecord> records) throws SQLException {
        dao.insertAll(playerId, records);
    }

    @Override
    public List<PendingRestoreRecord> takeAll(UUID playerId) throws SQLException {
        return dao.takeAll(playerId);
    }

    @Override
    public boolean hasAny(UUID playerId) throws SQLException {
        return dao.exists(playerId);
    }
}
