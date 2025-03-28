package org.example.honorsparkingbe.repository.internal;

import java.util.List;
import org.example.honorsparkingbe.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberRepository extends JpaRepository<MemberEntity, Long> {

  // 일반 로그인 사용자의 authId로 존재 여부 확인
  boolean existsByAuthId(String authId);

  // 일반 로그인 사용자의 authId로 회원 조회
  MemberEntity findByAuthId(String authId);

  List<MemberEntity> findAllByCarEntity_CarNumberIn(List<String> carNumbers);
}