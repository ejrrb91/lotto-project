package com.example.lotto_project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 로그인 성공 시, 클라이언트에게
 * AccessToken, RefreshToken을
 * 전달하기 위한 DTO
 */
@Getter
@AllArgsConstructor //모든 필드를 인자로 받는 생성자를 만들어줌.
public class TokenResponseDto {

  private String accessToken;
  private String refreshToken;

}
