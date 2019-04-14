package com.fis.is.terminy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TerminyApplication {

	public static void main(String[] args) {
		SpringApplication.run(TerminyApplication.class, args);
	}

}
