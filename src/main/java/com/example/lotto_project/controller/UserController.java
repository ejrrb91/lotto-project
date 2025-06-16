package com.example.lotto_project.controller;

import com.example.lotto_project.dto.LoginRequestDto;
import com.example.lotto_project.dto.TokenResponseDto;
import com.example.lotto_project.dto.UserRegisterRequestDto;
import com.example.lotto_project.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("register")
  public ResponseEntity<String> registerUser(
      @RequestBody UserRegisterRequestDto userRegisterRequestDto) {

    userService.registerUser(userRegisterRequestDto);
    return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponseDto> login(@RequestBody LoginRequestDto loginRequestDto) {

    TokenResponseDto tokenResponseDto = userService.login(loginRequestDto);

    return ResponseEntity.ok(tokenResponseDto);
  }
}
