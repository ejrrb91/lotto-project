package com.example.lotto_project.service;

import com.example.lotto_project.domain.LottoRound;
import com.example.lotto_project.repository.LottoRoundRepository;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

  @Transactional
  public void saveWinningNumbersByRound(int round) {
    //1.DB에 해당 회차 데이터가 이미 있는지 확인.
    if (lottoRoundRepository.existsById(round)) {
      System.out.println("회차 " + round + " 데이터는 이미 DB에 존재합니다.");
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
        System.out.println("회차 " + round + "의 당첨 결과가 아직 발표되지 않았습니다.");
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

      //6.완성된 엔티티를 데이터베이스에 저장합니다.
      lottoRoundRepository.save(lottoRound);
      System.out.println("회차 " + round + " 당첨 번호 저장 성공");

    } catch (IOException e) {
      System.err.println("회차 " + round + " 데이터를 가져오는 중 오류가 발생했습니다. : " + e.getMessage());
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
}
