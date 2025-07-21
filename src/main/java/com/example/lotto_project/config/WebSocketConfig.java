package com.example.lotto_project.config;

import com.example.lotto_project.config.jwt.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker //STOMP 메시지 브로커
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  private final StompHandler stompHandler;

  //메시지 주고받을 경로 설정
  @Override
  public void configureMessageBroker(MessageBrokerRegistry messageBrokerRegistry) {
    // "/topic"으로 시작하는 경로를 구독하는 클라이언트에게 메시지를 전달.
    messageBrokerRegistry.enableSimpleBroker("/topic");
    // "/app"으로 시작하는 경로로 클라이언트가 메시지를 보내면, @MessageMapping이 붙은 메서드가 처리
    messageBrokerRegistry.setApplicationDestinationPrefixes("/app");
  }

  //클라이언트가 처음 WebSocket에 접속할 때 사용할 주소(엔드포인트)를 설정.
  @Override
  public void registerStompEndpoints(StompEndpointRegistry stompEndpointRegistry) {
    //"/ws"라는 주소로 클라이언트가 접속 시도.
    //setAllowedOriginPatterns("*")는 모든 주소에서의 접속을 허용(CORS)
    //withSockJS()는 WebSocket을 지원하지 않는 브라우저를 위한 대체 옵션
    stompEndpointRegistry.addEndpoint("/ws")
        .setAllowedOriginPatterns("*")
        .withSockJS();
  }

  @Override
  public void configureClientInboundChannel(ChannelRegistration channelRegistration) {
    channelRegistration.interceptors(stompHandler);
  }
}