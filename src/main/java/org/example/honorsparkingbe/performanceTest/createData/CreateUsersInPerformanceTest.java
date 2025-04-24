package org.example.honorsparkingbe.performanceTest.createData;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@DependsOn("createParkingZonesInPerformanceTest")
public class CreateUsersInPerformanceTest extends PerformanceCheckInit {

  @PostConstruct
  @Transactional
  protected void insertUsers() {
    // 1. User 3명 만들기
    CarEntity car1 = CarEntity.builder().carNumber("11가1111").carType(defaultCarType)
        .isElectric(false).build();
    CarEntity car2 = CarEntity.builder().carNumber("22가2222").carType(defaultCarType)
        .isElectric(false).build();
    CarEntity car3 = CarEntity.builder().carNumber("33가3333").carType(defaultCarType)
        .isElectric(false).build();

    MemberEntity member1 = createMember(testUserIDs[0], "user1@example.com", "01011111111", "name1",
        car1);
    MemberEntity member2 = createMember(testUserIDs[1], "user2@example.com", "01022222222", "name2",
        car2);
    MemberEntity member3 = createMember(testUserIDs[2], "user3@example.com", "01033333333", "name3",
        car3);

    carRepository.saveAll(List.of(car1, car2, car3));
    memberRepository.saveAll(List.of(member1, member2, member3));

    /*
     * 2. User 3명 북마크 주차장 만들기
     *   유저 a : ID : performTUser1 PW : performTUser1
     *   즐겨찾기 하는 주차장이 10개입니다.
     *   유저 b : ID : performTUser2 PW : performTUser2
     *   즐겨찾기 하는 주차장이 없습니다.
     *   유저 c : ID : performTUser3 PW : performTUser3
     *   즐겨찾기 주차장 3개입니다
     */

    List<MemberEntity> targetMembers = memberRepository.findAll();
    List<ParkingZoneEntity> parkingZoneEntities = parkingZoneRepository.findAll();
    List<FavoriteParkingZoneEntity> favoriteEntities = new ArrayList<>();

    for (MemberEntity member : targetMembers) {
      String authId = member.getAuthId();

      int favoriteCount = switch (authId) {
        case "performTUser1" -> 10;
        case "performTUser3" -> 3;
        default -> 0;
      };

      if (favoriteCount > 0) {
        List<FavoriteParkingZoneEntity> memberFavorites = parkingZoneEntities.stream()
            .limit(favoriteCount)
            .map(parkingZone -> FavoriteParkingZoneEntity.builder()
                .memberEntity(member)
                .parkingZoneEntity(parkingZone)
                .build())
            .toList();

        favoriteEntities.addAll(memberFavorites);
      }
    }

    favoriteParkingZoneRepository.saveAll(favoriteEntities);

  }

  private MemberEntity createMember(String authId, String email, String phone, String name,
      CarEntity car) {
    return MemberEntity.builder()
        .email(email)
        .phoneNumber(phone)
        .userName(name)
        .carEntity(car)
        .authId(authId)
        .password(passwordEncoder.encode(authId)) // ID와 PW 동일
        .birthday("01-01")
        .birthdayYear(1990)
        .loginPlatform(LoginPlatform.NORMAL)
        .role(MemberRole.ROLE_USER)
        .build();
  }
}
