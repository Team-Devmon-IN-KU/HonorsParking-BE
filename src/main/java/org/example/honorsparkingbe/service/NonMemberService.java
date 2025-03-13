package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.repository.CarRepository;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NonMemberService {

    private final CarRepository carRepository;
    private final ParkingHistoryRepository parkingHistoryRepository;

    public NonMemberService(CarRepository carRepository, ParkingHistoryRepository parkingHistoryRepository) {
        this.carRepository = carRepository;
        this.parkingHistoryRepository = parkingHistoryRepository;
    }

    public ResponseEntity<?> getParkingTime(String carNumber) {
        Optional<CarEntity> carOptional= carRepository.findByCarNumber(carNumber);

        if(carOptional.isEmpty()){
            return ResponseEntity.status(404).body("해당 차량 번호가 존재하지 않습니다.");
        }

        CarEntity carEntity = carOptional.get();

        Optional<ParkingHistoryEntity> parkingEntranceHistory= parkingHistoryRepository.findTopByCarEntityOrderByEntranceTimeDesc(carEntity);

        if(parkingEntranceHistory.isEmpty()){
            return ResponseEntity.status(404).body("해당 차량의 주차 내역이 존재하지 않습니다.");
        }else{
            if(parkingEntranceHistory.get().getExitTime()==null){
                return ResponseEntity.ok(parkingEntranceHistory.get().getEntranceTime());
            }else{
                return ResponseEntity.status(404).body("현재 주차 중인 차량이 아닙니다.");
            }
        }
    }
}
