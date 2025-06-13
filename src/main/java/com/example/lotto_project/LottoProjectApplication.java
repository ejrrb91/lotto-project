package com.example.lotto_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LottoProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(LottoProjectApplication.class, args);
	}

}
