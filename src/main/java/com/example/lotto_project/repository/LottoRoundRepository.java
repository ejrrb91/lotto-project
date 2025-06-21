package com.example.lotto_project.repository;

import com.example.lotto_project.domain.LottoRound;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoRoundRepository extends JpaRepository<LottoRound, Integer> {
  // JpaRepository를 상속받으면, Spring Data JPA가 기본적인
  // 데이터베이스 CRUD(Create, Read, Update, Delete) 작업을 자동으로 만들어 줌.

  //DB에 저장된 가장 최신 회차 조회
  Optional<LottoRound> findTopByOrderByRoundDesc();

  //특정 날짜 이후의 모든 회차 정보를 조회
  List<LottoRound> findByDrawDateAfter(LocalDate date);

  //가장 최신 회차 5개의 데이터를 조회
  List<LottoRound> findTop5ByOrderByRoundDesc();


}
