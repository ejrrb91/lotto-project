package com.example.lotto_project.repository;

import com.example.lotto_project.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<관리할 엔티티, 엔티티의 PK 타입>
// 이 인터페이스를 만들어두는 것만으로 기본적인 DB 작업(save, findById, findAll, delete 등)이 가능.
public interface UserRepository extends JpaRepository<User, Long> {

  // 'Email'을 기준으로 사용자를 찾는다는 메소드
  // Spring Data JPA가 이 이름만 보고 알아서 SQL 쿼리를 생성.
  // Optional<User>는 사용자가 있을 수도, 없을 수도 있다는 것을 나타내서 Null 에러를 방지.
  Optional<User> findByEmail(String email);

}
