package com.example.lotto_project.config.jwt;

import com.example.lotto_project.security.UserDetailsImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  //doFilterInternal 메소드가 protected로 선언되어 있기 때문에, protected 사용.
  @Override
  protected void doFilterInternal(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, FilterChain filterChain)
      throws ServletException, IOException {

    //1. 토큰 재발급인 경우 해당 필터 건너 뜀
    if (httpServletRequest.getRequestURI().equals("/api/users/reissue")) {
      filterChain.doFilter(httpServletRequest, httpServletResponse);
      return;
    }

    //2. 요청 헤더에서 JWT 토큰을 가져옴
    String token = resolveTokenFromHttpServletRequest(httpServletRequest);

    //3. 토큰이 유요한 경우
    if (StringUtils.hasText(token) && jwtUtil.validationToken(token)) {
      //토큰에서 사용자 이메일을 가져와서 DB에서 조회
      String email = jwtUtil.getEmailFromToken(token);
      UserDetailsImpl userDetails = (UserDetailsImpl) userDetailsService.loadUserByUsername(email);

      //인증 정보를 생성
      Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
          userDetails.getAuthorities());

      //SecuriryContext에 인증 정보를 설정
      SecurityContext context = SecurityContextHolder.createEmptyContext();
      context.setAuthentication(authentication);
      SecurityContextHolder.setContext(context);

    }

    //다음 필터로 요청 전달
    filterChain.doFilter(httpServletRequest, httpServletResponse);
  }

  // 요청 헤더에서 'Bearer ' 접두사를 제거하고 순수한 토큰 값만 리턴
  private String resolveTokenFromHttpServletRequest(HttpServletRequest httpServletRequest) {
    String bearerToken = httpServletRequest.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return null;
  }

}
