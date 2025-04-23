package org.example.honorsparkingbe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class HonorsParkingBeApplication {

  public static void main(String[] args) {
    SpringApplication.run(HonorsParkingBeApplication.class, args);
  }

}

// CI/CD 성공 여부 확인용 주석 (변경 0회)