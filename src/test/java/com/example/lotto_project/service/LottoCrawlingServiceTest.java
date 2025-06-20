package com.example.lotto_project.service;

import com.example.lotto_project.repository.LottoRoundRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest //실제 스프링 환경과 동일하게 테스트
public class LottoCrawlingServiceTest {

  @Autowired
  private LottoCrawlingService lottoCrawlingService;

  //DB 조회를 위해 Repository 주입
  @Autowired
  private LottoRoundRepository lottoRoundRepository;

  @Test
  @DisplayName("특정 회차 당첨번호 크롤링 및 저장 테스트")
  void saveWinningNumbersByRoundTest() {
    //given : 테스트할 회차 번호.
    int testRound = 1176;

    //when : 서비스 호출
    lottoCrawlingService.saveWinningNumbersByRound(testRound);

    //then : DB에 해당 회차 데이터가 실제로 저장되었는지 코드로 검증
    boolean isSaved = lottoRoundRepository.existsById(testRound);

    // Assertions.assertTrue(조건, "실패 시 메시지"): 조건이 true가 아니면 테스트를 실패시킴.
    Assertions.assertTrue(isSaved, "1176회차 데이터가 DB에 저장되어야 합니다.");
    System.out.println("테스트 통과 : 1176회차 데이터 저장 확인");
    
    // 1. IntelliJ 콘솔에 "회차 1123 당첨 번호 저장 성공" 메시지가 출력되는지 확인
    // 2. DBeaver의 'lotto-docker' 연결에서 lotto_round 테이블에 1123회차 데이터가 들어갔는지 확인

  }
}
