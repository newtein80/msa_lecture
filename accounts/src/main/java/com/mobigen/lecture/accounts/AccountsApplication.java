package com.mobigen.lecture.accounts;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import com.mobigen.lecture.accounts.dto.AccountsContactInfoDto;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableFeignClients
@EnableDiscoveryClient
@EnableJpaAuditing(auditorAwareRef = "auditAwareImpl")
@SpringBootApplication
@EnableConfigurationProperties(value = {AccountsContactInfoDto.class})
@OpenAPIDefinition(
	info = @Info(
		title = "Accounts microservice REST API Documentation",
		description = "MSA - Accounts microservice REST API Documentation",
		version = "v1",
		contact = @Contact(
			name = "group1-team2",
			email = "group1-team2@email.com",
			url = "https://www.group1-team2.com"
		),
		license = @License(
			name = "Apache 2.0",
			url = "https://www.group1-team2.com"
		)
	),
	externalDocs = @ExternalDocumentation(
		description =  "MSA - Accounts microservice REST API Documentation",
		url = "https://ip:port/swagger-ui.html"
	)
)
public class AccountsApplication {

	@Value("${build.version}")
	private String buildVersion;

	public static void main(String[] args) {
		SpringApplication.run(AccountsApplication.class, args);
	}

	@Bean
	public CommandLineRunner runner() {
		return (a) -> {
			log.info("=================================================");
			log.info("Build Version: " + buildVersion);
			log.info("=================================================");
		};
	}

}
