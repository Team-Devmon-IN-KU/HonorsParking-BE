package org.example.honorsparkingbe.performanceTest.createData;

import org.example.honorsparkingbe.domain.enums.CarType;
import org.example.honorsparkingbe.repository.internal.AlarmRepository;
import org.example.honorsparkingbe.repository.internal.CarRepository;
import org.example.honorsparkingbe.repository.internal.CityRepository;
import org.example.honorsparkingbe.repository.internal.DistrictRepository;
import org.example.honorsparkingbe.repository.internal.EupMyeonDongRepository;
import org.example.honorsparkingbe.repository.internal.FavoriteParkingZoneRepository;
import org.example.honorsparkingbe.repository.internal.MemberRepository;
import org.example.honorsparkingbe.repository.internal.ParkingFeeRuleRepository;
import org.example.honorsparkingbe.repository.internal.ParkingHistoryRepository;
import org.example.honorsparkingbe.repository.internal.ParkingZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Profile("performanceTest")
public class PerformanceCheckInit {

  @Autowired
  protected MemberRepository memberRepository;
  @Autowired
  protected BCryptPasswordEncoder passwordEncoder;

  protected static final String[] testUserIDs = {"performTUser1", "performTUser2", "performTUser3"};
  protected static final Integer sampleParkingZoneNum = 30;
  protected static final CarType defaultCarType = CarType.COMPACT;

  @Autowired
  protected CarRepository carRepository;
  @Autowired
  protected CityRepository cityRepository;
  @Autowired
  protected DistrictRepository districtRepository;
  @Autowired
  protected EupMyeonDongRepository eupMyeonDongRepository;
  @Autowired
  protected ParkingZoneRepository parkingZoneRepository;
  @Autowired
  protected ParkingFeeRuleRepository parkingFeeRuleRepository;
  @Autowired
  AlarmRepository alarmRepository;
  @Autowired
  FavoriteParkingZoneRepository favoriteParkingZoneRepository;
  @Autowired
  ParkingHistoryRepository parkingHistoryRepository;
}

