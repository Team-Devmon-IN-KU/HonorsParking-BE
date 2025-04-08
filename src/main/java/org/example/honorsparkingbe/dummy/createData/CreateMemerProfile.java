package org.example.honorsparkingbe.dummy.createData;

import jakarta.annotation.PostConstruct;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.springframework.transaction.annotation.Transactional;

public class CreateMemerProfile {

  @PostConstruct
  @Transactional
  public void insertParkingZoneDummyData() {

    CarEntity carEntity = CarEntity.builder()
        .carNumber("33나3333")

        .build();

    MemberEntity dummyMember = MemberEntity.builder()
        .userName("모두의 영웅")
        .authId("devmon")
        .password("$2a$10$C8je5zzdP5cHiRhvE7yJrO1hQEqRclEjcOs/7pCssFsc2Z1w.o8EO")
        .loginPlatform(LoginPlatform.NORMAL)
        .role(MemberRole.ROLE_USER)
        .phoneNumber("01095448329")
        .build();
  }
}
