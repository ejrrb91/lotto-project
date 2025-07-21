package com.example.lotto_project.repository;

import com.example.lotto_project.domain.Recommendation;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {

//  List<Recommendation> findByUserIdOrderByRecommendedAtDesc(Long userId);

  //lottoRound를 기준으로 모든 추천 기록을 찾는 메서드
  List<Recommendation> findAllByLottoRound(int lottoRound);

  //특정 사용자 대한 추천 기록을 ID 역순으로 조회
  List<Recommendation> findByUserIdOrderByIdDesc(Long userId);
}
