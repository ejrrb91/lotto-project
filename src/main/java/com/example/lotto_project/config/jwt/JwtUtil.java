package com.example.lotto_project.config.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component //ㄷㅏ른 곳에서 주입해서 쓸 수 있도록 Spring Been으로 등록.
public class JwtUtil {

  //JWT 생성 및 검증에 사용될 SECRET_KEY, 외부 노출 금지
  //@Value로 application.yml 또는 docker-compose.yml에서 환경 변수 값을 가져옴.
  @Value("${JWT_SECRET_KEY}")
  private String secretKey;

  //Access Token의 만료 시간(24시간)
  //1일 = 24시간 * 60분 * 60초 * 1000밀리초
  private final long ACCESS_TOKEN_EXPIRATION_TIME = 24 * 60 * 60 * 1000L;

  //Refresh Token의 만료시간(7일)
  private final long REFRESH_TOKEN_EXPIRATION_TIME = 7 * 24 * 60 * 60 * 1000L;

  //JWT 서명에 사용할 키 객체
  private Key key;

  //서명 알고리즘으로 HS256 사용
  private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

  //이 클래스의 객체 생성 후, SecretKey 값을 Base64로 디코딩 하여 Key 객체 생성(한 번만 수행)
  @PostConstruct
  public void init() {

    byte[] bytes = Base64.getDecoder().decode(secretKey);
    key = Keys.hmacShaKeyFor(bytes);
  }

  /**
   * 이메일을 받아서 AccessToken을 생성
   * @param email
   * @return AccessToken
   */
  public String createAccessToken(String email) {
    return createToken(email, ACCESS_TOKEN_EXPIRATION_TIME);
  }

  /**
   * 이메일을 받아서 RefreshToken을 생성
   * @param email
   * @return RefreshToken
   */
  public String createRefreshToken(String email) {
    return createToken(email, REFRESH_TOKEN_EXPIRATION_TIME);
  }

  /**
   * JWT 토큰 생성
   * @param email
   * @param expirationTime
   * @return JWT 토큰
   */

  private String createToken(String email, long expirationTime) {

    //현재 시간
    Date now = new Date();

    //만료 시간
    Date expirationDate = new Date(now.getTime() + expirationTime);

    return Jwts.builder()
        .setSubject(email) //토큰의 주체로 이메일을 설정
        .setIssuedAt(now) //토큰 발급 시간
        .setExpiration(expirationDate) //토큰 만료 시간
        .signWith(key, signatureAlgorithm) //사용할 암호화 알고리즘과 비밀키
        .compact(); //토큰 생성 및 직렬화
  }

  /**
   * JWT 토큰을 검증
   * @param token
   * @return
   */
  public boolean validationToken(String token) {
    try {
      //Jwts.parseBuilder를 사용해 토큰을 파싱하고 서명을 검증.
      //서명이 유효하지 않거나 토큰이 만료되었을 경우 Exception 발생.
      Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public String getEmailFromToken(String token) {

    //토큰의 Payload 부분인 Claims를 가져옴.
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    //Claims에서 subject(이메일을 넣어둔곳)을 반환.
    return claims.getSubject();
  }

}
