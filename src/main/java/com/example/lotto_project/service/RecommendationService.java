package com.example.lotto_project.service;

import com.example.lotto_project.domain.AlgorithmType;
import com.example.lotto_project.domain.LottoRound;
import com.example.lotto_project.domain.Recommendation;
import com.example.lotto_project.domain.User;
import com.example.lotto_project.dto.MyPageResponseDto;
import com.example.lotto_project.dto.RecommendationResponseDto;
import com.example.lotto_project.repository.LottoRoundRepository;
import com.example.lotto_project.repository.RecommendationRepository;
import com.example.lotto_project.repository.UserRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
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
   *
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
   * 과거 통계를 기반으로 가장 많이 나온 번호 6개를 추천하고, DB에 저장.
   *
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
   * 최근 5주간 미출현 번호에 가중치 부여하여 번호 6개를 추천하고, DB에 저장
   *
   * @param userId
   * @return List<Integer>, 6개의 로또 번호 리스트
   */
  @Transactional
  public List<Integer> recommendInfrequentNumbers(Long userId) {
    //1. DB에서 가장 최신회차 5개의 데이터 추출
    List<LottoRound> recentRounds = lottoRoundRepository.findTop5ByOrderByRoundDesc();

    //2. 5주간의 모든 당첨 번호를 중복 없이 Set에 저장
    Set<Integer> recentWinningNumbers = new HashSet<>();

    //3. 가중치가 부여된 RaffleBox 생성
    for (LottoRound lottoRound : recentRounds) {
      recentWinningNumbers.addAll(
          List.of(lottoRound.getWinNum1(), lottoRound.getWinNum2(), lottoRound.getWinNum3(),
              lottoRound.getWinNum4(), lottoRound.getWinNum5(), lottoRound.getWinNum6()));
    }

    List<Integer> raffleBox = new ArrayList<>();

    for (int i = 0; i <= 45; i++) {
      if (recentWinningNumbers.contains(i)) {
        //최근에 나왔을 경우 (가중치 1)
        raffleBox.add(i);
      } else {
        //최근에 안나왔을 경우 (가중치 3)
        raffleBox.add(i);
        raffleBox.add(i);
        raffleBox.add(i);
      }
    }
    return drawNumbersFromRaffleBox(raffleBox, userId, AlgorithmType.INFREQUENT);
  }

  /**
   * 전체 기간 동안 가장 적게 나온 번호에 가중치를 부여하여 번호 6개를 추천하고, DB에 저장
   *
   * @param userId
   * @return List<Integer>, 6개의 로또 번호 리스트
   */
  @Transactional
  public List<Integer> recommendRareNumbers(Long userId) {
    //1. DB에서 모든 회차 정보를 조회하여 번호별로 출현 빈도 계산
    List<LottoRound> allRounds = lottoRoundRepository.findAll();
    Map<Integer, Integer> numbersFrequency = getNumberFrequency(allRounds);

    //2. 가장 많이 나온 번호의 횟수를 찾음
    List<Integer> raffleBox = new ArrayList<>();
    int maxFrequency = numbersFrequency.isEmpty() ? 0 : Collections.max(numbersFrequency.values());

    //3. 희귀한 정도에 따라서 가중치가 부여된 RaffleBox 생성
    for (int i = 0; i <= 45; i++) {
      int frequency = numbersFrequency.getOrDefault(i, 0);
      int weight = maxFrequency - frequency; //나온 횟수가 적을 수록 가중치가 높아짐.

      for (int j = 0; j < weight; j++) {
        raffleBox.add(i);
      }
      //모든 번호가 최소 1번은 포함 되어야 함
      raffleBox.add(i);
    }
    //4. RaffleBox에서 6개의 번호를 추출하여 저장한 뒤 반환
    return drawNumbersFromRaffleBox(raffleBox, userId, AlgorithmType.RARE_NUMBER);
  }

  /**
   * 최근 6개월간 3번 이상 나온 번호들 중에서 무작위로 번호 6개를 추천하고, DB에 저장
   *
   * @param userId
   * @return List<Integer>, 6개의 로또 번호 리스트
   */
  @Transactional
  public List<Integer> recommendRecentSixMonthsNumbers(Long userId) {
    //1. 오늘 날짜로 부터 6개월 전 날짜 계산
    LocalDate sixMonthsAgo = LocalDate.now().minusMonths(6);

    //2. 6개월 간의 데이터를 DB에서 조회
    List<LottoRound> recentRounds = lottoRoundRepository.findByDrawDateAfter(sixMonthsAgo);
    Map<Integer, Integer> numbersFrequency = getNumberFrequency(recentRounds);

    //3. 3번 이상 당첨된 번호들만 필터링 하여 리스트에 담음
    List<Integer> frequencyNumbers = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry : numbersFrequency.entrySet()) {
      if (entry.getValue() >= 3) {
        frequencyNumbers.add(entry.getKey());
      }
    }

    //4. 3번 이상 당첨된 번호가 6개 미만일 경우, 그냥 랜덤으로 추천
    if (frequencyNumbers.size() < 6) {
      return recommendRandomNumbers(userId);
    }

    //5. 필터링 된 번호 중, 6개를 무작위로 선택 후, 정렬하여 저장한 뒤 반환
    Collections.shuffle(frequencyNumbers);
    List<Integer> recommendedNumbers = new ArrayList<>(frequencyNumbers.subList(0, 6));
    recommendedNumbers.sort(Comparator.naturalOrder());
    saveRecommendation(userId, AlgorithmType.RECENT_6_MONTH, recommendedNumbers);
    return recommendedNumbers;
  }

  /**
   * 홀수 3개, 짝수 3개를 맞춰서 번호 6개를 추천하고, DB에 저장
   *
   * @param userId
   * @return List<Integer>, 6개의 로또 번호 리스트
   */
  @Transactional
  public List<Integer> recommendOddEvenRatioNumbers(Long userId) {
    List<Integer> oddNumbers = new ArrayList<>();
    List<Integer> evenNumbers = new ArrayList<>();

    //1. 1 ~ 45번을 홀수 그룹과 짝수 그룹으로 나눔
    for (int i = 1; i <= 45; i++) {
      if (i % 2 == 0) {
        evenNumbers.add(i);
      } else {
        oddNumbers.add(i);
      }
    }

    //2. 각 그룹의 숫자 목록을 무작위로 섞음
    Collections.shuffle(oddNumbers);
    Collections.shuffle(evenNumbers);

    //3. 홀수 3개, 짝수 3개를 각 그룹에서 추출하여 조합
    List<Integer> recommendedNumbers = new ArrayList<>();
    recommendedNumbers.addAll(oddNumbers.subList(0, 3));
    recommendedNumbers.addAll(evenNumbers.subList(0, 3));

    //4. 최종 6개 번호를 정렬하고, 저장한 뒤 반환
    recommendedNumbers.sort(Comparator.naturalOrder());
    saveRecommendation(userId, AlgorithmType.ODD_EVEN_RATIO, recommendedNumbers);
    return recommendedNumbers;
  }


  /**
   * Map에서 특정 번호의 카운트를 1씩 증가
   *
   * @param map    횟수를 기록할 Map
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
   *
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

  /**
   * 추천 받은 번호 조회
   *
   * @param userId
   * @return
   */
  @Transactional(readOnly = true)
  public MyPageResponseDto getMyRecommendations(Long userId) {

    LottoRound latestRound = lottoRoundRepository.findTopByOrderByRoundDesc().orElse(null);
    
    //1. 사용자의 모든 추천 기록을 가져옴.
    List<Recommendation> recommendations = recommendationRepository.findByUserIdOrderByIdDesc(
        userId);

    //2. 추천 기록들에서 필요한 모든 회차 번호를 중벅없이 추출
    Set<Integer> roundNumbers = recommendations.stream()
        .map(Recommendation::getLottoRound)
        .collect(Collectors.toSet());

    //3. 추출된 회차 번호에 해당하는 모든 당점 정보를 DB에서 한번에 조회
    Map<Integer, LottoRound> lottoRoundMap = lottoRoundRepository.findAllByRoundIn(roundNumbers)
        .stream()
        .collect(Collectors.toMap(LottoRound::getRound, round -> round));

    List<RecommendationResponseDto> recommendationResponseDtos = new ArrayList<>();
    for (Recommendation recommendation : recommendations) {
      RecommendationResponseDto recommendationResponseDto = new RecommendationResponseDto(
          recommendation);

      //4. 현재 추천 기록의 회차에 해당하는 당첨 정보를 Map에서 찾음
      LottoRound winningRound = lottoRoundMap.get(recommendation.getLottoRound());

      //5. 해당 회차의 추첨이 완료되었거나 당첨 정보가 있을 경우
      if (winningRound != null) {
        //DTO에 해당 회차의 당첨 번호와 보너스 번호를 설정
        recommendationResponseDto.setWinningNumbers(List.of(winningRound.getWinNum1(),
            winningRound.getWinNum2(), winningRound.getWinNum3(), winningRound.getWinNum4(),
            winningRound.getWinNum5(), winningRound.getWinNum6()));
        recommendationResponseDto.setBonusNumber(winningRound.getBonusNum());

        //등수 계산 로직
        Integer matchCount = recommendation.getMatchCount();
        if (matchCount != null) {
          Boolean isBonusMatched = recommendation.getIsBonusMatched();
          String rank = calculateRank(matchCount, isBonusMatched);
          recommendationResponseDto.setRank(rank);
        }
      } else {
        //아직 추첨 전인 경우
        recommendationResponseDto.setRank("추첨 전");
      }
      recommendationResponseDtos.add(recommendationResponseDto);
    }
    return new MyPageResponseDto(latestRound, recommendationResponseDtos);
  }

  /**
   * 여러 회차 정보로부터 번호별 출현 빈도 계싼
   *
   * @param lottoRounds
   * @return 번호를 Key로, 출현 횟수를 Value로 갖는 Map
   */
  private Map<Integer, Integer> getNumberFrequency(List<LottoRound> lottoRounds) {

    Map<Integer, Integer> frequencyMap = new HashMap<>();

    for (LottoRound lottoRound : lottoRounds) {
      updateFrequency(frequencyMap, lottoRound.getWinNum1());
      updateFrequency(frequencyMap, lottoRound.getWinNum2());
      updateFrequency(frequencyMap, lottoRound.getWinNum3());
      updateFrequency(frequencyMap, lottoRound.getWinNum4());
      updateFrequency(frequencyMap, lottoRound.getWinNum5());
      updateFrequency(frequencyMap, lottoRound.getWinNum6());
    }
    return frequencyMap;
  }

  /**
   * 가중치가 적용된 raffleBox에서 중복되지 않게 6개의 번호를 추출
   *
   * @param raffleBox
   * @param userId
   * @param type
   * @return List<Integer>, 6개의 로또 번호 리스트
   */
  private List<Integer> drawNumbersFromRaffleBox(List<Integer> raffleBox, Long userId,
      AlgorithmType type) {
    Collections.shuffle(raffleBox);
    Set<Integer> uniqueNumbers = new LinkedHashSet<>();
    int index = 0;
    while (uniqueNumbers.size() < 6) {
      if (index >= raffleBox.size()) {
        break;
      }
      uniqueNumbers.add(raffleBox.get(index));
      index++;
    }

    List<Integer> recommendedNumbers = new ArrayList<>(uniqueNumbers);
    recommendedNumbers.sort(Comparator.naturalOrder());
    saveRecommendation(userId, type, recommendedNumbers);
    return recommendedNumbers;
  }

  /**
   * 로또 등수 계산 로직
   */
  private String calculateRank(int matchCount, Boolean isBonusMatched) {
    if (matchCount == 6) {
      return "1등";
    } else if (matchCount == 5 && isBonusMatched != null && isBonusMatched) {
      return "2등";
    } else if (matchCount == 5) {
      return "3등";
    } else if (matchCount == 4) {
      return "4등";
    } else if (matchCount == 3) {
      return "5등";
    } else {
      return "낙첨";
    }
  }
}
