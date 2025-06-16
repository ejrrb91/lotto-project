package com.example.lotto_project.config;

import com.example.lotto_project.config.jwt.CustomAuthenticationEntryPoint;
import com.example.lotto_project.config.jwt.JwtAuthenticationFilter;
import com.example.lotto_project.config.jwt.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //해당 클래스는 스프링의 설정 파일임을 나타냄
@EnableWebSecurity //웹 보안을 활성화
@RequiredArgsConstructor //생성자 자동으로 만들어 줌.
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

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
        //'/api/users/**' 경로는 인증 없이 누구나 접근 허용 (회원가입, 로그인 등)
        .requestMatchers("/api/users/**").permitAll()
        //그 외의 모든 요청은 반드시 인증(로그인)이 필요함
        .anyRequest().authenticated()
    );
    //JwtAuthenticationFilter를 Spring Security의 기본 필터(UsernamePasswordAuthenticationFilter) 앞에 배치
    //이렇게 하면 모든 요청이 컨트롤러에 도달하기 전에 JWT 검사를 먼저 실행.
    httpSecurity.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
        UsernamePasswordAuthenticationFilter.class);

    //인증 예외 발생 시, CustomAuthenticationEntryPoint를 사용하도록 설정.
    httpSecurity.exceptionHandling(
        exception -> exception.authenticationEntryPoint(customAuthenticationEntryPoint));

    return httpSecurity.build();
  }
}
