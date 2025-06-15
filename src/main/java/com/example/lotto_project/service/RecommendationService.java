package com.example.lotto_project.service;

import com.example.lotto_project.domain.AlgorithmType;
import com.example.lotto_project.domain.LottoRound;
import com.example.lotto_project.domain.Recommendation;
import com.example.lotto_project.domain.User;
import com.example.lotto_project.repository.LottoRoundRepository;
import com.example.lotto_project.repository.RecommendationRepository;
import com.example.lotto_project.repository.UserRepository;
import jakarta.persistence.criteria.CriteriaBuilder.In;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RecommendationService {

  private final RecommendationRepository recommendationRepository;
  private final UserRepository userRepository;
  private final LottoRoundRepository lottoRoundRepository;

  /**
   * 무작위 로또 번호 6개를 추천 후, DB에 저장.
   * @param userId
   * @return List<Integer>, 6개의 로또 번호 리스트
   */
  @Transactional
  public List<Integer> recommendRandomNumbers(Long userId) {

    //1. 1부터 45까지의 숫자 리스트 생성
    List<Integer> numbers = new ArrayList<>();

    for (int i = 1; i <= 45; i++) {
      numbers.add(i);
    }

    //2. 리스트를 무작위로 섞음
    Collections.shuffle(numbers);

    //3. 최종 추천 번호를 담을 리스트 생성
    List<Integer> recommendedNumbers = new ArrayList<>();

    //4. 섞인 숫자 리스트에서 앞에서부터 6개 뽑아 추천번호로 선택
    for (int i = 0; i < 6; i++) {
      recommendedNumbers.add(numbers.get(i));
    }

    //5. 추천된 6개의 번호를 오름차순으로 정렬
    recommendedNumbers.sort(Comparator.naturalOrder());

    //6. 추천 결과를 DB에 저장
    saveRecommendation(userId, AlgorithmType.RANDOM, recommendedNumbers);

    //7. 정렬된 최종 번호 반환
    return recommendedNumbers;
  }

  /**
   * 과거 통계를 기반으로 가장 많이 나온 번호 6개를 추천하고, DB에 저장
   * @param userId
   * @return List<Integer>, 6개의 로또 번호 리스트
   */
  public List<Integer> recommendStatisticalNumbers(Long userId) {

    //1. DB에 저장된 모든 회차 정보를 조회
    List<LottoRound> allRounds = lottoRoundRepository.findAll();

    //2. 각 번호가 몇번 나왔는지 횟수를 저장할 Map 생성, Key : 로또 번호, Value : 나온 횟수
    Map<Integer, Integer> numbersFrequency = new HashMap<>();

    //3. 모든 회차를 하나씩 반복
    for (LottoRound round : allRounds) {
      //해당 회차의 당첨번호 6개를 가져온 뒤, 각각의 출현 횟수를 1씩 증가
      updateFrequency(numbersFrequency, round.getWinNum1());
      updateFrequency(numbersFrequency, round.getWinNum2());
      updateFrequency(numbersFrequency, round.getWinNum3());
      updateFrequency(numbersFrequency, round.getWinNum4());
      updateFrequency(numbersFrequency, round.getWinNum5());
      updateFrequency(numbersFrequency, round.getWinNum6());
    }

    //4. 출현 횟수만큼 번호를 담는 추첨함(?) 리스트 생성
    List<Integer> raffleBox = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry : numbersFrequency.entrySet()) {
      int number = entry.getKey(); //Key에서 번호를 가져옴
      int frequency = entry.getValue(); //Value에서 횟수를 가져옴

      for (int i = 0; i < frequency; i++) {
        raffleBox.add(number);
      }
    }

    //5. 추첨함을 무작위로 섞음.
    Collections.shuffle(raffleBox);

    //6. 섞인 추첨함에서 중복되지 않게 6개의 번호를 추출.(6~8번 과정은 추후 스트림으로 공부해보기)
    Set<Integer> uniqueNumbers = new LinkedHashSet<>(); // LinkedHashSet은 넣은 순서를 기억.
    int index = 0; //raffleBox의 몇 번째 공을 뽑을지 가르키는 변수

    while (uniqueNumbers.size() < 6) {
      //혹시 모를 무한 루프를 방지하기 위해, 추첨함의 공을 모두 확인했으면 멈춤.
      if (index >= raffleBox.size()) {
        break;
      }

      //추첨함에서 index 위치의 번호를 뽑아서 Set에 추가
      //만약 이미 있는 번호라고 해도, 중복이 허용되지 않아 추가 안됨
      uniqueNumbers.add(raffleBox.get(index));

      //다음 번호를 추출하기 위해 index에 1추가
      index++;
    }

    //7. Set에 들어있는 6개의 번호를 최종 결과 리스트로 옮김
    List<Integer> recommendedNumbers = new ArrayList<>(uniqueNumbers);

    //8. 6개의 번호를 오름차순으로 정렬
    recommendedNumbers.sort(Comparator.naturalOrder());

    //9. DB에 저장
    saveRecommendation(userId, AlgorithmType.STATISTICS, recommendedNumbers);

    return recommendedNumbers;
  }

  /**
   * Map에서 특정 번호의 카운트를 1씩 증가
   * @param map 횟수를 기록할 Map
   * @param number 카운트할 번호
   */
  private void updateFrequency(Map<Integer, Integer> map, int number) {
    // map.getOrDefault(key, defaultValue)
    // map에서 number를 key로 하는 값을 가져오되, 만약 값이 없을 경우 0을 가져옴.
    // 그리고 가져온 값에 1을 더해서 다시 map에 저장.
    map.put(number, map.getOrDefault(number, 0) + 1);
  }

  /**
   * 추천 결과를 Recommendation 엔티티에 담아 DB에 저장.
   * @param userId
   * @param type
   * @param numbers
   */
  private void saveRecommendation(Long userId, AlgorithmType type, List<Integer> numbers) {

    //1. userID로 User 엔티티를 찾음, 없을 경우 예외 발생
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("해당 ID의 사용자를 찾을 수 없습니다. : " + userId));

    //2. DB에 저장된 가장 마지막 회차 정보를 가져옴, 없을 경우 예외 발생
    LottoRound latestLottoRound = lottoRoundRepository.findTopByOrderByRoundDesc()
        .orElseThrow(() -> new IllegalArgumentException("로또 회차 정보가 없습니다."));

    //3. 새로운 Recommendation객체 생성 후, 파라미터로 받은 정보들을 설정
    Recommendation recommendation = new Recommendation();
    recommendation.setUser(user);
    //추천은 항상 다음 회차를 대상으로 하기에 + 1 추가
    recommendation.setLottoRound(latestLottoRound.getRound() + 1);
    recommendation.setAlgorithmType(type);
    recommendation.setNum1(numbers.get(0));
    recommendation.setNum2(numbers.get(1));
    recommendation.setNum3(numbers.get(2));
    recommendation.setNum4(numbers.get(3));
    recommendation.setNum5(numbers.get(4));
    recommendation.setNum6(numbers.get(5));

    //4. DB에 저장
    recommendationRepository.save(recommendation);
  }

}
