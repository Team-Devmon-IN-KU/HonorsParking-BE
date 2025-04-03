package org.example.honorsparkingbe.repository.internal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.hibernate.Session;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public class BulkParkingHistoryRepositoryImpl implements BulkParkingHistoryRepository {

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public void bulkInsertAndUpdate(List<ParkingHistoryEntity> parkingHistoryEntities) {
    String sql = """
            INSERT INTO parkingHistory (id, carId, memberId, parkingZoneId, cardId, payId,
                                         entranceTime, exitTime, paymentType, deleteAt)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
            ON DUPLICATE KEY UPDATE
                carId = VALUES(carId),
                memberId = VALUES(memberId),
                parkingZoneId = VALUES(parkingZoneId),
                cardId = VALUES(cardId),
                payId = VALUES(payId),
                entranceTime = VALUES(entranceTime),
                exitTime = VALUES(exitTime),
                paymentType = VALUES(paymentType),
                deleteAt = VALUES(deleteAt)
        """;

    EntityManager entityManager = this.entityManager;
    entityManager.unwrap(Session.class).doWork(connection -> {
      try (PreparedStatement ps = connection.prepareStatement(sql)) {
        int batchSize = 100; // 배치 크기 설정
        int count = 0;

        for (ParkingHistoryEntity entity : parkingHistoryEntities) {
          ps.setLong(1, entity.getId());
          ps.setLong(2, entity.getCarEntity().getId());
          ps.setLong(3, entity.getMemberEntity().getId());
          ps.setLong(4, entity.getParkingZoneEntity().getId());
          ps.setObject(5, entity.getCardEntity() != null ? entity.getCardEntity().getId() : null);
          ps.setObject(6, entity.getPayEntity() != null ? entity.getPayEntity().getId() : null);
          ps.setTimestamp(7, Timestamp.valueOf(entity.getEntranceTime()));
          ps.setObject(8,
              entity.getExitTime() != null ? Timestamp.valueOf(entity.getExitTime()) : null);
          ps.setString(9, entity.getPaymentType().name());
          ps.setObject(10,
              entity.getDeleteAt() != null ? Timestamp.valueOf(entity.getDeleteAt()) : null);

          ps.addBatch();
          count++;

          if (count % batchSize == 0) {
            ps.executeBatch();  // 배치 실행
          }
        }
        ps.executeBatch();  // 남은 데이터 실행
      } catch (SQLException e) {
        throw new RuntimeException("배치 Insert 중 오류 발생", e);
      }
    });
  }
}
