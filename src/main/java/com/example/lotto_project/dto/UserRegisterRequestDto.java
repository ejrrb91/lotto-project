package com.example.lotto_project.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRegisterRequestDto {

  private String email;
  private String password;
  private String nickname;

}
