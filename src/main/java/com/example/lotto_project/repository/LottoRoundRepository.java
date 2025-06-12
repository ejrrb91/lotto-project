package com.example.lotto_project.repository;

import com.example.lotto_project.domain.LottoRound;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LottoRoundRepository extends JpaRepository<LottoRound, Integer> {
  // JpaRepository를 상속받으면, Spring Data JPA가 기본적인
  // 데이터베이스 CRUD(Create, Read, Update, Delete) 작업을 자동으로 만들어 줌.
}
