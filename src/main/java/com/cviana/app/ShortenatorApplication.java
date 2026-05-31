package com.cviana.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@OpenAPIDefinition(
	info = @Info(
		title = "Shortenator API",
		description = "Documentação da API do encurtador de URLs Shortenator"
	)
)
public class ShortenatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShortenatorApplication.class, args);
	}

}
