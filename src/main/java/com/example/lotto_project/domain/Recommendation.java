package com.example.lotto_project.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class Recommendation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id; //추천 기록의 고유 ID

  @ManyToOne //Recommendation(N) : User(1) 관계
  @JoinColumn(name = "user_id") //외래키 컬럼 이름 지정
  private User user; //어떤 사용자가 추천받았는지

  @Column(nullable = false)
  private int lottoRound; //몇 회차를 대상으로 추천했는지

  @Enumerated(EnumType.STRING) //Enum 타입을 문자열로 저장
  @Column(nullable = false)
  private AlgorithmType algorithmType;

  //추천된 번호 6개
  private int num1;
  private int num2;
  private int num3;
  private int num4;
  private int num5;
  private int num6;

  @Column(nullable = false)
  private LocalDateTime recommendedAt; //추천받은 시간

  @PrePersist
  public void prePersist() {
    this.recommendedAt = LocalDateTime.now();
  }
}
