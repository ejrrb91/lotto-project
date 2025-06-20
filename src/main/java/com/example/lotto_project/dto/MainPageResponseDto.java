package com.example.lotto_project.dto;

import com.example.lotto_project.domain.LottoRound;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MainPageResponseDto {

  private Integer round;
  private LocalDate drawDate;
  private List<Integer> winningNumbers;
  private Integer bonusNumber;

  private long firstPrizeCount;
  private long secondPrizeCount;
  private long thirdPrizeCount;
  private long fourthPrizeCount;
  private long fifthPrizeCount;

  public MainPageResponseDto(LottoRound lottoRound, long[] prizeCounts) {
    if (lottoRound != null) {
      this.round = lottoRound.getRound();
      this.drawDate = lottoRound.getDrawDate();
      this.winningNumbers = List.of(
          lottoRound.getWinNum1(), lottoRound.getWinNum2(), lottoRound.getWinNum3(),
          lottoRound.getWinNum4(), lottoRound.getWinNum5(), lottoRound.getWinNum6()
      );
      this.bonusNumber = lottoRound.getBonusNum();
    }
    if (prizeCounts != null && prizeCounts.length == 5) {
      this.firstPrizeCount = prizeCounts[0];
      this.secondPrizeCount = prizeCounts[1];
      this.thirdPrizeCount = prizeCounts[2];
      this.fourthPrizeCount = prizeCounts[3];
      this.fifthPrizeCount = prizeCounts[4];
    }
  }

}
