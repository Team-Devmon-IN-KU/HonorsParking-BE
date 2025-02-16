package org.example.honorsparkingbe.domain.enums;

public enum CarType {
  NOT_DEFINED,    // 처음 회원가입 시 null 값이 불가하기에
  COMPACT,        // 소형
  MIDSIZE,        // 중형
  FULLSIZE,       // 대형
  LIGHT_CAR,      // 경차
  ELECTRIC,       // 전기차
  ECO_FRIENDLY,    // 친환경차
  NORMAL          //기본(요금 구분X)
}
