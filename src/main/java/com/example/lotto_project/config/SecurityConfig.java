package com.example.lotto_project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration //해당 클래스는 스프링의 설정 파일임을 나타냄
public class SecurityConfig {

  @Bean
  public PasswordEncoder passwordEncoder() {
    //BCrypt는 현재 가장 널리 쓰이는 안전한 패스워드 해싱 알고리즘 중 하나
    return new BCryptPasswordEncoder();
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    //CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화
    //REST API는 보통 세션이 아닌 토큰기반이므로 비활성화 해도 안전함.
    httpSecurity.csrf(csrf -> csrf.disable());

    //세션 관리 방식을 'STATELESS'로 설정하여 세션 사용 않하도록 설정
    //JWT 기반 인증에 적합함.
    httpSecurity.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    //HTTP 요청에 대한 접근 권한을 설정
    httpSecurity.authorizeHttpRequests(auth -> auth
        //아래에 명시된 주소 패턴에 대한 요청은 인증 없이 누구나 접근 허용
        .requestMatchers("/api/users/register", //회원가입 API
            "api/recommend/**" //모든 번호 추천 API
        ).permitAll()
        //TODO: 그 외의 요청은 일단 모두 허용(추후 로그인 기능 구현 후 변경 예정)
        .anyRequest().permitAll()
    );

    return httpSecurity.build();
  }
}
