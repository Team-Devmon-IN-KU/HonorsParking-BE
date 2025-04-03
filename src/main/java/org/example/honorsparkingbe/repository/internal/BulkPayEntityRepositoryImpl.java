package org.example.honorsparkingbe.repository.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.PayEntity;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class BulkPayEntityRepositoryImpl implements BulkPayEntityRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public List<PayEntity> bulkInsertAndUpdate(List<PayEntity> payEntities) {
    String sql = """
        INSERT INTO pay (id, amount, paidAt, memberId)
        VALUES (?, ?, ?, ?)
        ON DUPLICATE KEY UPDATE
            amount = VALUES(amount),
            paidAt = VALUES(paidAt),
            memberId = VALUES(memberId)
        """;

    entityManager.unwrap(Session.class).doWork(connection -> {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        int batchSize = 100;
        int count = 0;

        for (PayEntity entity : payEntities) {
          ps.setLong(1, entity.getId());
          ps.setInt(2, entity.getAmount());
          ps.setTimestamp(3, Timestamp.valueOf(entity.getPaidAt()));
          ps.setLong(4, entity.getMemberEntity().getId());

          ps.addBatch();
          count++;

          if (count % batchSize == 0) {
            ps.executeBatch();
          }
        }
        ps.executeBatch(); // 남은 데이터 처리
      } catch (SQLException e) {
        throw new RuntimeException("배치 Insert 중 오류 발생", e);

      }
    });

    return payEntities;
  }
}
