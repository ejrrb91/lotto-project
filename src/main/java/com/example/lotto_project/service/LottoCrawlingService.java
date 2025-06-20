package com.example.lotto_project.service;

import com.example.lotto_project.domain.LottoRound;
import com.example.lotto_project.domain.Recommendation;
import com.example.lotto_project.dto.MainPageResponseDto;
import com.example.lotto_project.repository.LottoRoundRepository;
import com.example.lotto_project.repository.RecommendationRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LottoCrawlingService {

  private final LottoRoundRepository lottoRoundRepository;
  private final RecommendationRepository recommendationRepository;


  @Transactional
  public void saveWinningNumbersByRound(int round) {
    //1.DB에 해당 회차 데이터가 이미 있는지 확인.
    if (lottoRoundRepository.existsById(round)) {
      System.out.println(round + "회차, 데이터는 이미 DB에 존재합니다.");
      return;
    }

    try {
      //2.동행복권 사이트에서 해당회차의 결과 페이지 HTML을 가져옴.
      String URL = "https://dhlottery.co.kr/gameResult.do?method=byWin&drwNo=" + round;
      Document doc = Jsoup.connect(URL).get();

      //3.HTML에서 필요한 정보들을 CSS 선택자로 추출.
      Elements winNumbers = doc.select("div.win_result div.num span.ball_645");
      String dateStr = doc.select("p.desc").text().replaceAll("[^0-9년월일]", "");

      //4.파싱한 정보가 유효한지 확인(7개 번호 모두 파싱)
      if (winNumbers.size() < 7) {
        System.out.println(round + "회차의 당첨 결과가 아직 발표되지 않았습니다.");
        return;
      }

      //5.파싱한 데이터를 LottoRound 엔티티 객체에 담음.
      LottoRound lottoRound = new LottoRound();
      lottoRound.setRound(round);
      lottoRound.setDrawDate(LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy년MM월dd일")));
      lottoRound.setWinNum1(Integer.parseInt(winNumbers.get(0).text()));
      lottoRound.setWinNum2(Integer.parseInt(winNumbers.get(1).text()));
      lottoRound.setWinNum3(Integer.parseInt(winNumbers.get(2).text()));
      lottoRound.setWinNum4(Integer.parseInt(winNumbers.get(3).text()));
      lottoRound.setWinNum5(Integer.parseInt(winNumbers.get(4).text()));
      lottoRound.setWinNum6(Integer.parseInt(winNumbers.get(5).text()));
      lottoRound.setBonusNum(Integer.parseInt(winNumbers.get(6).text()));

      //6.완성된 엔티티를 데이터베이스에 저장.
      lottoRoundRepository.save(lottoRound);
      System.out.println(round + "회차, 당첨 번호 저장 성공");

      //7. 저장된 최신 회차의 당첨번호 6개를 Set으로 만듦.(빠른 조회를 위해)
      Set<Integer> winningNumbers = new HashSet<>(List.of(lottoRound.getWinNum1(),
          lottoRound.getWinNum2(), lottoRound.getWinNum3(), lottoRound.getWinNum4(),
          lottoRound.getWinNum5(), lottoRound.getWinNum6()));

      int bonusNum = lottoRound.getBonusNum();

      //8. 방금 추첨이 끝난 회차에 대해 사용자들이 추천받았던 모든 기록을 가져옴.
      List<Recommendation> recommendations = recommendationRepository.findAllByLottoRound(round);

      //9. 각 추천 기록을 순회하면서 당첨 개수를 계산하고 업데이트 합니다.
      for (Recommendation recommendation : recommendations) {
        List<Integer> userNumbers = List.of(recommendation.getNum1(), recommendation.getNum2(),
            recommendation.getNum3(), recommendation.getNum4(), recommendation.getNum5(),
            recommendation.getNum6());

        int matchCount = 0;
        //보너스 번호가 포함되어 있는지 확인
        boolean hasBonus = userNumbers.contains(bonusNum);

        for (Integer userNumber : userNumbers) {
          if (winningNumbers.contains(userNumber)) {
            matchCount++;
          }
        }
        //계산된 일치하는 갯수를 엔티티에 설정
        recommendation.setMatchCount(matchCount);
        //보너스 번호 일치 여부도 엔티티에 설정
        recommendation.setIsBonusMatched(hasBonus);
      }
      //@Transactional 때문에, for문이 끝난 뒤 변경된 내용이 자동으로 DB에 저장
    } catch (IOException e) {
      System.err.println(round + "회차, 데이터를 가져오는 중 오류가 발생했습니다. : " + e.getMessage());
    }
  }

  //최신 회차 번호를 조회 하는 메서드
  private int getLatestRoundOnSite() throws IOException {
    String URL = "https://dhlottery.co.kr/gameResult.do?method=byWin";
    Document doc = Jsoup.connect(URL).get();

    //최신 회차 번호는 select box의 첫번째 option에 들어있음.
    String latestRoundStr = doc.select("select#dwrNoList option").first().text();
    return Integer.parseInt(latestRoundStr);
  }

  @Transactional
  public String updateAllLottoRounds() {
    try {
      //1.동행복권 사이트에서 최신 회차 조회
      int latestRoundOnSite = getLatestRoundOnSite();

      //2.DB에 저장된 마지막 회차 조회
      int lastSavedRound = lottoRoundRepository.findTopByOrderByRoundDesc()
          .map(LottoRound::getRound) //LottoRound 객체에서 회차(Integer)만 추출
          .orElse(0); //DB가 비어있으면 0을 반환
      //3. 마지막으로 저장한 회차 다음부터, 사이트의 최신 회차까지 반복
      if (lastSavedRound >= latestRoundOnSite) {
        return "이미 모든 회차의 데이터가 최신입니다.";
      }

      for (int i = lastSavedRound + 1; i <= latestRoundOnSite; i++) {
        saveWinningNumbersByRound(i); // 기존에 만들었던 회차별 저장 메소드 호출
      }
      return (latestRoundOnSite - lastSavedRound) + "개의 새로운 회차 데이터가 저장되었습니다.";

    } catch (IOException e) {
      System.err.println("최신 회차 업데이트 중 오류 발생. : " + e.getMessage());
      return "업데이트 중 오류가 발생했습니다.";
    }
  }

  @Transactional(readOnly = true)
  public MainPageResponseDto getLatestLottoRound() {
    //1. 최신 당첨 회차 정보를 가져옴
    LottoRound latestRound = lottoRoundRepository.findTopByOrderByRoundDesc().orElse(null);

    //2. DB에 당첨 정보가 없을 경우, 빈 값을 반환
    if (latestRound == null) {
      return new MainPageResponseDto(null, new long[5]);
    }

    //3. 해당 회차에 대한 모든 추천 기록을 가져옴.
    List<Recommendation> recommendations = recommendationRepository.findAllByLottoRound(
        latestRound.getRound());

    //4. 각 등수별 당첨 횟수를 저장할 배열 초기화
    long[] prizeCounts = new long[5];

    //5. 모든 추천 기록을 순회하며 등수 계산
    for (Recommendation recommendation : recommendations) {
      Integer matchCount = recommendation.getMatchCount();
      Boolean isBonusMatched = recommendation.getIsBonusMatched();
      if (matchCount == null) {
        continue;
      }
      //6. DB에 저장된 값을 기준으로 등수를 카운트
      if (matchCount == 6) { //1등
        prizeCounts[0]++;
      } else if (matchCount == 5 && isBonusMatched != null && isBonusMatched) { //2등
        prizeCounts[1]++;
      } else if (matchCount == 5) { //3등
        prizeCounts[2]++;
      } else if (matchCount == 4) { //4등
        prizeCounts[3]++;
      } else if (matchCount == 3) { //5등
        prizeCounts[4]++;
      }
    }
    //7. 최종결과를 DTO에 담아 반환
    return new MainPageResponseDto(latestRound, prizeCounts);
  }
}
