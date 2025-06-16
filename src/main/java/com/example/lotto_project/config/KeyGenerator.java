package com.example.lotto_project.config;

import java.security.SecureRandom;
import java.util.Base64;

public class KeyGenerator {

  public static void main(String[] args) {

    SecureRandom secureRandom = new SecureRandom();

    byte[] keyBytes = new byte[32]; //32바이트 = 256비트
    secureRandom.nextBytes(keyBytes);
    String secretKey = Base64.getEncoder().encodeToString(keyBytes);
    System.out.println("생성된 Secret_Key : " + secretKey);
  }
}
