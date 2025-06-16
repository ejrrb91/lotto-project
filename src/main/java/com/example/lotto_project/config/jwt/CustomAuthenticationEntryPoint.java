package com.example.lotto_project.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

  @Override
  public void commence(HttpServletRequest httpServletRequest,
      HttpServletResponse httpServletResponse, AuthenticationException authenticationException) throws IOException, ServletException {

    //응담상태 코드를 401 Unauthorized로 설정
    httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    //응답 컨텐츠 타입을 JSON으로 설정
    httpServletResponse.setContentType("application/json;charset=UTF-8");

    //응답 본문에 담을 내용을 Map으로 생성
    Map<String, Object> data = new HashMap<>();
    data.put("status", HttpServletResponse.SC_UNAUTHORIZED);
    data.put("message", "인증이 필요한 서비스 입니다.");

    //ObjectMapper를 사용하여 Map을 JSON 문자열로 변환
    ObjectMapper objectMapper = new ObjectMapper();
    String responseJson = objectMapper.writeValueAsString(data);

    //응답 본문에 JSON을 작성
    httpServletResponse.getWriter().write(responseJson);

  }

}
