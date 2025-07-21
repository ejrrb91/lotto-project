package com.example.lotto_project.controller;

import com.example.lotto_project.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class ChatController {

  private final SimpMessageSendingOperations simpMessageSendingOperations;

  @MessageMapping("/chat.addUser/{roomId}")
  public void addUser(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
    log.info("사용자 입장 : 방 ID = {}, 닉네임 = {}", roomId, chatMessage.getSender());
    chatMessage.setMessage(chatMessage.getSender() + "님이 입장하셨습니다.");
    simpMessageSendingOperations.convertAndSend("/topic/chat/room/" + roomId, chatMessage);
  }

  @MessageMapping("/chat.sendMessage/{roomId}")
  public void sendMessage(@DestinationVariable String roomId, @Payload ChatMessage chatMessage) {
    log.info("메시지 수신 : 방 ID = {}, 보낸사람 = {}, 내용 = {}", roomId, chatMessage.getSender(),
        chatMessage.getMessage());
    simpMessageSendingOperations.convertAndSend("/topic/chat/room/" + roomId, chatMessage);
  }

  @MessageMapping("/chat.leaveUser/{roomId}")
  public void sendLeaveMessage(@DestinationVariable String roomId,
      @Payload ChatMessage chatMessage) {
    log.info("사용자 퇴장 : 방 ID = {}, 닉네임 = {}", roomId, chatMessage.getSender());
    chatMessage.setMessage(chatMessage.getSender() + "님이 퇴장하셨습니다.");
    simpMessageSendingOperations.convertAndSend("/topic/chat/room/" + roomId, chatMessage);

  }
}
