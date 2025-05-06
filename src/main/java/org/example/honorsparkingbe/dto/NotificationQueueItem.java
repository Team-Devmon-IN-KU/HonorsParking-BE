package org.example.honorsparkingbe.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.example.honorsparkingbe.domain.enums.NotiChannel;
import org.example.honorsparkingbe.domain.enums.NotiEventType;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class NotificationQueueItem {

  NotiChannel notiChannel;
  NotiEventType notiEventType;

  String phoneNumber;
  String carNumber;
  LocalDateTime entranceTime;

  Integer retryCount;
}
