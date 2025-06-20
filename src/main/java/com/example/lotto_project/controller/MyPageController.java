package com.example.lotto_project.controller;

import com.example.lotto_project.dto.MyPageResponseDto;
import com.example.lotto_project.dto.RecommendationResponseDto;
import com.example.lotto_project.security.UserDetailsImpl;
import com.example.lotto_project.service.RecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/my-page")
@RequiredArgsConstructor
public class MyPageController {

  private final RecommendationService recommendationService;

  @GetMapping("/recommendations")
  public ResponseEntity<MyPageResponseDto> getMyRecommendations(
      @AuthenticationPrincipal UserDetailsImpl userDetails) {

    //@AuthenticationPrincipal을 사용해 로그인한 사용자의 Id를 가져옴
    Long userId = userDetails.getUser().getId();

    //서비스 로직을 호출하여 추천 기록 목록을 가져옴.
    MyPageResponseDto myPageResponseDto = recommendationService.getMyRecommendations(userId);

    //성공 시 응답과 함께 추천 기록 목록을 반환
    return ResponseEntity.ok(myPageResponseDto);
  }
}
