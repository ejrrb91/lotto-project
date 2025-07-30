package com.example.lotto_project.config;

import static org.springframework.security.config.Customizer.withDefaults;

import com.example.lotto_project.config.jwt.CustomAuthenticationEntryPoint;
import com.example.lotto_project.config.jwt.JwtAuthenticationFilter;
import com.example.lotto_project.config.jwt.JwtUtil;
import com.example.lotto_project.config.oauth.handler.OAuth2SuccessHandler;
import com.example.lotto_project.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration //해당 클래스는 스프링의 설정 파일임을 나타냄
@EnableWebSecurity //웹 보안을 활성화
@RequiredArgsConstructor //생성자 자동으로 만들어 줌.
@EnableMethodSecurity // 메소드 수준의 보안 설정을 활성화 (나중에 @PreAuthorize 등을 사용할 수 있음).
public class SecurityConfig {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;
  private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  //Spring Security의 메인 보안 설정 구성하는 Bean
  //HTTP 요청에 대한 보안 규칙, 필터, 세션 정책 등을 설정
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    //CORS 설정 활성화
    //withDefaults()는 @Bean으로 등록된 CorsConfigurationSource에 정의된 CORS 설정을 따름.
    httpSecurity.cors(withDefaults());

    //CSRF 보호 기능 비활성화
    //JWT와 같은 토큰 기반 인증 방식에서는 서버에 세션을 저장하지 않으므로 CSRF 공격에 비교적 안전하여 비활성화
    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    //세션 관리 정책을 STATELESS로 설정
    //서버가 클라이언트의 상태를 저장하지 않는 '무상태(STATELESS)' 방식으로 운영
    //모든 인증은 요청에 포함된 JWT를 통해 진행
    httpSecurity.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    //JWT 인증 필터 및 예외 처리 핸들러 추가
    //JwtAuthenticationFilter를 Spring Security의 기본 로그인 필터(UsernamePasswordAuthenticationFilter)앞에 추가
    //이렇게 하면 모든 HTTP 요청이 비즈니스 로직에 도달하기 전에, 이 필터가 먼저 JWT 토큰의 유효성을 검사하여 사용자를 인증
    httpSecurity.addFilterBefore(new JwtAuthenticationFilter(jwtUtil, userDetailsService),
        UsernamePasswordAuthenticationFilter.class);

    //인증 과정에서 발생하는 예외를 처리할 핸들러 등록
    httpSecurity.exceptionHandling(
        ex -> ex.authenticationEntryPoint(customAuthenticationEntryPoint));

    //OAuth2(소셜 로그인) 설정
    httpSecurity.oauth2Login(
        oauth2 -> oauth2
            //authorizationEndpoint의 기본 URI를 명시적으로 설정
            .authorizationEndpoint(authorization -> authorization.baseUri("/oauth2/authorization"))
            //redirectionEndpoint의 기본 URI를 명시적으로 설정
            .redirectionEndpoint(redirection -> redirection.baseUri("/login/oauth2/code/*"))
            //소셜 로그인 성공 후, 사용자 정보를 가져오는 최종 엔드포인트(user-info-uri)에서 사용자 정보를 처리할 서비스 지정
            .userInfoEndpoint(userInfo -> userInfo.userService(customOAuth2UserService))
            //소셜 로그인의 모든 과정이 성공적으로 끝나면, successHandler를 호출하여 후속 작업 진행(JWT 토큰 발급 및 리디렉션)
            .successHandler(oAuth2SuccessHandler));

    //요청 경로별 접근 권한 설정
    httpSecurity.authorizeHttpRequests(authorize -> authorize
        // "/ws/**" 경로: 웹소켓 연결을 위한 경로이므로, 누구나 접근할 수 있도록 허용(실제 인증은 연결 후 StompHandler가 담당).
        // "/api/users/**": 회원가입, 로그인 등 인증 없이 사용해야 하는 기능들이므로, 누구나 접근을 허용.
        // "/oauth2/**": 소셜 로그인 프로세스를 위한 경로이므로, 누구나 접근을 허용.
        // "/api/main-page": 메인 페이지 데이터는 로그인하지 않은 사용자도 볼 수 있으므로, 누구나 접근을 허용.
        .requestMatchers("/ws/**", "/api/users/**", "/oauth2/**", "/login/**", "/api/main-page")
        .permitAll()
        // "/api/users/reissue": 토큰 재발급 경로는 모두 허용
        .requestMatchers("/api/users/reissue").permitAll()
        // "/api/recommend/**": 추천 API는 인증된 사용자만 접근
        .requestMatchers("/api/recommend/**").authenticated()
        // "/api/users/logout" 로그아웃 API는 인증된 사용자만 접근 가능
        .requestMatchers("/api/users/logout").authenticated()
        // 위에서 정의한 경로 이외의 모든 요청은 반드시 인증(로그인)된 사용자만 접근할 수 있도록 설정.
        // 이 규칙은 항상 가장 마지막에 위치해야함.
        .anyRequest().authenticated()
    );

    // 설정이 완료된 HttpSecurity 객체를 빌드하여 반환.
    return httpSecurity.build();
  }

}
