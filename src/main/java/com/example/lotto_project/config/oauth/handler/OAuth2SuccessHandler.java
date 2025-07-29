package com.example.lotto_project.config.oauth.handler;

import com.example.lotto_project.config.jwt.JwtUtil;
import com.example.lotto_project.security.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;
  private final RedisTemplate redisTemplate;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Authentication authentication)
      throws IOException, ServletException {
    //1. Spring Security의 SecurityContext에서 인증된 사용자 정보를 가져옴.
    //이 정보는 CustomOAuth2UserService에서 반환한 UserDetailsImpl 객체임.
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    //2. 사용자 정보에서 이메일을 추출
    String email = userDetails.getUsername();
    String nickname = userDetails.getNickname();

    //3.JwtUtil을 사용하여 AccessToken과 RefreshToken을 생성
    String accessToken = jwtUtil.createAccessToken(email, nickname);
    String refreshToken = jwtUtil.createRefreshToken(email, nickname);

    //4. Redis에 RefreshToken 저장
    redisTemplate.opsForValue().set(
        email,
        refreshToken,
        JwtUtil.REFRESH_TOKEN_EXPIRATION_TIME,
        TimeUnit.MILLISECONDS
    );

    //5. UriComponentsBuilder를 사용하여 리디렉션 URL을 안전하게 생성
    String targetUrl = UriComponentsBuilder.fromUriString("http://lottohelper.kr/")
        .queryParam("accessToken", accessToken)
        .queryParam("refreshToken", refreshToken)
        .build()
        .encode(StandardCharsets.UTF_8)
        .toUriString();

    //5. 생성된 URL로 사용자를 리디렉션
    httpServletResponse.sendRedirect(targetUrl);
  }
}
