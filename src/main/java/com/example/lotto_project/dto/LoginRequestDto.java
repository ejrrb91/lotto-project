package com.example.lotto_project.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 요청을 처리하기 위한 DTO
 */
@Getter
@Setter
public class LoginRequestDto {

  private String email;
  private String password;
}
