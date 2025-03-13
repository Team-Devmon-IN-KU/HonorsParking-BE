package org.example.honorsparkingbe.service;

import org.example.honorsparkingbe.domain.entity.CarEntity;
import org.example.honorsparkingbe.domain.entity.ParkingHistoryEntity;
import org.example.honorsparkingbe.repository.CarRepository;
import org.example.honorsparkingbe.repository.ParkingHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NonMemberServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ParkingHistoryRepository parkingHistoryRepository;

    @InjectMocks
    private NonMemberService nonMemberService;

    private final String validCarNumber = "123ABC";
    private final String invalidCarNumber = "000XYZ";
    private final String parkedCarNumber = "456DEF";

    private CarEntity carEntity;
    private ParkingHistoryEntity parkingHistory;

    @BeforeEach
    void setUp() {
        carEntity = new CarEntity();
        carEntity.setCarNumber(validCarNumber);

        parkingHistory = new ParkingHistoryEntity();
        parkingHistory.setCarEntity(carEntity);
        parkingHistory.setEntranceTime(LocalDateTime.of(2025, 3, 10, 12, 0, 0));
        parkingHistory.setExitTime(null);
    }

    @Test
    void testGetParkingTime_Success() {
        when(carRepository.findByCarNumber(validCarNumber)).thenReturn(Optional.of(carEntity));
        when(parkingHistoryRepository.findTopByCarEntityOrderByEntranceTimeDesc(carEntity))
                .thenReturn(Optional.of(parkingHistory));

        ResponseEntity<?> response = nonMemberService.getParkingTime(validCarNumber);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(parkingHistory.getEntranceTime(), response.getBody());
    }

    @Test
    void testGetParkingTime_CarNotFound() {
        when(carRepository.findByCarNumber(invalidCarNumber)).thenReturn(Optional.empty());

        ResponseEntity<?> response = nonMemberService.getParkingTime(invalidCarNumber);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("해당 차량 번호가 존재하지 않습니다.", response.getBody());
    }

    @Test
    void testGetParkingTime_NoParkingHistory() {
        when(carRepository.findByCarNumber(validCarNumber)).thenReturn(Optional.of(carEntity));
        when(parkingHistoryRepository.findTopByCarEntityOrderByEntranceTimeDesc(carEntity))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = nonMemberService.getParkingTime(validCarNumber);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("해당 차량의 주차 내역이 존재하지 않습니다.", response.getBody());
    }

    @Test
    void testGetParkingTime_AlreadyExited() {
        parkingHistory.setExitTime(LocalDateTime.of(2025, 3, 10, 15, 0, 0));
        when(carRepository.findByCarNumber(parkedCarNumber)).thenReturn(Optional.of(carEntity));
        when(parkingHistoryRepository.findTopByCarEntityOrderByEntranceTimeDesc(carEntity))
                .thenReturn(Optional.of(parkingHistory));

        ResponseEntity<?> response = nonMemberService.getParkingTime(parkedCarNumber);

        assertEquals(404, response.getStatusCodeValue());
        assertEquals("현재 주차 중인 차량이 아닙니다.", response.getBody());
    }
}
