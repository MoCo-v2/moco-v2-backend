package com.moco.moco;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import jakarta.annotation.PostConstruct;

@EnableJpaAuditing //Base_Entity에 있는 날짜 자동 입력 활성화
@EnableJpaRepositories(basePackages = {"com.moco.moco.jpaRepository"})
@SpringBootApplication
public class MocoApplication {

	public static void main(String[] args) {
		SpringApplication.run(MocoApplication.class, args);
	}

	@PostConstruct
	public void setTimeZone() {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}
}
