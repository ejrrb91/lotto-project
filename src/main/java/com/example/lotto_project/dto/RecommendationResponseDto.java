package com.example.lotto_project.dto;

import com.example.lotto_project.domain.AlgorithmType;
import com.example.lotto_project.domain.Recommendation;
import java.time.LocalDateTime;
import java.util.Timer;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RecommendationResponseDto {

  private Long id;
  private LocalDateTime recommendedAt;
  private AlgorithmType algorithmType;
  private Integer num1;
  private Integer num2;
  private Integer num3;
  private Integer num4;
  private Integer num5;
  private Integer num6;
  private Integer matchCount;

  //Recommendation 엔티티를 이 DTO 객체로 변환 해주는 생성자
  public RecommendationResponseDto(Recommendation recommendation) {

    this.id = recommendation.getId();
    this.recommendedAt = recommendation.getRecommendedAt();
    this.algorithmType = recommendation.getAlgorithmType();
    this.num1 = recommendation.getNum1();
    this.num2 = recommendation.getNum2();
    this.num3 = recommendation.getNum3();
    this.num4 = recommendation.getNum4();
    this.num5 = recommendation.getNum5();
    this.num6 = recommendation.getNum6();
    this.matchCount = recommendation.getMatchCount();
  }
}
