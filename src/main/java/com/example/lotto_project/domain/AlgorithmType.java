package com.example.lotto_project.domain;

public enum AlgorithmType {
  RANDOM, //무작위 랜덤
  STATISTICS, //과거 통계 기반
  INFREQUENT, //최근 미출현
  RARE_NUMBER, //역사적 희귀 번호
  ODD_EVEV_RATIO, //홀짝 비율
  RECENT_6_MONTH //최근 6개월 분석
}
