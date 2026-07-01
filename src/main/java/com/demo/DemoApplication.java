package com.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class DemoApplication {


	 @Value("${DATABASE_URL:NOT_FOUND}")
    private String databaseUrl;

    @PostConstruct
    public void test() {
        System.out.println("DATABASE_URL = " + databaseUrl);
    }

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}
