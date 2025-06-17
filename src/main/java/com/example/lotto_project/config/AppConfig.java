package com.example.lotto_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AppConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    //BCrypt는 현재 가장 널리 쓰이는 안전한 패스워드 해싱 알고리즘 중 하나
    return new BCryptPasswordEncoder();
  }
}
