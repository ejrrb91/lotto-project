package com.example.lotto_project.controller;

import com.example.lotto_project.service.LottoCrawlingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lotto")
@RequiredArgsConstructor
public class LottoController {

  private final LottoCrawlingService lottoCrawlingService;

  @PostMapping("/update")
  public ResponseEntity<String> updateLottoData() {

    String resultMessage = lottoCrawlingService.updateAllLottoRounds();
    return ResponseEntity.ok(resultMessage);
  }
}
