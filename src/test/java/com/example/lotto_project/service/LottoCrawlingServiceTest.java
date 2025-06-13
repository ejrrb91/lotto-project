package com.example.lotto_project.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest //실제 스프링 환경과 동일하게 테스트
public class LottoCrawlingServiceTest {

  @Autowired
  private LottoCrawlingService lottoCrawlingService;

  @Test
  @DisplayName("특정 회차 당첨번호 크롤링 및 저장 테스트")
  void saveWinningNumbersByRoundTest() {
    //given : 테스트할 회차 번호.
    int testRound = 1123;

    //when : 서비스 호출
    lottoCrawlingService.saveWinningNumbersByRound(testRound);

    //then : 결과 확인
    // 1. IntelliJ 콘솔에 "회차 1123 당첨 번호 저장 성공" 메시지가 출력되는지 확인
    // 2. DBeaver의 'lotto-docker' 연결에서 lotto_round 테이블에 1123회차 데이터가 들어갔는지 확인

  }
}
