package com.example.lotto_project.repository;

import com.example.lotto_project.domain.LottoRound;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoRoundRepository extends JpaRepository<LottoRound, Integer> {
  // JpaRepository를 상속받으면, Spring Data JPA가 기본적인
  // 데이터베이스 CRUD(Create, Read, Update, Delete) 작업을 자동으로 만들어 줌.

  //DB에 저장된 가장 최신 회차 조회
  Optional<LottoRound> findTopByOrderByRoundDesc();
}
