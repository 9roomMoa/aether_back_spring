package com.groommoa.aether_back_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class AetherBackSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(AetherBackSpringApplication.class, args);
	}

}
