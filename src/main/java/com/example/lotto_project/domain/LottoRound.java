package com.example.lotto_project.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.LocalDate;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity //이 클래스가 데이터베이스 테이블과 1:1로 매핑되는 클래스임을 알려줌
public class LottoRound {

  @Id //이 필드가 테이블의 Primary Key임을 나타냄
  private Integer round; //회차번호(PK)

  private LocalDate drawDate; //추첨일

  private Integer winNum1;
  private Integer winNum2;
  private Integer winNum3;
  private Integer winNum4;
  private Integer winNum5;
  private Integer winNum6;

  private Integer bonusNum;


}
