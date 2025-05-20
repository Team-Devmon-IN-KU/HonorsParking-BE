package org.example.honorsparkingbe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class PaginationResponse {

  private long currentPage;
  private long totalPages;
  private long pageSize;
  private long totalItems;
}
