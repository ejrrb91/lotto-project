package com.example.lotto_project.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 @RestController에서 발생하는 예외를 여기서 처리함.
public class GlobalExceptionHandler {

  //IllegalArgumentException 타입의 예외가 발생하면 이 메서드 실행
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    //클라이언트에게는 409 Conflict 상태 코드와 예외 메시지를 전달.
    return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
  }
}
