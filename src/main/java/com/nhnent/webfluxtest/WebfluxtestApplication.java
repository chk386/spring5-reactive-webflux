package com.nhnent.webfluxtest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;

@SpringBootApplication
@EnableReactiveMongoRepositories
public class WebfluxtestApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebfluxtestApplication.class, args);
	}
}
