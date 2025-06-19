package com.example.lotto_project.controller;

import com.example.lotto_project.domain.User;
import com.example.lotto_project.security.UserDetailsImpl;
import com.example.lotto_project.service.RecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
  public ResponseEntity<List<Integer>> getRandomRecommendation(@AuthenticationPrincipal
  UserDetailsImpl userDetails) {

    // 1. @AuthenticationPrincipal 어노테이션으로 현재 로그인한 사용자의 UserDetailsImpl 객체를 직접 받아옴.
    User user = userDetails.getUser();

    //해당 사용자의 ID를 받아 서비스에 전달
    List<Integer> numbers = recommendationService.recommendRandomNumbers(user.getId());

    return ResponseEntity.ok(numbers);
  }

  //통계 기반 추천 API
  @PostMapping("/statistical")
  public ResponseEntity<List<Integer>> getStatisticalRecommendation(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User user = userDetails.getUser();
    List<Integer> numbers = recommendationService.recommendStatisticalNumbers(user.getId());

    return ResponseEntity.ok(numbers);
  }
}
