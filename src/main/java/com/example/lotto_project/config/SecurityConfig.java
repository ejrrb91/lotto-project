package com.example.lotto_project.config;

import com.example.lotto_project.config.jwt.CustomAuthenticationEntryPoint;
import com.example.lotto_project.config.jwt.JwtAuthenticationFilter;
import com.example.lotto_project.config.jwt.JwtUtil;
import com.example.lotto_project.config.oauth.handler.OAuth2SuccessHandler;
import com.example.lotto_project.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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
  private final CustomOAuth2UserService customOAuth2UserService;
  private final OAuth2SuccessHandler oAuth2SuccessHandler;

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    //CSRF(Cross-Site Request Forgery) 보호 기능을 비활성화
    //REST API는 보통 세션이 아닌 토큰기반이므로 비활성화 해도 안전함.
    httpSecurity.csrf(AbstractHttpConfigurer::disable);

    //세션 관리 방식을 'STATELESS'로 설정하여 세션 사용 않하도록 설정
    //JWT 기반 인증에 적합함.
    httpSecurity.sessionManagement(
        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

    //HTTP 요청에 대한 접근 권한을 설정
    httpSecurity.authorizeHttpRequests(auth -> auth
        //HttpMethod.OPTIONS인 모든 요청에 대해서는 인증 없이 접근 허용(CORS preflight)
        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
        //'/api/users/**' 경로는 인증 없이 누구나 접근 허용 (회원가입, 로그인, 소셜 로그인 등)
        .requestMatchers("/api/users/**", "/login/oauth2/**", "/oauth2/**").permitAll()
        //그 외의 모든 요청은 반드시 인증(로그인)이 필요함
        .anyRequest().authenticated()
    );

    //OAuth2 로그인 관련 설정
    httpSecurity.oauth2Login(oauth2 -> oauth2
        //1. userInfoEndpoint() : OAuth2 로그인 성공 후, 사용자 정보를 가져오는 설정을 담당
        .userInfoEndpoint(userInfo ->
            // 2. .userService(customOAuth2UserService): 구글 로그인이 성공하면,
            //    Spring Security는 구글로부터 받은 사용자 정보를 customOAuth2UserService에게 넘겨줌,
            //    그러면 이 서비스가 사용자를 DB에 저장하거나 업데이트하는 등의 작업을 처리함.
            userInfo.userService(customOAuth2UserService)
        )
        // 3. .successHandler(oAuth2SuccessHandler): OAuth2 로그인의 모든 과정이 성공적으로 끝나면,
        //    oAuth2SuccessHandler를 호출하여 후속 작업을 진행합니다(JWT 토큰 발급 및 리디렉션).
        .successHandler(oAuth2SuccessHandler)
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
