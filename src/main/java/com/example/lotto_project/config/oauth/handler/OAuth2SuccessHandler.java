package com.example.lotto_project.config.oauth.handler;

import com.example.lotto_project.config.jwt.JwtUtil;
import com.example.lotto_project.security.UserDetailsImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

  private final JwtUtil jwtUtil;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, Authentication authentication)
      throws IOException, ServletException {
    //1. Spring Security의 SecurityContext에서 인증된 사용자 정보를 가져옴.
    //이 정보는 CustomOAuth2UserService에서 반환한 UserDetailsImpl 객체임.
    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

    //2. 사용자 정보에서 이메일을 추출
    String email = userDetails.getUsername();

    //3.JwtUtil을 사용하여 AccessToken과 RefreshToken을 생성
    String accessToken = jwtUtil.createAccessToken(email);
    String refreshToken = jwtUtil.createRefreshToken(email);

    //4. 사용자의 브라우저를 특정 주소로 리디렉션 시킴
    //TODO: 지금은 프론트엔드가 없으므로, 토큰을 쿼리 파라미터로 붙여서 가상의 주소로 보냄.
    //      프론트엔드 개발할때 이 주소에서 토큰들을 파싱하여 안전하게 저장.
    httpServletResponse.sendRedirect(
        "http://localhost:3000/oauth-redirect?accessToken=" + accessToken + "&refreshToken="
            + refreshToken);
  }
}
