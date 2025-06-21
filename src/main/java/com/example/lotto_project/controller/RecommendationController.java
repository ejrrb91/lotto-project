package com.example.lotto_project.controller;

import com.example.lotto_project.domain.User;
import com.example.lotto_project.security.UserDetailsImpl;
import com.example.lotto_project.service.RecommendationService;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendationController {

  private final RecommendationService recommendationService;

  /**
   * 무작위로 추천하는 API
   *
   * @param userDetails
   * @return
   */
  @PostMapping("/random")
  public ResponseEntity<List<Integer>> recommendRandom(@AuthenticationPrincipal
  UserDetailsImpl userDetails) {

    // 1. @AuthenticationPrincipal 어노테이션으로 현재 로그인한 사용자의 UserDetailsImpl 객체를 직접 받아옴.
    User user = userDetails.getUser();

    //2. 해당 사용자의 ID를 받아 서비스에 전달
    List<Integer> numbers = recommendationService.recommendRandomNumbers(user.getId());

    return ResponseEntity.ok(numbers);
  }

  /**
   * 과거 통계를 기반으로 가장 많이 나온 번호에 가중치를 부여하여 추천하는 API
   *
   * @param userDetails
   * @return
   */
  @PostMapping("/statistical")
  public ResponseEntity<List<Integer>> recommendStatistical(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User user = userDetails.getUser();
    List<Integer> numbers = recommendationService.recommendStatisticalNumbers(user.getId());
    return ResponseEntity.ok(numbers);
  }

  /**
   * 최근 5주간 미출현 번호들 중에서 추천하는 API
   *
   * @param userDetails
   * @return
   */
  @PostMapping("/infrequent")
  public ResponseEntity<List<Integer>> recommendInfrequent(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User user = userDetails.getUser();
    List<Integer> numbers = recommendationService.recommendInfrequentNumbers(user.getId());
    return ResponseEntity.ok(numbers);
  }

  /**
   * 역대 가장 적게 나온 번호에 가중치를 부여하여 추천하는 API
   *
   * @param userDetails
   * @return
   */
  @PostMapping("/rare")
  public ResponseEntity<List<Integer>> recommendRare(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User user = userDetails.getUser();
    List<Integer> numbers = recommendationService.recommendRareNumbers(user.getId());
    return ResponseEntity.ok(numbers);
  }

  /**
   * 최근 6개월간 3번 이상 나온 번호 중에서 추천하는 API
   *
   * @param userDetails
   * @return
   */
  @PostMapping("/recent6month")
  public ResponseEntity<List<Integer>> recommendRecentSixMonth(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    User user = userDetails.getUser();
    List<Integer> numbers = recommendationService.recommendRecentSixMonthsNumbers(user.getId());
    return ResponseEntity.ok(numbers);
  }

  /**
   * 홀수 3개, 짝수 3개를 맞춰서 추천하는 API
   *
   * @param userDetails
   * @return
   */
  @PostMapping("/oddeven")
  public ResponseEntity<List<Integer>> recommendOddEven(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {
    User user = userDetails.getUser();

    List<Integer> numbers = recommendationService.recommendOddEvenRatioNumbers(user.getId());

    return ResponseEntity.ok(numbers);
  }
}
