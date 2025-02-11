package org.example.honorsparkingbe.unit.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.persistence.EntityManager;
import java.util.List;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.CityEntity;
import org.example.honorsparkingbe.domain.entity.DistrictEntity;
import org.example.honorsparkingbe.domain.entity.EupMyeonDongEntity;
import org.example.honorsparkingbe.domain.entity.FavoriteParkingZoneEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.entity.ParkingZoneEntity;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.repository.CarRepository;
import org.example.honorsparkingbe.repository.CityRepository;
import org.example.honorsparkingbe.repository.DistrictRepository;
import org.example.honorsparkingbe.repository.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.MemberRepository;
import org.example.honorsparkingbe.repository.ParkingZoneRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

@DataJpaTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:config/application-test.yml")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class FavoriteParkingZoneRepositoryTest {

  @Autowired
  private FavoriteParkingZoneRepository favoriteParkingZoneRepository;
  @Autowired
  private MemberRepository memberRepository;
  @Autowired
  private CarRepository carRepository;
  @Autowired
  private CityRepository cityRepository;
  @Autowired
  private ParkingZoneRepository parkingZoneRepository;
  @Autowired
  private DistrictRepository districtRepository;
  @Autowired
  private EupMyeonDongRepository eupMyeonDongRepository;

  private EntityManager entityManager;

  private CarEntity sampleCar1;
  private CarEntity sampleCar2;
  private CarEntity sampleCar3;

  private MemberEntity memberWith12Favorite;
  private MemberEntity memberWith3Favorite;
  private MemberEntity memberWith0Favorite;

  private CityEntity seoul;
  private CityEntity busan;
  private CityEntity daejeon;

  private DistrictEntity sampleDistrict;
  private EupMyeonDongEntity sampleEupMyeonDong;

  private ParkingZoneEntity seoulParking;
  private ParkingZoneEntity busanParking;
  private ParkingZoneEntity daejeonParking;

  private FavoriteParkingZoneEntity favoriteParkingZone1;
  private FavoriteParkingZoneEntity favoriteParkingZone2;
  private FavoriteParkingZoneEntity favoriteParkingZone3;

  //paging
  private int pagePerItem = 10;

  @BeforeEach
  void setUp() {
    // 도시 엔티티 저장
    seoul = cityRepository.save(CityEntity.builder().name("서울").build());
    busan = cityRepository.save(CityEntity.builder().name("부산").build());
    daejeon = cityRepository.save(CityEntity.builder().name("대전").build());

    // 구역 엔티티 저장
    sampleDistrict = districtRepository.save(DistrictEntity.builder().name("중구").build());
    sampleEupMyeonDong = eupMyeonDongRepository.save(
        EupMyeonDongEntity.builder().name("데브몬동").build());

    // 주차장 엔티티 설정 후 저장
    seoulParking = parkingZoneRepository.save(
        ParkingZoneEntity.builder()
            .cityEntity(seoul)
            .districtEntity(sampleDistrict)
            .eupMyeonDongEntity(sampleEupMyeonDong)
            .build()
    );
    busanParking = parkingZoneRepository.save(
        ParkingZoneEntity.builder()
            .cityEntity(busan)
            .districtEntity(sampleDistrict)
            .eupMyeonDongEntity(sampleEupMyeonDong)
            .build()
    );
    daejeonParking = parkingZoneRepository.save(
        ParkingZoneEntity.builder()
            .cityEntity(daejeon)
            .districtEntity(sampleDistrict)
            .eupMyeonDongEntity(sampleEupMyeonDong)
            .build()
    );

    // 자동차 엔티티 저장
    sampleCar1 = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber1").build());
    sampleCar2 = carRepository.save(CarEntity.builder().carNumber("sampleCarNumber2").build());

    // 12개 즐겨찾기 추가한 유저 생성
    memberWith12Favorite = memberRepository.save(
        MemberEntity.builder()
            .carEntity(sampleCar1)
            .userName("12bookMarkUser")
            .authId("authId1")
            .password("password2")
            .phoneNumber("01087654321")
            .email("user1@example.com")
            .birthdayYear(1985)
            .birthday("0202")
            .loginPlatform(LoginPlatform.GOOGLE)
            .role(MemberRole.ROLE_USER)
            .build()
    );
    // 멤버 엔티티 저장
    memberWith3Favorite = memberRepository.save(
        MemberEntity.builder()
            .carEntity(sampleCar2)
            .userName("3bookMarkUser")
            .authId("authId2")
            .password("password1")
            .phoneNumber("01012345678")
            .email("user2@example.com")
            .birthdayYear(1990)
            .birthday("0101")
            .loginPlatform(LoginPlatform.GOOGLE)
            .role(MemberRole.ROLE_USER)
            .build()
    );

    memberWith0Favorite = memberRepository.save(
        MemberEntity.builder()
            .carEntity(sampleCar3)
            .userName("0bookMarkUser")
            .authId("authId3")
            .password("password2")
            .phoneNumber("01087654321")
            .email("user3@example.com")
            .birthdayYear(1995)
            .birthday("0202")
            .loginPlatform(LoginPlatform.GOOGLE)
            .role(MemberRole.ROLE_USER)
            .build()
    );

    // FavoriteParkingZone 엔티티 저장
    favoriteParkingZone1 = favoriteParkingZoneRepository.save(
        FavoriteParkingZoneEntity.builder()
            .memberEntity(memberWith3Favorite)
            .parkingZoneEntity(seoulParking)
            .build()
    );
    favoriteParkingZone2 = favoriteParkingZoneRepository.save(
        FavoriteParkingZoneEntity.builder()
            .memberEntity(memberWith3Favorite)
            .parkingZoneEntity(busanParking)
            .build()
    );
    favoriteParkingZone3 = favoriteParkingZoneRepository.save(
        FavoriteParkingZoneEntity.builder()
            .memberEntity(memberWith3Favorite)
            .parkingZoneEntity(daejeonParking)
            .build()
    );

    // 즐겨찾기 12개 추가
    for (int i = 0; i < 12; i++) {
      ParkingZoneEntity newParking = parkingZoneRepository.save(
          ParkingZoneEntity.builder()
              .cityEntity(seoul)
              .districtEntity(sampleDistrict)
              .eupMyeonDongEntity(sampleEupMyeonDong)
              .build()
      );

      favoriteParkingZoneRepository.save(FavoriteParkingZoneEntity.builder()
          .memberEntity(memberWith12Favorite)
          .parkingZoneEntity(newParking)
          .build()
      );
    }
  }

  @Test
  @DisplayName("Member Id를 통한 즐겨찾는 주차장 불러오기 레포지토리 메서드 테스트")
  void testFindAllFavoriteParkingZonesByMemberId() {
    // when:
    // memberWith3Favorite의 ID를 통해 즐겨찾는 주차장 조회
    // memberWith0Favorite의 ID를 통해 즐겨찾는 주차장 조회
    Long memberId1 = memberWith3Favorite.getId();
    Long memberId2 = memberWith0Favorite.getId();

    Pageable pageable = PageRequest.of(0, pagePerItem); // 첫 번째 페이지, 2개씩 가져오기

    Page<FavoriteParkingZoneEntity> result1 = favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(
        memberId1, pageable);
    Page<FavoriteParkingZoneEntity> result2 = favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(
        memberId2, pageable);

    // then
    // 3개가 모두 정확하게 나와야함
    assertThat(result1).hasSize(3);
    assertThat(result1)
        .extracting("parkingZoneEntity")
        .containsExactlyInAnyOrder(seoulParking, busanParking, daejeonParking);

    // then
    // 0개가 모두 나와야함
    assertThat(result2).hasSize(0);
  }

  @Test
  @DisplayName("즐겨찾기 주차장이 ID 순서대로 정렬되는지  1페이지 2페이지에 가각 알맞은 데티거 개수와 정렬 확인")
  void testFindAllByMemberEntity_IdOrderByIdAsc() {
    // given: 테스트용 데이터 준비
    Long memberId = memberWith12Favorite.getId();

    // 10개 요청 (0페이지)
    Pageable tenItemsPageable = PageRequest.of(0, pagePerItem);
    Page<FavoriteParkingZoneEntity> tenItemsPage = favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(
        memberId, tenItemsPageable);

    // 2개 요청 (1페이지)
    Pageable twoItemsPageable = PageRequest.of(1, pagePerItem);
    Page<FavoriteParkingZoneEntity> twoItemsPage = favoriteParkingZoneRepository.findAllByMemberEntity_IdOrderByIdAsc(
        memberId, twoItemsPageable);

    // when: 10개 요청 결과 개수 확인
    assertThat(tenItemsPage.getContent()).hasSize(10);

    // when: 2개 요청 결과 개수 확인
    assertThat(twoItemsPage.getContent()).hasSize(2);

    // when: 10개 요청 ID 오름차순 확인
    List<FavoriteParkingZoneEntity> tenItemsContent = tenItemsPage.getContent();
    for (int i = 0; i < tenItemsContent.size() - 1; i++) {
      assertThat(tenItemsContent.get(i).getId()).isLessThan(tenItemsContent.get(i + 1).getId());
    }

    // when: 2개 요청 ID 오름차순 확인
    List<FavoriteParkingZoneEntity> twoItemsContent = twoItemsPage.getContent();
    for (int i = 0; i < twoItemsContent.size() - 1; i++) {
      assertThat(twoItemsContent.get(i).getId()).isLessThan(twoItemsContent.get(i + 1).getId());
    }

    // then: 10개 요청과 2개 요청이 서로 겹치지 않는지 확인
    if (!tenItemsContent.isEmpty() && !twoItemsContent.isEmpty()) {
      assertThat(tenItemsContent.get(tenItemsContent.size() - 1).getId())
          .isLessThan(twoItemsContent.get(0).getId());
    }
  }

  @Test
  void deleteByMemberEntity_IdAndParkingZoneEntity_Id_ShouldDeleteSuccessfully() {
    // Given
    Long memberId = memberWith3Favorite.getId();
    Long parkingZoneId = seoulParking.getId();

    // When
    int deletedCount = favoriteParkingZoneRepository.deleteByMemberEntity_IdAndParkingZoneEntity_Id(
        memberId, parkingZoneId);

    // Then
    assertEquals(1, deletedCount, "하나의 항목이 삭제되어야 한다.");
  }

  @Test
  void deleteByMemberEntity_IdAndParkingZoneEntity_Id_ShouldNotDeleteWhenNotExist() {
    // Given
    Long nonExistentMemberId = 999L;
    Long nonExistentParkingZoneId = 999L;

    // When
    int deletedCount = favoriteParkingZoneRepository.deleteByMemberEntity_IdAndParkingZoneEntity_Id(
        nonExistentMemberId, nonExistentParkingZoneId);

    // Then
    assertEquals(0, deletedCount, "존재하지 않는 항목에 대해 삭제는 0이어야 한다.");
  }
}
