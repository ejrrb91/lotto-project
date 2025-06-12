package com.example.lotto_project.repository;

import com.example.lotto_project.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JpaRepository<관리할 엔티티, 엔티티의 PK 타입>
// 이 인터페이스를 만들어두는 것만으로 기본적인 DB 작업(save, findById, findAll, delete 등)이 가능.
public interface UserRepository extends JpaRepository<User, Long> {

}
