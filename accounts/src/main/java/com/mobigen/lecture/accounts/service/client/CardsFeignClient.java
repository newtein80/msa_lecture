package com.mobigen.lecture.accounts.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import com.mobigen.lecture.accounts.dto.CardsDto;

@FeignClient(name = "cards", path = "/api")
public interface CardsFeignClient {

    @GetMapping(value = "/fetch", consumes = "application/json")
    public ResponseEntity<CardsDto> fetchCardDetails(
            @RequestHeader("msa-correlation-id") String correlationId,
            @RequestParam(value = "mobileNumber") String mobileNumber);

    @GetMapping(value = "/health", consumes = "application/json")
    public ResponseEntity<String> getHealthInfo();

}
