package com.example.lotto_project.dto;

import com.example.lotto_project.domain.LottoRound;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MyPageResponseDto {

  //최신 회차 번호
  private Integer round;

  //최신 회차의 추첨일
  private LocalDate drawDate;

  //최신 당첨 번호 6개 리스트
  private List<Integer> latestWinningNumbers;

  private Integer bonusNumber;

  //나의 추천 기록을 목록을 담을 필드
  private List<RecommendationResponseDto> myRecommendations;

  //생성자 : 서비스 레이어에서 두 종류의 데이터를 모두 준비한 뒤, 이 생성자를 호출하여 한번에 담음.
  public MyPageResponseDto(LottoRound latestRound,
      List<RecommendationResponseDto> myRecommendations) {
    if (latestRound != null) {
      this.round = latestRound.getRound();
      this.drawDate = latestRound.getDrawDate();
      this.latestWinningNumbers = List.of(latestRound.getWinNum1(), latestRound.getWinNum2(),
          latestRound.getWinNum3(), latestRound.getWinNum4(), latestRound.getWinNum5(),
          latestRound.getWinNum6());
      this.bonusNumber = latestRound.getBonusNum();
    }
    this.myRecommendations = myRecommendations;
  }
}
