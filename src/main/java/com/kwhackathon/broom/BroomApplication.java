package com.kwhackathon.broom;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@PropertySource("classpath:/local.properties")
public class BroomApplication {

	public static void main(String[] args) {
		SpringApplication.run(BroomApplication.class, args);
	}

}
