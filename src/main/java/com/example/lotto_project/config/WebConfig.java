package com.example.lotto_project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  @Override
  public void addCorsMappings(CorsRegistry corsRegistry) {
    //모든 경로("/**")에 대해서 CORS 설정을 적용
    corsRegistry.addMapping("/**")
        //Nginx를 통해 접속하는 주소인 http://localhost를 허용
        .allowedOrigins("http://localhost", "http://lottohelper.kr")
        //허용할 HTTP 메서드 지정
        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        //요청 헤더에 포함될 수 있는 값을 지정
        .allowedHeaders("*")
        //쿠키를 포함한 요청을 허용할지 여부를 설정(나중에 JWT Refresh Token 등을 위해 필요할 수 있음.)
        .allowCredentials(true)
        //브라우저가 preflight 요청의 결과를 캐시할 수 있는 시간을 설정(초 단위)
        .maxAge(3600);

  }
}
