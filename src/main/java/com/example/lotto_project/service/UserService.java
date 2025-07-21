package com.example.lotto_project.service;

import com.example.lotto_project.config.jwt.JwtUtil;
import com.example.lotto_project.domain.User;
import com.example.lotto_project.dto.LoginRequestDto;
import com.example.lotto_project.dto.TokenResponseDto;
import com.example.lotto_project.dto.UserRegisterRequestDto;
import com.example.lotto_project.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtUtil jwtUtil;

  @Transactional
  public void registerUser(UserRegisterRequestDto userRegisterRequestDto) {
    //1. 이메일 중복 체크
    if (userRepository.findByEmail(userRegisterRequestDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
    }

    User user = new User();
    user.setEmail(userRegisterRequestDto.getEmail());
    //2. 비밀번호는 반드시 암호화 해서 저장
    user.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));
    user.setNickname(userRegisterRequestDto.getNickname());

    userRepository.save(user);
  }

  /**
   * 로그인을 처리하는 메서드
   *
   * @param loginRequestDto
   * @return
   */
  @Transactional(readOnly = true) //조회만 하는 작업이므로 readOnly = true로 설정
  public TokenResponseDto login(LoginRequestDto loginRequestDto) {

    //1. 이메일을 기준으로 사용자를 DB에서 조회.
    User user = userRepository.findByEmail(loginRequestDto.getEmail())
        //사용자가 없을 경우 예외 발생
        .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

    //2. DB에 저장된 암호화된 비밀번호와, 사용자가 입력한 비밀번호가 일치하는지 확인.
    if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
      //일치하지 않으면 예외 발생
      throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
    }

    //3. 인증이 성공하면, AccessToken, RefreshToken을 생성.
    String accessToken = jwtUtil.createAccessToken(user.getEmail(), user.getNickname());
    String refreshToken = jwtUtil.createRefreshToken(user.getEmail(), user.getNickname());

    //4. AccessToken, RefreshToken을 DTO에 담아 리턴
    return new TokenResponseDto(accessToken, refreshToken);
  }
}
