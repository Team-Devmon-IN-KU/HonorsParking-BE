package org.example.honorsparkingbe.repository.internal;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.PayEntity;

public interface BulkPayEntityRepository {

  List<PayEntity> bulkInsertAndUpdate(List<PayEntity> payEntities);
}
