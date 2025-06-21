package com.example.lotto_project.scheduler;

import com.example.lotto_project.service.LottoCrawlingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component //스프링 빈으로 등록
@RequiredArgsConstructor
public class LottoDataSchedular {

  private final LottoCrawlingService lottoCrawlingService;

  //cron = "초 분 시 일 월 요일" 형식
  // "0 0 21 * * SAT" = 매주 토요일 오후 9시(21시) 0분 0초에 실행

  //  @Scheduled(cron = "0 * * * * *")
  @Scheduled(cron = "0 0 21 * * SAT")
  public void scheduleLottoDataUpdate() {
    log.info("========== 로또 데이터 자동 업데이트 스케줄러 실행 ==========");
    String result = lottoCrawlingService.updateAllLottoRounds();
    log.info("========== 로또 데이터 자동 업데이트 스케줄러 완료. 결과 {} ==========", result);
  }
}
