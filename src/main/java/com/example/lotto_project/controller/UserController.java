package com.example.lotto_project.controller;

import com.example.lotto_project.dto.LoginRequestDto;
import com.example.lotto_project.dto.TokenDto;
import com.example.lotto_project.dto.UserRegisterRequestDto;
import com.example.lotto_project.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

  private final UserService userService;

  @PostMapping("/register")
  public ResponseEntity<String> registerUser(
      @RequestBody UserRegisterRequestDto userRegisterRequestDto) {

    userService.registerUser(userRegisterRequestDto);
    return new ResponseEntity<>("회원가입이 완료되었습니다.", HttpStatus.CREATED);
  }

  @PostMapping("/login")
  public ResponseEntity<TokenDto> login(@RequestBody LoginRequestDto loginRequestDto) {

    TokenDto tokenDto = userService.login(loginRequestDto);

    return ResponseEntity.ok(tokenDto);
  }

  @PostMapping("/reissue")
  public ResponseEntity<TokenDto> reissueToken(HttpServletRequest httpServletRequest) {
    String bearerToken = httpServletRequest.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      String refreshToken = bearerToken.substring(7);
      TokenDto newTokenDto = userService.reissueToken(refreshToken);
      return ResponseEntity.ok(newTokenDto);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
  }
}
