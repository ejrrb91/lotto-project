package com.example.lotto_project.controller;

import com.example.lotto_project.dto.MainPageResponseDto;
import com.example.lotto_project.service.LottoCrawlingService;
import com.sun.tools.javac.Main;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/main-page")
@RequiredArgsConstructor
public class MainPageController {

  private final LottoCrawlingService lottoCrawlingService;

  @GetMapping
  public ResponseEntity<MainPageResponseDto> getMainPageData() {
    MainPageResponseDto mainPageResponseDto = lottoCrawlingService.getLatestLottoRound();
    if (mainPageResponseDto == null || mainPageResponseDto.getRound() == null) {
      //DB에 데이터가 없을 경우를 대비해 404 Not Found를 응답함
      return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(mainPageResponseDto);
  }
}
