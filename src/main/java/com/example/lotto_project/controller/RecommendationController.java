package com.example.lotto_project.controller;

import com.example.lotto_project.service.RecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService recommendationService;

  //무작위 추천 API
  @PostMapping("/random")
  public ResponseEntity<List<Integer>> getRandomRecommendation() {

    //TODO: 로그인 기능 구현 후, 실제 로그인한 사용자 ID를 넘겨줘야 함
    Long tempUserId = 1L;

    List<Integer> numbers = recommendationService.recommendRandomNumbers(tempUserId);

    return ResponseEntity.ok(numbers);
  }

  //통계 기반 추천 API
  @PostMapping("/statistical")
  public ResponseEntity<List<Integer>> getStatisticalRecommendation() {
    //TODO: 로그인 기능 구현 후, 실제 로그인한 사용자 ID를 넘겨줘야 함
    Long tempUserId = 1L;

    List<Integer> numbers = recommendationService.recommendStatisticalNumbers(tempUserId);

    return ResponseEntity.ok(numbers);
  }
}
