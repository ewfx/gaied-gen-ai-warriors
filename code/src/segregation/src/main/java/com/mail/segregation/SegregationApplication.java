package com.mail.segregation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.mail.segregation")
public class SegregationApplication {

	public static void main(String[] args) {
		SpringApplication.run(SegregationApplication.class, args);
	}

}
