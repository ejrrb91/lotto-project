package com.example.lotto_project.service;

import com.example.lotto_project.domain.User;
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

  @Transactional
  public void registerUser(UserRegisterRequestDto userRegisterRequestDto) {
    //이메일 중복 체크
    if (userRepository.findByEmail(userRegisterRequestDto.getEmail()).isPresent()) {
      throw new IllegalArgumentException("이미 사용중인 이메일입니다.");
    }

    User user = new User();
    user.setEmail(userRegisterRequestDto.getEmail());
    //비밀번호는 반드시 암호화 해서 저장
    user.setPassword(passwordEncoder.encode(userRegisterRequestDto.getPassword()));
    user.setNickname(userRegisterRequestDto.getNickname());

    userRepository.save(user);
  }

}
