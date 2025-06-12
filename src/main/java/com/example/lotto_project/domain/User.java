package com.example.lotto_project.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor //JPA는 기본 생성자 필요
@Entity //이 클래스가 데이터베이스 테이블과 매핑되는 엔티티 클래스임을 명시
@Table(name = "user_account") //데이터베이스에 생성될 테이블 이름을 'user'가 아닌 'user_account'로 지정(user는 예약어일 수 있음)

public class User {

  @Id //이 필드가 테이블의 Primary Key
  @GeneratedValue(strategy = GenerationType.IDENTITY) //PK 값을 데이터베이스가 자동으로 생성(auto-increment)하도록 설정
  private Long id;

  @Column(nullable = false, unique = true, length = 50) //null 불가, 유니크 제약조건, 길이 50
  private String email;

  @Column(nullable = false, length = 100) //null 불가, 길이 100
  private String password;

  @Column(nullable = false, length = 20)
  private String nickname;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  //JPA 엔티티가 처음 저장될 때 자동으로 날짜를 설정하는 프리-이벤트 리스너
  @PrePersist
  public void prePersist() {
    this.createdAt = LocalDateTime.now();
  }
}
