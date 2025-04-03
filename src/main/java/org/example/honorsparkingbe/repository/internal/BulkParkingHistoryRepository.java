package org.example.honorsparkingbe.repository.internal;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;

public interface BulkParkingHistoryRepository {

  void bulkInsertAndUpdate(List<ParkingHistoryEntity> parkingHistoryEntities);
}
