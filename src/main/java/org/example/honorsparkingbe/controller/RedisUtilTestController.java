package org.example.honorsparkingbe.controller;

import java.util.concurrent.TimeUnit;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.honorsparkingbe.util.RedisUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/test/redisutil")
/**
 * 테스트용 컨트롤러 추후 삭제 예정
 */
public class RedisUtilTestController {

  private final RedisUtil redisUtil;


  // key-value를 JSON으로 받음
  @PostMapping
  public ResponseEntity<Void> writeRedis(@RequestBody RedisRequest request) {
    redisUtil.set(request.getKey(), request.getValue(), 5, TimeUnit.MINUTES);
    return ResponseEntity.ok().build();
  }

  // key를 쿼리 파라미터로 받음
  @GetMapping
  public ResponseEntity<Object> readRedis(@RequestParam String key) {
    return ResponseEntity.ok(redisUtil.get(key));
  }

  // JSON 요청 바디를 받을 DTO
  @Data
  static class RedisRequest {

    private String key;
    private String value;
  }

}
