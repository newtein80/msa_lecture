package com.mobigen.lecture.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GatewayserverApplication {

	// https://docs.spring.io/spring-cloud-gateway/reference/spring-cloud-gateway.html

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

}
