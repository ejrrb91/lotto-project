package com.example.lotto_project.config.jwt;

import com.example.lotto_project.service.UserDetailServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

  private final JwtUtil jwtUtil;
  private final UserDetailServiceImpl userDetailService;

  @Override
  public Message<?> preSend(Message<?> message, MessageChannel messageChannel) {
    StompHeaderAccessor stompHeaderAccessor = StompHeaderAccessor.wrap(message);

    //STOMP CONNECT 요청일 때만 JWT 토큰 검증
    if (StompCommand.CONNECT.equals(stompHeaderAccessor.getCommand())) {
      //Authorization 헤더에서 토큰을 가져옴
      String jwtToken = stompHeaderAccessor.getFirstNativeHeader("Authorization");

      log.info("STOMP Handler - 연결 시도, 토큰 : {}", jwtToken);

      if (StringUtils.hasText(jwtToken) && jwtToken.startsWith("Bearer ")) {
        String token = jwtToken.substring(7);
        //토큰 유효성 검증
        if (jwtUtil.validationToken(token)) {
          //토큰에서 사용자 이메일 추철
          String email = jwtUtil.getEmailFromToken(token);
          //DB에서 사용자 정보 조회
          UserDetails userDetails = userDetailService.loadUserByUsername(email);
          //인증 객체 생성
          Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());
          //SecurityContext에 인증 정보 저장
          SecurityContextHolder.getContext().setAuthentication(authentication);
          log.info("STOMP Handler - 사용자 인증 성공 : {}", email);
        } else {
          log.warn("STOMP Handler - 유효하지 않은 JWT 토큰");
        }
      } else {
        log.warn("STOMP Handler - Authorization 헤더가 없거나 'Bearer '로 시작하지 않음");
      }
    }
    return message;
  }
}
