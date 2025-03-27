package org.example.honorsparkingbe.service;


import jakarta.transaction.Transactional;
import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.domain.enums.LoginPlatform;
import org.example.honorsparkingbe.domain.enums.MemberRole;
import org.example.honorsparkingbe.dto.JoinDTO;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

  private final MemberRepository memberRepository;
  private final CarRepository carRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public JoinService(MemberRepository memberRepository, CarRepository carRepository,
      BCryptPasswordEncoder bCryptPasswordEncoder) {
    this.memberRepository = memberRepository;
    this.carRepository = carRepository;
    this.bCryptPasswordEncoder = bCryptPasswordEncoder;
  }

  /**
   * 회원가입 기본 로직
   */
  @Transactional
  public void joinProcess(JoinDTO joinDTO) {

    // accountId 중복 확인
    boolean isUser = memberRepository.existsByAuthId(joinDTO.getAccountId());
    if (isUser) {
      throw new IllegalArgumentException(
          "accountId= " + joinDTO.getAccountId() + " already exists.");
    }

    // CarEntity 생성
    CarEntity carEntity = new CarEntity();
    carEntity.setCarNumber(joinDTO.getCarNumber());
    carEntity.setCarType(CarType.NOT_DEFINED); // 기본값 설정 (차량 종류)
    carEntity.setIsElectric(false);        // 기본값 설정 (전기차 여부)
    carEntity.setEntranceTime(null);     // 입차 시간 초기화

    carRepository.save(carEntity);

    // MemberEntity 생성
    MemberEntity data = new MemberEntity();
    data.setAuthId(joinDTO.getAccountId());
    data.setPassword(bCryptPasswordEncoder.encode(joinDTO.getAccountPassword()));
    data.setUserName(joinDTO.getName());
    data.setPhoneNumber(joinDTO.getMobile());
    data.setEmail(joinDTO.getEmail());
    data.setBirthdayYear(Integer.parseInt(joinDTO.getBirthyear()));
    data.setBirthday(joinDTO.getBirthday());
    data.setLoginPlatform(LoginPlatform.valueOf(joinDTO.getPlatform().toUpperCase()));
    data.setRole(MemberRole.ROLE_USER);
    data.setCarEntity(carEntity);

    // 저장
    memberRepository.save(data);
  }

  /**
   * ID 중복확인
   */
  public boolean isAuthIdAvailable(String authId) {
    return memberRepository.existsByAuthId(authId);
  }

}

